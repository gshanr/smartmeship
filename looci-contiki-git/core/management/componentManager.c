/**
LooCI Copyright (C) 2013 KU Leuven.
All rights reserved.

LooCI is an open-source software development kit for developing and maintaining networked embedded applications;
it is distributed under a dual-use software license model:

1. Non-commercial use:
Non-Profits, Academic Institutions, and Private Individuals can redistribute and/or modify LooCI code under the terms of the GNU General Public License version 3, as published by the Free Software Foundation
(http://www.gnu.org/licenses/gpl.html).

2. Commercial use:
In order to apply LooCI in commercial code, a dedicated software license must be negotiated with KU Leuven Research & Development.

Contact information:
  Administrative Contact: Sam Michiels, sam.michiels@cs.kuleuven.be
  Technical Contact:           Danny Hughes, danny.hughes@cs.kuleuven.be
Address:
  iMinds-DistriNet, KU Leuven
  Celestijnenlaan 200A - PB 2402,
  B-3001 Leuven,
  BELGIUM. 
 */
/*
 * @File
 * implementation of the component manager
 */


#include "componentManager.h"
#include "component.h"
#include "events_private.h"
#include "contiki.h"
#include "vector.h"
#include "error_codes.h"
#include "utils.h"
#include "interceptor.h"
#include <stdint.h>
#include <string.h>
#include <stdlib.h>

#ifdef LOOCI_MGT_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif

VECTOR(v_components,struct looci_comp,2);

void printComponents(){
	void printVectorMap(struct looci_comp* el, void* data){
		PRINTF("%u-%p,",el->id,el);
	}
	PRINTF("[CpM] Current comps: ");
	vector_map(&v_components,(vector_map_ft)printVectorMap,NULL);
	PRINT_LN("--");
}

struct looci_comp* looci_cmpMan_component_get(uint8_t cid){
	bool filter_cmp_id(struct looci_comp* el,uint8_t* data){
		return (el->id == data[0]);
	}
	return (struct looci_comp*) vector_filter(&v_components,(bool (*)(void*,void*))filter_cmp_id,&cid);
}

int8_t looci_cmpMan_component_start(lc_rc_component_t* data){
	struct looci_comp* comp = looci_cmpMan_component_get(data->cid);
	if(comp == NULL){
		return ERROR_CMP_NOT_FOUND;
	}
	lifecycle_intercept_t int_data = {INTERCEPT_LIFECYCLE_ACTIVATE,NULL,comp};
	if(!lc_intercept(INTERCEPT_LIFECYCLE_CHANGE,&int_data)){return 0;}
	if(comp != NULL && comp->state == COMPONENT_STATE_DEACTIVATED){
		process_start(&comp->process, NULL);
		comp->state = COMPONENT_STATE_ACTIVE;
		return MSG_SUCCESS;
	} else{
		return ERROR_ILLEGAL_STATE;
	}
}


int8_t looci_cmpMan_component_stop(lc_rc_component_t* data){
	uint8_t cid = data->cid;
	struct looci_comp* component = looci_cmpMan_component_get(cid);
	if(component == NULL){
		return ERROR_CMP_NOT_FOUND;
	}
	if(cid == 1){
		return ERROR_ILLEGAL_ARG;
	}
	lifecycle_intercept_t int_data = {INTERCEPT_LIFECYCLE_DEACTIVATE,NULL,component};
	if(!lc_intercept(INTERCEPT_LIFECYCLE_CHANGE,&int_data)){return 0;}
	if(component->state == COMPONENT_STATE_ACTIVE){
		component->state = COMPONENT_STATE_DEACTIVATED;
		process_exit(&component->process);
	} else{
		return ERROR_ILLEGAL_STATE;
	}
	return MSG_SUCCESS;
}

static uint8_t looci_im_getMgtId(){
	uint8_t i = 0;
	for(i = 1 ; i < 10 ; i ++){
		if(looci_cmpMan_component_get(i) == NULL){
			return i;
		}
	}
	return 0;
}

static uint8_t looci_im_getCmpId(){
	uint8_t i = 0;
	for(i = 10 ; i < 10+MAX_NR_COMPONENTS ; i ++){
		if(looci_cmpMan_component_get(i) == NULL){
			return i;
		}
	}
	return 0;
}


void looci_cmpMan_init(){
	vector_init(&v_components);
}

void looci_cmpMan_sendEvent(uint8_t cmpId, core_looci_event_t* event){
	struct looci_comp* comp = looci_cmpMan_component_get(cmpId);
	if(comp != NULL){
		process_post_synch(&comp->process, PROCESS_LC_RECEIVE_EVENT,event);
	}
}

static char looci_cmpMan_callProcess(struct process* callTarget, process_event_t ev,uint16_t lc_val,void* data){
	struct process* old = PROCESS_CURRENT();
	PROCESS_CURRENT() = callTarget;
	uint16_t old_lc = callTarget->pt.lc;
	callTarget->pt.lc = lc_val; //the LC val is a hack for standard, old components
	char ret = PROCESS_CURRENT()->thread(&PROCESS_CURRENT()->pt,ev,data);
	PROCESS_CURRENT()=old;
	callTarget->pt.lc = old_lc;
	return ret;
}

int8_t looci_cmpMan_instantiateComponent(struct looci_codebase* codebase){
	PRINTF("[CpM] instantiating cmp %u",codebase->id);
	struct looci_comp* component = (struct looci_comp*) vector_createElement(&v_components);
	if(component == NULL){return ERROR_NO_MEMORY;}

	lifecycle_intercept_t data = {INTERCEPT_LIFECYCLE_INSTANTIATE,NULL,component};
	if(!lc_intercept(INTERCEPT_LIFECYCLE_CHANGE,&data)){return 0;}

	component->codebase = codebase;
	component->state = COMPONENT_STATE_DEACTIVATED;

	component->data = malloc(codebase->sizeOfState);
	if(component->data == NULL){
		PRINTF("[CpM] could not allocate memory");
		vector_remove(&v_components,v_components.len-1);
		return ERROR_NO_MEMORY;
	}

	component->process.thread = codebase->thread;
	component->process.pt.lc = 0;

	uint8_t id = 0;
	if(codebase->flags&LC_MASK_MGT){
		id = looci_im_getMgtId();
	} else{
		id = looci_im_getCmpId();
	}

	if(id != 0){
		component->id = id;
	}
	PRINTF("[CpM]Creating instance of cmp %u with id %u  ",codebase->id,id);

	if(codebase->initState != NULL){
		uint_farptr_t tgt = getFP(codebase->initState,codebase->src_cmp);
		memcpy_F((char*)component->data,tgt,codebase->sizeOfState);
	}

	//call init
	looci_cmpMan_callProcess(&component->process,PROCESS_LC_INIT,COMPONENT_LC_INIT,NULL);

	if(codebase->flags&LC_MASK_AUTO_START){
		lc_rc_component_t compData;
		compData.cid = id;
		looci_cmpMan_component_start(&compData);
	}
	PRINTF("[CpM]Component created");
	return component->id;
}



int8_t looci_cmpMan_destroyComponent(lc_rc_component_t* data){
	uint8_t cid = data->cid;

	PRINT_LN("[CpM]request destroy %u",cid);
	printComponents();

	struct looci_comp* component = looci_cmpMan_component_get(cid);
	if(component == NULL){
		return ERROR_CMP_NOT_FOUND;
	}
	if(component->codebase->flags & LC_MASK_NO_DESTROY){
		return ERROR_FAILURE;
	}
	lifecycle_intercept_t int_data = {INTERCEPT_LIFECYCLE_DESTROY,NULL,component};
	if(!lc_intercept(INTERCEPT_LIFECYCLE_CHANGE,&int_data)){return 0;}

	PRINT_LN("[CpM]call stop");


	lc_rc_component_t compData = {cid};
	looci_cmpMan_component_stop(&compData);

	PRINT_LN("[CpM]call destroy");
	looci_cmpMan_callProcess(&component->process,PROCESS_LC_DESTROY,COMPONENT_LC_DESTROY,NULL);
	events_unwire_component(&compData);
	free(component->data);

	vector_remove_el(&v_components,component,1);

	return 1;
}

int8_t looci_cmpMan_destroyAllComponents(struct looci_codebase* cb){
	if(cb->flags & LC_MASK_NO_DESTROY){
		return 0;
	}
	uint8_t comps[10];
	lc_is_get_cid_by_cbid_t data ={cb->id,comps,0,10};
	looci_cmpMan_getcompids_by_cb_id(&data);
	uint8_t i = 0;
	for(i = 0; i < data.size; i ++){
		PRINT_LN("[Cmp] DestA %u",comps[i]);
		lc_rc_component_t data2 = {comps[i]};
		looci_cmpMan_destroyComponent(&data2);
	}
	return 1;
}


/**
 * @internal
 * Get a component by its process
 *
 * @param process The process in which the component runs.
 */
struct looci_comp * looci_cmpMan_component_get_by_process(struct process * process){
	bool filter_process(void* el,void* data){
		if( &((struct looci_comp*)el)->process == data){
			return true;
		} else{
			return false;
		}
	}
	return vector_filter(&v_components,filter_process,process);
}

uint8_t looci_cmpMan_get_cmpId_by_process(struct process* process){
	struct looci_comp* cmp= looci_cmpMan_component_get_by_process(process);
	if(cmp != NULL){
		return cmp->id;
	} else{
		return 0;
	}
}


int8_t looci_cmpMan_getComponents(struct vector** components){
	*components = &v_components;
	return 1;
}

static looci_property_t getPropertyOfComponent(struct looci_comp* comp, looci_prop_t propId ) {
	struct looci_codebase* cb = comp->codebase;
	uint8_t nrProps = cb->nrProperties;
	looci_property_t props[nrProps];
	uint_farptr_t tgt = getFP(cb->properties,cb->src_cmp);
	memcpy_F((char*)props,tgt,sizeof(looci_property_t) * nrProps);
	int i = 0;
	for(i = 0; i < nrProps ; i ++){
		if(props[i].propertyId == propId){
			return props[i];
		}
	}
	return (looci_property_t){0,0,0,0,NULL};
}


int8_t looci_cmpMan_getProperty(lc_is_get_property_t* data){
	struct looci_comp* comp = looci_cmpMan_component_get(data->cid);
	if(comp == NULL){
		return ERROR_CMP_NOT_FOUND;
	}else{
		looci_property_t prop = getPropertyOfComponent(comp,data->buffer->propertyId);
		if(prop.propertyId != 0){
			char ret = looci_cmpMan_callProcess(&comp->process,PROCESS_LC_GET_PROPERTY,COMPONENT_LC_GET_PROPERTY,data->buffer);
			if(ret == PROP_IS_SET){ //PROPERTY GET DONE AND HANDLED
				return MSG_SUCCESS;
			} else if(ret == PROP_ILLEGAL_ARG){
				return ERROR_ILLEGAL_ARG;
			}	else{
				PRINT_LN("[CpM] get property %u of %u at %p, offset %u, size %u",data->propertyId,data->cid,comp->data,prop.offset,prop.size);
				memcpy(data->buffer->buffer,(comp->data+prop.offset),prop.size);
				PRINT_BYTE_ARRAY(comp->data+prop.offset,prop.size);
				PRINT_BYTE_ARRAY(data->buffer,prop.size);
				data->buffer->elements = prop.size;
				return MSG_SUCCESS;
			}
		} else{
			return ERROR_PARAMETER_NOT_FOUND;
		}

	}
}


int8_t looci_cmpMan_setProperty(lc_rc_set_property_t* data){
	struct looci_comp* comp = looci_cmpMan_component_get(data->cid);
	if(comp == NULL){
		return ERROR_CMP_NOT_FOUND;
	} else{
		looci_property_t prop = getPropertyOfComponent(comp,data->buffer->propertyId);
		if(prop.propertyId == 0){
			return ERROR_PARAMETER_NOT_FOUND;
		}
		char ret = looci_cmpMan_callProcess(&comp->process,PROCESS_LC_SET_PROPERTY,COMPONENT_LC_SET_PROPERTY,data->buffer);
		if(ret == PROP_IS_SET){ //PROPERTY DONE AND HANDLED
			looci_cmpMan_callProcess(&comp->process,PROCESS_LC_PROPERTY_IS_SET,COMPONENT_LC_PROPERTY_IS_SET,data->buffer);
			return MSG_SUCCESS;
		} else if(ret == PROP_ILLEGAL_ARG){ //PROPERTY FAILURE
			return ERROR_ILLEGAL_ARG;
		} else{ //PROPERTY APPROVED, NOT SET, or no prop set method present
			memcpy(comp->data+prop.offset,data->buffer->buffer,prop.size);
			data->buffer->size = prop.size;
			looci_cmpMan_callProcess(&comp->process,PROCESS_LC_PROPERTY_IS_SET,COMPONENT_LC_PROPERTY_IS_SET,data->buffer);
			return MSG_SUCCESS;
		}
	}
}

int8_t looci_cmpMan_getProperties(lc_is_get_properties_t* data){
	struct looci_comp* comp = looci_cmpMan_component_get(data->cid);
	if(comp == NULL){
		PRINT_LN("[CpM] request prop of null comp %u",data->cid);
		return ERROR_CMP_NOT_FOUND;
	} else{
		struct looci_codebase* cb = comp->codebase;
		uint8_t nrProps = cb->nrProperties;
		looci_property_t props[nrProps];
		uint_farptr_t tgt = getFP(cb->properties,cb->src_cmp);
		memcpy_F((char*)props,tgt,sizeof(looci_property_t) * nrProps);


		PRINT_LN("[CpM] request prop of comp %u, len %u",data->cid,nrProps);

		uint8_t i;
		for(i = 0 ; i < nrProps; i ++){
			PRINTF("%u,",props[i].propertyId);
			data->buffer[i] = props[i].propertyId;
			PRINT_LN("");
		}
		data->elements = nrProps;

		return MSG_SUCCESS;
	}
}

int8_t looci_cmpMan_getPropertyInfo(lc_is_get_property_info_t* data){
	struct looci_comp* cmp = looci_cmpMan_component_get(data->cid);
	if(cmp == NULL){
		return ERROR_CMP_NOT_FOUND;
	}else{
		looci_property_t prop = getPropertyOfComponent(cmp,data->propertyId);
		if(prop.propertyId != 0){
			if(prop.name != NULL){
				data->elements = strcpy_F((char*)data->buffer,getFP(prop.name,cmp->codebase->src_cmp),data->size);
			} else{
				data->buffer[0] = 0;
				data->elements = 1;
			}
			data->propertyType = prop.dataType;
			return MSG_SUCCESS;
		} else{
			return ERROR_PARAMETER_NOT_FOUND;
		}
	}
}

static int8_t looci_cmpMan_cp_events(lc_is_eventtype_buffer_t* data,uint_farptr_t tgt){
	uint8_t len = shortLen_F(tgt,EV_END_EVENT);
	uint8_t retVal = 1;
	if(len > data->size){
		len = data->size;
		retVal = 0;
	}
	memcpy_F((char*)data->buffer,tgt,len*2);
	data->elements = len;
	return retVal;
}

int8_t looci_cmpMan_getInterfaces(lc_is_eventtype_buffer_t* data) {
	struct looci_comp* cmp = looci_cmpMan_component_get(data->cid);
	if(cmp == NULL){
		return ERROR_CMP_NOT_FOUND;
	} else{
		uint_farptr_t tgt = getFP(cmp->codebase->interfaces,cmp->codebase->src_cmp);
		return looci_cmpMan_cp_events(data,tgt);
	}

}

int8_t looci_cmpMan_getReceptacles(lc_is_eventtype_buffer_t* data) {
	struct looci_comp* cmp = looci_cmpMan_component_get(data->cid);
	if(cmp == NULL){
		return ERROR_CMP_NOT_FOUND;
	} else{
		uint_farptr_t tgt = getFP(cmp->codebase->receptacles,cmp->codebase->src_cmp);
		return looci_cmpMan_cp_events(data,tgt);
	}
}


struct cmp_id_buffer{
	uint8_t* buffer;
	uint8_t index;
	uint8_t size;
	uint8_t	filter;
};

//support filter for filtering component IDs in vector
static bool filter_cmpIds_by_cb_id(struct looci_comp* el,lc_is_get_cid_by_cbid_t* data){
	if(data->size == data->maxSize){
		return true;
	}
	uint8_t id = el->codebase->id;
	if(id == data->cbid){
		data->buffer[data->size] = el->id;
		data->size++;
	}
	return false;
}
//54
int8_t looci_cmpMan_getcompids_by_cb_id(lc_is_get_cid_by_cbid_t* data) {
	if(looci_cbMan_get_codebase(data->cbid)==0){
		return ERROR_CB_NOT_FOUND;
	}
	return (vector_filter(&v_components,(vector_filter_ft)filter_cmpIds_by_cb_id,(void*)data) == NULL);
}

//55
int8_t looci_cmpMan_get_cb_id_of_comp_id(lc_is_get_cbid_of_cid_t* data){
	struct looci_comp* c = looci_cmpMan_component_get(data->cid);
	if(c!=NULL){
		data->cbid = c->codebase->id;
		return true;
	} else{
		return ERROR_CMP_NOT_FOUND;
	}
}

//56
int8_t looci_cmpMan_get_comp_type(lc_is_get_c_type_t* data) {
  struct looci_comp * c = looci_cmpMan_component_get(data->cid);
  if(c!=NULL) {
	  uint_farptr_t tgt = getFP(c->codebase->name,c->codebase->src_cmp);
	  data->size = strcpy_F(data->type,tgt,data->maxsize);
	  return true;
  } else{
	  return ERROR_CMP_NOT_FOUND;
  }
}

static bool filter_get_cmp_ids(struct looci_comp* el,lc_is_get_c_ids_t* data){
	if(data->elements == data->maxSize){
		return true;
	}
	data->buffer[data->elements] = el->id;
	data->elements++;
	return false;
}
//57
int8_t looci_cmpMan_get_comp_ids(lc_is_get_c_ids_t* data) {
	return vector_filter(&v_components,(vector_filter_ft)filter_get_cmp_ids,data) == NULL;
}

int8_t looci_cmpMan_getstate(lc_is_get_state_t* data) {
  struct looci_comp * c = looci_cmpMan_component_get(data->cid);
  if(c!=NULL) {
	  data->state = c->state;
	  return 1;
  } else {
	  return ERROR_CMP_NOT_FOUND;
  }
}


int8_t looci_cmpMan_hasReceptacle(uint8_t cid, uint16_t eventId){
	struct looci_comp* cmp = looci_cmpMan_component_get(cid);
	if(cmp == NULL){
		return ERROR_CMP_NOT_FOUND;
	} else{
		uint_farptr_t tgt = getFP(cmp->codebase->receptacles,cmp->codebase->src_cmp);
		if(shortcmp_F(tgt,EV_ANY_EVENT,EV_END_EVENT)){
			return MSG_SUCCESS;
		} else{
			return shortcmp_F(tgt,eventId,EV_END_EVENT);
		}
	}
}

int8_t looci_cmpMan_hasInterface(uint8_t cid, uint16_t eventId){
	struct looci_comp* cmp = looci_cmpMan_component_get(cid);
	if(cmp == NULL){
		return ERROR_CMP_NOT_FOUND;
	} else{
		uint_farptr_t tgt = getFP(cmp->codebase->interfaces,cmp->codebase->src_cmp);
		if(shortcmp_F(tgt,EV_ANY_EVENT,EV_END_EVENT)){
			return MSG_SUCCESS;
		} else{
			return shortcmp_F(tgt,eventId,EV_END_EVENT);
		}
	}
}


void lcLL(void** ptr){
	struct looci_comp* current = looci_cmpMan_component_get_by_process(PROCESS_CURRENT());
	*ptr = current->data;
}

char _call(uint16_t ev, void* data1, void* data2, const struct eventList* list, void* cmp){
	uint_farptr_t thisPtr = getFP((void*)list, cmp);
	struct eventList cmd = {0,0};
	do{
		memcpy_F((char*)&cmd,thisPtr,sizeof(struct eventList));
		if(cmd.event == ev){
			PRINT_LN("calling %p with data %p,%p",cmd.f,data1,data2);
			return cmd.f(data1,data2);
		} else{
			thisPtr += sizeof(struct eventList);
		}
	}while(cmd.event!=0);
	if(cmd.f != NULL){
		struct contiki_call data_ev = {ev,data1};
		return cmd.f(&data_ev,data2);
	} else{
		return 0;
	}
}

char _lcc(uint16_t ev, void* data, const struct eventList* list, struct component* cmp){
	void* state;
	lcLL(&state);
	PRINT_LN("[LCC]received event %u",ev);
	return _call(ev,state,data,list,cmp);
}

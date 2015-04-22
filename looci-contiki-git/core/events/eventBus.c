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
/**
 * @addtogroup events
 * @{
*/
/**
 * @file
 * Implementation of the LooCI Event Bus
 * @author
 * Wouter Horré <wouter.horre@cs.kuleuven.be>
 */
#include "eventBus.h"
#include "events_private.h"
#include "interceptor.h"
#include "lib/vector.h"
#include "memb.h"
#include "networking_private.h"
#include "component.h"
#include "componentManager.h"
#include "process.h"
#include "comp_services.h"
#include "error_codes.h"

//#ifdef CONTIKI_TARGET_MINIMAL_NET
#include <string.h>
#include <stdint.h>
#include <stdbool.h>
//#endif

#ifdef LOOCI_EVENTS_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif

#define MIN(a, b) ((a) < (b)? (a): (b))

#define LOOCI_EVENTS_QUEUE_SIZE 5

/**
 * @internal
 * @name Internal contiki definitions
 * @{
 */
//! @internal
//! @todo how many events do we want to queue?
MEMB(events, looci_event_t, LOOCI_EVENTS_QUEUE_SIZE);
PROCESS(looci_event_manager, "LooCI Event Manager");
/** @} */

// Function prototypes
static looci_event_t* currentEvent;
static void dispatch_event(looci_event_t*);
static void dispatch_remote_event(looci_event_t*);
static void clearLoociEvent(struct looci_event* ev);

// A list of subscriptions
VECTOR(subscriptions, subs_local,2);
VECTOR(subscriptions_to_remote, subs_rem_to,2);
VECTOR(subscriptions_from_remote, subs_rem_from,2);


PROCESS_THREAD(looci_event_manager, ev, data)
{
  PROCESS_BEGIN(){}

  currentEvent = NULL;

  memb_init(&events);
  vector_init(&subscriptions);
  vector_init(&subscriptions_to_remote);
  vector_init(&subscriptions_from_remote);


  PRINTF("[LooCI EM] Started\r\n");

  while(1) {
    PROCESS_WAIT_EVENT_UNTIL(ev==PROCESS_LC_RECEIVE_EVENT || ev==PROCESS_LC_RM);
    currentEvent = (looci_event_t*)data;
    PRINTF("[LooCI EM] Processing event\r\n");
    PRINT_EVENT(currentEvent);
    if(ev==PROCESS_LC_RECEIVE_EVENT) {
      dispatch_event(currentEvent);
    } else if(ev==PROCESS_LC_RM) {
      dispatch_remote_event(currentEvent);
    }
    clearLoociEvent(currentEvent);
    currentEvent = NULL;
  }

  PROCESS_END(){}
}



looci_event_t* lc_em_getCurrentEvent(){
	return currentEvent;
}

static void postEventToInstance(struct looci_event* event){
	if(lc_intercept(INTERCEPT_TO_COMPONENT,event)){
		char buffer[event->len];
		core_looci_event_t tempEvent = {.type = event->type, .len = event->len, .payload = buffer};
		memcpy(buffer,event->payloadMemory.ptr,event->len);
		looci_cmpMan_sendEvent(event->dst_cid,&tempEvent);
	}
}

static void postEventToNetwork(struct looci_event* event){
	PRINTF("[EM] event to network to %u\r\n",event->dst_node);
	if(lc_intercept(INTERCEPT_TO_NETWORK,event)){
		looci_nf_send_event(event);
	}
}

static struct looci_event* createLoociEvent(looci_eventtype_t type, void* payload, uint16_t len){
	struct looci_event* e = (struct looci_event *) memb_alloc(&events);
	  if(e==NULL) {
	    // event queue is full, too bad ...
	    return NULL;
	  }
	  if(!mmem_alloc(&e->payloadMemory,len)){
		  memb_free(&events,e);
		  return NULL;
	  }
	  e->type = type;
	  e->source_node = PEER_ID_SELF;
	  e->dst_node = PEER_ID_NONE;
	  e->source_cid = ((struct looci_comp *) looci_cmpMan_component_get_by_process(PROCESS_CURRENT()))->id;
	  e->dst_cid = COMPONENT_ID_ANY;
	  e->len = len;
	  memset(e->header,0,LOOCI_EVENT_MAX_HEADER_LEN);
	  memcpy(e->payloadMemory.ptr, payload, e->len);
	  return e;
}

static void clearLoociEvent(struct looci_event* ev){
    mmem_free(&ev->payloadMemory);
    memb_free(&events, ev);
}

uint8_t _p(looci_eventtype_t type, void * payload, uint8_t len)
{
	struct looci_event* e = createLoociEvent(type,payload,len);
	if(e==NULL) {
		// event queue is full, too bad ...
	    PRINTF("[EM] Drop lcl ev: queue full\r\n");
		return LOOCI_ERR_EVENTNOTPUBLISHED;
	}
	if(lc_intercept(INTERCEPT_FROM_COMPONENT,e)){
		PRINT_LN("[EM] lcl ev in queue");
		//PRINT_EVENT(e);
		process_post(&looci_event_manager, PROCESS_LC_RECEIVE_EVENT, e);
	} else{
		clearLoociEvent(e);
	}
	return LOOCI_SUCCESS;
}

uint8_t lcpEvPub(looci_eventtype_t type, void * payload, uint16_t len)
{
	return _p(type,payload,len);
}

/*
 * Receive event from network stack.
 * First check with interceptors.
 * If ok and enough memory, copy event into event bus event list, if enough memory
 *
 */
void looci_event_handle_remote_event(looci_event_t* event) {

	if(lc_intercept(INTERCEPT_FROM_NETWORK,event)){
		looci_event_t* e = (looci_event_t*) memb_alloc(&events);
		if(e == NULL) {
			PRINTF("[EM] Drop rem ev queue full\r\n");
			return;
		}
		memcpy(e, event, sizeof(looci_event_t));
		if(mmem_alloc(&e->payloadMemory,e->len)){
			memcpy(e->payloadMemory.ptr, event->payloadMemory.ptr, event->len);
		 	PRINT_LN("[EM] rem ev in queue");
		 	//PRINT_EVENT(e);
		 	process_post(&looci_event_manager, PROCESS_LC_RM, e);
		} else{
			PRINTF("[EM] not enough memory for event\r\n");
			memb_free(&events,e);
		}
	}
}


static void dispatch_local(subs_local* sub, struct looci_event* e){
    if(evpTM(e->type, sub->type) &&  //type must match
  		 evpCM(e->source_cid,sub->src_cmp) && // source comp must match
  		 e->source_cid != sub->dst_cmp // source must not be destination (only for local subscriptions)
			){
  	  PRINT_LN("[EM] publishing to instance %u",sub->dst_cmp);
  	  e->dst_cid = sub->dst_cmp;
  	  postEventToInstance(e);
    }
}

static void dispatch_remote_to(subs_rem_to* sub_to, struct looci_event* e ){
    if(evpTM(e->type, sub_to->type) &&
  		  evpCM(e->source_cid,sub_to->src_cmp)) {
  	  PRINT_LN("[EM] publishing to node %u",sub_to->dst_nod);
  	  e->dst_node = sub_to->dst_nod;
  	  postEventToNetwork(e);
  	  e->dst_node = PEER_ID_NONE;
    }
}

static void dispatch_remote_from(subs_rem_from* sub, struct looci_event* e ){
	if(evpTM(e->type, sub->type) \
		&& evpNM(e->source_node,sub->src_nod) \
		&& evpCM(e->source_cid,sub->src_cmp)) {
		PRINT_LN("[EM] posting event to %u", sub->dst_cmp);
		e->dst_cid = sub->dst_cmp;
		postEventToInstance(e);
		e->dst_node = PEER_ID_NONE;
	}
}


/**
 * @internal
 * Dispatch a local event.
 * @param em The event to dispatch, including meta data
 */
static void dispatch_event(looci_event_t* e) {
	//check targetted
	if(e->dst_node != PEER_ID_NONE){
		if(e->dst_node == PEER_ID_SELF){
			//local trigger
			postEventToInstance(e);
		} else {
			postEventToNetwork(e);
		}
	} else{
		PRINTF("[EM] Dispatching event type: %u from %u\r\n",e->type,e->source_cid);
		vector_map(&subscriptions,(vector_map_ft) dispatch_local,e);
		vector_map(&subscriptions_to_remote,(vector_map_ft) dispatch_remote_to,e);
	}

}

/**
 * @internal
 * Dispatch an event from a remote node.
 *
 * @param em The event to dispatch, including meta data
 */
static void dispatch_remote_event(looci_event_t* e) {
  PRINTF("[L_EM] Dispatching remote event from peer %u, component %u, eventtype: %u, length: %u\r\n", \
      e->source_node, e->source_cid, e->type, e->len);
  if(e->dst_cid != COMPONENT_ID_ANY){
	  PRINTF("[L_EM] directed ev ");
	  PRINTF("[L_EM] d_ev for %u\r\n",e->dst_cid);
	  postEventToInstance(e);
  } else{
	  vector_map(&subscriptions_from_remote,(vector_map_ft) dispatch_remote_from,e);
  }
}


/**
 * @internal
 */
int8_t events_add_local_subscription(subs_local* subscription)
{
	PRINTF("[LooCI EM] Adding subscription for event type %u: source '%u', destination '%u'\r\n", subscription->type, \
			subscription->src_cmp, subscription->dst_cmp);
	if( (subscription->src_cmp != COMPONENT_ID_ANY && looci_cmpMan_component_get(subscription->src_cmp) == NULL) ||
			looci_cmpMan_component_get(subscription->dst_cmp) == NULL){
		return ERROR_CMP_NOT_FOUND;
	} else if(subscription->src_cmp != COMPONENT_ID_ANY && !looci_cmpMan_hasInterface(subscription->src_cmp,subscription->type)){
		return ERROR_PROVIDED_INTERFACE_NOT_FOUND;
	} else if(!looci_cmpMan_hasReceptacle(subscription->dst_cmp,subscription->type)){
		return ERROR_REQUIRED_INTERFACE_NOT_FOUND;
	} else if(vector_get_el(&subscriptions,subscription)!=NULL){
		return ERROR_WIRE_DUPLICATE;
	} else{
		return vector_add(&subscriptions, subscription);
	}
}

/**
 * @internal
 */
int8_t events_add_remote_subscription_to(subs_rem_to* subscription)
{
	PRINTF("[LooCI EM] Adding remote subscription for event type %u: source '%u', destination %u\r\n", \
			subscription->type, subscription->src_cmp, subscription->dst_nod);
	if( (subscription->src_cmp != COMPONENT_ID_ANY && looci_cmpMan_component_get(subscription->src_cmp) == NULL)){
		return ERROR_CMP_NOT_FOUND;
	} else if(subscription->src_cmp != COMPONENT_ID_ANY && !looci_cmpMan_hasInterface(subscription->src_cmp,subscription->type)){
		return ERROR_PROVIDED_INTERFACE_NOT_FOUND;
	} else if(vector_get_el(&subscriptions_to_remote,subscription)!= NULL){
		return ERROR_WIRE_DUPLICATE;
	} else{
		return vector_add(&subscriptions_to_remote, subscription);
	}
}

/**
 * @internal
 */
int8_t events_add_remote_subscription_from(subs_rem_from* subscription)
{
  PRINTF("[LooCI EM] Adding subscription for incoming remote event type %u: source '%u/%u', destination %u\r\n",\
		  subscription->type, subscription->src_nod, subscription->src_cmp, subscription->dst_cmp);
	if( looci_cmpMan_component_get(subscription->dst_cmp) == NULL){
		return ERROR_CMP_NOT_FOUND;
	}  else if(!looci_cmpMan_hasReceptacle(subscription->dst_cmp,subscription->type)){
		return ERROR_REQUIRED_INTERFACE_NOT_FOUND;
	} else if(vector_get_el(&subscriptions_from_remote,subscription)!=NULL){
		return ERROR_WIRE_DUPLICATE;
	} else{
		return vector_add(&subscriptions_from_remote, subscription);
	}
}



/**
 * @internal
 */

int8_t events_remove_local_subscription(subs_local* subscription)
{
	PRINTF("[LooCI EM] Removing subscription for event type %u: source '%u', destination '%u'\r\n", subscription->type, \
			subscription->src_cmp, subscription->dst_cmp);
	if(vector_remove_el(&subscriptions,subscription,1)){
		return MSG_SUCCESS;
	} else{
		return ERROR_WIRE_NOT_FOUND;
	}
}


/**
 * @internal
 */
int8_t events_remove_remote_subscription_to(subs_rem_to* subscription)
{
  PRINTF("[LooCI EM] Removing remote subscription for event type %u: source '%u', destination %u\r\n",\
		  subscription->type, subscription->src_cmp, subscription->dst_nod);
	if(vector_remove_el(&subscriptions_to_remote,subscription,1)){
		return MSG_SUCCESS;
	} else{
		return ERROR_WIRE_NOT_FOUND;
	}
}

/**
 * @internal
 */
int8_t events_remove_remote_subscription_from(subs_rem_from* subscription)
{
	PRINTF("[LooCI EM] Removing subscription for incoming remote event type %u: source '%u/%u', destination %u\r\n",\
			subscription->type, subscription->src_nod, subscription->src_cmp, subscription->dst_cmp);
	if(vector_remove_el(&subscriptions_from_remote,subscription,1)){
		return MSG_SUCCESS;
	} else{
		return ERROR_WIRE_NOT_FOUND;
	}
}

/**
 * @internal
 */
int8_t events_unwire_component(lc_rc_component_t* id) {
	if(looci_cmpMan_component_get(id->cid) == NULL){
		return ERROR_CMP_NOT_FOUND;
	}

	bool uwLc(subs_local* sub, lc_rc_component_t* data){
		return (sub->src_cmp == data->cid || sub->dst_cmp == data->cid);
	}
	vector_rm_filter(&subscriptions,(vector_filter_ft)uwLc,id,255);

	bool uwTo(subs_rem_to* tsub, lc_rc_component_t* data){
		return (tsub->src_cmp == data->cid);
	}
	vector_rm_filter(&subscriptions_to_remote,(vector_filter_ft)uwTo,id,255);

	bool uwFrom(subs_rem_from* fsub, lc_rc_component_t* data){
		return (fsub->dst_cmp == data->cid);
	}
	vector_rm_filter(&subscriptions_from_remote,(vector_filter_ft)uwFrom,id,255);
	return MSG_SUCCESS;
}

/**
 * @internal
 */
int8_t events_getlocalwires(lc_is_get_lcl_wires_t* data) {
	PRINT_LN("requested local wires for type %u, src %u, dst %u",data->eventtype,data->src_cid,data->dst_cid);
	bool filter_get_local_wire(subs_local* el, lc_is_get_lcl_wires_t* data){
		if((el->src_cmp == data->src_cid || data->src_cid == COMPONENT_ID_ANY)
				&& (el->dst_cmp == data->dst_cid || data->dst_cid == COMPONENT_ID_ANY)
				&& (el->type == data->eventtype || data->eventtype == EVENT_ANY)) {
			memcpy(&data->buffer[data->index],el,sizeof(subs_local));
			data->index++;
		}
		return data->index == data->size;
	}
	if(vector_filter(&subscriptions,(vector_filter_ft)filter_get_local_wire,data) == NULL){
		return MSG_SUCCESS;
	} else{
		return MSG_SUCCESS_CONTINUE;
	}

}

/**
 * @internal
 */
int8_t events_getremotewires_to(lc_is_get_out_wires_t* data) {
	PRINT_LN("requested outgoing wires for type %u, src %u, dstNode %u",data->eventtype,data->src_cid,data->tgt_node);
	bool filter_get_out_wire(subs_rem_to* tsub, lc_is_get_out_wires_t* data){
		if((tsub->src_cmp == data->src_cid ||data->src_cid == COMPONENT_ID_ANY)
				&& (tsub->dst_nod == data->tgt_node || data->tgt_node == PEER_ID_ANY)
				&&(tsub->type == data->eventtype|| data->eventtype == EVENT_ANY)) {
				memcpy(&data->buffer[data->index],tsub,sizeof(subs_rem_to));
				data->index++;
			}
		return data->index == data->size;
	}
	if( vector_filter(&subscriptions_to_remote,(vector_filter_ft)filter_get_out_wire,data)==NULL){
		return MSG_SUCCESS;
	} else{
		return MSG_SUCCESS_CONTINUE;
	}
}

/**
 * @internal
 */
int8_t events_getremotewires_from(lc_is_get_inc_wires_t* data){
	PRINT_LN("[EM] requested incoming wires for type %u, src %u, src_node %u, dst %u",data->eventtype,data->src_cid,data->src_node,data->dst_cid);
	bool filter_get_inc_wire(subs_rem_from* fsub, lc_is_get_inc_wires_t* data){
		if(((fsub->type == data->eventtype) || (data->eventtype == EVENT_ANY))
				&& ((fsub->src_cmp ==  data->src_cid) ||(data->src_cid == COMPONENT_ID_ANY))
				&& ((fsub->src_nod == data->src_node) || (data->src_node == PEER_ID_ANY))
				&& ((fsub->dst_cmp == data->dst_cid) ||(data->dst_cid == COMPONENT_ID_ANY)) ) {

			subs_rem_from* datatgt = &data->buffer[data->index];
			memcpy(datatgt,fsub,sizeof(subs_rem_from));
			PRINT_LN("[EM] found type %u, src %u, src_node %u, dst %u",datatgt->type,datatgt->src_cmp,datatgt->src_nod,datatgt->dst_cmp);
			PRINT_LN("[EM] from type %u, src %u, src_node %u, dst %u",fsub->type,fsub->src_cmp,fsub->src_nod,fsub->dst_cmp);
			data->index++;
		}
		return data->index == data->size;
	}
	if(vector_filter(&subscriptions_from_remote,(vector_filter_ft)filter_get_inc_wire,data)==NULL){
		return MSG_SUCCESS;
	} else{
		return MSG_SUCCESS_CONTINUE;
	}
}

/** @} */

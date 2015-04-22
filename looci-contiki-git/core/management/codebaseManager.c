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
 * Copyright (c) 2009, Katholieke Universiteit Leuven
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of the Katholieke Universiteit Leuven nor the names of
 *       its contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * @addtogroup components
 * @{
 */
/**
 * @file
 * Implementation for the LooCI codebase system
 */

#include "component.h"
#include "comp_services.h"
#include "codebaseManager.h"
#include "codebaseDeployment.h"
#include "project-conf.h"
#include "componentManager.h"
#include "lib/list.h"
#include "utils.h"
#include "error_codes.h"
#include "interceptor.h"
#include <string.h>
#include <stdbool.h>
#include <avr/pgmspace.h>

#ifdef LOOCI_MGT_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif

LIST(codebases);

#define MIN_ID 1
#define MAX_ID 127

void looci_cbMan_init(){
	PRINTF("[LC_CSTORE]init");
	// Initialize codebases
	list_init(codebases);
	// autostart codebases
	uint8_t i = 0;
	struct component* autoCodebases[] = START_COMPONENTS;
	for(; i < NR_START_COMPONENTS; i ++){
		autoCodebases[i]->doReg(COMP_OP_INIT);
	}
	PRINTF("[LC_CSTORE]init done");
}

static struct looci_codebase * looci_cbMan_get_first_codebase() {
  return (struct looci_codebase *) list_head(codebases);
}

struct looci_codebase* looci_cbMan_get_codebase(uint8_t cb_id) {
  struct looci_codebase * current  = looci_cbMan_get_first_codebase();
  for(; current != NULL; current = current->next) {
    if(current->id == cb_id) {
      return current;
    }
  }
  return NULL;
}




struct looci_codebase * lc_codebases_get_by_type(char * type) {
  struct looci_codebase * current = looci_cbMan_get_first_codebase();
  uint_farptr_t name;
  for(; current != NULL; current = current->next) {
	  name = getFP(current->name,current->src_cmp);
	  if(strcmp_F(type, name)==0) {
		  return current;
	  }
  }
  return NULL;
}

uint8_t _r(uint8_t mode, struct looci_codebase* codebase){
	if(mode != COMP_OP_INIT){
		return 0;
	}

	uint8_t nextid = MIN_ID;
	if(CB_ID(codebase)== COMPONENT_ID_NONE) {
		 while(looci_cbMan_get_codebase(nextid) != NULL){
			nextid ++;
			if(nextid > MAX_ID){return 0;}
		 };
		 CB_ID(codebase) = nextid;
	 }
	  // print some debug info
	  PRINTF("Adding component with id %u, iAddr %p, rAddr %p, nAddr %p\r\n",
			  CB_ID(codebase),codebase->interfaces,codebase->receptacles,codebase->name);
	  PRINT_LN("process loc %u: ",(uint16_t)codebase->thread);
	  // add to list of started components

	  list_add(codebases, codebase);

	  if(codebase->flags&LC_MASK_AUTO_START){
		  looci_cmpMan_instantiateComponent(codebase);
	  }
	  lifecycle_intercept_t data = {INTERCEPT_LIFECYCLE_DEPLOY,codebase,NULL};
	  lc_intercept(INTERCEPT_LIFECYCLE_CHANGE,&data);
	  return CB_ID(codebase);
}

int8_t looci_cbMan_instantiate_component(lc_rc_instantiate_t* cb){
	uint8_t cid = cb->cbid;
	struct looci_codebase * codebase = looci_cbMan_get_codebase(cid);
	if(codebase == NULL) {
		PRINT_LN("[cbMan] instantiate cb %u: not found",cb->cbid);
		return ERROR_CB_NOT_FOUND;
	} else{
		cb->cid = looci_cmpMan_instantiateComponent(codebase);
		PRINT_LN("[cbMan] instantiate cb %u: cmp %u",cb->cbid,cb->cid);
		return MSG_SUCCESS;
	}
}

int8_t looci_cbMan_deploy_component(lc_rc_codebase_t* data){
	return 0;
}

int8_t looci_cbMan_undeploy_component(lc_rc_codebase_t* cb){
	uint8_t cid = cb->cbid;
	struct looci_codebase * codebase = looci_cbMan_get_codebase(cid);
	if(codebase == NULL){
		return ERROR_CB_NOT_FOUND;
	} else{
		  lifecycle_intercept_t data = {INTERCEPT_LIFECYCLE_REMOVE,codebase,NULL};

		  if(lc_intercept(INTERCEPT_LIFECYCLE_CHANGE,&data) &&
				  looci_cmpMan_destroyAllComponents(codebase)){
				PRINTF("Removing codebase with id %u\r\n", CB_ID(codebase));
				list_remove(codebases, codebase);
		#ifdef WITH_LOADABLE_COMPONENTS
				lc_cstore_unload(codebase->src_cmp);
		#endif
				return 1;
		  } else{
			  return 0;
		  }
	}
}


//51
int8_t looci_cbMan_get_cb_ids(lc_is_get_cbids_t* data) {
  struct looci_codebase * c = looci_cbMan_get_first_codebase();
  uint8_t nb = 0;
  for(; nb < data->size && c != NULL; ++nb, c = c->next) {
	  data->buffer[nb] = c->id;
  }
  data->elements = nb;
  return (c == NULL);
}


//52
int8_t looci_cbMan_getcodebasetype(lc_is_get_cb_type_t* data) {
  struct looci_codebase * c = looci_cbMan_get_codebase(data->id);
  if(c!=NULL) {
	  uint_farptr_t tgt = getFP(c->name,c->src_cmp);
	  data->size = strcpy_F(data->type,tgt,data->maxsize);
	  return 1;
  } else {
	  data->type[0] = 0;
	  data->size = 1;
    return ERROR_CB_NOT_FOUND;
  }
}

//53
int8_t looci_cbMan_getcodebaseids_by_type(lc_is_get_cbids_of_type_t* data) {
  struct looci_codebase * current = looci_cbMan_get_first_codebase();
  uint8_t nb = 0;
  for(; current != NULL && nb < data->size; current = current->next) {
	    if(strcmp_F(data->ctype,getFP(current->name,current->src_cmp))==0) {
	    	data->buffer[nb] = CB_ID(current);
	    	++nb;
	    }
  }
  data->elements = nb;
  if(nb == 0){
	  return ERROR_CB_NOT_FOUND;
  } else{
	  return 1;
  }
}



/** @} */


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
 * Copyright (c) 2010, Katholieke Universiteit Leuven
 * All rights reserved.
 * do_copy_data
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
#include "contiki.h"
#include "looci.h"
#include "accessControl.h"
#include "timer_utils.h"
#include "utils.h"
#include <string.h>
#include <stdint.h>

#ifdef LOOCI_COMPONENT_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif

#define RF_EVENT 98
#define ACCESS_CONTROL_EVENT 116
#define APPLICATION_EVENT_TYPE 82
#define PROP_SET_USER1 1501
#define PROP_SET_USER2 1502
#define PROP_SET_USER3 1503
#define PROP_SET_USER4 1504
#define PROP_SET_USER5 1505

#define NR_USERS 5
#define NR_LOGS 10


struct idRecords {
	uint8_t tagId[5];
};


struct state{
	  struct idRecords users[NR_USERS];
	  uint8_t last_logged[NR_LOGS];
	  uint8_t nrLogged;
	  uint8_t sample_interval;
	  struct etimer et;
};
looci_property_t test;

#define LOOCI_COMPONENT_NAME ac_comp
#define LOOCI_NR_PROPERTIES 5
LOOCI_PROPERTIES(
		{PROP_SET_USER1,DATATYPE_BYTE_ARRAY,offsetof(struct state,users),5,NULL},
		{PROP_SET_USER2,DATATYPE_BYTE_ARRAY,offsetof(struct state,users)+1*sizeof(struct idRecords),5,NULL},
		{PROP_SET_USER3,DATATYPE_BYTE_ARRAY,offsetof(struct state,users)+2*sizeof(struct idRecords),5,NULL},
		{PROP_SET_USER4,DATATYPE_BYTE_ARRAY,offsetof(struct state,users)+3*sizeof(struct idRecords),5,NULL},
		{PROP_SET_USER5,DATATYPE_BYTE_ARRAY,offsetof(struct state,users)+4*sizeof(struct idRecords),5,NULL}
);
//COMPONENT_NO_INTERFACES();
COMPONENT_INTERFACES(APPLICATION_EVENT_TYPE);
//COMPONENT_NO_RECEPTACLES();
COMPONENT_RECEPTACLES(APPLICATION_EVENT_TYPE);
LOOCI_COMPONENT( "ac",struct state);


static uint8_t init(struct state* compState, void* data){
	compState->nrLogged = 0;
	compState->sample_interval = 10;
	PRINTF("init");
	return 1;
}


static uint8_t activate(struct state* compState, void* data){
	ETIMER_SET(&compState->et, (compState->sample_interval * 128));
	return 1;

}

static uint8_t time(struct state* compState, void* data){
	PUBLISH_EVENT(APPLICATION_EVENT_TYPE,compState->last_logged,compState->nrLogged);
	compState->nrLogged = 0;
	ETIMER_RESET(&compState->et);
	return 1;
}

static uint8_t event(struct state* compState, core_looci_event_t* event){
	if(event->type == RF_EVENT){
		uint8_t i = 0;
		for(i = 0; i < NR_USERS ; i++){
			if(!memcmp(event->payload,compState->users[i].tagId,5)){
			//if(1){
				i += NR_USERS; //make loop end by adding to end
				if(compState->nrLogged < NR_LOGS){
					compState->last_logged[compState->nrLogged] = i;
					compState->nrLogged ++;
				}
			}
		}
	}
	return 1;
}


COMP_FUNCS_INIT //THIS LINE MUST BE PRESENT
COMP_FUNC_INIT(init)
COMP_FUNC_ACTIVATE(activate)
COMP_FUNC_TIMER(time)
COMP_FUNC_EVENT(event)
COMP_FUNCS_END(NULL)//THIS LINE MUST BE PRESENT

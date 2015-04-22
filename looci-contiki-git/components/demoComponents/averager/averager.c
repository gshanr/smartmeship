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
#include "contiki.h"
#include "looci.h"
#include "utils.h"
#include "timer_utils.h"

#define ANY_EVENT 1
#define APPLICATION_DO_EVENT 82

#define LISTENING_EVENT_PROP 60
#define PRODUCTION_EVENT_PROP 61
#define ATTACH_TIME_PROP 11

struct state{
	uint16_t listeningEvent;
	uint16_t productionEvent;
	uint8_t nr_readings;
	uint16_t totalReadings;
	uint8_t attach_timestamp;
};

#define LOOCI_COMPONENT_NAME averager
#define LOOCI_NR_PROPERTIES 3
LOOCI_PROPERTIES(
		{ATTACH_TIME_PROP,DATATYPE_BYTE,offsetof(struct state,attach_timestamp),1,NULL},
		{PRODUCTION_EVENT_PROP,DATATYPE_SHORT,offsetof(struct state,productionEvent),2,NULL},
		{LISTENING_EVENT_PROP,DATATYPE_SHORT,offsetof(struct state,listeningEvent),2,NULL},
);

//COMPONENT_NO_INTERFACES();
COMPONENT_INTERFACES(ANY_EVENT);
//COMPONENT_NO_RECEPTACLES();
COMPONENT_RECEPTACLES(ANY_EVENT);
LOOCI_COMPONENT("averager",struct state);



static uint8_t init(struct state* compState, void* data){
	compState->nr_readings = 0;
	compState->totalReadings = 0;
	compState->listeningEvent = 1;
	compState->productionEvent = 1;
	compState->attach_timestamp = 0;
	return 1;
}

static uint8_t event(struct state* compState, core_looci_event_t* event){
	// check if event type matches
	if(evpTM(event->type,compState->listeningEvent)){
		compState->nr_readings += 1;
		compState->totalReadings += GET_UINT16(event->payload+0);
	} else if(event->type == APPLICATION_DO_EVENT){
		uint16_t val = compState->totalReadings / compState->nr_readings;
		compState->totalReadings = 0;
		compState->nr_readings = 0;
		PUBLISH_EVENT(compState->productionEvent,&val,sizeof(val));
	}
	return 1;
}

COMP_FUNCS_INIT //THIS LINE MUST BE PRESENT
COMP_FUNC_INIT(init)
COMP_FUNC_EVENT(event)
COMP_FUNCS_END(NULL)//THIS LINE MUST BE PRESENT

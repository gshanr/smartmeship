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
#include "act_val_filter.h"
#include "utils.h"

#define APPLICATION_DO_YN_EVENT 82
#define ANY_SENSOR_EVENT 1

#define HIGH_READING_PROP 70
#define LOW_READING_PROP 71

#define LISTENING_EVENT_PROP 60

struct state{
	  uint16_t upperVal;
	  uint16_t lowerVal;
	  uint16_t filterEvent;
	  uint8_t posMeasure;
	  uint8_t isActive;
};
#define LOOCI_COMPONENT_NAME act_val_comp
#define LOOCI_NR_PROPERTIES 3
LOOCI_PROPERTIES(
		{HIGH_READING_PROP,DATATYPE_SHORT,offsetof(struct state,upperVal),2,NULL},
		{LOW_READING_PROP,DATATYPE_SHORT,offsetof(struct state,lowerVal),2,NULL},
		{LISTENING_EVENT_PROP,DATATYPE_SHORT,offsetof(struct state,filterEvent),2,NULL},
);
//COMPONENT_NO_INTERFACES();
COMPONENT_INTERFACES(APPLICATION_DO_YN_EVENT);
//COMPONENT_NO_RECEPTACLES();
COMPONENT_RECEPTACLES(APPLICATION_DO_YN_EVENT,ANY_SENSOR_EVENT);
LOOCI_COMPONENT("actValFt",struct state);


static uint8_t init(struct state* compState, void* data){
	compState->upperVal = 100;
	compState->lowerVal = 0;
	compState->isActive = 0;
	compState->posMeasure = 0; // 2 = not init
	compState->filterEvent = 0;
	return 1;
}

static uint8_t event(struct state* compState, core_looci_event_t* event){
	if(event->type == APPLICATION_DO_YN_EVENT){
		if((compState->isActive && !event->payload[0])||
				(!compState->isActive && event->payload[0])
			){
			//changing from active to non active
			compState->isActive = event->payload[0];
			if(compState->posMeasure){
				//last measurement was positive => need to publish wether or not it
				// it goes active or inactive
				PUBLISH_EVENT(APPLICATION_DO_YN_EVENT, &compState->isActive, 1);
			}
		}
		// in other cases, the filter was active, and new value sets active
		// or filter was inactive, and new value is also inactive => do nothing
	} else if(event->type == compState->filterEvent){
		uint16_t newVal = GET_UINT16(event->payload);
		if(newVal >= compState->lowerVal && newVal <= compState->upperVal){
			if(compState->isActive && !compState->posMeasure){
				// previous measurement was negative => publish do TRUE event
				PUBLISH_EVENT(APPLICATION_DO_YN_EVENT, &compState->isActive, 1);
			}
			compState->posMeasure =1;
		}else{
			if(compState->isActive && compState->posMeasure){
				uint8_t val = 0;
				PUBLISH_EVENT(APPLICATION_DO_YN_EVENT, &val, 1);
			}
			compState->posMeasure =0;
		}
	}
	return 1;
}

COMP_FUNCS_INIT //THIS LINE MUST BE PRESENT
COMP_FUNC_INIT(init)
COMP_FUNC_EVENT(event)
COMP_FUNCS_END(NULL)//THIS LINE MUST BE PRESENT

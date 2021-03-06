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

#define ANY_EVENT 1
#define APPLICATION_DO_EVENT 82

#define HIGH_READING_PROP 70
#define LOW_READING_PROP 71
#define LISTENING_EVENT_PROP 60
#define PRODUCTION_EVENT_PROP 61
#define ATTACH_TIME_PROP 11

struct state{
	uint16_t listeningEvent;
	uint16_t productionEvent;
	uint16_t highReading;
	uint16_t lowReading;
	uint8_t attach_timestamp;
};

#define LOOCI_COMPONENT_NAME filter


//COMPONENT_NO_INTERFACES();
COMPONENT_INTERFACES(ANY_EVENT);
//COMPONENT_NO_RECEPTACLES();
COMPONENT_RECEPTACLES(ANY_EVENT);

#define LOOCI_NR_PROPERTIES 5
LOOCI_PROPERTIES(
		{LISTENING_EVENT_PROP,DATATYPE_SHORT,offsetof(struct state,listeningEvent),2,NULL},
		{PRODUCTION_EVENT_PROP,DATATYPE_SHORT,offsetof(struct state,productionEvent),2,NULL},
		{HIGH_READING_PROP,DATATYPE_SHORT,offsetof(struct state,highReading),2,NULL},
		{LOW_READING_PROP,DATATYPE_SHORT,offsetof(struct state,lowReading),2,NULL},
		{ATTACH_TIME_PROP,DATATYPE_BYTE,offsetof(struct state,attach_timestamp),1,NULL},
);

static const struct state initVar = {
		.listeningEvent = 1,
		.productionEvent=1,
		.lowReading = 0,
		.highReading = 100,
		.attach_timestamp =0
};

LOOCI_COMPONENT_INIT("filter",struct state,&initVar);

static uint8_t onEvent(struct state* compState, core_looci_event_t* event){
	// check if event type matches
	if(evpTM(event->type,compState->listeningEvent)){
		uint16_t val = GET_UINT16(event->payload+0);
		if(val <= compState->highReading && val >= compState->lowReading){
			lcpEvPub(compState->productionEvent,&val,sizeof(val));
		}

	}
	return 1;
}

COMP_FUNCS_INIT //THIS LINE MUST BE PRESENT
COMP_FUNC_EVENT(onEvent)
COMP_FUNCS_END(NULL)//THIS LINE MUST BE PRESENT

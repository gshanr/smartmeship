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
 * Component for sampling a custom door sensor attached to the ADC of an avr raven.
 *
 * Copyright (c) 2011, Katholieke Universiteit Leuven
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
/*
 * ADC code:
 * Copyright (c) 2011, Murdoch www.wsncloud-murdoch.org group.
 */
#include "contiki.h"
#include "looci.h"
#include "utils.h"
#include "switch-sensor.h"
#include "event-types.h"

#ifdef CONTIKI_TARGET_AVR_RAVEN
#include "raven-msg.h"
#include "lib/sensors.h"
#endif


#ifdef LOOCI_COMPONENT_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif


#define DOOR_EVENT 110
#define DOOR_ADC_CHANNEL 0
#define DOOR_ADC_THRESHOLD 900

struct state{
	struct etimer et;
	uint8_t door;
};


#define LOOCI_COMPONENT_NAME door_sample
#define LOOCI_NR_PROPERTIES 0
LOOCI_PROPERTIES();

COMPONENT_INTERFACES( DOOR_EVENT);
COMPONENT_NO_RECEPTACLES();
LOOCI_COMPONENT( "Door Sampling", struct state);

static uint8_t activate(struct state* compState, void* data){
	PRINTF("Door sampling starting\r\n");
	SENSORS_ACTIVATE(switch_sensor);
	compState->door = SWITCH_CLOSED;
	ETIMER_SET(&compState->et, CLOCK_SECOND * 10);
	return 1;
}

static uint8_t deactivate(struct state* compState, void* data){
	SENSORS_DEACTIVATE(switch_sensor);
	PRINTF("Door sampling stopped\r\n");
	return 1;
}


static uint8_t time(struct state* compState, void* data){
    uint16_t value = readADC(DOOR_ADC_CHANNEL);
    PRINTF("Got new door sensor (ADC) value: %u\r\n", value);
    if((value <= DOOR_ADC_THRESHOLD && compState->door==SWITCH_CLOSED) || (value > DOOR_ADC_THRESHOLD && compState->door==SWITCH_OPEN)) {
    	// door went from closed to open or vice versa
    	// toggle door
    	compState->door = (compState->door==SWITCH_CLOSED) ? SWITCH_OPEN : SWITCH_CLOSED;
    	// publish event
    	PUBLISH_EVENT(DOOR_EVENT, (uint8_t*) &compState->door, sizeof(compState->door));
    }
    ETIMER_RESET(&compState->et);
    return 1;
}


static uint8_t defaultFunc(struct state* state,struct contiki_call* data){
	return 0;
}
//FUNCTION DECLARATION

COMP_FUNCS_INIT //THIS LINE MUST BE PRESENT
COMP_FUNC_ACTIVATE(activate)
COMP_FUNC_DEACTIVATE(deactivate)
COMP_FUNC_TIMER(time)
COMP_FUNCS_END(defaultFunc)//THIS LINE MUST BE PRESENT


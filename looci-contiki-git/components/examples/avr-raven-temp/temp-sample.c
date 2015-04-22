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
#include "looci.h"
#include "contiki.h"
#include "component.h"
#include "sensors.h"
#include "event-types.h"
#ifdef CONTIKI_TARGET_AVR_RAVEN
#include "raven-msg.h"
#include "temp-sensor.h"
#endif
#include "timer_utils.h"

#ifdef LOOCI_COMPONENT_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif


#define PROPERTY_ID_INTERVAL 1

struct state{
	struct etimer et;
	uint8_t interval;
};

#define LOOCI_COMPONENT_NAME temp_sample
#define LOOCI_NR_PROPERTIES 1
static const looci_property_t temp_sample_properties[1] __attribute__((__progmem__)) = {{1,1,( &((struct state *)0)->interval),1,((void *)0)}};
LOOCI_PROPERTIES({PROPERTY_ID_INTERVAL,DATATYPE_BYTE,offsetof(struct state,interval),1,NULL});
COMPONENT_INTERFACES( TEMP_READING);
COMPONENT_NO_RECEPTACLES();
static const struct state initVar PROGMEM = {.interval = 10};
LOOCI_COMPONENT_INIT("temp sample",struct state,&initVar);

static uint8_t activate(struct state* compState, void* data){
	ETIMER_SET(&compState->et, CLOCK_SECOND * compState->interval);
	return 1;
}


static uint8_t time(struct state* compState, void* data){
	PRINT_LN("activate temp sensor");
	ETIMER_RESET(&compState->et);
	SENSORS_ACTIVATE(temp_sensor);
	return 1;
}


static uint8_t sensorCall(struct state* compState, void* sensor){
	PRINT_LN("sensor event %s",((struct sensors_sensor*)sensor)->type);
	if(sensor == &temp_sensor && temp_sensor.status(SENSORS_READY)){
		uint8_t temp = temp_sensor.value(0);
		PRINT_LN("Sampled temp : %u  ", temp);
		PUBLISH_EVENT(TEMP_READING, (uint8_t*)&temp, sizeof(temp));
	}
	return 1;
}

//FUNCTION DECLARATION

COMP_FUNCS_INIT //THIS LINE MUST BE PRESENT
COMP_FUNC_ACTIVATE(activate)
COMP_FUNC_TIMER(time)
COMP_FUNC_SENSOR(sensorCall)
COMP_FUNCS_END(NULL)//THIS LINE MUST BE PRESENT

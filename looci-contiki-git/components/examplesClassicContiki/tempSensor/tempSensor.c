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
#include "contiki.h"
#include "looci.h"
#include "utils.h"
#include "timer_utils.h"
#include <avr/pgmspace.h>
#include "sensors.h"
#include "temp-sensor.h"

#ifdef CONTIKI_TARGET_AVR_RAVEN
#include "raven-msg.h"
#endif

#include <stdint.h>
#include <stdio.h>

#ifdef LOOCI_COMPONENT_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif

//STATE DEFINITION

struct state{
	struct etimer et;
	uint8_t interval;
};

//LOOCI COMPONENT DEFINITION


#define TEMP_READING 4002

#define LOOCI_COMPONENT_NAME testSensor
COMPONENT_INTERFACES(TEMP_READING);
COMPONENT_NO_RECEPTACLES();
#define LOOCI_NR_PROPERTIES 0
LOOCI_PROPERTIES();
static struct state initVar PROGMEM = {.interval=10};
LOOCI_COMPONENT_INIT("testSensor",struct state,&initVar);

COMPONENT_THREAD(ev, data)
{
  COMPONENT_BEGIN(struct state, compState);

  // NOTE
  // printf might not work in a loadable component
  // this depends on whether printf is used in the core or not ...
  PRINTF("Temperature sampling starting\r\n");

  while(1) {
    etimer_set(&compState->et, CLOCK_SECOND * compState->interval);
    PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&compState->et));

    // Activate the sensor
    SENSORS_ACTIVATE(temp_sensor);
    // and wait until the value is available
    while(!temp_sensor.status(SENSORS_READY)) {
      PROCESS_WAIT_EVENT_UNTIL(ev == sensors_event && data == &temp_sensor);
    }
    // get the value
    uint8_t temp = temp_sensor.value(0);
    PRINTF("Measured temperature: %i\r\n", temp);

    // NOTE
    // printf might not work in a loadable component
    // this depends on whether printf is used in the core or not ...
    PRINTF("Temperature sampled\r\n");

    PUBLISH_EVENT(TEMP_READING, (uint8_t*)&temp, sizeof(temp));

  }

  COMPONENT_END();
}
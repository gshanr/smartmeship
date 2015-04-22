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

#define APPLICATION_EVENT_TYPE 82
#define PROP_TIME_INTERVAL 50

#define OPEN 1
#define CLOSED 0

#define THIS_EVENT 99

struct state{
	struct etimer et;
	uint8_t interval;
	uint8_t switchPosition;
	uint8_t pinAContent;
	uint8_t zeroCounter;
};

#define LOOCI_COMPONENT_NAME contact_comp

//COMPONENT_NO_INTERFACES();
COMPONENT_INTERFACES(APPLICATION_EVENT_TYPE);
COMPONENT_NO_RECEPTACLES();
//COMPONENT_RECEPTACLES(APPLICATION_EVENT_TYPE);


#define LOOCI_NR_PROPERTIES 1
LOOCI_PROPERTIES(
		{PROP_TIME_INTERVAL,DATATYPE_BYTE,offsetof(struct state,interval),1,NULL}
);

static struct state initVar PROGMEM = {.interval = 10,.switchPosition=CLOSED,.zeroCounter=0,.pinAContent = 0};
LOOCI_COMPONENT_INIT("contact",struct state,&initVar);

static uint8_t onActivate(struct state* compState, void* data){
	  // Define pull-ups or set outputs high
	  // PORTA |= (1<<PA2);
	  // disabling pull-ups or set outputs low
	  PORTA &= ~(1<<PA2); // setting PA6 to OFF
	  // Define directions for port pins to output
	   //DDRA |= (1<<DDA2);
	   //define direction for port pin to input
	   DDRA &= ~(1 << DDA2);
	  // DDRA=0b00100000;//set pin5 as output
	 // Insert nop for synchronization
	  // __no_operation();
	   compState->pinAContent = PINA;
	   if (compState->pinAContent & (1<<PA2)){// initializing the position of the door or window
		   compState->switchPosition = CLOSED;
	   }else {
		   compState->switchPosition = OPEN;
	   }
	   ETIMER_SET(&(compState->et), CLOCK_SECOND * compState->interval);
	return 1;
}

static uint8_t onTimer(struct state* compState, void* data){
	//create random value
	compState->pinAContent = PINA; //read portA pins


	if (compState->pinAContent & (1<<PA2)){
	  compState->zeroCounter = 0;
	}else {
	  compState->zeroCounter++;
	}
	if ((compState->pinAContent & (1<<PA2)) && (compState->switchPosition == OPEN) ){ //checks to see if there is 1 on portA2 means door or window closed
	  compState->zeroCounter = 0;
	  compState->switchPosition = CLOSED;
	  lcpEvPub(THIS_EVENT, &compState->switchPosition,1);
	}else if ((~(compState->pinAContent & (1<<PA2))) && (compState->switchPosition == CLOSED) && (compState->zeroCounter >2)){
	  compState->zeroCounter = 0;
	  compState->switchPosition = OPEN;
	  lcpEvPub(THIS_EVENT, &compState->switchPosition,1);
	}
	ETIMER_RESET(&(compState->et));
	return 1;
}

COMP_FUNCS_INIT //THIS LINE MUST BE PRESENT
COMP_FUNC_ACTIVATE(onActivate)
COMP_FUNC_TIMER(onTimer)
//COMP_FUNC_EVENT(event)
COMP_FUNCS_END(NULL)//THIS LINE MUST BE PRESENT

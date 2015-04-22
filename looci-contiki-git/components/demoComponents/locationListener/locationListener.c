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

#ifdef LOOCI_COMPONENT_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif

#define LOCATION_BC_EVENT 2001
#define LOCATION_NODE_EVENT 2002

struct beaconLoc{
	uint8_t beaconId;
	uint16_t beaconX;
	uint16_t beaconY;
	uint16_t dist;
	uint8_t age;
};

struct state{
	uint8_t interval;
	uint8_t receivedBeacon;
	uint16_t x;
	uint16_t y;
	struct beaconLoc location[5];
	struct etimer et;
};

#define LOOCI_COMPONENT_NAME comp_event

//COMPONENT_NO_INTERFACES();
COMPONENT_INTERFACES(LOCATION_NODE_EVENT);
//COMPONENT_NO_RECEPTACLES();
COMPONENT_RECEPTACLES(LOCATION_BC_EVENT);
#define LOOCI_NR_PROPERTIES 6
// uint16_t propertyId , uint8_t dataType , uint8_t offset , uint8_t size,name
LOOCI_PROPERTIES(
	{1,DATATYPE_BYTE,offsetof(struct state,interval), 1 , NULL},
	{2,DATATYPE_BYTE_ARRAY,offsetof(struct state,location),5,NULL},
	{3,DATATYPE_BYTE_ARRAY,offsetof(struct state,location)+sizeof(struct beaconLoc),5,NULL},
	{4,DATATYPE_BYTE_ARRAY,offsetof(struct state,location)+sizeof(struct beaconLoc)*2,5,NULL},
	{5,DATATYPE_BYTE_ARRAY,offsetof(struct state,location)+sizeof(struct beaconLoc)*3,5,NULL},
	{6,DATATYPE_BYTE_ARRAY,offsetof(struct state,location)+sizeof(struct beaconLoc)*4,5,NULL},
);

struct state initVar PROGMEM = {.x=0,.y=0,.interval=60,.receivedBeacon=0};

LOOCI_COMPONENT_INIT("locListener",struct state,&initVar);


static uint8_t activate(struct state* compState, void* data){
	ETIMER_SET(&compState->et,compState->interval * CLOCK_SECOND);
	return 1;
}

static void calcDist(struct state* compState){
	uint8_t goodBeacons[3];
	uint8_t age[3];
	uint8_t i = 0;
	for(i = 0 ; i < 3 ; i ++){
		goodBeacons[i] = 255;
		age[i] = 255;
	}
	for(i = 0 ; i < 5 ; i ++){
		uint8_t j;
		uint8_t myAge = compState->location[i].age;
		uint8_t myId = i;

		for(j = 0 ; j < 3 && myId != 255 ; j++){
			if(myAge<age[j]){
				uint8_t temp = age[j];
				age[j] = myAge;
				myAge = temp;
				temp = goodBeacons[j];
				goodBeacons[j] = myId;
				myId = temp;
			}
		}
	}
	if(goodBeacons[2] != 255){
		uint16_t x0 = compState->location[goodBeacons[0]].beaconX;
		uint16_t y0 = compState->location[goodBeacons[0]].beaconY;
		uint16_t r0 = compState->location[goodBeacons[0]].dist;
		uint16_t x1 = compState->location[goodBeacons[1]].beaconX;
		uint16_t y1 = compState->location[goodBeacons[1]].beaconY;
		uint16_t r1 = compState->location[goodBeacons[1]].dist;
		uint16_t x2 = compState->location[goodBeacons[2]].beaconX;
		uint16_t y2 = compState->location[goodBeacons[2]].beaconY;
		uint16_t r2 = compState->location[goodBeacons[2]].dist;
		uint16_t y = ((x1 - x0) * (r2*r2 -r0*r0 -y2*y2 -x2*x2 + x0*x0 + y0*y0) \
				+ (x0 - x2)*(r1*r1-r0*r0-y1*y1-x1*x1+x0*x0+y0*y0)) \
				/ ((x0 - x1)*(2*y2 - 2*y0) + (x2 - x0)*(2*y1-2*y0) )	;
		uint16_t x = (r1*r1-r0*r0+y*(2*y1-2*y0)-y1*y1-x1*x1+x0*x0+y0*y0)/(2*x0-2*x1);
		compState->x = x;
		compState->y = y;
	}else{
		compState->x = 0;
		compState->y = 0;
	}
}

static uint8_t time(struct state* compState, void* data){
	if(compState->receivedBeacon){
		calcDist(compState);

		uint16_t payload[2];
		payload[0] = UIP_HTONS(compState->x);
		payload[1] = UIP_HTONS(compState->y);
		PUBLISH_EVENT(LOCATION_NODE_EVENT,&payload,4);
		compState->receivedBeacon = 0;
	}
	return 1;
}



static uint8_t event(struct state* compState, core_looci_event_t* event){
	if(event->type == LOCATION_BC_EVENT){
		uint8_t id = event->payload[0];
		uint16_t dist = UIP_HTONS(*((uint16_t*)(event->payload+1)));
		uint8_t i = 0;
		for(i = 0 ; i < 5 ; i++){
			if(compState->location[i].beaconId == id){
				compState->location[i].dist = dist;
				compState->location[i].age = 0;
			}
		}
	}
	return 1;
}


//FUNCTION DECLARATION

COMP_FUNCS_INIT //THIS LINE MUST BE PRESENT
COMP_FUNC_ACTIVATE(activate)
COMP_FUNC_TIMER(time)
COMP_FUNC_EVENT(event)
COMP_FUNCS_END(NULL)//THIS LINE MUST BE PRESENT

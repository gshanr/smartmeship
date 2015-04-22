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
 * debug.c
 * FUSES
 *  Created on: Aug 5, 2011
 *      Author: root
 */
#include "debug.h"
#include "utils.h"

void lc_print(char* print){
	printf(print);
}

void lc_printString(char* string, uint8_t len){
	uint8_t i = 0;
	for(i = 0 ; i < len; i ++){
		PRINTF("%u-", (uint8_t) string[i]);
	}
	PRINTF("\n");
}

void lc_printByteArray(uint8_t* content, uint8_t len){
	uint8_t i = 0;
	for(i = 0 ; i < len ; i ++){
		PRINTF("%u-",(uint8_t)content[i]);
	}
	PRINTF("\n");
}


void lc_printShortArray(uint16_t* content, uint8_t len){
	uint8_t i = 0;
	for(i = 0 ; i < len ; i ++){
		PRINTF("%u-",(uint16_t)content[i]);
	}
	PRINTF("\n");
}


void lc_printHexArray(uint8_t* content, uint8_t len){
	uint8_t i = 0;
	char buffer[len * 2];
	xEnc((unsigned char*)content,(unsigned char*)buffer,len);
	for(i = 0 ; i < len ; i ++){
		PRINTF("%c%c-",buffer[2*i],buffer[2*i+1]);
	}
	PRINTF("\n");
}

void lc_printEvent(looci_event_t* event){
	PRINT_LN("[EV] ev: type %u, src cid %u, src node %u, dst cid %u, dst node %u",
			event->type,event->source_cid,event->source_node,event->dst_cid,event->dst_node);
	PRINTF("[EV] pl: ");
	PRINT_BYTE_ARRAY((uint8_t*)event->payloadMemory.ptr,event->len);
	PRINTF("[EV] hdr: ");
	PRINT_BYTE_ARRAY((uint8_t*)event->header,ev_get_totalHeaderSize(event->header));
	PRINT_LN("[EV] mmem: %p",event->payloadMemory.ptr);
}

void lc_printCoreEvent(core_looci_event_t* event){
	PRINT_LN("[EV] ev: type %u, ",event->type);
	PRINTF("[EV] pl: ");
	PRINT_BYTE_ARRAY((uint8_t*)event->payload,event->len);
}

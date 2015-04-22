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
 * event.c
 *
 *  Created on: Jan 18, 2012
 *      Author: root
 */


#include "event.h"
#include "component_type.h"
#include "mmem.h"
#include <string.h>

#ifdef LOOCI_EVENTS_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif

#define ERROR_HEADER 255
#define IS_VALID_HEADER(header) (header != ERROR_HEADER)

uint8_t ev_get_header_index(char* headers, uint8_t headerId){
	uint8_t headerIndex = 0;
	while(headers[headerIndex] != HEADER_PAYLOAD){
		if(headers[headerIndex] == headerId){
			return headerIndex;
		} else{
			headerIndex += headers[headerIndex+1]+2; //add 2 and the lenght of the header
		}
	}
	if(headerId == HEADER_PAYLOAD){
		return headerIndex;
	} else{
		return ERROR_HEADER;
	}
}

bool ev_has_header(struct looci_event* event, uint8_t headerId){
	return ev_get_header_index(event->header,headerId) != ERROR_HEADER;
}

uint8_t ev_get_header_len(struct looci_event* event, uint8_t headerId){
	uint8_t headerIndex = ev_get_header_index(event->header,headerId);
	if(IS_VALID_HEADER(headerIndex)){
		return event->header[headerIndex+1];
	} else{
		return 0;
	}
}

bool ev_get_header(struct looci_event* event, uint8_t headerId, char* buffer, uint8_t* buffer_length){
	uint8_t headerIndex = ev_get_header_index(event->header,headerId);
	if(IS_VALID_HEADER(headerIndex)){
		if(event->header[headerIndex+1] <= *buffer_length){
			*buffer_length = event->header[headerIndex+1];
			memcpy(buffer,event->header + headerIndex + 2,*buffer_length);
			return true;
		}
	}
	return false;
}

bool ev_add_header(struct looci_event* event, uint8_t headerId, char* data, uint8_t length){
	uint8_t index = ev_get_header_index(event->header,HEADER_PAYLOAD);

	PRINTF("add hdr: %u %u %u \r\n",headerId,length,index);
	if(!(index + 2 + length <LOOCI_EVENT_MAX_HEADER_LEN) ){
		return false;
	}
	uint8_t payloadIndex = index + 2;
	memcpy(&(event->header[payloadIndex]),data,length);

	event->header[index] = headerId;
	event->header[index+1] = length;
	event->header[payloadIndex+length]=0;
	PRINTF("post add hdr: %u \r\n",ev_get_totalHeaderSize(event->header));
	return true;
}

bool ev_remove_header(struct looci_event* event, uint8_t headerId){
	uint8_t index = ev_get_header_index(event->header,headerId);
	if(IS_VALID_HEADER(index)){
		uint8_t nextBlockLoc = index + 2 + event->header[index+1];
		memcpy(event->header+index,event->header+nextBlockLoc,LOOCI_EVENT_MAX_HEADER_LEN-nextBlockLoc);
		return true;
	}
	return false;
}

uint8_t ev_get_totalHeaderSize(char* headers){
	return ev_get_header_index(headers,HEADER_PAYLOAD)+1;
}

bool ev_getHeaderBit(struct looci_event* event, uint8_t headerId, uint8_t byte, uint8_t mask){
	return (ev_getHeaderByte(event,headerId,byte) & (0x01<<mask)) > 0;
}

uint8_t ev_getHeaderByte(struct looci_event* event, uint8_t headerId, uint8_t byte){
	uint8_t index = ev_get_header_index(event->header,headerId);
	if(index == ERROR_HEADER){return 0;}
	if(event->header[index+1]<=byte){return 0;}
	return event->header[index+2+byte];
}

void ev_setHeaderBit(struct looci_event* event, uint8_t headerId, uint8_t byteIndex, uint8_t bitIndex, bool bit){
	uint8_t byte = ev_getHeaderByte(event,headerId,byteIndex);
	uint8_t mask = 0x01 << bitIndex;
	byte = (byte & ~mask)|(mask * bit);
	ev_setHeaderByte(event,headerId,byteIndex,byte);
}

void ev_setHeaderByte(struct looci_event* event, uint8_t headerId, uint8_t byteIndex, uint8_t new_byte){
	uint8_t index = ev_get_header_index(event->header,headerId);
	if(index == ERROR_HEADER){
		uint8_t newHeader[byteIndex+1];
		memset(newHeader,0,byteIndex+1);
		newHeader[byteIndex] = new_byte;
		ev_add_header(event,headerId, (char*) newHeader,byteIndex+1);
	} else{
		if(event->header[index+1] <= byteIndex){
			uint8_t newHeader[byteIndex+1];
			memcpy(newHeader,(event->header)+index+2,event->header[index+1]);
			ev_remove_header(event,headerId);
			newHeader[byteIndex] = new_byte;
			ev_add_header(event,headerId, (char*)newHeader,byteIndex+1);
		}else{
			event->header[index+2+byteIndex] = new_byte;
		}
	}
}

void ev_fill_event_from_byte_array(uint8_t* array, uint16_t size, looci_event_t* event){
	event->type =  *array;
	array ++;
	event->type *= 256;
	event->type +=(uint8_t) *array;
	array ++;
	event->source_cid = (uint8_t)*array;
	array ++;

	uint8_t hdrSize = ev_get_totalHeaderSize((char*)array);
	memcpy(event->header,array,hdrSize);
	event->header[hdrSize] = 0;
	array += hdrSize;
	size -= hdrSize;
	size -= 3;
	event->len = size;
	mmem_alloc(&event->payloadMemory,event->len);
	event->dst_cid = COMPONENT_ID_ANY;
	memcpy(event->payloadMemory.ptr, array,size);
}

uint16_t ev_fill_byte_array_from_event(looci_event_t* event, unsigned char* array){
	*array = event->type/256;
	array ++;
	*array = event->type%256;
	array ++;
	*array = event->source_cid;
	array ++;
	uint8_t hdrSize = ev_get_totalHeaderSize(event->header);
	memcpy(array, event->header,hdrSize);
	array += hdrSize;
	memcpy(array, event->payloadMemory.ptr, event->len);
	return (event->len + hdrSize + 3);
}

//event public component matches
bool evpCM(uint8_t compId, uint8_t filterId){
	return ((compId == filterId) || filterId == COMPONENT_ID_NONE);
}

//event public node matches
bool evpNM(uint8_t peerId, uint8_t filterId){
	return ((peerId == filterId) || filterId == PEER_ID_ANY);
}

//event public type matches
bool evpTM(looci_eventtype_t type,  looci_eventtype_t filterType){
	return (filterType != 0 && (type % filterType) == 0);
}

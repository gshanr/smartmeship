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
 * debug.h
 *
 *  Created on: Aug 5, 2011
 *      Author: root
 */

#ifndef DEBUG_H_
#define DEBUG_H_

#include <stdint.h>
#include <stdio.h>
#include "utils.h"
#include "event.h"

#if defined(__AVR__)
#include <avr/pgmspace.h>
#ifdef BUILD_COMPONENT
#define PRINTF_COMP(strName,string,args...){\
		static const char strName[] PROGMEM = string;\
		_lpt(strName,##args);\
	}
//Obscure c macro stuff to get variable name, ignore
#define NAME2(fun,suffix) fun ## _ ## suffix
#define NAME1(fun,suffix) NAME2(fun,suffix)
#define NAME(fun) NAME1(fun,__LINE__)
#define PRINTF(string,args...) PRINTF_COMP(NAME(str),string,##args)
#else
#define PRINTF(FORMAT,args...) printf_P(PSTR(FORMAT),##args)
#endif


#else // other target than avr raven
#define PRINTF printf
#endif // end CONTIKI_TARGET_AVR_RAVEN

void lc_print(char* print);

void lc_printString(char* string, uint8_t len);

void lc_printByteArray(uint8_t* content, uint8_t len);
void lc_printShortArray(uint16_t* content, uint8_t len);
void lc_printHexArray(uint8_t* content, uint8_t len);

void lc_printEvent(looci_event_t* event);
void lc_printCoreEvent(core_looci_event_t* event);

#define PRINT_LN(FORMAT,args...) PRINTF(FORMAT "\r\n",##args)

#define PRINT_STRING(string,len) lc_printString(string,len)

#define PRINT_BYTE_ARRAY(array,len) lc_printByteArray((uint8_t*)array,len)
#define PRINT_SHORT_ARRAY(array,len) lc_printShortArray((uint16_t*)array,len)
#define PRINT_HEX_ARRAY(array,len) lc_printHexArray((uint8_t*)array,len)

#define PRINT_EVENT(event) lc_printEvent(event);
#define PRINT_CORE_EVENT(event) lc_printCoreEvent(event);



#define PRINT6ADDR(addr) PRINTF(" %02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x ", ((uint8_t *)addr)[0], ((uint8_t *)addr)[1], ((uint8_t *)addr)[2], ((uint8_t *)addr)[3], ((uint8_t *)addr)[4], ((uint8_t *)addr)[5], ((uint8_t *)addr)[6], ((uint8_t *)addr)[7], ((uint8_t *)addr)[8], ((uint8_t *)addr)[9], ((uint8_t *)addr)[10], ((uint8_t *)addr)[11], ((uint8_t *)addr)[12], ((uint8_t *)addr)[13], ((uint8_t *)addr)[14], ((uint8_t *)addr)[15])


#endif /* DEBUG_H_ */

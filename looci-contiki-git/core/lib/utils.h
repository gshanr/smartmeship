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
 * utils.h
 *
 *  Created on: Jul 5, 2011
 *      Author: root
 */

#ifndef UTILS_H_
#define UTILS_H_

#include "hexCoding.h"
#include <stdint.h>
#include <avr/pgmspace.h>
#include <stdbool.h>

#define SWITCHEND16(n) (((n) << 8) | ((n) >> 8))
#define SWITCHEND32(n) ( (n >>24) | ((n&0x00FF0000)>>8) | ((n&0x0000FF00)<<8)|((n << 24)))
#define GET_UINT16(n) (uint16_t)SWITCHEND16(*(uint16_t*)(n))
#define GET_UINT32(n) (uint32_t)SWITCHEND32(*(uint32_t*)(n))

uint16_t lc_utils_readWord(const void* data);

#define CALL_ROM_FUNC(retType,structName,function) ((retType (*)())lc_utils_readWord(&structName->function))




#define JOIN2(part1,part2) part1 ## _ ## part2
#define JOIN(part1,part2) JOIN2(part1,part2)



void memxor(void* dest, const void* src, uint16_t n);




/**
 * Host To Network byte order for Short Array
 */
void htonsa(uint16_t* buffer, uint8_t len);

#define FREE_RAM 1
#define USED_HEAP 2
#define FREE_MMEM 3

uint16_t memV(uint8_t val);


uint16_t readADC(uint8_t ch);

void memcpy_F(char* dst,uint_farptr_t src, uint8_t size);
uint8_t strcpy_F(char* dst, uint_farptr_t src, uint8_t size);

uint_farptr_t getFP(const void* ptrVal,const void* cmp);

uint8_t strLen_F(uint_farptr_t src);
/*
 * Counts a list of shorts in flash memory
 * End short is not counted, so len can be 0
 */
uint8_t shortLen_F(uint_farptr_t src,uint16_t endShort);
uint8_t shortcmp_F(uint_farptr_t src,uint16_t targetShort, uint16_t endShort);
uint8_t strcmp_F(char* val1, uint_farptr_t val2);

#define SET_PORT_READ(channel) DDRA = (DDRA & ~(1<<channel))
#define SET_PORT_WRITE(channel) DDRA = (DDRA | (1<<channel))
#define READ_PORT(channel) (PINA & (1<<channel) > 0)
#define WRITE_PORT_ZERO(channel) PORTA = (PORTA & ~(1<<channel))
#define WRITE_PORT_ONE(channel) (PORTA = (PORTA | (1<<channel)))
#define WRITE_PORT(channel,val) (val>0?WRITE_PORT_ONE(channel):write_PORT_ZERO(channel))


//Function for OTA printing
int _lpt(const char* format,...);




typedef struct{
	uint8_t* buffer;
	uint8_t elements;
	uint8_t size;
}array_8_t;

#endif /* UTILS_H_ */

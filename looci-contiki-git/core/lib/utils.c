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
 * utils.c
 *
 *  Created on: Jul 5, 2011
 *      Author: root
 */
#include "utils.h"
#include "mmem.h"
#include <stdbool.h>
#include <string.h>
#include <stdio.h>
#include <avr/pgmspace.h>
#include "lib/sensors.h"
#include "elfloader-arch.h"
#include "hexCoding.h"
#include <avr/io.h>

void memxor(void* dest, const void* src, uint16_t n){
  while(n--){
    *((uint8_t*)dest) ^= *((uint8_t*)src);
    dest = (uint8_t*)dest +1;
    src  = (uint8_t*)src  +1;
  }
}

uint8_t mcmp(const void* v1, const void* v2, uint8_t len){
	return (uint8_t)memcmp(v1,v2,len);
}

void htonsa(uint16_t* buffer, uint8_t len){
	uint8_t i = 0;
	for(i = 0 ; i < len ; i++){
		buffer[i] = SWITCHEND16(buffer[i]);
	}
}

uint16_t lc_utils_readWord(const void* data){
	return pgm_read_word(data);
}

uint8_t divB(uint8_t divider, uint8_t diviser){
	return divider / diviser;
}

uint16_t divS(uint16_t divider, uint16_t diviser){
	return divider / diviser;
}

#define FREE_RAM 1
#define USED_HEAP 2
#define FREE_MMEM 3

uint16_t memV(uint8_t val){
	if(val == FREE_RAM){
		extern uint16_t __heap_start, __brkval;
		return (uint16_t) (SP - (__brkval == 0 ? (uint16_t) &__heap_start : (uint16_t) __brkval));
	} else if(val == USED_HEAP){
		extern uint16_t __heap_start, __brkval;
		return (__brkval == 0 ? 0 :  (uint16_t) __brkval - (uint16_t) &__heap_start);
	} else if(val == FREE_MMEM){
		return (uint16_t)mmem_freememory();
	} else{
		return 0;
	}
}


uint16_t readADC(uint8_t ch){
	ch = ch&0b00000111;
	ADMUX = ((0<<REFS1)|(1<<REFS0)|ch);

  ADCSRA=(1<<ADEN)|(0<<ADPS2)|(0<<ADPS1)|(0<<ADPS0); //Rrescalar div factor to 2
  //Start Single conversion
  ADCSRA|=(1<<ADSC);
  //Wait for conversion to complete
  while(!(ADCSRA & (1<<ADIF)));
  //Clear ADIF by stdout writing one to it
  ADCSRA|=(1<<ADIF);

  return(ADC);
}

void memcpy_F(char* dst,uint_farptr_t src, uint8_t size){
	uint8_t i = 0;
	for(i = 0 ; i < size ; i ++){
		dst[i] = pgm_read_byte_far(src);
		src ++;
	}
}

uint8_t strLen_F(uint_farptr_t src){
	uint16_t i = 0;
	for(i = 0 ; i < 255; i ++){
		if(pgm_read_byte_far(src)==0){
			i++;
			return i;
		}
		src ++;
	}
	return 0;
}

uint8_t shortLen_F(uint_farptr_t src, uint16_t endShort){
	uint8_t i = 0;
	for(i = 0 ; i < 127; i ++){
		if(pgm_read_word_far(src)==endShort){
			return i;
		}
		src+=2;
	}
	return 0;
}

uint8_t shortcmp_F(uint_farptr_t src,uint16_t targetShort, uint16_t endShort){
	uint8_t i = 0;
	for(i = 0 ; i < 127; i ++){
		uint16_t word = pgm_read_word_far(src);
		if(word ==targetShort){
			return 1;
		} else if (word == endShort){
			return 0;
		} else{
			src+=2;
		}
	}
	return 0;
}

uint8_t strcpy_F(char* dst, uint_farptr_t src, uint8_t maxLen){
	uint8_t len = strLen_F(src);
	if(len > maxLen){
		return 0;
	}
	memcpy_F(dst,src,len);
	return len;
}


uint8_t strcmp_F(char* val1, uint_farptr_t val2){
	uint8_t i = 0;
	for(i = 0 ; i < 255; i ++){
		if(pgm_read_byte_far(val2)!=*val1){
			i++;
			return i;
		}
		if(*val1 == 0){
			return 0;
		}
		val1++;
		val2++;
	}
	return 0;
}

uint_farptr_t getFP(const void* ptrVal,const void* cmp){
	uint_farptr_t ptr = (uint16_t)ptrVal;
	if(elfloader_in_memory((void*)cmp)){
		ptr += 0x00010000;
	}
	return ptr;
}

#include <stdarg.h>
int _lpt(const char *format, ...)
{
	uint_farptr_t strLoc = (uint16_t)format;
	strLoc+= 0x00010000;
	uint16_t size = strLen_F(strLoc);
	char buffer[size];
	memcpy_F(buffer,strLoc,size);

	va_list args;
	va_start(args, format);
	int r = vprintf(buffer, args);
	va_end(args);
	return r;
}


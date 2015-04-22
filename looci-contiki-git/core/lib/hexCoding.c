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

#include <avr/pgmspace.h>

PROGMEM static const unsigned char hextable_p[16] = "0123456789abcdef";
#define MASK1 0xF0;
#define MASK2 0x0F;

uint16_t
xEnc(const unsigned char *in,unsigned char *out, uint16_t len){
	unsigned char hextable[16];
	memcpy_P(hextable,hextable_p,16);
	uint16_t i = len;
	while(i > 0){
		i --;
		*out = ((uint8_t)*in) & MASK1;
		*out = *out >> 4;
		*out = hextable[*out];
		out += 1;
		*out = ((uint8_t)*in) & MASK2;
		*out = hextable[*out];
		out += 1;
		in += 1;
	}
	return len * 2;
}

static uint8_t getHexIndex(unsigned char* hextable, char input){
	uint8_t i = 0;
	for(i = 0 ; i < 16; i ++){
		if(input == hextable[i]){
			return i;
		}
	}
	return 0;
}


uint16_t
xDec(const unsigned char *in, unsigned char *out, uint16_t len){
	unsigned char hextable[16];
	memcpy_P(hextable,hextable_p,16);
	uint16_t i = 0;
	len = len / 2;
	for(i = 0 ; i < len ; i++){
		out[i] = (getHexIndex(hextable,in[2*i])<<4) | (getHexIndex(hextable,in[2*i+1]));
	}
	return len;
}

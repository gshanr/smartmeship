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
 * keyList.c
 *
 *  Created on: Jul 8, 2011
 *      Author: root
 */

#include "keyList.h"
#include <stdlib.h>

void klIni(keyList list){
	uint8_t i = 0;
	for(i = 0 ; i<list.size; i++){
		list.list[i].key=0;
		list.list[i].el=0;
	}
}

uint8_t klAdd(keyList list, uint8_t key, void* el){
	uint8_t i = 0;
	for(i=0; i < list.size; i++){
		if(list.list[i].el == 0){
			list.list[i].key = key;
			list.list[i].el = el;
			return i;
		}
	}
	return 255;
}

void klRem(keyList list, uint8_t key){
	uint8_t i = 0;
	for(i=0; i < list.size; i++){
		if(list.list[i].key == key){
			list.list[i].el = 0;
			list.list[i].key = 0;
		}
	}
}

void* klGet(keyList list, uint8_t key){
	uint8_t i= 0;
	for(i=0; i < list.size; i++){
		if(list.list[i].key == key){
			return list.list[i].el;
		}
	}
	return NULL;
}

void klGetAll(keyList list, uint8_t* buffer, uint8_t* length){
	uint8_t i = 0;
	uint8_t number = 0;
	for(i = 0 ; i < list.size; i ++){
		if(list.list[i].el != 0){
			buffer[number] = list.list[i].key;
			number++;
		}
	}
	*length = number;
}

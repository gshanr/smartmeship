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
 * comp_services.c
 *
 *  Created on: Dec 6, 2012
 *      Author: root
 */


#include "random.h"
#include "mmem.h"
#include "interceptor.h"

unsigned int _ma(struct mmem *m, unsigned int size){
	uint8_t request = INTERCEPT_MMEM_ALLOC;
	if(lc_intercept(INTERCEPT_MEMORY_ALLOC,&request)){
		return mmem_alloc(m,size);
	} else{
		return 0;
	}
}

void _mf(struct mmem *m){
	uint8_t request = INTERCEPT_MMEM_FREE;
	lc_intercept(INTERCEPT_MEMORY_ALLOC,&request);
	mmem_free(m);
}

uint16_t rng(uint16_t max){
	return random_rand() % max;
}


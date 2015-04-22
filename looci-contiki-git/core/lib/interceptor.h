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
 * interceptor.h
 *
 *  Created on: Dec 13, 2012
 *      Author: root
 */

#ifndef INTERCEPTOR_H_
#define INTERCEPTOR_H_


#include <stdint.h>
#include <stdbool.h>

#define INTERCEPT_FROM_NETWORK 0
#define INTERCEPT_TO_NETWORK 1
#define INTERCEPT_FROM_COMPONENT 2
#define INTERCEPT_TO_COMPONENT 3
#define INTERCEPT_LIFECYCLE_CHANGE 4
#define INTERCEPT_MEMORY_ALLOC 5


typedef struct{
	uint8_t command;
	struct looci_codebase* codebase;
	struct looci_comp* component;
}lifecycle_intercept_t;

#define INTERCEPT_LIFECYCLE_DEPLOY 0
#define INTERCEPT_LIFECYCLE_REMOVE 1
#define INTERCEPT_LIFECYCLE_INSTANTIATE 2
#define INTERCEPT_LIFECYCLE_DESTROY 3
#define INTERCEPT_LIFECYCLE_ACTIVATE 4
#define INTERCEPT_LIFECYCLE_DEACTIVATE 5

#define INTERCEPT_MMEM_ALLOC 1
#define INTERCEPT_MMEM_FREE 2

typedef uint8_t (*interceptFunc)(void* data);

struct interception_module {
	struct interception_module* next;
	uint8_t interceptionPoint;
	uint8_t prio;
	uint8_t (*intercept)(void* data);
};
typedef struct interception_module interception_module_t;

void lc_int_init();
uint8_t lc_intercept(uint8_t interceptionPoint, void* data);

void lc_int_addModule(interception_module_t* module);
void lc_int_removeModule(interception_module_t* module);

#endif /* INTERCEPTOR_H_ */

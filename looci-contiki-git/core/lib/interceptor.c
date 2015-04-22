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
 * interceptor.c
 *
 *  Created on: Dec 13, 2012
 *      Author: root
 */


#include "interceptor.h"
#include "list.h"
#include "string.h"

#ifdef LOOCI_INTERCEPT_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif

LIST(interceptors);

void lc_int_init(){
	list_init(interceptors);
}

uint8_t lc_intercept(uint8_t interceptionPoint, void* data){
	PRINT_LN("[LI] INTERCEPTING AT %u", interceptionPoint);
	interception_module_t* module = (interception_module_t*)list_head(interceptors);
	while(module != NULL){
		if(module->interceptionPoint == interceptionPoint){
			PRINT_LN("[LI] INTERCEPTING AT PRIO %u", module->prio);
			if(module->intercept(data) == 0){
				return 0;
			}
		}
		module = (interception_module_t*)list_item_next(module);
	}
	return 1;
}

void lc_int_notify(uint8_t interceptionPoint, void* data){
	PRINT_LN("[LI] int %u", interceptionPoint);
	interception_module_t* module = (interception_module_t*)list_head(interceptors);
	while(module != NULL){
		if(module->interceptionPoint == interceptionPoint){
			PRINT_LN("[LI] int %u prio %u", module->prio);
			module->intercept(data);
		}
		module = (interception_module_t*)list_item_next(module);
	}
}

void lc_int_addModule(interception_module_t* module){
	PRINT_LN("[LI] add module %p at %u prio %u",module,module->interceptionPoint,module->prio);
	interception_module_t* prevMod = NULL;
	interception_module_t* nextMod = (interception_module_t*)list_head(interceptors);
	while(nextMod != NULL && nextMod->prio < module->prio){
		prevMod = nextMod;
		nextMod = (interception_module_t*)list_item_next(prevMod);
	}
	list_insert(interceptors,prevMod,module);
}

void lc_int_removeModule(interception_module_t* module){
	PRINT_LN("[LI] remove module %p at %u",module,module->interceptionPoint);
	list_remove(interceptors,module);
}

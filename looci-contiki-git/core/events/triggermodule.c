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
 * triggermodule.c
 *
 *  Created on: Oct 29, 2012
 *      Author: root
 */

#include "interceptor.h"
#include "event.h"
#include "eventBus.h"
#include "events_private.h"
#include "triggermodule.h"
#include <stdbool.h>


#ifdef LOOCI_EVENTS_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif

static uint8_t interceptFromComponent(looci_event_t* ev){
	looci_event_t* trigger = lc_em_getCurrentEvent();
	PRINT_LN("[TM]ev from component");
	if(trigger!=NULL){
		if(EV_NEEDS_REPLY(trigger)){
			PRINT_LN("[TM] trig event needs directed");
			ev->dst_cid = trigger->source_cid;
			ev->dst_node = trigger->source_node;
			char header[3];
			header[0] = trigger->source_cid;
			header[1] = (1<<EV_IS_REPLY_MASK);
			header[2] = ev_getHeaderByte(trigger,HEADER_CMD,2);
			ev_add_header(ev,HEADER_CMD,header,3);
			return 1;
		}
	} else if(EV_IS_DIRECTED(ev)){
		PRINT_LN("[TM] dir event from component");
		ev->dst_cid = EV_GET_DST_CMP(ev);
	}
	return 1;
}

static uint8_t interceptFromNetwork(looci_event_t* event){
	if(EV_IS_DIRECTED(event)){
		event->dst_cid = EV_GET_DST_CMP(event);
		PRINT_LN("[TM] dir nw ev to cmp %u",event->dst_cid);
	} else{
		PRINT_LN("[TM] undir nw ev");
	}
	return 1;
}

static interception_module_t fromNet= {
		NULL,
		INTERCEPT_FROM_NETWORK,
		100,
		(interceptFunc)interceptFromNetwork
};
static interception_module_t fromComp = {
		NULL,
		INTERCEPT_FROM_COMPONENT,
		100,
		(interceptFunc)interceptFromComponent
};

void lc_init_triggermodule(){
	lc_int_addModule(&fromNet);
	lc_int_addModule(&fromComp);
}


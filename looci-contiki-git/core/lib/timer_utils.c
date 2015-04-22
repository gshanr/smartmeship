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
 * timer_utils.c
 *
 *  Created on: Sep 24, 2012
 *      Author: root
 */

#include "etimer.h"
#include "timer_utils.h"
#include <stdlib.h>



uint8_t _tf(uint8_t mode, struct etimer *et, clock_time_t interval){
	switch(mode){
	case ET_SET:
		etimer_set(et,interval);
		break;
	case ET_RESTART:
		etimer_restart(et);
		break;
	case ET_RESET:
		etimer_reset(et);
		break;
	case ET_EXPIRED:
		return (uint8_t)etimer_expired(et);
	case ET_ADJUST:
		etimer_adjust(et,(int)interval);
		break;
	case ET_STOP:
		etimer_stop(et);
		break;
	}
	return 0;
}

struct etimer* getET(){
	return malloc(sizeof(struct etimer));
}

void relET(struct etimer* timer){
	free(timer);
}

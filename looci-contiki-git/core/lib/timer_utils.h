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
 * timer_utils.h
 *
 *  Created on: Sep 24, 2012
 *      Author: root
 */

#ifndef TIMER_UTILS_H_
#define TIMER_UTILS_H_

#include "etimer.h"

#define MAX_NR_TIMERS 20

typedef struct{
	struct etimer timer;
	uint8_t taken;
}timer_subs;

#define ET_SET 0
#define ET_RESTART 1
#define ET_RESET 2
#define ET_EXPIRED 3
#define ET_ADJUST 4
#define ET_STOP 5

#define ETIMER_SET(et,interval) _tf(ET_SET,et,interval)
#define ETIMER_RESET(et) _tf(ET_RESET,et,0)
#define ETIMER_RESTART(et) _tf(ET_RESTART,et,0)
#define ETIMER_ADJUST(et,timeDiff) _tf(ET_ADJUST,et,timeDiff)
#define ETIMER_EXPIRED(et) _tf(ET_EXPIRED,et,0)
#define ETIMER_STOP(et) _tf(ET_STOP,et,0)
#define ETIMER_INIT //NOOP

#define GET_ETIMER(timer) timer = getET()
#define RELEASE_ETIMER(timer) relET(timer); timer = NULL

//Timer functions, to be used as optimised
uint8_t _tf(uint8_t mode, struct etimer *et, clock_time_t interval);

struct etimer* getET();

void relET(struct etimer* timer);

#endif /* TIMER_UTILS_H_ */

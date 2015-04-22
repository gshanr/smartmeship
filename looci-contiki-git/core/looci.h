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
/**
 * @mainpage
 * @version 0.1
 * @author Wouter Horré <wouter.horre@cs.kuleuven.be>
 *
 * This is the documention for the LooCI implementation on the Contiki operating system.
 *
 * More information on LooCI (Loosely coupled Component Infrastructure) can be found in our publications.
 */
#ifndef __LOOCI_H__
#define __LOOCI_H__

#include "contiki.h"
#include "component.h"
#include "utils.h"
#include "event.h"
#include "timer_utils.h"
#include "error_codes.h"
#include "looci-types.h"

PROCESS_NAME(looci);

void looci_init();


#endif /* __LOOCI_H__ */

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
#ifndef __PROJECT_CONF_H__
#define __PROJECT_CONF_H__

#include "core/reconfiguration/reconfigurationEngine_pub.h"

#define COMP_NAME(arg) arg##__C

#ifndef MY_NR_COMPS
#define NR_START_COMPONENTS 1
#define START_COMPONENTS {COMP_NAME(reconfig)}
#else
#include "image-conf.h"
#define NR_START_COMPONENTS MY_NR_COMPS
#define START_COMPONENTS {MY_COMPS}
#ifdef WITH_IMAGE_INIT
void doImageInit();
#endif
#endif


#ifndef MY_NODE_MAC

#ifndef MY_NODE_ID
#define EUI64_ADDRESS {0x02, 0x11, 0x22, 0xff, 0xfe, 0x33, 0x44, 0x55}
#else
#define EUI64_ADDRESS {0x02, 0x11, 0x22, 0xff, 0xfe, 0x33, 0x44, MY_NODE_ID}

#endif
#else
#define EUI64_ADDRESS {MY_NODE_MAC}
#endif


#endif // __PROJECT_CONF_H__

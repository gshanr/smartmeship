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
 * component_pub.h
 */

#ifndef COMPONENT_PUB_H_
#define COMPONENT_PUB_H_

#include "utils.h"

#ifndef LOOCI_COMPONENT_NAME
#define LOOCI_NAMED(name) JOIN(LOOCI_COMPONENT_NAME,name)
#else
#define LOOCI_NAMED(name) JOIN(LOOCI_COMPONENT_NAME,name)
#endif


#ifndef BUILD_COMPONENT
#define DECLARE_LOOCI_COMPONENT(name)\
		extern struct component* name##__C;\
		extern struct looci_codebase name##_Lc

#else
#define DECLARE_LOOCI_COMPONENT(name) //NOOP

#endif

#endif /* COMPONENT_PUB_H_ */

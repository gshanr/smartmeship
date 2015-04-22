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
 * comp_services.h
 *
 *  Created on: Dec 6, 2012
 *      Author: root
 */

#ifndef COMP_SERVICES_H_
#define COMP_SERVICES_H_

#include "event.h"
#include "component_type.h"
#include "mmem.h"
#include <stdint.h>

/**
 * @internal
 * Add or remove a component to the list of components, depending on mode
 *
 * @param component The component structure to add
 * eventtype.h
 * @return The component id
 */
uint8_t _r(uint8_t mode, struct looci_codebase* component);

uint8_t _p(looci_eventtype_t type, void* payload, uint8_t len);

unsigned int _ma(struct mmem *m, unsigned int size);

void _mf(struct mmem *m);

#define MEMORY_ALLOC _ma

#define MEMORY_FREE  _mf

#define PUBLISH_EVENT(type,payload,size) _p(type,payload,size)

uint16_t rng(uint16_t max);

#endif /* COMP_SERVICES_H_ */

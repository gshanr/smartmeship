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
 * looci-defines.h
 *
 *  Created on: Nov 23, 2011
 *      Author: root
 */

#ifndef LOOCI_TYPES_H_
#define LOOCI_TYPES_H_

#include <stdint.h>

/**
 * @brief The LooCI event type
 */
typedef uint16_t looci_eventtype_t;

typedef uint16_t looci_prop_t;

typedef uint8_t cmp_id_t;

typedef uint8_t inst_id_t;

#define DATATYPE_UNDEFINED 0
#define DATATYPE_BYTE 1
#define DATATYPE_SHORT 2
#define DATATYPE_INT 3
#define DATATYPE_STRING 4
#define DATATYPE_BOOL 5
#define DATATYPE_BYTE_ARRAY 6

#endif /* LOOCI_DEFINES_H_ */

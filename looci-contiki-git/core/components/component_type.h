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
 * Declaration of structs and constants related to components
 */

#ifndef COMPONENT_TYPE_H_
#define COMPONENT_TYPE_H_

#include "looci-types.h"
#include "loociConstants.h"
#include "mmem.h"
#include "process.h"
#include <stdint.h>
#include <stdbool.h>
#include <avr/pgmspace.h>

/**
 * The component id for a component that hasn't been initialized.
 */
#define COMPONENT_ID_NONE 0
#define COMPONENT_ID_ANY 0

/**
 * The component states
 */
#define COMPONENT_STATE_NONE 2
#define COMPONENT_STATE_ACTIVE 1
#define COMPONENT_STATE_DEACTIVATED 0

#define COMP_OP_INIT 0
#define COMP_OP_END 1

/**
 * A component.
 */
struct component {
	uint8_t (*doReg)(uint8_t mode);
};

#define PROP_BUF_SIZE 40
#define PROPS_BUF_SIZE 10

typedef struct {
	looci_prop_t propertyId;
	uint8_t* buffer;
	uint8_t elements;
	uint8_t size;
}looci_prop_buffer_t;




#define LC_MASK_AUTO_START 1
#define LC_MASK_MGT 2
#define LC_MASK_NO_STOP 4
#define LC_MASK_NO_DESTROY 8
#define LC_MASK_INIT_DESTROY 16
#define LC_MASK_PROP 32

typedef struct{
	looci_prop_t propertyId;
	uint8_t dataType;
	uint8_t offset;
	uint8_t size;
	const char* name;
}looci_property_t;

struct looci_codebase{
	struct looci_codebase* next;
	uint8_t id;
	struct component* src_cmp;
	uint8_t flags;
	uint16_t sizeOfState;
	void* initState;
	PT_THREAD((* thread)(struct pt *, process_event_t, process_data_t));
	void* interfaces;
	void* receptacles;
	void* name;
	uint8_t nrProperties;
	void* properties;
};

typedef struct{
	looci_property_t* properties;
	uint8_t size;
}propertyListInfo;

struct looci_comp{
	struct process process;
	struct looci_codebase* codebase;
	uint8_t id;
	uint8_t state;
	void* data;
};

struct contiki_call{
	uint8_t event;
	void* data;
};

typedef uint8_t (*comp_func_ft)(void* el, void* data);

struct eventList{
	uint16_t event;
	comp_func_ft f;
};

#endif /* COMPONENT_TYPE_H_ */

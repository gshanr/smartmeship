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
 * Copyright (c) 2010, Katholieke Universiteit Leuven
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of the Katholieke Universiteit Leuven nor the names of
 *       its contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * @addtogroup reconfiguration
 * @{
 */
/**
 * @defgroup introspection Introspection
 * @{
 */
/**
 * @file 
 * Header file for the local introspection functionality
 * @author 
 * Wouter Horré <wouter.horre@cs.kuleuven.be>
 */
#ifndef __LOOCI_INTROSPECTION_LOCAL_H__
#define __LOOCI_INTROSPECTION_LOCAL_H__

#include <stdbool.h>
#include "net/peers.h"
#include "vector.h"
#include "component_type.h"
#include "events/eventBus.h"


#define LC_IS_GET_CBIDS 0
typedef struct{
	uint8_t* buffer;
	uint8_t elements;
	uint8_t size;
}lc_is_get_cbids_t;

#define LC_IS_GET_CB_TYPE 1
typedef struct{
	uint8_t id;
	char* type;
	uint8_t size;
	uint8_t maxsize;
}lc_is_get_cb_type_t;

#define LC_IS_GET_CBIDS_OF_TYPE 2
typedef struct{
	char * ctype;
	uint8_t * buffer;
	uint8_t elements;
	uint8_t size;
}lc_is_get_cbids_of_type_t;

#define LC_IS_GET_CID_BY_CBID 3
typedef struct{
	int8_t cbid;
	uint8_t* buffer;
	uint8_t size;
	uint8_t maxSize;
}lc_is_get_cid_by_cbid_t;

#define LC_IS_GET_CBID_OF_CID 4
typedef struct{
	uint8_t cid;
	uint8_t cbid;
}lc_is_get_cbid_of_cid_t;

#define LC_IS_GET_C_TYPE 5
typedef struct{
	uint8_t cid;
	char * type;
	uint8_t size;
	uint8_t maxsize;
}lc_is_get_c_type_t;

#define LC_IS_GET_C_IDS 6
typedef struct{
	uint8_t* buffer;
	uint8_t elements;
	uint8_t maxSize;
}lc_is_get_c_ids_t;

#define LC_IS_GET_STATE 7
typedef struct{
	uint8_t cid;
	uint8_t state;
}lc_is_get_state_t;

#define LC_IS_GET_PROPERTIES 8
typedef struct{
	uint8_t cid;
	looci_prop_t* buffer;
	uint8_t elements;
	uint8_t size;
}lc_is_get_properties_t;

#define LC_IS_GET_PROPERTY 9
typedef struct{
	uint8_t cid;
	looci_prop_buffer_t* buffer;
}lc_is_get_property_t;

typedef struct{
	uint8_t cid;
	looci_eventtype_t * buffer;
	uint8_t elements;
	uint8_t size;
}lc_is_eventtype_buffer_t;
#define LC_IS_GET_RECEPTACLES 10
#define LC_IS_GET_INTERFACES 11

#define LC_IS_GET_LCL_WIRES 12
typedef struct{
	looci_eventtype_t eventtype;
	uint8_t src_cid;
	uint8_t dst_cid;
	subs_local* buffer;
	uint8_t index;
	uint8_t size;
}lc_is_get_lcl_wires_t;
#define LC_IS_GET_OUT_WIRES 13
typedef struct{
	looci_eventtype_t eventtype;
	uint8_t src_cid;
	peer_id_t tgt_node;
	subs_rem_to* buffer;
	uint8_t index;
	uint8_t size;
}lc_is_get_out_wires_t;
#define LC_IS_GET_INC_WIRES 14
typedef struct{
	looci_eventtype_t eventtype;
	uint8_t src_cid;
	peer_id_t src_node;
	uint8_t dst_cid;
	subs_rem_from* buffer;
	uint8_t index;
	uint8_t size;
}lc_is_get_inc_wires_t;
#define LC_IS_GET_ALL_COMPS 15

typedef struct{
	uint8_t cid;
	looci_prop_t propertyId;
	uint8_t* buffer;
	uint8_t elements;
	uint8_t size;
	uint8_t propertyType;
}lc_is_get_property_info_t;
#define LC_IS_GET_PROP_INFO 16

typedef struct{
	struct vector* location;
}lc_is_get_all_comps_t;

typedef int8_t (*instrospection_manager_func)(uint16_t funcId, void* data);

int8_t lc_is_func(uint16_t funcId, void* data);

#endif
/** @} */
/** @} */


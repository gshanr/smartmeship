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
 * Copyright (c) 2009, Katholieke Universiteit Leuven
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
 * @defgroup runtime_control Runtime control
 * @{
 */
/**
 * @file 
 * Header file for the local runtime control functionality
 * @author 
 * Wouter Horré <wouter.horre@cs.kuleuven.be>
 */
#ifndef __LOOCI_RUNTIME_CONTROL_LOCAL_H__
#define __LOOCI_RUNTIME_CONTROL_LOCAL_H__

#include <stdbool.h>
#include "net/peers.h"
#include "eventBus.h"
#include "component_type.h"


#define LC_RC_INSTALL 0
#define LC_RC_REMOVE 1
typedef struct{
	uint8_t cbid;
}lc_rc_codebase_t;


#define LC_RC_ACTIVATE 2
#define LC_RC_DEACTIVATE 3
typedef struct{
	uint8_t cid;
}lc_rc_component_t;

#define LC_RC_INSTANTIATE 4
typedef struct{
	uint8_t cbid;
	uint8_t cid;
}lc_rc_instantiate_t;

#define LC_RC_DESTROY 5
#define LC_RC_RESET_WIRES 6


#define LC_RC_WIRE_LOCAL 7
#define LC_RC_WIRE_REM_FROM 8
#define LC_RC_WIRE_REM_TO 9
#define LC_RC_UNWIRE_LOCAL 10
#define LC_RC_UNWIRE_REM_FROM 11
#define LC_RC_UNWIRE_REM_TO 12

#define LC_RC_SET_PROPERTY 13
typedef struct{
	uint8_t cid;
	looci_prop_buffer_t* buffer;
}lc_rc_set_property_t;

typedef int8_t (*reconfig_manager_func)(uint16_t funcId, void* data);

int8_t lc_rc_func(uint16_t funcId, void* data);


#endif // __LOOCI_RUNTIME_CONTROL_LOCAL_H__
/** @} */
/** @} */

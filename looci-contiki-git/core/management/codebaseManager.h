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
 * @addtogroup components
 * @{
 */
/**
 * @internal
 * @file 
 * Header file for the internal API of the Codebase Manager
 */
#ifndef __CODEBASE_MANAGER_H__
#define __CODEBASE_MANAGER_H__

#include "contiki-net.h"
#include "component.h"
#include "component_type.h"

#include "runtime_control_local.h"
#include "introspection_local.h"
#include <stdint.h>


#define getComponentValues(varName,cb_id)\
	struct looci_comp_const varName;\
	lc_getValues(varName,cb_id)

void looci_cbMan_init();


#define CREG_MODE_REG 0
#define CREG_MODE_UREG 1

/**
 * @internal
 * Add or remove a component to the list of components, depending on mode
 *
 * @param component The component structure to add
 *
 * @return The component id
 */


int8_t looci_cbMan_deploy_component(lc_rc_codebase_t* data);

int8_t looci_cbMan_undeploy_component(lc_rc_codebase_t* data);

int8_t looci_cbMan_instantiate_component(lc_rc_instantiate_t* data);


struct looci_codebase* looci_cbMan_get_cb_by_type(char * type);
struct looci_codebase* looci_cbMan_get_codebase(uint8_t cb_id);

int8_t looci_cbMan_get_cb_ids(lc_is_get_cbids_t* data) ;

int8_t looci_cbMan_getcodebasetype(lc_is_get_cb_type_t* data);

int8_t looci_cbMan_getcodebaseids_by_type(lc_is_get_cbids_of_type_t* data);

#define GET_NAME(codebase, buffer, size) strcpy_F(buffer,codebase->name,size);
#define GET_INTERFACES(codebase, buffer, size) shortcpy_F(buffer,codebase->interfaces,size,EV_END_EVENT);
#define GET_RECEPTACLES(codebase, buffer, size) shortcpy_F(buffer,codebase->receptacles,size,EV_END_EVENT);

#endif // __LOOCI_COMPONENTS_PRIVATE_H__

/** @} */

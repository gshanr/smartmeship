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
 * @addtogroup introspection
 * @{
 */
/**
 * @file 
 * Implementation file for the local introspection functionality
 * @author
 * Wouter Horré <wouter.horre@cs.kuleuven.be>
 */

#include "introspection_local.h"
#include "management/codebaseManager.h"
#include "management/componentManager.h"
#include "events/events_private.h"


typedef int8_t (*introspectionFunc)(void* data);
typedef struct {
	introspectionFunc f;
} lc_is_ft;

PROGMEM const lc_is_ft lc_is_manager[17] = {
		{(introspectionFunc)looci_cbMan_get_cb_ids},
		{(introspectionFunc)looci_cbMan_getcodebasetype},
		{(introspectionFunc)looci_cbMan_getcodebaseids_by_type},
		{(introspectionFunc)looci_cmpMan_getcompids_by_cb_id},
		{(introspectionFunc)looci_cmpMan_get_cb_id_of_comp_id},
		{(introspectionFunc)looci_cmpMan_get_comp_type},
		{(introspectionFunc)looci_cmpMan_get_comp_ids},
		{(introspectionFunc)looci_cmpMan_getstate},
		{(introspectionFunc)looci_cmpMan_getProperties},
		{(introspectionFunc)looci_cmpMan_getProperty},
		{(introspectionFunc)looci_cmpMan_getReceptacles},
		{(introspectionFunc)looci_cmpMan_getInterfaces},
		{(introspectionFunc)events_getlocalwires},
		{(introspectionFunc)events_getremotewires_to},
		{(introspectionFunc)events_getremotewires_from},
		{(introspectionFunc)looci_cmpMan_getComponents},
		{(introspectionFunc)looci_cmpMan_getPropertyInfo}
};

int8_t lc_is_func(uint16_t funcId, void* data){
	lc_is_ft cmd;
	memcpy_P(&cmd,&lc_is_manager[funcId],sizeof(lc_is_ft));
	return cmd.f(data);
}



/** @} */
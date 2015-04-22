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
 * @addtogroup runtime_control
 * @{
 */
/**
 * @file 
 * Implementation of the local runtime control functionality
 * @author 
 * Wouter Horré <wouter.horre@cs.kuleuven.be>
 */

#include <string.h>

#include "runtime_control_local.h"
#include "management/codebaseManager.h"
#include "management/componentManager.h"
#include "events/events_private.h"
#include <avr/pgmspace.h>

#ifdef LOOCI_MGT_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif

#define WIRE_ANY 0
#define WIRE_ALL 1 

typedef int8_t (*reconfigFunc)(void* data);
typedef struct {
	reconfigFunc f;
} lc_rc_ft;

PROGMEM const lc_rc_ft lc_rc_manager[14]= {
		{(reconfigFunc)looci_cbMan_deploy_component},
		{(reconfigFunc)looci_cbMan_undeploy_component},
		{(reconfigFunc)looci_cmpMan_component_start},
		{(reconfigFunc)looci_cmpMan_component_stop},
		{(reconfigFunc)looci_cbMan_instantiate_component},
		{(reconfigFunc)looci_cmpMan_destroyComponent},
		{(reconfigFunc)events_unwire_component},
		{(reconfigFunc)events_add_local_subscription},
		{(reconfigFunc)events_add_remote_subscription_from},
		{(reconfigFunc)events_add_remote_subscription_to},
		{(reconfigFunc)events_remove_local_subscription},
		{(reconfigFunc)events_remove_remote_subscription_from},
		{(reconfigFunc)events_remove_remote_subscription_to},
		{(reconfigFunc)looci_cmpMan_setProperty}
};

int8_t lc_rc_func(uint16_t funcId, void* data){
	lc_rc_ft cmd;
	memcpy_P(&cmd,&lc_rc_manager[funcId],sizeof(lc_rc_ft));
	return cmd.f(data);
}

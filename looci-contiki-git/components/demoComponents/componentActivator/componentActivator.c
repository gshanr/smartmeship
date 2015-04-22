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
#include "contiki.h"
#include "looci.h"
#include "componentActivator.h"
#include "runtime_control_local.h"
#include <avr/pgmspace.h>

#ifdef LOOCI_COMPONENT_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif

#define APPLICATION_DO_YN_EVENT 82
#define PROP_SET_TARGET_INSTANCE_ID 105

struct state{
	  uint8_t activation_count;
	  uint8_t activation_instance;
};



#define LOOCI_COMPONENT_NAME activator


#define LOOCI_NR_PROPERTIES 1
LOOCI_PROPERTIES(
		{PROP_SET_TARGET_INSTANCE_ID,DATATYPE_BYTE,offsetof(struct state,activation_instance),1,NULL}
);

COMPONENT_NO_INTERFACES();
//COMPONENT_INTERFACES(APPLICATION_DO_YN_EVENT);
//COMPONENT_NO_RECEPTACLES();
COMPONENT_RECEPTACLES(APPLICATION_DO_YN_EVENT);
LOOCI_COMPONENT("activ_comp",struct state);

static uint8_t onInit(struct state* compState, void* data){
	compState->activation_count = 0;
	compState->activation_instance = 0;
	return 1;
}

static uint8_t onEvent(struct state* compState, core_looci_event_t* event){
	if(event->type == APPLICATION_DO_YN_EVENT && compState->activation_instance!=0){

				if(event->payload[0]){
					//received that i have to do something with arguement true
					if(compState->activation_count == 0){
						PRINT_LN("[CA] activating instance %u",compState->activation_instance);

						lc_rc_func(LC_RC_ACTIVATE,&compState->activation_instance);

					}
					PRINT_LN("[CA] increment act count %u ", compState->activation_count);
					compState->activation_count += 1;
				} else{
					//received a do event with arguement false
					if(compState->activation_count > 0){
						compState->activation_count -= 1;
						if(compState->activation_count == 0){

							PRINT_LN("[CA] deactivating instance %u ", compState->activation_instance);
							lc_rc_func(LC_RC_DEACTIVATE,&compState->activation_instance);
						}
					}

				}


			}
	return 1;
}

COMP_FUNCS_INIT //THIS LINE MUST BE PRESENT
COMP_FUNC_INIT(onInit)
COMP_FUNC_EVENT(onEvent)
//COMP_FUNC_EVENT(event)
COMP_FUNCS_END(NULL)//THIS LINE MUST BE PRESENT


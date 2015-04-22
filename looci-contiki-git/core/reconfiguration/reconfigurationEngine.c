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
 * Copyright (c) 2011, Katholieke Universiteit Leuven
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
/*
 * Reconfiguration Engine Component which allows for events to be sent to the component
 * to reconfigure it
 */

#include "component.h"
#include "eventBus.h"
#include "loociConstants.h"
#include "event-types.h"
#include "componentManager.h"
#include "deployment/codebaseDeployment.h"
#include "net/peers.h"
#include "utils.h"
#include "reconfigurationEngine.h"
#include "component_type.h"
#include "uip.h"
#include "error_codes.h"
#include "looci_platform.h"
#include <string.h>
#include <stdio.h>
#include <stdarg.h>
#include <inttypes.h>
#include <stdbool.h>
#include <string.h>
#include <avr/io.h>
#include <avr/pgmspace.h>

struct state{

};


static instrospection_manager_func looci_intro_P;
static reconfig_manager_func looci_reconf_P;

#define LOOCI_COMPONENT_NAME reconfig

COMPONENT_INTERFACES(EV_ANY_EVENT);
COMPONENT_RECEPTACLES(EV_ANY_EVENT);

#define LOOCI_NR_PROPERTIES 0
LOOCI_PROPERTIES();
LOOCI_COMP_PRIV("lc_reconfig", struct state,15,NULL);

#ifdef LOOCI_MGT_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif

#define UIP_IP_BUF   ((struct uip_udpip_hdr *)&uip_buf[UIP_LLH_LEN])

//1
static uint8_t cmd_install(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	// send back result
	output[0] = (uint8_t) false;
	return 2;
 }
//2
static uint8_t cmd_remove(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	lc_rc_codebase_t data = {input[0]};
	output[0] = looci_reconf_P(LC_RC_REMOVE,&data);
	return 2;
 }
//3
static uint8_t cmd_instantiate(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	lc_rc_instantiate_t data = {input[0],0};
	output[0] = looci_reconf_P(LC_RC_INSTANTIATE,&data);
	output[2] = data.cid;
	return 3;
}
//4
static uint8_t cmd_destroy(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	lc_rc_component_t data = {input[0]};
	output[0] = looci_reconf_P(LC_RC_DESTROY,&data);
	return 2;
}
//5
static uint8_t cmd_start(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	lc_rc_component_t data = {input[0]};
	output[0] = looci_reconf_P(LC_RC_ACTIVATE,&data);
	return 2;
}
//6
static uint8_t cmd_stop(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	lc_rc_component_t data = {input[0]};
	output[0] = looci_reconf_P(LC_RC_DEACTIVATE,&data);
	return 2;
}
//7
static uint8_t cmd_wire_local(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	subs_local sub = {HTONS(*((looci_eventtype_t*)input)),input[2],input[3]};
	output[0] = looci_reconf_P(LC_RC_WIRE_LOCAL,&sub);
	return 5;
}
//8
static uint8_t cmd_wire_remote_to(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	subs_rem_to sub = {
			HTONS(*((looci_eventtype_t*)input)),
			input[2],
			peer_get_id_or_add((peer_addr_t*)(input + 3))
	};
	output[0] = looci_reconf_P(LC_RC_WIRE_REM_TO,&sub);
	return 20;
}
//9
static uint8_t cmd_wire_remote_from(unsigned char * input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	subs_rem_from sub = {
			HTONS(*((looci_eventtype_t*)input)),
			input[18],
			peer_get_id_or_add((peer_addr_t *)(input+ 2)),
			input[19]
	};

	output[0] = looci_reconf_P(LC_RC_WIRE_REM_FROM,&sub);
	return 21;
}
//10
static uint8_t cmd_reset_wirings(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	lc_rc_component_t data = {input[0]};
   output[0] = looci_reconf_P(LC_RC_RESET_WIRES,&data);
	return 2;
}
//11
static uint8_t cmd_unwire_local(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
		subs_local sub = {HTONS(*((looci_eventtype_t*)input)),input[2],input[3]};
		output[0] = looci_reconf_P(LC_RC_UNWIRE_LOCAL,&sub);
		return 5;
}
//12
static uint8_t cmd_unwire_remote_to(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	subs_rem_to sub = {
		HTONS(*((looci_eventtype_t*)input)),
		input[2],
		peer_get_id_or_add((peer_addr_t*)(input + 3))
	};
	output[0] = looci_reconf_P(LC_RC_UNWIRE_REM_TO,&sub);
	return 20;
}
//13
static uint8_t cmd_unwire_remote_from(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	subs_rem_from sub = {
		HTONS(*((looci_eventtype_t*)input)),
		input[18],
		peer_get_id_or_add((peer_addr_t *)(input+ 2)),
		input[19]
	};
	output[0] = looci_reconf_P(LC_RC_UNWIRE_REM_FROM,&sub);
	return 21;
}
//14
static uint8_t cmd_set_property(unsigned char * input, uint16_t iplen, unsigned char * output, uint16_t oplen){
	memcpy(output+5,input+4,input[3]);
	looci_prop_buffer_t buffer;
	buffer.propertyId = HTONS(*((looci_prop_t*)(input+1)));
	buffer.buffer = input + 4;
	buffer.size = input[3];
	buffer.elements = input[3];
	lc_rc_set_property_t data = {input[0],&buffer};
	output[0] = looci_reconf_P(LC_RC_SET_PROPERTY,&data);
	return 5 + input[3];
}

///////////////////
//Introspection
///////////////////
//51
static uint8_t cmd_get_codebases_all(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	// initialize output
   lc_is_get_cbids_t data = {(uint8_t*)(output + 2),0,oplen - 2};
   output[0] = looci_intro_P(LC_IS_GET_CBIDS,&data);
   output[1] = data.elements;
   PRINTF("getting comps %u %u %u",oplen,output[0],output[1]);
   return output[1] + 2;
 }

//52
static uint8_t cmd_get_codebase_type(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
   	oplen -=2;
   	lc_is_get_cb_type_t data = {input[0], (char*)(output + 2), 0,oplen};
    output[0] = looci_intro_P(LC_IS_GET_CB_TYPE,&data);
	return data.size + 2; // + 3: 1 for result, 1 for terminating null character, 1 for reply char

}

//53
static uint8_t cmd_get_codebase_by_type(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	lc_is_get_cbids_of_type_t data = {(char*)input, output + iplen + 2,0,oplen-iplen-2};
	memcpy(output+1,input,iplen);
	output[0] = looci_intro_P(LC_IS_GET_CBIDS_OF_TYPE,&data);
	output[iplen+1] = data.elements;
    return data.elements + iplen + 2;
 }

//54
static uint8_t cmd_get_components_by_cb_id(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	lc_is_get_cid_by_cbid_t data = {input[0],(uint8_t*)(output + 3),0, oplen - 2};
	output[0] = looci_intro_P(LC_IS_GET_CID_BY_CBID,&data);
	output[2] = data.size;
    return output[2] + 3;
 }
//55
static uint8_t cmd_get_cb_id_of_cmp_id(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen){
	lc_is_get_cbid_of_cid_t data = {(uint8_t)input[0],0};
	output[0] = looci_intro_P(LC_IS_GET_CBID_OF_CID,&data);
	output[2] = data.cbid;
    return 3;
}
//56
static uint8_t cmd_get_component_type(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
   	oplen -=2;
	lc_is_get_c_type_t data = {input[0], (char*)(output + 2),0, oplen};
	output[0] = looci_intro_P(LC_IS_GET_C_TYPE,&data);
   	return data.size + 3; // + 2: 1 for result, 1 for terminating null character

}
//57
static uint8_t cmd_get_components_all(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
    // initialize output
   lc_is_get_c_ids_t data = {(uint8_t*)(output + 2),0, oplen - 2};
	output[0] = looci_intro_P(LC_IS_GET_C_IDS,&data);
	output[1] = data.elements;
   	return data.elements + 2;
 }
//58
static uint8_t cmd_get_state(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	lc_is_get_state_t data = {input[0],0};
	output[0] = looci_intro_P(LC_IS_GET_STATE,&data);
	output[2] = data.state;
    return 3;
}
//59
static uint8_t cmd_get_properties(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	lc_is_get_properties_t data = {input[0],(uint16_t*)(output+3),0,oplen-3};
	output[0] = looci_intro_P(LC_IS_GET_PROPERTIES,&data);
	if((int8_t)output[0] <= 0){
    	return 2;
    } else{
    	output[2] = data.elements;
    	htonsa((uint16_t*)(output+3),output[2]);
    	return 3 + data.elements *2; // success, val, len, 16bit prop values * len
    }

}
//60
static uint8_t cmd_get_property(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	looci_prop_buffer_t buffer;
	buffer.propertyId = HTONS(*((looci_prop_t*)(input+1)));
	buffer.buffer = output + 5;
	buffer.size = oplen-5;
	buffer.elements = 0;
	lc_is_get_property_t data = {input[0],&buffer};
	output[0] = looci_intro_P(LC_IS_GET_PROPERTY,&data);
   	output[4] = buffer.elements;
    return 5 + buffer.elements;
}

static uint8_t cmd_get_propertyInfo(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen){
	lc_is_get_property_info_t data = {input[0],HTONS(*((looci_prop_t*)(input+1))),output + 5,0,oplen-5,0};
	output[0] = looci_intro_P(LC_IS_GET_PROP_INFO,&data);
	output[4] = data.propertyType;
   	return 5 + data.elements ; // succes, propId, cmpId, datatype
}

//61
static uint8_t cmd_get_receptacles(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen){
    // Initialize output
	lc_is_eventtype_buffer_t buffer = {input[0], (looci_eventtype_t*) (output + 3),0, oplen - 3};
	output[0] = looci_intro_P(LC_IS_GET_RECEPTACLES,&buffer);
   	output[2] = buffer.elements;
    htonsa((uint16_t*)(output+3),output[2]);
    return buffer.elements * 2 + 3;
 }
//62
static uint8_t cmd_get_interfaces(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen){
    // Initialize output
	lc_is_eventtype_buffer_t buffer = {input[0], (looci_eventtype_t*) (output + 3),0, oplen - 3};
	output[0] = looci_intro_P(LC_IS_GET_INTERFACES,&buffer);
	output[2] = buffer.elements;
	htonsa((uint16_t*)(output+3),output[2]);
	return buffer.elements * 2 + 3;
 }
//63
static uint8_t cmd_get_local_wire(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {
	// Initialize output
	looci_eventtype_t type = HTONS(*((looci_eventtype_t*)input + 0));
	// Initialize output
	uint8_t maxlen = (oplen - 5) / 4;
	subs_local buf[maxlen];
	// send back result
	lc_is_get_lcl_wires_t data = {type,input[2],input[3],buf, 0,maxlen};
	output[0] = looci_intro_P(LC_IS_GET_LCL_WIRES,&data);

	output[5] = data.index;
	//debug
	uint8_t i = 0;
	uint8_t* target = output+6;
	uint16_t* eventTypeLoc = 0;
	for(i = 0; i < data.index;i++){
		eventTypeLoc = (uint16_t*)target;
		*eventTypeLoc = HTONS(buf[i].type);
		target[2] = buf[i].src_cmp;
		target[3] = buf[i].dst_cmp;
		target += 2 + sizeof(looci_eventtype_t);
	}
	return (data.index * 4) + 6;

 }
//64
static uint8_t cmd_get_rem_to_wire(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen){

	// initialize output
    uint8_t maxLen = (oplen - 21) / 19; // integer division

    looci_eventtype_t type = HTONS(*((looci_eventtype_t*)input));
	PRINT_LN("rem to wire");
	PRINT_BYTE_ARRAY(input,iplen);
    peer_id_t to_peer = peer_get_id((peer_addr_t*)&(input[3]));

    subs_rem_to buf[maxLen];
      // send back result
    	lc_is_get_out_wires_t data = {type,input[2],to_peer,buf,0,maxLen};
    	output[0] = looci_intro_P(LC_IS_GET_OUT_WIRES,&data);
     	output[20] = data.index;
      //debug
      PRINT_LN("nr to wires:%u \r\n",(uint8_t) output[1]);
      uint8_t i = 0;
      uint8_t* target = (uint8_t*) (output + 21);
      uint16_t* eventTypeLoc = 0;
      for(i=0; i < data.index; ++i) {
        uip_ip6addr_t * addr = peer_get_addr(buf[i].dst_nod);
        if(addr != NULL){
			eventTypeLoc = (uint16_t*)target;
			*eventTypeLoc = HTONS(buf[i].type);
			target[2] = buf[i].src_cmp;
			target += 1 + sizeof(looci_eventtype_t);
			memcpy(target, addr, sizeof(uip_ip6addr_t));
			target += sizeof(uip_ip6addr_t);
		} else{
			PRINT_LN("could not find peer");
			output[0] = 0;
			output[1] = 0;
		}
      }
    return (data.index * 19) + 21;
 }
//65
static uint8_t cmd_get_rem_from_wire(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen) {


   looci_eventtype_t type = HTONS(*((looci_eventtype_t*)input));
   peer_id_t to_peer = peer_get_id((peer_addr_t*)&(input[3]));
   uint8_t maxLen = (oplen - 22) / 20;
	subs_rem_from buf[maxLen];
	// send back result
	lc_is_get_inc_wires_t data = {type,input[2], to_peer, input[19],buf,0,maxLen};
	output[0] = looci_intro_P(LC_IS_GET_INC_WIRES,&data);
	output[21] = data.index;
    //debug
    PRINT_LN("nr from wires:%u \r\n",(uint8_t) output[21]);
	uint8_t i = 0;
	uint8_t* target = (uint8_t*) (output + 22);
   uint16_t* eventTypeLoc = 0;
	for(i=0; i < data.index; ++i) {
		uip_ip6addr_t * addr = peer_get_addr(buf[i].src_nod);
		if(addr != NULL) { // better be safe than sorry
			eventTypeLoc = (uint16_t*)target;
			*eventTypeLoc = HTONS(buf[i].type);
			target += sizeof(looci_eventtype_t);
			target[0] = buf[i].src_cmp;
			target += 1;
			memcpy(target, addr, sizeof(uip_ip6addr_t));
			target += sizeof(uip_ip6addr_t);
			target[0] = buf[i].dst_cmp;
			target += 1;
		} else{
			PRINT_LN("could not find peer");
			output[0] = 0;
			output[1] = 0;
		}
	}
   return (data.index * 20) + 22;
 }
//66
static uint8_t cmd_get_platform_type(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t oplen){
	//return runtime raven
	output[1] = LOOCI_PLATFORM_CONSTANT;
	output[0] = 1;
	return 2;
}


#define NR_INS_REQUESTS 17
#define NR_REC_REQUESTS 14

typedef struct {
	uint8_t (*command_func)(unsigned char* input, uint16_t iplen, unsigned char* output, uint16_t maxOpLen);
	uint8_t minArgLength;
} commandImplementation;
PROGMEM const static commandImplementation commandReconfigList[NR_REC_REQUESTS]= {
		{cmd_install,0}, 		//1
		{cmd_remove,1}, 		//2
		{cmd_instantiate,1},	//3
		{cmd_destroy,1},		//4
		{cmd_start,1},			//5
		{cmd_stop,1},			//6
		{cmd_wire_local,4},		//7
		{cmd_wire_remote_to,19}, //8
		{cmd_wire_remote_from,20}, 		//9
		{cmd_reset_wirings,1},	//10
		{cmd_unwire_local,4},	 		//11
		{cmd_unwire_remote_to,19},		//12
		{cmd_unwire_remote_from,20},	//13
		{cmd_set_property,4}			//14
};
PROGMEM const static commandImplementation commandIntrospectionList[NR_INS_REQUESTS] = {	//18
		{cmd_get_codebases_all,0}, //51
		{cmd_get_codebase_type,1}, //52
		{cmd_get_codebase_by_type,1},		//53
		{cmd_get_components_by_cb_id,1}, //54
		{cmd_get_cb_id_of_cmp_id,1},	//55
		{cmd_get_component_type,1}, 	//56
		{cmd_get_components_all,0}, 	//57
		{cmd_get_state,1},			//58
		{cmd_get_properties,1}, //59
		{cmd_get_property,3},	//60
		{cmd_get_propertyInfo,3}, //61
		{cmd_get_receptacles,1},	//62
		{cmd_get_interfaces,1},		//63
		{cmd_get_local_wire,4},		//64
		{cmd_get_rem_to_wire,19},	//65
		{cmd_get_rem_from_wire,20},	//66
		{cmd_get_platform_type,0}, //67
};


void lc_man_init_reconfigEngine(instrospection_manager_func introE, reconfig_manager_func reconfigE){
	looci_intro_P = introE;
	looci_reconf_P = reconfigE;
}

static uint16_t doCommand(core_looci_event_t* request,const commandImplementation* commands,unsigned char* buffer, uint16_t* len,uint16_t req_type, uint16_t rep_type){
	uint16_t index = request->type - req_type;
	commandImplementation cmd;
	memcpy_P(&cmd,&commands[index],sizeof(commandImplementation));
	if(request->len >= cmd.minArgLength){
		*len = cmd.command_func((unsigned char*)request->payload,request->len,buffer,*len);
		if((int8_t)buffer[0] <= 0){
			memcpy(buffer+1,request->payload,request->len);
			*len = 1 + request->len;
		}else{
			memcpy(buffer+1,request->payload,cmd.minArgLength);
		}
	} else{
		buffer[0] = (int8_t) ERROR_ILLEGAL_ARG;
		*len = 1 + cmd.minArgLength;
	}
	return index + rep_type;
}


static uint8_t on_event(struct state* compState, core_looci_event_t* request){
	PRINT_LN("[RE] received event with id %u",request->type);
	PRINT_CORE_EVENT(request);

	uint16_t len = LOOCI_EVENT_PAYLOAD_MAXLEN;
	unsigned char buffer[LOOCI_EVENT_PAYLOAD_MAXLEN];
	uint16_t replyId = (uint16_t)EVENT_ERROR_EVENT_NOT_SUPPORTED;

	if(request->type >= CMD_RECONFIG_REQUEST_START && request->type < CMD_INTROSPECTION_REQUEST_START){
		replyId = doCommand(request,commandReconfigList,buffer,&len,CMD_RECONFIG_REQUEST_START,CMD_RECONFIG_REPLY_START);
	} else if(request->type >= CMD_INTROSPECTION_REQUEST_START && request->type < CMD_RECONFIG_REPLY_START){
		replyId =  doCommand(request,commandIntrospectionList,buffer,&len,CMD_INTROSPECTION_REQUEST_START,CMD_INTROSPECTION_REPLY_START);
	} else{
		len = 0;
	}
	PRINT_LN("[RE] handled reconfig event %u reply %u len %u",request->type,replyId,len);
	PRINTF("[RE] payload: ");
	PRINT_BYTE_ARRAY(buffer,len);
	lcpEvPub(replyId,buffer,len);

	PRINT_LN("[RE] current mem free %u",mmem_freememory());
	return LC_SUCCESS;
}

COMP_FUNCS_INIT //THIS LINE MUST BE PRESENT
COMP_FUNC_EVENT(on_event)
COMP_FUNCS_END(NULL)//THIS LINE MUST BE PRESENT

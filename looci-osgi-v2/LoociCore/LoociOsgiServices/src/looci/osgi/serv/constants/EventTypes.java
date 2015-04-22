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
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Katholieke Universiteit Leuven nor the names of
 *       its contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package looci.osgi.serv.constants;

public class EventTypes {

	/** 
	 * Event types
	 * Sub 256 reserverd for reconfigration
	 * Sub 1024 reserverd for predefined usage
	 */
	
    public static short ANY_EVENT = 0;
    public static short RECONFIGURATION_REQ_RANGE_START = 1;
    public static short RECONFIGURATION_REQ_RANGE_STOP = 50;
    public static short RECONFIGURATION_REP_RANGE_START = 101;
    public static short RECONFIGURATION_REP_RANGE_STOP = 150;
    public static short INTROSPECTION_REQ_RANGE_START = 51;
    public static short INTROSPECTION_REQ_RANGE_STOP = 100;
    public static short INTROSPECTION_REP_RANGE_START = 151;
    public static short INTROSPECTION_REP_RANGE_STOP = 200;
    
    //RECONFIG
    
    //COMPONENT MANAGEMENT
    public static short INSTALL_CODEBASE_EV = 1;
    public static short REMOVE_CODEBASE_EV = 2;
    public static short INSTANTIATE_CMP_EV = 3;
    public static short DESTROY_CMP_EV = 4;
    public static short START_COMPONENT_EV = 5;
    public static short STOP_COMPONENT_EV = 6;
    
    // WIRING COMMANDS
    public static short WIRE_LOCAL_EV = 7;
    public static short WIRE_REM_TO_EV = 8;
    public static short WIRE_REM_FROM_EV = 9;
    public static short RESET_WIRINGS_EV = 10;
    public static short UNWIRE_LCL_EV = 11;
    public static short UNWIRE_REM_TO_EV = 12;
    public static short UNWIRE_REM_FROM_EV = 13;
    
    //PROPERTIES
    public static short SET_PROPERTY = 14;
    
    // INTROSPECTION
    
    // COMPONENT META INFO
    public static short GET_ALL_CODEBASE_IDS_EV = 51;
    public static short GET_NAME_OF_CB_ID_EV = 52;
    public static short GET_CB_ID_BY_NAME_EV = 53;
    public static short GET_COMP_IDS_OF_CB_ID = 54;
    public static short GET_CB_ID_OF_COMP_ID_EV = 55;
    public static short GET_NAME_OF_COMP_ID_EV = 56;
    public static short GET_ALL_COMPONENT_IDS_EV = 57;
    public static short GET_STATE_EV = 58;
    
    //properties
    public static short GET_PROPERTIES = 59;
    public static short GET_PROPERTY = 60;
    public static short GET_PROPERTY_NAME = 61;
    
    public static short GET_RECEPTACLES_EV = 62;
    public static short GET_INTERFACES_EV = 63;
    
    //COMPONENT RUNTIME INFO
    public static short GET_LCL_WIRE_EV = 64;
    public static short GET_REM_TO_WIRE_EV = 65;
    public static short GET_REM_FROM_WIRE_EV = 66;
    public static short GET_PLATFORM_TYPE_EV = 67;
    
    //shortcuts
    public static short GET_FULL_COMPONENT_INFO = 68;
    public static short GET_ALL_INSTANCES_INFO = 69;
    
    ///////////////////////////////////////////////////
    //REPLY EVENTS
    ///////////////////////////////////////////////////
    
    //RECONFIG
    public static short INSTALL_APPROVE_EV = 101;
    public static short REMOVED_EV = 102;
    public static short INSTANTIATED_EV = 103;
    public static short DESTROYED_EV = 104;
    public static short STARTED_EV = 105;
    public static short STOPPED_EV = 106;
    public static short WIRED_LOCAL_EV = 107;
    public static short WIRED_REM_TO_EV = 108;
    public static short WIRED_REM_FROM_EV = 109;
    public static short WIRES_RESET_EV = 110;
    public static short UNWIRED_LCL_EV = 111;
    public static short UNWIRED_REM_TO_EV = 112;
    public static short UNWIRED_REM_FROM_EV = 113;
    public static short PROPERTY_SET_EV = 114;
    
    //INTROSPECTION
    public static short ALL_CODEBASE_IDS_EV = 151;
    public static short NAME_OF_CB_ID_EV = 152;
    public static short CB_ID_BY_NAME_EV = 153;
    public static short COMP_IDS_OF_CB_ID = 154;
    public static short CB_ID_OF_COMP_ID_EV = 155;
    public static short NAME_OF_COMP_ID_EV = 156;
    public static short ALL_COMPONENT_IDS_EV = 157;
    public static short STATE_EV = 158;
    public static short PROPERTIES_EV = 159;
    public static short PROPERTY_EV = 160;
    public static short PROPERTY_NAME_EV = 161;
    public static short RECEPTACLES_EV = 162;
    public static short INTERFACES_EV = 163;
    public static short LCL_WIRE_EV = 164;
    public static short REM_TO_WIRE_EV = 165;
    public static short REM_FROM_WIRE_EV = 166;
    public static short PLATFORM_TYPE_EV = 167;
    public static short FULL_COMPONENT_INFO = 168;
    public static short ALL_INSTANCES_INFO = 169;
    

    /*
     * Sub 200 r
     */
    


    

    // Application events
    public static short HUMIDITY_READING = 256;
    public static short TEMP_READING = 257;
    public static short LIGHT_READING = 258;
    public static short FILTERED_LIGHT_READING = 259;
    public static short FILTERED_TEMP_READING = 260;
    public static short FILTERED_HUMIDITY_READING = 261;
    public static short AVERAGE_LIGHT_READING = 262;
    public static short AVERAGE_TEMP_READING = 263;
    public static short AVERAGE_HUMIDITY_READING = 264;    
    
    //reading stuff
    public static short BUTTON_READING = 270;
    public static short SWITCH_READING = 271;
    
    //operational events
    public static short DO_OP_EVENT = 280;
    public static short ON_OFF_EVENT = 281;
    public static short STRING_EVENT = 283;
    
    // NeighbourDetection events
    public static short STARTNEIGHBOURDETECTION = 301;
    public static short STOPNEIGHBOURDETECTION = 302;
    public static short GETNEIGHBOURS = 303;
    public static short GET_K_NEIGHBOURS = 304;
    public static short NEIGHBOURSADVERT = 305;
        
    
    //RPC events
    public static short RPC_REQUEST = (short) 512;
    public static short RPC_REPLY = (short) 513;
    public static short RPC_ERROR = (short) 514;
    

    public static final boolean event_type_matches(short event1, short event2) {
        if ((event1 == ANY_EVENT) || (event2 == ANY_EVENT) || (event1 == event2)) {
            return true;
        } else {
            return false;
        }
    }
    

    
}
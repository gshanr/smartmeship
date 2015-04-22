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
/**
 * @file 
 * Header file for the LooCI Event Types
 * @author
 * Wouter Horré <wouter.horre@cs.kuleuven.be>
 */

#ifndef __LOOCI_EVENTTYPE_H__
#define __LOOCI_EVENTTYPE_H__

#include <stdint.h>
#include "looci-types.h"
/*
 * List of pre-configured event types
 */

#define EV_ANY_EVENT 0
#define EV_END_EVENT 65535

// REQUEST EVENTS
#define CMD_RECONFIG_REQUEST_START 1
#define CMD_INTROSPECTION_REQUEST_START 51
#define CMD_RECONFIG_REPLY_START 101
#define CMD_INTROSPECTION_REPLY_START 151
#define CMD_MGT_END 200

#define EV_MGT_SEC_REQ 65000
#define EV_MGT_SEC_REP 65050
#define EV_SEC_COM 65280

//User constants

#define LC_OWNER_NONE 0

#define PROCESS_LC_RM 					0x70 //12
#define PROCESS_LC_RECEIVE_EVENT 		0x71
#define PROCESS_LC_INIT 				0x72 //114
#define PROCESS_LC_DESTROY 				0x73
#define PROCESS_LC_GET_PROPERTIES 		0x74
#define PROCESS_LC_GET_PROPERTY 		0x75
#define PROCESS_LC_SET_PROPERTY 		0x76
#define PROCESS_LC_GET_PROPERTY_NAME 	0x77
#define PROCESS_LC_PROPERTY_IS_SET 		0x78

// Note contiki processes start at 0x80, so do not use 0x80 or higher

#define COMPONENT_LC_SET_PROPERTY 0xfff0
#define COMPONENT_LC_GET_PROPERTY 0xfff1
#define COMPONENT_LC_GET_PROPERTIES 0xfff2
#define COMPONENT_LC_GET_PROPERTY_NAME 0xfff3
#define COMPONENT_LC_PROPERTY_IS_SET 0xfff4

#define COMPONENT_LC_INIT 0xfff5
#define COMPONENT_LC_DESTROY 0xfff6

#endif /* __LOOCI_EVENTTYPE_H__ */
/** @} */
/** @} */

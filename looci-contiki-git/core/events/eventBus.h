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
 * @addtogroup event
 * @{
 * @defgroup events The LooCI Event Bus
 * @{
  */
/**
 * @file 
 * Header file for the LooCI Event Bus
 */
#ifndef __EVENT_BUS_H__
#define __EVENT_BUS_H__

#include "lib/memb.h"
#include "lib/list.h"
#include "event.h"
#include "process.h"
#include <string.h>
#include <stdbool.h>
#include <stdint.h>


/**
 * @name Return values
 * @{
 */
/**
 * @name Contiki definitions
 * @brief Definitions and macro calls needed for Contiki
 * @{
 */
/**
 * @brief The name of the Contiki process for the LooCI Event Manager
 */
PROCESS_NAME(looci_event_manager);


/** @} */


/**
 * Publish a LooCI event.
 *
 * @param type The event type.
 * @param payload The event payload.
 * @param len The length of the payload.
 * 
 * @retval LOOCI_SUCCESS The event was published successful
 * @retval LOOCI_ERR_EVENTNOTPUBLISHED The event could not be published
 *
 */
uint8_t lcpEvPub(looci_eventtype_t type, void * payload, uint16_t len);


/**
 * Wait until  an event is received.
 *
 * @param event Pointer to the event after the event is received. This pointer
 *              should point to an allocated event structure. The event will be
 *              copied into the memory pointed to by the pointer.
 *
 * @hideinitializer
 */
// The do { } while() construction is needed because we can't use the PROCESS_WAIT_EVENT_UNTIL macro twice
// in the same macro.
// We could define this one in terms of LOOCI_EVENT_RECEIVE_UNTIL, but since this is a macro and will
// be included verbatim into components that use it, we go for the shortest form.
#define LOOCI_EVENT_RECEIVE(event) \
	PROCESS_WAIT_EVENT_UNTIL(ev == PROCESS_LC_RECEIVE_EVENT); \
	core_looci_event_t* event = data

/**
 * Wait until an event is received or condition is true.
 *
 * @param event Pointer to the event after the event is received. This pointer
 *              should point to an allocated event structure. The event will be
 *              copied into the memory pointed to by the pointer.
 * @param condition Alternative condition that will interupt the waiting for an event
 *                  when the condition becomes true.
 *
 * @hideinitializer
 */
// The do { } while() construction is needed because we can't use the PROCESS_WAIT_EVENT_UNTIL macro twice
// in the same macro.
#define LOOCI_EVENT_RECEIVE_UNTIL(event, condition) do { \
	PROCESS_WAIT_EVENT_UNTIL(ev == PROCESS_LC_RECEIVE_EVENT || (condition)); \
	if(ev == PROCESS_LC_RECEIVE_EVENT) {event = data;}\
	else{ event = NULL;}\
} while(0);\

/**
 * Condition to test whether an event is received. 
 * Can be used to test whether LOOCI_EVENT_RECEIVE_UNTIL stopped because 
 * an event was received.
 *
 * For example:
 * LOOCI_EVENT_RECEIVE_UNTIL(event, etimer_expired(&et));
 * if(LOOCI_EVENT_RECEIVED) {
 *   do something with event
 * } else if(etimer_expired(&et)) {
 *   do something else
 * }
 *
 * @hideinitializer.
 */
#define LOOCI_EVENT_RECEIVED (ev == PROCESS_LC_PR)


////////////////////////
// Structures for use in the introspection API.
///////////////////////////
/**
 * Structure that holds the information for an incoming remote wire.
 *
 */
struct wire_from {
  peer_id_t source_node;
  uint8_t source_cid;
};

// Data structures
typedef struct{
  looci_eventtype_t type;
  uint8_t src_cmp;
  uint8_t dst_cmp;
}subs_local;

typedef struct{
  looci_eventtype_t type;
  uint8_t src_cmp;
  peer_id_t src_nod;
  uint8_t dst_cmp;
}subs_rem_from;

typedef struct{
  looci_eventtype_t type;
  uint8_t src_cmp;
  peer_id_t dst_nod;
}subs_rem_to;


extern struct net_listener lc_em_listener;


looci_event_t* lc_em_getCurrentEvent();

#endif /* __LOOCI_EVENTS_H__ */
/** @} */
/** @} */

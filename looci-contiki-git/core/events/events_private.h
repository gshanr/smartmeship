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
 * @addtogroup events
 * @{
 */
/**
 * @internal
 * @file Header file for the internal API of the event manager
 * @author Wouter Horré <wouter.horre@cs.kuleuven.be>
 */
#ifndef __LOOCI_EVENTS_PRIVATE_H__
#define __LOOCI_EVENTS_PRIVATE_H__

#include "introspection_local.h"
#include "runtime_control_local.h"
#include "eventBus.h"
#include "net/peers.h"
#include <stdint.h>
#include <stdbool.h>

#define SOURCE_ANY 0


/**
 * @internal
 * Add a local subscription
 *
 * @param type The event type for the subscription
 * @param source The id of the event source
 * @param target The id of the event target
 */
int8_t events_add_local_subscription(subs_local* subscription);

/**
 * @internal
 * Add a remote subscription (outgoing events)
 *
 * @param type The event type for the subscription
 * @param source The id of the event source
 * @param peer The id of the peer to send the event to
 */
int8_t events_add_remote_subscription_to(subs_rem_to* subscription);

/**
 * @internal
 * Add a remote subscription (incoming events)
 *
 * @param type The event type for the subscription
 * @param source_node The source node of the event
 * @param source_iid The instance id of the source
 * @param destination_iid The id of the destination instance
 */
int8_t events_add_remote_subscription_from(subs_rem_from* subscription);

/**
 * @internal
 * Remove a local subscription
 *
 * @param type The event type for the subscription
 * @param source_iid The instance id of the source
 * @param destination_iid The id of the destination instance
 */
int8_t events_remove_local_subscription(subs_local* subscription);

/**
 * @internal
 * Remove a remote subscription (outgoing events)
 *
 * @param type The event type for the subscription
 * @param source_iid The instance id of the source
 * @param peer The id of the peer to send the event to
 */
int8_t events_remove_remote_subscription_to(subs_rem_to* subscription);

/**
 * @internal
 * Remove a remote subscription (incoming events)
 *
 * @param type The event type for the subscription
 * @param source_node The node id of the source
 * @param source_iid The instance id of the source
 * @param destination_iid The id of the destination instance
 */
int8_t events_remove_remote_subscription_from(subs_rem_from* subscription);


/**
 * @internal
 * Remove all subscriptions for a specific instance
 *
 * @param cid The instance id of the instance to be removed
 */
int8_t events_unwire_component(lc_rc_component_t* data);

/**
 * Get the local wires for an interface.
 *
 * @param eventtype The type of events the interface produces.
 * @param cid The component that produces the events
 * @param buffer A buffer where the ids of the destination components will be copied to.
 * @param size A pointer to a variable that will contain the number of component ids copied to the
 *             buffer after the call. This variable must be initialized to the maximum number of
 *             component ids the buffer can hold.
 * 
 * @return 'true' if successful, 'false' otherwise
 *
 * @note if the return value is false and *size > 0, the buffer was too small to hold all component ids.
 *       if the return value is false and *size==0, an other error occured.
 *       if the return value is true and *size==0, the interface is not wired to a local component.
 */
int8_t events_getlocalwires(lc_is_get_lcl_wires_t* data);

/**
 * Get the remote outgoing wires for an interface.
 *
 * @param eventtype The type of events the inteface produces.
 * @param cid The component that produces the events
 * @param buffer A buffer where the ids of the remote destination nodes will be copied to.
 * @param size A pointer to a variable that will contain the number of node ids copied to the
 *	       buffer after the call. This variable must be initialized to the maximum number of
 *	       component ids the buffer can hold.
 * 
 * @return 'true' if successful, 'false' otherwise
 *
 * @note if the return value is false and *size > 0, the buffer was too small to hold all node ids.
 *       if the return value is false and *size==0, an other error occured.
 *       if the return value is true and *size==0, the interface is not wired to a remote component.
 */
int8_t events_getremotewires_to(lc_is_get_out_wires_t* data);

/**
 * Get the remote incoming wires for a receptacle.
 *
 * @param eventtype The type of events the remote interface produces.
 * @param cid The identifier of the destination component.
 * @param buffer A buffer where the information about the incoming wires will be written to.
 * @param size A pointer to a variable that will contain the number of incoming wires after the call. This variable
 *             must be initialized to the maximum number of incoming wires that the buffer can contain.
 *
 * @return 'true' if successful, 'false' otherwise
 *
 * @note if the return value is false and *size > 0, the buffer was too small to hold all incoming wires.
 *       if the return value is false and *size==0, an other error occured.
 *       if the return value is true and *size==0, there are no incoming wires for the receptacle.
 */
int8_t events_getremotewires_from(lc_is_get_inc_wires_t* data);


bool ev_getHeaderLoc(char* buffer, uint8_t headerId, uint8_t* headerLoc, uint8_t* blockLoc);
void looci_event_handle_remote_event(looci_event_t* event);


#endif
/** @} */

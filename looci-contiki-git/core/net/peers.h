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
 * @addtogroup net
 * @{
 *
 * @defgroup peers The LooCI peers library
 * @{
 *
 * This library is used to allow the LooCI system to store each peer address only once.
 * 
 * The peer address is typically a large data structure (16 bytes for uIP6). This library
 * allows to use an id, which is a lot smaller.
 */
/**
 * @file
 * The header file for the LooCI peers library
 * @author
 * Wouter Horré <wouter.horre@cs.kuleuven.be>
 */
#ifndef __PEERS_H__
#define __PEERS_H__

#include "contiki-net.h"
#include "peers_pub.h"




/**
 * The type of the peer address
 */
typedef uip_ip6addr_t peer_addr_t;

/**
 * Initialize the peer library
 */
CCIF void peer_init();

/**
 * Add a peer to the peer list
 *
 * @param addr The address of the new peer
 *
 * @return The id assigned to the peer
 */
CCIF peer_id_t peer_add(peer_addr_t * addr);

/**
 * Remove a peer from the peer list
 *
 * @param id The peer id
 *
 * @note All peer_addr_t pointers become invalid by calling this method.
 *       They must be rerequested from the peers library with a call to
 *       peer_get_addr().
 */
CCIF void peer_remove(peer_id_t id);

/**
 * Get the address of a peer
 *
 * @param id The peer id
 *
 * @return The address of the peer. NULL if the id is not known.
 *
 * @note The returned pointer is NOT valid across Contiki
 *       process waits!!!
 */
CCIF peer_addr_t * peer_get_addr(peer_id_t id);

/**
 * Get the id of a peer
 *
 * @param addr The address of the peer
 *
 * @return The id of the peer. PEER_ID_NONE if the peer isn't
 *         in the list yet.
 */
CCIF peer_id_t peer_get_id(peer_addr_t * addr);

/**
 * Get the id of a peer or add it if the peer is not yet known.
 *
 * @param addr The address of the peer
 *
 * @return The id of the peer.
 */
CCIF peer_id_t peer_get_id_or_add(peer_addr_t * addr);

#endif // __PEERS_H__
/** @} */
/** @} */

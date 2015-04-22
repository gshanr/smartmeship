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
 * @addtogroup peers
 * @{
 */
/**
 * @file
 * The implementation of the LooCI peers library
 * @author
 * Wouter Horré <wouter.horre@cs.kuleuven.be>
 */

#include <string.h>
#include "peers.h"
#include "lib/vector.h"
#include "uip-ds6.h"

#define SMARTMESH_EN 1

#ifdef LOOCI_PEER_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif

/**
 * @internal
 * Structure for storing a peer
 */
struct peer {
  peer_id_t id;
  peer_addr_t addr;
};

/**
 * @internal
 * The vector to store the peers
 */
VECTOR(peers, struct peer,4);

/**
 * @internal
 * The next free id to use
 */
static peer_id_t next_id = 3; // we start at 3 (1 = PEER_ID_ALL)

/**
 * @internal
 * Grab an unused id
 *
 * @TODO Fix to prevent wrapping around, etc ...
 */
#define GRAB_ID next_id++

/**
 * @internal
 * Release an id
 *
 * @TODO implement
 */
#define RELEASE_ID(id)

void peer_init() {
	vector_init(&peers);

	struct peer new_peer;

	static uint8_t i;

	i=0;

	// add identification for PEER_ID_ANY
	new_peer.id = PEER_ID_ANY;
	memset(&(new_peer.addr), 0, sizeof(peer_addr_t));
	PRINTF("[LooCI Peers] Added new peer with id %u and address: ", new_peer.id);
	PRINT6ADDR(&new_peer.addr);
	PRINTF("\r\n");
	vector_add(&peers, &new_peer);
	#if SMARTMESH_EN == 0
	// add broadcast with id PEER_ID_ALL
	new_peer.id = PEER_ID_ALL;
	new_peer.addr.u8[0] = 0xFF;
	new_peer.addr.u8[1] = 0x02;
	new_peer.addr.u8[15] = 0x01;
	#else
	// add broadcast with id PEER_ID_ALL
	new_peer.id = PEER_ID_ALL;
		for(i=0;i<16;i++)
		new_peer.addr.u8[i] = 0xFF;
	#endif


	PRINTF("[LooCI Peers] Added new peer with id %u and address: ", new_peer.id);
	PRINT6ADDR(&new_peer.addr);
	PRINTF("\r\n");
	vector_add(&peers, &new_peer);
}

peer_id_t peer_add(peer_addr_t * addr) {
  struct peer new_peer;
  new_peer.id = GRAB_ID;
  memcpy(&(new_peer.addr), addr, sizeof(peer_addr_t));
  if(vector_add(&peers, &new_peer) == VECTOR_OK) {
    PRINTF("[LooCI Peers] Added new peer with id %u and address: ", new_peer.id);
    PRINT6ADDR(&new_peer.addr);
    PRINTF("\r\n");
    return new_peer.id;
  } else {
    return PEER_ID_NONE;
  }
}

void peer_remove(peer_id_t id) {
  int i;
  for(i=0; i < vector_len(&peers); ++i) {
    struct peer * thepeer = (struct peer *)vector_get(&peers, i);
    if(thepeer->id == id) {
      vector_remove(&peers, i);
      RELEASE_ID(id);
      break;
    }
  }
}

peer_addr_t * peer_get_addr(peer_id_t id) {
//	if(id ==PEER_ID_SELF){
//		uip_ds6_addr_t* myAddr = uip_ds6_get_global(ADDR_PREFERRED);
//		return (peer_addr_t*) &(myAddr->ipaddr);
//	}

  int i;
  for(i=0; i < vector_len(&peers); ++i) {
    struct peer * thepeer = (struct peer *) vector_get(&peers, i);
    if(thepeer->id == id) {
      return &(thepeer->addr);
    }
  }
  return NULL;
}

peer_id_t peer_get_id(peer_addr_t * addr) {
    PRINTF("[Peers] requested peer with address: ");
    PRINT6ADDR(addr);
    PRINTF("\r\n");
  short i;
//  if(memcmp(addr, &(uip_ds6_get_global(ADDR_PREFERRED)->ipaddr), sizeof(peer_addr_t))==0){
//	  PRINT_LN("[Peers]  returing self");
//	  return PEER_ID_SELF;
//  }
  for(i=0; i < vector_len(&peers); ++i) {
    struct peer * thepeer = (struct peer *) vector_get(&peers, i);
    if(memcmp(addr, &(thepeer->addr), sizeof(peer_addr_t))==0) {
    	PRINT_LN("[Peers] returning id: %u",thepeer->id);
    	return thepeer->id;
    }
  }
  PRINT_LN("[Peers] peer not found");
  return PEER_ID_NONE;
}

peer_id_t peer_get_id_or_add(peer_addr_t * addr) {
  peer_id_t result = peer_get_id(addr);
  if(result == PEER_ID_NONE) {
    result = peer_add(addr);
  }
  return result;
}

//TODO peer cleanup

/** @} */

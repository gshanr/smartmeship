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
 * peers_pub.h
 *
 *  Created on: Mar 28, 2012
 *      Author: root
 */

#ifndef PEERS_PUB_H_
#define PEERS_PUB_H_

/**
 * The peer id that will never be assigned to a real peer.
 *
 * If this peer id is returned, it indicates a failure
 */
#define PEER_ID_NONE 255

#define PEER_ID_ANY 0
/**
 * The peer id that will be assigned to the 'broadcast peer'.
 * In the current IPv6 implementation, this is the
 * 'all nodes on this link' multicast address FF02::1
 */
#define PEER_ID_ALL 1


#define PEER_ID_SELF 2

/**
 * The type of the peer id.
 */
typedef uint8_t peer_id_t;

#endif /* PEERS_PUB_H_ */

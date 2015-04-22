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
 * @internal
 * @file 
 * Header file for the internal API of the LooCI component store
 * @author 
 * Wouter Horré <wouter.horre@cs.kuleuven.be>
 */
#ifndef __LOOCI_COMPONENTSTORE_PRIVATE_H__
#define __LOOCI_COMPONENTSTORE_PRIVATE_H__

PROCESS_NAME(looci_component_store);

/**
 * @internal
 * Unload a component
 *
 * NOTE: if there were multiple components in the file that was loaded
 * to load this component, they will all be stopped and unloaded!
 *
 * @param cid The id of the component to unload.
 */
void lc_cstore_unload(struct component* component);

#endif // __LOOCI_COMPONENTSTORE_PRIVATE_H__
/** @} */
/** @} */

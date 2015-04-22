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
package looci.osgi.serv.interfaces;

import looci.osgi.serv.constants.LoociManagementException;

/**
 *  Interface providing reconfiguration operations for the looci middleware on this node.
 *  Note this is all local
 */
public interface IReconfigure {

	public void installComponent() throws LoociManagementException;
	
	public void removeCodebase(byte cb_id) throws LoociManagementException;
	
	public byte instantiateComponent(byte cb_id) throws LoociManagementException;
	
	public void destroyComponent(byte comp_id) throws LoociManagementException;

	public void activate(byte comp_id)throws LoociManagementException;

	public void deactivate(byte comp_id) throws LoociManagementException;

	public void resetWirings(byte comp_id) throws LoociManagementException;
		
	//Wiring

	public void wireLocal(short event_id, byte src_comp_id, byte dst_comp_id) throws LoociManagementException;

	public void wireFrom(short event_id, byte src_comp_id, String src_addr, byte dst_comp_id) throws LoociManagementException;

	public void wireTo(short event_id, byte src_comp_id, String dst_addr) throws LoociManagementException;
	
	public void unWireLocal(short event_id, byte src_comp_id, byte dst_comp_id) throws LoociManagementException;

	public void unWireFrom(short event_id, byte src_comp_id, String src_addr, byte dest_comp_id) throws LoociManagementException;
		
	public void unWireTo(short event_id, byte src_comp_id, String dst_addr) throws LoociManagementException;

	public void setProperty(byte inst_id, short prop_id, byte[] val) throws LoociManagementException;
	

}

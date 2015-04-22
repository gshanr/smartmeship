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

import looci.osgi.serv.bindings.LocalBinding;
import looci.osgi.serv.bindings.RemoteFromBinding;
import looci.osgi.serv.bindings.RemoteToBinding;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.impl.property.PropertyInfo;


public interface IInspect {

	/**
	 * get all codebase ids of all codebases installed on this node
	 * @throws LoociManagementException
	 */
	public byte[] getCodebaseIDs() throws LoociManagementException;	

	/**
	 * get the codebase type of the codebase with the given id
	 */
	public String getCodebaseType(byte cb_id) throws LoociManagementException;
	
	/**
	 * get the component ids of of all components installed on this node
	 */
	public byte[] getComponentIDs() throws LoociManagementException;
		
	
	/**
	 * get all component ids for a gived codebase id
	 */
	public byte[] getComponentIDsByCbID(byte cb_id) throws LoociManagementException;
	
	/**
	 * get the codebase id of the codebase with the given type
	 */
	public byte[] getCodebaseIDsByType(String cb_type) throws LoociManagementException;

	
	
	/**
	 * get the component type of the codebase with the given id
	 */
	public String getComponentType(byte cb_id) throws LoociManagementException;
	
	/**
	 * get the codebase id of the component with the given component id
	 */
	public byte getCodebaseOfComponent(byte comp_id) throws LoociManagementException;
	
	/**
	 * get the state of the component with the given component id
	 */
	public byte getState(byte comp_id) throws LoociManagementException;	
	
	/**
	 * get the interfaces (provided interfaces) of the component with the given component id
	 */
	public short[] getInterfaces(byte comp_id) throws LoociManagementException;

	/**
	 * give the receptacles (required interfaces) of the component with the given component id
	 */
	public short[] getReceptacles(byte comp_id) throws LoociManagementException;
	
	/**
	 * get all the local wirings of the node that match the requested filters
	 * wildcards can be used
	 */
	public LocalBinding[] getLocalWires(short event_id, byte src_comp_id, byte dst_comp_id) throws LoociManagementException;

	/**
	 * get all the outgoing wires of the node that match the requested filters
	 * wildcards can be used
	 */
	public RemoteToBinding[] getOutgoingRemoteWires(short event_id, byte src_comp_id, String dst_node_id) throws LoociManagementException;

	/**
	 * get all the incoming wires of the node that match the requested filters
	 * wildcard can be used
	 */
	public RemoteFromBinding[] getIncomingRemoteWires(short event_id, byte src_comp_id, String src_node_id, byte dst_comp_id) throws LoociManagementException;
	
	/**
	 * get all the properties that the component with the given id 
	 */
	public short[] getAllProperties(byte comp_id) throws LoociManagementException;
	
	/**
	 * get the property value of the property with the given property id of the component with the given component id
	 * property value is always byte array.
	 * What the byte array represents can be checked by requesting the property info
	 */
	public byte[] getPropertyValue(byte comp_id, short prop_id) throws LoociManagementException;

	/**
	 * get the property info of the property with the given property id of the component with the given component id
	 * the property id contains a property name, and a property type
	 */
	public PropertyInfo getPropertyInfo(byte comp_id, short prop_id)  throws LoociManagementException;

	
}

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
 * TBC.
 */
package looci.osgi.servExt.mgt;

import java.net.UnknownHostException;

import looci.osgi.serv.bindings.LocalBinding;
import looci.osgi.serv.bindings.RemoteFromBinding;
import looci.osgi.serv.bindings.RemoteToBinding;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.impl.property.PropertyInfo;

/**
 * Introspection API that allows for remote introspection of nodes.
 */
public interface IIntrospectionAPI {
	
	/**
	 * get all the codebaseIDs of the codebases on the targetted node
	 */
    public byte[] getCodebaseIDs(String nodeID) throws LoociManagementException,UnknownHostException; 
    
    /**
     * get the name of the codebase by a given codebaseId on a given node
     * @param codebaseID
     * @param nodeID
     * @return
     * @throws LoociManagementException
     */
    public String getCodebaseName(byte codebaseID, String nodeID) throws LoociManagementException,UnknownHostException;
    
    /**
     * get the codebaseId of a component with a given name on the given node
     * @param componentType
     * @param nodeID
     * @return
     * @throws LoociManagementException
     */
    public byte[] getCodebaseIDsByName(String componentType, String nodeID) throws LoociManagementException,UnknownHostException;
    
    /**
     * get all the component ids which are an instance of a given codebaseId
     * @param codebaseID
     * @param nodeID
     * @return
     * @throws LoociManagementException
     */
    public byte[] getComponentIDsbyCodebaseID(byte codebaseID, String nodeID) throws LoociManagementException,UnknownHostException;
    
    /**
     * Get the codebaseId of the codebase of which the component with the given id is an instance of
     * @param componentID
     * @param nodeID
     * @return
     * @throws LoociManagementException
     */
    public byte getCodebaseIdOfComponent(byte componentID, String nodeID) throws LoociManagementException,UnknownHostException;
    
    /**
     * get the name of the codebase of which this component with the given id is an instance of
     * @param componentID
     * @param nodeID
     * @return
     * @throws LoociManagementException
     */
    public String getComponentName(byte componentID, String nodeID) throws LoociManagementException,UnknownHostException;

    /**
     * get all component ids of the given node
     * @param nodeID
     * @return
     * @throws LoociManagementException
     */
    public byte[] getComponentIDs(String nodeID) throws LoociManagementException,UnknownHostException;
    
    /**
     * get the state of a given component on a given node
     * 
     * @param componentID
     * @param nodeID
     * @return
     * @throws LoociManagementException
     */
    public byte getState(byte componentID, String nodeID) throws LoociManagementException,UnknownHostException;

    /**
     * get a list of the properties that the given component supports
     * 
     * @param componentID
     * @param nodeID
     * @return
     * @throws LoociManagementException
     */
    public short[] getProperties(byte componentID, String nodeID) throws LoociManagementException,UnknownHostException;
    
    /**
     * get the value of the property of a given component
     * 
     * @param propertyID
     * @param componentID
     * @param nodeID
     * @return
     * @throws LoociManagementException
     */
    public byte[] getProperty(short propertyID, byte componentID, String nodeID) throws LoociManagementException,UnknownHostException;
      
    /**
     * get meta information about the property such as its type, its name, and its length 
     * 
     * @param propertyID
     * @param componentID
     * @param nodeID
     * @return
     * @throws LoociManagementException
     */
    public PropertyInfo getPropertyInfo(short propertyID, byte componentID, String nodeID) throws LoociManagementException,UnknownHostException;    
    
    /**
     * get the interfaces of a given component
     * 
     * @param componentID
     * @param nodeID
     * @return
     * @throws LoociManagementException
     */
    public short[] getInterfaces(byte componentID, String nodeID) throws LoociManagementException,UnknownHostException;

    /**
     * get the receptacles of a given component
     * @param componentID
     * @param nodeID
     * @return
     * @throws LoociManagementException
     */
    public short[] getReceptacles(byte componentID, String nodeID) throws LoociManagementException,UnknownHostException;
         
    /**
     * get all the local wires that match the given arguements. Wildcards are allowed for eventID and componentIDs 
     * Wildcard for eventType and component is zero
     * 
     * @throws LoociManagementException
     */
    public LocalBinding[] getLocalWires(short eventID, byte srcComponentID, byte dstComponentID, String nodeID) throws LoociManagementException,UnknownHostException;
    
    /**
     * get all outgoing remote wires that match the given arguements
     * Wildcards are possible for eventID, componentIDs and dstNodeId
     * 
     * @throws LoociManagementException
     */
    public RemoteToBinding[] getOutgoingRemoteWires(short eventID, byte srcComponentID, String srcNodeID, String dstNodeID) throws LoociManagementException,UnknownHostException;

    /**
     * get all incoming remote wires that match the given arguements
     * Wildcards are possible for eventID, componentIDs and srcNodeID
     * 
     * @param eventID
     * @param srcComponentID
     * @param srcNodeID
     * @param dstComponentID
     * @param nodeID
     * @return
     * @throws LoociManagementException
     */
    public RemoteFromBinding[] getIncomingRemoteWires(short eventID, byte srcComponentID, String srcNodeID, byte dstComponentID, String nodeID) throws LoociManagementException,UnknownHostException;
    
    
    /**
     * Get the platform type of the given node id
     * 
     * @param nodeID
     * 	the address of the node to connect to, must be resolvable by pc
     * @return
     * 	byte representing the type of the platform
     *  current supported types can be found in looci standard 
     * 
     * @throws LoociManagementException
     * 	unable to connect to the platform
     */
    public byte getPlatformType(String nodeID) throws LoociManagementException,UnknownHostException;
    
    /**
     * discover all nodes present in the local environment
     * 
     * @return
     * 	a list of nodeIDs of all nodes which are currently in broadcast range
     * @throws LoociManagementException
     */
    public String[] discover() throws LoociManagementException;
  
}

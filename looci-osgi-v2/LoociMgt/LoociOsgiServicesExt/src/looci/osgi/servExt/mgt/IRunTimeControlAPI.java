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

import looci.osgi.serv.constants.LoociManagementException;

/**
 * Runtime control API that allows for remote management of nodes
 */
public interface IRunTimeControlAPI {

    /**
     * Instantiate a given codebase
     * 
     * @param codebaseID
     * 	the codebase identifier 
     * @param nodeID
     *  the node identifier
     * 	
     * @return
     * 	the component identifier
     * @throws LoociManagementException
     * 	cannot find codebase
     * 	cannot connect to node
     */
    public byte instantiate(byte codebaseID, String nodeID) throws LoociManagementException,UnknownHostException;
    
    /**
     * Remove the given codebase of the given node
     * 
     * @param codebaseID
     * @param nodeID
     * @throws LoociManagementException
     * 	cannot find codebase
     * 	cannot connect to node
     */
    public void remove(byte codebaseID, String nodeID) throws LoociManagementException,UnknownHostException;
	
    /**
     * Deactivate the component with the given componentID at the given nodeID
     * 
     * @param componentID
     * @param nodeID
     * @throws LoociManagementException
     */
    public void deactivate(byte componentID, String nodeID) throws LoociManagementException,UnknownHostException;

    /**
     * Activate the component with the given componentID at the given nodeID
     * 
     * @param componentID
     * @param nodeID
     * @throws LoociManagementException
     */
    public void activate(byte componentID, String nodeID) throws LoociManagementException,UnknownHostException;
    
    /**
     * Destroy the component with the given componentID at the given nodeID
     * This will also destroy all wires on that node to and from the components.
     * WARNING: Wires on remote nodes involving this component wil REMAIN
     * The codebase of the given component remains 
     * 
     * @param componentID
     * @param nodeID
     * @throws LoociManagementException
     */
    public void destroy(byte componentID, String nodeID) throws LoociManagementException,UnknownHostException;    

    /**
     * Remove all wires to and from the component with the given componentID at the given nodeID
     * wires on other nodes involving this component will REMAIN
     * 
     * @param componentID
     * @param nodeID
     * @throws LoociManagementException
     */
    public void resetWirings(byte componentID, String nodeID) throws LoociManagementException,UnknownHostException;

    /**
     * perform a local wire between two components on the same node, with a given componentID
     * srcComponent can be a wildcard
     * 
     * @param eventID
     * @param srcComponentID
     * @param dstComponentID
     * @param nodeID
     * @throws LoociManagementException
     */
    public void wireLocal(short eventID, byte srcComponentID, byte dstComponentID, String nodeID) throws LoociManagementException,UnknownHostException;

    /**
     * remove a previously made local wire on the given node
     * parameters must match exactly in order for the wire to be removed
     * 
     * @param eventID
     * @param srcComponentID
     * @param dstComponentID
     * @param nodeID
     * @throws LoociManagementException
     */
    public void unwireLocal(short eventID, byte srcComponentID, byte dstComponentID, String nodeID) throws LoociManagementException,UnknownHostException;

    /**
     * add an incoming wire from a remote node , to a component on the given node
     * 
     * @param eventID
     * 	the event id of the wire
     * @param srcComponentID
     * 	the source component id. This can be zero
     * @param srcNodeID
     * 	the source node identifier. This can be node wildcard. Note that this must be resolved to an IPv6 address
     * @param dstComponentID
     * 	the destination component id. Must resolve to an existing component
     * @param dstNodeID
     * 	the destination component id. Must resolve to operating component
     * @throws LoociManagementException
     */
    public void wireFrom(short eventID, byte srcComponentID, String srcNodeID,  byte dstComponentID, String dstNodeID) throws LoociManagementException,UnknownHostException;

    /**
     * remove a previously made incoming wire.
     * parameters must match exactly
     * 
     * @param eventID
     * @param srcComponentID
     * @param srcNodeID
     * @param dstComponentID
     * @param dstNodeID
     * @throws LoociManagementException
     */
    public void unwireFrom(short eventID, byte srcComponentID, String srcNodeID, byte dstComponentID, String dstNodeID) throws LoociManagementException,UnknownHostException;

    /**
     * add an outgoing wire to a remote node.
     * 
     * @param eventID
     *  the event id of thw wire
     * @param srcComponentID
     * 	the source component id. This can be zero to send all events.
     * @param srcNodeID
     * 	the source node. Must resolve to an existing component
     * @param dstNodeID
     * 	the destination node id. This can be wildcard, which will be resolved to the link local broadcast address.
     * @throws LoociManagementException
     */
    public void wireTo(short eventID, byte srcComponentID, String srcNodeID, String dstNodeID) throws LoociManagementException,UnknownHostException;

    /**
     * remove a previously made outgoing wire
     * parameters must match exactly
     * 
     * @param eventID
     * @param srcComponentID
     * @param srcNodeID
     * @param dstNodeID
     * @throws LoociManagementException
     */
    public void unwireTo(short eventID, byte srcComponentID, String srcNodeID, String dstNodeID) throws LoociManagementException,UnknownHostException;

    /**
     * set the given parameter of the given component on the given node to the given value.
     * 
     * @param propertyValue
     * @param propertyID
     * @param componentID
     * @param nodeID
     * @throws LoociManagementException
     */
    public void setProperty(byte[] propertyValue, short propertyID, byte componentID ,String nodeID) throws LoociManagementException,UnknownHostException;


	/**
	 * placeholder / support for deployment interactions. Currently unused in core LooCI
	 * @throws LoociManagementException
	 */
    public byte deploy(String codebaseName, String nodeID) throws LoociManagementException, UnknownHostException;
}

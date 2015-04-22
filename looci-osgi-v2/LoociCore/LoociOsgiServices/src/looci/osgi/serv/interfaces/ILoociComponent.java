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

import looci.osgi.serv.components.Event;
import looci.osgi.serv.components.IEventSource;
import looci.osgi.serv.components.IReceive;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.impl.EventQueue;
import looci.osgi.serv.impl.LoociCodebase;
import looci.osgi.serv.impl.property.PropertyInfo;

/**
 * Interface representing a looci component
 */
public interface ILoociComponent extends IReceive,IEventSource{

	
	/**
	 * Internal method called by component to set all variables
	 * @param instanceId
	 * @param publisher
	 * @param instanceOwner 
	 * @param component
	 */
	public void init(byte componentID, IReceive receiver, LoociCodebase codebase);
	
	/**
	 * Call when creating the instance
	 */
	public void create();
	
	/**
	 * Call when destroying the instance
	 */
	public void destroy();
	
	/**
	 * Activate the Instance.
	 */
	public void activate();

	/**
	 * Deactivate the Instance.
	 */
	public void deactivate();

	/**
	 * Returns the current state of this component instance.
	 * @return
	 */
	public byte getState();
	
	/**
	 * Returns the instance id of this instance
	 */
	public byte getComponentID();
	
	/**
	 * Returns the associated component of this instance.
	 * @return
	 */
	public ILoociCodebase getCodebase();
	
	/**
	 * Returns the receiver queue associated with this instance
	 * Can be re-implemented to return instance itself
	 */
	public EventQueue getReceiver();
	
	/**
	 * Returns the event that is currently being handled. Is removed once the receive methods returns.
	 */
	public Event getReceptionEvent();
	
	/**
	 * Return the list of interfaces of this component
	 */
	public short[] getInterfaces();
	
	/**
	 * Return the list receptacles of this component
	 */
	public short[] getReceptacles();
	
	/**
	 * Return the list of properties of this component
	 */
	public short[] getProperties();	
	
	/**
	 * Returns the byte array representation of the requests property
	 * 
	 * @throws LoociManagementException
	 * 	The requested property is not available
	 */
	public byte[] getProperty(short propertyId) throws LoociManagementException;
	
	/**
	 * Returns information on the requests property
	 * 
	 * @throws LoociManagementException
	 * 	The requested property is not available
	 */
	public PropertyInfo getPropertyInfo(short propertyId)  throws LoociManagementException;
	
	/**
	 * Sets a given property to a given value
	 * 
	 * @throws LoociManagementException
	 * 	The requested property is not available
	 * 	The requested property cannot be changed
	 */
	public void setProperty(short propertyId, byte[] propertyValue) throws LoociManagementException;
}

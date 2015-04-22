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
package looci.osgi.serv.impl;

import java.util.ArrayList;

import looci.osgi.serv.components.ComponentStateTypes;
import looci.osgi.serv.components.Event;
import looci.osgi.serv.components.IEventSource;
import looci.osgi.serv.components.IReceive;
import looci.osgi.serv.constants.ErrorCodes;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.impl.property.Property;
import looci.osgi.serv.impl.property.PropertyInfo;
import looci.osgi.serv.interfaces.ILoociCodebase;
import looci.osgi.serv.interfaces.ILoociComponent;


public abstract class LoociComponent implements ILoociComponent {


	private IReceive eventPublisher;
	private byte componentID;
	private byte componentState = ComponentStateTypes.INACTIVE;
	private LoociCodebase codebase;
	private EventQueue eventQueue;
	
	private ArrayList<Property> properties;
	
	public LoociComponent(){
		properties = new ArrayList<Property>();
		eventQueue = new EventQueue(this);
		eventQueue.start();
	}
	
	public EventQueue getReceiver(){
		return eventQueue;
	}
		
	public void init(byte componentID, IReceive reciever, LoociCodebase component){
		this.componentID = componentID;
		this.eventPublisher = reciever;
		this.codebase = component;
	}
	
	public boolean isActive(){
		return componentState == ComponentStateTypes.ACTIVE;
	}
	
	
	private void setState(byte state) {
		componentState = state;
	}

	public byte getState() {
		return componentState;
	}
	
	////////////////
	// Event publication
	////////////////
	
	protected void publish(Event event) {
		if (componentState == ComponentStateTypes.ACTIVE) {
			Event clone = event.clone();
			clone.setSourceComp(componentID);			
			eventPublisher.receive(clone, this);
		}
	}
	
	protected void publish(short eventId, byte[] content){
		Event e = new Event(eventId, content);
		publish(e);		
	}
		

	public short[] getInterfaces() {
		return codebase.getInterfaces();
	}
	
	/////////////
	// Event reception
	/////////////
	
	private Event receptionEvent;
	
	public Event getReceptionEvent(){
		return receptionEvent;
	}
	
	@Override
	public final void receive(Event ev,IEventSource source){
		receptionEvent = ev;
		receive(ev.getEventID(),ev.getPayload());
		receptionEvent = null;
	}
	
	
	public abstract void receive(short eventID, byte[] payload);
	
	
	public short[] getReceptacles(){
		return codebase.getReceptacles();
	}
	
	
	//////////////
	// Mgt
	/////////////
	
	public final void activate() {
		try{
			componentStart();	
			setState(ComponentStateTypes.ACTIVE);		
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public final void deactivate() {
		try{
			setState(ComponentStateTypes.INACTIVE);
			componentStop();			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public byte getComponentID() {
		return componentID;
	}

	public ILoociCodebase getCodebase() {
		return codebase;
	}
	
	
	public final void create() {
		codebase.addInstance(this);
		try{
			componentCreate();			
		} catch(Exception e){
			e.printStackTrace();
		}
	}


	public final void destroy() {
		try{
			componentDestroy();
		} catch(Exception e){
			e.printStackTrace();
		}
		eventQueue.stop();
		codebase.removeInstance(this);
	}
	
	/**
	 * Default implementations
	 * Can be overridden by implementations
	 */
	public short[] getProperties() {
		short[] temp = new short[properties.size()];
		for(int i = 0 ; i < temp.length ; i++){
			temp[i] = properties.get(i).getPropertyId();
		}		
		return temp;
	}

	protected Property getLocalProperty(short propertyId) throws LoociManagementException{
		Property temp;
		for(int i = 0 ; i < properties.size() ; i ++){
			temp = properties.get(i);
			if(temp.getPropertyId() == propertyId){
				return temp;
			}
		}throw new LoociManagementException(ErrorCodes.ERROR_CODE_PARAMETER_NOT_FOUND);
	}
	
	public byte[] getProperty(short propertyId) throws LoociManagementException{
		return getLocalProperty(propertyId).toByteArray();
	}

	public PropertyInfo getPropertyInfo(short propertyId) throws LoociManagementException{
		return getLocalProperty(propertyId).getInfo();
	}	
	
	public void setProperty(short propertyId, byte[] propertyValue) throws LoociManagementException {
		componentSetProperty(propertyId, propertyValue);
		getLocalProperty(propertyId).fromByteArray(propertyValue);
		componentAfterProperty(propertyId);
	}
	
	protected void addProperty(Property property){
		properties.add(property);
	}
	
	protected void removeProperty(Property property){
		properties.remove(property);
	}
	
	protected void componentCreate(){}
	
	protected void componentDestroy(){}
	
	protected void componentStart(){}

	protected void componentStop(){}
	
	protected void componentSetProperty(short propertyId, byte[] value) throws LoociManagementException{}
	
	protected void componentAfterProperty(short propertyId) throws LoociManagementException{}
	
}

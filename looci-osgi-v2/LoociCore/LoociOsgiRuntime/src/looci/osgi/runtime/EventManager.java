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
 * Copyright (c) 2010, Katholieke Universiteit Leuven
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Katholieke Universiteit Leuven nor the names of
 *       its contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package looci.osgi.runtime;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import looci.osgi.serv.bindings.Binding;
import looci.osgi.serv.bindings.LocalBinding;
import looci.osgi.serv.bindings.RemoteFromBinding;
import looci.osgi.serv.bindings.RemoteToBinding;
import looci.osgi.serv.components.ComponentTypes;
import looci.osgi.serv.components.Event;
import looci.osgi.serv.components.IEventSource;
import looci.osgi.serv.components.IReceive;
import looci.osgi.serv.constants.ErrorCodes;
import looci.osgi.serv.constants.EventTypes;
import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.impl.EventQueue;
import looci.osgi.serv.interception.IInterceptModule;
import looci.osgi.serv.interception.InterceptionModuleRegistration;
import looci.osgi.serv.log.LLog;



public class EventManager implements IEventSource {

	private EventQueue toNetworkQueue;
	private EventQueue fromNetworkQueue;
	private EventQueue fromLocalQueue;
	private NetworkComponent networkSender;
	
	private boolean isActive = true;

	private ArrayList<LocalBinding> localBindings = new ArrayList<LocalBinding>();
	private ArrayList<RemoteToBinding> remoteToBindings = new ArrayList<RemoteToBinding>();
	private ArrayList<RemoteFromBinding> remoteFromBindings = new ArrayList<RemoteFromBinding>();
	private HashMap<Byte, EventQueue> subscribers = new HashMap<Byte, EventQueue>();

	
	private ArrayList<InterceptionModuleRegistration> networkReceptionModules;
	private ArrayList<InterceptionModuleRegistration> networkSendModules;
	private ArrayList<InterceptionModuleRegistration> componentReceptionModules;
	private ArrayList<InterceptionModuleRegistration> componentSendModules;	
		
	public EventManager(NetworkComponent networkComp) {
		//networkQueue
		this.toNetworkQueue = new EventQueue(networkComp);
		
		networkReceptionModules = new ArrayList<InterceptionModuleRegistration>();
		networkSendModules = new ArrayList<InterceptionModuleRegistration>();
		componentReceptionModules = new ArrayList<InterceptionModuleRegistration>();
		componentSendModules = new ArrayList<InterceptionModuleRegistration>();
		networkSender = networkComp;
		
		//incomingEventQueue
		fromNetworkQueue = new EventQueue(
				new IReceive() {					
					@Override
					public void receive(Event event, IEventSource source) {
						receiveFromNetworkQueue(event);
					}
					
					@Override
					public boolean isActive() {
						return isActive;
					}
				}
			);
		
		fromLocalQueue = new EventQueue(
				new IReceive() {					
					@Override
					public void receive(Event event, IEventSource source) {
						receiveFromComponentQueue(event);
					}					
					@Override
					public boolean isActive() {
						return isActive;
					}
				});		

	}
	
	private void receiveFromNetworkQueue(Event e){
		if(e.getDestComp() == LoociConstants.COMPONENT_WILDCARD){
			for (int i = 0 ; i < remoteFromBindings.size(); i++) {
				RemoteFromBinding b = (RemoteFromBinding) remoteFromBindings.get(i);
				if (b.matches(e)) {
					Event e2 = (Event) e.clone();
					e2.setDestComp(b.getDestinationComponentID());
					sendEventToComponent(e2);
				}
			}
		} else{
			sendEventToComponent(e);
		}

	}
	
	private void receiveFromComponentQueue(Event event){
		if(event.destinationMatches(LoociConstants.ADDR_VOID)){
			publishLocal(event);
			publishRemoteTo(event);
		} else if(event.destinationMatches(LoociConstants.ADDR_LOCAL)){
			sendEventToComponent(event);
		} else{
			sendEventToNetwork(event);			
		}

	}

	public synchronized void addInterceptionRegistration(InterceptionModuleRegistration reg) {
		LLog.out(this,"adding interceptor at " +reg.getInterceptionPoint() + " from module " + reg.getName() + " prio " + reg.getPriority()  );
		
		ArrayList<InterceptionModuleRegistration> target;
		if(reg.getInterceptionPoint() == LoociConstants.INTERCEPT_FROM_COMPONENT){
			target = componentReceptionModules;
		} else if(reg.getInterceptionPoint() == LoociConstants.INTERCEPT_TO_COMPONENT){
			target = componentSendModules;
		}else if(reg.getInterceptionPoint() == LoociConstants.INTERCEPT_FROM_NETWORK){
			target = networkReceptionModules;
		}else if(reg.getInterceptionPoint() == LoociConstants.INTERCEPT_TO_NETWORK){
			target = networkSendModules;
		} else{
			return;
		}
		
		for(int i =0 ; i < target.size(); i ++){
			if(reg.getPriority() < target.get(i).getPriority()){
				target.add(i,reg);
				return;
			}
		}
		target.add(reg);
		return;
	}
	

	public void removeInterceptionRegistration(
			InterceptionModuleRegistration reg) {
		if(reg.getInterceptionPoint() == LoociConstants.INTERCEPT_FROM_COMPONENT){
			componentReceptionModules.remove(reg);
		} else if(reg.getInterceptionPoint() == LoociConstants.INTERCEPT_TO_COMPONENT){
			componentSendModules.remove(reg);
		}else if(reg.getInterceptionPoint() == LoociConstants.INTERCEPT_FROM_NETWORK){
			networkReceptionModules.remove(reg);
		}else if(reg.getInterceptionPoint() == LoociConstants.INTERCEPT_TO_NETWORK){
			networkSendModules.remove(reg);
		}
	}

	public void sendEventToComponent(Event event) {
		IReceive receiver = (IReceive)subscribers.get(new Byte(event.getDestComp()));
		if (receiver == null){
			return;
		}		
		boolean allowed_to_pass = true;
		for(int i = 0 ; i < componentSendModules.size() && allowed_to_pass ; i ++){
			IInterceptModule module = componentSendModules.get(i).getModule();
			allowed_to_pass = module.intercept(LoociConstants.INTERCEPT_TO_COMPONENT, event, this, receiver);
		}
		
		if (allowed_to_pass) {
			receiver.receive(event,this);
		}
	}
	

	public void sendEventToNetwork(Event event) {
		event.setSourceAddress(networkSender.getMyAddress());
		boolean allowed_to_pass = true;
		for(int i = 0 ; i < networkSendModules.size() && allowed_to_pass ; i ++){
			IInterceptModule module = networkSendModules.get(i).getModule();
			allowed_to_pass = module.intercept(LoociConstants.INTERCEPT_TO_NETWORK, event, this, networkSender);
		}
		
		if (allowed_to_pass) {
			LLog.out(this,"[EB] sending event to remote");
			toNetworkQueue.receive(event, this);
		}
	}
	

	public IReceive getNetworkReceiver(){
		return new IReceive(){			
			@Override
			public void receive(Event event, IEventSource source) {
				boolean allowed_to_pass = true;
				for(int i = 0 ; i < networkReceptionModules.size() && allowed_to_pass ; i ++){
					IInterceptModule module = networkReceptionModules.get(i).getModule();
					allowed_to_pass = module.intercept(LoociConstants.INTERCEPT_FROM_NETWORK, event, source, this);
				}				
				if (allowed_to_pass) {
					fromNetworkQueue.receive(event, source);
				}				
			}
			
			@Override
			public boolean isActive() {
				return isActive;
			}
		};
	}
	
	public IReceive getComponentReceiver(){
		return new IReceive(){			
			@Override
			public void receive(Event event, IEventSource source) {
				boolean allowed_to_pass = true;
				for(int i = 0 ; i < componentReceptionModules.size() && allowed_to_pass ; i ++){
					IInterceptModule module = componentReceptionModules.get(i).getModule();
					allowed_to_pass = module.intercept(LoociConstants.INTERCEPT_FROM_COMPONENT, event, source, this);
				}				
				if (allowed_to_pass) {
					fromLocalQueue.receive(event, source);
				} else{
					LLog.out(this,"[EB] lcl event blocked");					
				}
			}
			
			@Override
			public boolean isActive() {
				return isActive;
			}
		};
	}


	/**
	 * Publish the event to the remote subscribers.
	 * 
	 * @param event
	 */
	private void publishRemoteTo(Event e) {
		for (int i = 0 ; i < remoteToBindings.size(); i++) {
			RemoteToBinding b = (RemoteToBinding) remoteToBindings.get(i);
			if (b.matches(e)) {
				Event temp = (Event)e.clone();
				try {
					temp.setDestinationAddress(b.getDestinationNode());
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
				sendEventToNetwork(temp);
			}
		}
	}

	/**
	 * Publish the event to all local subscribers.
	 * 
	 * @param ev
	 */
	private void publishLocal(Event e) {
		for (int i = 0 ; i < localBindings.size(); i++) {
			LocalBinding b = (LocalBinding) localBindings.get(i);
			if (b.matches(e) && b.getDestinationComponentID() != e.getSourceComp()) {
				Event temp = (Event)e.clone();
				temp.setDestComp(b.getDestinationComponentID());
				sendEventToComponent(temp);
			}
		}
	}

	public void addLocalComponentBinding(LocalBinding b) throws LoociManagementException{
		if(!localBindings.contains(b)){
			localBindings.add(b);
			LLog.out(this,"[EM] lbin added " + b);
		} else{
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_WIRE_DUPLICATE);
		}
	}
	
	
	public void removeLocalComponentBinding(LocalBinding b) throws LoociManagementException {
		if(! localBindings.remove(b)){
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_WIRE_NOT_FOUND);
		}
	}

	public void addRemoteToComponentBinding(RemoteToBinding b) throws LoociManagementException{
		if(!remoteToBindings.contains(b)){
			remoteToBindings.add(b);
			LLog.out(this,"[EM] rtbin added " + b);
		}else{
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_WIRE_DUPLICATE);
		}
	}

	public void removeRemoteToComponentBinding(RemoteToBinding b) throws LoociManagementException{
		if(! remoteToBindings.remove(b)){
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_WIRE_NOT_FOUND);
		}
	}

	public void addRemoteFromComponentBinding(RemoteFromBinding b) throws LoociManagementException{
		if(!remoteFromBindings.contains(b)){
			remoteFromBindings.add(b);
			LLog.out(this,"[EM] rfbin added " + b);
		}else{
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_WIRE_DUPLICATE);
		}
	}

	public void removeRemoteFromComponentBinding(RemoteFromBinding b)throws LoociManagementException {
		if(!remoteFromBindings.remove(b)){
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_WIRE_NOT_FOUND);
		}
	}

	public void start() {
		isActive = true;
		fromLocalQueue.start();
		fromNetworkQueue.start();
		toNetworkQueue.start();
	}

	public void stop() {
		isActive = false;
		fromLocalQueue.stop();
		fromNetworkQueue.stop();
		toNetworkQueue.stop();
	}

	public LocalBinding[] getLocalWires(short eventID, byte srcCompID, byte dstCompID){
		ArrayList<LocalBinding> vals = new ArrayList<LocalBinding>();
		LocalBinding b;
		for (int i = 0 ; i < localBindings.size(); i++) {
			b = (LocalBinding) localBindings.get(i);
			if (((b.getDestinationComponentID() == dstCompID) || (dstCompID == ComponentTypes.COMPONENT_WILDCARD))
				&&((b.getSourceComponentID() == srcCompID) || (srcCompID == ComponentTypes.COMPONENT_WILDCARD))
					&& EventTypes.event_type_matches(b.getEventID(),eventID)) {
				vals.add(b);
			}
		}
		return (LocalBinding[]) vals.toArray(new LocalBinding[0]);
	}
	
	public RemoteToBinding[] getOutgoingRemoteWires(short eventID, byte srcCompID, String dstNodeID){
		ArrayList<RemoteToBinding> vals = new ArrayList<RemoteToBinding>();
		for(int i = 0 ; i < remoteToBindings.size() ; i++) {
			RemoteToBinding b = (RemoteToBinding)remoteToBindings.get(i);
			if ((b.getSourceComponentID() == srcCompID || srcCompID == ComponentTypes.COMPONENT_WILDCARD) 
					&& (b.getDestinationNode().equals(dstNodeID) || dstNodeID.equals(LoociConstants.ADDR_ANY)) 
					&& EventTypes.event_type_matches(b.getEventID(),eventID)){
				vals.add(b);
			}
		}
		return (RemoteToBinding[])vals.toArray(new RemoteToBinding[0]);
	}

	public RemoteFromBinding[] getIncomingRemoteWires(short eventID, byte srcCompID, String srcNodeID, byte dstCompID) {

		ArrayList<RemoteFromBinding> vals = new ArrayList<RemoteFromBinding>();
		for (int i =0 ; i < remoteFromBindings.size(); i++) {
			RemoteFromBinding b = (RemoteFromBinding)remoteFromBindings.get(i);
			if ((b.getDestinationComponentID() == dstCompID || dstCompID == ComponentTypes.COMPONENT_WILDCARD)
					&&(b.getSourceNode().equals(srcNodeID) || srcNodeID.equals(LoociConstants.ADDR_ANY)) 
					&&(b.getSourceComponentID() == srcCompID || srcCompID == ComponentTypes.COMPONENT_WILDCARD) 
					&& EventTypes.event_type_matches(b.getEventID(),eventID)) {
				vals.add(b);
			}
		}
		return (RemoteFromBinding[])vals.toArray(new RemoteFromBinding[0]);
	}

	public byte getComponentID() {
		return 0;
	}

	public void resetWirings(byte componentID) {
		Binding[] compBindings ;
		compBindings = getLocalWires(EventTypes.ANY_EVENT, componentID, ComponentTypes.COMPONENT_WILDCARD);
		for(int i  = 0 ; i < compBindings.length ; i ++){
			localBindings.remove(compBindings[i]);
		}
		compBindings = getLocalWires(EventTypes.ANY_EVENT,ComponentTypes.COMPONENT_WILDCARD, componentID);
		for(int i  = 0 ; i < compBindings.length ; i ++){
			localBindings.remove(compBindings[i]);
		}
		compBindings = getOutgoingRemoteWires(EventTypes.ANY_EVENT, componentID, LoociConstants.ADDR_ANY);
		for(int i  = 0 ; i < compBindings.length ; i ++){
			remoteToBindings.remove(compBindings[i]);
		}
		compBindings = getIncomingRemoteWires(EventTypes.ANY_EVENT,ComponentTypes.COMPONENT_WILDCARD, LoociConstants.ADDR_ANY,componentID);
		for(int i  = 0 ; i < compBindings.length ; i ++){
			remoteFromBindings.remove(compBindings[i]);
		}	
	}


	public void addLocalSubscriber(byte id, EventQueue subscriber) {
		subscribers.put(new Byte(id), subscriber);
	}

	public boolean removeLocalSubscriber(byte id) {
		resetWirings(id);
		IReceive queue = (IReceive)subscribers.remove(new Byte(id));
		return queue != null;
	}


	public IReceive getSubscriber(byte id){
		return (IReceive)subscribers.get(new Byte(id));
	}
	
	public boolean isActive() {
		return true;
	}


	public EventQueue getLocalQueue(){
		return fromLocalQueue;
	}
	
	public EventQueue getNetworkQueue(){
		return fromNetworkQueue;
	}

}

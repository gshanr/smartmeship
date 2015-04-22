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
package looci.osgi.mgtClient;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import looci.osgi.serv.components.Event;
import looci.osgi.serv.components.HeaderManagement;
import looci.osgi.serv.constants.EventTypes;
import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.impl.LoociComponent;
import looci.osgi.serv.log.LLog;
import looci.osgi.servExt.mgt.IEventSendAPI;
import looci.osgi.servExt.mgt.ILoociAPI;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


public class LoociMgtClientInstance extends LoociComponent  implements IEventSendAPI {

	private ArrayList<Event> eventBuffer = new ArrayList<Event>();
	private short sessionId;
	
	private ServiceRegistration apiReg;
	private ServiceRegistration senderReg;
	
	
	
	public LoociMgtClientInstance() {
		sessionId = 0;
	}	
	
	protected void componentCreate(){
		LLog.out(this,"starting looci client");
		
		try{
			BundleContext context = getCodebase().getBundleContext();
			LoociMgtAPI api = new LoociMgtAPI(this);
			apiReg = context.registerService(ILoociAPI.class.getName(), api, null);
			senderReg = context.registerService(IEventSendAPI.class.getName(),this,null);			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	protected void componentDestroy(){ 
		LLog.out(this,"Ending looci client");
		
		if(apiReg != null){
			apiReg.unregister();
		}
		if(senderReg != null){
			senderReg.unregister();
		}
	}


	@Override
	public void receive(short type, byte[] payload) {
		LLog.out(this,"[CLIENT] received ev: "+type);
		synchronized (eventBuffer) {
			eventBuffer.add(getReceptionEvent());
			eventBuffer.notifyAll();
		}
		new DelayDeleteEvent(getReceptionEvent(), 10).start();
	}
	
	private class DelayDeleteEvent extends Thread{
		
		private Event ev;
		private int delay;
		
		public DelayDeleteEvent(Event ev, int delay){
			this.ev = ev;
			this.delay = delay;
		}
		
		@Override
		public void run() {
			synchronized (this) {
				try {
					this.wait(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				eventBuffer.remove(ev);
			}
		}
	}

	private boolean eventMatches(Event event, String source, byte componentId, short eventId){
		
		if(!(event.sourceMatches(source))){
			return false;
		}
		if(event.getSourceComp() != componentId && !(componentId == LoociConstants.COMPONENT_WILDCARD)){
			return false;
		}
		if(event.getEventID() != eventId && ! (eventId == EventTypes.ANY_EVENT)){
			return false;
		}
		
		return true;
		
	}
	
	private boolean sessionMatches(Event request, Event reply){
		byte requestID = HeaderManagement.getTransactionID(request);
		byte replyID = HeaderManagement.getTransactionID(reply);
		return requestID == replyID;	
		
	}
	
	public Event sendEventAndWaitforReply(Event ev, short replyEvId, long timeout){
	
		byte thisId = (byte) sessionId;
		sessionId += 1;

		HeaderManagement.setNeedsReply(ev);
		HeaderManagement.setTransactionID(ev, thisId);
		publish(ev);
		Event reply = null;

		boolean goOn = true;
		boolean success = false;
		long endTime = System.currentTimeMillis() + timeout;
		
		synchronized(eventBuffer){
			while(goOn){
				{
					Iterator<Event> iter = eventBuffer.iterator();
					while(iter.hasNext()&& !success){
						reply = iter.next();
						if(eventMatches(reply,ev.getDestinationAddress(),ev.getDestComp(),replyEvId)
								|| sessionMatches(ev,reply)){
							success = true;
						}
					}
				}
				if(success){
					goOn = false;
					eventBuffer.remove(reply);
				} else{
					try {
						eventBuffer.wait(endTime - System.currentTimeMillis());
					} catch (InterruptedException e1) {						
					}					
					if(endTime <= System.currentTimeMillis()){
						reply = null;
						goOn = false;
					}
					
				}
			}			
		}		
		return reply;
	}

	
	@Override
	public void sendEvent(Event event) {
		publish(event);
	}

	@Override
	public List<Event> sendEventWaitAllReplies(Event ev, short replyEvId,
			long timeout) {
		byte thisId = (byte) sessionId;
		sessionId += 1;

		HeaderManagement.setNeedsReply(ev);
		HeaderManagement.setTransactionID(ev, thisId);
		publish(ev);
		List<Event> returnList = new ArrayList<Event>();

		boolean goOn = true;
		long endTime = System.currentTimeMillis() + timeout;
		
		synchronized(eventBuffer){
			while(goOn){
				{
					Iterator<Event> iter = eventBuffer.iterator();
					while(iter.hasNext()){
						Event reply = iter.next();
						if(eventMatches(reply,ev.getDestinationAddress(),ev.getDestComp(),replyEvId)
								|| sessionMatches(ev,reply)){
							returnList.add(reply.clone());
						}
					}
				}

				try {
					eventBuffer.wait(endTime - System.currentTimeMillis());
				} catch (InterruptedException e1) {						
				}					
				if(endTime <= System.currentTimeMillis()){
					goOn = false;
				}
				
			}			
		}		
		
		
		return returnList;		
	}

	

}

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
package looci.osgi.runtime;

import looci.osgi.serv.components.Event;
import looci.osgi.serv.components.HeaderManagement;
import looci.osgi.serv.components.IEventSource;
import looci.osgi.serv.components.IReceive;
import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.impl.LoociComponent;
import looci.osgi.serv.interception.IInterceptModule;

public class TargettingModule implements IInterceptModule, IEventSource{

	private EventManager manager;
	
	public TargettingModule(EventManager manager){
		this.manager = manager;
	}
	
	public boolean intercept(int interceptionPoint,Event event, IEventSource source, IReceive eventReceiver){
		if(interceptionPoint == LoociConstants.INTERCEPT_FROM_NETWORK){
			//Received event from network
			if(event.hasHeader(HeaderManagement.HEADER_TARGET)){
				byte comp = event.getHeaderByte(HeaderManagement.HEADER_TARGET, HeaderManagement.TARGET_COMP_BYTE);
				event.setDestComp(comp);				
			}
			return true;
			
		} else if(interceptionPoint == LoociConstants.INTERCEPT_FROM_COMPONENT){
			if(HeaderManagement.isDirected(event)){
				if(event.destinationMatches(LoociConstants.ADDR_LOCAL)){
					byte comp = event.getHeaderByte(HeaderManagement.HEADER_TARGET, HeaderManagement.TARGET_COMP_BYTE);
					event.setDestComp(comp);
				} 
				return true;
			} else if(source instanceof LoociComponent){
				//source is component, and not targetted
				LoociComponent sender = (LoociComponent) source;
				Event triggerEvent = sender.getReceptionEvent();
				if(triggerEvent != null){
					if(triggerEvent.hasHeader(HeaderManagement.HEADER_TARGET)){
						boolean needsReply = HeaderManagement.getNeedsReply(triggerEvent);
						if(needsReply){
							Event replyEvent = event.clone();
							HeaderManagement.createTargetReply(triggerEvent, replyEvent);
							replyEvent.setDestComp(triggerEvent.getSourceComp());
							manager.getLocalQueue().receive(replyEvent, this);
						} else{ 
						}
					}
				}
				return true;
			}
		}
		return true;
	}
	
	
	
}

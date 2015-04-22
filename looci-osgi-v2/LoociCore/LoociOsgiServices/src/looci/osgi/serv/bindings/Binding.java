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
package looci.osgi.serv.bindings;

import looci.osgi.serv.components.Event;
import looci.osgi.serv.constants.EventTypes;
import looci.osgi.serv.constants.LoociConstants;

/**
 * This class represents a component binding
 * 
 * @author nelson
 * 
 */
public class Binding {

	// event Id of the bindin
	private short eventID;
	
	//source component id to which this binding can match
	private byte srcComponentID;	

	/*
	 * public Binding(byte interfaceType, byte src_component_id, byte
	 * recep_type, byte dest_component_id) { this.iface_type = interfaceType;
	 * this.recep_type = recep_type; this.src_component_id = src_component_id;
	 * this.dest_component_id = dest_component_id; dest_host = "localhost";
	 * src_host = "localhost"; }
	 */

	public Binding(short eventID, byte srcComponentID){
		this.eventID = eventID;
		this.srcComponentID = srcComponentID;
	}


	/**
	 * Get the event type with which this binding matches
	 */
	public short getEventID() {
		return eventID;
	}

	/**
	 * Get the source component of this binding
	 */
	public byte getSourceComponentID() {
		return srcComponentID;
	}

	/**
	 * Check if the given event matches this binding.
	 * Checks both event type and component
	 */
	public boolean matches(Event e){
		return EventTypes.event_type_matches(eventID, e.getEventID())
				&& (e.getSourceComp() == srcComponentID
					|| srcComponentID == LoociConstants.COMPONENT_WILDCARD);
	}

	/**
	 * To string of binding
	 */
	public String toString() {
		return "( EV:"+ eventID + ", SRC_COMP:" + srcComponentID +")";
	}
	
	public boolean equals(Object obj){
		if(!(obj instanceof Binding)){
			return false;
		}
		Binding b = (Binding) obj;
		return (eventID == b.eventID) 
				&& (srcComponentID == b.srcComponentID);
	}
}

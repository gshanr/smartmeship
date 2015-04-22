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
 * Class representing a local LoOCi binding
 * 
 *
 */
public class LocalBinding extends Binding {
	
	//the local binding id
	private byte dstComponentID;

	
	public LocalBinding(short eventID, byte srcComponentID, byte dstComponentID){
		super(eventID,srcComponentID);
		this.dstComponentID = dstComponentID;
	}	
	
	/**
	 * Verify whether a given event matches the binding, and has to be handled according to this bindings rules
	 */
	public boolean matches(Event event){
		return EventTypes.event_type_matches(getEventID(), event.getEventID())
				&& (event.getSourceComp() == getSourceComponentID()
				|| (getSourceComponentID() == LoociConstants.COMPONENT_WILDCARD
					&& dstComponentID != event.getSourceComp())
				);
	}
	
	/**
	 * Get the destinatio componentid of this binding.
	 */
	public byte getDestinationComponentID() {
		return dstComponentID;
	}	
	
	/**
	 * Verify if two bindings are the same.
	 * This method returns true of the object is a binding,
	 * And the supers of the objects match, and the destination component ids match
	 */
	public boolean equals(Object obj){
		if(!(obj instanceof LocalBinding)){
			return false;
		}
		LocalBinding binding = (LocalBinding) obj;
		return super.equals(obj) && binding.getDestinationComponentID() == dstComponentID;
	}
}

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
package looci.osgi.app.testfilter;



import java.util.Random;

import looci.osgi.serv.components.Event;
import looci.osgi.serv.constants.EventTypes;
import looci.osgi.serv.impl.LoociComponent;
import looci.osgi.serv.impl.property.PropertyByte;
import looci.osgi.serv.util.ITimeListener;
import looci.osgi.serv.util.LoociTimer;


public class LoociFilterComponent extends LoociComponent {

	private Random rand;
	private LoociTimer timer;
	
	private PropertyByte thresHold;
	
	public LoociFilterComponent(){
		thresHold = new PropertyByte((short)1, "threshold", (byte)20);
		addProperty(thresHold);
		System.out.println("[Filter] created.");
	}
	
	
	
	@Override
	public void receive(short eventID, byte[] payload) {
		String output = "[Filter] Received a temperature reading and ";
		byte value = payload[0];
		if(value < thresHold.getVal()){
			publish(EventTypes.TEMP_READING,new byte[]{value});
			output += "and forwarded it.";
		} else {
			output += "and dropped it.";
		}
		System.out.println(output);
	}

	

}

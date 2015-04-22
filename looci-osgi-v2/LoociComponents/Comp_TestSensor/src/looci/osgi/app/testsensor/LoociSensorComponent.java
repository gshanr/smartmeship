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
package looci.osgi.app.testsensor;


import java.util.Random;

import looci.osgi.serv.components.Event;
import looci.osgi.serv.constants.EventTypes;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.impl.LoociComponent;
import looci.osgi.serv.impl.property.PropertyInteger;
import looci.osgi.serv.util.ITimeListener;
import looci.osgi.serv.util.LoociTimer;


public class LoociSensorComponent extends LoociComponent implements ITimeListener {

	private Random rand;
	private LoociTimer timer;
	private PropertyInteger intProp;
	
	
	public LoociSensorComponent(){
		intProp = new PropertyInteger((short)1, "interval", 10);
		timer = new LoociTimer(this, 1000*intProp.getVal(), true);
		addProperty(intProp);
	}
	
	public void componentStart(){
		System.out.println("[Test] Activate sensor");
		rand = new Random();
		timer.startRunning();
	}
	
	public void componentStop(){
		System.out.println("[Test] Deactivate sensor");
		timer.stopRunning();
	}
	
	
	@Override
	public void receive(short eventID, byte[] payload) {
		// do nothing
		
	}

	@Override
	public void doOnTimeEvent(LoociTimer expiredTimer) {
		System.out.println("[Sensor] Timer fired.");
		byte val = (byte)rand.nextInt();
		publish(EventTypes.TEMP_READING, new byte[]{val});		
	}

	
	
	protected void componentAfterProperty(short propertyId){
		if(propertyId == 1){
			timer.updateInterval(1000 * intProp.getVal());
		}
	}

}

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
package looci.osgi.app.sensorstub;

import java.util.Random;

import looci.osgi.serv.components.Event;
import looci.osgi.serv.constants.EventTypes;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.impl.LoociComponent;
import looci.osgi.serv.impl.property.PropertyShort;
import looci.osgi.serv.util.ITimeListener;
import looci.osgi.serv.util.LoociTimer;


public class LoociSensorStubInstance extends LoociComponent implements ITimeListener {

	private static final short SAMPLE_FREQUENCY = 30;
	
	private static final short MIN_SAMPLE = 31;
	private static final short MAX_SAMPLE = 32;
	private static final short SAMPLE_TYPE = 33;
	
	
	private PropertyShort sampleFreq;
	private PropertyShort minSample;
	private PropertyShort maxSample;
	private PropertyShort sampleType;
	private LoociTimer timer;
	private Random rng;
	
	
	public LoociSensorStubInstance() {
		sampleFreq = new PropertyShort(SAMPLE_FREQUENCY, "sample freq",(short) 10);
		minSample = new PropertyShort(MIN_SAMPLE, "min sample", (short) 0);
		maxSample = new PropertyShort(MAX_SAMPLE, "max sample", (short) 100);
		sampleType = new PropertyShort(SAMPLE_TYPE, "sample type", (short) EventTypes.TEMP_READING );
		addProperty(sampleFreq);
		addProperty(minSample);
		addProperty(maxSample);
		addProperty(sampleType);
	}
		
	public void receive(short eventId, byte[] payload) {}
	
	public void setProperty(short propertyId, byte[] propertyValue) throws LoociManagementException {
		super.setProperty(propertyId, propertyValue);
		if(propertyId == SAMPLE_FREQUENCY){
			timer.updateInterval(sampleFreq.getVal() * 1000);
		}
	}
	
	
	@Override
	protected void componentStart() {
		rng = new Random();
		timer = new LoociTimer(this, sampleFreq.getVal() * 1000,true);
		timer.startRunning();
	}
	
	@Override
	protected void componentStop() {
		timer.stopRunning();
		timer = null;
	}
	@Override
	public void doOnTimeEvent(LoociTimer timer) {
		int maxVal = (maxSample.getVal());
		int minVal = minSample.getVal();
		int type = sampleType.getVal();
		
		int val = rng.nextInt(maxVal-minVal)+minVal;
		
		
		Event e = new Event((short)type, new byte[]{(byte)val});
		
		publish(e);			
	}

}

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
 * Copyright (c) 2012, Katholieke Universiteit Leuven
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

package tempSensor;

import java.util.Random;

import looci.osgi.serv.constants.EventTypes;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.impl.LoociComponent;
import looci.osgi.serv.impl.property.PropertyInteger;
import looci.osgi.serv.util.ITimeListener;
import looci.osgi.serv.util.LoociTimer;

/**
 * 
 *
 * @author 
 * @version 1.0
 * @since 2012-01-01
 *
 */

public class tempSensorComp extends LoociComponent  implements ITimeListener{
	
    /**
     * Holds the parent codebase
     */
    private tempSensor _parent;
    private LoociTimer timer;
    private PropertyInteger delay;

	private Random rand;


	/**
	 * LooCIComponent(<name>, <interfaces>, <receptacles>);
	 */
    public tempSensorComp(tempSensor parent) {
        _parent = parent;
        delay = new PropertyInteger((short) 1, "interval", 10);
        timer = new LoociTimer(this, delay.getVal() * 1000, true);
        addProperty(delay);
        rand = new Random();
    }

    @Override
    public void receive(short event_id, byte[] payload) {		
        // handle receptacles
    }
    
    @Override
    public void componentStart() {
       timer.startRunning();
    }

    @Override
    public void componentStop() {
    	timer.stopRunning();
    }

	@Override
	public void doOnTimeEvent(LoociTimer arg0) {
		byte val = (byte)rand.nextInt();
		publish(EventTypes.TEMP_READING, new byte[]{val});
	}
    
	protected void componentAfterProperty(short propertyId) throws LoociManagementException{
		if(propertyId == delay.getPropertyId()){
			timer.updateInterval(delay.getVal() * 1000);
		}
	}



}

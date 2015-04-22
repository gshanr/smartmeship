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
package looci.osgi.app.sensorDisplay;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import looci.osgi.serv.components.Event;
import looci.osgi.serv.constants.EventTypes;
import looci.osgi.serv.impl.LoociComponent;


public class TempGraphInstance extends LoociComponent{

	
    private JTextArea status;
    private String[] addresses = new String[8];
    private DataWindow[] plots = new DataWindow[8];
    

    
	
	@Override
    public void receive(short eventType, byte[] payload) {
		System.out.println("[Tempgraph] received event "+ eventType);
		Event event = getReceptionEvent();
		if(event.getEventID() == EventTypes.TEMP_READING){
	        System.out.println("[TempGraph] Recieved temp event. , painting");
	        int value = event.getPayload()[0];

	        DataWindow dw = findPlot(event.getSourceAddress());
	        long time = System.currentTimeMillis();      // read time of the reading
	        dw.addData(time, value);
		} else  if(event.getEventID() == EventTypes.SWITCH_READING){
			 System.out.println("[TempGraph] Recieved door event. , painting");
		        
			boolean value = (event.getPayload()[0] == 1);
			DataWindow dw = findPlot(event.getSourceAddress());
			dw.setDoor(value);
		}else  if(event.getEventID() == EventTypes.BUTTON_READING){
			System.out.println("[TempGraph] Recieved motion event. , painting");
		    DataWindow dw = findPlot(event.getSourceAddress());
			dw.setMotion(true,payload[0]);
		}

    }

    private DataWindow findPlot(String addr) {
        for (int i = 0; i < addresses.length; i++) {
            if (addresses[i].equals("")) {                
                status.append("Received packet from : " + addr + "\n");
                addresses[i] = addr;
                plots[i] = new DataWindow(addr);
                final int ii = i;
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        plots[ii].setVisible(true);
                    }
                });
                return plots[i];
            }
            if (addresses[i].equals(addr)) {
                return plots[i];
            }
        }
        return plots[0];
    }

	private JFrame fr;
	
	protected void componentCreate(){
		System.out.println("created instance");
	     fr = new JFrame("TempGraph");
	        status = new JTextArea();
	        JScrollPane sp = new JScrollPane(status);
	        fr.add(sp);
	        fr.setSize(360, 200);
	        fr.validate();
	        fr.setVisible(true);
	        for (int i = 0; i < addresses.length; i++) {
	            addresses[i] = "";
	            plots[i] = null;
	        }
		
	}
	
	
	protected void componentDestroy(){
		for(int i = 0; i < plots.length; i++){
			if(plots[i] != null){
				plots[i].killAll();
			}
		}
		fr.setVisible(false);
		fr.dispose();
	}
	
	protected void componentStart(){ 
		System.out.println("started instance");
		
	}
    
}

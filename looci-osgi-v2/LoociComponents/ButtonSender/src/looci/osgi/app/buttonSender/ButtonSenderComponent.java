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
package looci.osgi.app.buttonSender;



import looci.osgi.serv.constants.EventTypes;
import looci.osgi.serv.impl.LoociComponent;
import looci.osgi.serv.impl.property.PropertyByte;


public class ButtonSenderComponent extends LoociComponent {


	private ButtonSenderWindow window;
	
	public ButtonSenderComponent() {

		window = new ButtonSenderWindow(this);
	}
	

	@Override
	public void componentStart(){
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                window.setVisible(true);
            }
        });
	}
	
	@Override
	public void componentStop(){
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                window.setVisible(false);
            }
        });
	}
	
	@Override
	public void receive(short eventID, byte[] payload) {
		// do nothing
		
	}

	
	public void buttonPressed(){
		System.out.println("Button pressed");
		// to publish an event: publish(type,payload);
		// Relevent event type: EventTypes.BUTTON_READING
	}
	
	public void tempReadingDone(byte reading){
		//send out temp reading
		System.out.println("temp reading: "+reading);
	}
	

}

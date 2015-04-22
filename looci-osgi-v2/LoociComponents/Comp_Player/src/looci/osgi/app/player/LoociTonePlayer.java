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
package looci.osgi.app.player;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import looci.osgi.gui.serv.GuiInterface;
import looci.osgi.serv.impl.LoociCodebase;
import looci.osgi.serv.interfaces.ILoociComponent;



/**
 * The activator class controls the plug-in life cycle
 */
public class LoociTonePlayer extends LoociCodebase {
	
	public static final short TONE_EVENT = (short) 403;
	
	private GuiInterface gui;
	
	public LoociTonePlayer() throws Exception{
		super("tone player",
				new short[] { 
				TONE_EVENT},
				new short[] {});
	}
	
	protected void componentStart(){
		
		ServiceReference[] guiRefs;
		try {
			guiRefs = getBundleContext().getServiceReferences(GuiInterface.class.getName(),null);
			if(guiRefs == null || guiRefs.length==0){
				System.out.println("no LooCI gui found");
				throw new IllegalStateException();
			}
			gui = (GuiInterface) getBundleContext().getService(guiRefs[0]);
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

				
	}

	@Override
	public ILoociComponent createLoociComponent() {
		return new LoociTonePlayerComponent(gui);
	}	
}

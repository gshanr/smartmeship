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
package looci.osgi.mgtGui;


import looci.osgi.gui.serv.GuiInterface;
import looci.osgi.servExt.mgt.IDeployerAPI;
import looci.osgi.servExt.mgt.ILoociAPI;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;




/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator{

	// The plug-in ID
	public static final String PLUGIN_ID = "LoociOsgiMgtGui"; //$NON-NLS-1$
	public BundleContext context;
	private ILoociAPI loociApi;
	private IDeployerAPI deployerApi;
	private GuiInterface gui;
	private LoociClient client;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		

		this.context = context;
		
		ServiceReference[] guiRefs = context.getServiceReferences(GuiInterface.class.getName(),null);
		if(guiRefs == null || guiRefs.length==0){
			System.out.println("no LooCI gui found");
			throw new IllegalStateException();
		}
		ServiceReference[] apiRefs = context.getServiceReferences(ILoociAPI.class.getName(),null);
		if(apiRefs == null || apiRefs.length==0){
			System.out.println("no LooCI mgt API found");
			throw new IllegalStateException();
		}

		ServiceReference[] deployRefs = context.getServiceReferences(IDeployerAPI.class.getName(),null);
		if(deployRefs == null || deployRefs.length==0){
			System.out.println("no LooCI deployment API found");
			throw new IllegalStateException();
		}
		
		
		loociApi = (ILoociAPI) context.getService(apiRefs[0]);
		deployerApi = (IDeployerAPI) context.getService(deployRefs[0]);
		gui = (GuiInterface) context.getService(guiRefs[0]);
		
		
		
		client = new LoociClient(loociApi, deployerApi, gui);
		client.startClient();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		client.stopClient();
	}


}

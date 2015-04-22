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
package looci.osgi.visualizer;


import looci.osgi.gui.serv.GuiInterface;
import looci.osgi.servExt.mgt.ILoociAPI;
import looci.osgi.servExt.mgt.IServiceClient;
import looci.osgi.visualizer.lib.Principal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Visualizer implements BundleActivator {

	
	public BundleContext context;
	private ILoociAPI loociApi;
	private IServiceClient servAPI;
	
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

		ServiceReference[] servClient = context.getServiceReferences(IServiceClient.class.getName(),null);
		if(servClient == null || servClient.length==0){
			System.out.println("no service client API found");
			throw new IllegalStateException();
		}
		
		
		loociApi = (ILoociAPI) context.getService(apiRefs[0]);
		servAPI = (IServiceClient) context.getService(servClient[0]);
		
		Principal principal =new Principal(loociApi);
		VisualizerCommandList cmd = new VisualizerCommandList(principal);
		servAPI.addCommandList(cmd);
		
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		
	}

}

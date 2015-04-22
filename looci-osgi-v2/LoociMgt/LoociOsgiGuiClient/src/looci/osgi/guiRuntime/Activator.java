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
package looci.osgi.guiRuntime;


import looci.osgi.gui.serv.GuiInterface;
import looci.osgi.serv.log.LLog;
import looci.osgi.servExt.mgt.IDeployerAPI;
import looci.osgi.servExt.mgt.ILoociAPI;
import looci.osgi.servExt.mgt.IServiceClient;
import looci.osgi.servExt.mgt.LoociCommandList;
import looci.osgi.servExt.mgt.ServiceClient;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;



public class Activator implements BundleActivator {

	private static BundleContext context;

	private GuiHandler handler;
	private ILoociAPI loociApi;
	private IDeployerAPI deployerApi;
	private ServiceRegistration registration;
	private ServiceRegistration registrationServiceClient;
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		LLog.out(this, "received start");
		/*GuiHandler*/ handler = new GuiHandler();
		
		Activator.context = bundleContext;
		
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
		
		loociApi = (ILoociAPI) Activator.context.getService(apiRefs[0]);
		deployerApi = (IDeployerAPI) Activator.context.getService(deployRefs[0]);
		
		LoociCommandList loociCommands = new LoociCommandList(deployerApi, loociApi);
		ServiceClient client = new ServiceClient(loociCommands);
		
		
		handler.addComponent(new CommandShell(client), "cmd", "looci shell");
				
		
		registration = context.registerService(GuiInterface.class.getName(), handler, null);
		registrationServiceClient = context.registerService(IServiceClient.class.getName(), client, null);
		LLog.out(this, "start complete");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		LLog.out(this, "received end");
		Activator.context = null;
		registration.unregister();
		registrationServiceClient.unregister();
		handler.end();
		LLog.out(this, "end completed");
	}
	

}

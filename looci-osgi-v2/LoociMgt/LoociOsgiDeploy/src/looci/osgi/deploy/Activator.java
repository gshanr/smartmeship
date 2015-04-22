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
package looci.osgi.deploy;


import looci.osgi.servExt.mgt.IDeployerAPI;
import looci.osgi.servExt.mgt.IDeploymentAPI;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;


public class Activator implements BundleActivator,ServiceListener {

	private static BundleContext context;

	private LooCIDeployer deployer;
	
	private ServiceRegistration deployerRegistration;
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;	
		listenToDeployers();
		registerService();		
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		unregisterService();		
		Activator.context = null;
	}

	/////////////////////////////////////////////////////
	

	
	private void registerService(){
		deployer = new LooCIDeployer();
		deployerRegistration = Activator.context.registerService(IDeployerAPI.class.getName(), deployer, null);
	}
	
	private void unregisterService(){
		deployerRegistration.unregister();
	}
	
	private void listenToDeployers(){
		String filter = "(objectclass=" + IDeploymentAPI.class.getName() + ")";
		try {
			context.addServiceListener(this,filter);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		if(event.getType() == ServiceEvent.REGISTERED) {
			ServiceReference ref = event.getServiceReference();
			Object o = context.getService(ref);
			if(o instanceof IDeploymentAPI){
				deployer.addDeployer((IDeploymentAPI)o);
			}
		}
		else if(event.getType() == ServiceEvent.UNREGISTERING) {
			ServiceReference ref = event.getServiceReference();
			Object o = context.getService(ref);
			if(o instanceof IDeploymentAPI){
				deployer.removeDeployer((IDeploymentAPI)o);
			}
		}		
	}
}

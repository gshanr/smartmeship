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
package looci.osgi.deploy.avrraven;


import looci.osgi.serv.constants.LoociRuntimes;
import looci.osgi.servExt.mgt.IDeploymentAPI;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


public class Activator implements BundleActivator {

	private static BundleContext context;

	private LoociContikiDeployAPI deployerRaven;
	private LoociContikiDeployAPI deployerZigduino;
	private ServiceRegistration reg;
	private ServiceRegistration regZigduino;
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;		
		deployerRaven = new LoociContikiDeployAPI(
				LoociRuntimes.RUNTIME_RAVEN,
				5000,
				60000);
		deployerZigduino = new LoociContikiDeployAPI(
				LoociRuntimes.RUNTIME_ZIGDUINO,
				10000,
				60000);
		reg = context.registerService(IDeploymentAPI.class.getName(), deployerRaven, null);
		regZigduino = context.registerService(IDeploymentAPI.class.getName(), deployerZigduino, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		reg.unregister();
		regZigduino.unregister();
		Activator.context = null;
	}

}

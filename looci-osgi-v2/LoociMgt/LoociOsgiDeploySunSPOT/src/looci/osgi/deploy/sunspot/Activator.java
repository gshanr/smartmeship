/**
 * LooCI Copyright (C) 2013 KU Leuven.
 * All rights reserved.
 *
 * LooCI is an open-source software development kit for developing and 
 * maintaining networked embedded applications;
 * it is distributed under a dual-use software license model:
 *
 * 1. Non-commercial use:
 * Non-Profits, Academic Institutions, and Private Individuals can redistribute 
 * and/or modify LooCI code under the terms of the GNU General Public License 
 * version 3, as published by the Free Software Foundation
 * (http://www.gnu.org/licenses/gpl.html).
 *
 * 2. Commercial use:
 * In order to apply LooCI in commercial code, a dedicated software license must 
 * be negotiated with KU Leuven Research & Development.
 *
 * Contact information:
 *  Administrative Contact: Sam Michiels, sam.michiels@cs.kuleuven.be
 *  Technical Contact:           Danny Hughes, danny.hughes@cs.kuleuven.be
 * Address:
 *  iMinds-DistriNet, KU Leuven
 *  Celestijnenlaan 200A - PB 2402,
 *  B-3001 Leuven,
 *  BELGIUM. 
 **/

package looci.osgi.deploy.sunspot;

import looci.osgi.deploy.sunspot.LoociSunSPOTDeploymentAPI;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.ServiceReference;

import looci.osgi.servExt.mgt.IDeploymentAPI;
import looci.osgi.servExt.mgt.ILoociAPI;

public class Activator implements BundleActivator {

    private static BundleContext context;
    private LoociSunSPOTDeploymentAPI deployer;
    private ServiceRegistration reg;
    private ILoociAPI loociApi;

    static BundleContext getContext() {
        return context;
    }

    /*
     * (non-Javadoc) @see
     * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext bundleContext) throws Exception {
        //System.out.println("[SUNSPOT DEPLOYER - ACTIVATOR] start()");
        context = bundleContext;
        
        
        ServiceReference[] apiRefs = context.getServiceReferences(ILoociAPI.class.getName(),null);
        if(apiRefs == null || apiRefs.length == 0){
            System.out.println("[SUNSPOT DEPLOYER - ACTIVATOR] No LooCI mgt API found.");
            throw new IllegalStateException();
        }
        
        loociApi = (ILoociAPI)context.getService(apiRefs[0]);
        
        deployer = new LoociSunSPOTDeploymentAPI(loociApi);
        reg = context.registerService(IDeploymentAPI.class.getName(), deployer, null);
    }

    /*
     * (non-Javadoc) @see
     * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext bundleContext) throws Exception {
        //System.out.println("[SUNSPOT DEPLOYER - ACTIVATOR] stop()");
        reg.unregister();
        Activator.context = null;
    }
}

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
 * Copyright (c) 2010, Katholieke Universiteit Leuven
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

package looci.osgi.runtime;


import looci.osgi.runtime.deployment.DeploymentServer;
import looci.osgi.runtime.reconfiguration.IntrospectionEngine;
import looci.osgi.runtime.reconfiguration.ReconfigEngineCB;
import looci.osgi.runtime.reconfiguration.ReconfigurationEngine;
import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.interception.IInspectProxy;
import looci.osgi.serv.interception.IProxyInjectorService;
import looci.osgi.serv.interception.IReconfigureProxy;
import looci.osgi.serv.interception.InterceptionModuleRegistration;
import looci.osgi.serv.interfaces.ICodebaseManager;
import looci.osgi.serv.interfaces.IInspect;
import looci.osgi.serv.interfaces.IReconfigure;
import looci.osgi.serv.log.LLog;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;


/**
 * The LooCI osgi runtime.
 *  
 */
public class LoociRuntime implements BundleActivator, ServiceListener, IProxyInjectorService {

	private EventManager eventManager;
	private CodebaseManager cManager;
	private ComponentManager iManager;
	private NetworkComponent networkComp;

	private ReconfigurationEngine reconfigurationEngine;
	private IntrospectionEngine introspectionEngine;
	private BundleContext context;
	
	private DeploymentServer deployServer;
	private ReconfigEngineCB reconfigComp;
	
	private ServiceRegistration inspectReg;
	private ServiceRegistration reconfigReg;
	private ServiceRegistration compManReg;
	private ServiceRegistration proxyReg;
	
	
	public LoociRuntime() {
		System.out.println("--LooCI--");
	}

	public void start(BundleContext context) throws Exception {
		LLog.out(this,"[LooCI Runtime] initializing");
		this.context = context;
		initialize();
		networkComp.start();
		eventManager.start();
		
		String filter = "(objectclass=" + InterceptionModuleRegistration.class.getName() + ")";
		try{
			context.addServiceListener(this,filter);			
		} catch(Exception e){
			
		}
		LLog.out(this,"[LooCI Runtime] started");
	}

	public void stop(BundleContext context) throws Exception {
		LLog.out(this,"received stop request");
		eventManager.stop();
		networkComp.stop();
		endBundle();
		LLog.out(this,"[LooCI Runtime] stopped");
	}

	
	
	private void loadSwComponents(){		
		networkComp = new NetworkComponent();		
		eventManager = new EventManager(networkComp);
		registerTargettingModule();
		networkComp.registerReceiver(eventManager.getNetworkReceiver());
		
		iManager = new ComponentManager(eventManager);
		cManager = new CodebaseManager(iManager);
		
		reconfigurationEngine = new ReconfigurationEngine(eventManager, cManager, iManager);
		introspectionEngine = new IntrospectionEngine(eventManager, cManager,	iManager);
		
	}
	
	private void registerTargettingModule(){
		TargettingModule targetter = new TargettingModule(eventManager);
		InterceptionModuleRegistration reg1 = new InterceptionModuleRegistration(
				targetter, 
				LoociConstants.INTERCEPT_FROM_COMPONENT,
				(short) 255);
		InterceptionModuleRegistration reg2 = new InterceptionModuleRegistration(
				targetter, 
				LoociConstants.INTERCEPT_FROM_NETWORK,
				(short) 255);
		eventManager.addInterceptionRegistration(reg1);
		eventManager.addInterceptionRegistration(reg2);
		
	}
	
	
	
	private void registerServices(){
		//register codebase manager to allow bundles to register
		compManReg = context.registerService(ICodebaseManager.class.getName(), cManager,null);
		reconfigReg = context.registerService(IReconfigure.class.getName(),	reconfigurationEngine, null);
		inspectReg = context.registerService(IInspect.class.getName(), introspectionEngine, null);
		proxyReg = context.registerService(IProxyInjectorService.class.getName(),this,null);
	}
	
	
	
	private void loadServers() throws Exception{		
		reconfigComp = new ReconfigEngineCB(reconfigurationEngine, introspectionEngine);
		reconfigComp.start(context);

		deployServer = new DeploymentServer(context);
		deployServer.start();
	}
	
	
	
	/**
	 * Initializes the OSGi platform 
	 * It loads the config from the config file
	 * It attempts to get the policy manager
	 * it loads the different SW components
	 * It registers these components on the OSGi service bus
	 * 
	 * If need for security, it loads the security components and the authorization components
	 * Else, it starts the normal reconfig components.
	 * 
	 * @throws Exception
	 */
	private void initialize() throws Exception {
	
		loadSwComponents();		
		registerServices();		
		loadServers();
		
	}
	
	private void endBundle(){
		compManReg.unregister();
		reconfigReg.unregister();
		inspectReg.unregister();
		proxyReg.unregister();
		deployServer.stop();
	}


	// /////////////
	// OSGi specific stuff
	// /////////////



	
	//////////////////////////
	//  Interception stuff
	//////////////////////////
	
	private IInspectProxy introIntercept;
	private IReconfigureProxy reconfigIntercept;
	
	public synchronized void serviceChanged(ServiceEvent event) {
		if(event.getServiceReference().isAssignableTo(context.getBundle(), InterceptionModuleRegistration.class.getName())){
			interceptServiceChanged(event);
		} 
	}
	
	public void interceptServiceChanged(ServiceEvent event){
		if(event.getType() == ServiceEvent.REGISTERED) {
			ServiceReference ref = event.getServiceReference();
			InterceptionModuleRegistration reg = (InterceptionModuleRegistration) context.getService(ref);
			eventManager.addInterceptionRegistration(reg);
			LLog.out(this,"[LooCI Runtime] added interception registrationg");
		}else if (event.getType() == ServiceEvent.UNREGISTERING){
			ServiceReference ref = event.getServiceReference();
			InterceptionModuleRegistration reg = (InterceptionModuleRegistration) context.getService(ref);
			eventManager.removeInterceptionRegistration(reg);
			LLog.out(this,"[LooCI Runtime] added interception registrationg");
			LLog.out(this,"[LooCI Runtime] Disconnected from Policy Framework");

		}
	}	
	
	
	public void registerInspectProxy(IInspectProxy inspectProxy){
		introIntercept = inspectProxy;
		reconfigComp.getManagementInstance().setInspect(introIntercept);
		introIntercept.init(introspectionEngine, reconfigComp.getComponents().get(0));
		LLog.out(this,"[LooCI Runtime] registered inspectProxy");
	}
	
	public void unregisterInspectProxy(){
		introIntercept = null;
		reconfigComp.getManagementInstance().setInspect(introspectionEngine);	

		LLog.out(this,"[LooCI Runtime] unregistered inspectProxy");
	}
	
	public void registerReconfigProxy(IReconfigureProxy reconfigProxy){

		reconfigIntercept = reconfigProxy;
		reconfigComp.getManagementInstance().setReconfigure(reconfigIntercept);
		reconfigIntercept.init(reconfigurationEngine, reconfigComp.getComponents().get(0));

		LLog.out(this,"[LooCI Runtime] registered reconfigProxy");
	}
	public void unregisterReconfigProxy(){
		reconfigIntercept = null;
		reconfigComp.getManagementInstance().setReconfigure(reconfigurationEngine);	

		LLog.out(this,"[LooCI Runtime] unregistered reconfigProxy");
	}

	@Override
	public void registerInterceptModule(
			InterceptionModuleRegistration registration) {
		eventManager.addInterceptionRegistration(registration);
	}

	@Override
	public void unregisterCommInterceptor(
			InterceptionModuleRegistration registration) {
		eventManager.removeInterceptionRegistration(registration);
	}
}

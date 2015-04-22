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
package looci.osgi.eventDebug;

import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.interception.IProxyInjectorService;
import looci.osgi.serv.interception.InterceptionModuleRegistration;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class EventDebugger implements BundleActivator {

	IProxyInjectorService proxyInject;
	
	private InterceptionModuleRegistration regPre1;
	private InterceptionModuleRegistration regPre2;
	private InterceptionModuleRegistration regPre3;
	private InterceptionModuleRegistration regPre4;
	

	private InterceptionModuleRegistration regPost1;
	private InterceptionModuleRegistration regPost2;
	private InterceptionModuleRegistration regPost3;
	private InterceptionModuleRegistration regPost4;
	
	@Override
	public void start(BundleContext context) throws Exception {
		ServiceReference[] refs4 = context.getServiceReferences(IProxyInjectorService.class.getName(), null);
		proxyInject = (IProxyInjectorService) context.getService(refs4[0]);
		
		EventInterceptorPre evIntPre = new EventInterceptorPre();
		EventInterceptorPost evIntPost = new EventInterceptorPost();
		
		 regPre1 = new InterceptionModuleRegistration(evIntPre, LoociConstants.INTERCEPT_FROM_COMPONENT, (short)0);
		 regPre2 = new InterceptionModuleRegistration(evIntPre, LoociConstants.INTERCEPT_FROM_NETWORK,(short) 0);
		 regPre3 = new InterceptionModuleRegistration(evIntPre, LoociConstants.INTERCEPT_TO_COMPONENT,(short) 0);
		 regPre4 = new InterceptionModuleRegistration(evIntPre, LoociConstants.INTERCEPT_TO_NETWORK, (short)0);
		
		 regPost1 = new InterceptionModuleRegistration(evIntPost, LoociConstants.INTERCEPT_FROM_COMPONENT,(short)255);
		 regPost2 = new InterceptionModuleRegistration(evIntPost, LoociConstants.INTERCEPT_FROM_NETWORK,(short) 255);
		 regPost3 = new InterceptionModuleRegistration(evIntPost, LoociConstants.INTERCEPT_TO_COMPONENT,(short) 255);
		 regPost4 = new InterceptionModuleRegistration(evIntPost, LoociConstants.INTERCEPT_TO_NETWORK, (short)255);
		
		
		proxyInject.registerInterceptModule(regPre1);
		proxyInject.registerInterceptModule(regPre2);
		proxyInject.registerInterceptModule(regPre3);
		proxyInject.registerInterceptModule(regPre4);
		

		proxyInject.registerInterceptModule(regPost1);
		proxyInject.registerInterceptModule(regPost2);
		proxyInject.registerInterceptModule(regPost3);
		proxyInject.registerInterceptModule(regPost4);
		
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		proxyInject.unregisterCommInterceptor(regPre1);
		proxyInject.unregisterCommInterceptor(regPre2);
		proxyInject.unregisterCommInterceptor(regPre3);
		proxyInject.unregisterCommInterceptor(regPre4);
		proxyInject.unregisterCommInterceptor(regPost1);
		proxyInject.unregisterCommInterceptor(regPost2);
		proxyInject.unregisterCommInterceptor(regPost3);
		proxyInject.unregisterCommInterceptor(regPost4);
	}

}

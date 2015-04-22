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
package looci.osgi.deploy.osgi;

import java.util.Map;

import looci.osgi.serv.constants.LoociRuntimes;
import looci.osgi.servExt.appInfo.LoociNodeInfo;
import looci.osgi.servExt.mgt.IDeploymentAPI;
import looci.osgi.servExt.mgt.TextObserver;


public class LoociOsgiDeploymentAPI implements IDeploymentAPI{

	public static final int TIME_OUT = 10;
	
	public byte deploy(String componentFile, String address) throws Exception{
		
		
		LoociOsgiDeployer deployer = new LoociOsgiDeployer(address, componentFile);
		new DelayThread(deployer).start();
		return deployer.execute();
	}
	
	@Override
	public byte deploy(String componentFile, LoociNodeInfo nodeId,TextObserver callback,  Map<String, String> parameters) throws Exception{
		return deploy(componentFile,nodeId.getNodeIP());
	}

	@Override
	public String getTargettedPlatform() {
		return LoociRuntimes.RUNTIME_OSGI;
	}
	
	private class DelayThread extends Thread {

		private LoociOsgiDeployer deployer;
		
		public DelayThread(LoociOsgiDeployer deployer){
			this.deployer = deployer;
		}
		
		@Override
		public void run() {
			synchronized (this) {
				try {
					wait(1000*TIME_OUT);
				} catch (InterruptedException e) {
				}
				if(deployer.isRunning()){
					deployer.timeOut();
				}
			}
		}
		
	}
	
	
}

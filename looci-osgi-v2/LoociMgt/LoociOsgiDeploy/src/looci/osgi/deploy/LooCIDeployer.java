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

import java.io.File;
import java.util.Map;
import java.util.HashMap;

import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.servExt.appInfo.LoociNodeInfo;
import looci.osgi.servExt.mgt.IDeployerAPI;
import looci.osgi.servExt.mgt.IDeploymentAPI;
import looci.osgi.servExt.mgt.TextObserver;


public class LooCIDeployer implements IDeployerAPI {

	
	private HashMap<String,IDeploymentAPI> deployers;
	
	public LooCIDeployer(){
		deployers = new HashMap<String, IDeploymentAPI>();
	}
	
	public void addDeployer(IDeploymentAPI deployer){
		deployers.put(deployer.getTargettedPlatform(),deployer);
	}
	
	public void removeDeployer(IDeploymentAPI deployer){
		deployers.remove(deployer.getTargettedPlatform());
	}	
	
	
	@Override
	public byte deploy(String nodeType, String componentFile, String nodeId) throws Exception {
		IDeploymentAPI api = deployers.get(nodeType);
		if(api != null){
			File f = new File(componentFile);
			if(!f.isAbsolute()){
				componentFile =  LoociConstants.COMPONENT_DIR+"/"+componentFile;
			}
			return api.deploy(componentFile, nodeId);
		} else{
			throw new Exception("deployer not found for selected node type");
		}
	}

	@Override
	public byte deploy(String nodeType, String componentFile,
			LoociNodeInfo nodeId, TextObserver callback, Map<String, String> parameters)  throws Exception{
		IDeploymentAPI api = deployers.get(nodeType);
		if(api != null){
			File f = new File(componentFile);
			if(!f.isAbsolute()){
				componentFile = LoociConstants.COMPONENT_DIR+"/"+componentFile;
			}
			return api.deploy(componentFile, nodeId,callback,parameters);
		} else{
			throw new Exception("deployer not found for selected node type");
		}
	}

}

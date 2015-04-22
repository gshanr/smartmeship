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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package looci.osgi.servExt.appInfo;

import looci.osgi.serv.constants.LoociRuntimes;


/**
 *
 * @author jef
 */
public class LoociNodeInfo {
    
    
    private String federationName = "";

    private String federationIp = "";
    
    private String nodeMac = "";
    
    private String nodeIP = "";
    
    private String nodeId = "";
    
    private String nodeType = LoociRuntimes.RUNTIME_UNDEFINED;
    
    public LoociNodeInfo(){
    }	
    
    
    public LoociNodeInfo(String nodeMac, String nodeId, String nodeIp, String federationName, String federationIp, String nodeType){
        this.nodeMac = nodeMac;
        this.nodeId = nodeId;
        this.nodeIP = nodeIp;        
        this.federationName = federationName;
        this.federationIp = federationIp;
        this.nodeType = nodeType;
    }
    
    public String getFederationName(){
        return federationName;
    }
	
    public void setFederationName(String federationName){
        this.federationName = federationName;
    }

    public String getFederationIp(){
        return federationIp;
    }
    
    public void setFederationIP(String ip){
        this.federationIp = ip;
    }
    
    public String getNodeMac(){
        return nodeMac;
    }

    public void setNodeMac(String mac){
        this.nodeMac = mac;
    }  
     
    public void setNodeIP(String nodeIP) {
		this.nodeIP = nodeIP;
	}
    
    public String getNodeIP() {
		return nodeIP;
	}
    
    public void setNodeId(String nodeId){
    	this.nodeId = nodeId;
    }
    
    public String getNodeId(){
    	return nodeId;
    }
    
    public String getNodeType(){
        return nodeType;
    }
    
    public void setNodeType(String nodeType){
        this.nodeType = nodeType;
    }


}

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
package looci.osgi.servExt.appInfo;


public class InstanceInfo {
	

	private byte instanceId;
	
	private byte state;
	
	
	private ComponentInfo component;
	
	private short[] receptacles;
	
	private short[] interfaces;
		

	public InstanceInfo(byte instanceId, ComponentInfo info, byte state, short[] receptacles,
			short[] interfaces) {
		this.instanceId = instanceId;
		this.component = info;
		this.state = state;
		this.receptacles = receptacles;
		this.interfaces = interfaces;
	}
	
	public byte getInstanceId(){
		return instanceId;		
	}
	
	public byte getState(){
		return state;
	}
	
	public void setReceptacles(short[] receptacles){
		this.receptacles = receptacles;
	}
	
	public void setInterfaces(short[] interfaces){
		this.interfaces = interfaces;
	}
	
	public short[] getReceptacles(){
		return receptacles;
	}
	
	public short[] getInterfaces(){
		return interfaces;
	}
	
	
	public ComponentInfo getComponentInfo(){
		return component;
	}
	
}

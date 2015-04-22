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

public class ComponentContext {
	
	private String componentName;
	
	private String binaryName;
	
	private int length;
	
	private String hash;
	
	private boolean isSecurityComponent;
	
	public ComponentContext(){
		
	}
	
	public String getName(){
		return componentName;
	}
	
	public void setName(String name){
		this.componentName = name;
	}
	
	public String getBinaryName(){
		return binaryName;
	}
	
	public void setBinaryName(String bin){
		this.binaryName = bin;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public String getHash() {
		return hash;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public boolean isSecurityComponent(){
		return isSecurityComponent;
	}
	
	public void setIsSecurityComponent(boolean isSec){
		isSecurityComponent = isSec;
	}

}

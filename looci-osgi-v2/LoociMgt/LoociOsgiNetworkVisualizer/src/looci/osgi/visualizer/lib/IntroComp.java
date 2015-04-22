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
package looci.osgi.visualizer.lib;
import java.util.ArrayList;

// Represent a component obtained through introspection.
public class IntroComp {
	
	private String id;
	private ArrayList<String> interfaces;
	private ArrayList<String> receptacles;
	
	// Create the component.
	IntroComp() {
		id = "default";
		interfaces = new ArrayList<String>();
		receptacles = new ArrayList<String>();
	}
	
	IntroComp(String id) {
		this.id = id;
		interfaces = new ArrayList<String>();
		receptacles = new ArrayList<String>();
	}
	
	// Add an interface.
	void addInterface (String inter) {
		
		boolean existing = false; // For checking.
		
		// Check if it is already inside.
		for (int i = 0; i < interfaces.size() && existing == false; i++) {
			if (interfaces.get(i).compareTo(inter) == 0) {
				existing = true;
			}
		}
			
		// If it does not exist, add the element.
		if (existing == false) {
			interfaces.add(inter);
		}
	}
	
	// Add a receptacle
	void addReceptacle (String recept) {
		
		boolean existing = false; // For checking.
		
		// Check if it is already inside.
		for (int i = 0; i < receptacles.size() && existing == false; i++) {
			if (receptacles.get(i).compareTo(recept) == 0) {
				existing = true;
			}
		}
			
		// If it does not exist, add the element.
		if (existing == false) {
			receptacles.add(recept);
		}
	}	

	// Get the component id.
	String getid() {
		return id;
	}
	
	// Get the interfaces.
	String[] getInterfaces() {
		return (String[]) interfaces.toArray(new String[interfaces.size()]);
	}
	
	// Get the receptacles.
	String[] getReceptacles() {
		return (String[]) receptacles.toArray(new String[receptacles.size()]);
	}	
}

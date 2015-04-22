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
// Represent a link obtained through introspection.
public class IntroLink {
	
	private String idI;
	private String I;
	private String idR;
	private String R;
	
	// Create the Link.
	IntroLink(String idI, String I, String idR, String R) {
		this.idI = idI;
		this.I = I;
		this.idR = idR;
		this.R = R;
	}
	
	// Check if one link is equal to another one.
	boolean areEquals (IntroLink a, IntroLink b) {
		
		boolean res = false;  // Value to be returned.
		
		// Compare all the values.
		if ((a.idI.compareTo(b.idI) == 0)  && (a.I.compareTo(b.I) == 0) && 
				(a.idR.compareTo(b.idR) == 0) && (a.R.compareTo(b.R) == 0)) {
			res = true;
		}

		return res;
	}
	
	// Get the node of the interface.
	String getIdI() {
		return idI;
	}
	
	// Get the interface.
	String getI() {
		return I;
	}
	
	// Get the component of the receptacle.
	String getIdR() {
		return idR;
	}
	
	// Get the receptacle.
	String getR() {
		return R;
	}
}

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
package looci.osgi.serv.constants;

public class LoociRuntimes {

	public static final String RUNTIME_UNDEFINED = "undefined";
	public static final String RUNTIME_RAVEN = "raven";
	public static final String RUNTIME_SUNSPOT= "spot";
	public static final String RUNTIME_OSGI = "osgi";
	public static final String RUNTIME_ANDROID = "android";
	public static final String RUNTIME_ZIGDUINO = "zigduino";
	public static final String RUNTIME_SMARTMESH = "smartmesh";
	
	public static final int RUNTIME_UNDEFINED_INT = 0;
	public static final int RUNTIME_RAVEN_INT = 1;
	public static final int RUNTIME_SUNSPOT_INT= 2;
	public static final int RUNTIME_OSGI_INT = 3;
	public static final int RUNTIME_ANDROID_INT = 4;
	public static final int RUNTIME_ZIGDUINO_INT = 5;
	public static final int RUNTIME_SMARTMESH_INT = 6;
	
	
	public static final String[] RUNTIME_NAMES = {
		RUNTIME_UNDEFINED,
		RUNTIME_RAVEN,
		RUNTIME_SUNSPOT,
		RUNTIME_OSGI,
		RUNTIME_ANDROID,
		RUNTIME_ZIGDUINO,
		RUNTIME_SMARTMESH
		};
	
	
	public static int getRuntimeVal(String runtimeName){
		for(int i = 0 ; i < RUNTIME_NAMES.length ; i++){
			if(runtimeName.equals(RUNTIME_NAMES[i])){
				return i;
			}
		}
		return RUNTIME_UNDEFINED_INT;
	}
	
	public static String getRuntimeName(int val){
		if(val<0 | val >= RUNTIME_NAMES.length){
			return RUNTIME_UNDEFINED;
		} else{
			return RUNTIME_NAMES[val];
		}
	}
		
	
}

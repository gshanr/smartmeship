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


public class LoociConstants {


	public static final byte COMPONENT_WILDCARD = 0;
	public static final byte COMPONENT_RECONFIG = 1;
	
	public final static short ERROR = 0;

	public static final String COMPONENT_DIR = System.getenv("LOOCI")+"/components";
	public static final String LOOCI_DIR = "looci";
	
	public static final int RECONFIGURATION_PORT = 9999;
	public static final int EVENT_PORT = 5555;
	public static final int COMMANDLINE_PORT = 6667;
	
	public static final String ADDR_WILDCARD = "*";
	public static final String ADDR_ANY = "0:0:0:0:0:0:0:0";
	public static final String ADDR_BC = "ff02:0:0:0:0:0:0:1";
	public static final String ADDR_LOCAL = "0:0:0:0:0:0:0:1";
	public static final String ADDR_VOID = null;
		
	
	public static final int MAX_EVENT_PAYLOAD = 256;
	
	
	public static final byte INTERCEPT_FROM_NETWORK = 0;
	public static final byte INTERCEPT_TO_NETWORK = 1;
	public static final byte INTERCEPT_TO_COMPONENT = 2;
	public static final byte INTERCEPT_FROM_COMPONENT = 3;
	
	public static final byte DATATYPE_UNDEFINED = 0;
	public static final byte DATATYPE_BYTE = 1;
	public static final byte DATATYPE_SHORT = 2;
	public static final byte DATATYPE_INT = 3;
	public static final byte DATATYPE_STRING = 4;
	public static final byte DATATYPE_BOOL = 5;
	public static final byte DATATYPE_BINARRAY = 6;
	
	public static final int NR_DATATYPES = 7;

	public static final String DATATYPE_STRING_UNDEFINED = "undefined";
	public static final String DATATYPE_STRING_BYTE = "byte";
	public static final String DATATYPE_STRING_SHORT = "short";
	public static final String DATATYPE_STRING_INT = "int";
	public static final String DATATYPE_STRING_STRING = "string";
	public static final String DATATYPE_STRING_BOOL = "bool";
	public static final String DATATYPE_STRING_BINARRAY = "byte[]";
	
	public static final String[] DATATYPE_STRINGS ={
		DATATYPE_STRING_UNDEFINED,
		DATATYPE_STRING_BYTE,
		DATATYPE_STRING_SHORT,
		DATATYPE_STRING_INT,
		DATATYPE_STRING_STRING,
		DATATYPE_STRING_BOOL,
		DATATYPE_STRING_BINARRAY
	};
	
	public static String typeToString(byte propertyType) {
		if(propertyType >= 0 && propertyType < NR_DATATYPES){
			return DATATYPE_STRINGS[propertyType];
		} else{
			return DATATYPE_STRING_UNDEFINED;
		}
	}

}

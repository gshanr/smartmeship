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


public class ErrorCodes {
	
	public static final byte SUCCESS = 1;
	public static final byte ERROR = 0;
	
	public static final byte ERROR_CODE_CODEBASE_NOT_FOUND = -1;
	public static final byte ERROR_CODE_COMPONENT_NOT_FOUND = -2;
	public static final byte ERROR_CODE_ILLEGAL_ARGUMENT_EXCEPTION = -3;
	public static final byte ERROR_CODE_INSUFFICIENT_MEMORY_EXCEPTION = -4;
	public static final byte ERROR_CODE_PARAMETER_NOT_FOUND = -5;
	public static final byte ERROR_CODE_WIRE_NOT_FOUND = -6;
	public static final byte ERROR_CODE_WIRE_DUPLICATE = -7;	
	public static final byte ERROR_CODE_TIMEOUT = -8;
	public static final byte ERROR_CODE_PROVIDED_INTERFACE_NOT_FOUND = -9;
	public static final byte ERROR_CODE_REQURIED_INTERFACE_NOT_FOUND = -10;
	public static final byte ERROR_CODE_ILLEGAL_STATE = -11;
	
	public static final byte ERROR_CODE_RUNTIME_ERROR = -12;
	public static final byte ERROR_CODE_RUNTIME_EXCEPTION = -13;
	public static final byte ERROR_CODE_SECURITY = -14;

	public static final String ERROR_STRING_ERROR = "error";
	public static final String ERROR_STRING_CODEBASE_NOT_FOUND = "error: codebase not found";
	public static final String ERROR_STRING_COMPONENT_NOT_FOUND = "error: component not found";
	public static final String ERROR_STRING_ILLEGAL_ARGUMENT_EXCEPTION = "error: illegal argument";
	public static final String ERROR_STRING_INSUFFICIENT_MEMORY_EXCEPTION = "error: insufficient memory";
	public static final String ERROR_STRING_PARAMETER_NOT_FOUND = "error: parameter not found";
	public static final String ERROR_STRING_WIRE_NOT_FOUND = "error: wire not found";
	public static final String ERROR_STRING_WIRE_DUPLICATE = "error: wire duplicate";
	public static final String ERROR_STRING_TIMEOUT = "error: reply timeout";
	public static final String ERROR_STRING_PROVIDED_INTERFACE_NOT_FOUND = "error: provided interface not found";
	public static final String ERROR_STRING_REQUIRED_INTERFACE_NOT_FOUND = "error: required interface not found";
	public static final String ERROR_STRING_ILLEGAL_STATE =	 "error: illegal state ";
	public static final String ERROR_STRING_RUNTIME_ERROR =	 "error: code cause runtime error ";
	public static final String ERROR_STRING_RUNTIME_EXCEPTION =	 "error: code cause runtime exception ";
	public static final String ERROR_STRING_SECURITY =	 "error: security exception ";
	
	public static final String[] ERROR_CODES = {
		ERROR_STRING_ERROR,
		ERROR_STRING_CODEBASE_NOT_FOUND,
		ERROR_STRING_COMPONENT_NOT_FOUND,
		ERROR_STRING_ILLEGAL_ARGUMENT_EXCEPTION,
		ERROR_STRING_INSUFFICIENT_MEMORY_EXCEPTION,
		ERROR_STRING_PARAMETER_NOT_FOUND,
		ERROR_STRING_WIRE_NOT_FOUND,
		ERROR_STRING_WIRE_DUPLICATE,
		ERROR_STRING_TIMEOUT,
		ERROR_STRING_PROVIDED_INTERFACE_NOT_FOUND,
		ERROR_STRING_REQUIRED_INTERFACE_NOT_FOUND,
		ERROR_STRING_ILLEGAL_STATE,
		ERROR_STRING_RUNTIME_ERROR,
		ERROR_STRING_RUNTIME_EXCEPTION,
		ERROR_STRING_SECURITY};

	
	public static String getErrorString(byte errorCode){
		int code = -errorCode;
		if(code >= 0 && code <= ERROR_CODES.length){
			return ERROR_CODES[code];
		} else{
			return "undefined error code";
		}
		
	}
	
	private ErrorCodes(){
	
	}
	
	private static ErrorCodes instance = null;
	
	public static ErrorCodes getInstance(){
		if(instance == null){
			instance = new ErrorCodes();
		}
		return instance;
	}
	
}

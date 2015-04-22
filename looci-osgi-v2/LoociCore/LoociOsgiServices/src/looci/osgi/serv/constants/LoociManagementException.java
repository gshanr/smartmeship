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

public class LoociManagementException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2492188976938322185L;

	private byte runtimeCode;
		
	
	public LoociManagementException(byte code){
		runtimeCode = code;
	}
	
	public String getMessage(){
		String message = super.getMessage();
		message += "errorType : " +ErrorCodes.getErrorString(runtimeCode);
		return message;
	}
	
	public byte getErrorCode(){
		
		return runtimeCode;
	}
	
	public String getLoociErrorMessage(){
		return ErrorCodes.getErrorString(runtimeCode);
	}
	
	
}

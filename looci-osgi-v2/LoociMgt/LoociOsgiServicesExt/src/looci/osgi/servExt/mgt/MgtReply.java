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
package looci.osgi.servExt.mgt;

import looci.osgi.serv.impl.ShortPayloadBuilder;

public class MgtReply {
		
	public static final byte NO_REPLY = -1;
	
	private ShortPayloadBuilder pb;
	private byte code;
	
	public MgtReply(byte code){
		this.code = code;
		pb = new ShortPayloadBuilder(new byte[0]);
	}
	
	public MgtReply(byte[] payload){
		code = payload[0];
		byte[] shortLoad = new byte[payload.length -1];
		System.arraycopy(payload, 1, shortLoad, 0, shortLoad.length);
		pb = new ShortPayloadBuilder(shortLoad); 
	}
	
	public MgtReply() {
		code = 0;
		pb = new ShortPayloadBuilder();
	}

	public boolean isSucces(){
		return code > 0;
	}
	
	public byte getCode(){
		return code;
	}
	
	public ShortPayloadBuilder getPb(){
		return pb;
	}
	

}

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
package looci.osgi.serv.components;

import java.net.UnknownHostException;


public class HeaderManagement {


	public static final byte HEADER_TARGET = 1;
	public static final byte HEADER_USER = 2;
	
	public static final byte TARGET_COMP_BYTE = 0;
	public static final byte TARGET_FLAGS_BYTE = 1;
	public static final byte TARGET_TRANSACTION_BYTE=2;
	
	public static final int TARGET_FLAG_NEEDS_REPLY_BIT = 0;
	public static final int TARGET_FLAG_IS_REPLY_BIT = 1;
	
	
	public static void createTargetReply(Event srcEvent, Event replyEvent){
		try {
			replyEvent.setDestinationAddress(srcEvent.getSourceAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		setDestinationComp(replyEvent, srcEvent.getSourceComp());
		setIsReply(replyEvent);
		setTransactionID(replyEvent, getTransactionID(srcEvent));
	}
	
	public static void setDestinationComp(Event event, byte dstComp){
		event.setHeaderByte(HEADER_TARGET, TARGET_COMP_BYTE, dstComp);
	}
	
	public static void setTransactionID(Event event, byte txID){
		event.setHeaderByte(HEADER_TARGET, TARGET_TRANSACTION_BYTE, txID);
	}
	
	public static void setIsReply(Event event){
		event.setHeaderBit(HEADER_TARGET, TARGET_FLAGS_BYTE, TARGET_FLAG_IS_REPLY_BIT, true);
	}
	
	public static void setNeedsReply(Event event){
		event.setHeaderBit(HEADER_TARGET, TARGET_FLAGS_BYTE, TARGET_FLAG_NEEDS_REPLY_BIT, true);
	}
	
	public static boolean getIsReply(Event event){
		return event.getHeaderBit(
				HeaderManagement.HEADER_TARGET,
				HeaderManagement.TARGET_FLAGS_BYTE,
				HeaderManagement.TARGET_FLAG_IS_REPLY_BIT);
	}
	
	
	public static boolean getNeedsReply(Event event){
		if(!event.hasHeader(HEADER_TARGET)){
			return false;
		}		
		return event.getHeaderBit(
				HeaderManagement.HEADER_TARGET,
				HeaderManagement.TARGET_FLAGS_BYTE,
				HeaderManagement.TARGET_FLAG_NEEDS_REPLY_BIT);
	}
	
	public static boolean isDirected(Event event){
		return event.hasHeader(HeaderManagement.HEADER_TARGET);
	}
	
	public static boolean isReply(Event event){
		return event.getHeaderBit(HEADER_TARGET, TARGET_FLAGS_BYTE, TARGET_FLAG_IS_REPLY_BIT);
	}
	
	public static byte getTargetComponent(Event event){
		return event.getHeaderByte(
				HEADER_TARGET,
				TARGET_COMP_BYTE);
				
	}
	
	public static byte getTransactionID(Event event){
		return event.getHeaderByte(
				HEADER_TARGET,
				TARGET_TRANSACTION_BYTE);
	}
	
	
}

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
/*
 * Copyright (c) 2010, Katholieke Universiteit Leuven
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Katholieke Universiteit Leuven nor the names of
 *       its contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package looci.osgi.serv.components;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.util.Utils;
import looci.osgi.serv.util.XString;


/**
 * Class representing an event.
 * 
 * a typical event contains 
 * 	a source node, 
 * 	a destination node, 
 * 	a source component, 
 * 	a destination component,
 * 	an event type,
 * 	and associated headers
 * 
 */

public class Event {

	public static final byte HEADER_PAYLOAD = 0;	
	
	
	// NETWORK HEADER
	private String srcNode = LoociConstants.ADDR_ANY; // where event comes from
	private String dstNode = LoociConstants.ADDR_ANY; // only filled in when event is directed
	
	//flags	
	private short eventID;
	private byte srcComponentID;
	private byte dstComponentID;
	
	private HashMap<Byte,byte[]> headers;
	
	private byte[] payload;

	public Event(short eventID, byte[] payload) {
		this.eventID = eventID;
		if(payload != null){
			this.payload = payload;
		} else{
			this.payload = new byte[0];
		}
		setSourceAddress(LoociConstants.ADDR_LOCAL);
		try {
			setDestinationAddress(LoociConstants.ADDR_VOID);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		headers = new LinkedHashMap<Byte, byte[]>();
		
	}
	
	public Event(short eventID, byte[] payload, HashMap<Byte, byte[]> headers){
		this(eventID,payload);
		setHeaders(headers);
	}
		
	public void reset(){
		headers = new LinkedHashMap<Byte, byte[]>();
		srcNode = LoociConstants.ADDR_ANY; // where event comes from
		dstNode = LoociConstants.ADDR_ANY; // only filled in when event is directed		
		//flags	
		eventID = 0;
		srcComponentID = 0;		
		payload = new byte[0];
	}
	
	/**
	 * Reconstruct this event from a given byte array
	 * @param content
	 */
	public void fillEventFromByteArray(byte[] content){
		headers = new LinkedHashMap<Byte, byte[]>();
		
		int index = 0;
		eventID = Utils.getShortAt(content, index);
		index += 2;
		srcComponentID = content[index];
		index += 1;

		//header processing;	
		
		byte currentHeader = content[index];
		index +=1;
		byte currentSize = 0;
		while(currentHeader != HEADER_PAYLOAD){
			currentSize = content[index];
			index +=1;
			byte[] temp = new byte[currentSize];
			System.arraycopy(content, index, temp, 0, currentSize);
			index += currentSize;
			headers.put(new Byte(currentHeader), temp);
			currentHeader = content[index];
			index +=1;
		}	
		
		int payloadLength = content.length - index;
		
		payload = new byte[payloadLength];
		System.arraycopy(content, index, payload, 0, payloadLength);
		setSourceAddress(LoociConstants.ADDR_LOCAL);
		try {
			setDestinationAddress(LoociConstants.ADDR_LOCAL);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Class constructor to recreate an event from a given byte array.
	 * Received over the network
	 * 
	 * @param b
	 *            the byte array to create this event from
	 * @param from
	 * 				the source address
	 */
	public Event(byte[] content, String from) {
		fillEventFromByteArray(content);
		setSourceAddress(from);
		try {
			setDestinationAddress(LoociConstants.ADDR_LOCAL);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public short getEventID() {
		return eventID;
	}

	public void setSourceComp(byte comp) {
		this.srcComponentID = comp;
	}

	public byte getSourceComp() {
		return srcComponentID;
	}
	
	public void setDestComp(byte comp) {
		this.dstComponentID = comp;
	}

	public byte getDestComp() {
		return dstComponentID;
	}

	public void setSourceAddress(String addr) {
		try{
			srcNode = Utils.normalizeIpAddr(addr);			
		} catch (Exception e) {	}
	}

	public String getSourceAddress() {
		return srcNode;
	}
	
	public void setHeaderByte(byte headerId, int byteId, byte byteIn){
		byte[] header = (byte[])headers.get(new Byte(headerId));
		if(header == null){
			header = new byte[byteId+1];
			headers.put(new Byte(headerId), header);
		}
		if(header.length <= byteId){
			byte[] tempHeader = new byte[byteId +1];
			System.arraycopy(header, 0, tempHeader, 0, header.length);
			header = tempHeader;
			headers.put(new Byte(headerId), header);
		}
		header[byteId] = byteIn;
	}
	
	public void setHeaderBit(byte headerId, int byteId, int bitId, boolean bit){
		byte[] header = (byte[])headers.get(new Byte(headerId));
		if(header == null){
			header = new byte[byteId+1];
			headers.put(new Byte(headerId), header);
		}else if(header.length <= byteId){
			byte[] header2 = new byte[byteId+1];
			System.arraycopy(header, 0, header2, 0, header.length);
			headers.put(new Byte(headerId), header2);
			header = header2;
		}
		header[byteId] = Utils.setBitAtTo(header[byteId], bitId, bit);
	}
	
	public byte getHeaderByte(byte headerId, int byteId) throws IndexOutOfBoundsException{
		byte[] header = (byte[])headers.get(new Byte(headerId));
		if(header == null){
			throw new IndexOutOfBoundsException("No such headerId at get byte: " + headerId + ","+byteId);
		}
		return header[byteId];
	}
	
	public boolean getHeaderBit(byte headerId, int byteId, int bitId) throws IndexOutOfBoundsException{
		byte header = getHeaderByte(headerId, byteId);
		return Utils.convertByteToBoolArray(header)[bitId];
	}



	public String getDestinationAddress() {
		return dstNode;
	}

	public void setDestinationAddress(String addr) throws UnknownHostException {
		
		dstNode =  Utils.normalizeIpAddr(addr);
		
	}
	
	public boolean destinationMatches(String addr){
		try{
			String testNode = Utils.normalizeIpAddr(addr);
			return testNode.equals(dstNode);
		} catch (Exception e) 
		{}
		return false;
	}
	
	
	public boolean sourceMatches(String addr){
		try{
			String testNode = Utils.normalizeIpAddr(addr);		
			if(addr.equals(LoociConstants.ADDR_ANY)){
				return true;
			}
			return testNode.equals(srcNode);
		} catch (Exception e) 
		{}
		return false;
	}

	public byte[] getPayload() {
		return payload;
	}

	public Event clone() {
		Event e = new Event(this.eventID, this.payload,headers);
		e.setSourceComp(srcComponentID);
		e.setSourceAddress(srcNode);
		try{

			e.setDestinationAddress(dstNode);
		} catch (UnknownHostException ex){}
		return e;
	}

	private void setHeaders(HashMap<Byte, byte[]> headersIn) {
		this.headers = new HashMap<Byte, byte[]>();
		for(Byte b : headersIn.keySet()){
			this.headers.put(b, headersIn.get(b));
		}
	}

	/**
	 * Marshalls this event for transmission over the network
	 * 
	 * @return this event as a byte array
	 */
	public byte[] toByteArray() {
		// Convert the payload to a byte array and make space
		
		int index = 0;
		int plSize = 4+getHeaderLength()+payload.length;			
		
		byte[] ba = new byte[plSize]; // 2 for event type and source component
		// Add the event type
		Utils.putShortAt(getEventID(), ba, index);
		index += 2;
		// Add source component
		ba[index] = srcComponentID;
		index +=1;
		
		// Add headers			
		Iterator<Entry<Byte, byte[]>> iter = headers.entrySet().iterator();
		
		while(iter.hasNext()){
			Entry<Byte, byte[]> entry = (Entry<Byte, byte[]>) iter.next();
			ba[index] = ((Byte) entry.getKey()).byteValue();
			ba[index+1] = Utils.convertIntToByte(((byte[])entry.getValue()).length);
			System.arraycopy(entry.getValue(), 0, ba, index+2, ((byte[])entry.getValue()).length);
			index += 2 + ((byte[])entry.getValue()).length;				
		}
		// Add payload header
		ba[index] = HEADER_PAYLOAD;
		index += 1;
								
		// Add the payload
		System.arraycopy(payload, 0, ba, index, payload.length);
		return ba;
		
	}

	private String getShortString(){
		String headerString = "";
		Byte[] headerBytes = (Byte[]) headers.keySet().toArray(new Byte[0]);
		for(int i = 0 ; i < headerBytes.length; i ++){
			headerString += headerBytes[i] + ",";
		}
		return "--[EV]:START-----------------------------------\n"+
			"[EV] type: " + getEventID() +"; len: "+getPayload().length +"; hdrIDs: " + headerString+ "\n" +
			"[EV] src_cmp:"+ getSourceComp() + ", src_addr: "+ getSourceAddress() + ",dst_cmp:"
			+ getDestComp() +", dst_addr: "+getDestinationAddress()+
			"\n--[EV]:END---------------------------------------\n";
		
	}
	
	public String getHeaderString(){
		String headerString = "";
		Byte[] headerBytes = (Byte[])headers.keySet().toArray(new Byte[0]);
		for(int i = 0 ; i < headerBytes.length; i ++){
			headerString += "[EV] header "+ headerBytes[i] + ":";
			byte[] value = getHeader(headerBytes[i].byteValue());
			headerString += "[" + XString.printByteArray(value) + "]";
			headerString += "\r\n";
		}
		return headerString;
	}
		
	
	public String toString() {
		return getShortString() + "\n"+
				"[EV] payload: ["+ XString.printByteArray(payload) + "]" + "\n"+ getHeaderString();
	}
	
	private int getHeaderLength(){
		Iterator<Entry<Byte, byte[]>> iter = headers.entrySet().iterator();
		int length = 0;
		while(iter.hasNext()){
			Entry<Byte, byte[]> entry = (Entry<Byte, byte[]>)iter.next();
			length += 2;
			length += ((byte[])entry.getValue()).length;
		}
		return length;		
	}

	public byte[] getHeader(byte headerId){
		return (byte[])headers.get(new Byte(headerId));
	}
	
	public void setHeader(byte headerId, byte[] data){
		headers.put(new Byte(headerId), data);
	}
	
	public boolean hasHeader(byte headerId){
		return headers.containsKey(new Byte(headerId));
	}
	
	public void removeHeader(byte headerId){
		headers.remove(new Byte(headerId));
	}

	public void setEventId(short newEventId) {
		eventID = newEventId;
	}

	public void setPayload(byte[] payload) {
		this.payload = (byte[]) payload.clone();
	}

	public byte[] getSourceAddressInBytes() {
		try {
			return InetAddress.getByName(srcNode).getAddress();
		} catch (UnknownHostException e) {
			return new byte[0];
		}
	}

	public byte[] getDstAddressInBytes() {
		try {
			return InetAddress.getByName(dstNode).getAddress();
		} catch (UnknownHostException e) {
			return new byte[0];
		}
	}
	
} // End of class Event

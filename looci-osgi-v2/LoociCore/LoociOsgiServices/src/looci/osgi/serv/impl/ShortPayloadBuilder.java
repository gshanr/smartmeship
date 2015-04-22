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
package looci.osgi.serv.impl;

import java.net.UnknownHostException;

import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.util.Utils;

public class ShortPayloadBuilder {

	private byte[] payload;
	private int size;
	private int readLoc;
	
	public ShortPayloadBuilder(byte[] payload){
		this.payload = payload;
		size = payload.length;
		readLoc = 0;
	}
	
	public ShortPayloadBuilder(){
		this.payload = new byte[LoociConstants.MAX_EVENT_PAYLOAD];
		size = 0;
		readLoc = 0;
	}
	
	public byte[] getPayload(){
		return payload;
	}
	
	public byte getByteAt(int pos){
		return payload[pos];
	}
	
	public short getShortAt(int pos){
		return Utils.getShortAt(payload, pos);
	}
	
	public int getIntAt(int pos){
		return Utils.getIntAt(payload, pos);
	}
	
	public String getIpAt(int pos){
		return Utils.getIpAt(payload, pos);
	}
	
	public byte[] getByteArrayAt(int pos, int length){
		byte[] retVal = new byte[length];
		System.arraycopy(payload, pos, retVal, 0, length);
		return retVal;
	}
	
	public byte[] getLenByteArrayAt(int pos){
		byte len = getByteAt(pos);
		return getByteArrayAt(pos+1, len);
	}
	
	public short[] getShortArrayAt(int pos, int length){
		short[] retVal = new short[length];
		for(int i =0 ; i < length; i ++){
			retVal[i] = Utils.getShortAt(payload, pos+2*i);
		}
		return retVal;
	}
	
	public short[] getLenShortArrayAt(int pos){
		byte len = getByteAt(pos);
		return getShortArrayAt(pos+1, len);
	}
	
	public String getNTStringAt(int pos){
		int i = pos;
		while(payload[i] != 0x00 && i < size){
			i ++;
		}
		if(payload[i] == 0x00){
			byte[] stringBytes = new byte[i - pos];
			System.arraycopy(payload, pos, stringBytes, 0, stringBytes.length);
			return new String(stringBytes);			
		} else{
			return "";
		}
		
	}
	
	//////////////////////////////////////////////
	// PutAt methods
	/////////////////////////////////
	
	public void putByteAt(int pos, byte input){
		payload[pos] = input;
		setExtendSize(pos+1);
	}
	
	public void putBooleanAt(int pos, boolean input){
		Utils.putBoolAt(input, payload, pos);
		setExtendSize(pos+1);
	}
	
	public void putShortAt(int pos, short input){
		Utils.putShortAt(input, payload, pos);
		setExtendSize(pos+2);
	}
	
	public void putIntAt(int pos, int input){
		Utils.putIntAt(input, payload, pos);
		setExtendSize(pos+4);
	}
	
	public void putIpAt(int pos, String input) throws UnknownHostException{
		Utils.putIpAt(input, payload, pos);
		setExtendSize(pos+16);
	}
	
	public void putByteArrayAt(int pos, byte[] input){
		System.arraycopy(input, 0, payload, pos, input.length);
		setExtendSize(pos+input.length);
	}
	
	public void putLenByteArrayAt(int pos, byte[] input){
		putByteAt(pos, Utils.convertIntToUByte(input.length));
		System.arraycopy(input, 0, payload, pos+1, input.length);
		setExtendSize(pos+input.length+1);
	}
	
	public void putShortArrayAt(int pos, short[] input){
		for(int i = 0 ; i < input.length ; i ++){
			Utils.putShortAt(input[i], payload, pos+2*i);
		}
		setExtendSize(pos+(input.length*2));
	}
	
	public void putLenShortArrayAt(int pos, short[] input){
		putByteAt(pos, Utils.convertIntToUByte(input.length));
		putShortArrayAt(pos+1,input);		
		setExtendSize(pos+(input.length*2)+1);
	}
	
	
	public void putNTStringAt(int pos, String input){
		byte[] stringBytes = input.getBytes();
		System.arraycopy(stringBytes, 0, payload, pos, stringBytes.length);
		payload[pos+stringBytes.length] = 0x00;
		setExtendSize(pos+stringBytes.length+1);
	}
	
	
	////////////////////////////////////
	// PUT METHODS
	////////////////////////////////////
	
	public void putByte(byte input){
		putByteAt(getSize(),input);
	}
	
	public void putBoolean(boolean input){
		putBooleanAt(getSize(),input);
	}
	
	public void putShort(short input){
		putShortAt(getSize(),input);
	}
	
	public void putInt(int input){
		putIntAt(getSize(),input);
	}
	
	public void putIp(String input) throws UnknownHostException{
		putIpAt(getSize(),input);
	}
	
	public void putByteArray(byte[] input){
		putByteArrayAt(getSize(),input);
	}
	
	public void putLenByteArray(byte[] input){
		putLenByteArrayAt(getSize(),input);
	}
	
	public void putShortArray( short[] input){
		putShortArrayAt(getSize(),input);
	}
	
	public void putLenShortArray(short[] input){
		putLenShortArrayAt(getSize(),input);
	}
	
	
	public void putNTString( String input){
		putNTStringAt(getSize(),input);
	}

	////////////////////////
	// Read methods
	///////////////////////
	
	public void resetRead(){
		readLoc = 0;
	}
	
	public void skip(int val){
		readLoc += val;
	}
	
	public void setRead(int val){
		readLoc = val;
	}
	
	public byte getByte(){
		byte val = getByteAt(readLoc);
		readLoc += 1;		
		return val;
	}
	
	public short getShort(){
		short val = getShortAt(readLoc);
		readLoc += 2;		
		return val;
	}
	
	public int getInt(){
		int val = getIntAt(readLoc);
		readLoc += 4;		
		return val;
	}
	
	public String getIp(){
		String val = getIpAt(readLoc);
		readLoc += 16;		
		return val;
	}
	
	public byte[] getByteArray(int length){
		byte[] val = getByteArrayAt(readLoc,length);
		readLoc += val.length;		
		return val;
	}
	
	public byte[] getLenByteArray(){
		byte[] val = getLenByteArrayAt(readLoc);
		readLoc += 1 + val.length;
		return val;
	}
	
	public short[] getShortArray(int length){
		short[] val = getShortArrayAt(readLoc,length);
		readLoc += val.length*2;		
		return val;
	}
	
	public short[] getLenShortArray(){
		short[] val = getLenShortArrayAt(readLoc);
		readLoc += val.length*2+1;		
		return val;
	}
	
	public String getNTString(){
		String val = getNTStringAt(readLoc);
		readLoc += val.length()+1;		
		return val;
	}
	
	
	
	////////////////////////
	// Size methods
	///////////////////////

	public int getSize() {
		return size;
	}
	
	private void setExtendSize(int newSize){
		if(newSize > size){
			size = newSize;
		}
	}
	
	public byte[] getSizedPayload(){
		byte[] retVal = new byte[size];
		System.arraycopy(payload, 0, retVal, 0, size);
		return retVal;
	}
}

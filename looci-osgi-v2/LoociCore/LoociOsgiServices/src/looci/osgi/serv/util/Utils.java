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
package looci.osgi.serv.util;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import looci.osgi.serv.constants.LoociConstants;

/**
 * Utility class allowing for many bit and byte operations, which are sometimes difficult in Java
 */
public class Utils {
	
///////////////////////////
// 	Byte array manipulations
///////////////////////////
	
	public static final String normalizeIpAddr(String addr) throws UnknownHostException{
		
		return Inet6Address.getByName(addr).getHostAddress().split("%")[0];
		
	}
	
	public static final boolean getBitAt(byte input, int index){
		return convertByteToBoolArray(input)[index];
	}
	
	public static final byte setBitAtTo(byte input, int index, boolean value){
		boolean[] old = convertByteToBoolArray(input);
		old[index] = value;
		return convertBoolArrayToByte(old);
	}
	
	public static boolean getBoolAt(byte[] input, int index){
		if(input[index] <= 0x00){
			return false;
		} else{
			return true;
		}
	}
	
	
	public static short getShortAt(byte[] input, int index){
		short result = (short)(((input[index] & (short)0x00FF) << (short)8) 
				| (input[index+1] & (short)0x00FF));
		return result;
	}
	
	public static int getIntAt(byte[] input, int index){
		int result = ((input[index] & 0x000000FF) << 24)
				| ((input[index+1] & 0x000000FF) << 16)
				| ((input[index+2] & 0x000000FF) << 8) | (input[index+3] & 0x000000FF);
		return result;
	}
	
	
	public static String getIpAt(byte[] in, int loc){
		byte[] temp = new byte[16];
		System.arraycopy(in, loc, temp, 0, 16);
		try {
			// at this point, we don't care if it's ipv4 or v6
			// it gets read the same way
			return ((InetAddress) InetAddress.getByAddress(temp)).getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getStringAt(byte[] input, int index, int length){
		byte[] elem = new byte[length];
		System.arraycopy(input, index, elem, 0, length);
		return new String(elem);
	}

	public static void putIpAt(String in, byte[] buffer, int index) throws UnknownHostException{
		InetAddress a;
		byte[] addr = new byte[16];
		
		a = (InetAddress) InetAddress.getByName(in);
		
		// if it's an ipv4 address, convert it as per:
		// http://en.wikipedia.org/wiki/IPv6#IPv4-mapped_IPv6_addresses
		if (a.getAddress().length == 4) {
			addr = convertIpv4ToIpv6(in);
		} else {
			addr = a.getAddress();
		}
			
		System.arraycopy(addr, 0, buffer, index, 16);
	}
	
	public static void putShortAt(short in, byte[] buffer, int index){
		buffer[index+0] = (byte) ((in & 0x0000FF00 )>>8);	
		buffer[index+1] = (byte) ((in & 0x000000FF ));	
	}
	
	public static void putIntAt(int in, byte[] buffer, int index){
		buffer[index] = (byte) ((in & 0xFF000000 )>>24);
		buffer[index+1] = (byte) ((in & 0x00FF0000 )>>16);
		buffer[index+2] = (byte) ((in & 0x0000FF00 )>>8);	
		buffer[index+3] = (byte) ((in & 0x000000FF ));	
	}
	
	public static void putBoolAt(boolean in, byte[] buffer, int index){
		if(in){
			buffer[index] = (byte) 1;
		} else{
			buffer[index] = (byte) 0;
		}
	}
		
	public static void putStringAt(String in, byte[] buffer, int index){
		byte[] elem = in.getBytes();
		System.arraycopy(elem, 0, buffer, index, elem.length);
	}
	
///////////////////////////
// 	conversions
///////////////////////////	
	
	public static boolean[] convertByteToBoolArray(byte in){
		boolean[] out = new boolean[8];
		for(int i = 0 ; i <= 7 ; i ++){
			out[i] = ((in & 0x01) == 1);
			in = (byte) (in >> 1);
		}
		return out;
	}
	
	public static byte convertBoolArrayToByte(boolean[] in){
		byte out = 0x00;
		for(int i = 0 ; i < 8 ; i ++){
			out = (byte)(out <<1);
			if(in[7-i]){
				out = (byte)(out|0x01);
			}
		}
		return out;
	}
	
	/**
	 * Hex conversion
	 */
	
    private static final String HEXES = "0123456789abcdef";
    public static String convertBytesToHex( byte [] input ) {
      if ( input == null ) {
        return null;
      }
      final StringBuilder hex = new StringBuilder( 2 * input.length );
      for ( final byte b : input ) {
        hex.append(HEXES.charAt((b & 0xF0) >> 4))
           .append(HEXES.charAt((b & 0x0F)));
      }
      return hex.toString();
    }
    
    /**
     * Transforms a given hexadecimal string into a byte array
     * 
     * @param s
     * 	the hexadecimal string
     * @return
     * 	the byte array
     */
    public static byte[] convertHexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
	
    /**
     * String conversion
     * 
     * @param datatype
     * 	The type of content in the byte array
     * 
     * @param content
     * 	The actual content
     * 
     * @return
     * 	A string containing the value in the content. Can be parsed to get the actual content.
     */


	public static String createStringFromByteContent(String datatype, byte[] content){
		String output = "";
		
		
		if(datatype.equals(LoociConstants.DATATYPE_STRING_BOOL)){
			boolean test = content[0] > 0;
			output = ""+test;
		} else if(datatype.equals(LoociConstants.DATATYPE_STRING_BYTE)){
			output = ""+content[0];
		} else if(datatype.equals(LoociConstants.DATATYPE_STRING_BINARRAY)){
			output = convertBytesToHex(content);
		}else if(datatype.equals(LoociConstants.DATATYPE_STRING_SHORT)){
			output += getShortAt(content, 0);
		}else if(datatype.equals(LoociConstants.DATATYPE_STRING_INT)){
			output += getIntAt(content, 0);
		}else if(datatype.equals(LoociConstants.DATATYPE_STRING_STRING)){
			output = new String(content);
		}
		return output;
	}	
	
	public static byte[] createByteArrayFromTypeString(String datatype, String content){
		byte[] output = null;
			
		if(datatype.equals(LoociConstants.DATATYPE_STRING_BOOL)){
			boolean test = Boolean.parseBoolean(content);
			if(test){
				output = new byte[]{1};
			} else{
				output = new byte[]{0};
			}
		} else if(datatype.equals(LoociConstants.DATATYPE_STRING_BYTE)){
			output = new byte[1];
			output[0] = Byte.parseByte(content);
		}else if(datatype.equals(LoociConstants.DATATYPE_STRING_BINARRAY)){		
			output = Utils.convertHexToBytes(content);
		}else if(datatype.equals(LoociConstants.DATATYPE_STRING_SHORT)){
			short value = Short.parseShort(content);
			output = new byte[2];
			putShortAt(value, output, 0);
		}else if(datatype.equals(LoociConstants.DATATYPE_STRING_INT)){
			int value = Integer.parseInt(content);
			output = new byte[4];
			putIntAt(value, output, 0);
		}else if(datatype.equals(LoociConstants.DATATYPE_STRING_STRING)){
			output = content.getBytes();
		}
		return output;		
	}
	
	////////////
	// Byte conversion from different source types
	//
	
	public static byte convertIntToByte(int val){
		val %= 256;
		return (byte) val;		
	}
	
	public static int convertByteToInt(byte val){
		return (int) val;
	}
	
	public static int convertUByteToInt(byte val){
		int retVal = (int) val;
		if(retVal < 0){
			retVal += 256;
		}
		return retVal;
	}
	
	public static byte convertIntToUByte(int val){
		val %= 256;
		if(val >= 128){
			val -= 256;
		}
		return (byte) val;		
	}

	public static byte[] convertIntToByteArray(int val) {		
		return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(val).array();
	}

	public static int convertByteArrayToInt(byte[] val){
		return ByteBuffer.wrap(val).order(ByteOrder.BIG_ENDIAN).getInt();
	}

	public static byte convertShortToByte(short val){
		val %= 256;
		return (byte) val;		
	}
	
	public static short convertByteToShort(byte val){
		return (short) val;
	}
	
	public static short convertUByteToShort(byte val){
		short retVal = (short) val;
		if(retVal < 0){
			retVal += 256;
		}
		return retVal;
	}
	
	public static byte convertShortToUByte(short val){
		val %= 256;
		if(val >= 128){
			val -= 256;
		}
		return (byte) val;		
	}

	public static byte[] convertShortToByteArray(short val) {
		return ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort(val).array();
		
	}

	public static short convertByteArrayToShort(byte[] val){
		return ByteBuffer.wrap(val).order(ByteOrder.BIG_ENDIAN).getShort();
	}
	
	public static byte[] convertIpv4ToIpv6(String address) {
		String[] octets = address.split("\\.");
		byte[] octetBytes = new byte[4];
		for (int i = 0; i < 4; ++i) {
			octetBytes[i] = (byte) Integer.parseInt(octets[i]);
		}

		byte addr[] = new byte[16];
		addr[10] = (byte)0xff;
		addr[11] = (byte)0xff;
		addr[12] = octetBytes[0];
		addr[13] = octetBytes[1];
		addr[14] = octetBytes[2];
		addr[15] = octetBytes[3];
		
		return addr;
	}
	
//////////////////////////////
// print shortcuts
/////////////////////////////

	public static final String DELIMITER = ",";


	public static boolean contains(byte[] array, byte value) {
		for(int i  = 0 ; i < array.length; i ++){
			if(array[i] == value){
				return true;
			}
		}
		return false;
	}
	
	public static short[] parseShortArray(String data){
		if(data.length() < 2){
			return new short[0];
		}		
		String[] temp = XString.split(data,DELIMITER);
		short[] output = new short[temp.length];
		for(int i = 0 ; i < temp.length;i++){
			try{
				output[i] = Short.parseShort(temp[i]);				
			} catch (NumberFormatException e){
				output[i] = 0;
			}
		}
		return output;
	}
	
}

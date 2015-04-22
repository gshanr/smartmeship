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
 

/**
 * String operation class.
 * Provides several static methods that allow for 
 * 	* String manipulation
 * 	* String printing
 */
public class XString {

    public static String[] split(String splitStr, String delimiter) {

        /** Note, delimiter can't be regex-type of argument as
        * in orginal J2SE implementation!!!*/
        int dLen = delimiter.length();
        int p1 = 0;
        int cnt = 0;

        if (splitStr.length() == 0) {
            String[] excepStr = new String[1];
            excepStr[0] = "";
            return excepStr;
        }

        if (dLen == 0) {
            String[] excepStr = new String[splitStr.length()+1];
            excepStr[0] = "";
            for (int i = 0; i<excepStr.length-1; i++) {
                excepStr[i+1] = String.valueOf(splitStr.charAt(i));
            }
            return excepStr;
        }

        p1 = splitStr.indexOf(delimiter, p1);
        while (p1 != -1) {
            cnt++;
            p1 = p1 + dLen;
            p1 = splitStr.indexOf(delimiter, p1);
        }

        String[] tmp = new String[cnt + 1];
        p1 = 0;
        int p2 = 0;
        for (int i = 0; i<tmp.length; i++) {
            p2 = splitStr.indexOf(delimiter, p2);
            if (p2 == -1) {
                tmp[i] = splitStr.substring(p1);
            } else {
                tmp[i] = splitStr.substring(p1, p2);
            }
            p1 = p2 + dLen;
            p2 = p2 + dLen;
        }
        cnt = 0;

        for (int i = tmp.length-1; i>-1; i--) {
            if (tmp[i].length() > 0) {
                break;
            } else {
                cnt++;
            }
        }
        String[] result = new String[tmp.length-cnt];
        for (int i = 0; i<result.length; i++) {
            result[i] = tmp[i];
        }
        return result;

    }
    

	public static String printByteArray(byte[] array) {
		String res = "[";
		res += (array.length>=1)?""+array[0]:"";
		for (int i = 1; i < array.length; i++) {
			int value = array[i];
			if(value < 0){
				value += 256;
			}
			res = res + "," + value;
		}
		res = res + "]";
		return res;
	}
	
	public static String printByteArray(byte[] array, int size) {
		String res = "[";
		res += (array.length>=1)?""+array[0]:"";
		for (int i = 1; i < size && i < array.length; i++) {
			res = res + "," + array[i];
		}
		res = res + "]";
		return res;
	}
	
	public static String printShortArray(short[] array){
		String res = "[";
		res += (array.length>=1)?""+array[0]:"";
		for (int i = 1; i < array.length; i++) {
			res = res + "," + array[i];
		}
		res = res + "]";
		return res;
	}
	
	public static String printString(String[] array) {
		String res = "[";
		res += (array.length>=1)?""+array[0]:"";
		for (int i = 1; i < array.length; i++) {
			res = res + DELIMITER + array[i];
		}
		res = res + "]";
		return res;
	}
	
	public static String printString(String[] array, String delimiter) {
		String res = "";
		res += (array.length>=1)?""+array[0]:"";
		for (int i = 1; i < array.length; i++) {
			res = res + delimiter + array[i];
		}
		return res;
	}
	
	public static String printStringArray(String[][] array){
		String res = "[";
		for(int i = 0 ; i < array.length ; i++){
			res += printString(array[i]);
			res += "\n";
		}
		res += "]";
		return res;
	}
	
	public static final String DELIMITER = ",";




	
}

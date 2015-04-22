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

package looci.osgi.serv.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Utility class to create Event payloads. The construction of a payload is as
 * follows:
 * 
 * nr of elements - length of element 1 - ... - length of element n - element 1
 * - ... - element n
 * 
 * @author wouterh, klaas
 */
public class PayloadBuilder {


	private Vector<byte[]> elements = new Vector<byte[]>();

	/**
	 * Default constructor, used when creating a payload when sending an event.
	 */
	public PayloadBuilder() {

	}

	/**
	 * Constructor used when recreating the payload at the reception of an
	 * event.
	 * 
	 * @param b
	 *            the event's payload
	 */
	public PayloadBuilder(byte[] b) {
		ByteArrayInputStream sizes = new ByteArrayInputStream(b);
		ByteArrayInputStream content = new ByteArrayInputStream(b);
		int nbElements = sizes.read();
		content.skip(nbElements + 1);
		for (int i = 0; i < nbElements; ++i) {
			int size = sizes.read();
			byte[] elem = new byte[size];
			content.read(elem, 0, size);
			elements.addElement(elem);
		}
	}

	/**
	 * A getter to retrieve the full payload. This includes the fields
	 * specifying the number of elements and their sizes, together with the
	 * elements themselves.
	 * 
	 * @return byte[]
	 */
	public byte[] getPayload() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		output.write(elements.size());
		for (int i = 0; i < elements.size(); ++i) {
			byte[] elem = (byte[]) elements.elementAt(i);
			output.write(elem.length);
		}
		for (int i = 0; i < elements.size(); ++i) {
			byte[] elem = (byte[]) elements.elementAt(i);
			try {
				output.write(elem);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return output.toByteArray();
	}

	/**
	 * Get the number of elements in the payload.
	 * 
	 * @return int the number of elements
	 */
	public int getNbElements() {
		return elements.size();
	}

	/**
	 * Add a single byte to the payload.
	 * 
	 * @param b
	 */
	public void addByte(byte b) {
		byte[] elem = new byte[1];
		elem[0] = b;
		elements.addElement(elem);
	}

	/**
	 * Add a String to the payload.
	 * 
	 * @param str
	 */
	public void addString(String str) {
		elements.addElement(str.getBytes());
	}

	/**
	 * Add an integer to the payload.
	 * 
	 * @param integer
	 */
	public void addInteger(int integer) {
		byte[] elem = new byte[] { (byte) (integer >> 24 & 0xff),
				(byte) (integer >> 16 & 0xff), (byte) (integer >> 8 & 0xff),
				(byte) (integer & 0xff) };
		elements.addElement(elem);
	}

	public void addShort(short val) {
		byte[] elem = new byte[] { (byte) (val >> 8 & 0xFF),(byte)(val & 0xFF)};
		elements.addElement(elem);
	}
	
	/**
	 * Get the byte at the specified element position (this is not the byte
	 * offset).
	 * 
	 * @param pos
	 *            the position of the element in the payload
	 * @return the requested byte
	 */
	public byte getByteAt(int pos) {
		byte[] elem = (byte[]) elements.elementAt(pos);
		return elem[0];
	}

	/**
	 * Get the String at the specified element position (this is not the byte
	 * offset).
	 * 
	 * @param pos
	 *            the position of the element in the payload
	 * @return the requested String
	 */
	public String getStringAt(int pos) {
		byte[] elem = (byte[]) elements.elementAt(pos);
		return new String(elem);
	}

	/**
	 * Get the integer at the specified element position (this is not the byte
	 * offset).
	 * 
	 * @param pos
	 *            the position of the element in the payload
	 * @return the requested integer
	 */
	public int getIntegerAt(int pos) {
		byte[] elem = (byte[]) elements.elementAt(pos);
		int result = ((elem[0] & 0x000000FF) << 24)
				| ((elem[1] & 0x000000FF) << 16)
				| ((elem[2] & 0x000000FF) << 8) | (elem[3] & 0x000000FF);
		return result;
	}

	public short getShortAt(int pos) {
		byte[] elem = (byte[]) elements.elementAt(pos);
		short result = (short)(((elem[0] & 0x00FF) << 8) | ((elem[1] & 0x00FF)));
		return result;
	}


}

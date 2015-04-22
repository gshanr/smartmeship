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

package looci.osgi.runtime;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

import looci.osgi.serv.components.ComponentStateTypes;
import looci.osgi.serv.components.Event;
import looci.osgi.serv.components.IEventSource;
import looci.osgi.serv.components.IReceive;
import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.log.LLog;
import looci.osgi.serv.util.LoociProperties;
import looci.osgi.serv.util.Utils;



public class NetworkComponent implements IReceive,IEventSource {

	private final int PORT = LoociConstants.EVENT_PORT;
	private final int MAX_PACKET_SIZE = 2048;
	private DatagramSocket senderSocket;
	private IReceive eventReceiver;

	private ListenerThread listener;

	private final int SRC_PORT = 5557;
	
	
	private ArrayList<String> myAdresses;
	
	public NetworkComponent() {
		listener = new ListenerThread(this);
		eventReceiver = null;
		myAdresses = new ArrayList<String>();
		myAdresses.add(LoociConstants.ADDR_LOCAL);
	}



	public String getMyAddress() {
		if(senderSocket != null){
			LLog.out(this, "returning addres:"+senderSocket.getLocalAddress().getHostAddress());
			return senderSocket.getLocalAddress().getHostAddress();
		} else{
			LLog.out(this, "returning addres: local");
			return LoociConstants.ADDR_LOCAL;
		}
	}
	
	
	/**
	 * Start this network component.
	 */
	public void start() {
		try {
			String prefAddr = LoociProperties.getProp("addr");
			if(prefAddr != null){				
				senderSocket = new DatagramSocket(new InetSocketAddress(prefAddr,SRC_PORT));				
			} else{
				senderSocket = new DatagramSocket(SRC_PORT);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		listener.start();
	}

	/**
	 * Stop this network component.
	 */
	public void stop() {
		try{
			senderSocket.close();
			senderSocket = null;
		} catch(Exception e){
			
		}
		try{
			listener.stop();
		} catch(Exception e){
			
		}
	}

	/**
	 * Receive data from the network and dispatch it to the event bus.
	 * 
	 * @param data
	 */
	public void receive_event_from_network(byte[] data, String from, String to) {
		LLog.out(this,"[NW] received event with length:"+data);
		Event ev = null;
		try {
			ev = new Event(data, from);
			LLog.out(this,"[NW] event length: "+ev.getPayload().length);
			ev.setDestinationAddress(to);
		} catch (Exception e) {
			e.printStackTrace();
			LLog.out(this,"[NC]received an indecipherable event, ignoring");
		}
		if (ev != null && eventReceiver != null) {
			try{
				eventReceiver.receive(ev,this);				
			} catch (Exception e){
				e.printStackTrace();
			}
		} else{
			if(ev == null){
				LLog.out(this,"[NC] null event received");
			}
			if(eventReceiver == null){
				LLog.out(this,"[NC] no receiver set");
			}
		}
	}

	/**
	 * Receive an event that has to be sent to network
	 * @param event
	 */
	public void receive(Event event, IEventSource source) {
		LLog.out(this,"Network sending event to "+event.getDestinationAddress());
		if(event.destinationMatches(LoociConstants.ADDR_ANY)){	
			try {
				event.setDestinationAddress(LoociConstants.ADDR_BC);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}		
		
		this.send(event.getDestinationAddress(), event.toByteArray());
	}
	
	public boolean isActive(){
		return senderSocket != null;
	}

	/**
	 * Send data from the event bus to a destination using (unreliable) datagram
	 * communication.
	 */
	public void send(String dest, byte[] payload) {
		DatagramPacket packet = null;
		try {
			LLog.out(this,"sending to "+dest);
			InetAddress address = InetAddress.getByName(dest);
			
			LLog.out(this,"sending packet with length: "+payload.length);
			packet = new DatagramPacket(payload, payload.length,
					address, PORT);
			senderSocket.send(packet);
		} catch (IOException exc) {
			LLog.out(this,"[NETWORK] ERROR: " + exc.getLocalizedMessage());
			try{
				if(packet != null){
					DatagramSocket sock = new DatagramSocket(SRC_PORT);
					sock.send(packet);
					sock.close();
				}	
			}catch(IOException exc1){
				LLog.out(this,"[NW] retry failed");
			}
		}
	}

	private class ListenerThread implements Runnable {
		private DatagramSocket listenerSocket;
		private NetworkComponent nwcomp;
		private volatile Thread thr;

		public ListenerThread(NetworkComponent n) {
			nwcomp = n;
		}

		public void start() {
			setup();
			thr = new Thread(this);
			thr.start();
			LLog.out(this,"[Network Component] listening to the network on port "
							+ PORT);
		}

		public void stop() {
			thr = null;
			try {
				listenerSocket.close();
				LLog.out(this,"[Network Component] stopped listening to the network");
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}

		private void setup() {
			try {
				String prefAddr = LoociProperties.getProp("addr");
				if(prefAddr != null){
					LLog.out(this, "setting listener socket to "+prefAddr);
					try{
						listenerSocket = new DatagramSocket(new InetSocketAddress(prefAddr, PORT)); 
					} catch (BindException e){
						LLog.out(this, "setting listener socket failed, using default)");
						listenerSocket = new DatagramSocket(PORT);		
					}
				} else{
					listenerSocket = new DatagramSocket(PORT);					
				}
				
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		}

		public void run() {
			Thread thisThread = Thread.currentThread();
			byte[] buf = new byte[MAX_PACKET_SIZE];
			while (thisThread == thr) {
				try {
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					listenerSocket.receive(packet);
					String srcAddr = packet.getAddress().getHostAddress();
					
					String dstAddr = listenerSocket.getLocalAddress().getHostAddress();
					
					LLog.out(this,"received datagram from: "+packet.getAddress().getHostAddress() + " with len " + packet.getLength());
					
					
					LLog.out(this, "adding as destination: "+dstAddr);

					byte[] tempBuffer = new byte[packet.getLength()];
					System.arraycopy(buf, 0, tempBuffer, 0, tempBuffer.length);
					nwcomp.receive_event_from_network(tempBuffer, srcAddr, dstAddr);
					
				} catch (IOException exc) {
					exc.printStackTrace();
					listenerSocket.close(); 
				}
			}
		}
	}

	public byte getState() {
		return ComponentStateTypes.ACTIVE;
	}

	public String getComponentType() {
		return "networkcomponent";
	}

	public void registerReceiver(IReceive receiver) {
		this.eventReceiver = receiver;
	}

}

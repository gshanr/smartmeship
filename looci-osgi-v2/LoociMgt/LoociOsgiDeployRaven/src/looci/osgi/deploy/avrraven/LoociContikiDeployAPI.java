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
package looci.osgi.deploy.avrraven;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import looci.osgi.serv.util.Utils;
import looci.osgi.servExt.appInfo.LoociNodeInfo;
import looci.osgi.servExt.mgt.IDeploymentAPI;
import looci.osgi.servExt.mgt.TextObserver;


public class LoociContikiDeployAPI implements IDeploymentAPI {

	
	Socket dataSocket = null;
	BufferedInputStream bis = null;
	BufferedOutputStream bos = null;
	BufferedReader input = null;
	
	private String targettedPlatform;
	private int delay;
	private int timeout;
	
	public LoociContikiDeployAPI(String targettedPlatform, int delay, int timeout) {
		this.targettedPlatform = targettedPlatform;
		this.delay = delay;
		this.timeout = timeout;
	}
	
	
	public byte deploy(String file, String address) throws Exception{
		dataSocket = null;
		bis = null;
		bos = null;
		input = null;
		char[] retBuffer = new char[10];
		String exception = "";
		try{
			long time = System.currentTimeMillis();
			System.out.println("[AVR DEPLOYER] opening socket for C Test");
			dataSocket = new Socket();
			dataSocket.connect(new InetSocketAddress(address,4321), 20000);
			//dataSocket.connect(new InetSocketAddress("127.0.0.1",4321), 20000);
			
			
			DelaySocketClose close = new DelaySocketClose(dataSocket, timeout);
			close.start();
			
			bis = new BufferedInputStream(
					new FileInputStream(file));
			bos = new BufferedOutputStream(dataSocket
					.getOutputStream());
			input = new BufferedReader(new InputStreamReader(
					dataSocket.getInputStream()));
			
			File f = new File(file);
			long length = f.length();
			
			int bufferLength = 610;
			int offset = 2;
			byte[] buffer = new byte[bufferLength];

			Utils.putShortAt((short) length, buffer, 0);
					
			int totalWritten = 0;
			int readCount;
			
			
			System.out.println("[AVR DEPLOYER] Sending component of size" + length);
			while ((readCount = bis.read(buffer,offset,bufferLength-offset)) > 0) {
				bos.write(buffer, 0, readCount+offset);
				totalWritten += readCount;
				System.out.println("[AVR DEPLOYER] sending "+ (readCount + offset) + "bytes");
				bos.flush();
				offset = 0;
				if(totalWritten < length){
					synchronized (this) {						
						this.wait(delay);
					}
				}
			}

			int ret = input.read(retBuffer);
			System.out.println("[AVR DEPLOYER] returned " + ret + "cid: "+retBuffer[0]);
			long time2 = System.currentTimeMillis();
			long diff = time2 - time;
			System.out.println("[AVR DEPLOYER] time for deploy : " + diff +"ms");
			
			bos.close();
			bis.close();
			dataSocket.close();
			return (byte) retBuffer[0];
		} catch(Exception e){
			e.printStackTrace();
			exception = e.getMessage();
			try{
				if(bis != null){
					bis.close();
				}
				if(bos != null){
					bos.close();
				}
				if(input != null){
					input.close();
				}
				if(dataSocket != null){
					dataSocket.close();
				}
			
			}catch(Exception e1){
				
			}
		}
		throw new Exception(exception);	
	}
	
	private class DelaySocketClose extends Thread{
		
		private int delay;
		private Socket socket;
		
		public DelaySocketClose(Socket socket,int delay){
			this.delay = delay;
			this.socket = socket;
		}
		
		@Override
		public void run() {
			synchronized (this) {
				try {
					this.wait(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(!socket.isClosed()){
					try {
						System.out.println("[AVR DEPLOYER] Closing deploy socket");
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	@Override
	public byte deploy(String componentFile, LoociNodeInfo nodeId,TextObserver callback, Map<String, String> parameters) throws Exception {
		return deploy(componentFile,nodeId.getNodeIP());
	}	
	

	@Override
	public String getTargettedPlatform() {
		return targettedPlatform;
	}
	
}

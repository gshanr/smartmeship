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

package looci.osgi.runtime.deployment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import looci.osgi.serv.interfaces.ILoociCodebase;
import looci.osgi.serv.util.XString;


public class ClientConnection implements Runnable {
	private Socket controlSocket;
	private BundleContext context;
	private Thread thr;
	private PrintWriter out;
	private BufferedReader in;
	private File component;
	private String deployDirectory;
	
	/////////////////////
	// Thread construction
	/////////////////////
	
	public ClientConnection(Socket s, BundleContext context, String deployDir) {
		controlSocket = s;
		this.context = context;
		this.deployDirectory = deployDir;
		
		try {
			out = new PrintWriter(controlSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					controlSocket.getInputStream()));
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void start() {
		thr = new Thread(this);
		thr.start();
	}	
	
	
	
	
	////////////////////////////
	// Thread operation
	////////////////////////////
	

	public void run() {
		try {
			String inputLine;
			out.println("READY");
			while (thr == Thread.currentThread()) {
				inputLine = in.readLine();
				if (inputLine != null) {
					process(inputLine);
				} else {
					thr = null;
				}
			}
			out.close();
			in.close();
			controlSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stopClientThread(){
		thr = null;
	}

	private void process(String s) {
		if (s.startsWith("deploy")) {
			String[] args = XString.split(s," ");
			startListeningForDeployment(args[1]);
		} else if(s.startsWith("failure")){
			stopListeningForDeployment();
		} else{
			throw new IllegalArgumentException("unknown string received");
		}
	}

	private DataThread dataThread;
	
	private void startListeningForDeployment(String componentName) {
		component = new File(deployDirectory+"/" + componentName + System.currentTimeMillis());
		
		try {
			ServerSocket s = new ServerSocket(5678);
			dataThread = new DataThread(this, s, component);
			dataThread.start();
			out.println("START_DEPLOY port=5678");
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	private void stopListeningForDeployment(){
		dataThread.stopThread();
	}

	public void receivedComponent() {
		byte id;
		try {
			id = install(component);
			out.println("SUCCEEDED id=" + id);
		} catch (Exception e) {
			e.printStackTrace();
			out.println("FAILED::" + e.getMessage());
		}
		
		
		stopClientThread();
		component.delete();
	}
	
	public void deployFailed(){
		out.println("FAILURE");
		stopClientThread();
		component.delete();
	}



	private class DataThread implements Runnable {
		private ServerSocket serverSocket;
		private ClientConnection conn;
		private File componentFile;
		private Socket dataS;

		public DataThread(ClientConnection conn, ServerSocket s,
				File componentFile) {
			this.conn = conn;
			serverSocket = s;
			this.componentFile = componentFile;
		}

		public void start() {
			new Thread(this).start();
		}
		
		
		public void stopThread(){
			if(dataS != null){				
				try {
					dataS.close();
				} catch (IOException e) {}
				dataS = null;
			}
			if(serverSocket!=null){
				try {
					serverSocket.close();
				} catch (IOException e) {}
				serverSocket = null;				
			}
		}

		/**
		 * Receive component from deployer.
		 */
		public void run() {
			try {
				dataS = serverSocket.accept();
				BufferedInputStream bis = new BufferedInputStream(
						dataS.getInputStream());
				FileOutputStream os = new FileOutputStream(componentFile);
				BufferedOutputStream bos = new BufferedOutputStream(os);
				byte[] buffer = new byte[1024];
				int readCount;
				while ((readCount = bis.read(buffer)) > 0) {
					bos.write(buffer, 0, readCount);
				}
				bos.close();
				stopThread();
				conn.receivedComponent();
			} catch (Exception exc) {
				exc.printStackTrace();
				stopThread();
				conn.deployFailed();
			}
			
		}

	}

	public byte install(File component) throws Exception{

			
		Bundle b = context.installBundle(component.getName(), new FileInputStream(component));
		
		b.start();
		ServiceReference[] refs = (ServiceReference[])b.getRegisteredServices();
		for (int i = 0; i < refs.length; i++) {
			Object obj = context.getService(refs[i]);
			if (obj instanceof ILoociCodebase) {
				return ((ILoociCodebase) obj).getCodebaseID();
			}
		}

		return 0;
	}
}

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
package looci.osgi.deploy.osgi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.log.LLog;



public class LoociOsgiDeployer extends Thread {

	private static final int PORT = LoociConstants.RECONFIGURATION_PORT;
	private Socket sock;
	private String address;
	private String componentFile;
	private PrintWriter pw;
	private File file;
	private String errorMsg = "";
	private volatile boolean isExecuting;
	private byte resultingComponentID = 0;

	public LoociOsgiDeployer(String address, String componentFile) {
		this.address = address;
		this.componentFile = componentFile;
		isExecuting = true;
	}

	public boolean isRunning() {
		return isExecuting;
	}
	

	public byte getResultingComponentId() throws Exception{
		if(resultingComponentID != 0){
			return resultingComponentID;
		} else{
			throw new Exception(errorMsg);
		}
		
	}

	public void run() {
		try {
			execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte execute() throws Exception{

		file = lookupFile(componentFile);
		if (!file.canRead()) {
			LLog.out(this,"[DEPLOYMENT PROCESS] ERROR -- Unable to locate file "
							+ componentFile);
			throw new FileNotFoundException("can not find file "+componentFile);
		}
		connect(address);
		return getResultingComponentId();
	}
	
	public void timeOut(){
		quitProtocol();
	}

	public void process(String s) {
//		System.out.println("[DEPLOYER] received " + s);
		LLog.out(this, "received:"+s);
		if (s.equals("READY")) {
			prepareUpload();
		} else if (s.startsWith("START_DEPLOY")) {
			String[] val = s.split("\\s");
			String[] arg = val[1].split("=");

			uploadComponent(Integer.parseInt(arg[1]));
			// pw.println("id");
		} else if (s.startsWith("SUCCEEDED")) {
			LLog.out(this,"[DEPLOYER] component installed");
			String[] val = s.split("\\s");
			String[] arg = val[1].split("=");
			resultingComponentID = Byte.parseByte(arg[1]);
			LLog.out(this,"[DEPLOYER] component id: "+resultingComponentID);
			quitProtocol();
		} else if (s.startsWith("FAILED")) {
			LLog.out(this,"[DEPLOYER] component installation failed");
			String[] val = s.split("::");
			errorMsg = val[1];
			quitProtocol();
		} else {
			// something weird happened;
			errorMsg = "unspecified failure happened";
			quitProtocol();
		}
	}

	private void quitProtocol() {
		try {
			isExecuting = false;
			if(sock != null){
				sock.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File lookupFile(String componentFile) {
		File f = new File(componentFile);
		return f;
	}

	private void connect(String address) throws Exception{

			sock = new Socket(address, PORT);
			pw = new PrintWriter(sock.getOutputStream(), true);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					sock.getInputStream()));
			String s = "";
			while (isExecuting) {
				s = input.readLine();
				if (s != null) {
					process(s);
				}
			}
			LLog.out(this,"[DEPLOYER] connection closed");
	}

	public void prepareUpload() {
		pw.println("deploy " + file.getName());
	}
	
	public void uploadFailed(){
		pw.println("failure");
	}

	public void uploadComponent(int port) {
		try {

			Socket dataSocket = new Socket(address, port);
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			BufferedOutputStream bos = new BufferedOutputStream(dataSocket
					.getOutputStream());
			byte[] buffer = new byte[1024];
			int readCount;
			LLog.out(this,"[DEPLOYER] Sending component");
			while ((readCount = bis.read(buffer)) > 0) {
				bos.write(buffer, 0, readCount);
			}
			bos.flush();
			bos.close();
			bis.close();
			LLog.out(this,"[DEPLOYER] Component uploaded!");
		} catch (Exception ex) {
			ex.printStackTrace();
			uploadFailed();
		} 

	}



}

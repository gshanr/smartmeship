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
package looci.osgi.app.orocosserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class OrocosServerListener extends Thread{

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private int port = 30333;
	
	private ArrayList<OrocosServerConnection> connections;
	
	BufferedReader reader;
	
	private OrocosServerInst inst;
	private boolean goOn;
	
	public OrocosServerListener(OrocosServerInst inst){
		this.inst = inst;
		connections = new ArrayList<OrocosServerConnection>();
		goOn = true;
	}
	
	@Override
	public void run() {
		boolean success = startServer();
		if(success){
			startListeningForConnections();
		}	

		
	}
	
	private boolean startServer(){
		try {
		    serverSocket = new ServerSocket(port);

		    System.out.println("[OROCOS] Listening to port" + port);
		    return true;
		} 
		catch (IOException e) {
		    System.out.println("Could not listen on port:"+ port);
		    e.printStackTrace();
		}
		return false;
	}
	
	private void startListeningForConnections(){
		while(goOn){
			try {

			    System.out.println("[OROCOS] Awaiting connection");
			    clientSocket = serverSocket.accept();
			    OrocosServerConnection conn = new OrocosServerConnection(clientSocket,this);
			    connections.add(conn);
			    conn.start();
			    System.out.println("[OROCOS] Accepted conn on port" + port);
			} 
			catch (IOException e) {
			    System.out.println("Accept failed:" + port);
			}
		}

	}
	
	public synchronized void publishEvent(String data){
		OrocosServerConnection conn;
		for(int i = 0 ; i < connections.size() ; i ++){
			conn = connections.get(i);
			conn.writeToSocket(data);
		}
	}
	
	public synchronized void close(){
		goOn = false;
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//close all connections
		OrocosServerConnection conn;
		for(int i = 0 ; i < connections.size() ; i ++){
			conn = connections.get(i);
			conn.close();
		}
		
	}
	
	public synchronized void closedConn(OrocosServerConnection conn){
		connections.remove(conn);
	}
	
	
	
}

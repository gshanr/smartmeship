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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class OrocosServerConnection extends Thread {

	private Socket clientSocket;	
	private boolean goOn;
	private OrocosServerListener server;
	
	PrintWriter out;
	BufferedReader in;
	
	public OrocosServerConnection(Socket clientSocket, OrocosServerListener inst){
		this.clientSocket = clientSocket;
		this.server = inst;
		goOn = true;
	}
	
	@Override
	public void run() {
		
		try {
			System.out.println("[OROCOS] Starting client conn");
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String inputLine;
			int state = 0;
			char[] input = new char[100];
			
			while(goOn){
				if(in != null){
					state = in.read(input);					
				} else{
					state = -1;
				}
				if(state >= 0){
					inputLine = new String(input,0,state);
					System.out.println("[OROCOS] Read: "+ inputLine);		
				} else{
					System.out.println("failed to read");
					goOn = false;
				}
				// process line
				
				//publish to serverInstance
				
			}
		}	
		catch (IOException e) {
			e.printStackTrace();
		}		
		close();
	}
	
	
	public void writeToSocket(String line){
		if(out != null){
			System.out.println("[OROCOS] Writing to socket");
			out.println(line);			
		} else{
			System.out.println("[OROCOS] Socet write failed, out is null");
		}
	}
	
	
	public void close(){
		try{
			if(in != null){
				in.close();				
			}
			if(out != null){
				out.close();				
			}
			in = null;
			out = null;
			if(clientSocket != null){
				clientSocket.close();				
			}
			server.closedConn(this);
		} catch (IOException ex){
			
		}
		

	}
}

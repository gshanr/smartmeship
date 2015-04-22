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
package looci.osgi.runtime.deployment;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;

import org.osgi.framework.BundleContext;
import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.constants.platform.LoociPlatformConstants;
import looci.osgi.serv.log.LLog;
import looci.osgi.serv.util.LoociProperties;


public class DeploymentServer implements Runnable {

	private ServerSocket listenerSocket;
	private BundleContext context;
	private volatile Thread thr;

	public DeploymentServer(BundleContext context) {
		this.context = context;
	}

	public void start() {
		setup();
		thr = new Thread(this);
		thr.start();
	}

	public void stop() {
		thr = null;
		try {
			listenerSocket.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	private void setup() {
		File folder = new File(LoociPlatformConstants.LOOCI_TMP_FOLDER);
		if(!folder.exists()){
			folder.mkdir();
		}
		try {
			String prefAddr = LoociProperties.getProp("addr");
			if(prefAddr != null){
				LLog.out(this, "setting deployment server to new address: "+prefAddr);
				try{
					listenerSocket = new ServerSocket(LoociConstants.RECONFIGURATION_PORT,5, InetAddress.getByName(prefAddr));
				} catch (BindException e){
					LLog.out(this, "could not set server to selected address, using default" );
					listenerSocket = new ServerSocket(LoociConstants.RECONFIGURATION_PORT);		
				}
				
			}else{
				listenerSocket = new ServerSocket(LoociConstants.RECONFIGURATION_PORT);				
			}
			
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}

	public void run() {
		Thread thisThread = Thread.currentThread();
		while (thisThread == thr) {
			try {
				ClientConnection sc = new ClientConnection(
						listenerSocket.accept(), context,LoociPlatformConstants.LOOCI_TMP_FOLDER);
				sc.start();
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		}
	}

	

}

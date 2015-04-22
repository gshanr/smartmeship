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
package looci.osgi.mgtGui;


import looci.osgi.gui.serv.GuiInterface;
import looci.osgi.mgtGui.lib.InstanceSelectionListener;
import looci.osgi.mgtGui.lib.NodeSelectionListener;
import looci.osgi.mgtGui.nodeInterface.EventSendPanel;
import looci.osgi.mgtGui.nodeInterface.NodeDeployPanel;
import looci.osgi.mgtGui.nodeInterface.NodePanel;
import looci.osgi.mgtGui.nodeInterface.NodePropertyPanel;
import looci.osgi.mgtGui.nodeInterface.NodeWire;
import looci.osgi.serv.constants.LoociRuntimes;
import looci.osgi.servExt.appInfo.InstanceInfo;
import looci.osgi.servExt.appInfo.LoociNodeInfo;
import looci.osgi.servExt.mgt.IDeployerAPI;
import looci.osgi.servExt.mgt.ILoociAPI;


public class LoociClient implements NodeSelectionListener,InstanceSelectionListener {
	
	private ILoociAPI eventAPI;
	
	private GuiInterface handler;
	private NodePanel panel;
	private NodeDeployPanel nodeDeployPanel;
	private NodeWire wiringPanel;
	private NodePropertyPanel propertyPanel;
	private EventSendPanel eventPanel;
	
	private String[] names;
	private IDeployerAPI deployment;
	

	
	public LoociClient(ILoociAPI eventAPI,IDeployerAPI deployment,GuiInterface gui){
		this.eventAPI = eventAPI;
		this.deployment = deployment;
		this.handler = gui;
	}
	
	
	
	public void startClient() throws Exception{
		
		
		names = LoociRuntimes.RUNTIME_NAMES;
		
		
		initGui();
	}
	
	public void stopClient(){
		handler.removeComponent(panel);
		handler.removeComponent(nodeDeployPanel);
		handler.removeComponent(wiringPanel);
		handler.removeComponent(propertyPanel);
		handler.removeComponent(eventPanel);
	}
	
	
	private void initGui(){		
		panel = new NodePanel(handler,eventAPI,names,this);
		nodeDeployPanel = new NodeDeployPanel(eventAPI,deployment);
		wiringPanel = new NodeWire( eventAPI,names);
		propertyPanel = new NodePropertyPanel( eventAPI, names);
		eventPanel = new EventSendPanel(eventAPI);	


		
		handler.addComponent(panel, "Node info", "Panel to querry a node");
		handler.addComponent(nodeDeployPanel, "Node deployment", "Panel to deploy components to node");
		handler.addComponent(wiringPanel, "Wirings", "wiring panel");
		handler.addComponent(propertyPanel, "Propertie mgt", "Panel to manage properties of an instance");
		handler.addComponent(eventPanel, "Event sender", "Panel to send events");		

	}
	
	public void notifyError(String source, int error){
		//handler.getShell().printLine("Error received:" + source + 
		//		" reported error " + error + ": "+ LooCIConstants.ERROR_CODES[-error]);
		//handler.showError(error, LooCIConstants.ERROR_CODES[-error]);
	}
	
	@Override
	public void setNodeInfo(LoociNodeInfo info) {
		System.out.println("Client set node info : "+info.getNodeId());
		panel.setNodeInfo(info);
		nodeDeployPanel.setNodeInfo(info);
		wiringPanel.setNodeInfo(info);
		propertyPanel.setNodeInfo(info);
		System.out.println("end set node info");
	}

	@Override
	public void setInstanceInfo(InstanceInfo info){
		propertyPanel.setInstanceInfo(info);
		eventPanel.setInstanceInfo(info);
	}
	
}

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
package looci.osgi.visualizer.lib;
import java.awt.Color;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import looci.osgi.serv.bindings.LocalBinding;
import looci.osgi.serv.bindings.RemoteFromBinding;
import looci.osgi.serv.bindings.RemoteToBinding;
import looci.osgi.serv.constants.EventTypeRepository;
import looci.osgi.serv.constants.EventTypes;
import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.servExt.mgt.ILoociAPI;

public class Principal {


	
	private ILoociAPI api;
	private VisualGraph myGraph;
	
	public Principal(ILoociAPI api){
		this.api = api;
	}

	public void visualize(String[] nodes) {

		// The graph itself.
		myGraph = new VisualGraph("Demo");
		componentNames = new HashMap<String, String>();
		
			
		try{

			for(int i = 0 ; i < nodes.length ; i++){
				visualizeNode(nodes[i]);
			}
			for(int i = 0 ; i < nodes.length ; i++){
				visualizeLocalWires(nodes[i]);
				visualizeIncomingWires(nodes[i]);
				visualizeOutgoingWires(nodes[i]);
			}


			

			// example(a);

			// Make visible the graph.
			myGraph.setVisible(true);
			
		} catch(Exception e){
			e.printStackTrace();
		}



		

	}

	private void visualizeNode(String nodeID) throws LoociManagementException,UnknownHostException{
		byte[] codebases = api.getComponentIDs(nodeID);
	
		for(int i =0 ; i < codebases.length ; i ++){
			visualizeComponent(nodeID, codebases[i]);
		}
		
	}
	
	private HashMap<String, String> componentNames;
	
	private String getCannonHostId(String nodeId){

		try {
			InetAddress addr = InetAddress.getByName(nodeId);
			nodeId = addr.getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return nodeId;
	}
	
	private void visualizeComponent(String nodeId, byte componentId) throws LoociManagementException,UnknownHostException{
		nodeId = getCannonHostId(nodeId);
		short[] interfaces = api.getInterfaces(componentId, nodeId);
		short[] receptacles = api.getReceptacles(componentId, nodeId);
		String[] intString = convertEventTypes(interfaces);
		String[] recString = convertEventTypes(receptacles);
		String compName = api.getComponentName(componentId, nodeId);
		String uniCompName = componentId + "@" + nodeId;
		compName += ":" + uniCompName;
		componentNames.put(uniCompName, compName);
		
		Component c = myGraph.panel.addComponent(compName,recString, intString);
		
		byte state = api.getState(componentId, nodeId);
		if(state == 1){
			c.animColour = Color.GREEN;			
		} else{
			c.animColour = Color.RED;
		}
	}
	
	private String[] convertEventTypes(short[] events){
		String[] retVal = new String[events.length];
		for(int i = 0 ; i < retVal.length ; i ++){
			retVal[i] = EventTypeRepository.getInstance().getEventStringFromType(events[i]);
		}
		return retVal;
	}
	
	private void visualizeLocalWires(String nodeId) throws LoociManagementException,UnknownHostException{
		nodeId = getCannonHostId(nodeId);
		LocalBinding[] localbinding = api.getLocalWires((short)0,(byte) 0,(byte) 0, nodeId);
		for(int i = 0 ; i < localbinding.length ; i++){
			String eventName = EventTypeRepository.getInstance().getEventStringFromType(localbinding[i].getEventID());
			String srcCompName = localbinding[i].getSourceComponentID() + "@" + nodeId;
			String dstCompName = localbinding[i].getDestinationComponentID() + "@" + nodeId;
			
			
			myGraph.panel.addLink(componentNames.get(srcCompName),
					componentNames.get(dstCompName),
					eventName);
		}
	}
	

	private void visualizeOutgoingWires(String nodeId) throws LoociManagementException,UnknownHostException{
		nodeId = getCannonHostId(nodeId);
		RemoteToBinding[] bindings = api.getOutgoingRemoteWires(EventTypes.ANY_EVENT,
				LoociConstants.COMPONENT_WILDCARD,nodeId, LoociConstants.ADDR_ANY);
				
		for(int i = 0 ; i < bindings.length ; i++){
			String eventName = EventTypeRepository.getInstance().getEventStringFromType(bindings[i].getEventID());
			String srcCompName = bindings[i].getSourceComponentID() + "@" + nodeId;
			String dstCompName = "ev:"+eventName + "@" + getCannonHostId(bindings[i].getDestinationNode());
			
			System.out.println("draw " +srcCompName + " to " + dstCompName);
			System.out.println(componentNames.get(srcCompName));
			
			if(myGraph.panel.findComponent(dstCompName) == null){
				myGraph.panel.addComponent(dstCompName,new String[]{eventName}, new String[]{});
			}
			
			myGraph.panel.addLink(componentNames.get(srcCompName),
					dstCompName,
					eventName);
		}
	}
	
	private void visualizeIncomingWires(String nodeId) throws LoociManagementException,UnknownHostException{
		nodeId = getCannonHostId(nodeId);
		RemoteFromBinding[] bindings = api.getIncomingRemoteWires(EventTypes.ANY_EVENT,
				LoociConstants.COMPONENT_WILDCARD, LoociConstants.ADDR_ANY, LoociConstants.COMPONENT_WILDCARD, nodeId);
				
		for(int i = 0 ; i < bindings.length ; i++){
			String eventName = EventTypeRepository.getInstance().getEventStringFromType(bindings[i].getEventID());
			String srcCompName = bindings[i].getSourceComponentID() + "@" + getCannonHostId(bindings[i].getSourceNode());
			String dstCompName = bindings[i].getDestinationComponentID() + "@" + nodeId;
			
			System.out.println("draw " +srcCompName + " to " + dstCompName);
			
			String longSrc = componentNames.get(srcCompName);
			String longDst = componentNames.get(dstCompName);
			
			if(longSrc == null && bindings[i].getSourceComponentID() != 0){
				try{
					visualizeComponent(bindings[i].getSourceNode(), bindings[i].getSourceComponentID());
					longSrc = componentNames.get(dstCompName);
				} catch(Exception e){
					
				}
			}
			if(longSrc == null){
				longSrc = eventName+":"+srcCompName;
				if(myGraph.panel.findComponent(longSrc) == null){
					myGraph.panel.addComponent(longSrc,new String[]{}, new String[]{eventName});
				}
			}			

			
			myGraph.panel.addLink(longSrc,
					longDst,
					eventName);
		}
	}
}

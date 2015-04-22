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
package looci.osgi.servExt.mgt;

import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.naming.event.EventContext;

import looci.osgi.serv.bindings.LocalBinding;
import looci.osgi.serv.bindings.RemoteFromBinding;
import looci.osgi.serv.bindings.RemoteToBinding;
import looci.osgi.serv.constants.EventType;
import looci.osgi.serv.constants.EventTypeRepository;
import looci.osgi.serv.constants.EventTypes;
import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.constants.LoociRuntimes;
import looci.osgi.serv.impl.property.PropertyInfo;
import looci.osgi.serv.util.Utils;
import looci.osgi.serv.util.XString;

public class LoociCommandList implements IServiceCommandList{

	
	private ArrayList<ServiceCommand> loociCommands;
	
	private IDeployerAPI deployer;
	private ILoociAPI api;
	
	
	public LoociCommandList(IDeployerAPI loociDeployer, ILoociAPI loociApi){
		this.deployer = loociDeployer;
		this.api = loociApi;
		loociCommands = new ArrayList<ServiceCommand>();
		loadLooCICommands();		
	}
	

	private void loadLooCICommands(){
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {
				return "Deploys the given file to the given node address. Node type must match target node. \r\nEnter 'getNodeTypes' to see available node types.";}			
			@Override
			public String getCommand() {return "deploy";}			
			@Override
			public String getArgs() {return "file - address - nodetype";}
			@Override
			public String doCommand(  String[] command) throws LoociManagementException {
				String file = command[1];
				String address = command[2];
				String nodeType = command[3];
				byte r;
				try {
					r = deployer.deploy(nodeType,file,address);
					return Byte.toString(r);
				} catch (Exception e) {
					e.printStackTrace();
					return "error while deploying:"+ e.getMessage();
				}
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {
				return "Deploys the given file to the given node address. Node type must match target node. \r\n" +
						"Enter 'getNodeTypes' to see available node types.\r\n" +
						"Next it instantiates the installed codebase, and activates the component";}			
			@Override
			public String getCommand() {return "deployInstAct";}			
			@Override
			public String getArgs() {return "file - address - nodetype";}
			@Override
			public String doCommand(  String[] command) throws LoociManagementException,UnknownHostException {
				String file = command[1];
				String address = command[2];
				String nodeType = command[3];
				byte cbId;
				try {
					cbId = deployer.deploy(nodeType,file,address);
					String reply = "codebase id: "+cbId + " \r\n";
					System.out.println("deploy successful : "+cbId);
					synchronized (this) {
						this.wait(5000);
					}
					byte cmpId = api.instantiate(cbId, address);
					System.out.println("inst successful : "+cmpId);
					reply += "component id: "+cmpId + "\r\n";
					synchronized (this) {
						this.wait(500);
					}
					api.activate(cmpId, address);
					reply += "activated succesful";					
					return reply;
				}catch (LoociManagementException e) {
					throw e;
				} catch (UnknownHostException e) {
					throw e;
				} catch (Exception e) {
					e.printStackTrace();
					return "error while deploying:"+ e.getMessage();
				}
			}
		});
		
		
		//
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Removes codebase with given id.";}			
			@Override
			public String getCommand() {return "removeCodebase";}			
			@Override
			public String getArgs() {return "codebase id - address";}
			@Override
			public String doCommand(  String[] command) throws LoociManagementException,UnknownHostException {
				byte componentID = Byte.parseByte(command[1]);
				String address = command[2];
				api.remove(componentID, address);
				return "success";		
			}
		});
		//
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Instantiate given codebase.";}			
			@Override
			public String getCommand() {return "instantiate";}			
			@Override
			public String getArgs() {return "codebase id - address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				byte component = Byte.parseByte(command[1]);
				String nodeId = command[2];					
				byte reply =  api.instantiate(component, nodeId);
				return Byte.toString(reply);	
			}
		});
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "destroy given component";}			
			@Override
			public String getCommand() {return "destroyComponent";}			
			@Override
//			public String getArgs() {return "?cid ?address";}
            public String getArgs() {return "component id - address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				byte instance = Byte.parseByte(command[1]);
				String nodeId = command[2];					
				api.destroy(instance, nodeId);
				return "success";			
			}
		});
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Activate given component.";}			
			@Override
			public String getCommand() {return "activate";}			
			@Override
			public String getArgs() {return "component id - address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				byte componentID = Byte.parseByte(command[1]);
				String address = command[2];
				api.activate(componentID, address);
				return "success";			
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Deactivate given component.";}			
			@Override
			public String getCommand() {return "deactivate";}			
			@Override
			public String getArgs() {return "component id - address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				byte componentID = Byte.parseByte(command[1]);
				String address = command[2];
				api.deactivate(componentID, address);
				return "success";				
			}
		});
		
		loociCommands.add(new ServiceCommand(){
			@Override
			public String getExtendedHelp() {return "Perform a wire between two components.";}			
			@Override
			public String getCommand() {return "wire";}			
			@Override
			public String getArgs() {return "event type, source component, source node, destination component, destination node";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				short ifaceID = EventTypeRepository.getInstance().getEventTypeFromString(command[1]);
				byte srcCompID = Byte.parseByte(command[2]);
				String srcNode = command[3];
				byte dstCompID = Byte.parseByte(command[4]);
				String dstNode = command[5];
				
				if(srcNode.equals(dstNode)){
					api.wireLocal(ifaceID,srcCompID,dstCompID,srcNode);
				} else{
					api.wireTo(ifaceID, srcCompID, srcNode, dstNode);
					api.wireFrom(ifaceID, srcCompID, srcNode, dstCompID, dstNode);
				}
				
				return "success";				
			}
		});
		
		loociCommands.add(new ServiceCommand(){
			@Override
			public String getExtendedHelp() {return "Removes a wire between two components.";}			
			@Override
			public String getCommand() {return "unwire";}			
			@Override
			public String getArgs() {return "event type, source component, source node, destination component, destination node";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				short ifaceID = EventTypeRepository.getInstance().getEventTypeFromString(command[1]);
				byte srcCompID = Byte.parseByte(command[2]);
				String srcNode = command[3];
				byte dstCompID = Byte.parseByte(command[4]);
				String dstNode = command[5];
				
				if(srcNode.equals(dstNode)){
					api.unwireLocal(ifaceID,srcCompID,dstCompID,srcNode);
				} else{
					api.unwireTo(ifaceID, srcCompID, srcNode, dstNode);
					api.unwireFrom(ifaceID, srcCompID, srcNode, dstCompID, dstNode);
				}
				
				return "success";				
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Perform a local wiring. \r\nEnter 'getEventTypes' to see available event types.";}			
			@Override
			public String getCommand() {return "wireLocal";}			
			@Override
			public String getArgs() {return "event id - src component id - dst component id - address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				short ifaceID = EventTypeRepository.getInstance().getEventTypeFromString(command[1]);
				byte compID = Byte.parseByte(command[2]);
				byte destCompID = Byte.parseByte(command[3]);
				String address = command[4];
				api.wireLocal(ifaceID, compID, destCompID, address);
				return "success";				
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Remove a local wire.";}			
			@Override
			public String getCommand() {return "unwireLocal";}			
			@Override
			public String getArgs() {return "event id - src component id - dst component id - address";}
			@Override
			public String doCommand( String[] command)  throws LoociManagementException,UnknownHostException{
				short ifaceID = EventTypeRepository.getInstance().getEventTypeFromString(command[1]);
				byte compID = Byte.parseByte(command[2]);
				byte destCompID = Byte.parseByte(command[3]);
				String address = command[4];
				api.unwireLocal(ifaceID, compID, destCompID, address);
				return "success";			
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Perform an incoming wire. \r\nEnter 'getEventTypes' to see available event types.";}			
			@Override
			public String getCommand() {return "wireFrom";}			
			@Override
//			public String getArgs() {return "?eventId ?srcCid ?srcAddr ?dstCid ?dstAddr";}			
                        public String getArgs() {return "event id - src component id - src address - dst component id - dst address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				short ifaceID = EventTypeRepository.getInstance().getEventTypeFromString(command[1]);
				byte compID = Byte.parseByte(command[2]);
				String src_addr = command[3];
				byte dst_comp = Byte.parseByte(command[4]);
				String address = command[5];
				api.wireFrom(ifaceID, compID, src_addr,	dst_comp, address);
				return "success";		
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Remove an incoming wire.";}			
			@Override
			public String getCommand() {return "unwireFrom";}			
			@Override
			public String getArgs() {return "event id - src component id - src address - dst component id - dst address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				short ifaceID = EventTypeRepository.getInstance().getEventTypeFromString(command[1]);
				byte compID = Byte.parseByte(command[2]);
				String src_addr = command[3];
				byte dst_comp = Byte.parseByte(command[4]);
				String address = command[5];
				api.unwireFrom(ifaceID, compID, src_addr,dst_comp, address);
				return"success";			
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Perform an outgoing wire. \r\nEnter 'getEventTypes' to see available event types.";}			
			@Override
			public String getCommand() {return "wireTo";}			
			@Override
			public String getArgs() {return "event id - src component id - src address - dst address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				short ifaceID = EventTypeRepository.getInstance().getEventTypeFromString(command[1]);
				byte compID = Byte.parseByte(command[2]);
				String src_address = command[3];
				String dst_address = command[4];
				api.wireTo(ifaceID, compID, src_address,dst_address);
				return "success";			
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Remove an outgoing wire.";}			
			@Override
			public String getCommand() {return "unwireTo";}			
			@Override
			public String getArgs() {return "event id - src component id - src address - dst address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				short ifaceID = EventTypeRepository.getInstance().getEventTypeFromString(command[1]);
				byte compID = Byte.parseByte(command[2]);
				String src_address = command[3];
				String dst_address = command[4];
				api.unwireTo(ifaceID, compID, src_address,dst_address);
				return "success";		
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Remove all wires from a component.";}			
			@Override
			public String getCommand() {return "resetWires";}			
			@Override
			public String getArgs() {return "component id - address";}
			@Override
			public String doCommand(String[] command)  throws LoociManagementException,UnknownHostException{
				byte compID = Byte.parseByte(command[1]);
				String address = command[2];
				api.resetWirings(compID, address);
				return "success";		
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Set the property value of a component. Type getPropertyTypes to list of types.";}			
			@Override
			public String getCommand() {return "setProperty";}			
			@Override
//			public String getArgs() {return "?propertyId ?cid ?address ?value ?type";}			
                        public String getArgs() {return "property id - component id - address - property value - property type";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				short propertyId = Short.parseShort(command[1]);
				byte instance_id = Byte.parseByte(command[2]);
				String address = command[3];
				String propValue = command[4];
				String type = command[5];
				byte[] prop = Utils.createByteArrayFromTypeString(type, propValue);
				api.setProperty(prop, propertyId, instance_id, address);
				return "success";		
			}
		});
	
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get the id of a codebase with given name.";}			
			@Override
			public String getCommand() {return "getCodebaseIDbyName";}			
			@Override
//			public String getArgs() {return "?name ?address";}
                        public String getArgs() {return "codebase name - address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				String comp_type = command[1];
				String address = command[2];
				byte[] b= api.getCodebaseIDsByName(comp_type, address);
				return XString.printByteArray(b);	
			}
		});
	
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get available codebase ids.";}			
			@Override
			public String getCommand() {return "getCodebaseIDs";}			
			@Override
			public String getArgs() {return "address";}		
			@Override
			public String doCommand( String[] command)  throws LoociManagementException,UnknownHostException{
				String address = command[1];
				byte[] b= api.getCodebaseIDs(address);
				return XString.printByteArray(b);
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get components of the given codebase.";}			
			@Override
			public String getCommand() {return "getComponentsOfCodebase";}			
			@Override
//			public String getArgs() {return "?cbid ?address";}			
                        public String getArgs() {return "codebase id - address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				byte cb_id = Byte.parseByte(command[1]);
				String address = command[2];
				byte[] b= api.getComponentIDsbyCodebaseID(cb_id, address);
				return XString.printByteArray(b);
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get the name of the given codebase id.";}			
			@Override
			public String getCommand() {return "getCodebaseName";}			
			@Override
			public String getArgs() {return "codebase id - address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				byte cb_id = Byte.parseByte(command[1]);
				String address = command[2];
				return api.getCodebaseName(cb_id, address);
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get the codebase id of the given component.";}			
			@Override
			public String getCommand() {return "getCodebaseIDOfComponent";}			
			@Override
			public String getArgs() {return "component id - address";}
			@Override
			public String doCommand(String[] command)  throws LoociManagementException,UnknownHostException{
				byte comp_id = Byte.parseByte(command[1]);
				String address = command[2];
				byte b= api.getCodebaseIdOfComponent(comp_id, address);
				return Byte.toString(b);
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get the codebase name of the given component.";}			
			@Override
			public String getCommand() {return "getCodebaseNameOfComponent";}			
			@Override
//			public String getArgs() {return "?cid ?address";}
                        public String getArgs() {return "component id - address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				byte comp_id = Byte.parseByte(command[1]);
				String address = command[2];
				return api.getComponentName(comp_id, address);
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get the available component ids.";}			
			@Override
			public String getCommand() {return "getComponentIDs";}			
			@Override
			public String getArgs() {return "address";}			
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				String address = command[1];
				byte[] b = api.getComponentIDs(address);
				return XString.printByteArray(b);
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get the state of the given component.";}			
			@Override
			public String getCommand() {return "getState";}			
			@Override
			public String getArgs() {return "component id - address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				byte component_id = Byte.parseByte(command[1]);
				String address = command[2];
				byte b= api.getState(component_id, address);
				return Byte.toString(b);
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get the provided interfaces of this component (events that this component publishes).";}			
			@Override
			public String getCommand() {return "getProvidedInterfaces";}			
			@Override
			public String getArgs() {return "component id - address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				byte component_id = Byte.parseByte(command[1]);
				String address = command[2];
				short[] b= api.getInterfaces(component_id, address);
				return EventTypeRepository.printEventArray(b);
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get the required interfaces of this component (events that this component receives).";}			
			@Override
			public String getCommand() {return "getRequiredInterfaces";}			
			@Override
			public String getArgs() {return "component id - address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				byte component_id = Byte.parseByte(command[1]);
				String address = command[2];
				short[] b= api.getReceptacles(component_id, address);
				return EventTypeRepository.printEventArray(b);
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get the local wires.\r\n 0 can be used as event and component wildcard.";}			
			@Override
			public String getCommand() {return "getLocalWires";}			
			@Override

			public String getArgs() {return "event id - src component id - dst component id - address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				short ifaceID = EventTypeRepository.getInstance().getEventTypeFromString(command[1]);
				byte src_component_id = Byte.parseByte(command[2]);
				byte dst_component_id = Byte.parseByte(command[3]);
				String address = command[4];
				LocalBinding[] b= api.getLocalWires(ifaceID, src_component_id,dst_component_id, address);
				String response = "{";
				for(int i =0 ; i < b.length; i ++){
					response += EventTypeRepository.getInstance().getEventStringFromType(b[i].getEventID()) + ",";
					response += b[i].getSourceComponentID()+ ",";
					response += b[i].getDestinationComponentID();
					response += ";";					
				}
				response += "}";
				return response;
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get all the local wires.\r\n";}			
			@Override
			public String getCommand() {return "getAllLocalWires";}			
			@Override

			public String getArgs() {return "address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				short ifaceID = EventTypes.ANY_EVENT;
				byte src_component_id = LoociConstants.COMPONENT_WILDCARD;
				byte dst_component_id =  LoociConstants.COMPONENT_WILDCARD;
				String address = command[1];
				LocalBinding[] b= api.getLocalWires(ifaceID, src_component_id,dst_component_id, address);
				String response = "{";
				for(int i =0 ; i < b.length; i ++){
					response += EventTypeRepository.getInstance().getEventStringFromType(b[i].getEventID()) + ",";
					response += b[i].getSourceComponentID()+ ",";
					response += b[i].getDestinationComponentID();
					response += ";";					
				}
				response += "}";
				return response;
			}
		});
		
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get the outgoing wires.\r\n 0 can be used as event and component wildcard, '::' can be used as destination wildcard.";}			
			@Override
			public String getCommand() {return "getWiresTo";}			
			@Override
//			public String getArgs() {return "?eventId ?srcCid ?dstAddr ?Addr";}			
                        public String getArgs() {return "event id - src component id - src address - dst address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				short ifaceID = EventTypeRepository.getInstance().getEventTypeFromString(command[1]);
				byte component_id = Byte.parseByte(command[2]);
				String src_address = command[3];
				String dst_address = command[4];
				RemoteToBinding[] b= api.getOutgoingRemoteWires(ifaceID, component_id, src_address,dst_address);
				String response = "{";
				for(int i =0 ; i < b.length; i ++){
					response += EventTypeRepository.getInstance().getEventStringFromType(b[i].getEventID()) + ",";
					response += b[i].getSourceComponentID()+ ",";
					response += b[i].getDestinationNode();
					response += ";";					
				}
				response += "}";
				return response;
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get all the outgoing wires of a target node.\r\n";}			
			@Override
			public String getCommand() {return "getAllToWires";}			
			@Override
//			public String getArgs() {return "?eventId ?srcCid ?dstAddr ?Addr";}			
                        public String getArgs() {return "src address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				short ifaceID = EventTypes.ANY_EVENT;
				byte component_id = LoociConstants.COMPONENT_WILDCARD;
				String src_address = command[1];
				String dst_address = LoociConstants.ADDR_ANY;
				RemoteToBinding[] b= api.getOutgoingRemoteWires(ifaceID, component_id, src_address,dst_address);
				String response = "{";
				for(int i =0 ; i < b.length; i ++){
					response += EventTypeRepository.getInstance().getEventStringFromType(b[i].getEventID()) + ",";
					response += b[i].getSourceComponentID()+ ",";
					response += b[i].getDestinationNode();
					response += ";";					
				}
				response += "}";
				return response;
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get the incoming wires.\r\n 0 can be used as event and component wildcard, '::' can be used as source wildcard.";}			
			@Override
			public String getCommand() {return "getWiresFrom";}			
			@Override
			public String getArgs() {return "event id - src component id - src address - dst component id - dst address";}	
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				short ifaceID = EventTypeRepository.getInstance().getEventTypeFromString(command[1]);
				byte src_component_id = Byte.parseByte(command[2]);
				String src_address = command[3];
				byte dst_component_id = Byte.parseByte(command[4]);
				String dst_address = command[5];
				
				RemoteFromBinding[] b= api.getIncomingRemoteWires(ifaceID, src_component_id, src_address, dst_component_id,dst_address);
				String response = "{";
				for(int i =0 ; i < b.length; i ++){
					response += EventTypeRepository.getInstance().getEventStringFromType(b[i].getEventID()) + ",";
					response += b[i].getSourceComponentID()+ ",";
					response += b[i].getSourceNode()+ ",";					
					response += b[i].getDestinationComponentID();
					response += ";";					
				}
				response += "}";	
				return response;
			}
		});
		
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get all the incoming wires.";}			
			@Override
			public String getCommand() {return "getAllFromWires";}			
			@Override
			public String getArgs() {return "dst address";}	
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				short ifaceID = EventTypes.ANY_EVENT;
				byte src_component_id = LoociConstants.COMPONENT_WILDCARD;
				String src_address = LoociConstants.ADDR_ANY;
				byte dst_component_id = LoociConstants.COMPONENT_WILDCARD;
				String dst_address = command[1];
				
				RemoteFromBinding[] b= api.getIncomingRemoteWires(ifaceID, src_component_id, src_address, dst_component_id,dst_address);
				String response = "{";
				for(int i =0 ; i < b.length; i ++){
					response += EventTypeRepository.getInstance().getEventStringFromType(b[i].getEventID()) + ",";
					response += b[i].getSourceComponentID()+ ",";
					response += b[i].getSourceNode()+ ",";					
					response += b[i].getDestinationComponentID();
					response += ";";					
				}
				response += "}";	
				return response;
			}
		});
		
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get the property ids for this component.";}			
			@Override
			public String getCommand() {return "getProperties";}			
			@Override
			public String getArgs() {return "component id - address";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				byte instance_id = Byte.parseByte(command[1]);
				String address = command[2];
				
				short[] prop = api.getProperties(instance_id, address);
				return XString.printShortArray(prop);
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get the property data of the given property id of the given component.\r\nType getPropertyTypes to see list of available types.";}			
			@Override
			public String getCommand() {return "getProperty";}			
			@Override
			public String getArgs() {return "property id - component id - address - property type";}
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				short propertyId = Short.parseShort(command[1]);
				byte instance_id = Byte.parseByte(command[2]);
				String address = command[3];
				String type = command[4];
				byte[] prop = api.getProperty(propertyId, instance_id, address);
				return Utils.createStringFromByteContent(type, prop);
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get the property information of the given property id of the given component.";}			
			@Override
			public String getCommand() {return "getPropertyInfo";}			
			@Override
			public String getArgs() {return "property id - component id - address";}
			@Override
			public String doCommand( String[] command)  throws LoociManagementException,UnknownHostException{
				short propertyId = Short.parseShort(command[1]);
				byte instance_id = Byte.parseByte(command[2]);
				String address = command[3];
				PropertyInfo info = api.getPropertyInfo(propertyId, instance_id, address);
				return "type: " + info.getPropertyTypeName() + ", name: "+info.getPropertyName();
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Get the platform type of the node.";}			
			@Override
			public String getCommand() {return "getPlatformType";}			
			@Override
			public String getArgs() {return "address";}			
			@Override
			public String doCommand(  String[] command)  throws LoociManagementException,UnknownHostException{
				String address = command[1];
				byte type = api.getPlatformType(address);
				return LoociRuntimes.getRuntimeName(type);
			}
		});
		
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Send an event to the component on the given node.";}			
			@Override
			public String getCommand() {return "sendEvent";}			
			@Override
			public String getArgs() {return "event id - component id - address - payload - type";}
			@Override
			public String doCommand( String[] command)  throws LoociManagementException,UnknownHostException{
				short eventId = EventTypeRepository.getInstance().getEventTypeFromString(command[1]);
				byte instTarget = Byte.parseByte(command[2]);
				String nodeTarget = command[3];
				byte[] payload = Utils.createByteArrayFromTypeString(command[5], command[4]);
				api.sendEvent(eventId,payload,nodeTarget,instTarget);
				return "sent event";
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Send an event to the component on the given node and wait for a reply.";}			
			@Override
			public String getCommand() {return "sendEventWithReply";}			
			@Override
			public String getArgs() {return "event id - component id - address - reply event id - type - payload";}
			@Override
			public String doCommand( String[] command)  throws LoociManagementException,UnknownHostException{
				short eventId = Short.parseShort(command[1]);
				byte instTarget = Byte.parseByte(command[2]);
				String nodeTarget = command[3];
				short replyType = Short.parseShort(command[4]);
				byte[] payload = Utils.createByteArrayFromTypeString(command[6], command[5]);
				api.sendEventGetReply(eventId,payload,nodeTarget,instTarget,replyType);
				return "sent event";
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Return a list of currently supported event types.";}			
			@Override
			public String getCommand() {return "getEventTypes";}			
			@Override
			public String getArgs() {return "";}
			@Override
			public String doCommand( String[] command)  throws LoociManagementException{
		    	ArrayList<EventType> types = EventTypeRepository.getInstance().getEventTypeList();
		    	String answer = "";
		    	for(int i =0 ; i < types.size() ; i ++){
		    		EventType type = types.get(i);
		    		answer += type.getType() + ": " + type.getName() + "\r\n";
		    	}
		    	return answer;
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Adds a new event type to the list of currently supported event types.";}			
			@Override
			public String getCommand() {return "addEventType";}			
			@Override
			public String getArgs() {return "typeId typeName";}
			@Override
			public String doCommand(String[] command)  throws LoociManagementException{
				short id = Short.parseShort(command[1]);
				String name = command[2];
				EventType type = new EventType(id, name);				
				EventTypeRepository.getInstance().addEventType(type);	
		    	return "success";
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Removes an event type to the list of currently supported event types.";}			
			@Override
			public String getCommand() {return "removeEventType";}			
			@Override
			public String getArgs() {return "type";}
			@Override
			public String doCommand(String[] command)  throws LoociManagementException{
				short id = EventTypeRepository.getInstance().getEventTypeFromString(command[1]);
				if(EventTypeRepository.getInstance().removeType(id)){
					return "success";
				} else{
			    	return "event not found";					
				}
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "";}			
			@Override
			public String getCommand() {return "getPropertyTypes";}			
			@Override
			public String getArgs() {return "";}			
			@Override
			public String doCommand( String[] command)  throws LoociManagementException{
				String response = "List of supported datatypes : "+ "\r\n";
				response += XString.printString(LoociConstants.DATATYPE_STRINGS);
				return response;
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "";}			
			@Override
			public String getCommand() {return "getNodeTypes";}			
			@Override
			public String getArgs() {return "";}			
			@Override
			public String doCommand( String[] command)  throws LoociManagementException{
				String response = "List of supported runtimes : "+ "\r\n";
				response += XString.printString(LoociRuntimes.RUNTIME_NAMES);
				return response;
			}
		});
		
		loociCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "";}			
			@Override
			public String getCommand() {return "discover";}			
			@Override
			public String getArgs() {return "";}			
			@Override
			public String doCommand( String[] command)  throws LoociManagementException{
				String response = "List of available nodes : "+ "\r\n";
				String[] nodes = api.discover();
				response += XString.printString(nodes);
				return response;
			}
		});
    }

	@Override
	public String commandListName() {
		return "LooCI";
	}

	@Override
	public ArrayList<ServiceCommand> getServiceCommands() {
		return loociCommands;
	}
	
	public void registerServiceClient(ServiceClient client){
		client.setWorkingDirectory(LoociConstants.COMPONENT_DIR);
	}
	
}

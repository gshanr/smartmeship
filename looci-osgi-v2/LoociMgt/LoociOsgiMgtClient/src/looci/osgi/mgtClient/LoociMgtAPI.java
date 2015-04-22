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
package looci.osgi.mgtClient;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import looci.osgi.serv.bindings.LocalBinding;
import looci.osgi.serv.bindings.RemoteFromBinding;
import looci.osgi.serv.bindings.RemoteToBinding;
import looci.osgi.serv.components.Event;
import looci.osgi.serv.components.HeaderManagement;
import looci.osgi.serv.constants.ErrorCodes;
import looci.osgi.serv.constants.EventTypes;
import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.impl.ShortPayloadBuilder;
import looci.osgi.serv.impl.property.PropertyInfo;
import looci.osgi.servExt.mgt.IEventSendAPI;
import looci.osgi.servExt.mgt.ILoociAPI;
import looci.osgi.servExt.mgt.MgtReply;


public class LoociMgtAPI implements ILoociAPI{
	
	IEventSendAPI sender;
	private static final int TIMEOUT = 4000;
	private static final byte MGT_COMP_ID = 1;
	
	private MgtReply lastReply;
	
	public LoociMgtAPI(IEventSendAPI api){
		sender = api;
		lastReply = null;
	}

	
	private ShortPayloadBuilder sendEventWaitMgtReply(Event event, short reply) throws LoociManagementException{
		HeaderManagement.setDestinationComp(event, MGT_COMP_ID);
		Event ev = sender.sendEventAndWaitforReply(event, reply, TIMEOUT);
		if(ev != null){
			MgtReply rep = new MgtReply(ev.getPayload());
			lastReply = rep;
			if(!rep.isSucces()){
				throw new LoociManagementException(rep.getCode());
			} else{
				return rep.getPb();
			}
		} else{
			lastReply = new MgtReply(ErrorCodes.ERROR_CODE_TIMEOUT);
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_TIMEOUT);
		}
	}
	
	
	/////////////////////////////
	// Reconfig
	/////////////////////////////
	@Override
        public byte deploy(String codebaseName, String nodeID)  throws LoociManagementException ,UnknownHostException{
            ShortPayloadBuilder builder = new ShortPayloadBuilder();
			builder.putNTStringAt(0, codebaseName);
			Event ev = new Event(EventTypes.INSTALL_CODEBASE_EV, builder.getSizedPayload());
			ev.setDestinationAddress(nodeID);
			
			ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.INSTALL_APPROVE_EV);

	        byte[] payload = rep.getPayload();
	        return payload[payload.length - 1];

        } 
	

	
	@Override
	public void remove(byte comp_id, String nodeID)  throws LoociManagementException ,UnknownHostException{
		ShortPayloadBuilder builder = new ShortPayloadBuilder();
		builder.putByteAt(0, comp_id);
		
		Event ev = new Event(EventTypes.REMOVE_CODEBASE_EV, builder.getSizedPayload());
		ev.setDestinationAddress(nodeID);		
		sendEventWaitMgtReply(ev, EventTypes.REMOVED_EV);
	}
	
	
	@Override
	public byte instantiate(byte comp_id, String nodeID) throws LoociManagementException,UnknownHostException {
		ShortPayloadBuilder builder = new ShortPayloadBuilder();
		builder.putByteAt(0, comp_id);
		
		Event ev = new Event(EventTypes.INSTANTIATE_CMP_EV, builder.getSizedPayload());
		ev.setDestinationAddress(nodeID);		
		ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.INSTANTIATED_EV);		
		rep.skip(builder.getSize());
		return rep.getByte();
	}
	
	@Override
	public void destroy(byte inst_id, String nodeID)  throws LoociManagementException,UnknownHostException{
		ShortPayloadBuilder builder = new ShortPayloadBuilder();
		builder.putByteAt(0, inst_id);
		
		Event ev = new Event(EventTypes.DESTROY_CMP_EV, builder.getSizedPayload());
		ev.setDestinationAddress(nodeID);		
		sendEventWaitMgtReply(ev, EventTypes.DESTROYED_EV);		
	}
	
	
	@Override
	public void deactivate(byte instanceID, String nodeID)  throws LoociManagementException,UnknownHostException{
		Event ev = new Event(EventTypes.STOP_COMPONENT_EV, new byte[]{instanceID});
		ev.setDestinationAddress(nodeID);		
		sendEventWaitMgtReply(ev, EventTypes.STOPPED_EV);
	}
	
	@Override
	public void activate(byte instanceID, String nodeID)  throws LoociManagementException,UnknownHostException{
		Event ev = new Event(EventTypes.START_COMPONENT_EV, new byte[]{instanceID});
		ev.setDestinationAddress(nodeID);		
		sendEventWaitMgtReply(ev, EventTypes.STARTED_EV);		
	}
	
	@Override
	public void resetWirings(byte instanceID, String nodeID)  throws LoociManagementException,UnknownHostException{
		Event ev = new Event(EventTypes.RESET_WIRINGS_EV, new byte[]{instanceID});
		ev.setDestinationAddress(nodeID);		
		sendEventWaitMgtReply(ev, EventTypes.WIRES_RESET_EV);		
	}
	
	
	@Override
	public void wireLocal(short interfaceEvent, byte src_inst_id,
			byte dst_inst_id, String nodeID)  throws LoociManagementException,UnknownHostException{
		ShortPayloadBuilder builder = new ShortPayloadBuilder();
		builder.putShortAt(0, interfaceEvent);
		builder.putByteAt(2, src_inst_id);
		builder.putByteAt(3, dst_inst_id);
		
		Event ev = new Event(EventTypes.WIRE_LOCAL_EV, builder.getSizedPayload());
		ev.setDestinationAddress(nodeID);		
		sendEventWaitMgtReply(ev, EventTypes.WIRED_LOCAL_EV);		
	}
	
	@Override
	public void unwireLocal(short interfaceEvent, byte src_inst_id,
			byte dst_inst_id, String nodeID)  throws LoociManagementException,UnknownHostException {
		ShortPayloadBuilder builder = new ShortPayloadBuilder();
		builder.putShortAt(0, interfaceEvent);
		builder.putByteAt(2, src_inst_id);
		builder.putByteAt(3, dst_inst_id);
		
		Event ev = new Event(EventTypes.UNWIRE_LCL_EV, builder.getSizedPayload());
		ev.setDestinationAddress(nodeID);		
		sendEventWaitMgtReply(ev, EventTypes.UNWIRED_LCL_EV);		
	}
	
	@Override
	public void wireFrom(short interfaceEvent, byte src_inst_id,
			String sourceNodeID, byte dst_inst_id, String destNodeID) throws LoociManagementException,UnknownHostException {
		ShortPayloadBuilder builder = new ShortPayloadBuilder();
		builder.putShortAt(0, interfaceEvent);
		builder.putIpAt(2, sourceNodeID);
		builder.putByteAt(18, src_inst_id);
		builder.putByteAt(19, dst_inst_id);
		
		Event ev = new Event(EventTypes.WIRE_REM_FROM_EV, builder.getSizedPayload());
		ev.setDestinationAddress(destNodeID);		
		sendEventWaitMgtReply(ev, EventTypes.WIRED_REM_FROM_EV);		
	}
	
	@Override
	public void unwireFrom(short interfaceEvent, byte src_inst_id,
			String sourceNodeID, byte dst_inst_id, String destNodeID) throws LoociManagementException,UnknownHostException {
		ShortPayloadBuilder builder = new ShortPayloadBuilder();
		builder.putShortAt(0, interfaceEvent);
		builder.putIpAt(2, sourceNodeID);
		builder.putByteAt(18, src_inst_id);
		builder.putByteAt(19, dst_inst_id);
		
		Event ev = new Event(EventTypes.UNWIRE_REM_FROM_EV, builder.getSizedPayload());
		ev.setDestinationAddress(destNodeID);		
		sendEventWaitMgtReply(ev, EventTypes.UNWIRED_REM_FROM_EV);		
	}
	
	@Override
	public void wireTo(short interfaceEvent, byte src_inst_id,
			String sourceNodeID, String destNodeID) throws LoociManagementException,UnknownHostException {
		ShortPayloadBuilder builder = new ShortPayloadBuilder();
		builder.putShortAt(0, interfaceEvent);
		builder.putByteAt(2, src_inst_id);
		builder.putIpAt(3, destNodeID);
		
		Event ev = new Event(EventTypes.WIRE_REM_TO_EV, builder.getSizedPayload());
		ev.setDestinationAddress(sourceNodeID);		
		sendEventWaitMgtReply(ev, EventTypes.WIRED_REM_TO_EV);		
	}
	
	@Override
	public void unwireTo(short interfaceEvent, byte src_inst_id,
			String sourceNodeID, String destNodeID) throws LoociManagementException,UnknownHostException {
		ShortPayloadBuilder builder = new ShortPayloadBuilder();
		builder.putShortAt(0, interfaceEvent);
		builder.putByteAt(2, src_inst_id);
		builder.putIpAt(3, destNodeID);
		
		Event ev = new Event(EventTypes.UNWIRE_REM_TO_EV, builder.getSizedPayload());
		ev.setDestinationAddress(sourceNodeID);		
		sendEventWaitMgtReply(ev, EventTypes.UNWIRED_REM_TO_EV);		
	}
	
	
	@Override
	public void setProperty(byte[] propertyValue, short propertyId,
			byte instanceId, String nodeID) throws LoociManagementException,UnknownHostException {
		ShortPayloadBuilder builder = new ShortPayloadBuilder();
		builder.putByteAt(0, instanceId);
		builder.putShortAt(1, propertyId);
		builder.putByteAt(3, (byte)propertyValue.length);
		builder.putByteArrayAt(4, propertyValue);
		
		Event ev = new Event(EventTypes.SET_PROPERTY, builder.getSizedPayload());
		ev.setDestinationAddress(nodeID);		
		sendEventWaitMgtReply(ev, EventTypes.PROPERTY_SET_EV);
	}
	
	
	/////////////////////////////
	// Introspection
	/////////////////////////////
	
	//51
	@Override
	public byte[] getCodebaseIDs(String nodeID)  throws LoociManagementException,UnknownHostException {
		Event ev = new Event(EventTypes.GET_ALL_CODEBASE_IDS_EV, new byte[0]);
		ev.setDestinationAddress(nodeID);
		
		ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.ALL_CODEBASE_IDS_EV);
		return rep.getLenByteArray();
	}
	
	//52
	@Override
	public String getCodebaseName(byte codebaseID, String nodeID) throws LoociManagementException,UnknownHostException {
		Event ev = new Event(EventTypes.GET_NAME_OF_CB_ID_EV, new byte[]{codebaseID});
		ev.setDestinationAddress(nodeID);		
		ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.NAME_OF_CB_ID_EV);
		
			rep.getByte();
			return rep.getNTString();
	}
	
	//53
	@Override
	public byte[] getCodebaseIDsByName(String componentType, String nodeID) throws LoociManagementException,UnknownHostException {
		ShortPayloadBuilder builder = new ShortPayloadBuilder();
		builder.putNTStringAt(0, componentType);
		Event ev = new Event(EventTypes.GET_CB_ID_BY_NAME_EV, builder.getSizedPayload());
		ev.setDestinationAddress(nodeID);
		
		ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.CB_ID_BY_NAME_EV);
		rep.getNTString();
		byte nrComps = rep.getByte();
		return rep.getByteArray(nrComps);			
	}
	
	//54
	@Override
	public byte[] getComponentIDsbyCodebaseID(byte codebaseID, String nodeID) throws LoociManagementException,UnknownHostException {
		Event ev = new Event(EventTypes.GET_COMP_IDS_OF_CB_ID, new byte[]{codebaseID});
		ev.setDestinationAddress(nodeID);		
		ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.COMP_IDS_OF_CB_ID);
		
		rep.getByte();
		return rep.getLenByteArray();
	}
	
	//55
	@Override
	public byte getCodebaseIdOfComponent(byte instanceID, String nodeID) throws LoociManagementException,UnknownHostException {
		Event ev = new Event(EventTypes.GET_CB_ID_OF_COMP_ID_EV, new byte[]{instanceID});
		ev.setDestinationAddress(nodeID);		
		ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.CB_ID_OF_COMP_ID_EV);
		
		rep.getByte(); // the componentID
		return rep.getByte();
	}
	
	//56
	@Override
	public String getComponentName(byte componentID, String nodeID) throws LoociManagementException,UnknownHostException {
		Event ev = new Event(EventTypes.GET_NAME_OF_COMP_ID_EV, new byte[]{componentID});
		ev.setDestinationAddress(nodeID);		
		ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.NAME_OF_COMP_ID_EV);
		
		rep.getByte();
		return rep.getNTString();
	}
	
	//57
	@Override
	public byte[] getComponentIDs(String nodeID) throws LoociManagementException,UnknownHostException {
		Event ev = new Event(EventTypes.GET_ALL_COMPONENT_IDS_EV, new byte[0]);
		ev.setDestinationAddress(nodeID);
		
		ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.ALL_COMPONENT_IDS_EV);
		return rep.getLenByteArray();	
	}
	
	//58
	@Override
	public byte getState(byte instanceID, String nodeID) throws LoociManagementException,UnknownHostException {
		Event ev = new Event(EventTypes.GET_STATE_EV, new byte[]{instanceID});
		ev.setDestinationAddress(nodeID);		
		ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.STATE_EV);
		rep.getByte();
		return rep.getByte();
	}
	
	
	
	//59
	@Override
	public short[] getInterfaces(byte componentID, String nodeID) throws LoociManagementException,UnknownHostException {
		Event ev = new Event(EventTypes.GET_INTERFACES_EV, new byte[]{componentID});
		ev.setDestinationAddress(nodeID);
		
		ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.INTERFACES_EV);
		rep.getByte(); // the componentID;
		return rep.getLenShortArray();
	}
	
	//60
	@Override
	public short[] getReceptacles(byte componentID, String nodeID) throws LoociManagementException,UnknownHostException {
		Event ev = new Event(EventTypes.GET_RECEPTACLES_EV, new byte[]{componentID});
		ev.setDestinationAddress(nodeID);
		
		ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.RECEPTACLES_EV);
		
			rep.getByte(); // the componentID;
			return rep.getLenShortArray();		
	}
	
	
	//61
	@Override
	public short[] getProperties(byte compID, String nodeID) throws LoociManagementException,UnknownHostException {
		ShortPayloadBuilder builder = new ShortPayloadBuilder();
		builder.putByteAt(0, compID);
		
		Event ev = new Event(EventTypes.GET_PROPERTIES, builder.getSizedPayload());
		ev.setDestinationAddress(nodeID);		
		ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.PROPERTIES_EV);	
			rep.getByte(); // the componentID
			return rep.getLenShortArray();
	}
	
	//62
	@Override
	public byte[] getProperty(short propertyId, byte instanceId, String nodeID) throws LoociManagementException,UnknownHostException {
		ShortPayloadBuilder builder = new ShortPayloadBuilder();
		builder.putByteAt(0, instanceId);
		builder.putShortAt(1, propertyId);
		
		Event ev = new Event(EventTypes.GET_PROPERTY, builder.getSizedPayload());
		ev.setDestinationAddress(nodeID);		
		ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.PROPERTY_EV);	

			rep.skip(3); // componentID & property id
			return rep.getLenByteArray();

	}
	
	public PropertyInfo getPropertyInfo(short propertyId, byte instanceId, String nodeID) throws LoociManagementException,UnknownHostException{
		ShortPayloadBuilder builder = new ShortPayloadBuilder();
		builder.putByteAt(0, instanceId);
		builder.putShortAt(1, propertyId);
		
		Event ev = new Event(EventTypes.GET_PROPERTY_NAME, builder.getSizedPayload());
		ev.setDestinationAddress(nodeID);		
		ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.PROPERTY_NAME_EV);	
		rep.skip(3); // componentID & property id
		byte type = rep.getByte();
		String name = rep.getNTString();
		
		return new PropertyInfo(name, type);
	}
	
	//63
		@Override
		 public LocalBinding[] getLocalWires(short eventType, byte srcComponentID, byte dstComponentID, String nodeID) throws LoociManagementException,UnknownHostException{
			ShortPayloadBuilder builder = new ShortPayloadBuilder();
			builder.putShortAt(0, eventType);
			builder.putByteAt(2, srcComponentID);
			builder.putByteAt(3, dstComponentID);
			
			Event ev = new Event(EventTypes.GET_LCL_WIRE_EV, builder.getSizedPayload());
			ev.setDestinationAddress(nodeID);		
			ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.LCL_WIRE_EV);	

			rep.skip(4);
			int len = (int) rep.getByte();
			ArrayList<LocalBinding> bindings = new ArrayList<LocalBinding>();
			for(int i =0 ; i < len ; i ++){
				short type = rep.getShort();
				byte srcCompID = rep.getByte();
				byte dsstCompID = rep.getByte();
				bindings.add(new LocalBinding(type, srcCompID, dsstCompID));
			}	
			
			return bindings.toArray(new LocalBinding[0]);
			
		}
	
		//64
		@Override
		public RemoteToBinding[] getOutgoingRemoteWires(short eventSequenceNumber, byte srcComponentID, String srcNodeID, String dstNodeID
				) throws LoociManagementException,UnknownHostException{
			ShortPayloadBuilder builder = new ShortPayloadBuilder();
			builder.putShortAt(0, eventSequenceNumber);
			builder.putByteAt(2, srcComponentID);
			builder.putIpAt(3, dstNodeID);
			
			Event ev = new Event(EventTypes.GET_REM_TO_WIRE_EV, builder.getSizedPayload());
			ev.setDestinationAddress(srcNodeID);		
			ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.REM_TO_WIRE_EV);	
			rep.skip(19);
				int len = (int) rep.getByte();
				ArrayList<RemoteToBinding> bindings = new ArrayList<RemoteToBinding>();
				for(int i =0 ; i < len ; i ++){
					short type = rep.getShort();
					byte srcCompID = rep.getByte();
					String dstNodeAddr = rep.getIp();
					bindings.add(new RemoteToBinding(type, srcCompID, dstNodeAddr));
				}	
				
				
				return bindings.toArray(new RemoteToBinding[0]);

			
		}
		
		//65
		@Override
		public RemoteFromBinding[] getIncomingRemoteWires(short eventSequenceNumber, byte srcComponentID, String srcNodeID, byte dstComponentID, String nodeID) throws LoociManagementException,UnknownHostException{
			ShortPayloadBuilder builder = new ShortPayloadBuilder();
			builder.putShortAt(0, eventSequenceNumber);
			builder.putByteAt(2, srcComponentID);
			builder.putIpAt(3, srcNodeID);
			builder.putByteAt(19, dstComponentID);
			
			Event ev = new Event(EventTypes.GET_REM_FROM_WIRE_EV, builder.getSizedPayload());
			ev.setDestinationAddress(nodeID);		
			ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.REM_FROM_WIRE_EV);	
			rep.skip(20);
			int len = (int) rep.getByte();
			ArrayList<RemoteFromBinding> bindings = new ArrayList<RemoteFromBinding>();
			for(int i =0 ; i < len ; i ++){
				short type = rep.getShort();
				byte srcCompID = rep.getByte();
				String srcNodeAddr = rep.getIp();
				byte dstCompID = rep.getByte();				
				bindings.add(new RemoteFromBinding(type, srcCompID, srcNodeAddr,dstCompID));
			}				
			
			return bindings.toArray(new RemoteFromBinding[0]);

			
		}
	
	
	//66
	@Override
	public byte getPlatformType(String nodeID) throws LoociManagementException,UnknownHostException {
		Event ev = new Event(EventTypes.GET_PLATFORM_TYPE_EV, new byte[]{});
		ev.setDestinationAddress(nodeID);		
		ShortPayloadBuilder rep = sendEventWaitMgtReply(ev, EventTypes.PLATFORM_TYPE_EV);
		
			return rep.getByte();
	}
	



	@Override
	public void sendEvent(short eventType, byte[] content, String address, byte dstComp) throws UnknownHostException {
		Event event = new Event(eventType,content);
		event.setDestinationAddress(address);
		
		if(!event.getDestinationAddress().equals(LoociConstants.ADDR_ANY)){
			event.setDestComp(dstComp);
			HeaderManagement.setDestinationComp(event, dstComp);
		}		
		sender.sendEvent(event);
	}



	@Override
	public Event sendEventGetReply(short eventType, byte[] content,
			String address, byte dstComp, short replyType) throws UnknownHostException {
		Event event = new Event(eventType,content);
		event.setDestinationAddress(address);
		event.setDestComp(dstComp);
		return sender.sendEventAndWaitforReply(event, replyType, TIMEOUT);
	}
		


	@Override
	public MgtReply getLastMgtReply() {
		return lastReply;
	}


	@Override
	public String[] discover() throws LoociManagementException {
		Event event = new Event(EventTypes.GET_PLATFORM_TYPE_EV, new byte[]{});
		HeaderManagement.setDestinationComp(event, MGT_COMP_ID);
		try {
			event.setDestinationAddress(LoociConstants.ADDR_ANY);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		List<Event> events = sender.sendEventWaitAllReplies(event, EventTypes.PLATFORM_TYPE_EV, TIMEOUT);
		String[] ipAddresses = new String[events.size()];
		for(int i =0 ; i < ipAddresses.length; i ++){
			ipAddresses[i] = events.get(i).getSourceAddress();
		}		
		return ipAddresses;
	}

}

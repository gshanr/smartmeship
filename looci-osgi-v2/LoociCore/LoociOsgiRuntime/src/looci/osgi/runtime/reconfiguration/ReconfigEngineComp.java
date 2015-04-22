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
package looci.osgi.runtime.reconfiguration;

import looci.osgi.serv.bindings.LocalBinding;
import looci.osgi.serv.bindings.RemoteFromBinding;
import looci.osgi.serv.bindings.RemoteToBinding;
import looci.osgi.serv.constants.ErrorCodes;
import looci.osgi.serv.constants.EventTypes;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.constants.platform.LoociPlatformConstants;
import looci.osgi.serv.impl.LoociComponent;
import looci.osgi.serv.impl.ShortPayloadBuilder;
import looci.osgi.serv.impl.property.PropertyInfo;
import looci.osgi.serv.interfaces.IInspect;
import looci.osgi.serv.interfaces.IReconfigure;
import looci.osgi.serv.log.LLog;

public class ReconfigEngineComp extends LoociComponent {

	private IInspect intro;
	private IReconfigure reconfig;

	public ReconfigEngineComp(IInspect intro,
			IReconfigure reconfig) {
		setInspect(intro);
		setReconfigure(reconfig);
	}
	
	public void setInspect(IInspect intro){
		this.intro = intro;
	}
	
	public void setReconfigure(IReconfigure reconfig){
		this.reconfig = reconfig;
	}

	
	public void receive(short eventID, byte[] payload) {

		ShortPayloadBuilder pb = new ShortPayloadBuilder(payload);
		ShortPayloadBuilder buffer = new ShortPayloadBuilder();
		

		short type = eventID;
		short replyEventId = 0 ;
		byte errorcode = 1; //default success
		buffer.putByteArray(pb.getSizedPayload());
		
		if(type >= EventTypes.RECONFIGURATION_REQ_RANGE_START && type < EventTypes.RECONFIGURATION_REQ_RANGE_STOP){
			replyEventId = (short) (type - EventTypes.RECONFIGURATION_REQ_RANGE_START + EventTypes.RECONFIGURATION_REP_RANGE_START);
		} else if (type >= EventTypes.INTROSPECTION_REQ_RANGE_START && type < EventTypes.INTROSPECTION_REQ_RANGE_STOP){
			replyEventId = (short) (type - EventTypes.INTROSPECTION_REQ_RANGE_START + EventTypes.INTROSPECTION_REP_RANGE_START);
		} else{
			
		}
		try {
			if (type == EventTypes.REMOVE_CODEBASE_EV) {
				reconfig.removeCodebase(pb.getByteAt(0));
			} else if (type == EventTypes.INSTANTIATE_CMP_EV) {
				byte val = reconfig.instantiateComponent(pb.getByteAt(0));
				buffer.putByte(val);
			} else if (type == EventTypes.DESTROY_CMP_EV) {
				reconfig.destroyComponent(pb.getByteAt(0));
			} else if (type == EventTypes.START_COMPONENT_EV) {
				reconfig.activate(pb.getByteAt(0));
			} else if (type == EventTypes.STOP_COMPONENT_EV) {
				reconfig.deactivate(pb.getByteAt(0));
			} else if (type == EventTypes.RESET_WIRINGS_EV) {
				reconfig.resetWirings(pb.getByteAt(0));
			} else if (type == EventTypes.WIRE_LOCAL_EV) {
				reconfig.wireLocal(pb.getShortAt(0),pb.getByteAt(2), pb.getByteAt(3));				
			} else if (type == EventTypes.WIRE_REM_TO_EV) {
				reconfig.wireTo(pb.getShortAt(0), pb.getByteAt(2),pb.getIpAt(3));
			} else if (type == EventTypes.WIRE_REM_FROM_EV) {
				reconfig.wireFrom(pb.getShortAt(0),pb.getByteAt(18), pb.getIpAt(2), pb.getByteAt(19));
			} else if (type == EventTypes.UNWIRE_LCL_EV) {
				reconfig.unWireLocal(pb.getShortAt(0),pb.getByteAt(2), pb.getByteAt(3));
			} else if (type == EventTypes.UNWIRE_REM_TO_EV) {
				reconfig.unWireTo(pb.getShortAt(0),	pb.getByteAt(2), pb.getIpAt(3));
			} else if (type == EventTypes.UNWIRE_REM_FROM_EV) {
				reconfig.unWireFrom(pb.getShortAt(0),
						pb.getByteAt(18), pb.getIpAt(2), pb.getByteAt(19));
			} else if (type == EventTypes.SET_PROPERTY) {					
					byte[] val = pb.getByteArrayAt(4, pb.getByteAt(3));
					reconfig.setProperty(pb.getByteAt(0),pb.getShortAt(1), val);
			} 
			
			// ////////////////////
			// Introspection
			// ///////////////////
			else if (type == EventTypes.GET_ALL_CODEBASE_IDS_EV) { //51
				byte[] r = intro.getCodebaseIDs();
				buffer.putByte( (byte) r.length);
				buffer.putByteArray(r);
			} else if (type == EventTypes.GET_NAME_OF_CB_ID_EV) { //52
				String compType = intro.getCodebaseType(pb.getByteAt(0));
				buffer.putNTString(compType);
			}  else if (type == EventTypes.GET_CB_ID_BY_NAME_EV) { //53
				byte[] cb_ids = intro.getCodebaseIDsByType(pb.getNTStringAt(0));
				buffer.putByte((byte) cb_ids.length);
				buffer.putByteArray(cb_ids);
			}  else if (type == EventTypes.GET_COMP_IDS_OF_CB_ID) { //54
				byte[] comp_ids = intro.getComponentIDsByCbID(pb.getByteAt(0));
				buffer.putByte((byte) comp_ids.length);
				buffer.putByteArray(comp_ids);
			} else if (type == EventTypes.GET_CB_ID_OF_COMP_ID_EV) { //55
				byte comp_id = intro.getCodebaseOfComponent(pb.getByteAt(0));
				buffer.putByte(comp_id);
			}else if (type == EventTypes.GET_NAME_OF_COMP_ID_EV) { //56
				String compType = intro.getComponentType(pb.getByteAt(0));
				buffer.putNTString(compType);
			} else if (type == EventTypes.GET_ALL_COMPONENT_IDS_EV) {  //57
				byte[] r = intro.getComponentIDs();
				buffer.putByte((byte) r.length);
				buffer.putByteArray( r);
			}   else if (type == EventTypes.GET_STATE_EV) { //58
				byte state = intro.getState(pb.getByteAt(0));
				buffer.putByte(state);
			} else if (type == EventTypes.GET_PROPERTIES) { //59
				short[] r = intro.getAllProperties(pb.getByteAt(0));
				buffer.putByte( (byte) r.length);
				buffer.putShortArray(r);
			} else if (type == EventTypes.GET_PROPERTY) { //60
				byte[] r = intro.getPropertyValue(pb.getByteAt(0),
						pb.getShortAt(1));
				buffer.putByte((byte) r.length);
				buffer.putByteArray( r);
			}  else if (type == EventTypes.GET_PROPERTY_NAME) { //60
				PropertyInfo info = intro.getPropertyInfo(pb.getByteAt(0),
						pb.getShortAt(1));
				buffer.putByte(info.getPropertyType());
				buffer.putNTString(info.getPropertyName());
			}else if (type == EventTypes.GET_INTERFACES_EV) { //61
				short[] r = intro.getInterfaces(pb.getByteAt(0));
				buffer.putByte( (byte) r.length);
				buffer.putShortArray( r);
			} else if (type == EventTypes.GET_RECEPTACLES_EV) { //62
				short[] r = intro.getReceptacles(pb.getByteAt(0));
				buffer.putByte((byte) r.length);
				buffer.putShortArray(r);
			} else if (type == EventTypes.GET_LCL_WIRE_EV) { //63
				LocalBinding[] r = intro.getLocalWires(pb.getShortAt(0),pb.getByteAt(2),pb.getByteAt(3));
				buffer.putByte((byte)r.length);
					for (int i = 0; i < r.length; i++) {
						buffer.putShort(r[i].getEventID());
						buffer.putByte(r[i].getSourceComponentID());
						buffer.putByte(r[i].getDestinationComponentID());
					}
			} else if (type == EventTypes.GET_REM_TO_WIRE_EV) { //64
				RemoteToBinding[] r = intro.getOutgoingRemoteWires(
						pb.getShortAt(0),
						pb.getByteAt(2),
						pb.getIpAt(3));
				buffer.putByte((byte) r.length);
				for (int i = 0; i < r.length; i++) {
					buffer.putShort(r[i].getEventID());
					buffer.putByte(r[i].getSourceComponentID());
					buffer.putIp(r[i].getDestinationNode());
				}
			} else if (type == EventTypes.GET_REM_FROM_WIRE_EV) { //65
				RemoteFromBinding[] r = intro.getIncomingRemoteWires(
						pb.getShortAt(0),
						pb.getByteAt(2),
						pb.getIpAt(3),
						pb.getByteAt(19));
				buffer.putByte((byte) r.length);
				for (int i = 0; i < r.length; i++) {
					buffer.putShort(r[i].getEventID());
					buffer.putByte(r[i].getSourceComponentID());						
					buffer.putIp(r[i].getSourceNode());
					buffer.putByte(r[i].getDestinationComponentID());
				}
		
			} else if (type == EventTypes.GET_PLATFORM_TYPE_EV){
				buffer.putByte((byte) LoociPlatformConstants.LOOCI_RUNTIME);				
			} else{
				throw new LoociManagementException(ErrorCodes.ERROR);
			}
		} catch (SecurityException e){
			LLog.out(this,"caught security Exception");
			errorcode = ErrorCodes.ERROR_CODE_SECURITY;
		} catch (LoociManagementException e){
			LLog.out(this,"caught looci exception " + e.getErrorCode());
			errorcode = e.getErrorCode();
		}catch (Exception exc) {
			LLog.out(this,"caught unknown exception" + exc.getLocalizedMessage());
			errorcode = ErrorCodes.ERROR_CODE_RUNTIME_EXCEPTION;
			exc.printStackTrace();
		} catch(Error err){			
			errorcode = ErrorCodes.ERROR_CODE_RUNTIME_ERROR;
			err.printStackTrace();
		}

		int size = buffer.getSize();

		byte[] result = new byte[size + 1];
		System.arraycopy(buffer.getPayload(), 0, result, 1, size);

		result[0] = errorcode;

		LLog.out(this,"[RC] sending reply event : " + replyEventId);
		publish(replyEventId, result);
	}
}

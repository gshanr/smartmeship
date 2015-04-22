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

import looci.osgi.runtime.CodebaseManager;
import looci.osgi.runtime.ComponentManager;
import looci.osgi.runtime.EventManager;
import looci.osgi.serv.bindings.LocalBinding;
import looci.osgi.serv.bindings.RemoteFromBinding;
import looci.osgi.serv.bindings.RemoteToBinding;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.impl.property.PropertyInfo;
import looci.osgi.serv.interfaces.IInspect;

public class IntrospectionEngine implements IInspect {

	private EventManager eManager;
	private CodebaseManager cbManager;
	private ComponentManager cmpManager;

	public IntrospectionEngine(EventManager eManager,
			CodebaseManager cbManager, ComponentManager cmpManager) {
		this.eManager = eManager;
		this.cbManager = cbManager;
		this.cmpManager = cmpManager;
	}


	public byte[] getCodebaseIDs() {
		return cbManager.getCodebaseIDs();
	};

	public byte[] getCodebaseIDsByType(String componentType) throws LoociManagementException{
		return cbManager.getCodebaseIdsOfType(componentType);
	};

	public String getComponentType(byte compId)throws LoociManagementException {
		return cmpManager.getComponentType(compId);
	};

	public short[] getInterfaces(byte compID) throws LoociManagementException{
		return cmpManager.getInterfacesOfComponent(compID);
	};

	public short[] getReceptacles(byte compID) throws LoociManagementException{
		return cmpManager.getReceptaclesOfComponent(compID);
	}

	@Override
	public byte getState(byte compID) throws LoociManagementException{
		return cmpManager.getComponentState(compID);
	}

	@Override
	public byte[] getComponentIDs(){
		return cmpManager.getComponentIDs();
	}

	@Override
	public byte getCodebaseOfComponent(byte inst_id) throws LoociManagementException {
		return cmpManager.getComponentIdOfInstance(inst_id);
	}

	@Override
	public LocalBinding[] getLocalWires(short event_id, byte srcCompID, byte dstCompID) {
		return eManager.getLocalWires(event_id, srcCompID, dstCompID);
	}
	
	@Override
	public RemoteToBinding[] getOutgoingRemoteWires(short eventID, byte srcCompID, String dstNodeID) {
		return eManager.getOutgoingRemoteWires(eventID, srcCompID,dstNodeID);
	}

	@Override
	public RemoteFromBinding[] getIncomingRemoteWires(short eventID, byte srcCompID, String srcNodeID, byte dstCompID) {
		return eManager.getIncomingRemoteWires(eventID, srcCompID,srcNodeID,dstCompID);
	}

	@Override
	public short[] getAllProperties(byte cmp_id) throws LoociManagementException{
		return cmpManager.getComponentProperties(cmp_id);
	}

	@Override
	public byte[] getPropertyValue(byte cmp_id, short propValue) throws LoociManagementException{
		return cmpManager.getPropertyValue(cmp_id, propValue);
	}

	@Override
	public PropertyInfo getPropertyInfo(byte compId, short propValue)  throws LoociManagementException
	{
		return cmpManager.getPropertyInfo(compId, propValue);
	}

	@Override
	public String getCodebaseType(byte codebaseID) throws LoociManagementException {
		return cbManager.getCodebaseType(codebaseID);
	}


	@Override
	public byte[] getComponentIDsByCbID(byte codebaseID) throws LoociManagementException{
		return cbManager.getComponentIDsbyCbID(codebaseID);
	}


}

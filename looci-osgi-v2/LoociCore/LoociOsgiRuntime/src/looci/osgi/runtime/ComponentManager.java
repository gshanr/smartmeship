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
package looci.osgi.runtime;

import java.util.ArrayList;

import looci.osgi.serv.components.ComponentStateTypes;
import looci.osgi.serv.constants.ErrorCodes;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.impl.property.PropertyInfo;
import looci.osgi.serv.interfaces.ILoociCodebase;
import looci.osgi.serv.interfaces.ILoociComponent;


public class ComponentManager{

	private ArrayList<ILoociComponent> components = new ArrayList<ILoociComponent>();
	private EventManager eManager;

	public ComponentManager(EventManager eManager) {
		this.eManager = eManager;
	}

	public ILoociComponent getComponent(byte componentID) {
		for (int i = 0; i < components.size(); i++) {
			if (components.get(i).getComponentID() == componentID) {
				return components.get(i);
			}
		}
		return null;
	}
	
	private ILoociComponent getComponentNotNull(byte componentID) throws LoociManagementException{
		for (int i = 0; i < components.size(); i++) {
			if (components.get(i).getComponentID() == componentID) {
				return components.get(i);
			}
		}
		throw new LoociManagementException(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND);
	}

	private byte findComponentID() {
		for (byte i = 10; i < 255; i++) {
			if (getComponent(i) == null) {
				return i;
			}
		}
		return 0;
	}

	public byte instantiateComponent(ILoociCodebase codebase) throws LoociManagementException {
		byte nextId;
		if (codebase.isManagementCodebase()) {
			if(codebase.getComponents().size()==0){
				nextId = codebase.getCodebaseID();				
			} else{
				throw new LoociManagementException(ErrorCodes.ERROR_CODE_ILLEGAL_STATE);
			}
		} else {
			nextId = findComponentID();
		}

		ILoociComponent component = codebase.instantiateComponent(nextId,eManager.getComponentReceiver());
		components.add(component);
		component.create();

		eManager.addLocalSubscriber(component.getComponentID(), component.getReceiver());
		return nextId;
	}

	public byte getComponentState(byte compID)throws LoociManagementException {
		return getComponentNotNull(compID).getState();		
	}

	public byte[] getComponentIDs(){
		byte[] retVal = new byte[components.size()];
		for (int i = 0; i < components.size(); i++) {
			retVal[i] = components.get(i).getComponentID();
		}
		return retVal;
	}

	public byte getComponentIdOfInstance(byte compID) throws LoociManagementException{
		return getComponentNotNull(compID).getCodebase().getCodebaseID();
	}

	public short[] getComponentProperties(byte compID) throws LoociManagementException{
		return getComponentNotNull(compID).getProperties();
		
	}

	public byte[] getPropertyValue(byte compID, short propValue)  throws LoociManagementException{
		return getComponentNotNull(compID).getProperty(propValue);
	}
	
	public PropertyInfo getPropertyInfo(byte compID, short propValue)  throws LoociManagementException{
		return getComponentNotNull(compID).getPropertyInfo(propValue);
	}

	public void destroyComponent(byte compID) throws LoociManagementException{
		ILoociComponent comp = getComponentNotNull(compID);
		if (comp.getState() == ComponentStateTypes.ACTIVE) {
			deactivateComponent(compID);
		}
		eManager.removeLocalSubscriber(comp.getComponentID());
		try{
			comp.destroy();
		} catch(Exception e){
			e.printStackTrace();
			throw new LoociManagementException(ErrorCodes.ERROR);
		}
		components.remove(comp);
	}

	public void activateComponent(byte compID) throws LoociManagementException{
		ILoociComponent comp = getComponent(compID);
		if (comp != null) {
			if(!comp.isActive()){
				try{
					comp.activate();	
				} catch(Exception e){
					e.printStackTrace();
					throw new LoociManagementException(ErrorCodes.ERROR);
				}
			} else{
				throw new LoociManagementException(ErrorCodes.ERROR_CODE_ILLEGAL_STATE);
			}
		} else{
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND);
		}
	}
	

	public void deactivateComponent(byte compID) throws LoociManagementException{
		ILoociComponent comp = getComponent(compID);
		if (comp != null) {
			if(comp.isActive()){
				try{
					comp.deactivate();	
				} catch(Exception e){
					e.printStackTrace();
					throw new LoociManagementException(ErrorCodes.ERROR);
				}			
			}else{
				throw new LoociManagementException(ErrorCodes.ERROR_CODE_ILLEGAL_STATE);
			}
		} else{
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND);
		}
	}

	public void setProperty(byte compID, short propID, byte[] val)throws LoociManagementException {
		ILoociComponent comp = getComponent(compID);
		if (comp != null) {
			try{
				comp.setProperty(propID, val);
			} catch(LoociManagementException e){
				throw e;
			} catch(Exception e){
				e.printStackTrace();
				throw new LoociManagementException(ErrorCodes.ERROR);
			}	
		} else{
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND);
		}
	}

	public String getComponentType(byte compId) throws LoociManagementException{
		ILoociComponent comp = getComponent(compId);
		if (comp != null) {
			try{
				return comp.getCodebase().getCodebaseType();
			}catch(Exception e){
				e.printStackTrace();
				throw new LoociManagementException(ErrorCodes.ERROR);
			}	
		}else{
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND);
		}
	}
	

	public short[] getInterfacesOfComponent(byte compId)throws LoociManagementException {
		ILoociComponent comp = getComponent(compId);
		if (comp != null) {
			try{
				return comp.getCodebase().getInterfaces();
			} catch(Exception e){
				e.printStackTrace();
				throw new LoociManagementException(ErrorCodes.ERROR);
			}	
		}else{
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND);
		}
	}

	public short[] getReceptaclesOfComponent(byte compId)throws LoociManagementException {
		ILoociComponent comp = getComponent(compId);
		if (comp != null) {
			try{
				return comp.getCodebase().getReceptacles();
			} catch(Exception e){
				e.printStackTrace();
				throw new LoociManagementException(ErrorCodes.ERROR);
			}
		}else{
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND);
		}
	}



}

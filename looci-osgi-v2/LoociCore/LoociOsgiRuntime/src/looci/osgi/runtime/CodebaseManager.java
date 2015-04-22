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

import looci.osgi.serv.constants.ErrorCodes;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.interfaces.ICodebaseManager;
import looci.osgi.serv.interfaces.ILoociCodebase;
import looci.osgi.serv.interfaces.ILoociComponent;
import looci.osgi.serv.log.LLog;


public class CodebaseManager implements ICodebaseManager {

	
	private ArrayList<ILoociCodebase> codebases;

	private static final byte startNormalCodebases = 10;
	private ComponentManager cManager;

	public CodebaseManager(ComponentManager cManager) {
		codebases = new ArrayList<ILoociCodebase>();
		this.cManager = cManager;
	}

	private byte findCodebaseId(byte minId, byte maxId) {

		for (byte i = minId; i <= maxId; i++) {
			if (getCodebase(i) == null) {
				return i;
			}
		}
		return 0;
	}

	public void registerCodebase(ILoociCodebase codebase) {
		LLog.out(this,"[LooCI cManager] registering component:"
				+ codebase.getCodebaseType());
		codebases.add(codebase);
		byte id;
		if (codebase.isManagementCodebase()) {
			id = codebase.getCodebaseID();
		} else {
			id = findCodebaseId(startNormalCodebases, (byte) 127);
		}
		LLog.out(this,"[LooCI cManager] component id:" + id);
		codebase.setCodebaseID(id);
		if (codebase.autoStartCodebase()) {
			LLog.out(this,"[LooCI cManager] instantiating installed component:");
			try {
				byte instance = cManager.instantiateComponent(codebase);
				cManager.activateComponent(instance);
			} catch (LoociManagementException e) {
				e.printStackTrace();
			}
		}

	}

	public void unregisterCodebase(ILoociCodebase codebase) {
		while (codebase.getComponents().size() != 0) {
			ILoociComponent instance = (ILoociComponent) codebase.getComponents().get(0);
			try {
				cManager.destroyComponent(instance.getComponentID());
			} catch (LoociManagementException e) {
				e.printStackTrace();
			}
		}
		codebases.remove(codebase);
	}

	public byte[] getCodebaseIDs() {
		byte[] retVal = new byte[codebases.size()];
		for (int i = 0; i < codebases.size(); i++) {
			retVal[i] = getLclCodebase(i).getCodebaseID();
		}
		return retVal;
	}

	public ILoociCodebase getCodebase(byte codebaseID) {
		for (int j = 0; j < codebases.size(); j++) {
			ILoociCodebase component = (ILoociCodebase) codebases.get(j);
			if (component.getCodebaseID() == codebaseID) {
				return component;
			}
		}
		return null;
	}
	
	private ILoociCodebase getLclCodebase(int id){
		return (ILoociCodebase) codebases.get(id);
	}

	public byte instantiateComponent(byte componentId) throws LoociManagementException{
		ILoociCodebase component = getCodebase(componentId);
		if (component != null) {
			return cManager.instantiateComponent(component);
		} else{
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_CODEBASE_NOT_FOUND);
		}
	}

	public byte[] getCodebaseIdsOfType(String componentType) throws LoociManagementException {
		ArrayList<ILoociCodebase> retComps = new ArrayList<ILoociCodebase>();
		for (int i = 0; i < codebases.size(); i++) {
			if (getLclCodebase(i).getCodebaseType().equals(componentType)) {
				retComps.add(codebases.get(i));
			}
		}
		byte[] retVal = new byte[retComps.size()];
		for (int i = 0; i < retComps.size(); i++) {
			retVal[i] = ((ILoociCodebase)retComps.get(i)).getCodebaseID();
		}
		if(retVal.length == 0){
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_CODEBASE_NOT_FOUND);
		}
		return retVal;
	}

	public String getCodebaseType(byte codebaseID)  throws LoociManagementException{
		ILoociCodebase codebase = getCodebase(codebaseID);
		if (codebase != null) {
			return codebase.getCodebaseType();
		} else{
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_CODEBASE_NOT_FOUND);
		}
	}

	public void removeCodebase(byte codebaseID) throws LoociManagementException{
		ILoociCodebase codebase = getCodebase(codebaseID);
		if (codebase == null) {
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_CODEBASE_NOT_FOUND);
		}
		ArrayList<ILoociComponent> components = codebase.getComponents();

		for (int i = 0; i < components.size(); i++) {
			cManager.destroyComponent(((ILoociComponent)components.get(i)).getComponentID());
		}
		codebase.remove();
	}

	public byte[] getComponentIDsbyCbID(byte codebaseID) throws LoociManagementException {
		ILoociCodebase codebase = getCodebase(codebaseID);
		if (codebase == null) {
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_CODEBASE_NOT_FOUND);
		}
		ArrayList<ILoociComponent> components = codebase.getComponents();
		byte[] retval = new byte[components.size()];
		for (int i = 0; i < components.size(); i++) {
			retval[i] = components.get(i).getComponentID();
		}
		return retval;
	}

}

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
/*
 * Copyright (c) 2010, Katholieke Universiteit Leuven
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Katholieke Universiteit Leuven nor the names of
 *       its contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package looci.osgi.runtime.reconfiguration;

import looci.osgi.runtime.CodebaseManager;
import looci.osgi.runtime.ComponentManager;
import looci.osgi.runtime.EventManager;
import looci.osgi.serv.bindings.LocalBinding;
import looci.osgi.serv.bindings.RemoteFromBinding;
import looci.osgi.serv.bindings.RemoteToBinding;
import looci.osgi.serv.constants.ErrorCodes;
import looci.osgi.serv.constants.EventTypes;
import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.interfaces.ILoociComponent;
import looci.osgi.serv.interfaces.IReconfigure;
import looci.osgi.serv.log.LLog;

public class ReconfigurationEngine implements IReconfigure {

	private EventManager eManager;
	private CodebaseManager cManager;
	private ComponentManager iManager;

	public ReconfigurationEngine(EventManager eventManager,
			CodebaseManager cManager, ComponentManager iManager) {
		this.eManager = eventManager;
		this.cManager = cManager;
		this.iManager = iManager;
		init();
	}
	
	private void checkComponent(byte cmpId) throws LoociManagementException{
		if(iManager.getComponent(cmpId)==null){
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND);
		}
	}

	private void checkInterface(byte inst_id, short event_id) throws LoociManagementException{
		if(inst_id == LoociConstants.COMPONENT_WILDCARD){
			return;
		}
		ILoociComponent inst = iManager.getComponent(inst_id);
		if (inst == null) {
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND);
		}
		if(!contains(event_id, inst.getInterfaces())){
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_PROVIDED_INTERFACE_NOT_FOUND);
		}
	}

	private void checkReceptacle(byte inst_id, short event_id) throws LoociManagementException{
		ILoociComponent inst = iManager.getComponent(inst_id);
		if (inst == null) {
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND);
		}
		if(!contains(event_id, inst.getReceptacles())){
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_REQURIED_INTERFACE_NOT_FOUND);
		}
	}

	private void init() {

	}

	public void wireLocal(short event_id, byte src_component_id,
			byte dst_component_id) throws LoociManagementException{
	
		if(src_component_id != LoociConstants.COMPONENT_WILDCARD){ 
			checkComponent(src_component_id);
		}
		checkComponent(dst_component_id);
		checkInterface(src_component_id, event_id);
		checkReceptacle(dst_component_id, event_id);
		LocalBinding b = new LocalBinding(event_id, src_component_id, dst_component_id);
		eManager.addLocalComponentBinding(b);
		LLog.out(this,"[RECONF ENGINE] wired interface " + event_id
				+ " of local component: " + src_component_id
				+ " to local component: " + dst_component_id);
	}

	@Override
	public void unWireLocal(
			short event_id, 
			byte src_component_id,
			byte dst_component_id)  throws LoociManagementException
	{

		LocalBinding b = new LocalBinding(event_id, src_component_id, dst_component_id);
		eManager.removeLocalComponentBinding(b);
	}

	public void removeCodebase(byte component_id) throws LoociManagementException {
		cManager.removeCodebase(component_id);
	}

	public void activate(byte inst_id) throws LoociManagementException {
		iManager.activateComponent(inst_id);
	}

	private boolean contains(short b, short[] array) {
		for (int i = 0; i < array.length; i++) {
			if (b == array[i] || array[i] == EventTypes.ANY_EVENT) {
				return true;
			}
		}
		return false;
	}

	public void deactivate(byte cmp_id) throws LoociManagementException{
		iManager.deactivateComponent(cmp_id);
	}

	public void resetWirings(byte cmp_id) throws LoociManagementException{
		if(iManager.getComponent(cmp_id)==null){
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND);
		}
		eManager.resetWirings(cmp_id);
	}


	@Override
	public void wireTo(short event_id, byte src_comp_id, String dst_addr) throws LoociManagementException{
		if(src_comp_id != LoociConstants.COMPONENT_WILDCARD){ 
			checkComponent(src_comp_id);
		}
		checkInterface(src_comp_id, event_id);
		
		RemoteToBinding b = new RemoteToBinding(event_id, src_comp_id, dst_addr);
		eManager.addRemoteToComponentBinding(b);
		LLog.out(this,"[RECONF] wired interface " + event_id
				+ " of component: " + src_comp_id + " to remote device on: "
				+ dst_addr);
	}

	@Override
	public void unWireTo(short event_id, byte src_comp_id, String dst_addr) throws LoociManagementException {
		
			RemoteToBinding b = new RemoteToBinding(event_id, src_comp_id, dst_addr);
			eManager.removeRemoteToComponentBinding(b);
	}

	@Override
	public void wireFrom(short event_id, byte src_comp_id, String src_addr,
			byte dst_comp_id)throws LoociManagementException {
		checkComponent(dst_comp_id);
		checkReceptacle(dst_comp_id, event_id);
		
		RemoteFromBinding b = new RemoteFromBinding(event_id, src_comp_id, src_addr, dst_comp_id);
		eManager.addRemoteFromComponentBinding(b);
		LLog.out(this,"[RECONF] wired receptacle " + event_id
				+ " of component: " + src_comp_id + " coming from devices: "
				+ src_addr);
	}

	@Override
	public void unWireFrom(short event_id, byte src_comp_id,
			String src_addr, byte dest_comp_id) throws LoociManagementException{
			RemoteFromBinding b = new RemoteFromBinding(event_id, src_comp_id, src_addr, dest_comp_id);
			eManager.removeRemoteFromComponentBinding(b);

	}

	@Override
	public void installComponent() {
		// currently unimplemented: Work for later
	}

	@Override
	public byte instantiateComponent(byte comp_id) throws LoociManagementException{
		try{
			return cManager.instantiateComponent(comp_id);			
		} catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public void destroyComponent(byte inst_id) throws LoociManagementException{
		iManager.destroyComponent(inst_id);
	}

	@Override
	public void setProperty(byte inst_id, short prop_id, byte[] val) throws LoociManagementException {
		iManager.setProperty(inst_id, prop_id, val);
	}

}

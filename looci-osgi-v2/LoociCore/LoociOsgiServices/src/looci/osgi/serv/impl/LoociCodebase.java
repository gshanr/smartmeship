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

package looci.osgi.serv.impl;

import java.util.ArrayList;

import looci.osgi.serv.components.ComponentTypes;
import looci.osgi.serv.components.IReceive;
import looci.osgi.serv.constants.ErrorCodes;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.interfaces.ICodebaseManager;
import looci.osgi.serv.interfaces.ILoociCodebase;
import looci.osgi.serv.interfaces.ILoociComponent;
import looci.osgi.serv.log.LLog;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;


/**
 * Abstract class providing the core functionality to realize LooCIcomponents.
 * Every LooCI component should inherit from this class.
 * @author nelson & Jef
 * 
 */

public abstract class LoociCodebase implements BundleActivator, ILoociCodebase {


	private short[] interfaces;
	private short[] receptacles;
	
	
	private ArrayList<ILoociComponent> components = new ArrayList<ILoociComponent>();
	
	
	private String componentType;
	private BundleContext context;
	private byte codebaseID;
	
	private ICodebaseManager codebaseManager;
	private ServiceRegistration componentReg;


	public LoociCodebase(String componentType, short[] interfaces,
			short[] receptacles) {
		this.componentType = componentType;
		this.interfaces = interfaces;
		this.receptacles = receptacles;
		codebaseID = ComponentTypes.CODEBASE_WILDCARD;
	}

	public void start(BundleContext context) throws Exception {
		LLog.out(this, "starting codebase: "+componentType);
		this.context = context;

		ServiceReference[] refs = context.getServiceReferences(ICodebaseManager.class.getName(), null);
		codebaseManager = (ICodebaseManager) context.getService(refs[0]);
		codebaseManager.registerCodebase(this);
		
		componentReg = context.registerService(ILoociCodebase.class.getName(), this, null);
		this.codebaseInit();
	}
	
	public void remove() throws LoociManagementException{
		this.codebaseRemove();
		componentReg.unregister();
		Bundle b = context.getBundle();
		try {
			b.stop();
			b.uninstall();
		} catch (BundleException e) {
			throw new LoociManagementException(ErrorCodes.ERROR_CODE_ILLEGAL_STATE);
		}
	}
	
	/**
	 * Protected method which is called when this codebase is initiated.
	 * Can be overwritten by sub-codebases to perform specific functionality
	 */
	protected void codebaseInit(){}
	
	/**
	 * Protected method which is called when this codebase is removed.
	 * Can be overwritten by sub-codebases to perform specific functionality
	 * 
	 */
	protected void codebaseRemove(){}

	public void stop(BundleContext context) throws Exception {
		LLog.out(this, "ending codebase: "+componentType +":"+codebaseID);
		codebaseManager.unregisterCodebase(this);
	}


	@Override
	public String getCodebaseType() {
		return componentType;
	}

	@Override
	public byte getCodebaseID() {
		return codebaseID;
	} 

	@Override
	public short[] getInterfaces() {
		return interfaces;
	}

	@Override
	public short[] getReceptacles() {
		return receptacles;
	}

	@Override
	public void setCodebaseID(byte cbID) {
		codebaseID = cbID;
	}

	
	public BundleContext getBundleContext(){
		return context;
	}


	public ArrayList<ILoociComponent> getComponents() {
		return components;
	}
	
	
	public void addInstance(ILoociComponent instance){
		components.add(instance);
	}
	
	public void removeInstance(ILoociComponent instance){
		components.remove(instance);
	}


	public boolean isManagementCodebase() {
		return false;
	}

	public boolean autoStartCodebase() {
		return false;
	}

	public boolean canDeactivateCodebase() {
		return true;
	}
	
	public final ILoociComponent instantiateComponent(byte componentID, IReceive receiver) {
		ILoociComponent comp = createLoociComponent();
		comp.init(componentID, receiver, this);
		return comp;
	}
	
	protected abstract ILoociComponent createLoociComponent();
	
}

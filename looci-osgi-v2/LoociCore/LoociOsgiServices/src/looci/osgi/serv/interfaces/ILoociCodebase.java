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
package looci.osgi.serv.interfaces;

import java.util.ArrayList;

import looci.osgi.serv.components.IReceive;
import looci.osgi.serv.constants.LoociManagementException;

import org.osgi.framework.BundleContext;

/**
 * Class representing a LooCI codebase
 */
public interface ILoociCodebase {

	/**
	 * Returns whether this is a management codebase
	 */
	public boolean isManagementCodebase();
	
	/**
	 * Return whether this codebase has to be started automatically
	 */
	public boolean autoStartCodebase();
	
	/**
	 * Return whether this codebase can be deactivated
	 */
	public boolean canDeactivateCodebase();

	/**
	 * remove this codebase. 
	 * It unregisters this codebase from the codebasemanager, and stops the containing bundle
	 */
	public void remove() throws LoociManagementException;
	
	/**
	 * Returns the type of this component.
	 */
	public String getCodebaseType();

	/**
	 * Returns the ID of this component.
	 * 
	 * @return
	 */
	public byte getCodebaseID();
	
	/**
	 * Initialize this codebase with a given id.
	 */
	public void setCodebaseID(byte cbID);
	
	
	/**
	 * Creates a new inntance of the component
	 */
	public ILoociComponent instantiateComponent(byte compID, IReceive receiver);
	
	public ArrayList<ILoociComponent> getComponents();
	
	public short[] getInterfaces();

	public short[] getReceptacles();
	
	public BundleContext getBundleContext();
}
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

import looci.osgi.serv.constants.EventTypes;
import looci.osgi.serv.impl.LoociCodebase;
import looci.osgi.serv.interfaces.IInspect;
import looci.osgi.serv.interfaces.ILoociComponent;
import looci.osgi.serv.interfaces.IReconfigure;

public class ReconfigEngineCB extends LoociCodebase {

	private IReconfigure rEngine;
	private IInspect iEngine;
	private ReconfigEngineComp instance;

	public ReconfigEngineCB(IReconfigure rEngine,
			IInspect iEngine) {
		super("reconfigurationComponent",
				new short[] { EventTypes.ANY_EVENT},
				new short[] { EventTypes.ANY_EVENT});
		this.rEngine = rEngine;
		this.iEngine = iEngine;
		setCodebaseID((byte) 1);
	}

	@Override
	public boolean isManagementCodebase() {
		return true;
	}

	@Override
	public boolean autoStartCodebase() {
		return true;
	}

	@Override
	public boolean canDeactivateCodebase() {
		return false;
	}

	@Override
	protected ILoociComponent createLoociComponent() {
		instance = new ReconfigEngineComp(iEngine, rEngine);
		return instance;
	}
	
	public ReconfigEngineComp getManagementInstance(){
		return instance;
	}

}

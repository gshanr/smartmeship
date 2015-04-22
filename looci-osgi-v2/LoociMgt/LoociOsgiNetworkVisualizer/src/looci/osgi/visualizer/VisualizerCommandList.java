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
package looci.osgi.visualizer;

import java.util.ArrayList;

import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.servExt.mgt.IServiceCommandList;
import looci.osgi.servExt.mgt.ServiceClient;
import looci.osgi.servExt.mgt.ServiceCommand;
import looci.osgi.visualizer.lib.Principal;


public class VisualizerCommandList implements IServiceCommandList{

	private Principal principal;
	private ArrayList<ServiceCommand> commandList;
	
	
	public VisualizerCommandList(Principal principalIn){
		this.principal = principalIn;
		commandList = new ArrayList<ServiceCommand>();
		commandList.add(new ServiceCommand() {
			
			@Override
			public String getExtendedHelp() {
				return "draws the list of nodes provided";
			}
			
			@Override
			public String getCommand() {
				return "draw";
			}
			
			@Override
			public String getArgs() {
				return "listOfNodes*";
			}
			
			@Override
			public String doCommand(String[] command) throws LoociManagementException {
				String[] nodes = new String[command.length -1];
				for(int i = 1 ; i < command.length; i ++){
					nodes[i-1] = command[i];
				}
				principal.visualize(nodes);
				return "success";
			}
		});
		
	}
	
	@Override
	public String commandListName() {
		return "Visualizer";
	}

	@Override
	public ArrayList<ServiceCommand> getServiceCommands() {
		return commandList;
	}

	@Override
	public void registerServiceClient(ServiceClient client) {
		//no need to do anything
	}


	
	

}

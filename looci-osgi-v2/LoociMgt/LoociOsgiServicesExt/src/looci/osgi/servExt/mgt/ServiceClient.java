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
package looci.osgi.servExt.mgt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import looci.osgi.serv.constants.ErrorCodes;
import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.log.LLog;
import looci.osgi.serv.util.XString;



public class ServiceClient implements IServiceClient{

	private Hashtable<String, String> savedVars;
	
	private ArrayList<ServiceCommand> terminalCommands;
	private ArrayList<IServiceCommandList> listOfCommands;
	
    private HashMap<String, ServiceCommand> commandsMap;

	public ServiceClient(IServiceCommandList startingList) {
		this.savedVars = new Hashtable<String, String>();
				
	    listOfCommands = new ArrayList<IServiceCommandList>();
		terminalCommands = new ArrayList<ServiceCommand>();
        loadTerminalCommands();
        
		
        commandsMap = new HashMap<String, ServiceCommand>();


		for(int i = 0; i < terminalCommands.size() ; i ++){
			ServiceCommand command = terminalCommands.get(i);
			commandsMap.put(command.getCommand().toLowerCase(), command);
		}
		
		addCommandList(startingList);		
		
	}
	
	public void addCommandList(IServiceCommandList list){
		list.registerServiceClient(this);
		listOfCommands.add(list);
		List<ServiceCommand> commands = list.getServiceCommands();		
		for(int i = 0; i < commands.size() ; i ++){
			ServiceCommand command = commands.get(i);
			commandsMap.put(command.getCommand().toLowerCase(), command);
		}
	}
	
	public void removeCommandList(IServiceCommandList list){
		if(listOfCommands.remove(list)){
			List<ServiceCommand> commands = list.getServiceCommands();		
			for(int i = 0; i < commands.size() ; i ++){
				ServiceCommand command = commands.get(i);
				commandsMap.remove(command.getCommand().toLowerCase());
			}
		}
	}
        
	private void loadTerminalCommands(){
		
		terminalCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Delay execution for given amount of milliseconds.";}			
			@Override
			public String getCommand() {return "delay";}			
			@Override
			public String getArgs() {return "time";}			
			@Override
			public String doCommand( String[] command)  throws LoociManagementException{
				int time = Integer.parseInt(command[1]);
				synchronized (this) {
					try {
						this.wait(time);
					} catch (InterruptedException e) {
					}
				}
				return "delayed for " + time + "milliseconds";
			}
		});
		
		terminalCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Execute script with given name.";}			
			@Override
			public String getCommand() {return "doScript";}			
			@Override
			public String getArgs() {return "script name";}			
			@Override
			public String doCommand( String[] command)  throws LoociManagementException{
				return fileInput(command[1],command);
			}
		});
		
		terminalCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {
				String response = "Prints out help in the terminal.\r\n";
				response += "Type help commandName to get extended help for that command.\r\n";
				response += "To get list of arguments of a given command, just enter the command without any arguments";
				return response; 
			}			
			@Override
			public String getCommand() {return "help";}			
			@Override
			public String getArgs() {return "";}			
			@Override
			public String doCommand( String[] command)  throws LoociManagementException{
				String response = "";
				if(command.length == 1){
                                        response += "This is the full list of current management commands and their respective arguments.\n";
                                        response += "Quick help can also be retrieved by simply entering a command (without arguments).\n\n";
					ServiceCommand commandObj;
					for(int i =0 ; i < listOfCommands.size(); i ++){
						List<ServiceCommand> cmdList = listOfCommands.get(i).getServiceCommands();
						Collections.sort(cmdList,new Comparator<ServiceCommand>() {

							@Override
							public int compare(ServiceCommand arg0,
									ServiceCommand arg1) {
								return arg0.getCommand().compareTo(arg1.getCommand());
							}
						});
						
						response += "list of commands for :" + listOfCommands.get(i).commandListName() + "\r\n";
						for(int j = 0 ; j < cmdList.size(); j++){
							commandObj = cmdList.get(j);
							response += String.format("%-30s" + ": " + commandObj.getArgs() + "\r\n", commandObj.getCommand());
						}
						
						
					}
                    response += "\n\nAdditional commands supported in the terminal:\n\n";
                    for(int i =0 ; i < terminalCommands.size(); i ++){
						commandObj = terminalCommands.get(i);
                        response += String.format("%-30s" + ": " + commandObj.getArgs() + "\r\n", commandObj.getCommand());
					}
				} else{
					ServiceCommand commandObj = commandsMap.get(command[1].toLowerCase());
					if(commandObj != null){
						response = "help for: " + commandObj.getCommand() + "\r\n";
						response += "arguments: " + commandObj.getArgs() + "\r\n";
						response += "extended help : "+ commandObj.getExtendedHelp();
					} else{
						response = "Command not found. Type help to get all commands.";
					}
				}
				return response;
			}
		});
		
		
		terminalCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "";}			
			@Override
			public String getCommand() {return "getSavedStrings";}			
			@Override
			public String getArgs() {return "";}			
			@Override
			public String doCommand( String[] command)  throws LoociManagementException{
				String response = "List of currently saved strings : "+ "\r\n";
				Enumeration<String> vars = savedVars.keys();
				while(vars.hasMoreElements()){
					String key = vars.nextElement();
					response += key + ": " + savedVars.get(key) + "\r\n";
				}
				return response;
			}
		});
		
		terminalCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "";}			
			@Override
			public String getCommand() {return "save";}			
			@Override
			public String getArgs() {return "key - value";}			
			@Override
			public String doCommand( String[] command)  throws LoociManagementException{
				savedVars.put(command[1], command[2]);
				return "saving : "+ command[1] + " as "+command[2] + "\r\n";
			}
		});
		
		terminalCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "";}			
			@Override
			public String getCommand() {return "delete";}			
			@Override
			public String getArgs() {return "key";}			
			@Override
			public String doCommand( String[] command)  throws LoociManagementException{
				String removed = savedVars.remove(command[1]);
				return "removed : "+ command[1] + ", contained "+ removed + "\r\n";
			}
		});
		
		
		terminalCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Add a variable to the terminal. To recall variable, enter it with $";}			
			@Override
			public String getCommand() {return "get";}			
			@Override
			public String getArgs() {return "key";}			
			@Override
			public String doCommand( String[] command)  throws LoociManagementException{
				String get = savedVars.get(command[1]);
				return "get : "+ command[1] + ", contained "+ get + "\r\n";
			}
		});
		
		terminalCommands.add(new ServiceCommand() {			
			@Override
			public String getExtendedHelp() {
				return "";
			}
			
			@Override
			public String getCommand() {
				return "ping";
			}			
			@Override
			public String getArgs() {
				return "address";
			}			
			@Override
			public String doCommand(String[] command) throws LoociManagementException {
				InetAddress inet;
			    String retVal = "Sending ping request to " + command[1] + "\r\n";
				try {

					inet = InetAddress.getByName(command[1]);
	
					 if(inet.isReachable(5000)){
						 System.out.println("PING reachable");
						 if(ping(inet)){

							 retVal += "Host is reachable";
						 } else{
							 retVal += "Route to host available, but host not reachable";
						 }
						 
					 } else{
							retVal +="Route to host not available";			
					 }
				} catch (Exception e) {
					System.out.println("PING exception");
					retVal += "encountered exception " + e.getMessage();
				}

			   return retVal;
			}
		});
		

		
		terminalCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "List the elements in the current working directory.";}			
			@Override
			public String getCommand() {return "ls";}			
			@Override
			public String getArgs() {return "";}
			@Override
			public String doCommand( String[] command)  throws LoociManagementException{
		    	File f = new File(LoociConstants.COMPONENT_DIR);
		    	File[] files = f.listFiles();
		    	String response = "currently available components : \r\n";
		    	for(int i = 0 ; i < files.length;i++){
		    		if(files[i].isFile()){
			    		response += files[i].getName() + "\r\n";		    			
		    		}
		    	}   
		    	return response;
			}
		});
		
		terminalCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Print the current working directory.";}			
			@Override
			public String getCommand() {return "pwd";}			
			@Override
			public String getArgs() {return "";}
			@Override
			public String doCommand( String[] command)  throws LoociManagementException{
		    	File f = new File(workingDirectory);
		    	return f.getAbsolutePath();
			}
		});
		
		terminalCommands.add(new ServiceCommand() {
			@Override
			public String getExtendedHelp() {return "Restarts the logger, rereading input";}			
			@Override
			public String getCommand() {return "RebootLog";}			
			@Override
			public String getArgs() {return "";}
			@Override
			public String doCommand( String[] command)  throws LoociManagementException{
		    	LLog.reboot();
		    	return "succes";
			}
		});
	}
        
        
    public String process(String input) {
		String response = "";
		String save = "";
		boolean saveVar = false;
		if(input.contains("=")){
			saveVar = true;
			String[] lines = XString.split(input,"=");
			input = lines[1].trim();
			save = lines[0].trim();
		}
		try {
			String[] command = XString.split(input," ");
			command = loadVarsInLine(savedVars, command);
			String action = command[0].toLowerCase();
			System.out.println("RECEIVED " + input);
			
			ServiceCommand commandObject = commandsMap.get(action);
			
			if(commandObject != null){
				if(command.length == 1 && !commandObject.getArgs().equals("")){
					response = "arguments of " + command[0] + ": " + commandObject.getArgs();
				} else{
					response = commandObject.doCommand(command);
				}				
			} else {
				response = "Command not found. Type help to get all commands.";
				System.out.println("Command not found. Type help to get all commands.");
			}
		} catch (LoociManagementException e){
			saveVar = false;
			byte error = e.getErrorCode();
			response += ErrorCodes.getErrorString(error) + "\r\n";
		} catch (UnknownHostException e){
			saveVar = false;
			response += "Entered host name not found :"+e.getMessage();
		} catch(IndexOutOfBoundsException exc){
			saveVar = false;
			exc.printStackTrace();
			System.out.println("[Parser] "+ "parsing exception? wrong arguments?");
			if(response.equals("")){
				response += "[Parser] exception:" +exc.toString()+  "\r\n Maybe insufficient arguments \r\nType help to see command list";
			}			
		}catch (Exception exc) {
			saveVar = false;
			exc.printStackTrace();
			System.out.println("[Parser] "
					+ "parsing exception? Wrong arguments?");
			if(response.equals("")){
				response += "[Parser] exception:" +exc.toString()+  "\r\nType help to see command list";
			}			
		}
		if(saveVar){
			savedVars.put(save, response);
		}
		return response;
	}
	
	
	private String[] loadVarsInLine(Hashtable<String,String> vars, String[] command) throws IllegalArgumentException{
		for(int i = 0 ; i < command.length ; i++){
			if(command[i].startsWith("$")){
				String var = command[i].substring(1);
				command[i] = vars.get(var);
				if(command[i] == null){
					throw new IllegalArgumentException("Could not find var: "+var);
				}
			}
		}
		return command;
	}
	
	private String fileInput(String fileName, String[] args) {
		String response = "";
		Hashtable<String, String> vars = new Hashtable<String, String>();
		int argNr = 0;
		for(int i = 2 ; i < args.length ; i++){
			argNr = i - 2;
			vars.put("args"+(argNr),args[i]);
		}
		
		try {
			File f = new File(fileName);
			if(!f.isAbsolute()){
				f = new File("looci/scripts/"+fileName);
			}
			BufferedReader input = new BufferedReader(new FileReader(f));
			if (!input.ready()) {
				input.close();
				return "input not ready or found";
			}
			String line;
			String output;
			while ((line = input.readLine()) != null) {
				if (!line.startsWith("#")) {
					// we don't want to execute comments	
					output = process(line);					
					response += line + " => "+output + "\r\n";					
				}
			}
			input.close();
			
		} catch (FileNotFoundException ex){
			ex.printStackTrace();
			response += "script not found, check if present \r\n";
		} catch (IOException exc) {
			exc.printStackTrace();
			response += "Exception occured";
		} 
		
		
		return response;
	}
	
	public String getWelcomeMessage(){
		String welcome = "Welcome to the LooCI Management client. \r\n";
		welcome += "Type 'help' if you need any.\r\n";
		welcome += "Typing a command without arguments or pressing F1 prints the required arguments. \r\n";
		welcome += "Typing 'help commandName' shows extended help for the command. \r\n";
		welcome += "Terminal support tab completion of command \r\n";
		return welcome;
	}
	
	private String workingDirectory = "";
	
	public String getWorkingDirectory(){
		return workingDirectory;
	}
	
	public void setWorkingDirectory(String dir){
		workingDirectory = dir;
	}

	public List<String> getOptions(String text) {
		List<String> options = new ArrayList<String>();
		text = text.toLowerCase();
		for(ServiceCommand cmd : commandsMap.values()){
			if(cmd.getCommand().toLowerCase().startsWith(text)){
				options.add(cmd.getCommand());
			}
		}
		return options;
	}

	public String getHelp(String text) {
		String cmdString = XString.split(text.trim()," ")[0].toLowerCase();
		ServiceCommand cmd = commandsMap.get(cmdString);
		if(cmd != null){
			return "args:"+cmd.getArgs();
		}
		return null;
	}
	
	private static boolean ping(InetAddress host) throws IOException, InterruptedException {
	    boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
	    ProcessBuilder processBuilder;
	    
	    if(host instanceof Inet6Address){
	    	if(isWindows){
		    	 processBuilder = new ProcessBuilder("ping", "-n", "1", host.getHostAddress());
	    	} else{
	    		processBuilder = new ProcessBuilder("ping6","-c", "1", host.getHostAddress());
	    	}
	    } else{

	    	 processBuilder = new ProcessBuilder("ping", isWindows? "-n" : "-c", "1", host.getHostAddress());
	    }
	    
	   
	    Process proc = processBuilder.start();

	    int returnVal = proc.waitFor();
	    return returnVal == 0;
	}
	
}

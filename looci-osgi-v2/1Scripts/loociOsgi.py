#!/usr/bin/env python2 
import os
import shutil
import sys

loociEnv = str(os.getenv('LOOCI'))
sys.path.append(loociEnv+"/lnk/lc_osgi/1Scripts/scripts")


from loociGlobals import Looci

commandList = []


import cmd_build
commandList.append(cmd_build.getCommand())
import cmd_clean
commandList.append(cmd_clean.getCommand())
import cmd_help
commandList.append(cmd_help.getCommand())
import cmd_edit
commandList.append(cmd_edit.getCommand())
import cmd_createComp
commandList.append(cmd_createComp.getCommand())
import cmd_makeComp
commandList.append(cmd_makeComp.getCommand())
import cmd_makeConf
commandList.append(cmd_makeConf.getCommand())
import cmd_start
commandList.append(cmd_start.getCommand())

Looci.commandList = commandList


curDir = os.getcwd()
###
# Start of printing
##

print ""
print "##########welcome to LooCI OSGi#############"

if(len(sys.argv) == 1):
    print "loociOsgi expects a command arguement"
    print "enter 'loociOsgi help' for more help"

 
else:
	commandName = sys.argv[1]
	print "selected command:",commandName
	found = 0
	for command in commandList:
		if command.getName() == commandName:
			found = 1
			f = command.getFunc()
			f(sys.argv[2:])
	if found == 0:
		print "sorry, could not find command, try entering help to see a list of commands"
		
print "##########goodbye#############"
			
def getCmdList():
	return commandList
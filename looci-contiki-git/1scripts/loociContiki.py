#!/usr/bin/env python2 
import os
import shutil
import sys

loociEnv = str(os.getenv('LOOCI'))
contikiDir = loociEnv+"/lnk/lc_contiki"
sys.path.append(loociEnv+"/lnk/lc_contiki/1scripts/scripts")


from loociGlobals import Looci

commandList = []


import make
commandList.append(make.getCommand())
import help
commandList.append(help.getCommand())
import edit
commandList.append(edit.getCommand())
import term
commandList.append(term.getCommand())
import setup
commandList.append(setup.getCommand())
import createComp
commandList.append(createComp.getCommand())
import makeComp
commandList.append(makeComp.getCommand())
import createImage
commandList.append(createImage.getCommand())

Looci.commandList = commandList


curDir = os.getcwd()
###
# Start of printing
##

print ""
print "##########welcome to looci contiki#############"

if(len(sys.argv) == 1):
    print "loociContiki expects a command arguement"
    print "enter 'loociContiki help' for more help"

 
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
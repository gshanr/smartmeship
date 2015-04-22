import command
import os
import sys
from loociGlobals import Looci


sys.path.append(Looci.felixDir)


import build
import doStart


def help():
	print "looci new component creator"
	print "this command creates a new component in the current working directory"
	print "required arguements: <ComponentName> <ProjectName>"
	print "Component name should be camelcase ie. HxMSensor"
	print "Project Name should be all lower-case ie. hxmsensor"
	print "The component name will automatically have Comp added to the end where appropriate."

def myCommand(arg):	
	if(len(arg) != 2):
		print "makeComp needs 2 arguements, ",len(arg),"given"
	else:
		#make component in this directory
		argList = sys.argv[2:]
		argList.append(osgiCurDir)		
		build.makeNewComponent(argList)


def getCommand():
	return command.Command("createComp",myCommand,help)
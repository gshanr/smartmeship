import command
import os
import sys
from loociGlobals import Looci


sys.path.append(Looci.felixDir)


import build
import doStart


def help():
	print "looci component builder"
	print "this command builds the component residing in the current working directory"
	print "this command can only be executed in the folder containing a component"
	print "no arguments required"

def myCommand(arg):	
	os.system("ant clean-build-comp")


def getCommand():
	return command.Command("makeComp",myCommand,help)
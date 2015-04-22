import command
import os
import sys
from loociGlobals import Looci


sys.path.append(Looci.felixDir)


import build
import doStart

def myCommand(arg):	
   	build.defaultBuild()
   	build.makeFelixConfig()
   	doStart.startFelix([])

def help():
	print "build looci, no additional arguements needed"


def getCommand():
	return command.Command("build",myCommand,help)
import command
import os
import sys
from loociGlobals import Looci


sys.path.append(Looci.felixDir)


import build
import doStart

def myCommand(arg):	
   	build.clean()

def help():
	print "cleaning looci, no additional arguements needed"


def getCommand():
	return command.Command("clean",myCommand,help)
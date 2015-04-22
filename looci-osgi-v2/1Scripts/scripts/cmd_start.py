import command
import os
import sys
from loociGlobals import Looci


sys.path.append(Looci.felixDir)

import doStart

def myCommand(arg):	
	doStart.startFelix(arg[0:])

def help():
	print "start looci, no additional arguements needed"


def getCommand():
	return command.Command("start",myCommand,help)
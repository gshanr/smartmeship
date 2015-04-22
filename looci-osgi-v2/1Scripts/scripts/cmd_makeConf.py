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
	print "creates a new configuration, based on the bundles that are currently available in the felix bundle dir"
	print "ordering is based on the osgi build file, bundles not mentioned will be started after all mentioned ones"
	print "order in not mentioned bundles can be random"


def getCommand():
	return command.Command("makeConf",myCommand,help)
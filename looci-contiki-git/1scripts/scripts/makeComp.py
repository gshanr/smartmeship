import command
import os
from loociGlobals import Looci

def myCommand(arg):
	print "execute make command with args:"+' '.join(arg)
	if(len(arg) == 0):
		os.system('make comp')
	else:
		os.system('make '+' '.join(arg))
		

def help():
	print "Help for make"
	print "current accepted arguement for make are:"
	print "\t comp: creates a new comp file in the current dir without debug"
	print "\t debug: creates a new comp file in the current dir with debug enabled"
	print "\t copy: same as comp, but copies it to the LooCI component repository"
	print "\t cpd: same as debug, but copies it to the LooCI component repository"
	print "\t clean: cleans the temporary build files"
	print "\t cl: cleans all temporary build files, and built component files"


def getCommand():
	return command.Command("makeComp",myCommand,help)
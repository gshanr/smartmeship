import command
import os
from loociGlobals import Looci

def myCommand(arg):
	print "loociContiki help"
	if len(arg) > 0:
		print "searching help for",arg[0]		
		for command in Looci.commandList:
			if command.getName() == arg[0]:
				f = command.getHelp()
				f()
	else:
		print "accepted command are:  "
		for command in Looci.commandList:
			print command.getName()
		print ""
		print "execute 'loociContiki help cmdName' for more information on that command"


def help():
	print "help"


def getCommand():
	return command.Command("help",myCommand,help)
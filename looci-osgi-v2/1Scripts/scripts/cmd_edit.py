import command
import os
from loociGlobals import Looci

def openFile(location):		
	editor = str(os.getenv('EDITOR'))
	if(editor != "None"):
		print "opening with default editor: "+editor
		os.system(editor+" "+location)
	else:
		print "opening with xdg-open"
		os.system("xdg-open "+location)

def myCommand(arg):
	print "execute edit command with args:"+' '.join(arg)
	if(len(arg) == 0):
		print "sorry, edit requires an arguement, please check help edit"
	else:	
		if arg[0] == "build":
			openFile(Looci.scriptDir+"/loociCompFile.txt")
		
			os.chdir(Looci.scriptDir)
		else:
			print "sorry, cannot edit that file, please perform help to see which files can be edited"
		

def help():
	print "Help for edit"
	print "current accepted arguement for edit are:"
	print "\t build: edit the build options of the felix OSGi build"


def getCommand():
	return command.Command("edit",myCommand,help)
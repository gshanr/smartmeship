import command
import os
from loociGlobals import Looci

list = ["debug","options","target","programmer"]

def openFile(location):		
	editor = str(os.getenv('EDITOR'))
	if(editor != "None"):
		print "opening with default editor: "+editor
		os.system(editor+" "+location)
	else:
		print "opening with nano"
		os.system("nano "+location)

def myCommand(arg):
	print "execute edit command with args:"+' '.join(arg)
	if arg[0] in list:
		os.chdir(Looci.lc_contiki_Env+"/build")
		openFile('Makefile.'+arg[0])
	else:
		print "sorry, cannot edit that file, please perform help to see which files can be edited"
		

def help():
	print "Help for edit"
	print "current accepted arguement for edit are:"
	print "\t debug: edit the debug options of the contiki image"
	print "\t options: edit the build options of this contiki image"
	print "\t target: edit the programming target"
	print "\t programmer: edit the used programmer"


def getCommand():
	return command.Command("edit",myCommand,help)
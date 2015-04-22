import command
import os
import shutil
from loociGlobals import Looci

def myCommand(arg):
	curDir = os.getcwd()
	
	if(len(arg) < 2):
		print "createComp requires two arguements, please provide these args"
	else:
		os.chdir(Looci.lc_contiki_Env+"/1scripts")
		os.system('./createComponent '+' '.join(arg))
		shutil.move(Looci.lc_contiki_Env+"/components/myComponents/"+arg[0],curDir)

def help():
	print "Help for createComp"
	print "createComp creates a new contiki component in the current directory"
	print "createComp requires two arguements: projectName componentName"
	print "projectName is the name of the folder containing the project"
	print "componentName is the name of the actual component"


def getCommand():
	return command.Command("createComp",myCommand,help)

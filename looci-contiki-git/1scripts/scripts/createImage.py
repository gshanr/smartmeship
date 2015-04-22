import command
import os
import shutil
from loociGlobals import Looci

def myCommand(arg):
	curDir = os.getcwd()
	
	if(len(arg) < 1):
		print "createComp requires one arguement"
	else:
		os.chdir(Looci.lc_contiki_Env+"/1scripts")
		os.system('./createImage '+' '.join(arg))
		if("here" in arg[1:]):
			shutil.move(Looci.lc_contiki_Env+"/cmp_images/"+arg[0],curDir)

def help():
	print "Help for createImg"
	print "use: createImg imgName [options]"
	print "createImg creates a new contiki image in the cmp_images directory"
	print "createImg requires one arguements: imgName: the name of the new image"
	print "the following options are supported:"
	print "\t here: puts the image in the current working dir instead of  the cmp_images directory"


def getCommand():
	return command.Command("createImg",myCommand,help)
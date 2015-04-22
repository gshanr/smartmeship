import command
import os
from loociGlobals import Looci


def myCommand(arg):
	os.chdir(Looci.lc_contiki_Env+"/1scripts")
	if(len(arg)==0):
		print "opening dev ttyUSB0"
		os.system("./openTerm /dev/ttyUSB0 57600")
	elif(len(arg) == 1):		
		os.system("./openTerm "+arg[0]+" 57600")
	else:
		os.system("./openTerm "+arg[0]+" "+arg[1])
		

def help():
	print "Help for terminal"
	print "Terminal offers a serial debug interface, using the contiki dump image"
	print "can be used either without additional arguements, or with the port to listen to"
	

def getCommand():
	return command.Command("term",myCommand,help)
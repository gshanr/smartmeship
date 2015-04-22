import command
import os
from loociGlobals import Looci


def myCommand(arg):
	os.chdir(Looci.lc_contiki_Env+"/1scripts")
	if(arg[0] == "gw"):
		print "setting up gateway"
		os.system("./doSetup")
	elif(arg[0] == "endGw"):
		print "ending gateway"
		os.system("./doTeardown")
	elif (arg[0] == "networkNode"):
		os.chdir(Looci.lc_contiki_Env+"/1scripts")
		print "setting up networkNode"
		os.system("./doTutorialSetup")
	elif (arg[0] == "tunslip"):
		os.chdir(Looci.contiki_env+"/tools")
		os.system("make tunslip6")
		port = "/dev/ttyUSB0"
		if(len(arg)>1):
			port = arg[1]
			
		os.system("sudo ./tunslip6 -v5 -s "+port+" -B 38400 aaaa::1/64")
	else:
		print "unknown setup"
			

def help():
	print "Help for setup"
	print "current accepted arguement for setup are:"
	print "\t gw: sets this pc up as a gateway node"
	print "\t endGw: shuts down radvd"
	print "\t networkNode: sets this node up as a network node, with a remote gateway for connecting to the WSN"
	print "\t tunslip: starts tunslip to /dev/ttyUSB0. If other tty, submit as arguement"

def getCommand():
	return command.Command("setup",myCommand,help)
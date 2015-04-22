import command
import os
from loociGlobals import Looci

def myCommand(arg):
	print "execute make command with args:"+' '.join(arg)
	os.chdir(Looci.lc_contiki_Env+"/build")
	os.system('make '+' '.join(arg))
		

def help():
	print "Help for make"
	print "current accepted arguement for make are:"
	print "\t no arguement: creates a hex and elf image in build folder"
	print "\t upload: builds and uploads a contiki image"
	print "\t fuses: programs the fuses"
	print "\t ravenUsbStick: programs the raven usb stick: make sure USB stick is connected"
	print "\t ravenLcdScreen: programs the raven lcd screen: make sure LCD screen is connected"
	print "\t zigBorderRouter : programs a zigduino sensor node as border router, make sure zigduino is connected"
	print "\t clean: clean the build folder"

def getCommand():
	return command.Command("make",myCommand,help)

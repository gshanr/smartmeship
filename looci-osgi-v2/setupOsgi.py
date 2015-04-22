#!/usr/bin/env python2 
import os
import shutil
import sys
from sys import platform as _platform

loociEnv = str(os.getenv('LOOCI'))

if(loociEnv == "None"):
	sys.exit("could not find looci environement")

print "looci osgi at",loociEnv
curDir = os.getcwd()
print "installing osgi at",curDir

if not os.path.exists(loociEnv+"/bin"):
	print("creating bin dir")
	os.makedirs(loociEnv+"/bin")
if not os.path.exists(loociEnv+"/lnk"):
	print("creating lnk dir")
	os.makedirs(loociEnv+"/lnk")

if _platform == "linux" or _platform == "linux2" or _platform=="darwin":

    # linux
	os.system("rm $LOOCI/lnk/lc_osgi")
	os.system("ln -sf "+curDir+" $LOOCI/lnk/lc_osgi")
	os.system("ln -sf "+curDir+"/1Scripts/loociOsgi.py $LOOCI/bin/loociOsgi")
	os.system("ln -sf "+curDir+"/1Scripts/loociOsgi.py $LOOCI/bin/lcos")

elif _platform == "win32":
	os.system("mklink /J %LOOCI%\lnk\lc_osgi "+curDir)

	os.system("mklink /H %LOOCI%\\bin\\loociOsgi.py " + curDir +"\\1Scripts\\loociOsgi.py")

	print "windows setup done"
	# Windows...
	

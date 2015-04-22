import os
import shutil
import sys


loociEnv = os.getenv('LOOCI')
felixDir = loociEnv + '/lnk/lc_osgi/LoociCore/Felix'



def startFelix(arguements):


	nrArgs = len(arguements)

	if(nrArgs == 0):
		print 'starting default looci'
		try:
			shutil.rmtree(felixDir+'/felix-cache')
		except:
			print 'felix cache not found'

	
		os.chdir(felixDir)
		os.system("java -jar bin/felix.jar")
	else:
		configFile = arguements[0]
		print 'starting looci with config name '+configFile
		try:
			shutil.rmtree(felixDir+'/looci-cache/'+configFile)
		except:
			print 'felix cache not found'
	
		os.chdir(felixDir)
		os.system("java -DloociConfig="+configFile+".txt"+" -jar bin/felix.jar looci-cache/"+configFile)

if __name__ == "__main__":
	startFelix(sys.argv[1:])

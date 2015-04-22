import os
import shutil

prioBundles=["LoociOsgiServices.jar" , "LoociOsgiRuntime.jar",
"LoociOsgiServicesExt.jar", "LoociOsgiUtils.jar", "LoociOsgiMgtClient.jar",
"LoociOsgiDeploy.jar", "LoociOsgiGui.jar"]

def prio(bundle):
	try:
		return prioBundles.index(bundle)
	except ValueError:
		return 99

path = os.environ["LOOCI"]+ "/lnk/lc_osgi/bundle"
bundles = os.listdir(path)
print bundles
bundles = sorted(bundles,key=prio)
print bundles
newBundles = []
for bundle in bundles:
	newBundles.append("file:../../bundle/"+bundle+" \\\n")

print newBundles

totalLine = reduce(lambda prev,x: prev + x,newBundles,"") 
print "tl",totalLine

srcfile = "looci.properties.template"
targetFile = "looci.properties"

source = open(srcfile,'r')
destination = open(targetFile, 'w')
for line in source:
	if line.startswith("{REPLACE_BUNDLES}"):
		destination.write(totalLine)
	else:
		destination.write(line)

source.close()
destination.close()


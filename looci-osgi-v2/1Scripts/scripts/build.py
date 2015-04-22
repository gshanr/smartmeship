#!/usr/bin/env python2
import os
import shutil
import loadComponentsFile


from loociGlobals import Looci
prioBundles = []
toCompile = []

def build(componentFile, targetDir, buildOptions):
	(prioBundles,toCompile) = loadComponentsFile.loadFoldersGiven(componentFile,componentFile)
	try:
		shutil.rmtree(targetDir)
		print 'bundle dir removed'
	except:
		print 'bundle dir not found'

	os.makedirs(targetDir)

	for file in toCompile:
		os.chdir(file)
		print ''
		print file
		print os.listdir('.')
		os.system("ant -Ddup.dir="+targetDir+" "+buildOptions)

def clean():
	(prioBundles,toCompile) = loadComponentsFile.loadFolders()
	
	
	try:
		shutil.rmtree(Looci.bundleDir)
		print 'bundle dir removed'
	except:
		print 'bundle dir not found'
	
	os.makedirs(Looci.bundleDir)
	
	
	for file in toCompile:
		os.chdir(file)
		print ''
		print file
		print os.listdir('.')
		os.system("ant clean")


def defaultBuild():
	(prioBundles,toCompile) = loadComponentsFile.loadFolders()
	
	
	try:
		shutil.rmtree(Looci.bundleDir)
		print 'bundle dir removed'
	except:
		print 'bundle dir not found'
	
	os.makedirs(Looci.bundleDir)
	
	
	for file in toCompile:
		os.chdir(file)
		print ''
		print file
		print os.listdir('.')
		os.system("ant")
		
def prio(bundle):
    global prioBundles
    try:
        return prioBundles.index(bundle)
    except ValueError:
        return 99
	
def makeFelixConfig():
	global prioBundles
	(prioBundles,toCompile) = loadComponentsFile.loadFolders()
		
	print "prio",(prioBundles)
	print "compile",(toCompile)
	
	bundles = os.listdir(Looci.bundleDir)
	print bundles
	bundles = sorted(bundles,key=prio)
	print bundles
	newBundles = []
	for bundle in bundles:
	    newBundles.append("file:bundle/plugins/"+bundle+" \\\n")
	
	print newBundles
	
	totalLine = reduce(lambda prev,x: prev + x,newBundles,"") 
	print "tl",totalLine
	
	
	srcfile = "looci.properties.template"
	targetFile = "looci.properties"
	
	os.chdir(Looci.osgiEnv+'/1Scripts/confs')
	
	source = open(srcfile,'r')
	destination = open(targetFile, 'w')
	for line in source:
	    if line.startswith("{REPLACE_BUNDLES}"):
	        destination.write(totalLine)
	    else:
	        destination.write(line)
	
	source.close()
	destination.close()
	
	configFile = Looci.felixDir+'/conf/config.properties'
	
	shutil.copyfile(targetFile,configFile)
	
	
		
def makeNewComponent(argumentList):
	nrArgs = len(argumentList)
	
	print 'Number of arguments:', nrArgs , 'arguments.'
	print 'Argument List:', str(argumentList)
	
	if(nrArgs < 2):
	
		print "Need arguements <ComponentName> <projectName> <targetLocation*>"
		print "Component name should be camelcase ie. SomeSensor"
		print "Project Name should be all lower-case ie. somesensor"
		print "The component name will automatically have Comp added to the end where appropriate."
	else:
		if(nrArgs == 2):
			target = Looci.componentDir
		else:
			target = argumentList[2]
	
		print "Generating component..."
		os.chdir(Looci.scriptDir+'/buildFiles')
		os.system('ant -Dproject_name='+argumentList[0]+' -Dpackage_name='+argumentList[1]+' -Dcomp.dir='+target+' -f componentGenerator.xml generate')

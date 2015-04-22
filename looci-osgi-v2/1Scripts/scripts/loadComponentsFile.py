import os
import shutil

from loociGlobals import Looci

toCompile = []


dirs = {"$looci":Looci.loociEnv,"$osgi":Looci.osgiEnv,"$coreEnv":Looci.coreEnv,"$mgtEnv":Looci.mgtEnv}
prioBundles = []
toCompile = []

def addFolder(path,folder):
    global prioBundles
    global toCompile
    toCompile.append(path + '/' + folder)
    prioBundles.append(folder + '.jar')


def loadFoldersGiven(fileName,defaultFile):
    try:
        with open(fileName) as f:
            lines = f.read().splitlines()
    except IOError:
       print 'components file not found, loading defaults'
       shutil.copy2(defaultFile,fileName)
       with open(fileName) as f:
            lines = f.read().splitlines()
    
    
    for line in lines:
        parts = line.split(" ")
        if(not line.startswith("#") and len(parts) >= 3):   
            dir = parts[1] 
            target = parts[2]
            for replaceFile in dirs:
                dir = dir.replace(replaceFile,dirs[replaceFile])        
            if(line.startswith("b")):
                addFolder(dir, target)           
                
            elif(line.startswith("d")):
                print "adding $",target,"as dir",dir
                dirs["$"+target] = dir
    return (prioBundles,toCompile)

def loadFolders():    
    os.chdir(Looci.scriptDir)
    return loadFoldersGiven("loociCompFile.txt","scripts/defaultComponents.txt")

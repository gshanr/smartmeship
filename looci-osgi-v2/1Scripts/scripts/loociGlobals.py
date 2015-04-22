import os

class Looci(object):
    loociEnv = str(os.getenv('LOOCI'))
    osgiEnv = loociEnv + '/lnk/lc_osgi'
    scriptDir = loociEnv + '/lnk/lc_osgi/1Scripts'
    componentDir = loociEnv + '/lnk/lc_osgi/LoociComponents'
    coreEnv = osgiEnv + '/LoociCore'
    mgtEnv = osgiEnv + '/LoociMgt'
    felixDir = coreEnv + '/Felix'
    bundleDir = felixDir + '/bundle/plugins'

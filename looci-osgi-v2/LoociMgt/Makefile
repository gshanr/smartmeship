# Makefile to build Looci Middleware.
# DO NOT OVERWRITE THIS FILE
# To set which elements should be built, please modify the Makefile.Plugins in the root folder
DIR= $(LOOCI)/lnk/lc_osgi/bundle

# usually this is passed by command line
# this will only override if it isn't
BUILD_PLATFORM=pc

all_dir= LoociOsgiServicesExt LoociOsgiMgtClient LoociOsgiDeploy LoociOsgiTelnetClient\
	LoociOsgiDeployOsgi   LoociOsgiGuiClient  LoociOsgiMgtGui\
	LoociOsgiNetworkVisualizer LoociOsgiDeploy  LoociOsgiDeploySMesh  LoociOsgiDeployRaven  LoociOsgiEventDebug    \
	LoociOsgiMgtTerminal  LoociOsgiTester 
ifndef ($PLUGINS)
	PLUGINS=$(all_dir)

endif
MAKEDIRS = $(PLUGINS:%=%.bundle)
CLEANDIRS = $(all_dir:%=%.clean)

all: $(MAKEDIRS)

%.bundle:
	echo "Building ..." $*
	ant -Dbuild.platform=$(BUILD_PLATFORM) -Ddup.dir=$(DIR) -f $*/build.xml


clean: $(CLEANDIRS)

%.clean:
	ant -f $*/build.xml clean


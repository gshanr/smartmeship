# Makefile to build Looci Middleware.

DIR= $(LOOCI)/lnk/lc_osgi/bundle

# usually this is passed by command line
# this will only override if it isn't
BUILD_PLATFORM=pc


all_dir= LoociOsgiServices LoociOsgiRuntime 

MAKEDIRS = $(all_dir:%=%.bundle)
CLEANDIRS = $(all_dir:%=%.clean)

all: $(MAKEDIRS)

%.bundle:
	echo "Building ..." $*
	ant -Dbuild.platform=$(BUILD_PLATFORM) -Ddup.dir=$(DIR) -f $*/build.xml


clean: $(CLEANDIRS)

%.clean:
	ant -f $*/build.xml clean

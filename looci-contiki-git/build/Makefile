# LOOCI MAKE FILE
# DO NOT ALTER THIS FILE
all: 
	$(MAKE) -f ../core/Makefile.looci loociCore.hex > debug.txt
	$(MAKE) -f ../core/Makefile.looci loociCore.elf > debug.txt

.PRECIOUS: loociCore.out
include ${LOOCI}/lnk/lc_contiki/build/Makefile.options
include ${LOOCI}/lnk/lc_contiki/build/Makefile.programmer

clean:
	$(MAKE) -f ../core/Makefile.looci clean TARGET=avr-raven
	$(MAKE) -f ../core/Makefile.looci clean TARGET=avr-zigduino
	-rm -f symbols.c symbols.h
	-rm -f loociCore.hex loociCore.elf loociCore.myelf loociCore.minimal-net
	-rm debug.txt
			
			  
upload:
	$(MAKE) -f ../core/Makefile.looci loociCore.u > debug.txt
	
	
fuses:
	$(MAKE) -f ../core/Makefile.looci loociCore.fuses > debug.txt

eeprom:
	$(MAKE) -f ../core/Makefile.looci loociCore.ueep > debug.txt	
	
term:
	../1scripts/openTerm /dev/ttyUSB0 57600
	
ravenUsbStick:
	-$(MAKE) -C $(LOOCI)/lnk/lc_contiki_os/examples/ravenusbstick upload > debug.txt
	-$(MAKE) -C $(LOOCI)/lnk/lc_contiki_os/examples/ravenusbstick clean > debug.txt
	
ravenLcdScreen:
	-$(MAKE) -C $(LOOCI)/lnk/lc_contiki_os/platform/avr-ravenlcd-looci upload > debug.txt
	-$(MAKE) -C $(LOOCI)/lnk/lc_contiki_os/platform/avr-ravenlcd-looci clean > debug.txt

zigBorderRouter:
	-$(MAKE) -C $(LOOCI)/lnk/lc_contiki_os/examples/ipv6/rpl-border-router TARGET=avr-zigduino savetarget
	-$(MAKE) -C $(LOOCI)/lnk/lc_contiki_os/examples/ipv6/rpl-border-router clean > debug.txt
	-$(MAKE) -C $(LOOCI)/lnk/lc_contiki_os/examples/ipv6/rpl-border-router border-router.eu MY_CHANNEL_ID=$(MY_CHANNEL) PROGRAMMER=$(PROGRAMMER) > debug.txt
	-$(MAKE) -C $(LOOCI)/lnk/lc_contiki_os/examples/ipv6/rpl-border-router border-router.u MY_CHANNEL_ID=$(MY_CHANNEL) PROGRAMMER=$(PROGRAMMER) > debug.txt
	-$(MAKE) -C $(LOOCI)/lnk/lc_contiki_os/examples/ipv6/rpl-border-router clean > debug.txt
			
loociCore.%:
	$(MAKE) -f ../core/Makefile.looci $@ > debug.txt

ifeq ($(PROGRAMMER),)
  -include ${LOOCI}/lnk/lc_contiki/build/Makefile.programmer
  ifeq ($(PROGRAMMER),)
    ${info PROGRAMMER not defined, using programmer 'jtag2'}
    PROGRAMMER=jtag2
  else
    ${info using saved programmer '$(PROGRAMMER)'}
  endif
endif

all:
	${MAKE} TARGET=avr-ravenusb NOAVRSIZE=1 -f Makefile.ravenusbstick ravenusbstick.elf
	avr-objcopy -O ihex -R .signature -R .fuse -R .eeprom ravenusbstick.elf ravenusbstick.hex
	avr-objcopy -j .eeprom --set-section-flags=.eeprom="alloc,load" --change-section-lma .eeprom=0 -O ihex ravenusbstick.elf ravenusbstick.eep
	avr-size -C --mcu=at90usb1287 ravenusbstick.elf

clean:
	${MAKE} -f Makefile.ravenusbstick clean
	rm -rf obj_avr-ravenusb
	rm symbols.c symbols.h ravenusbstick.elf ravenusbstick.hex ravenusbstick.eep

upload: all
	avrdude -P usb -c $(PROGRAMMER) -p usb1287 -U flash:w:ravenusbstick.hex -U eeprom:w:ravenusbstick.eep

%:
	$(MAKE) -f Makefile.ravenusbstick $@

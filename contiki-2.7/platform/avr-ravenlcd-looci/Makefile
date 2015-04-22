# Hey Emacs, this is a -*- makefile -*-
###############################################################################
# Makefile for the RES-Raven-Mega3290 project
###############################################################################

## Update progammer in associated directory, or give PROGRAMMER arguement
ifeq ($(PROGRAMMER),)
  -include ${LOOCI}/lnk/lc_contiki/build/Makefile.programmer
  ifeq ($(PROGRAMMER),)
    ${info PROGRAMMER not defined, using programmer 'jtag2'}
    PROGRAMMER=jtag2
  else
    ${info using saved programmer '$(PROGRAMMER)'}
  endif
endif

## General Flags
PROJECT = ravenlcd_3290
MCU = atmega3290
TARGET = $(PROJECT).elf
CC = avr-gcc

## Options common to compile, link and assembly rules
COMMON = -mmcu=$(MCU)
COMMON += -DF_CPU=8000000UL

## Compile options common for all C compilation units.
CFLAGS = $(COMMON) $(CEXTRA)
CFLAGS += -D AVRGCC -Wall -gdwarf-2  -Os -fsigned-char
CFLAGS += -MD -MP -MT $(*F).o -MF dep/$(@F).d
CFLAGS += -fshort-enums

## Assembly specific flags
ASMFLAGS = $(COMMON)
ASMFLAGS += -x assembler-with-cpp -Wa,-gdwarf-2

## Linker flags
LDFLAGS = $(COMMON)
LDFLAGS += -Wl,-Map=$(PROJECT).map,--cref

## Intel Hex file production flags
HEX_FLASH_FLAGS = -R .eeprom -R .fuse

HEX_EEPROM_FLAGS = -j .eeprom
HEX_EEPROM_FLAGS += --set-section-flags=.eeprom="alloc,load"
HEX_EEPROM_FLAGS += --change-section-lma .eeprom=0

## Programming flags

ifeq ($(PROGRAMMER),jtag2)
	AVRDUDE_PORT=usb
	AVRDUDE_PROGRAMMER=jtag2
	AVARICE_OPTIONS=-2 -j usb
endif

ifeq ($(PROGRAMMER),jtag3)
	AVRDUDE_PORT=usb
	AVRDUDE_PROGRAMMER=jtag3
	AVARICE_OPTIONS=-3
endif


## Include Directories
INCLUDES =

## Objects that must be built in order to link
SRC = adc.c key.c  lcd.c  raven3290.c  uart.c menu.c beep.c temp.c timer.c looci_gui.c

OBJECTS = $(SRC:.c=.o)

## Objects explicitly added by the user
LINKONLYOBJECTS =

## Build
all: $(TARGET) $(PROJECT).hex $(PROJECT).eep $(PROJECT).lss size

## Compile: create object files from C source files.
.c.o:
	$(CC) $(INCLUDES) $(CFLAGS) -c  $<

##Link
$(TARGET): $(OBJECTS)
	 $(CC) $(LDFLAGS) $(OBJECTS) $(LINKONLYOBJECTS) $(LIBDIRS) $(LIBS) -o $(TARGET)

%.hex: $(TARGET)
	avr-objcopy -O ihex $(HEX_FLASH_FLAGS)  $< $@

%.eep: $(TARGET)
	avr-objcopy $(HEX_EEPROM_FLAGS) -O ihex $< $@  || exit 0

%.lss: $(TARGET)
	avr-objdump -h -S $< > $@

size: ${TARGET}
	@echo
	@avr-size -C --mcu=${MCU} ${TARGET}

## Clean target
.PHONY: clean
clean:
	-rm -rf $(OBJECTS) $(PROJECT).elf dep/* $(PROJECT).hex $(PROJECT).eep $(PROJECT).map $(PROJECT).lss

## Other dependencies
## In cygwin the /dep folder causes make to fail after the initial make.
## $make CYG=1 allows cleans and makes based on .c dependencies (but not .h)
ifndef CYG
-include $(shell mkdir dep 2>/dev/null) $(wildcard dep/*)
endif

upload: $(PROJECT).hex $(PROJECT).eep
	-avarice ${AVARICE_OPTIONS} -R --erase
	sleep 5
	avrdude -c ${AVRDUDE_PROGRAMMER} -P ${AVRDUDE_PORT} -p atmega3290p -U efuse:w:0xFF:m -U lfuse:w:0xE2:m -U hfuse:w:0x99:m -U flash:w:$(PROJECT).hex

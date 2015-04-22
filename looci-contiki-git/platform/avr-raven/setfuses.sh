#!/bin/sh

avrdude -c jtag2 -P usb -p atmega1284p -U efuse:w:0xFF:m -U lfuse:w:0xE2:m -U hfuse:w:0x11:m

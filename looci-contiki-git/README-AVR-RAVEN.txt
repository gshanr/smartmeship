Fuses
-----

Set the fuses for the ATmega1284P as follows:
lfuse: 0xE2
hfuse: 0x11
efuse: 0xFF

hfuse differs from contiki instructions. We additionally set EESAVE fuse to
preserve EEPROM during programming. EEPROM doesn't change frequently during
development, so having to reflash it every time is cumbersome.

For the other MCU's, follow the instructions on:
http://www.sics.se/~adam/contiki/docs/a01180.html#advanced


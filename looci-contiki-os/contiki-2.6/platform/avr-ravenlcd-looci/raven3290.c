/*
 *     \mainpage ATmega3290p LCD Driver Software for Raven
*/
/**
 *     \image html raven.png
 *     \ingroup platform
 *     \defgroup lcdraven RZRAVEN LCD 3290p
 *
 *     \section intro_lcd LCD Introduction
 *
 *  This Raven LCD Driver application software was designed for a user interface
 *  to the Contiki 6LoWPAN collaboration on board the ATmega3290p. The
 *  LCD functionality uses the binary command set described in the release notes.
 *  These binary commands can also be found in a list of main.h.
 *
 *     \section compile_lcd Compiling Raven LCD Driver
 *
 *  Raven LCD Driver can be compiled on the following platforms:
 *
 *   -# <b>WinAvr + AVR Studio (AVR-GCC).</b>  The @b ravenlcd_3290.aps file is used by AVR
 *      Studio.  If you have WinAVR and AVR Studio installed, ravenlcd can be
 *      compiled and developed using free tools.  The Makefile.avr-ravenlcd is not
 *      used with AVR Studio.
 *   -# <b>AVR-GCC on Linux.</b>  The <b>Makefile.avr-ravenlcd</b> file is to be used for Linux.
 *      Be sure you have a recent toolchain installed, including patches
 *      required for GCC/avr-libc to recognize new devices.  The avr-libc
 *      webpage includes a concise guide on how to patch and build the AVR
 *      toolchain.
 *
 *     \section fuses_lcd Board fuse settings
 *
 *  The Raven LCD (3290p device) requires the proper fuse settings to function
 *  properly. These settings have been summarized below:
 *   -# Raven LCD (3290p device)
 *      -# Extended: <b>0xFF</b> (No Brown Out)
 *      -# High: <b>0x99</b> (JTAG and ISP enabled, No OCDEN or EEPROM saving required)
 *      -# Low: <b>0xE2</b> (Use Int RC OSC - Start-up Time:6CK + 65ms)
 *
 *     \section notes_lcd Operation Release Notes
 *
 *  After programming the Raven LCD 3290p with the proper image, you will be introduced to
 *  the menu in the picture below:
 *
 *     \image html contiki_menu_3290.jpg
 *
 *  Operating the menu requires that the matching command set has been programmed into
 *  the ATmega1284 application. This will allow the menu to communicate properly and control the
 *  Contiki 6LoWPAN applcation.
 *
 *  During normal operation, you will need to make note of these <b>IMPORTANT</b> operating instructions:
 *   -# <b>Temp Sensor</b> - The temperature sensor shares the same GPIO as the JTAG interface for the 3290p.
 *   This requires the JTAG feature to be <b>disabled</b> in order to have proper temperature readings.
 *   -# <b>Debug Menu</b> - The DEBUG menu setting is used to configure this JTAG feature.
 *      -# If the JTAG feature is enabled during a temperature reading attempt,
 *      the menu will signal a <b>caution</b> symbol to the user letting them know the JTAG
 *      feature needs to be disabled.
 *     \image html caution.gif
 *      -# The JTAG header may also need to be physically disconnected from any external
 *      programming/debugging device in order to obtain correct temperature readings.
 *   -# <b>Temp Data</b> - Once the temperature reading is proper, the user can send this reading
 *   to the webserver for Sensor Reading Data (<b>Once</b> or <b>Auto</b>). The webserver will
 *   only update the html data when <b>refreshed</b>.
 *   -# <b>EXT_SUPL_SIG</b> - This signal connects the external supply voltage to ADC2 through a divider.
 *   Enabling MEASURE_ADC2 in temp.h causes it to be sampled and sent to the webserver along
 *   with the temperature.
 *
 *   More information about the operation of the Contiki 6LoWPAN system can be found
 *   at the \ref tutorialraven.
 *
 *   More information about the 802.15.4 MAC designed for the Contiki 6LoWPAN system
 *   can be found at the \ref macdoc.
 *
 *     \section binary_lcd Binary Command Description
 *
 *  Using the binary commmand list described in main.h, the 3290p will contruct a binary
 *  command serial frame to control the 1284p. An example command frame is contructed below:
 *   -# <b>0x01,0x01,0x81,0x01,0x04</b> - Send Ping Request number 1 to 1284p
 *       -# <b>0x01</b> - Start of binary command frame
 *       -# <b>0x01</b> - Length of binary command payload
 *       -# <b>0x81</b> - Binary command SEND_PING
 *       -# <b>0x01</b> - Payload value (eg. ping Request number 1)
 *       -# <b>0x04</b> - End of binary command frame
 *
 *  The following commands are used to control the 1284p.
 *   -# <b>SEND_TEMP - (0x80)</b>
 *   -# <b>SEND_PING - (0x81)</b>
 *
 *  The following commands are used to update the 3290p.
 *   -# <b>REPORT_PING - (0xC0)</b>
 *   -# <b>REPORT_PING_BEEP - (0xC1)</b>
 *   -# <b>REPORT_TEXT_MSG - (0xC2)</b>
*/
/*
 *  Copyright (c) 2008  Swedish Institute of Computer Science
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *  * Neither the name of the copyright holders nor the names of
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * Modified for LooCI.
 * Copyright (c) 2011, Katholieke Universiteit Leuven
 * All rights reserved.
 */

/**
 * \file
 *
 * \brief
 *      This is the main file for the Raven LCD application.
 *
 * \author
 *      Mike Vidales mavida404@gmail.com
 *
 */

#include "lcd.h"
#include "key.h"
#include "main.h"
#include "uart.h"
#include "timer.h"
#include "menu.h"
#include "temp.h"

#include "looci_gui.h"

#include <avr/io.h>
#include <avr/fuse.h>
FUSES =
	{
		.low = 0xe2,
		.high = 0x99,
		.extended = 0xff,
	};

/*---------------------------------------------------------------------------*/


/**
 *   \brief This is main...
*/
int
main(void)
{
    lcd_init();

    key_init();

    uart_init();

    eeprom_init();

    temp_init();

    timer_init();

    sei();

    menu_init();

    looci_gui_init();

    timer_start();

    for (;;){
        /* Make sure interrupts are always on */
        sei();

        /* The one second timer has fired. */
        if(timer_flag){
            timer_flag = false;
            // Redraw menu (may have been interrupted by a message)
            //menu_redraw();
        }

        /* Check for button press and deal with it */
        if (is_button()){
          menu_handle_button();
        }
        /* Process any progress frames */
        uart_serial_rcv_frame(false);
    } /* end for(). */
} /* end main(). */



/** \} */

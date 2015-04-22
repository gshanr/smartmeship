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
 *      This file operates the menu flow chart described in the readme
 *      notes. This will create the proper commands needed to control the 1284p.
 *
 * \author
 *      Mike Vidales mavida404@gmail.com
 *
 */

#include <avr/eeprom.h>
#include <avr/interrupt.h>
#include "menu.h"
#include "key.h"
#include "temp.h"
#include "lcd.h"

tmenu_item menu;

/**
 *  \addtogroup lcd
 *  \{
*/

/*---------------------------------------------------------------------------*/

/**
 *   \brief This function will convert decimal to ascii.
 *
 *   \param val Decimal value to convert.
 *   \param str Address location to store converted value.
 */
void
dectoascii(uint8_t val, char *str)
{
      *(str+1) = (val % 10) + '0';
          *str = (val / 10) + '0';
}

/**
 *  \brief This will check for DEBUG mode after power up.
*/
void
eeprom_init(void)
{
    uint8_t val;
    if(0xFF == eeprom_read_byte(EEPROM_DEBUG_ADDR)){
        /* Disable - Reverse logic. */
        val = 1;
        menu_debug_mode(&val);
    }
    else{
        /* Enable - Reverse logic. */
        val = 0;
        menu_debug_mode(&val);
    }
}

/*---------------------------------------------------------------------------*/

/**
 *   \brief This will enable or disable the JTAG debug interface to allow for
 *   proper temperature sensor readings.
 *
 *   \param val Flag to trigger the proper debug mode.
*/
void
menu_debug_mode(uint8_t *val)
{
    uint8_t sreg = SREG;
    cli();
    if(*val){
        /* Disable - Could use inline ASM to meet timing requirements. */
        MCUCR |= (1 << JTD);
        MCUCR |= (1 << JTD);
        /* Needed for timing critical JTD disable. */
        temp_init();
        /* Store setting in EEPROM. */
        eeprom_write_byte(EEPROM_DEBUG_ADDR, 0xFF);
    }
    else{
        /* Enable - Could use inline ASM to meet timing requirements. */
        MCUCR &= ~(1 << JTD);
        MCUCR &= ~(1 << JTD);
        /* Store setting in EEPROM. */
        eeprom_write_byte(EEPROM_DEBUG_ADDR, 0x01);
    }
    SREG = sreg;
}

/*---------------------------------------------------------------------------*/

void menu_init() {
  menu_draw(0); // draw main menu
}

/**
 *   \brief This will read the menu_items[] from the index requested.
 *
 *   \param ndx Position index of menu_items[] lookup.
*/
void
menu_read(uint8_t ndx)
{
    /* Reads menu structure from Flash area */
    uint8_t i;
    uint8_t *src = (uint8_t*)&menu_items[ndx];
    uint8_t *dest = (uint8_t*)&menu;

    for (i=0;i<sizeof(tmenu_item);i++){
        *dest++ = pgm_read_byte(src+i);
    }
}

inline void menu_draw(uint8_t ndx) {
  menu_read(ndx);
  menu_redraw();
}

inline void menu_redraw() {
  lcd_puts_P(menu.text);
}


void menu_handle_button() {
  /* Dispatch the button pressed */
  switch (get_button()){
      case KEY_UP:
          menu_draw(menu.up);
          break;
      case KEY_DOWN:
          menu_draw(menu.down);
          break;
      case KEY_LEFT:
          menu_draw(menu.left);
          break;
      case KEY_RIGHT:
          /*
           * Check to see if we should show another menu or
           * run a function
           */
          if (!menu.enter_func){
              /* Just another menu to display */
              menu_draw(menu.right);
              break;
          }
          /* Drop through here */
      case KEY_ENTER:
          /* Call the menu function on right or enter buttons */
          if (menu.enter_func){
              menu.enter_func(menu.state);
              if (menu.state){
                  /*
                   * We just called a selection menu (not a test),
                   * so re-display the text for this menu level
                   */
                  lcd_puts_P(menu.text);
              }
              /* After enter key, check the right button menu and display. */
              menu_draw(menu.right);
          }
          break;
      default:
          break;
  }
}
/** \}   */

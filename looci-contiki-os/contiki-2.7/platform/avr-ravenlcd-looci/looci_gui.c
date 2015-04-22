/*
 * Copyright (c) 2011, Katholieke Universiteit Leuven
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of the Katholieke Universiteit Leuven nor the names of
 *       its contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include "looci_gui.h"

#include "lcd.h"
#include "menu.h"
#include "uart.h"
#include "command.h"

/*************
 * VARIABLES *
 *************/

uint8_t nb_components = 0;
uint8_t temp_lcd_state = 0;

/*********************************
 * PRIVATE FUNCTION DECLARATIONS *
 ********************************/

void set_nbcomponents(uint8_t components);

/********
 * MENU *
 ********/

void send_door(uint8_t val);
void send_door_open(uint8_t*);
void send_door_closed(uint8_t*);
void send_move(uint8_t*);

const char menu_text0[] PROGMEM =  "LOOCI";
const char menu_text1[] PROGMEM =  "SWITCH";
const char menu_text2[] PROGMEM =  "OPEN";
const char menu_text3[] PROGMEM =  "CLOSED";
const char menu_text4[] PROGMEM =  "BUTTON";

/**
 *   \brief Menus for user interface
 *
 *   { text, left, right, up, down, *state, tmenufunc enter_func}
*/
const PROGMEM tmenu_item menu_items[5]  = {
    {menu_text0,   0,  1,  0,  0, 0,                       0                  },
    {menu_text1,   0,  2,  4,  4, 0,                       0                  },
    {menu_text2,   1,  2,  3,  3, 0,                       send_door_open     }, 
    {menu_text3,   1,  3,  2,  2, 0,                       send_door_closed   }, 
    {menu_text4,   0,  4,  1,  1, 0,                       send_move          }, 
};

/******************
 * INITIALIZATION *
 ******************/

void looci_gui_init() {
    lcd_symbol_set(LCD_SYMBOL_RAVEN);
    lcd_symbol_set(LCD_SYMBOL_IP);
    lcd_symbol_clr(LCD_SYMBOL_C);
  
    set_nbcomponents(0);
}

/*****************
 * GUI FUNCTIONS *
 *****************/

void
send_door(uint8_t val) 
{
  uart_serial_send_frame(SEND_SWITCH, 1, &val);
}

void
send_door_open(uint8_t* data)
{
  send_door(1);
}

void
send_door_closed(uint8_t* data)
{
  send_door(0);
}

void
send_move(uint8_t* data)
{
  uart_serial_send_frame(SEND_BUTTON, 0, NULL);
}

void set_nbcomponents(uint8_t components) {
  nb_components = components;
  lcd_num_putdec(nb_components, LCD_NUM_PADDING_SPACE);
}

inline void looci_add_component() {
  set_nbcomponents(nb_components+1);
}

inline void looci_remove_component() {
  set_nbcomponents(nb_components-1);
}

inline void looci_temp_measured() {
  temp_lcd_state = ~temp_lcd_state;
  if(temp_lcd_state) {
    lcd_symbol_set(LCD_SYMBOL_C);
  } else {
    lcd_symbol_clr(LCD_SYMBOL_C);
  }
}


/*
 * Copyright (c) 2009, Katholieke Universiteit Leuven
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
#include "contiki.h"
#include "raven-lcd.h"
#include "raven-msg.h"
#include "dev/rs232.h"

#include "command.h"
#include "lib/sensors.h"
#include "temp-sensor.h"
#include "switch-sensor.h"
#include "raven-button-sensor.h"

#ifdef LOOCI_RAVENLCD_DEBUG
#include <stdio.h>
#include <avr/pgmspace.h>
#define PRINTF(FORMAT,args...) printf_P(PSTR(FORMAT),##args)
#else
#define PRINTF(...)
#endif

#define SOF_CHAR 1
#define EOF_CHAR 4

#define cmd_len 8
static struct{
    uint8_t frame[cmd_len];
    uint8_t ndx;
    uint8_t len;
    uint8_t cmd;
    uint8_t done;
} cmd;

static void new_temperature(int16_t * temp);
static void new_switch_value(uint8_t * new_switch);
static void new_press(void);

// seperate function to be able to use switch on cmd.cmd
static uint8_t process_command(process_event_t ev, process_data_t data) {
	PRINTF("new command done %u, cmd %u ",cmd.done,cmd.cmd);
  if(cmd.done) {

	  // do the command
    switch(cmd.cmd) {
      case SEND_TEMP:
    	PRINTF("new temp");
        new_temperature((int16_t*)cmd.frame);
        break;
      case SEND_SWITCH:
        PRINTF("Got new switch value: %u\r\n", (uint8_t)cmd.frame[0]);
        new_switch_value((uint8_t*)cmd.frame);
        break;
      case SEND_BUTTON:
        PRINTF("Got new button\r\n");
        new_press();
        break;
      default:
        break;
    }
    cmd.done = 0;
  }
  return 0;
}

PROCESS(raven_lcd_process, "Raven LCD process");

PROCESS_THREAD(raven_lcd_process, ev, data)
{
  PROCESS_BEGIN();
  
  while(1) {
    PROCESS_WAIT_EVENT();
    if(ev == SERIAL_CMD) {
      process_command(ev, data);
    }
  }

  PROCESS_END();
}

static void send_frame(uint8_t cmd, uint8_t len, uint8_t *payload) {
    uint8_t i;

    rs232_send(0, SOF_CHAR);    /* Start of Frame */
    rs232_send(0, len);
    rs232_send(0, cmd);
    for (i=0;i<len;i++)
        rs232_send(0,*payload++);
    rs232_send(0, EOF_CHAR);
}

void lcdShow(uint8_t len, char* payload) {
  send_frame(REPORT_TEXT_MSG, len, (uint8_t*)payload);
}

void avrBuzz(uint16_t time, uint8_t buzz){
	uint8_t payload[3];
	payload[0] = time / 256;
	payload[1] = time % 256;
	payload[2] = buzz;
	send_frame(DO_BUZZ_EVENT,3,payload);
}

/*---------------------------------------------------------------------------*/
/* Process an input character from serial port.  
 *  ** This is called from an ISR!!
*/
int raven_lcd_serial_input(unsigned char ch)
{
    /* Parse frame,  */
    switch (cmd.ndx){
    case 0:
        /* first byte, must be 0x01 */
        cmd.done = 0;
        if (ch != SOF_CHAR){
            return 0;
        }
        break;
    case 1: 
        /* Second byte, length of payload */
        cmd.len = ch;
        break;
    case 2:
        /* Third byte, command byte */
        cmd.cmd = ch;
        break;
    default:
        /* Payload and ETX */
        if (cmd.ndx >= cmd.len+3){
            /* all done, check ETX */
            if (ch == EOF_CHAR){
                cmd.done = 1;
                process_post(&raven_lcd_process, SERIAL_CMD, 0);
            } else {
                /* Failed ETX */
                cmd.ndx = 0;
            }
        } else {
            /* Just grab and store payload */
            cmd.frame[cmd.ndx - 3] = ch;
        }
        break;
    }

    cmd.ndx++;

    return 0;
}

/**********************
 * Temperature sensor *
 **********************/

// Temperature sensor
#define INACTIVE 0
#define ACTIVE 1
#define READY 2
const struct sensors_sensor temp_sensor;
static int temperature = 0;
static uint8_t temp_state = INACTIVE;

static void new_temperature(int16_t * temp) {
  if(temp_state != ACTIVE) {
    return;
  }
  temperature = (int) *temp;
  temp_state = READY;
  sensors_changed(&temp_sensor);
}

static int
temp_value(int type)
{
  // Is this needed? If we are ready, we stay ready... the value gets older, but is that a problem?
  //if(state == READY) state = INACTIVE;
  return temperature;
}

static int
temp_configure(int type, int c)
{
  switch(type) {
  case SENSORS_ACTIVE:
    if(c) {
      // send a request to the atmega3290p
      temp_state = ACTIVE;
      send_frame(POLL_TEMP, 0, NULL);
      //state = READY; // temp shortcut for testing the sensors interface
      //temperature++; // temp shortcut
      //sensors_changed(&temp_sensor); // temp shortcut
    } else {
      temp_state = INACTIVE;
    }
    return 1;
  case SENSORS_HW_INIT:
    // TODO do we need init?
    return 1;
  }
  return 0;
}

static int
temp_status(int type)
{
  switch(type) {
    case SENSORS_ACTIVE:
      return (temp_state == ACTIVE);
    case SENSORS_READY:
      return (temp_state == READY);
  }
  return 0;
}

SENSORS_SENSOR(temp_sensor, TEMP_SENSOR,
                   temp_value, temp_configure, temp_status);

/***************
 * Door sensor *
 ***************/

const struct sensors_sensor switch_sensor;
static uint8_t switch_value = SWITCH_CLOSED;
static uint8_t switch_state = INACTIVE;

static void new_switch_value(uint8_t * new_switch) {
  switch_value = *new_switch;
  if(switch_state == ACTIVE || switch_state == READY) {
    // If we are not inactive: we become ready and notify users of changed value
    switch_state = READY;
    sensors_changed(&switch_sensor);
    PRINTF("[door sensor] We are now ready and we notified clients\r\n");
  }
}

static int
switch_getValue(int type)
{
  PRINTF("[door sensor] Somebody asks for our value: %u\r\n", switch_value);
  return (int) switch_value;
}

static int
switch_configure(int type, int c)
{
  switch(type) {
  case SENSORS_ACTIVE:
    if(c) {
      // Become active and send latest value to interested party
      switch_state = ACTIVE;
      new_switch_value(&switch_value);
      PRINTF("[door sensor] We are now active\r\n");
    } else {
      switch_state = INACTIVE;
      PRINTF("[door sensor] We are now inactive\r\n");
    }
    return 1;
  case SENSORS_HW_INIT:
    return 1;
  }
  return 0;
}

static int
switch_getStatus(int type)
{
  switch(type) {
    case SENSORS_ACTIVE:
      return (switch_state == ACTIVE);
    case SENSORS_READY:
      PRINTF("[door sensor] Somebody asks if we are ready\r\n");
      return (switch_state == READY);
  }
  return 0;
}

SENSORS_SENSOR(switch_sensor, SWITCH_SENSOR,
		switch_getValue, switch_configure, switch_getStatus);

/***************
 * Move sensor *
 ***************/

const struct sensors_sensor raven_button_sensor;
static uint8_t btn_sensor_state = INACTIVE;

static void new_press() {
  if(btn_sensor_state == ACTIVE || btn_sensor_state == READY) {
    // If we are not inactive: we become ready and publish our changed value
    btn_sensor_state = READY;
    sensors_changed(&raven_button_sensor);
  }
}

static int
press_value(int type)
{
  // We become READY if a move has been detected.
  if(btn_sensor_state == READY) {
    btn_sensor_state = ACTIVE;
    return (int) PRESS_DETECTED;
  } else {
    return (int) PRESS_NOT_DETECTED;
  }
}

static int
press_configure(int type, int c)
{
  switch(type) {
  case SENSORS_ACTIVE:
    if(c) {
      // Become active
      btn_sensor_state = ACTIVE;
    } else {
      btn_sensor_state = INACTIVE;
    }
    return 1;
  case SENSORS_HW_INIT:
    return 1;
  }
  return 0;
}

static int
press_status(int type)
{
  switch(type) {
    case SENSORS_ACTIVE:
      return (btn_sensor_state == ACTIVE);
    case SENSORS_READY:
      return (btn_sensor_state == READY);
  }
  return 0;
}

SENSORS_SENSOR(raven_button_sensor, BUTTON_SENSOR, press_value, press_configure, press_status);

/*************
 * LOOCI GUI *
 *************/

#ifdef WITH_RAVEN_GUI

void ravenlcd_gui_init() {
  send_frame(LOOCI_GUI_INIT,0, NULL);
}

void ravenlcd_add_component() {
  send_frame(LOOCI_ADD_COMPONENT,0, NULL);
}

void ravenlcd_remove_component() {
  send_frame(LOOCI_REMOVE_COMPONENT,0, NULL);
}

#endif // WITH_RAVEN_GUI


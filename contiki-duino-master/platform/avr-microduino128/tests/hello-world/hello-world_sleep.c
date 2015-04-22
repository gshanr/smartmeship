/*
 * Copyright (c) 2006, Swedish Institute of Computer Science.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * This file is part of the Contiki operating system.
 *
 * $Id: hello-world.c,v 1.1 2006/10/02 21:46:46 adamdunkels Exp $
 */

/**
 * \file
 *         A very simple Contiki application showing how Contiki programs look
 * \author
 *         Adam Dunkels <adam@sics.se>
 */

#include "contiki.h"
#include "dev/rs232.h"

#include <stdio.h> /* For printf() */
#include <avr/io.h>
#include <avr/sleep.h>
#include <avr/interrupt.h>
#include <util/delay.h>

uint8_t i = 0;

// Flag telling CPU to sleep
uint8_t sleep = 1;


void
dosleep() {
  _delay_ms(500);
  // enable external interrupt
  EIMSK = _BV(INT0);

  // set sleep mode
  set_sleep_mode(SLEEP_MODE_PWR_DOWN);

  // sleep_mode() has a possible race condition
  sleep_enable();
  // Make sure interrupts are enabled
  sei();
  sleep_cpu();
  sleep_disable();

  // waking up...
  // disable external interrupt here, in case the external low pulse is too long
  EIMSK = 0;

  // disable all interrupts
  //cli();

  _delay_ms(500);
  printf("Awake\n");
}

int
handler(unsigned char sym) {
  rs232_send(RS232_PORT_1, sym);
  i++;

  if(i == 6) {
    printf("recieved 6 chars\n");
    i=0;
    sleep = 1;
  }
  return 1;
}

/*---------------------------------------------------------------------------*/
PROCESS(hello_world_process, "Hello world process");
AUTOSTART_PROCESSES(&hello_world_process);
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(hello_world_process, ev, data)
{
  PROCESS_BEGIN();
  PROCESS_PAUSE();

  rs232_init(RS232_PORT_1, USART_BAUD_115200,USART_PARITY_NONE | USART_STOP_BITS_1 | USART_DATA_BITS_8);

  rs232_print(RS232_PORT_1, "Test\n");
  rs232_set_input(RS232_PORT_1, *handler);

  printf("Hello, world\n");

  DDRD &= ~(1 << PD2);    // INT0: input...
  EICRA = _BV(ISC00);

  _delay_ms(2000);

  while(1){
    _delay_ms(500);
    if(sleep) {
      sleep = 0;
      printf("going to sleep\n");
      dosleep();
    }
  }


  PROCESS_END();
}

ISR(INT0_vect)
{
  // ISR might be empty, but is necessary nonetheless
  //printf("interrupt received\n");
}
/*---------------------------------------------------------------------------*/

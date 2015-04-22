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
#include "hello-world.h"
#include "contiki-smip.h"

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>

/*
 * A global flag used to communicate between the Interrupt Service Routine
 * and the main program.  It has to be declared volatile or the compiler
 *  might optimize it out.
 */
volatile bool update = false;

uint8_t bcastaddr[16]={0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff};

/**
 * set update on a high edge
 */
ISR(INT0_vect)
{
  // ISR might be empty, but is necessary nonetheless
  //printf("interrupt received\n");
	update = true;
}
/*---------------------------------------------------------------------------*/



#include <stdio.h> /* For printf() */
/*---------------------------------------------------------------------------*/
PROCESS(hello_world_process, "Hello world process");
//AUTOSTART_PROCESSES(&hello_world_process);
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(hello_world_process, ev, data)
{
    PROCESS_BEGIN();
    static struct etimer t;


    //etimer_set(&t, CLOCK_SECOND*10);



    uint8_t buffer[2];




    DDRD &= ~(1 << PD2);    // INT0: input...
    DDRB = 0x04;
    PORTB = 0x04;
      EICRA = _BV(ISC00);

      _delay_ms(2000);


  while(1)
  {
	  PROCESS_WAIT_EVENT();


	  // If the ISR has indicated we need to update the state
	  			// then run this block.
	  			if (update) {
	  				printf("interrupted\n");
	  				update = false;
	  			}

	  printf("CThree\n");
	  PORTB ^= 0x04;
	  buffer[0]=0x00;
	  buffer[1]=0x05;
	  api_sendTo(buffer,2,61000,ipv6Addr_manager);
	  //etimer_set(&t, CLOCK_SECOND*10);
  }


    PROCESS_END();
}
/*---------------------------------------------------------------------------*/

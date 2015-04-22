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
 */

/**
 * \file
 *         A very simple Contiki application showing how Contiki programs look
 * \author
 *         Adam Dunkels <adam@sics.se>
 */

#include "contiki.h"
#include "dev/clock-avr.h"
#include <avr/io.h>
#include <avr/interrupt.h>
#include <avr/power.h>
#include <avr/sleep.h>

#include <stdio.h> /* For printf() */
/*---------------------------------------------------------------------------*/
PROCESS(hello_world_process, "Hello world process");
AUTOSTART_PROCESSES(&hello_world_process);
/*---------------------------------------------------------------------------*/


void init_Ex1(void) {

	TCCR0A = (1<<CS02)|(1<<CS00); //Timer clock = //system clock /1024
	TIFR0 = 1<<TOV0;	//Clear TOV0 / clear //pending interrupts
	TIMSK0 = 1<<TOIE0;	//Enable Timer0 //Overflow Interrupt

	sei();

}


void init_Ex3(void) {


	TCNT4 = 0;
	//TCCR0B=(1<<WGM12)|(1<<CS11)|(1<<CS10);

	//OCR4A = F_CPU/1024UL/CLOCK_CONF_SECOND;

	OCR4A = 240;

	//TCCR0A = _BV(WGM01);
	//TCCR0B =  _BV(CS00) | _BV(CS02);

	TCCR4B =  _BV(CS00) | _BV(CS01) |  _BV(CS02) |  _BV(WGM01);

	// Clear OCF2/ Clear // pending interrupts
	// Enable Timer2 Output
	// Compare Match Interrupt
	// Set Output Compare // Value to 32

	TIFR4 = 0x00;

	//TIFR0= 1<<OCF0;
	//TIMSK0= 1<<OCIE0;
	//OCR0= 32;
	//while (ASSR&(1<<OCR0UB));

	TIMSK4 = _BV (OCIE4A);


}

void toggle(struct rtimer *t, void *ptr)
{
	PORTB ^= 0x02;
	PORTD ^= 0x60;

	// rtimer_set(t, (RTIMER_TIME(t) + RTIMER_ARCH_SECOND)*4, 1,
	  //           toggle, ptr);
}


ISR(TIMER0_OVF_vect)
{
	PORTB ^= 0x02;
	PORTD ^= 0x60;
}

ISR(TIMER4_COMPA_vect)
{
	PORTB ^= 0x02;
	PORTD ^= 0x60;
}

PROCESS_THREAD(hello_world_process, ev, data)
{

  PROCESS_BEGIN();

  static struct etimer t;





  etimer_set(&t, CLOCK_SECOND*120);




  	//DDRB=0x02;
  	//DDRD=0x60;



  	//PORTB=0x02;
  	//PORTD=0x60;



  while(1)
  {
	  PROCESS_WAIT_EVENT();
	  printf("CThree\n");

	  //PORTB ^= 0x02;
	  //PORTD ^= 0x60;

//	  	  // Choose our preferred sleep mode:
//	      set_sleep_mode(SLEEP_MODE_IDLE);
//	      //
//	      // Set sleep enable (SE) bit:
//	      sleep_enable();
//	      //
//	      // Put the device to sleep:
//
//	      sleep_cpu();
//	      //
//	      // Upon waking up, sketch continues from this point.
//	      sleep_disable();


	      etimer_set(&t, CLOCK_SECOND*120);
  }
  PROCESS_END();
}
/*---------------------------------------------------------------------------*/

/*
 * Copyright (c) 2006, Technical University of Munich
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
#define PRINTF(FORMAT,args...) printf_P(PSTR(FORMAT),##args)

#define ANNOUNCE_BOOT 1    //adds about 600 bytes to program size
#if ANNOUNCE_BOOT
#define PRINTA(FORMAT,args...) printf_P(PSTR(FORMAT),##args)
#else
#define PRINTA(...)
#endif

#define DEBUG 0
#if DEBUG
#define PRINTD(FORMAT,args...) printf_P(PSTR(FORMAT),##args)
#else
#define PRINTD(...)
#endif

#include <avr/pgmspace.h>
#include <avr/fuse.h>
#include <avr/eeprom.h>
#include <avr/wdt.h>
#include <stdio.h>
#include <string.h>
#include <dev/watchdog.h>
#include <util/delay.h>
#include "sys/etimer.h"
#include "sys/clock.h"
#include "smip/cone.h"
#include "smip/ctwo.h"

#include "loader/symbols-def.h"
#include "loader/symtab.h"

#include "params.h"
#include "radio/rf230bb/rf230bb.h"
#include "net/mac/frame802154.h"
#include "net/mac/framer-802154.h"
#include "net/sicslowpan.h"

#include "contiki.h"
#include "contiki-net.h"
#include "contiki-lib.h"

#include "lib/mmem.h"

#include "dev/rs232.h"
#include "dev/serial-line.h"
#include "dev/slip.h"

#ifdef RAVEN_LCD_INTERFACE
#include "raven-lcd.h"
#endif

#if AVR_WEBSERVER
#include "httpd-fs.h"
#include "httpd-cgi.h"
#endif

#ifdef COFFEE_FILES
#include "cfs/cfs.h"
#include "cfs/cfs-coffee.h"
#endif

#if UIP_CONF_ROUTER&&0
#include "net/routing/rimeroute.h"
#include "net/rime/rime-udp.h"
#endif

#include "net/rime.h"

/* Track interrupt flow through mac, rdc and radio driver */
//#define DEBUGFLOWSIZE 32
#if DEBUGFLOWSIZE
uint8_t debugflowsize,debugflow[DEBUGFLOWSIZE];
#define DEBUGFLOW(c) if (debugflowsize<(DEBUGFLOWSIZE-1)) debugflow[debugflowsize++]=c
#else
#define DEBUGFLOW(c)
#endif

/* Get periodic prints from idle loop, from clock seconds or rtimer interrupts */
/* Use of rtimer will conflict with other rtimer interrupts such as contikimac radio cycling */
/* STAMPS will print ENERGEST outputs if that is enabled. */
#define PERIODICPRINTS 0
#if PERIODICPRINTS
//#define PINGS 64
#define ROUTES 600
#define STAMPS 60
#define STACKMONITOR 1024
uint32_t clocktime;
#define TESTRTIMER 0
#if TESTRTIMER
uint8_t rtimerflag=1;
struct rtimer rt;
void rtimercycle(void) {rtimerflag=1;}
#endif
#endif

uint16_t ledtimer;

volatile uint8_t sleepen,counter;

/*-------------------------------------------------------------------------*/
/*----------------------Configuration of the .elf file---------------------*/
#if 1
/* The proper way to set the signature is */
#include <avr/signature.h>
#else
/* Older avr-gcc's may not define the needed SIGNATURE bytes. Do it manually if you get an error */
typedef struct {const unsigned char B2;const unsigned char B1;const unsigned char B0;} __signature_t;
#define SIGNATURE __signature_t __signature __attribute__((section (".signature")))
SIGNATURE = {
  .B2 = 0x01,//SIGNATURE_2, //ATMEGA128rfa1
  .B1 = 0xA7,//SIGNATURE_1, //128KB flash
  .B0 = 0x1E,//SIGNATURE_0, //Atmel
};
#endif

#if 1
/* JTAG, SPI enabled, Internal RC osc, Boot flash size 4K, 6CK+65msec delay, brownout disabled */
FUSES ={.low = 0xe2, .high = 0x99, .extended = 0xff,};
#else
/* JTAG+SPI, Boot 4096 words @ $F000, Internal  oscillator, startup 6 CK +0 ms, Brownout 1.8 volts */
FUSES ={.low = 0xC2, .high = 0x99, .extended = 0xfe,};
#endif

uint8_t
rng_get_uint8(void) {
#if 1
	uint8_t i = 0;
	uint8_t j = 0;
	for(; i < 4; i ++){
		j = j * 4;
		j += ((PHY_RSSI&0x60)>>5);
		_delay_ms(2);
	}

#elif 1
  /* Upper two RSSI reg bits (RND_VALUE) are random in rf231 */
  uint8_t j;
  j = (PHY_RSSI&0xc0) + ((PHY_RSSI>>2)&0x30) + ((PHY_RSSI>>4)&0x0c) + ((PHY_RSSI>>6)&0x03);
#else
/* Get a pseudo random number using the ADC */
  uint8_t i,j;
  ADCSRA=1<<ADEN;             //Enable ADC, not free running, interrupt disabled, fastest clock
  for (i=0;i<4;i++) {
    ADMUX = 0;                //toggle reference to increase noise
    ADMUX =0x1E;              //Select AREF as reference, measure 1.1 volt bandgap reference.
    ADCSRA|=1<<ADSC;          //Start conversion
    while (ADCSRA&(1<<ADSC)); //Wait till done
	j = (j<<2) + ADC;
  }
  ADCSRA=0;                   //Disable ADC
#endif
  PRINTD("rng issues %d\n",j);
  return j;
}



void timerinit(void)
{
	 /* Clock from crystal on TOSC0-1 */
	  //ASSR = _BV(AS2);

	  /* Set counter to zero */
	  TCNT5 = 0;

	  /*						  \
	   * Set comparison register: \
	   * Crystal freq. is 32768,\
	   * pre-scale factor is 8, we want CLOCK_CONF_SECOND ticks / sec: \
	   * 32768 = 8 * CLOCK_CONF_SECOND * OCR2A, less 1 for CTC mode\
	   */ \
	   OCR5A = 32768/8/CLOCK_CONF_SECOND - 1;

	   //OCR5A = 65000;
	   //OCR5A=F_CPU/256UL/CLOCK_CONF_SECOND;

	  /* 								\
	   * Set timer control register: 	\
	   *  - prescale: 8 (CS21) \
	   *  - counter reset via comparison register (WGM21) \
	   */
	  TCCR5A = _BV(WGM51);
	  TCCR5B = _BV(CS52) | _BV(CS50);

	  /* Clear interrupt flag register */
	  TIFR5 = 0x00;

	  /* \
	   * Raise interrupt when value in OCR2 is reached. Note that the \
	   * counter value in TCNT2 is cleared automatically. \
	   */
	  TIMSK5 = _BV (OCIE5A);

}


void includeip(void)
{
	  /* Start radio and radio receive process */
	  //NETSTACK_RADIO.init();

	/* Get a random seed for the 802.15.4 packet sequence number.
	 * Some layers will ignore duplicates found in a history (e.g. Contikimac)
	 * causing the initial packets to be ignored after a short-cycle restart.
	 */


	  /* Set addresses BEFORE starting tcpip process */

	//  rimeaddr_t addr;
	//
	//  if (params_get_eui64(addr.u8)) {
	//      PRINTA("Random EUI64 address generated\n");
	//  }
	//
	//#if UIP_CONF_IPV6
	//  memcpy(&uip_lladdr.addr, &addr.u8, sizeof(rimeaddr_t));
	//#elif WITH_NODE_ID
	//  node_id=get_panaddr_from_eeprom();
	//  addr.u8[1]=node_id&0xff;
	//  addr.u8[0]=(node_id&0xff00)>>8;
	//  PRINTA("Node ID from eeprom: %X\n",node_id);
	//#endif
	  //rimeaddr_set_node_addr(&addr);

	  //rf230_set_pan_addr(params_get_panid(),params_get_panaddr(),(uint8_t *)&addr.u8);
	  //rf230_set_channel(params_get_channel());
	  //rf230_set_txpower(params_get_txpower());

	//#if UIP_CONF_IPV6
	//  PRINTA("EUI-64 MAC: %x-%x-%x-%x-%x-%x-%x-%x\n",addr.u8[0],addr.u8[1],addr.u8[2],addr.u8[3],addr.u8[4],addr.u8[5],addr.u8[6],addr.u8[7]);
	//#else
	//  PRINTA("MAC address ");
	//  uint8_t i;
	//  for (i=sizeof(rimeaddr_t); i>0; i--){
	//    PRINTA("%x:",addr.u8[i-1]);
	//  }
	//  PRINTA("\n");
	//#endif
	//  /* Initialize stack protocols */
	//  queuebuf_init();
	//  NETSTACK_RDC.init();
	//  NETSTACK_MAC.init();
	//  NETSTACK_NETWORK.init();
	//
	//#if ANNOUNCE_BOOT
	//  PRINTA("SmartMesh-Contiki \n");
	//  PRINTA("%s %s, channel %u , check rate %u Hz tx power %u\n",NETSTACK_MAC.name, NETSTACK_RDC.name, rf230_get_channel(),
	//    CLOCK_SECOND / (NETSTACK_RDC.channel_check_interval() == 0 ? 1:NETSTACK_RDC.channel_check_interval()),
	//    rf230_get_txpower());
	//#if UIP_CONF_IPV6_RPL
	//  PRINTA("RPL Enabled\n");
	//#endif
	//#if UIP_CONF_ROUTER
	//  PRINTA("Routing Enabled\n");
	//#endif
	//
	//#endif /* ANNOUNCE_BOOT */
	//
	//  process_start(&tcpip_process, NULL);
	//
	//#ifdef RAVEN_LCD_INTERFACE
	//  process_start(&raven_lcd_process, NULL);
	//#endif
}

/*-------------------------Low level initialization------------------------*/
/*------Done in a subroutine to keep main routine stack usage small--------*/
void initialize(void)
{

// This define
#if 1
	uint8_t resetSrc = MCUSR;
	MCUSR = 0;

	watchdog_init();
	watchdog_start();



#if WITH_SLIP
//Slip border router on uart0
rs232_init(RS232_PORT_0, USART_BAUD_38400,USART_PARITY_NONE | USART_STOP_BITS_1 | USART_DATA_BITS_8);
#else
/* First rs232 port for debugging */
rs232_init(RS232_PORT_0, USART_BAUD_57600,USART_PARITY_NONE | USART_STOP_BITS_1 | USART_DATA_BITS_8);

/* Redirect stdout to first port */
rs232_redirect_stdout(RS232_PORT_0);

/* Get input from first port */
rs232_set_input(RS232_PORT_0, serial_line_input_byte);
#endif

clock_init();

#if ANNOUNCE_BOOT
PRINTA("\n\n*******Booting %s*******\n",CONTIKI_VERSION_STRING);
#endif
if(resetSrc & (1<<PORF )) PRINTA("Power-on reset.\n");
if(resetSrc & (1<<EXTRF)) PRINTA("External reset!\n");
if(resetSrc & (1<<BORF )) PRINTA("Brownout reset!\n");
if(resetSrc & (1<<WDRF )) PRINTA("Watchdog reset!\n");
if(resetSrc & (1<<JTRF )) PRINTA("JTAG reset!\n");

#else
// Default old contiki implementation

//watchdog_init();
//watchdog_start();
/* The Raven implements a serial command and data interface via uart0 to a 3290p,
 * which could be duplicated using another host computer.
 */
#if !RF230BB_CONF_LEDONPORTE1   //Conflicts with USART0
#ifdef RAVEN_LCD_INTERFACE
  rs232_init(RS232_PORT_0, USART_BAUD_38400,USART_PARITY_NONE | USART_STOP_BITS_1 | USART_DATA_BITS_8);
  rs232_set_input(0,raven_lcd_serial_input);
#else
  /* Generic or slip connection on uart0 */
  rs232_init(RS232_PORT_0, USART_BAUD_38400,USART_PARITY_NONE | USART_STOP_BITS_1 | USART_DATA_BITS_8);
#endif
#endif

  /* Second rs232 port for debugging or slip alternative */
  rs232_init(RS232_PORT_1, USART_BAUD_57600,USART_PARITY_NONE | USART_STOP_BITS_1 | USART_DATA_BITS_8);
  /* Redirect stdout */
#if RF230BB_CONF_LEDONPORTE1 || defined(RAVEN_LCD_INTERFACE)
  rs232_redirect_stdout(RS232_PORT_1);
#else
  rs232_redirect_stdout(RS232_PORT_0);
#endif
  clock_init();

  if(MCUSR & (1<<PORF )) PRINTD("Power-on reset.\n");
  if(MCUSR & (1<<EXTRF)) PRINTD("External reset!\n");
  if(MCUSR & (1<<BORF )) PRINTD("Brownout reset!\n");
  if(MCUSR & (1<<WDRF )) PRINTD("Watchdog reset!\n");
  if(MCUSR & (1<<JTRF )) PRINTD("JTAG reset!\n");
#endif



#if STACKMONITOR
  /* Simple stack pointer highwater monitor. Checks for magic numbers in the main
   * loop. In conjuction with PERIODICPRINTS, never-used stack will be printed
   * every STACKMONITOR seconds.
   */
{
extern uint16_t __bss_end;
uint16_t p=(uint16_t)&__bss_end;
    do {
      *(uint16_t *)p = 0x4242;
      p+=10;
    } while (p<SP-10); //don't overwrite our own stack
}
#endif

#define CONF_CALIBRATE_OSCCAL 0
#if CONF_CALIBRATE_OSCCAL
void calibrate_rc_osc_32k();
{
extern uint8_t osccal_calibrated;
uint8_t i;
  PRINTD("\nBefore calibration OSCCAL=%x\n",OSCCAL);
  for (i=0;i<10;i++) { 
    calibrate_rc_osc_32k();  
    PRINTD("Calibrated=%x\n",osccal_calibrated);
//#include <util/delay_basic.h>
//#define delay_us( us )   ( _delay_loop_2(1+(us*F_CPU)/4000000UL) ) 
//   delay_us(50000);
 }
   clock_init();
}
#endif 

  PRINTA("\n*******Booting %s*******\n",CONTIKI_VERSION_STRING);

/* rtimers needed for radio cycling */
  //rtimer_init();

 /* Initialize process subsystem */
  process_init();


  //wdt_disable();

  mmem_init();

  /* etimers must be started before ctimer_init */
  process_start(&etimer_process, NULL);

  //process_start(&C_One_process,NULL);
 // process_start(&C_Two_process,NULL);

  ctimer_init();

  random_init(rng_get_uint8());

  /* Autostart other processes */
  autostart_start(autostart_processes);

  /*---If using coffee file system create initial web content if necessary---*/
#if COFFEE_FILES
  int fa = cfs_open( "/index.html", CFS_READ);
  if (fa<0) {     //Make some default web content
    PRINTA("No index.html file found, creating upload.html!\n");
    PRINTA("Formatting FLASH file system for coffee...");
    cfs_coffee_format();
    PRINTA("Done!\n");
    fa = cfs_open( "/index.html", CFS_WRITE);
    int r = cfs_write(fa, &"It works!", 9);
    if (r<0) PRINTA("Can''t create /index.html!\n");
    cfs_close(fa);
//  fa = cfs_open("upload.html"), CFW_WRITE);
// <html><body><form action="upload.html" enctype="multipart/form-data" method="post"><input name="userfile" type="file" size="50" /><input value="Upload" type="submit" /></form></body></html>
  }
#endif /* COFFEE_FILES */

/* Add addresses for testing */
#if 0
{  
  uip_ip6addr_t ipaddr;
  uip_ip6addr(&ipaddr, 0xaaaa, 0, 0, 0, 0, 0, 0, 0);
  uip_ds6_addr_add(&ipaddr, 0, ADDR_AUTOCONF);
//  uip_ds6_prefix_add(&ipaddr,64,0);
}
#endif

/*--------------------------Announce the configuration---------------------*/
#if ANNOUNCE_BOOT
#if AVR_WEBSERVER
{ uint8_t i;
  char buf[80];
  unsigned int size;

  for (i=0;i<UIP_DS6_ADDR_NB;i++) {
	if (uip_ds6_if.addr_list[i].isused) {	  
	   httpd_cgi_sprint_ip6(uip_ds6_if.addr_list[i].ipaddr,buf);
       PRINTA("IPv6 Address: %s\n",buf);
	}
  }
   cli();
   eeprom_read_block (buf,eemem_server_name, sizeof(eemem_server_name));
   sei();
   buf[sizeof(eemem_server_name)]=0;
   PRINTA("%s",buf);
   cli();
   eeprom_read_block (buf,eemem_domain_name, sizeof(eemem_domain_name));
   sei();
   buf[sizeof(eemem_domain_name)]=0;
   size=httpd_fs_get_size();
#ifndef COFFEE_FILES
   PRINTA(".%s online with fixed %u byte web content\n",buf,size);
#elif COFFEE_FILES==1
   PRINTA(".%s online with static %u byte EEPROM file system\n",buf,size);
#elif COFFEE_FILES==2
   PRINTA(".%s online with dynamic %u KB EEPROM file system\n",buf,size>>10);
#elif COFFEE_FILES==3
   PRINTA(".%s online with static %u byte program memory file system\n",buf,size);
#elif COFFEE_FILES==4
   PRINTA(".%s online with dynamic %u KB program memory file system\n",buf,size>>10);
#endif /* COFFEE_FILES */
}
#else
   PRINTA("Online\n");
#endif
#endif /* ANNOUNCE_BOOT */

#if RF230BB_CONF_LEDONPORTE1
  /* NB: PORTE1 conflicts with UART0 */
  DDRE|=(1<<DDE1);  //set led pin to output (Micheal Hatrtman board)
  PORTE&=~(1<<PE1); //and low to turn led off
#endif
}

#if ROUTES && UIP_CONF_IPV6
static void
ipaddr_add(const uip_ipaddr_t *addr)
{
  uint16_t a;
  int8_t i, f;
  for(i = 0, f = 0; i < sizeof(uip_ipaddr_t); i += 2) {
    a = (addr->u8[i] << 8) + addr->u8[i + 1];
    if(a == 0 && f >= 0) {
      if(f++ == 0) PRINTF("::");
    } else {
      if(f > 0) {
        f = -1;
      } else if(i > 0) {
        PRINTF(":");
      }
      PRINTF("%x",a);
    }
  }
}
#endif

/*-------------------------------------------------------------------------*/
/*------------------------- Main Scheduler loop----------------------------*/
/*-------------------------------------------------------------------------*/
int main(void)
{

						initialize();
						sei();

						 	 DDRE = 0xff;
						 	 PORTE = 0xff;
						 	 DDRB = 0xff;
						 	 PORTB = 0xfd;
						 	 DDRF = 0xff ;
						 	 PORTF = 0xff ;
						 	 DDRD = 0xff ;
						 	 PORTD = 0xff ;
						 	 DDRD = 0x7f ;
						 	 PORTD = 0x1f ;



						while(1) {
						//PRINTF("sleep time %u",getsleeptime());
//						if(getsleeptime()>1)
//						{
//								enterSleep();
//						}
//						else
//						{
								//PORTE ^= 0x80;
								process_run();
								//PORTE ^= 0x80;
						//}
						watchdog_periodic();

						}
						return 0;
}

/*---------------------------------------------------------------------------*/

void log_message(char *m1, char *m2)
{
  PRINTF("%s%s\n", m1, m2);
}

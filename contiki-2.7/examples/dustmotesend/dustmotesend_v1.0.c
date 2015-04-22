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
#include <avr/io.h>
#include "lib/dn_uart.h"
#include "lib/dn_serial_mt.h"
#include "lib/dn_ipmt.h"
#include <dev/watchdog.h>
#include "dev/rs232.h"
#include "lib/dn_common.h"

#include <stdio.h> /* For printf() */

#define IPv6ADDR_LEN              16

// mote state
#define MOTE_STATE_IDLE           0x01
#define MOTE_STATE_SEARCHING      0x02
#define MOTE_STATE_NEGOCIATING    0x03
#define MOTE_STATE_CONNECTED      0x04
#define MOTE_STATE_OPERATIONAL    0x05

typedef void (*fsm_reply_callback)(void);
void fsm_setCallback(fsm_reply_callback cb);

typedef struct {


	fsm_reply_callback   replyCb;
   // module
   dn_uart_rxByte_cbt   ipmt_uart_rxByte_cb;
   // api
   uint8_t              socketId;                          // ID of the mote's UDP socket
   uint16_t             srcPort;                           // UDP source port
   uint8_t              destAddr[IPv6ADDR_LEN];            // IPv6 destination address
   uint16_t             destPort;                          // UDP destination port

   uint8_t              replyBuf[MAX_FRAME_LENGTH];        // holds notifications from ipmt
   uint8_t              notifBuf[MAX_FRAME_LENGTH];        // notifications buffer internal to ipmt
} app_vars_t;

app_vars_t              app_vars;



//dn_serial_request_cbt requestCbSerial;
//dn_serial_reply_cbt  replyCbSerial;

//dn_ipmt_notif_cbt notifCbmt;
//dn_ipmt_reply_cbt replyCbmt;
uint8_t* notifBuf;
uint8_t notifBufLen;

dn_ipmt_getParameter_macAddress_rpt* replymac;
dn_ipmt_getParameter_moteInfo_rpt* replyinfo;
dn_ipmt_openSocket_rpt* replyopen;
dn_ipmt_join_rpt* replyjoin;
dn_ipmt_getParameter_networkId_rpt* replynwid;
dn_ipmt_getParameter_moteStatus_rpt* replymotestatus;
dn_ipmt_getParameter_txPower_rpt* replytxpower;
dn_ipmt_bindSocket_rpt* replybind;
dn_ipmt_getParameter_netInfo_rpt* replynetinfo;
dn_ipmt_requestService_rpt* replyservice;
dn_ipmt_sendTo_rpt* sendtoreply;

int readdata(unsigned char c)
{
	printf("rxd %d\n",c);
	app_vars.ipmt_uart_rxByte_cb((uint8_t)c);
	return c;
}


extern void dn_uart_init(dn_uart_rxByte_cbt rxByte_cb) {
   // remember function to call back
   app_vars.ipmt_uart_rxByte_cb = rxByte_cb;

   rs232_init(RS232_PORT_1, USART_BAUD_115200,USART_PARITY_NONE | USART_STOP_BITS_1 | USART_DATA_BITS_8);

   rs232_set_input(RS232_PORT_1,readdata);

}

extern void dn_uart_txByte(uint8_t byte) {
   // write to the serial 1 port on the Arduino Due
	rs232_send(RS232_PORT_1,byte);
}

void fsm_setCallback(fsm_reply_callback cb) {
   app_vars.replyCb     = cb;
}




//===== getMoteStatus//

void api_getMoteStatus_reply() {
   dn_ipmt_getParameter_moteStatus_rpt* reply;

   printf("INFO:     api_getMoteStatus_reply");

   reply = (dn_ipmt_getParameter_moteStatus_rpt*)app_vars.replyBuf;

   printf("\nINFO:     state=\n");
   printf("\nReply:%d\n",reply->state);

   switch (reply->state) {
      case MOTE_STATE_IDLE:
    	  printf("\nMote Idle\n");
         break;
      case MOTE_STATE_OPERATIONAL:
         // the API currently does not allow to find out what the open sockets are
         //app_vars.socketId = DEFAULT_SOCKETID;

    	  printf("\nMote Operational\n");
         break;
      default:

         break;
      }
}


void api_getMoteStatus(void) {
   dn_err_t err;
   // log
   printf("\n");
   printf("\nINFO:     api_getMoteStatus... returns \n");

   // arm callback
   fsm_setCallback(api_getMoteStatus_reply);

   // issue function
   err = dn_ipmt_getParameter_moteStatus(
      (dn_ipmt_getParameter_moteStatus_rpt*)(app_vars.replyBuf)
   );

   // log
   printf("Error Code is:%d\n",err);
}






void notifCbmt(uint8_t cmdId, uint8_t subCmdId) {

   dn_ipmt_events_nt* dn_ipmt_events_notif;

   switch (cmdId) {
      case CMDID_EVENTS:

         printf("\n");
         printf("\nINFO:     notif CMDID_EVENTS\n");

         dn_ipmt_events_notif = (dn_ipmt_events_nt*)app_vars.notifBuf;

         printf("\nINFO:     state=\n");
         printf("%d\n",dn_ipmt_events_notif->state);

         switch (dn_ipmt_events_notif->state) {
            case MOTE_STATE_IDLE:
            	printf("%d\n",dn_ipmt_events_notif->state);
               break;
            case MOTE_STATE_OPERATIONAL:
            	printf("%d\n",dn_ipmt_events_notif->state);
               break;
            default:
               // nothing to do
               break;
         }
         break;
      default:
         // nothing to do
         break;
   }
}




//void notifCbmt(uint8_t cmdId,uint8_t subCmdId)
//{
//	printf("\nCommand ID is:%d\n",cmdId);
//	printf("Sub Command ID is:%d\n",subCmdId);
//}

void replyCbmt(uint8_t rcmdId)
{
	//printf("\nReply Command Id is:%u\n",atoi(rcmdId));
}


void moteinit()
{
	// reset local variables
	memset(&app_vars,    0, sizeof(app_vars));


	app_vars.srcPort=60000;
	app_vars.destPort=61000;
	memcpy(app_vars.destAddr,(uint8_t*)ipv6Addr_manager,IPv6ADDR_LEN);

	dn_ipmt_init(notifCbmt,app_vars.notifBuf,sizeof(app_vars.notifBuf),replyCbmt);

}

/*---------------------------------------------------------------------------*/
PROCESS(hello_world_process, "Hello world process");
AUTOSTART_PROCESSES(&hello_world_process);
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(hello_world_process, ev, data)
{



	PROCESS_BEGIN();

  	static struct etimer timer;

  	static uint8_t i;

  	DDRB=0x02;
  	DDRD=0x60;

  	PORTB=0x02;
  	PORTD=0x60;


  	moteinit();

  	watchdog_stop();

//  	dn_ipmt_getParameter_macAddress(replymac);
//    clock_delay_msec(1000);
//    printf("reply is: %d",replymac->RC);
//  	printf("\nMAC address is:");
//  	for(i=0;i<8;i++)
//  	printf(":%x:",replymac->macAddress[i]);
//    clock_delay_msec(1000);
  	api_getMoteStatus();


//  	dn_ipmt_getParameter_moteStatus(replymotestatus);
//  	clock_delay_msec(1500);
//  	printf("Mote Status is:%d\n",replymotestatus->state);
//  	clock_delay_msec(1000);

//	dn_ipmt_openSocket(0,replyopen);
//	clock_delay_msec(1000);
//	printf("\n Socket ID is:%d-%d\n",replyopen->RC,replyopen->socketId);
//	socketid=replyopen->socketId;
//  	clock_delay_msec(1000);

//  	socketid=22;
//  	srcport=0xea60;
//  	dstport=0xf0b0;
//    dn_ipmt_bindSocket(socketid,srcport,replybind);
//    clock_delay_msec(1000);
//    printf("\nsocket creation is %d\n",replybind->RC);
//    clock_delay_msec(1000);
//
//    dn_ipmt_join(replyjoin);
//    clock_delay_msec(5000);
//    printf("\njoin reply is:%d\n",replyjoin->RC);
//    dn_ipmt_getParameter_moteStatus(replymotestatus);
//    clock_delay_msec(2000);
//    printf("Mote Status is:%d\n",replymotestatus->state);
//    clock_delay_msec(2000);
//
//
//
//	dn_ipmt_requestService(65534,0,2000,replyservice);
//	clock_delay_msec(5000);
//	printf("\nservice response is:%d\n",replyservice->RC);

	watchdog_start();


  	etimer_set(&timer,CLOCK_CONF_SECOND*5);
  	while(1)
  	{
	  PROCESS_WAIT_EVENT();


	  api_getMoteStatus();


	  //res=readdata(value);
	  //printf("data is:%d,%d\n",value,res);
	  PORTB ^= 0x02;
	  PORTD ^= 0x60;

	  //dn_uart_txByte('h');

	  /*Get the mote status*/
//	  dn_ipmt_getParameter_netInfo(replynetinfo);
//	  clock_delay_msec(1000);
//	  printf("\nNetInfo %u-%u-%u-%u\n",replynetinfo->RC,replynetinfo->moteId,replynetinfo->networkId,replynetinfo->slotSize);
//
//	  printf("\nMAC address is:");
//	  for(i=0;i<8;i++)
//	  printf(":%x:",replynetinfo->macAddress[i]);

//
//	  payload[0]=200;
//	  payload[1]=12;
//	  dn_ipmt_sendTo(socketid,(uint8_t*)ipv6Addr_manager,dstport, 0, 0,count++,payload,sizeof(payload),sendtoreply);//
//	  clock_delay_msec(1000);//
//	  printf("\nsendto reply is:%u\n",sendtoreply->RC);


	  //etimer_set(&timer,CLOCK_CONF_SECOND*5);
	  etimer_reset(&timer);
  }
  PROCESS_END();
}
/*---------------------------------------------------------------------------*/

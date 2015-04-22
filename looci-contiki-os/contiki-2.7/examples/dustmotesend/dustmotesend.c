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
#include <dev/watchdog.h>
#include "dev/rs232.h"
#include "contiki-smip.h"
//#include "smip/dn_common.h"
//#include "smip/dn_uart.h"
//#include "smip/dn_serial_mt.h"
//#include "smip/dn_ipmt.h"


#include <stdio.h> /* For printf() */



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
   uint8_t				readytosend;
} app_vars_t;

app_vars_t              app_vars;




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
	//printf("rxd %d\n",c);
	app_vars.ipmt_uart_rxByte_cb((uint8_t)c);
	return c;
}


extern void dn_uart_init(dn_uart_rxByte_cbt rxByte_cb) {
   // remember function to call back
   app_vars.ipmt_uart_rxByte_cb = rxByte_cb;

   rs232_init(RS232_PORT_1, USART_BAUD_115200,USART_PARITY_NONE | USART_STOP_BITS_1 | USART_DATA_BITS_8);

   rs232_set_input(RS232_PORT_1,readdata);

   printf("\n[SMARTDUST] Initializing UART\n");

}

extern void dn_uart_txByte(uint8_t byte) {
   // write to the serial 1 port on the Arduino Due
	rs232_send(RS232_PORT_1,byte);
}

void fsm_setCallback(fsm_reply_callback cb) {
   app_vars.replyCb     = cb;

}


void printByteArray(uint8_t* payload, uint8_t length) {
   uint8_t i;

   //printf("\n ");
   for (i=0;i<length;i++) {
      printf("%x",payload[i]);
      if (i<length-1) {
         printf("-");
      }
   }
}

//===== bindSocket

void api_bindSocket_reply() {
   dn_ipmt_bindSocket_rpt* reply;





   printf("INFO:     api_bindSocket_reply");

   reply = (dn_ipmt_bindSocket_rpt*)app_vars.replyBuf;

   printf("INFO:     RC=");
   printf("%d\n",reply->RC);


}

void api_bindSocket(void) {
   dn_err_t err;



   // log
   printf("");
   printf("INFO:     api_bindSocket... returns \n");

   // arm callback
   fsm_setCallback(api_bindSocket_reply);

   // issue function
   err = dn_ipmt_bindSocket(
      app_vars.socketId,                              // socketId
      app_vars.srcPort,                               // port
      (dn_ipmt_bindSocket_rpt*)(app_vars.replyBuf)    // reply
   );

   // log
   printf("\nError:%d\n",err);


}



//===== join
void api_join_reply() {
   dn_ipmt_join_rpt* reply;

   printf("INFO:     api_join_reply\n");

   reply = (dn_ipmt_join_rpt*)app_vars.replyBuf;

   printf("INFO:     RC=");
   printf("%d\n",reply->RC);
}



void api_join(void) {
   dn_err_t err;



   // log
   printf("\n");
   printf("\nINFO:     api_join... returns\n ");

   // arm callback
   fsm_setCallback(api_join_reply);

   // issue function
   err = dn_ipmt_join(
      (dn_ipmt_join_rpt*)(app_vars.replyBuf)     // reply
   );

   // log
   printf("\nError:%d\n",err);


}



//===== getServiceInfo

void api_getServiceInfo_reply() {
   dn_ipmt_getServiceInfo_rpt* reply;

   printf("\nINFO:     api_getServiceInfo_reply\n");

   reply = (dn_ipmt_getServiceInfo_rpt*)app_vars.replyBuf;

   printf("\nINFO:     RC=");
   printf("%d\n",reply->RC);

   printf("\nINFO:     value=");
   printf("%d\n",reply->value);

//   // schedule next event
//   if (reply->RC!=0 || reply->value>app_vars.dataPeriod) {
//      fsm_scheduleEvent(CMD_PERIOD, &IpMtWrapper::api_requestService);
//   } else {
//      fsm_scheduleEvent(CMD_PERIOD, &IpMtWrapper::api_sendTo);
//   }
}


void api_getServiceInfo(void) {
	dn_err_t err;


   // log
   printf("\n");
   printf("\nINFO:     api_getServiceInfo... returns \n");

   // arm callback
   fsm_setCallback(api_getServiceInfo_reply);

   // issue function
   err = dn_ipmt_getServiceInfo(
      0xfffe,                                              // destAddr (0xfffe==manager)
      SERVICE_TYPE_BW,                                     // type
      (dn_ipmt_getServiceInfo_rpt*)(app_vars.replyBuf)     // reply
   );

   // log
   printf("\nError:%d\n",err);


}



//===== requestService

void api_requestService_reply() {
   dn_ipmt_requestService_rpt* reply;





   printf("\nINFO:     api_requestService_reply\n");

   reply = (dn_ipmt_requestService_rpt*)app_vars.replyBuf;

   printf("\nINFO:     RC=");
   printf("%d\n",reply->RC);
}



void api_requestService(void) {
   dn_err_t err;



   // log
   printf("\n");
   printf("\nINFO:     api_requestService... returns\n ");

   // arm callback
   fsm_setCallback(api_requestService_reply);

   // issue function
   err = dn_ipmt_requestService(
      0xfffe,                                              // destAddr (0xfffe==manager)
      SERVICE_TYPE_BW,                                     // serviceType
      5000,                                 // value
      (dn_ipmt_requestService_rpt*)(app_vars.replyBuf)     // reply
   );

   // log
   printf("\nError:%d\n",err);


}



//===== sendTo

void api_sendTo_reply() {
   dn_ipmt_sendTo_rpt* reply;

   printf("\nINFO:     api_sendTo_reply\n");

   reply = (dn_ipmt_sendTo_rpt*)app_vars.replyBuf;

   printf("\nINFO:     RC=");
   printf("%d\n",reply->RC);

}


void api_sendTo(void) {
   dn_err_t err;
   uint16_t dataVal;
   uint8_t  payload[2];
   uint8_t  lenWritten;
   static uint8_t i=0;



   // log
   printf("\n");
   printf("INFO:     api_sendTo... returns ");

   // arm callback
   fsm_setCallback(api_sendTo_reply);

   // create payload
//   app_vars.dataGenerator(&dataVal);
   	 dataVal=20000;
   	 dn_write_uint16_t(payload, dataVal);

   // issue function
   err = dn_ipmt_sendTo(
      app_vars.socketId,                                   // socketId
      app_vars.destAddr,                                   // destIP
      app_vars.destPort,                                   // destPort
      SERVICE_TYPE_BW,                                     // serviceType
      0,                                                   // priority
      0xffff,                                              // packetId
      payload,                                             // payload
      sizeof(payload),                                     // payloadLen
      (dn_ipmt_sendTo_rpt*)(app_vars.replyBuf)             // reply
   );

   // log
   printf("\nError:%d\n",err);

   printf("\nINFO:     sending value: ");
   printf("%d\n",dataVal);


}













//===== openSocket

void api_openSocket_reply() {
   dn_ipmt_openSocket_rpt* reply;

   printf("\nINFO:     api_openSocket_reply\n");

   reply = (dn_ipmt_openSocket_rpt*)app_vars.replyBuf;

   printf("INFO:     socketId=");
   printf("%d",reply->socketId);

   // store the socketID
   app_vars.socketId = reply->socketId;
}





void api_openSocket(void) {
   dn_err_t err;

   // log
   printf("\n");
   printf("\nINFO:     api_openSocket... returns \n");

   // arm callback
   fsm_setCallback(api_openSocket_reply);

   // issue function
   err = dn_ipmt_openSocket(
      0,                                              // protocol
      (dn_ipmt_openSocket_rpt*)(app_vars.replyBuf)    // reply
   );

   // log
   printf("\nError code is:%d\n",err);
}






//===== getMoteStatus//

void api_getMoteStatus_reply() {
   dn_ipmt_getParameter_moteStatus_rpt* reply;

   printf("INFO:     api_getMoteStatus_reply");

   reply = (dn_ipmt_getParameter_moteStatus_rpt*)app_vars.replyBuf;

   printf("\nINFO:     state=");
   printf("%d\n",reply->state);

   switch (reply->state) {
      case MOTE_STATE_IDLE:
    	  printf("\nMote Idle\n");
    	  clock_delay_msec(500);
    	  api_getMoteStatus();

         break;
      case MOTE_STATE_OPERATIONAL:
         // the API currently does not allow to find out what the open sockets are
    	  app_vars.socketId = DEFAULT_SOCKETID;

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

void api_getAutoJoinStatus_reply(void){

		dn_ipmt_getParameter_autoJoin_rpt* reply;

	   printf("INFO:     api_getAutoJoinStatus_reply");

	   reply = (dn_ipmt_getParameter_autoJoin_rpt*)app_vars.replyBuf;

	   printf("\nINFO:     state=");
	   printf("%d\n",reply->RC);
	   printf("\nAutoJoin Status:%d\n",reply->autoJoin);


}


void api_getAutoJoinStatus(void){
	dn_err_t err;
	   // log
	   printf("\n");
	   printf("\nINFO:     api_getAutoJoinStatus... returns \n");

	   // arm callback
	   fsm_setCallback(api_getAutoJoinStatus_reply);

	   // issue function
	   err = dn_ipmt_getParameter_autoJoin(
	      (dn_ipmt_getParameter_autoJoin_rpt*)(app_vars.replyBuf)
	   );

	   // log
	   printf("Error Code is:%d\n",err);


}








void notifCbmt(uint8_t cmdId, uint8_t subCmdId) {

   dn_ipmt_events_nt* dn_ipmt_events_notif;
   dn_ipmt_receive_nt* dn_ipmt_receive_notif;

   printf("\nNotification received %x %x",cmdId,subCmdId);

   switch (cmdId) {
      case CMDID_EVENTS:

         printf("\n");
         printf("\nINFO:     notif CMDID_EVENTS\n");

         dn_ipmt_events_notif = (dn_ipmt_events_nt*)app_vars.notifBuf;

         printf("\nINFO:     state=");
         printf("%d\n",dn_ipmt_events_notif->state);
         printf("%u\n",dn_ipmt_events_notif->events);

         switch (dn_ipmt_events_notif->state) {
            case MOTE_STATE_IDLE:
            	printf("State is:%d\n",dn_ipmt_events_notif->state);
            	clock_delay_msec(500);
            	    	  api_getMoteStatus();

               break;
            case MOTE_STATE_OPERATIONAL:


            	printf("State is: %d\n",dn_ipmt_events_notif->state);
               break;
            default:
               // nothing to do
               break;
         }
         break;
         case CMDID_RECEIVE:
        	 dn_ipmt_receive_notif=(dn_ipmt_receive_nt*)app_vars.notifBuf;
        	 printf("\n Data received from %x\n",dn_ipmt_receive_notif->socketId);
        	 printByteArray(dn_ipmt_receive_notif->srcAddr,sizeof(dn_ipmt_receive_notif->srcAddr));
        	 printf("\n");
        	 printByteArray(dn_ipmt_receive_notif->payload,dn_ipmt_receive_notif->payloadLen);
        	 break;
      default:
         // nothing to do
         break;
   }
}






void replyCbmt(uint8_t rcmdId)
{
	printf("\nReply Command Id is:%u\n",rcmdId);
	app_vars.replyCb();
}


void moteinit()
{
	// reset local variables
	memset(&app_vars,    0, sizeof(app_vars));

	printf("\n[SMARTDUST] Initializing Mote\n");
//	app_vars.readytosend=0;
//	app_vars.srcPort=60000;
//	app_vars.destPort=61000;
//	memcpy(app_vars.destAddr,(uint8_t*)ipv6Addr_manager,IPv6ADDR_LEN);

	clock_delay_msec(1000);

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

  	//watchdog_stop();
  	//api_getMoteStatus();
	//watchdog_start();

  	api_getMoteStatus();
  	clock_delay_msec(1000);
  	api_getAutoJoinStatus();
  	clock_delay_msec(1000);

  	api_join();
  	clock_delay_msec(1000);
  	api_openSocket();
  	clock_delay_msec(1000);
  	api_bindSocket();
  	clock_delay_msec(1000);


//  	api_getServiceInfo();
//  	clock_delay_msec(1000);
//  	api_requestService();
//  	clock_delay_msec(1000);



  	etimer_set(&timer,CLOCK_CONF_SECOND*5);
  	while(1)
  	{
	  PROCESS_WAIT_EVENT();

	  PORTB ^= 0x02;
	  PORTD ^= 0x60;

	  api_sendTo();

	  //etimer_set(&timer,CLOCK_CONF_SECOND*5);
	  etimer_reset(&timer);
  }
  PROCESS_END();
}
/*---------------------------------------------------------------------------*/


#include "contiki.h"
#include "contiki-smip.h"
#include "smartmesh.h"
#include "sys/clock.h"

#include "smip/cone.h"
#include "smip/ctwo.h"
#include "platform/avr-microduino64/tests/hello-world/hello-world.h"

#define LOOCIEN 0

#if LOOCIEN==1
#include "looci.h"
#include "networking_private.h"
#endif

#include "debug.h"

uint8_t nodeaddr[16]={0xfe,0x80,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x17,0x0d,0x00,0x00,0x60,0x1f,0x7f};
//uint8_t hostaddr[16]={0xbb,0xbb,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02};
//uint8_t bcastaddr[16]={0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff};

PROCESS(looci_smartmeship, "LooCI SmartMeshIP");
//AUTOSTART_PROCESSES(&looci_smartmeship);

static void api_timedout(void){

	// issue cancel command
	   dn_ipmt_cancelTx();

	   api_getMoteStatus();
}

static void test_transmit(void){

		//api_sendTo();


}

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


void notifCbmt(uint8_t cmdId, uint8_t subCmdId) {

   dn_ipmt_events_nt* dn_ipmt_events_notif;


   //printf("\nNotification received %x %x",cmdId,subCmdId);

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
            	printf("\nMote Idle\n");
            	//clock_delay_msec(500);
            	//api_getMoteStatus();
            	process_post(&looci_smartmeship,PC_MOTE_IDLE_EVENT,NULL);
               break;
            case MOTE_STATE_NEGOCIATING:
            	printf("\nMote Negotiating\n");
            	/*reseting to avoid the restart of entire procedure*/
            	ctimer_reset(&config_timeout);
            	break;
            case MOTE_STATE_OPERATIONAL:
            	printf("State is: %d\n",dn_ipmt_events_notif->state);
            	if(app_vars.currstate==0)
            	{
            	process_post(&looci_smartmeship,PC_MOTE_OPRL_EVENT,NULL);
            	app_vars.currstate=1;
            	}
            	printf("\nMote Operational\n");
            	break;
            default:
               // nothing to do
               break;
         }
         break;
         case CMDID_RECEIVE:
        	 dn_ipmt_receive_notif=(dn_ipmt_receive_nt*)app_vars.notifBuf;

        	 /*Uncomment the following lines to print the received payload*/
        	 //lc_printHexArray(dn_ipmt_receive_notif->srcAddr,sizeof(dn_ipmt_receive_notif->srcAddr));

        	 printf("\n");
        	 //lc_printHexArray(dn_ipmt_receive_notif->payload,dn_ipmt_receive_notif->payloadLen);
        	 printf("%d\n",dn_ipmt_receive_notif->payload[0]);
        	 //printf("\n");
        	 printf("Port is %u",dn_ipmt_receive_notif->srcPort);

        	 /* Copying received payload to global receive buffer! (aka. struct)*/
        	 memcpy(rxpayload.socketId,dn_ipmt_receive_notif->socketId,sizeof(uint8_t));
        	 memcpy(rxpayload.srcAddr,dn_ipmt_receive_notif->srcAddr,16);
        	 rxpayload.srcPort=dn_ipmt_receive_notif->srcPort;
        	 rxpayload.payloadLen=dn_ipmt_receive_notif->payloadLen;
        	 memcpy(rxpayload.payload,dn_ipmt_receive_notif->payload,dn_ipmt_receive_notif->payloadLen);

        	 /* Uncomment the following line to check the correctness of memcpy call */
        	 //lc_printHexArray(rxpayload.payload,rxpayload.payloadLen);
#if LOOCIEN==1
        	 /*Event is posted to LooCI networking framework (networking.c)*/
        	 process_post(&looci_networking_framework,PC_MOTE_RXD_EVENT,NULL);
#endif
        	 process_post(&hello_world_process,PROCESS_EVENT_CONTINUE,NULL);
        	 process_run();
        	 sleep=1;
        	 //enterSleep();
        	 break;
      default:
         // nothing to do
         break;
   }
}

void replyCbmt(uint8_t rcmdId)
{
	//printf("\nReply Command Id is:%u\n",rcmdId);
	app_vars.replyCb();
}


void moteinit(void)
{
	// reset local variables
	memset(&app_vars,    0, sizeof(app_vars));

	printf("\n[SMARTDUST] Initializing Mote\n");

	app_vars.srcPort=5555;
	//app_vars.destPort=61000;

	app_vars.destPort=5555;

	app_vars.dataPeriod=1000;

	app_vars.init=0;
	app_vars.currstate=0;

	memcpy(app_vars.destAddr,(uint8_t*)ipv6Addr_manager,IPv6ADDR_LEN);

	//memcpy(app_vars.destAddr,(uint8_t*)hostaddr,IPv6ADDR_LEN);

	dn_ipmt_init(notifCbmt,app_vars.notifBuf,sizeof(app_vars.notifBuf),replyCbmt);

	//clock_delay_msec(1000);

	api_getMoteStatus();

}


//===== getMoteStatus//

void api_getMoteStatus_reply(void) {
   dn_ipmt_getParameter_moteStatus_rpt* reply;

   //printf("INFO:     api_getMoteStatus_reply");


   ctimer_stop(&config_timeout);

   reply = (dn_ipmt_getParameter_moteStatus_rpt*)app_vars.replyBuf;

   //printf("\nINFO:     state=");
   //printf("%d\n",reply->state);

   switch (reply->state) {
      case MOTE_STATE_IDLE:
    	  printf("\nMote Idle\n");
    	  app_vars.currstate=0;
    	  /* Posting event to the process */
    	  //process_post_synch(&looci_smartmeship,PC_MOTE_IDLE_EVENT,NULL);
    	  process_post(&looci_smartmeship,PC_MOTE_IDLE_EVENT,NULL);

          break;
      case MOTE_STATE_OPERATIONAL:
         // the API currently does not allow to find out what the open sockets are
    	  app_vars.socketId = DEFAULT_SOCKETID;
    	  if(app_vars.currstate==0)
    	  {
    	  /* Posting event to the process */
    	  //process_post_synch(&looci_smartmeship,PC_MOTE_OPRL_EVENT,NULL);
    	  process_post(&looci_smartmeship,PC_MOTE_OPRL_EVENT,NULL);
    	  app_vars.currstate=1;
    	  }
    	  else
    	  {
    		  sleep=1;
    	  }
    	  printf("\nMote Operational\n");
         break;
      default:

         break;
      }
}


void api_getMoteStatus(void) {
   dn_err_t err;
   // log
   //printf("\n");
   //printf("\nINFO:     api_getMoteStatus... returns \n");

   // arm callback
   fsm_setCallback(api_getMoteStatus_reply);

   // issue function
   err = dn_ipmt_getParameter_moteStatus(
      (dn_ipmt_getParameter_moteStatus_rpt*)(app_vars.replyBuf)
   );

   // log
   //printf("Error Code is:%d\n",err);

   //Set callback timer
   ctimer_set(&config_timeout,API_TIMEOUT,api_timedout,NULL);

}


//===== join
void api_join_reply(void) {
   dn_ipmt_join_rpt* reply;

   //printf("INFO:     api_join_reply\n");

   ctimer_stop(&config_timeout);

   reply = (dn_ipmt_join_rpt*)app_vars.replyBuf;

   //printf("INFO:     RC=");
   //printf("%d\n",reply->RC);
}



void api_join(void) {
   dn_err_t err;



   // log
   //printf("\n");
   //printf("\nINFO:     api_join... returns\n ");

   // arm callback
   fsm_setCallback(api_join_reply);

   // issue function
   err = dn_ipmt_join(
      (dn_ipmt_join_rpt*)(app_vars.replyBuf)     // reply
   );

   // log
   //printf("\nError:%d\n",err);

   //Set callback timer
   ctimer_set(&config_timeout,JOIN_TIMEOUT,api_timedout,NULL);

}


//===== getServiceInfo

void api_getServiceInfo_reply(void) {
   dn_ipmt_getServiceInfo_rpt* reply;

   ctimer_stop(&config_timeout);

   //printf("\nINFO:     api_getServiceInfo_reply\n");

   reply = (dn_ipmt_getServiceInfo_rpt*)app_vars.replyBuf;

   //printf("\nINFO:     RC=");
   //printf("%d\n",reply->RC);

   //printf("\nINFO:     value=");
   //printf("%d\n",reply->value);

   // schedule next event
   if (reply->RC!=0 || reply->value>app_vars.dataPeriod) {
	   process_post(&looci_smartmeship,PC_MOTE_BWREQ_EVENT,NULL);
//      fsm_scheduleEvent(CMD_PERIOD, &IpMtWrapper::api_requestService);
   } else {
	   process_post(&looci_smartmeship,PC_MOTE_BWRXD_EVENT,NULL);
//      fsm_scheduleEvent(CMD_PERIOD, &IpMtWrapper::api_sendTo);
   }
}


void api_getServiceInfo(void) {
	dn_err_t err;


   // log
   //printf("\n");
   //printf("\nINFO:     api_getServiceInfo... returns \n");

   // arm callback
   fsm_setCallback(api_getServiceInfo_reply);

   // issue function
   err = dn_ipmt_getServiceInfo(
      0xfffe,                                              // destAddr (0xfffe==manager)
      SERVICE_TYPE_BW,                                     // type
      (dn_ipmt_getServiceInfo_rpt*)(app_vars.replyBuf)     // reply
   );

   // log
   //printf("\nError:%d\n",err);
   //Set callback timer
      ctimer_set(&config_timeout,API_TIMEOUT,api_timedout,NULL);

}


//===== requestService

void api_requestService_reply(void) {
   dn_ipmt_requestService_rpt* reply;

   ctimer_stop(&config_timeout);

   //printf("\nINFO:     api_requestService_reply\n");

   reply = (dn_ipmt_requestService_rpt*)app_vars.replyBuf;

   //printf("\nINFO:     RC=");
   //printf("%d\n",reply->RC);

   process_post(&looci_smartmeship,PC_MOTE_BWRXD_EVENT,NULL);

}



void api_requestService(void) {
	dn_err_t err;

	// log
   //printf("\n");
   //printf("\nINFO:     api_requestService... returns\n ");

   // arm callback
   fsm_setCallback(api_requestService_reply);

   // issue function
   err = dn_ipmt_requestService(
      0xfffe,                                              // destAddr (0xfffe==manager)
      SERVICE_TYPE_BW,                                     // serviceType
      app_vars.dataPeriod,                                 // value
      (dn_ipmt_requestService_rpt*)(app_vars.replyBuf)     // reply
   );

   // log
   //printf("\nError:%d\n",err);

   //Set callback timer
   ctimer_set(&config_timeout,API_TIMEOUT,api_timedout,NULL);

}


//===== openSocket

void api_openSocket_reply(void) {
   dn_ipmt_openSocket_rpt* reply;

   //printf("\nINFO:     api_openSocket_reply\n");

   ctimer_stop(&config_timeout);

   reply = (dn_ipmt_openSocket_rpt*)app_vars.replyBuf;

   //printf("INFO:     socketId=");
   //printf("%d",reply->socketId);

   if(reply->RC==0)
   {
   // store the socketID
   app_vars.socketId = reply->socketId;
   }
   else
   {
   //process_post(&looci_smartmeship,PC_MOTE_EXCEPTION,NULL);
	   app_vars.socketId = DEFAULT_SOCKETID;
   }
   process_post(&looci_smartmeship,PC_MOTE_OPENSOCKET_EVENT,NULL);
}





void api_openSocket(void) {
   dn_err_t err;

   // log
   //printf("\n");
   //printf("\nINFO:     api_openSocket... returns \n");

   // arm callback
   fsm_setCallback(api_openSocket_reply);

   // issue function
   err = dn_ipmt_openSocket(
      0,                                              // protocol
      (dn_ipmt_openSocket_rpt*)(app_vars.replyBuf)    // reply
   );

   // log
   //printf("\nError code is:%d\n",err);

   //Set callback timer
   ctimer_set(&config_timeout,API_TIMEOUT,api_timedout,NULL);

}


//===== bindSocket

void api_bindSocket_reply(void) {
   dn_ipmt_bindSocket_rpt* reply;

   ctimer_stop(&config_timeout);

   //printf("INFO:     api_bindSocket_reply");

   reply = (dn_ipmt_bindSocket_rpt*)app_vars.replyBuf;

   //printf("INFO:     RC=");
   printf("\n Bound socket with id %d and port %u -> reply %d\n",app_vars.socketId,app_vars.srcPort,reply->RC);
   //if(reply->RC==0)
   //{
	   process_post(&looci_smartmeship,PC_MOTE_BOUND_EVENT,NULL);
   //}



}

void api_bindSocket(void) {
   dn_err_t err;

   // log
   //printf("");
   //printf("INFO:     api_bindSocket... returns \n");

   // arm callback
   fsm_setCallback(api_bindSocket_reply);

   // issue function
   err = dn_ipmt_bindSocket(
      app_vars.socketId,                              // socketId
      app_vars.srcPort,                               // port
      (dn_ipmt_bindSocket_rpt*)(app_vars.replyBuf)    // reply
   );

   // log
   //printf("\nError:%d\n",err);
   //Set callback timer
   ctimer_set(&config_timeout,API_TIMEOUT,api_timedout,NULL);

}



//===== sendTo

void api_sendTo_reply(void) {
   dn_ipmt_sendTo_rpt* reply;

   //printf("\nINFO:     api_sendTo_reply\n");

   reply = (dn_ipmt_sendTo_rpt*)app_vars.replyBuf;

   //printf("\nINFO:     RC=");
   //printf("%d\n",reply->RC);

   if(reply->RC==0)
   {
	   printf("\n Socket is %d and packet queued up\n",app_vars.socketId);
	   clock_delay_msec(1000);
   }
   else
   {
	   printf("\n queue overflow \n");
	   process_post(&looci_smartmeship,PC_MOTE_EXCEPTION,NULL);
   }

}


void api_sendTo(const void *data, int len,uint16_t dport,uint8_t toaddr[16]) {
   dn_err_t err;
   //uint16_t dataVal;
   //uint8_t  payload[2];
   uint8_t  lenWritten;
   static uint8_t i=0;


   //lc_printByteArray(data,len);


   // log
   //printf("\n");
   //printf("INFO:     api_sendTo... returns ");

   // arm callback
   fsm_setCallback(api_sendTo_reply);

   // create payload
//   app_vars.dataGenerator(&dataVal);
   	 //dataVal=20000;
   	 //dn_write_uint16_t(payload, dataVal);

   //lc_printHexArray(toaddr,16);

   // issue function
   err = dn_ipmt_sendTo(
      app_vars.socketId,                                   // socketId
      //app_vars.destAddr,                                   	// destIP
      toaddr,
      dport,                                   				// destPort
      SERVICE_TYPE_BW,                                     // serviceType
      0,                                                   // priority
      0xffff,                                              // packetId
      data,                                             // payload
      len,                                     // payloadLen
      (dn_ipmt_sendTo_rpt*)(app_vars.replyBuf)             // reply
   );

   // log
   //printf("\nError:%d\n",err);

   //printf("\nINFO:     sending value: ");
   //printf("%d\n",dataVal);


   //ctimer_set(&config_timeout,TEST_TIMEOUT,test_transmit,NULL);

}


//======soft reset
void api_reset(void)
{
	dn_err_t err;


	printf("\n sending reset \n");

	// arm callback
	fsm_setCallback(api_reset_reply);


	err=dn_ipmt_reset((dn_ipmt_reset_rpt*)(app_vars.replyBuf));

}

void api_reset_reply(void)
{
	dn_ipmt_reset_rpt* reply;


	reply = (dn_ipmt_reset_rpt*)app_vars.replyBuf;

	if(reply->RC==0)
	{
		   printf("\n node is reset \n");
		   process_post(&looci_smartmeship,PC_MOTE_RESTART,NULL);
	}
}



void api_get_ipv6addr(void)
{
	dn_err_t err;


	// arm callback
	   fsm_setCallback(api_get_ipv6addr_reply);

	   err=dn_ipmt_getParameter_ipv6Address((dn_ipmt_getParameter_ipv6Address_rpt*)(app_vars.replyBuf));

}

void api_get_ipv6addr_reply(void)
{
	dn_ipmt_getParameter_ipv6Address_rpt* reply;

	reply = (dn_ipmt_getParameter_ipv6Address_rpt*)app_vars.replyBuf;

	memcpy(app_vars.myaddr,(uint8_t*)reply->ipv6Address,IPv6ADDR_LEN);

	//lc_printHexArray(app_vars.myaddr,IPv6ADDR_LEN);

}

void api_closeSocket_reply(void)
{
	dn_ipmt_closeSocket_rpt* reply;

	reply=(dn_ipmt_closeSocket_rpt*)app_vars.replyBuf;

	process_post(&looci_smartmeship,PC_MOTE_CLOSED_SOCKET_EVENT,NULL);

}


void api_closeSocket(void)
{

	dn_err_t err;

	// arm callback
	fsm_setCallback(api_closeSocket_reply);


	err=dn_ipmt_closeSocket(app_vars.socketId,(dn_ipmt_closeSocket_rpt*)app_vars.replyBuf);

}


PROCESS_THREAD(looci_smartmeship, ev, data)
{
  PROCESS_BEGIN();

  printf("[NF] LooCI SmartMesh Process Starting\r\n");

  moteinit();

  while(1) {
    PROCESS_WAIT_EVENT();

    switch(ev)
    {
    case PC_MOTE_IDLE_EVENT:
    		printf("\nMote is in idle state\n");
    		api_join();
    		break;
    case PC_MOTE_OPRL_EVENT:
    	printf("\nMote is operational\n");
    	api_closeSocket();
    	break;
    case PC_MOTE_BWREQ_EVENT:
    	printf("\n Request BW\n");
    	api_requestService();
    	break;
    case PC_MOTE_BWRXD_EVENT:
    	printf("\n Bandwidth Granted \n");
    	api_openSocket();
    	break;
    case PC_MOTE_BOUND_EVENT:
    	printf("\n ready for communication \n");
    	api_get_ipv6addr();
    	if(app_vars.init==0)
    	{
#if LOOCIEN == 1
    		process_post(&looci,PROCESS_EVENT_CONTINUE,NULL);
#else
    		//process_start(&C_One_process,NULL);
    		//process_start(&C_Two_process,NULL);
    		//stop_clock();

    		process_start(&hello_world_process,NULL);
    		sleep=1;

#endif

    		app_vars.init=1;
    	}
    	clock_delay_msec(1000);
    	//api_sendTo();
    	break;
    case PC_MOTE_OPENSOCKET_EVENT:
    	printf("\n binding new socket \n");
    	api_bindSocket();
    	break;
    case PC_MOTE_RESTART:
    	printf("\n Restarting the steps \n");
    	api_getMoteStatus();
    	break;
    case PC_MOTE_EXCEPTION:
    	printf("\n resource depleted! \n");
    	//api_reset();
    	break;
    case PC_MOTE_CLOSED_SOCKET_EVENT:
    	printf("\n Socket closed \n");
    	api_getServiceInfo();
    	break;
    default:
    	//printf("\n Unknown event \n");
    	break;
    }
  }

  PROCESS_END();
}


//void printpayload(smpl rxpayload){
//	printf("\n Payload: SrcP %u DstP %u Length %d \n",rxpayload.srcP,rxpayload.dstP,rxpayload.length);
//	lc_printHexArray(rxpayload.srcIP,16);
//	lc_printHexArray(rxpayload.pl,rxpayload.aplength);
//}

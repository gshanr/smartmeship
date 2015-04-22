
#include "contiki.h"
#include "contiki-smip.h"
#include "smartmesh.h"
#include "looci.h"
#include "networking_private.h"



#include "debug.h"

#define DEBUG_PRINT 0

uint8_t nodeaddr[16]={0xfe,0x80,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x17,0x0d,0x00,0x00,0x60,0x1f,0x7f};
//uint8_t hostaddr[16]={0xbb,0xbb,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x02};
uint8_t bcastaddr[16]={0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff};

PROCESS(looci_smartmeship, "LooCI SmartMeshIP");
//AUTOSTART_PROCESSES(&looci_smartmeship);

void api_timedout(void *ptr){

	// issue cancel command
	dn_ipmt_cancelTx();

	api_getMoteStatus();
}

void test_transmit(void){

	//api_sendTo();
}

int readdata(unsigned char c)
{
	//printf("rxd %d\n",c);
	app_vars.ipmt_uart_rxByte_cb((uint8_t)c);
	return c;
}


void error_check(dn_err_t e){
	if(e!=DN_ERR_NONE) {
		printf("\nAPI Error\n");
	}

}


extern void dn_uart_init(dn_uart_rxByte_cbt rxByte_cb) {
	// remember function to call back
	app_vars.ipmt_uart_rxByte_cb = rxByte_cb;

	rs232_init(RS232_PORT_1, USART_BAUD_115200,USART_PARITY_NONE | USART_STOP_BITS_1 | USART_DATA_BITS_8);

	rs232_set_input(RS232_PORT_1,readdata);


   #if DEBUGPRINT
	printf("\n[SMARTDUST] Initializing UART\n");
   #endif

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


		dn_ipmt_events_notif = (dn_ipmt_events_nt*)app_vars.notifBuf;
		#if DEBUGPRINT
		printf("\n");
		printf("\nINFO:     notif CMDID_EVENTS\n");
		printf("\nINFO:     state=");
		printf("%d\n",dn_ipmt_events_notif->state);
		printf("%u\n",dn_ipmt_events_notif->events);
		#endif

		switch (dn_ipmt_events_notif->state) {
		case MOTE_STATE_IDLE:
				#if DEBUGPRINT
			printf("\nMote Idle\n");
				#endif
			process_post(&looci_smartmeship,PC_MOTE_IDLE_EVENT,NULL);
			break;
		case MOTE_STATE_NEGOCIATING:
				#if DEBUGPRINT
			printf("\nMote Negotiating\n");
				#endif
			/*reseting to avoid the restart of state machine*/
			ctimer_reset(&config_timeout);
			break;
		case MOTE_STATE_OPERATIONAL:
				#if DEBUGPRINT
			printf("State is: %d\n",dn_ipmt_events_notif->state);
			printf("\nMote Operational\n");
				#endif
			if(app_vars.currstate==0)
			{
				process_post(&looci_smartmeship,PC_MOTE_OPRL_EVENT,NULL);
				app_vars.currstate=1;
			}
			break;
		default:
			// nothing to do
			break;
		}
		break;
	case CMDID_RECEIVE:
		dn_ipmt_receive_notif=(dn_ipmt_receive_nt*)app_vars.notifBuf;
			#if DEBUGPRINT
		lc_printHexArray(dn_ipmt_receive_notif->srcAddr,sizeof(dn_ipmt_receive_notif->srcAddr));
		printf("\n");
		lc_printHexArray(dn_ipmt_receive_notif->payload,dn_ipmt_receive_notif->payloadLen);
		printf("\n");
		printf("Port is %u",dn_ipmt_receive_notif->srcPort);
			#endif

		/* Copying received payload to global receive buffer! */
		memcpy(rxpayload.socketId,dn_ipmt_receive_notif->socketId,sizeof(uint8_t));
		memcpy(rxpayload.srcAddr,dn_ipmt_receive_notif->srcAddr,16);
		rxpayload.srcPort=dn_ipmt_receive_notif->srcPort;
		rxpayload.payloadLen=dn_ipmt_receive_notif->payloadLen;
		memcpy(rxpayload.payload,dn_ipmt_receive_notif->payload,dn_ipmt_receive_notif->payloadLen);

		/* Uncomment the following line to check the correctness of memcpy call */
		//lc_printHexArray(rxpayload.payload,rxpayload.payloadLen);

		/*Event is posted to LooCI networking framework (networking.c)*/
		process_post(&looci_networking_framework,PC_MOTE_RXD_EVENT,NULL);

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
	#if DEBUGPRINT
	printf("\n[SMARTDUST] Initializing Mote\n");
	#endif

	app_vars.srcPort=5555;
	//app_vars.destPort=61000;
	app_vars.destPort=5555;
	app_vars.dataPeriod=1000;
	app_vars.init=0;
	app_vars.currstate=0;

	memcpy(app_vars.destAddr,(uint8_t*)ipv6Addr_manager,IPv6ADDR_LEN);
	//memcpy(app_vars.destAddr,(uint8_t*)hostaddr,IPv6ADDR_LEN);
	dn_ipmt_init(notifCbmt,app_vars.notifBuf,sizeof(app_vars.notifBuf),replyCbmt);

	api_getMoteStatus();

}


//===== getMoteStatus//

void api_getMoteStatus_reply(void) {
	dn_ipmt_getParameter_moteStatus_rpt* reply;

	ctimer_stop(&config_timeout);

	reply = (dn_ipmt_getParameter_moteStatus_rpt*)app_vars.replyBuf;



	switch (reply->state) {
	case MOTE_STATE_IDLE:
		  #if DEBUGPRINT
		printf("\nMote Idle\n");
		  #endif
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
		  #if DEBUGPRINT
		printf("\nMote Operational\n");
		  #endif
		break;
	default:

		break;
	}
}


void api_getMoteStatus(void) {
	dn_err_t err;

	// arm callback
	fsm_setCallback(api_getMoteStatus_reply);

	// issue function
	err = dn_ipmt_getParameter_moteStatus(
	        (dn_ipmt_getParameter_moteStatus_rpt*)(app_vars.replyBuf)
	        );

	error_check(err);

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

	// arm callback
	fsm_setCallback(api_join_reply);

	// issue function
	err = dn_ipmt_join(
	        (dn_ipmt_join_rpt*)(app_vars.replyBuf) // reply
	        );

	error_check(err);

	//Set callback timer
	ctimer_set(&config_timeout,JOIN_TIMEOUT,api_timedout,NULL);

}


//===== getServiceInfo

void api_getServiceInfo_reply(void) {
	dn_ipmt_getServiceInfo_rpt* reply;
	ctimer_stop(&config_timeout);
	reply = (dn_ipmt_getServiceInfo_rpt*)app_vars.replyBuf;

	// schedule next event
	if (reply->RC!=0 || reply->value>app_vars.dataPeriod) {
		process_post(&looci_smartmeship,PC_MOTE_BWREQ_EVENT,NULL);
	} else {
		process_post(&looci_smartmeship,PC_MOTE_BWRXD_EVENT,NULL);
	}
}


void api_getServiceInfo(void) {
	dn_err_t err;

	// arm callback
	fsm_setCallback(api_getServiceInfo_reply);

	// issue function
	err = dn_ipmt_getServiceInfo(
	        0xfffe,                                    // destAddr (0xfffe==manager)
	        SERVICE_TYPE_BW,                           // type
	        (dn_ipmt_getServiceInfo_rpt*)(app_vars.replyBuf) // reply
	        );

	error_check(err);

	//Set callback timer
	ctimer_set(&config_timeout,API_TIMEOUT,api_timedout,NULL);

}


//===== requestService

void api_requestService_reply(void) {
	dn_ipmt_requestService_rpt* reply;

	ctimer_stop(&config_timeout);

	reply = (dn_ipmt_requestService_rpt*)app_vars.replyBuf;



	process_post(&looci_smartmeship,PC_MOTE_BWRXD_EVENT,NULL);

}



void api_requestService(void) {
	dn_err_t err;

	// arm callback
	fsm_setCallback(api_requestService_reply);

	// issue function
	err = dn_ipmt_requestService(
	        0xfffe,                                    // destAddr (0xfffe==manager)
	        SERVICE_TYPE_BW,                           // serviceType
	        app_vars.dataPeriod,                       // value
	        (dn_ipmt_requestService_rpt*)(app_vars.replyBuf) // reply
	        );

	error_check(err);

	//Set callback timer
	ctimer_set(&config_timeout,API_TIMEOUT,api_timedout,NULL);

}


//===== openSocket

void api_openSocket_reply(void) {
	dn_ipmt_openSocket_rpt* reply;

	ctimer_stop(&config_timeout);

	reply = (dn_ipmt_openSocket_rpt*)app_vars.replyBuf;


	if(reply->RC==0) {
		// store the socketID
		app_vars.socketId = reply->socketId;
	} else {
		//process_post(&looci_smartmeship,PC_MOTE_EXCEPTION,NULL);
		app_vars.socketId = DEFAULT_SOCKETID;
	}
	process_post(&looci_smartmeship,PC_MOTE_OPENSOCKET_EVENT,NULL);
}





void api_openSocket(void) {
	dn_err_t err;

	// arm callback
	fsm_setCallback(api_openSocket_reply);

	// issue function
	err = dn_ipmt_openSocket(
	        0,                                    // protocol
	        (dn_ipmt_openSocket_rpt*)(app_vars.replyBuf) // reply
	        );

	error_check(err);

	//Set callback timer
	ctimer_set(&config_timeout,API_TIMEOUT,api_timedout,NULL);

}


//===== bindSocket

void api_bindSocket_reply(void) {
	dn_ipmt_bindSocket_rpt* reply;

	ctimer_stop(&config_timeout);

	reply = (dn_ipmt_bindSocket_rpt*)app_vars.replyBuf;

	#if DEBUG_PRINT
	printf("\n Bound socket with id %d and port %u -> reply %d\n",app_vars.socketId,app_vars.srcPort,reply->RC);
	#endif
	process_post(&looci_smartmeship,PC_MOTE_BOUND_EVENT,NULL);
}

void api_bindSocket(void) {
	dn_err_t err;

	// arm callback
	fsm_setCallback(api_bindSocket_reply);

	// issue function
	err = dn_ipmt_bindSocket(
	        app_vars.socketId,                    // socketId
	        app_vars.srcPort,                     // port
	        (dn_ipmt_bindSocket_rpt*)(app_vars.replyBuf) // reply
	        );

	error_check(err);

	//Set callback timer
	ctimer_set(&config_timeout,API_TIMEOUT,api_timedout,NULL);

}



//===== sendTo

void api_sendTo_reply(void) {
	dn_ipmt_sendTo_rpt* reply;



	reply = (dn_ipmt_sendTo_rpt*)app_vars.replyBuf;



	if(reply->RC==0)   {
#if DEBUG_PRINT
		printf("\n Socket is %d and packet queued up\n",app_vars.socketId);
#endif
		clock_delay_msec(1000);
	}    else    {
#if DEBUG_PRINT
		printf("\n queue overflow \n");
#endif
		process_post(&looci_smartmeship,PC_MOTE_EXCEPTION,NULL);
	}

}


void api_sendTo(const void *data, int len,uint16_t dport,uint8_t toaddr[16]) {
	dn_err_t err;

	lc_printByteArray(data,len);



	// arm callback
	fsm_setCallback(api_sendTo_reply);


	//dataVal=20000;
	//dn_write_uint16_t(payload, dataVal);

	lc_printHexArray(toaddr,16);

	// issue function
	err = dn_ipmt_sendTo(
	        app_vars.socketId,                         // socketId
	        //app_vars.destAddr,                                    // destIP
	        toaddr,
	        dport,                                                  // destPort
	        SERVICE_TYPE_BW,                           // serviceType
	        0,                                         // priority
	        0xffff,                                    // packetId
	        data,                                   // payload
	        len,                           // payloadLen
	        (dn_ipmt_sendTo_rpt*)(app_vars.replyBuf)   // reply
	        );

	error_check(err);

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

	error_check(err);
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

	error_check(err);

}

void api_get_ipv6addr_reply(void)
{
	dn_ipmt_getParameter_ipv6Address_rpt* reply;

	reply = (dn_ipmt_getParameter_ipv6Address_rpt*)app_vars.replyBuf;

	memcpy(app_vars.myaddr,(uint8_t*)reply->ipv6Address,IPv6ADDR_LEN);

	lc_printHexArray(app_vars.myaddr,IPv6ADDR_LEN);

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

	error_check(err);

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
				process_post(&looci,PROCESS_EVENT_CONTINUE,NULL);
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

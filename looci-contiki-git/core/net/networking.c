/**
   LooCI Copyright (C) 2013 KU Leuven.
   All rights reserved.

   LooCI is an open-source software development kit for developing and maintaining networked embedded applications;
   it is distributed under a dual-use software license model:

   1. Non-commercial use:
   Non-Profits, Academic Institutions, and Private Individuals can redistribute and/or modify LooCI code under the terms of the GNU General Public License version 3, as published by the Free Software Foundation
   (http://www.gnu.org/licenses/gpl.html).

   2. Commercial use:
   In order to apply LooCI in commercial code, a dedicated software license must be negotiated with KU Leuven Research & Development.

   Contact information:
   Administrative Contact: Sam Michiels, sam.michiels@cs.kuleuven.be
   Technical Contact:           Danny Hughes, danny.hughes@cs.kuleuven.be
   Address:
   iMinds-DistriNet, KU Leuven
   Celestijnenlaan 200A - PB 2402,
   B-3001 Leuven,
   BELGIUM.
 */
/**
 * @addtogroup nf
 * @{
 */
/**
 * @file
 * Implementation of the LooCI Networking Framework
 * @author
 * Wouter Horré <wouter.horre@cs.kuleuven.be>
 */

#include "contiki-net.h"
#include "networking_private.h"
#include "events_private.h"
#include "peers.h"
#include "memb.h"
#include "codebaseDeployment.h"

#define SMARTMESH_EN 1

#if SMARTMESH_EN
#include "contiki-smip.h"
#include "smartmesh.h"
#endif


#include <string.h>

#ifdef LOOCI_NETWORK_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif


#define UIP_IP_BUF   ((struct uip_udpip_hdr *)&uip_buf[UIP_LLH_LEN])


PROCESS(looci_networking_framework, "LooCI Networking Framework");

static struct uip_udp_conn * udpconn;
#if SMARTMESH_EN == 0
static void udp_handler(process_event_t ev, process_data_t data) {
	PRINT_LN("[LooCI NF] UDP Handler called");
	if(uip_udp_conn!=NULL && uip_newdata()) {
		PRINT_LN("[LooCI NF] uip_datalen() is %u", uip_datalen());
		struct looci_event event;
		event.payloadMemory.next = NULL;
		event.payloadMemory.size = 0;
		event.payloadMemory.ptr = NULL;
		event.source_node = peer_get_id_or_add((peer_addr_t *) &UIP_IP_BUF->srcipaddr);
		event.dst_node = peer_get_id_or_add((peer_addr_t *) &UIP_IP_BUF->destipaddr);

		PRINT6ADDR(&UIP_IP_BUF->srcipaddr);
		PRINT_LN("[NF] The event is from peer %u", event.source_node);
		if(event.source_node == PEER_ID_NONE) {
			// Do we need to handle this in another way?
			PRINT_LN("[NF] Dropping incoming event due to peer library error");
			return;
		}



		ev_fill_event_from_byte_array((unsigned char*) uip_appdata,uip_datalen(),&event);
		PRINT_LN("[NF] Event length is %u", event.len);
		looci_event_handle_remote_event(&event);
		PRINT_LN("[NF] Done calling receive");
		mmem_free(&event.payloadMemory);
	}
}
#endif

extern void smreceive(dn_ipmt_receive_nt* rxpayload){

	PRINT_LN("[NF] Data received");

	struct looci_event event;
	event.payloadMemory.next = NULL;
	event.payloadMemory.size = 0;
	event.payloadMemory.ptr = NULL;
	event.source_node = peer_get_id_or_add((peer_addr_t *) rxpayload->srcAddr);
	event.dst_node = peer_get_id_or_add((peer_addr_t *) app_vars.myaddr);

	//lc_printHexArray(rxpayload.payload,rxpayload.payloadLen);

	PRINT6ADDR(rxpayload->srcAddr);
	PRINT_LN("[NF] The event is from peer %u", event.source_node);
	if(event.source_node == PEER_ID_NONE) {
		// Do we need to handle this in another way?
		PRINT_LN("[NF] Dropping incoming event due to peer library error");
		return;
	}
	lc_printHexArray(rxpayload->payload,rxpayload->payloadLen);
	ev_fill_event_from_byte_array((unsigned char*)rxpayload->payload,rxpayload->payloadLen,&event);
	PRINT_LN("[NF] Event type is %u", event.type);
	looci_event_handle_remote_event(&event);
	PRINT_LN("[NF] Done calling receive");
	mmem_free(&event.payloadMemory);
}

PROCESS_THREAD(looci_networking_framework, ev, data)
{
	PROCESS_BEGIN();

	PRINT_LN("[NF] starting\r\n");

	//initialization
	//memb_init(&net_events);
	peer_init();

//  udpconn = udp_new(NULL, UIP_HTONS(LOOCI_NF_UDP_PORT), NULL);
  #if SMARTMESH_EN == 0
	udpconn = udp_new(NULL, 0, NULL);
	udp_bind(udpconn, UIP_HTONS(LOOCI_NF_UDP_PORT));
  #endif

	while(1) {
		PROCESS_WAIT_EVENT();
	#if SMARTMESH_EN == 0
		if(ev == tcpip_event && uip_udp_conn==udpconn) {
			udp_handler(ev, data);
		}
		else if(ev==PC_MOTE_RXD_EVENT)
		{
	#endif
		PRINT_LN("[NF] SmartMesh Rxd\r\n");
		//lc_printHexArray(dn_ipmt_receive_notif->payload,dn_ipmt_receive_notif->payloadLen);
		/*Payload Extraction*/
		//rxpayload.length=dn_ipmt_receive_notif->payloadLen;
		//rxpayload.aplength=rxpayload.length-23;
		//printf("\n Data received with length %d\n",rxpayload.aplength);

		//memcpy(rxpayload.srcIP,dn_ipmt_receive_notif->payload+2,16);
		//rxpayload.srcP = (dn_ipmt_receive_notif->payload[19] << 8) | (dn_ipmt_receive_notif->payload[20]);
		//rxpayload.dstP=  (dn_ipmt_receive_notif->payload[21] << 8) | (dn_ipmt_receive_notif->payload[22]);
		//memcpy(rxpayload.pl,dn_ipmt_receive_notif->payload+23,rxpayload.aplength);
		//memcpy(rxpayload.dstIP,app_vars.myaddr,IPv6ADDR_LEN);
		//lc_printHexArray(*rxpayload.payload,*rxpayload.payloadLen);
		//lc_printHexArray(rxpayload.srcIP,sizeof(rxpayload.srcIP));
		//printf("\n Src and Dst Port is: %u %u \n",rxpayload.srcP,rxpayload.dstP);
		if(rxpayload.srcPort==DEPLOY_PORT)
		{
			process_post(&looci_component_store,PC_MOTE_DEPLOY_EVENT,NULL);
		}
		else
		{
			smreceive(&rxpayload);
		}
#if SMARTMESH_EN == 0
	}
#endif
	}

	PROCESS_END();
}

void looci_nf_send_event(struct looci_event * event) {
	//if anycast: send back up sender to allow wire from alls
	if(event->dst_node == PEER_ID_ANY) {
		PRINT_LN("[NF] Received anycast message, routing back up");
		looci_event_handle_remote_event(event);
		event->dst_node = PEER_ID_ALL;
	}

	// allocate space for temporarily storing the event before sending
	PRINT_LN("[NF] sending event of size: %u to peer %u",event->len, event->dst_node);
	unsigned char netPayload[LOOCI_EVENT_MAX_HEADER_LEN + event->len + 3];

	uint16_t index = ev_fill_byte_array_from_event(event,netPayload);
	// get dst addr
	uip_ip6addr_t * destination = (uip_ip6addr_t *) peer_get_addr(event->dst_node);
	// send the event
	PRINT_LN("[NF] Sending event");
	PRINT_LN("[NF] packet: ");
	PRINT_BYTE_ARRAY(netPayload,index);
	PRINT_LN("[NF] Event len: %u, Header len; %u, data len %u", event->len,ev_get_totalHeaderSize(event->header),index);
	#if SMARTMESH_EN
	api_sendTo(netPayload,index,APP_PORT,destination);
	#else
	uip_udp_packet_sendto(udpconn, netPayload, index,
	                      destination, UIP_HTONS(LOOCI_NF_UDP_PORT));
	#endif


}







/** @} */

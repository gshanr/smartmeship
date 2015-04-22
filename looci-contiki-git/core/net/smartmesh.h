#ifndef __SMARTMESH_H__
#define __SMARTMESH_H__

#include "contiki.h"
#include "contiki-smip.h"

PROCESS_NAME(looci_smartmeship);

struct ctimer config_timeout;

#define DEPLOY_PORT             61010
#define APP_PORT                5555

#define API_TIMEOUT             CLOCK_SECOND*2
#define JOIN_TIMEOUT    CLOCK_SECOND*60
#define TEST_TIMEOUT    CLOCK_SECOND*30

#define IPv6ADDR_LEN              16

// mote state
#define MOTE_STATE_IDLE           0x01
#define MOTE_STATE_SEARCHING      0x02
#define MOTE_STATE_NEGOCIATING    0x03
#define MOTE_STATE_CONNECTED      0x04
#define MOTE_STATE_OPERATIONAL    0x05

#define DEFAULT_SOCKETID          22

#define SERVICE_TYPE_BW           0x00

typedef void (*fsm_reply_callback)(void);
void fsm_setCallback(fsm_reply_callback cb);

dn_ipmt_receive_nt* dn_ipmt_receive_notif;

//contiki process events
#define PC_MOTE_IDLE_EVENT                      0x30
#define PC_MOTE_OPRL_EVENT                      0x31
#define PC_MOTE_BWREQ_EVENT                     0x32
#define PC_MOTE_BWRXD_EVENT                     0x33
#define PC_MOTE_BOUND_EVENT                     0x34
#define PC_MOTE_OPENSOCKET_EVENT        0x35
#define PC_MOTE_EXCEPTION                       0x36
#define PC_MOTE_RESTART                         0x37
#define PC_MOTE_OPENSOCK_EVENT          0x39
#define PC_MOTE_RXD_EVENT                       0x40
#define PC_MOTE_DEPLOY_EVENT            0x41
#define PC_MOTE_CLOSED_SOCKET_EVENT 0x42





typedef struct {

	fsm_reply_callback replyCb;
	// module
	dn_uart_rxByte_cbt ipmt_uart_rxByte_cb;
	// api
	uint8_t socketId;                                  // ID of the mote's UDP socket
	uint16_t srcPort;                                  // UDP source port
	uint8_t destAddr[IPv6ADDR_LEN];                    // IPv6 destination address
	uint16_t destPort;                                 // UDP destination port
	uint32_t dataPeriod;                                                                            //periodicity
	uint8_t replyBuf[MAX_FRAME_LENGTH];                // holds notifications from ipmt
	uint8_t notifBuf[MAX_FRAME_LENGTH];                // notifications buffer internal to ipmt
	uint8_t myaddr[IPv6ADDR_LEN];
	uint8_t init;
	uint8_t currstate;

} app_vars_t;

app_vars_t app_vars;

dn_ipmt_receive_nt rxpayload;

//typedef struct{
//	uint8_t                         srcIP[IPv6ADDR_LEN];
//	uint8_t				dstIP[IPv6ADDR_LEN];
//	uint16_t			srcP;
//	uint16_t			dstP;
//	uint8_t				length;
//	uint8_t				pl[80];
//	uint8_t                         aplength;
//}smpl;
//
//smpl rxpayload;

uint8_t deployReply[IPv6ADDR_LEN];            // IPv6 destination address

void api_timedout(void *ptr);
void test_transmit(void);

//void printpayload(smpl rxpayload);

int readdata(unsigned char c);
void replyCbmt(uint8_t rcmdId);
void moteinit(void);
void api_getMoteStatus(void);
void api_getMoteStatus_reply(void);
void api_join(void);
void api_join_reply(void);
void api_getServiceInfo(void);
void api_getServiceInfo_reply(void);
void api_requestService(void);
void api_requestService_reply(void);
void api_openSocket(void);
void api_openSocket_reply(void);
void api_bindSocket(void);
void api_bindSocket_reply(void);
void api_sendTo(const void *data, int len,uint16_t dport,uint8_t toaddr[16]);
void api_sendTo_reply(void);
void api_reset(void);
void api_reset_reply(void);
void api_get_ipv6addr(void);
void api_get_ipv6addr_reply(void);
void api_closeSocket(void);
void api_closeSocket_reply(void);
void error_check(dn_err_t e);
extern void smreceive(dn_ipmt_receive_nt* rxpayload);


#endif /* __SMARTMESH_H__ */

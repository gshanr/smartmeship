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
 * @addtogroup cstore
 * @{
 */
/**
 * @internal
 * @file 
 * Implementation of the LooCI Component Store
 * @author 
 * Wouter Horré <wouter.horre@cs.kuleuven.be>
 */

#include "contiki.h"
#include "mmem.h"
#include "cfs.h"
#include "cfs-coffee.h"
#include "contiki-net.h"
#include "codebaseDeployment.h"
#include "elfloader.h"
#include "management/codebaseManager.h"
#include "events/events_private.h"
#include "smartmesh.h"

#include "looci_platform.h"

#ifdef LOOCI_DEPLOYMENT_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif

#ifndef LOOCI_CSTORE_PORT
#define LOOCI_CSTORE_PORT 4321
#endif

#ifndef LOOCI_CSTORE_MAX_CONN
#define LOOCI_CSTORE_MAX_CONN 1
#endif

PROCESS(looci_component_store, "LooCI Component Store");

#define CSTORE_BUF_NONE 0
#define CSTORE_BUF_READ 1
#define CSTORE_BUF_BLOCKED_NEWDATA 2

#define TMP_FILE NULL


struct cstore_buf {
  uint8_t * readptr;
  uint16_t readlen;
  uint8_t state;
};

struct cstore_conn_state {
  struct pt pt;
  struct cstore_buf buffer;
  uint8_t endBuffer[2];
  uint16_t length;
  uint16_t received;
  int fp;
};

uint8_t chunk;
uint16_t size;
uint8_t fptr;
uint8_t	ldbuf[2];

static struct mmem connectionState;


/**
 * @internal
 * Load components from the given file descriptor
 *
 * @param fd The file descriptor to load from
 * 
 * @return the elfloader slot where the components are loaded to
 */
static uint8_t cstore_load(char * filename) {
  uint8_t slot = ELFLOADER_SLOTS; // this is an invalid slot number.
  int fd = cfs_open(filename, CFS_READ | CFS_WRITE);
  uint8_t cmpId = 0;
  if(fd >= 0) {
    int ret = elfloader_load(fd, &slot);
    cfs_close(fd);
    if(ret == ELFLOADER_OK) {
      PRINTF("Successfully loaded components from file %s\r\n", filename);
      PRINTF("Into slot %u \r\n",slot);
      PRINTF("Adding comp from %p\r\n", &elfloader_autostart_processes[slot]);
      PRINTF("That pointer points to %p\r\n", elfloader_autostart_processes[slot]);
      PRINTF("Adding component to component repository\r\n");
      cmpId = ((struct component *) elfloader_autostart_processes[slot])->doReg(COMP_OP_INIT);
      PRINTF("Coming here\n");
      if(cmpId > 0){
          PRINTF("Added components found in file %s\r\n", filename);
      } else{
          elfloader_unload(slot);
      }
    } else {
      PRINTF("Error %u loading components from file %s\r\n", ret, filename);
      // Release allocated slot
      elfloader_unload(slot);
    }
  } else {
    PRINTF("Could not open file %s to load components\r\n", filename);
  }
  return cmpId;
}

static uint8_t cstore_newdata(struct cstore_buf * buf) {
  if(buf->readlen > 0) {
    // still data left in uip_appdata
    PRINTF("There is still data left in uip_appdata: %u bytes\r\n", buf->readlen);
    return 1;
  } else if (buf->state == CSTORE_BUF_READ) {
    // data is read
    PRINTF("All data was read from uip_appdata, now waiting for new data to come in\r\n");
    buf->state = CSTORE_BUF_BLOCKED_NEWDATA;
    return 0;
  } else if (uip_newdata()) {
    // new data in uip_appdata
    PRINTF("New data available from uip\r\n");
    buf->readptr = uip_appdata;
    buf->readlen = uip_datalen();
    buf->state = CSTORE_BUF_NONE;
    return 1;
  } else {
    // no new data
    PRINTF("No new data available\r\n");
    PRINTF("Flag: %u\r\n",uip_flags);
    return 0;
  }
}

static void init_buf(struct cstore_buf * buffer) {
  buffer->readlen = 0;
  buffer->readptr = NULL;
  buffer->state = CSTORE_BUF_NONE;
}

static uint16_t read_length(struct cstore_buf * buffer) {
  uint16_t * len = (uint16_t *) buffer->readptr;
  buffer->state = CSTORE_BUF_READ;
  buffer->readptr += sizeof(uint16_t);
  buffer->readlen -= sizeof(uint16_t);
  return UIP_HTONS(*len); // NTOHS operation is identical to HTONS
}

static void read_component(struct cstore_conn_state * state) {
	PRINT_LN("Prepare to write");
	unsigned int writeLen = state->buffer.readlen;
	if(writeLen>MAX_BLOCK_WRITE){
		writeLen = MAX_BLOCK_WRITE;
	}

	int written = cfs_write(state->fp, state->buffer.readptr, writeLen);
	state->buffer.state = CSTORE_BUF_READ;
	state->buffer.readptr += written;
	state->buffer.readlen -= written;
	state->received += written;
	PRINTF("Written %u bytes to file\r\n", written);
}

static 
PT_THREAD(handle_connection(struct cstore_conn_state * state))
{
    PT_BEGIN(&(state->pt));

    PRINTF("Protothread starts \r\n");

    PT_WAIT_UNTIL(&(state->pt), cstore_newdata(&(state->buffer))|| uip_closed());

    if(uip_closed()){
    	goto end;
    }

    // We have data, read the length
    state->length = read_length(&(state->buffer)); // TODO: what if we only receive 1 byte in first packet?
    PRINTF("Length of component is %hu\r\n", state->length);

    // Read the component itself
    PRINTF("Waiting for component itself to arrive\r\n");

    state->fp = cfs_open(TMP_FILE, CFS_WRITE);
    if(state->fp != -1) {
      do {
    	  PT_YIELD_UNTIL(&(state->pt), cstore_newdata(&(state->buffer))|| uip_closed());
        if(uip_closed()){
        	//unexpected close, end everything
            cfs_close(state->fp);
            cfs_remove(TMP_FILE);
            goto end;
        }else{
            read_component(state);
        }
      } while(state->received < state->length);
      cfs_close(state->fp);
      PRINTF("Successfully written component to file\r\n");


    } else {
      PRINTF("Could not open file to write component\r\n");
      goto end;
    }
    
    state->endBuffer[0] = cstore_load(TMP_FILE);
    state->endBuffer[1] = 0;
    // Remove the file
    PRINT_LN("Removing file %s\r\n", TMP_FILE);
    cfs_remove(TMP_FILE);

    if(state->endBuffer[0] < ELFLOADER_SLOTS) {
      // send info back to client

        do {
          PRINTF("We send %u\r\n", state->endBuffer[0]);
          uip_send(state->endBuffer,2);
          PRINTF("Start wait\r\n");
          PT_YIELD_UNTIL(&(state->pt), uip_poll() || uip_rexmit() || uip_closed());
          PRINTF("Stop wait\r\n");
        } while(!(uip_poll() || uip_closed()));
    }


    end:;

     // We're done, close connection if needed
    if(!uip_closed()) {
      uip_close();
    }


    PT_END(&(state->pt));
}

void lc_cstore_unload(struct component* component) {
  // find the slot in which the component resides
  uint8_t slot = ELFLOADER_SLOTS;
  uint8_t i = 0;
  PRINTF("check component %p:",component);
  for(i=0; i < ELFLOADER_SLOTS && slot >= ELFLOADER_SLOTS; ++i) {
	  PRINTF("%p,",elfloader_autostart_processes[i]);
	  if(elfloader_autostart_processes[i] == (void*)component) {
		  slot = i;
	  }
  }
  if(slot >= ELFLOADER_SLOTS) {
    // component is not dynamically loaded
	  PRINT_LN("[CD] cmp to unload not found %p",component);
    return;
  }
  PRINTF("[CD]unload components from slot %u, pointer %p,%p\r\n", slot, elfloader_autostart_processes[slot]);

  ((struct component *) elfloader_autostart_processes[slot])->doReg(COMP_OP_END);


  // unload the code in the elfloader
  elfloader_unload(slot);
}

PROCESS_THREAD(looci_component_store, ev, data)
{
  PROCESS_BEGIN();

  connectionState.ptr = NULL;

  static uint16_t written,rxd;

  PRINTF("Component Store starting\r\n");

  //tcp_listen(UIP_HTONS(LOOCI_CSTORE_PORT));
  chunk=0;
  size=0;
  written=0;

  while(1) {
	PROCESS_WAIT_EVENT_UNTIL(ev==PC_MOTE_DEPLOY_EVENT);
	chunk++;
	//PRINTF("deploy event received %d\n",chunk);
	//deployReply=rxpayload.srcIP;
	memcpy(deployReply,rxpayload.srcAddr,16);
	//PRINTF("LooCI Component Store, connection from rport: %u\r\n", rxpayload.srcP);
	//lc_printHexArray(rxpayload.pl,rxpayload.aplength);
	if(chunk==1)
	{
		size= ((rxpayload.payload[0] << 8) | rxpayload.payload[1]);
		PRINTF("Receiving component of size %u",size);
		//PRINTF("Expecting %d chunks", (size/50));
	    fptr=cfs_open(TMP_FILE, CFS_WRITE);
	}
	 if(fptr != -1) {

		 if(chunk==1)
		 {
		 rxd = cfs_write(fptr,rxpayload.payload+2, rxpayload.payloadLen-2);
		 }
		 else
		 {
	     rxd = cfs_write(fptr,rxpayload.payload,rxpayload.payloadLen);
		 }
		 //PRINTF("Written %u bytes", rxd);
		 written=written+rxd;
		 rxd=0;
		 PRINTF("Received %u bytes... \n", written);
		 if(written >= size)
		 {
		   cfs_close(fptr);
	       PRINTF("Successfully written component to file\r\n");
	       chunk=size=written=0;

		 ldbuf[0] = cstore_load(TMP_FILE);
		 ldbuf[1] = 0;
		 // Remove the file
		 PRINT_LN("Removing file %s\r\n", TMP_FILE);
		 cfs_remove(TMP_FILE);

		 if(ldbuf[0] < ELFLOADER_SLOTS) {
		       // send info back to client
			 api_sendTo(ldbuf,2,DEPLOY_PORT,(uint8_t *)deployReply);
		 }
		 }

	     } else {
	       PRINTF("Could not open file to write component\r\n");
	     }

//      if(mmem_alloc(&connectionState,sizeof(struct cstore_conn_state))) {
//    	 struct cstore_conn_state * state = connectionState.ptr;
//         PT_INIT(&(state->pt));
//         init_buf(&(state->buffer));
//         state->fp = -1;
//         state->received = 0;
//        tcp_markconn(uip_conn, state);
//         data = state;

//      else {
//        uip_abort();
//        continue;
//      }
//    }
//    // Schedule the protothread
//    if(uip_aborted() || uip_timedout() || (data!=NULL && PT_SCHEDULE(handle_connection(data))==0)) { // data!=NULL: sanity check
//      // Protothread exited or connection closed from other end -> cleanup
//      // TODO: in which order are closed, aborted, etc generated ?
//      PRINTF("Connection has been aborted, timed out or handling the connection is finished\r\n");
//      if(((struct cstore_conn_state *)data)->fp != -1) {
//        cfs_close(((struct cstore_conn_state *)data)->fp);
//      }
//      tcp_markconn(uip_conn, NULL);
//      mmem_free(&connectionState);
//      connectionState.ptr = NULL;
//    }
  }

  PROCESS_END();
}

/** @} */

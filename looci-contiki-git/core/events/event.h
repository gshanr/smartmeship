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
/*
 * event.h
 *
 *  Created on: Jan 18, 2012
 *      Author: root
 */

#ifndef __LOOCI_EVENT_H_
#define __LOOCI_EVENT_H_

#include "loociConstants.h"
#include "peers_pub.h"
#include "mmem.h"
#include <stdbool.h>
#include <stdint.h>



/**
 * @brief Successful
 *
 *        This value is returned by the LooCI Event Bus if an operation
 *        was successful.
 *
 * @hideinitializer
 */
#define LOOCI_SUCCESS 0
/**
 * @brief Event could not be published
 *
 *        This value is returned by the LooCI Event Bus if an event could
 *        not be published successfully.
 * @hideinitializer
 */
#define LOOCI_ERR_EVENTNOTPUBLISHED -1
/** @} */

/**
 * @brief The maximum length of a LooCI event payload.
 *
 *        The payload of a LooCI cannot be larger than
 *        this constant. If a component tries to publish
 *        an event with a larger payload, the passed payload
 *        will be truncated!
 *
 * @hideinitializer
 *
 * @todo is this large enough?
 */
#define LOOCI_EVENT_PAYLOAD_MAXLEN 512

/**
 * Max size of the headers of the event
 */
#define LOOCI_EVENT_MAX_HEADER_LEN 40

typedef struct {
	looci_eventtype_t type;
	char* payload;
	uint16_t len;
}core_looci_event_t;

/**
 * A LooCI event
 */
struct looci_event {
  looci_eventtype_t type;
  peer_id_t source_node;
  peer_id_t dst_node;
  uint8_t source_cid;
  uint8_t dst_cid;
  uint16_t len;
  char header[LOOCI_EVENT_MAX_HEADER_LEN];
  struct mmem payloadMemory;
};

typedef struct looci_event looci_event_t;

bool ev_has_header(looci_event_t* event, uint8_t headerId);

bool ev_get_header(looci_event_t* event, uint8_t headerId, char* buffer, uint8_t* length);

bool ev_add_header(looci_event_t* event, uint8_t headerId, char* buffer, uint8_t length);

uint8_t ev_get_header_len(struct looci_event* event, uint8_t headerId);

bool ev_remove_header(looci_event_t* event, uint8_t headerId);

uint8_t ev_get_totalHeaderSize(char* headers);

bool ev_getHeaderBit(looci_event_t* event, uint8_t headerId, uint8_t byte, uint8_t mask);

uint8_t ev_getHeaderByte(looci_event_t* event, uint8_t headerId, uint8_t byte);

void ev_setHeaderBit(looci_event_t* event, uint8_t headerId, uint8_t byteIndex, uint8_t mask, bool bit);

void ev_setHeaderByte(looci_event_t* event, uint8_t headerId, uint8_t byteIndex, uint8_t new_byte);

/**
 * Fills a byte array with all the data from this event
 *
 * @param event
 * 	the event to be reconstructed
 * @param array
 * 	the source array which contains the event
 */
void ev_fill_event_from_byte_array(unsigned char* array, uint16_t size, looci_event_t* event);

/**
 * Fills a byte array with all the data from this event
 *
 *
 *
 * @param event
 * 	the event to be translated to a byte array
 * @param array
 * 	the target array
 * 	MUST BE AT LEAST LOOCI_MAX_EVENT_LEN + 3 large
 * @return
 * 	the filled length of the byte array
 */
uint16_t ev_fill_byte_array_from_event(looci_event_t* event, unsigned char* array);

bool evpCM(uint8_t compId, uint8_t filterId);

bool evpNM(uint8_t peerId, uint8_t filterId);

bool evpTM(looci_eventtype_t type,  looci_eventtype_t filterType);

#define EV_RETURN_REPLY_MASK 0
#define EV_IS_REPLY_MASK 1

/**
 * Header definitions
 */
#define EVENT_NONE 0
#define EVENT_ANY 0

#define HEADER_PAYLOAD 0
#define HEADER_CMD 1

#define EV_IS_DIRECTED(event) ev_has_header(event,HEADER_CMD)

#define EV_NEEDS_REPLY(event) ev_getHeaderBit(event,HEADER_CMD,1,EV_RETURN_REPLY_MASK)

#define EV_IS_REPLY(event) ev_getHeaderBit(event,HEADER_CMD,1,EV_IS_REPLY_MASK)


#define EV_SET_NEEDS_REPLY(event) ev_setHeaderBit(event,HEADER_CMD,1,EV_RETURN_REPLY_MASK,true)

#define EV_SET_IS_REPLY(event) ev_setHeaderBit(event,HEADER_CMD,1,EV_IS_REPLY_MASK,true)

#define EV_GET_DST_CMP(event) ev_getHeaderByte(event,HEADER_CMD,0)

#define EV_SET_DST_CMP(event,cmp) ev_setHeaderByte(event,HEADER_CMD,0,cmp)

#endif /* EVENT_H_ */

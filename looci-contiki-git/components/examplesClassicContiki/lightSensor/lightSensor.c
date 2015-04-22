#include "contiki.h"
#include "looci.h"
#include "utils.h"
#include "timer_utils.h"
#include <avr/pgmspace.h>
#include "sensors.h"
#include "event-types.h"

#ifdef CONTIKI_TARGET_AVR_RAVEN
#include "raven-msg.h"
#endif

#include <stdint.h>
#include <stdio.h>

#ifdef LOOCI_COMPONENT_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif

struct state{
	struct etimer et;
	int light;
	looci_event_t* event;
};

#define LOOCI_NR_PROPERTIES 0
LOOCI_PROPERTIES();
COMPONENT_INTERFACES(LIGHT_READING);
COMPONENT_RECEPTACLES(ANY_EVENT);
LOOCI_COMPONENT("light sampler", struct state);
COMPONENT_THREAD( ev, data)
{
	COMPONENT_BEGIN(struct state,compState);
	compState->light = 0;
	ETIMER_SET(&compState->et, CLOCK_SECOND * 10);
	while(1) {
		LOOCI_EVENT_RECEIVE_UNTIL(compState->event,
			ETIMER_EXPIRED(compState->et));
		// create dummy value when timer expires
		compState->light = rng();
		PUBLISH_EVENT(LIGHT_READING, (uint8_t)&compState->light,
		 sizeof(compState->light));
		etimer_reset(&compState->et);
	}
	COMPONENT_END();
}

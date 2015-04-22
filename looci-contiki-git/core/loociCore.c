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
#include "contiki.h"
#include "looci.h"
#include "project-conf.h"
#include "introspection_local.h"
#include "runtime_control_local.h"
#include "reconfigurationEngine.h"

#include "smartmesh.h"

#include "eventBus.h"
#include "networking_private.h"
#include "triggermodule.h"

#ifdef LOOCI_ANNOUNCE_BOOT
#include "debug.h"
#else
#include "nodebug.h"
#endif

PROCESS(looci, "Core LooCI");
AUTOSTART_PROCESSES(&looci);
PROCESS_THREAD(looci, ev, data)
{
	PROCESS_BEGIN();

	process_start(&looci_smartmeship, NULL);


	PRINTF("\n===LOOCI BOOT===\n");

#if defined(__AVR_ATmega1284P__)
	PRINTF("--running on raven--");
#elif defined(__AVR_ATmega128RFA1__)
	PRINTF("--running on zigduino--");
#endif

	PROCESS_YIELD();

	//init reconfig engine before boot
	lc_man_init_reconfigEngine(lc_is_func,lc_rc_func);

	//init looci
	looci_init();

#ifdef WITH_IMAGE_INIT
	  PRINT_LN("[INIT] do imange init func");
	  doImageInit();
#endif

	PROCESS_END();
}

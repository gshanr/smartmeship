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
 * @file
 * Implementation for the core LooCI system
 * @author
 * Wouter Horré <wouter.horre@cs.kuleuven.be>
 */
#include "contiki.h"
#include "eventBus.h"
#include "components/component.h"
#include "codebaseManager.h"
#include "componentManager.h"
#include "net/peers.h"
#include "net/networking_private.h"
#include "lib/mmem.h"
#include "triggermodule.h"
#include "sensor-conf.h"
#include "interceptor.h"
#include "timer_utils.h"



#ifdef WITH_LOADABLE_COMPONENTS
#include <stdio.h>
#include "deployment/codebaseDeployment.h"
#endif // WITH_LOADABLE_COMPONENTS


#ifdef LOOCI_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif

#ifdef WITH_RAVEN_GUI
#include "raven-msg.h"
#endif

// Sensors
#include "lib/sensors.h"

void looci_init(){
	  PRINTF("LooCI initializing\r\n");

	  // init managed memory, since no one likes to do it
	  mmem_init();

	  // init peer library
	  peer_init();


	  /* Start sensors process */
	  PRINTF("Starting Sensors process\n");
	  process_start(&sensors_process, NULL);


	  PRINTF("Starting LooCI Event Manager\n");
	  process_start(&looci_event_manager, NULL);

	  PRINTF("Starting LooCI Networking Framework\r\n");
	  process_start(&looci_networking_framework, NULL);




	#ifdef WITH_LOADABLE_COMPONENTS
	  // This one can start after the component manager since it is not used
	  // by components
	  PRINTF("Starting LooCI Component Store\r\n");
	  process_start(&looci_component_store, NULL);
	#endif // WITH_LOADABLE_COMPONENTS



	  PRINTF("Init instance manager");
	  looci_cmpMan_init();

	  // This one is the last one to make sure everything is started
	  // before the components
	  PRINTF("Starting LooCI Component Manager\n");
	  looci_cbMan_init();

	  lc_int_init();

	  PRINT_LN("[INIT] Start trigger mod");
	  lc_init_triggermodule();

	#ifdef WITH_RAVEN_GUI
	  ravenlcd_gui_init();
	#endif


	  PRINTF("LooCI Init complete\n");
}



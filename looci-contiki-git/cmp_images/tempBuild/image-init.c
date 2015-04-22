/*
 * composition-init.c
 *
 *  Created on: Nov 12, 2013
 *      Author: user
 */


#include "project-conf.h"
#include "image-conf.h"
#include "looci.h"
#include "component_type.h"
#include "codebaseManager.h"
#include "componentManager.h"
#include "events_private.h"
#include "event-types.h"



#ifdef LOOCI_IMAGE_INIT_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif


void doImageInit(){
	PRINT_LN("test app: %u",temp_sample_Lc.id);
	//instantiating component
	lc_rc_instantiate_t instantiateCmd;
	instantiateCmd.cbid = temp_sample_Lc.id;
	uint8_t success = looci_cbMan_instantiate_component(&instantiateCmd);
	PRINT_LN("instantiated temp sensor as: %u",instantiateCmd.cid);

	//do activation
	lc_rc_component_t cmpCmd;
	cmpCmd.cid = instantiateCmd.cid;
	success = looci_cmpMan_component_start(&cmpCmd);
	PRINT_LN("succes = %u",success);

	// do subscription
	subs_rem_to subCmd;
	subCmd.src_cmp = instantiateCmd.cid;
	subCmd.dst_nod = PEER_ID_ALL;
	subCmd.type = TEMP_READING;
	events_add_remote_subscription_to(&subCmd);

}




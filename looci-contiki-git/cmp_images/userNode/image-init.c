/*
 * composition-init.c
 *
 *  Created on: Nov 12, 2013
 *      Author: user
 */


#include "looci.h"
#include "component_type.h"
#include "codebaseManager.h"
#include "componentManager.h"
#include "events_private.h"
#include "event-types.h"

#include "project-conf.h"
#include "image-conf.h"

#ifdef LOOCI_IMAGE_INIT_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif

//Declare here the extern structs of

// Enter what needs to be done to initialise
void doImageInit(){
	PRINT_LN("no init needed");

}




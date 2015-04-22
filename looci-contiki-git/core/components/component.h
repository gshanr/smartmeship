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
 * Header file containing the macro definitions for component declaration
 */
#ifndef __LOOCI_COMPONENTS_H__
#define __LOOCI_COMPONENTS_H__

#include <stdlib.h>
#include <stddef.h>
#include <avr/pgmspace.h>
#include "contiki.h"
#include "process.h"
#include "sys/cc.h"
#include "events/loociConstants.h"
#include "events/event.h"
#include "events/eventBus.h"
#include "codebaseManager.h"
#include "component_type.h"
#include "componentManager.h"
#include "utils.h"
#include "component_pub.h"
#include "comp_services.h"

#ifndef offsetof
#define offsetof(a,b) 0
#endif
/**
 * Declare a component thread.
 *
 * @param name The component name (declared with COMPONENT())
 * @param ev
 * @param data
 *
 * @hideinitializer
 */

#define COMP_THREAD_NAME JOIN(process_thread,LOOCI_NAMED(P))
#define COMPONENT_THREAD(ev,data) \
	static PT_THREAD(COMP_THREAD_NAME(struct pt *process_pt,	\
				       process_event_t ev,	\
				       process_data_t data))

#if ! CC_NO_VA_ARGS
/**
 * Declare the interfaces of a component.
 *
 * @param name The name of the component.
 *
 * @param ... The eventtypes the component publishes.
 *
 * @note One of COMPONENT_INTERFACES or COMPONENT_NO_INTERFACES is mandatory! 
 *       The declaration must be done before the component declaration!
 *
 * @hideinitializer
 */
#define COMPONENT_INTERFACES(...) \
  static const looci_eventtype_t LOOCI_NAMED(interfaces)[] PROGMEM= {__VA_ARGS__, EV_END_EVENT };


/**
 * Declare the absence of interfaces on a component.
 *
 * @param name The name of the component.
 *
 * @note One of COMPONENT_INTERFACES or COMPONENT_NO_INTERFACES is mandatory! 
 *       The declaration must be done before the component declaration!
 *
 * @hideinitializer
 */
#define COMPONENT_NO_INTERFACES() \
  static const looci_eventtype_t LOOCI_NAMED(interfaces)[] PROGMEM = { EV_END_EVENT };

/**
 * Declare the receptacles of a component.
 *
 * @param name The name of the component.
 *
 * @param ... The eventtypes the component consumes.
 *
 * @note One of COMPONENT_RECEPTACLES or COMPONENT_NO_RECEPTACLES is mandatory! 
 *       The declaration must be done before the component declaration!
 *
 * @hideinitializer
 */
#define COMPONENT_RECEPTACLES(...) \
  static const looci_eventtype_t LOOCI_NAMED(receptacles)[] PROGMEM= {__VA_ARGS__, EV_END_EVENT};


/**
 * Declare the absence of receptacles on a component.
 *
 * @param name The name of the component.
 *
 * @note One of COMPONENT_RECEPTACLES or COMPONENT_NO_RECEPTACLES is mandatory! 
 *       The declaration must be done before the component declaration!
 *
 * @hideinitializer
 */
#define COMPONENT_NO_RECEPTACLES() \
  static const looci_eventtype_t LOOCI_NAMED(receptacles)[] PROGMEM = { EV_END_EVENT };

#define LOOCI_PROPERTIES(...) \
		static const looci_property_t LOOCI_NAMED(properties)[LOOCI_NR_PROPERTIES] PROGMEM = {__VA_ARGS__}

#else // ! CC_NO_VA_ARGS
#error "C compiler must support __VA_ARGS__ macro"
#endif // ! CC_NO_VA_ARGS

/**
 * Get the type of a component
 *
 * @hideinitializer
 *//**
 * Mark the beginning of a COMPONENT_THREAD
 *
 * @hideinitializer
 */
#define COMPONENT_BEGIN(structName,varName) \
	structName* varName;\
	lcLL((void**)&varName);\
	PROCESS_BEGIN()


#define COMP_INIT_VAR(structName,varName,...) \
	{static structName tempState PROGMEM = {__VA_ARGS__ }; \
		memcpy_F((char*)varName,getFP((void*)&tempState,&LOOCI_NAMED(C)),sizeof(structName));}

/**
 * Mark the end of a COMPONENT_THREAD
 *
 * @hideinitializer
 */
#define COMPONENT_END() \
	PROCESS_END();return PT_ENDED


/**
 * Get the type of a component
 *
 * @hideinitializer
 */

#define CB_TYPE(lc_cb) lc_cb->values->name

#define CMP_TYPE(lc_comp) CB_TYPE(lc_comp->codebase)


#define CB_ID(cb) cb->id

#define CMP_ID(comp) CB_ID(lc_comp->codebase)


#if ! CC_NO_VA_ARGS


#ifdef BUILD_COMPONENT
// We trick contiki by setting our components as autostart processes
// We can then access them by using elfloader_autostart_processes
#define START_LC_COMPONENT(...)\
		struct process* const _ap[] = {__VA_ARGS__}
#define DECLARE_COMPONENT() //noOp
#else
#define START_LC_COMPONENT(...) //noOp
#define DECLARE_COMPONENT()\
	struct component* LOOCI_NAMED(_C) = &LOOCI_NAMED(C)
#endif // BUILD_COMPONENT

/**
 * Declare the components define in a file.
 *
 *temp part that does not need to be here
/
 * @hideinitializer
 */
#define LOOCI_COMP_PRIV(strName,structName,flag,initVar)\
COMPONENT_THREAD(ev,data);\
static uint8_t LOOCI_NAMED(reg)(uint8_t mode);\
static struct component LOOCI_NAMED(C) = {LOOCI_NAMED(reg)};\
static const char LOOCI_NAMED(name)[] PROGMEM = strName;\
struct looci_codebase LOOCI_NAMED(Lc) = {NULL,COMPONENT_ID_NONE, &LOOCI_NAMED(C),flag,sizeof(structName),(void*)initVar,COMP_THREAD_NAME,\
	&LOOCI_NAMED(interfaces),&LOOCI_NAMED(receptacles),&LOOCI_NAMED(name),LOOCI_NR_PROPERTIES,&LOOCI_NAMED(properties)};\
static uint8_t LOOCI_NAMED(reg)(uint8_t mode){return _r(mode,&LOOCI_NAMED(Lc));}\
START_LC_COMPONENT((struct process*)&LOOCI_NAMED(C));\
DECLARE_COMPONENT()

#define LOOCI_COMPONENT(strName,structName)\
	LOOCI_COMP_PRIV(strName,structName,LC_MASK_PROP|LC_MASK_INIT_DESTROY,NULL);

#define LOOCI_COMPONENT_INIT(strName,structName,initVar)\
		LOOCI_COMP_PRIV(strName,structName,LC_MASK_PROP|LC_MASK_INIT_DESTROY,initVar);

#else // ! CC_NO_VA_ARGS
#error "C compiler must support __VA_ARGS__ macro"
#endif // ! CC_NO_VA_ARGS

extern struct component * const autostart_components[];

//////////////
#define USE_LC_PROPERTIES(data,propertyArray,nrProps)\
	data->properties = propertyArray;\
	data->size = nrProps
#define SET_LC_PROPERTY(property,nr,id,var,newSize,newName,type)\
	property[nr].value=var;\
	property[nr].name=newName;\
	property[nr].size=newSize;\
	property[nr].propertyId = id;\
	property[nr].dataType=type

//////////////

#define COMP_FUNCS_INIT PROGMEM static const struct eventList methods[] = {
#define COMP_FUNCS_END(defaultFunc) \
		{0,(comp_func_ft)defaultFunc}}; \
COMPONENT_THREAD(ev, data){\
	return _lcc(ev,data,methods,&LOOCI_NAMED(C));}
#define COMP_FUNC_INIT(func) {PROCESS_LC_INIT,(comp_func_ft)func},
#define COMP_FUNC_DESTROY(func) {PROCESS_LC_DESTROY,(comp_func_ft)func},
#define COMP_FUNC_ACTIVATE(func) {PROCESS_EVENT_INIT,(comp_func_ft)func},
#define COMP_FUNC_DEACTIVATE(func) {PROCESS_EVENT_EXIT,(comp_func_ft)func},
#define COMP_FUNC_TIMER(func) {PROCESS_EVENT_TIMER,(comp_func_ft)func},
#define COMP_FUNC_EVENT(func) {PROCESS_LC_RECEIVE_EVENT,(comp_func_ft)func},
#define COMP_FUNC_GET_PROPERTY(func) {PROCESS_LC_GET_PROPERTY,(comp_func_ft)func},
#define COMP_FUNC_SET_PROPERTY(func) {PROCESS_LC_SET_PROPERTY,(comp_func_ft)func},
#define COMP_FUNC_PROPERTY_IS_SET(func) {PROCESS_LC_PROPERTY_IS_SET,(comp_func_ft)func},
#define COMP_FUNC_SENSOR(func) {PROCESS_EVENT_SENSOR,(comp_func_ft)func},
#define COMP_FUNC_OTHER_EVENT(event,func) {event,(comp_func_ft)func},
#define COMP_FUNC_DEFAULT(func) {PROCESS_LC_INIT,(comp_func_ft)func},
#endif /* __LOOCI_COMPONENTS_H__ */


#define PROP_OK   			0xa0
#define PROP_IS_SET 		0xa1
#define PROP_ILLEGAL_ARG 	0xa2

/** @} */
/** @} */

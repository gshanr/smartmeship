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
 * @File
 * interface of the component manager
 */

#ifndef __COMPONENT_MANAGER_H_
#define __COMPONENT_MANAGER_H_

#include "runtime_control_local.h"
#include "introspection_local.h"
#include "component_type.h"
#include "vector.h"
#include "event.h"

/**
 * Define the max number of components that can run simultaneously
 */
#define MAX_NR_COMPONENTS 20

/**
 * Initiate the instance manager.
 * Should only be called once
 */
void looci_cmpMan_init();

/**
 * Send an event to the given component
 */
void looci_cmpMan_sendEvent(uint8_t cmpId, core_looci_event_t* event);

/**
 * instantiate the given codebase
 */
int8_t looci_cmpMan_instantiateComponent(struct looci_codebase* cmp);

/**
 * destroy all components of the given codebase
 */
int8_t looci_cmpMan_destroyAllComponents(struct looci_codebase* cmp);

/**
 * get the component by process ID
 */
struct looci_comp* looci_cmpMan_component_get_by_process(struct process * process);

/**
 * get the component by component ID
 */
struct looci_comp* looci_cmpMan_component_get(uint8_t cid);

/**
 * get the componentID by process ID
 */
uint8_t looci_cmpMan_get_cmpId_by_process(struct process* process);

/**
 * Start given component
 */
int8_t looci_cmpMan_component_start(lc_rc_component_t* data);

/**
 * Stop given component
 */
int8_t looci_cmpMan_component_stop(lc_rc_component_t* data);

/**
 * Destroy given component
 */
int8_t looci_cmpMan_destroyComponent(lc_rc_component_t* data);

/**
 * get vector of components
 * @arg components
 * 		a pointer to a vector struct pointer. The fucntion will put the pointer to the component vector in the
 * 		struct pointet
 */
int8_t looci_cmpMan_getComponents(struct vector** components);

/**
 * Get the interfaces of the component
 * List of interfaces is put in the eventtype buffer
 */
int8_t looci_cmpMan_getInterfaces(lc_is_eventtype_buffer_t* data);

/**
 * Get the receptacles of the component
 * List of receptacles is put in the eventtype buffer
 */
int8_t looci_cmpMan_getReceptacles(lc_is_eventtype_buffer_t* data);

/**
 * get the component ids of a given codebase id
 */
int8_t looci_cmpMan_getcompids_by_cb_id(lc_is_get_cid_by_cbid_t* data) ;

/**
 * get the codebase id of a given component id
 */
int8_t looci_cmpMan_get_cb_id_of_comp_id(lc_is_get_cbid_of_cid_t* data);

/**
 * get the type of a given component
 */
int8_t looci_cmpMan_get_comp_type(lc_is_get_c_type_t* data) ;

/**
 * get all component ids currently installed on this node
 */
int8_t looci_cmpMan_get_comp_ids(lc_is_get_c_ids_t* data) ;

/**
 * get the state of a given component
 */
int8_t looci_cmpMan_getstate(lc_is_get_state_t* data);

/**
 * get a property of a given component, and a given property number
 */
int8_t looci_cmpMan_getProperty(lc_is_get_property_t* data);

int8_t looci_cmpMan_setProperty(lc_rc_set_property_t* data);

int8_t looci_cmpMan_getProperties(lc_is_get_properties_t* data);

int8_t looci_cmpMan_getPropertyInfo(lc_is_get_property_info_t* data);

int8_t looci_cmpMan_hasReceptacle(uint8_t cid, uint16_t eventId);

int8_t looci_cmpMan_hasInterface(uint8_t cid, uint16_t eventId);

void lcLL(void** ptr);

char _lcc(uint16_t ev, void* data, const struct eventList* list, struct component* cmp);

#endif /* LOOCICMPMGR_H_ */

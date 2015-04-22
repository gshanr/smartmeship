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
 * @addtogroup lib
 * @{
 * @defgroup vector Vector library
 * @{
 */
/**
 * @file
 * Header file for the Vector library
 * @author 
 * Wouter Horré <wouter.horre@cs.kuleuven.be>
 */
#ifndef __LOOCI_LIB_VECTOR_H__
#define __LOOCI_LIB_VECTOR_H__

#include "contiki.h"
#include "mmem.h"
#include <stdbool.h>
#include "sys/cc.h"

/**
 * @name Return values
 * @{
 */
/**
 * Operation completed successful.
 *
 * @hideinitializer
 */
#define VECTOR_OK 1
/**
 * Failure during the execution of the operation.
 *
 * @hideinitializer
 */
#define VECTOR_FAIL 0
/** @} */

/**
 * The data structure for a vector block.
 */
struct vector_block {
  struct vector_block* mem_next;
  char elements[];
};

/**
 * The data structure for a vector.
 */
struct vector {
  uint8_t len;
  uint8_t elementsize;
  uint8_t blocksize;
  struct vector_block* first;
};

struct vector_iter{
	uint8_t index; //next element to give
	struct vector* vector;
	struct vector_block * current;
};



/**
 * @name Application Programming Interface (API)
 * @{
 */

/**
 * Declare a vector.
 *
 * Afterwards 'name' can be used as a vector (i.e. it is of
 * type struct vector).
 *
 * @hideinitializer
 */
#define VECTOR(name, structure,blockSize)\
	static char CC_CONCAT(vector_block_,name) \
  [sizeof(struct vector_block)+sizeof(structure)*blockSize]; \
  static struct vector name = { 0, sizeof(structure), blockSize,(struct vector_block*) CC_CONCAT(vector_block_,name) }

/**
 * Initialize a vector declared with VECTOR()
 *
 * @param vector A pointer to a vector that was previously 
 *               declared with VECTOR()
 */
void vector_init(struct vector * vector);


struct vector_iter vector_getIter(struct vector* vector);

void* vector_nextEl(struct vector_iter*);

void* vector_iterRm(struct vector_iter* iter);

/**
 * Initialize a vector declared with VECTOR()
 *
 * @param vector A pointer to a vector that was previously 
 *               declared with VECTOR()
 */
uint8_t vector_len(struct vector * vector);

/**
 * Add an element to a vector.
 *
 * @param vector A pointer to a vector that was previously 
 *               declared with VECTOR()
 * @param element The element to add.
 *
 * @return VECTOR_OK The element was successfully added
 * @return VECTOR_FAIL The element could not be added
 */
uint8_t vector_add(struct vector * vector, void * element);

/**
 * Get an element from a vector.
 *
 * @param vector A pointer to a vector that was previously 
 *               declared with VECTOR()
 * @param index The index of the element to retrieve. Index starts at zero!
 *
 * @return A pointer to the element at the given index.
 * @return NULL If the index is larger than the length of the vector.
 *
 * @note The returned pointer may become invalid over time (i.e.
 *       it is not guaranteed to stay valid after a protothread/contiki 
 *       process wait).
 */
void * vector_get(struct vector * vector, uint8_t index);

/**
 * Remove an element from a vector.
 *
 * @param vector A pointer to a vector that was previously
 *               declared with VECTOR()
 * @param index The index of the element to remove. Index starts at zero!
 *
 * @note After this operation all pointers to elements of the vector are invalid!!!
 */
void vector_remove(struct vector * vector, uint8_t index);


uint8_t vector_remove_el(struct vector* vector, void* data, uint8_t maxRem);

void* vector_get_el(struct vector* vector, void* data);

/**
 * Create a new empty element to a vector and put a pointer to it in element.
 *
 * @param vector A pointer to a vector that was previously
 *               declared with VECTOR()
 * @return SUCCESS: pointer to new element
 * @return FAILURE : NULL
 */
void* vector_createElement(struct vector * vector);

typedef bool (*vector_filter_ft)(void* el, void* data);

typedef void (*vector_map_ft)(void* el, void* data);

void* vector_filter(struct vector* vector,  vector_filter_ft filter, void* data);

void vector_map(struct vector* vector,vector_map_ft map, void* data);

uint8_t vector_rm_filter(struct vector* vector,  vector_filter_ft filter, void* data, uint8_t maxRem);

void vector_reset(struct vector* vector);
/** @} */

#endif

/** @} */
/** @} */

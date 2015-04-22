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
 * @addtogroup vector
 * @{
 */
/**
 * @file
 * Implementation for the vector library.
 * @author
 * Wouter Horré <wouter.horre@cs.kuleuven.be>
 */

#include "lib/vector.h"

#include <string.h>
#include <stdbool.h>
#include <stdlib.h>

#ifdef LOOCI_VECTOR_DEBUG
#include "debug.h"
#else
#include "nodebug.h"
#endif

static void vector_shrink(struct vector * vector);
static void vector_blockinit(struct vector * vector, struct vector_block * block);
static uint8_t vector_grow(struct vector * vector, struct vector_block * last);


#define NEXT_BLOCK_PTR(block) (struct vector_block *)(block->mem_next)

static void vector_shrink(struct vector * vector)
{
  uint8_t nb_blocks = ((vector->len-1) / vector->blocksize) + 1;

  PRINTF("[V]Shrink %u to %u blocks\r\n", vector->len, nb_blocks);
  if(vector->len == 0) { // nb_blocks probably isn't correct if len == 0
    return;
  }
  // skip to last used block
  struct vector_block * current = vector->first;
  while(nb_blocks > 1) {
    current = NEXT_BLOCK_PTR(current);
    nb_blocks--;
  }
  // now check whether there is an unused block after that one
  if(current->mem_next != NULL) {
    PRINTF("[V]Free block\r\n");
    free(current->mem_next);
    current->mem_next = NULL;
  }
}

static void vector_blockinit(struct vector * vector, struct vector_block * block)
{
  int i;
  for(i=0; i < vector->blocksize * vector->elementsize; ++i) {
    block->elements[i] = 0;
  }
  block->mem_next = NULL;
}

static uint8_t vector_grow(struct vector * vector, struct vector_block * last)
{
  PRINTF("[V]grow %u\r\n", vector->len);
  // try to allocate a successor
  last->mem_next = malloc(sizeof(struct vector_block) + vector->elementsize * vector->blocksize);
  if(last->mem_next == NULL ) {
    // failure -> return FAIL
    PRINTF("[V]grow fail!\r\n");
    return VECTOR_FAIL;
  }
  // succesfully allocated successor
  // update last's next pointer
  // initialize new block
  vector_blockinit(vector, NEXT_BLOCK_PTR(last));
  return VECTOR_OK;
}

void vector_init(struct vector * vector)
{
	vector->len = 0;
	vector_blockinit(vector, vector->first);
}

uint8_t vector_len(struct vector * vector)
{
  return vector->len;
}

void* vector_createElement(struct vector* vector){
	  struct vector_block * block = vector->first;
	  uint8_t len = vector->len;
	  //skip to last block
	  while(block->mem_next != NULL) {
	    block = NEXT_BLOCK_PTR(block);
	    len -= vector->blocksize;
	  }
	  if(len >= vector->blocksize) {
	    // we will have to create a successor and skip to it
	    if(vector_grow(vector, block)==VECTOR_FAIL) {
	      // could not grow
	      return NULL;
	    }
	    // skip to new successor
	    block = (struct vector_block *)NEXT_BLOCK_PTR(block);
	    len -= vector->blocksize;
	  }

	  vector->len++;
	  return (block->elements + vector->elementsize * (len));
}

uint8_t vector_add(struct vector * vector, void * element)
{
	void* newElement = vector_createElement(vector);
	if(newElement != NULL){
		memcpy(newElement,element,vector->elementsize);
		return VECTOR_OK;
	}
	return VECTOR_FAIL;
}

void * vector_get(struct vector * vector, uint8_t index)
{
  if(index >= vector->len) {
    // index too large
    return NULL;
  }
  struct vector_block * block = vector->first;
  while(index >= vector->blocksize) {
    index -= vector->blocksize;
    if(block->mem_next == NULL) {
      // index too large! return NULL -> we shouldn't get here, but you never know
      return NULL;
    }
    block = (struct vector_block *)NEXT_BLOCK_PTR(block);
  }
  return block->elements + vector->elementsize * index;
}

void vector_remove(struct vector * vector, uint8_t index) {
  if(index >= vector->len) { // sanity check
    PRINTF("[V] Trying to remove an element which is not here\r\n");
    return;
  }
  // find correct block
  struct vector_block * block = vector->first;
  while(index >= vector->blocksize) {
    if(block->mem_next == NULL) {
      PRINTF("[V] Index too large, but we shouldn't get here\r\n");
      // index too large! return -> we shouldn't get hre, but you never know
      return;
    }
    block = NEXT_BLOCK_PTR(block);
    index -= vector->blocksize;
  }
  // remove element
  vector->len--;
  PRINTF("[Vector] Reduced length to %u\r\n", vector->len);
  // shift all further elements forward
  do {
    // copy within the block
    while(index < vector->blocksize-1) {
      PRINTF("[V] Shifting element with index %u\r\n", index);
      memcpy(block->elements + (index * vector->elementsize), block->elements + ((index+1) * vector->elementsize), vector->elementsize);
      ++index;
    }
    if(block->mem_next!=NULL) { // if there is a next block
      // copy first element of next block into last on of this block
      struct vector_block * nextblock = NEXT_BLOCK_PTR(block);
      PRINTF("[V] Shifting first element of next block\r\n");
      memcpy(block->elements + (index * vector->elementsize), nextblock->elements, vector->elementsize);
      // reset index and make nextblock current block
      index = 0;
      block = nextblock;
    } else { // if there is no next block
      // set last element to zero
      PRINTF("[V] Zeroing last element of the block\r\n");
      memset(block->elements + (index * vector->elementsize), 0, vector->elementsize);
      block = NULL;
    }
  } while (block != NULL);
  // check whether we can free a block
  if((vector->len % vector->blocksize) == 0) {
    PRINTF("[Vector] Calling shrink\r\n");
    vector_shrink(vector);
  }
}

struct vector_iter vector_getIter(struct vector* vector){
	struct vector_iter iter;
	iter.index = 0;
	iter.vector = vector;
	iter.current = vector->first;
	return iter;
}

void* vector_nextEl(struct vector_iter* iter){
	if(iter->index >= iter->vector->len){
		return NULL;
	} else{
		void* temp = iter->current->elements + ( (iter->index% iter->vector->blocksize) * iter->vector->elementsize);
		iter->index ++;
		if(iter->index% iter->vector->blocksize == 0){
			iter->current = NEXT_BLOCK_PTR(iter->current);
		}
		return temp;
	}
}


void* vector_filter(struct vector* vector,vector_filter_ft filter, void* data){
	struct vector_iter iter = vector_getIter(vector);
	void* el = vector_nextEl(&iter);
	while(el!=NULL){
		if((*filter)(el,data)){
			return el;
		} else{
			el = vector_nextEl(&iter);
		}
	}
	return NULL;
}

void vector_map(struct vector* vector,vector_map_ft map, void* data){
	struct vector_iter iter = vector_getIter(vector);
	void* el = vector_nextEl(&iter);
	while(el!=NULL){
		(*map)(el,data);
		el = vector_nextEl(&iter);
	}
}

uint8_t vector_rm_filter(struct vector* vector,  vector_filter_ft filter, void* data, uint8_t maxRem){
	PRINT_LN("[VECTOR] rm filter");
	struct vector_iter iter = vector_getIter(vector);
	uint8_t nrRem = 0;
	void* el = vector_nextEl(&iter);
	while(el!=NULL && nrRem < maxRem){
		if((*filter)(el,data)){
			PRINT_LN("el_f %u %u",vector->len, iter.index);
			vector_remove(vector,iter.index-1);
			el = vector_get(vector,iter.index-1);
			++nrRem;
		} else{
			PRINT_LN("el_n");
			el = vector_nextEl(&iter);
		}
	}
	return nrRem;
}

typedef struct{
	uint8_t elementSize;
	void* data;
}vector_match_el_t;

static bool vector_match_filter_func(void* element, vector_match_el_t* data){
	return element == data || (memcmp(element,data->data,data->elementSize) == 0);
}

uint8_t vector_remove_el(struct vector* vector, void* data,uint8_t maxRem){
	vector_match_el_t rmData={vector->elementsize,data};
	return vector_rm_filter(vector,(vector_filter_ft)vector_match_filter_func,&rmData,maxRem);
}

void* vector_get_el(struct vector* vector, void* data){
	vector_match_el_t match={vector->elementsize,data};
	return vector_filter(vector,(vector_filter_ft)vector_match_filter_func,&match);
}

static void vector_block_reset(struct vector_block * block){
	struct vector_block* nextBlock = NEXT_BLOCK_PTR(block);
	if(nextBlock != NULL){
		vector_block_reset(nextBlock);
		free(block->mem_next);
	}
}

void vector_reset(struct vector* vector){
	vector_block_reset(vector->first);
	vector->len = 0;
}

/** @} */

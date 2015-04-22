/*
 * Copyright (c) 2005, Swedish Institute of Computer Science
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * This file is part of the Contiki operating system.
 *
 * @(#)$Id: mmem.c,v 1.2 2006/12/22 17:14:06 barner Exp $
 */

/**
 * \addtogroup mmem
 * @{
 */

/**
 * \file
 *         Implementation of the managed memory allocator
 * \author
 *         Adam Dunkels <adam@sics.se>
 * 
 */


#include "mmem.h"
#include "list.h"
#include "contiki-conf.h"
#include <string.h>
#include <stdio.h>

#ifdef MMEM_DEBUG
#include <avr/pgmspace.h>
#define PRINTF(FORMAT,args...) printf_P(PSTR(FORMAT),##args)
#else
#define PRINTF(...)
#endif

#ifdef MMEM_CONF_SIZE
#define MMEM_SIZE MMEM_CONF_SIZE
#else
#define MMEM_SIZE 4096
#endif

LIST(mmemlist);
mmem_t avail_memory;
static char memory[MMEM_SIZE];

/*---------------------------------------------------------------------------*/
/**
 * \brief      Allocate a managed memory block
 * \param m    A pointer to a struct mmem.
 * \param size The size of the requested memory block
 * \return     Non-zero if the memory could be allocated, zero if memory
 *             was not available.
 * \author     Adam Dunkels
 *
 *             This function allocates a chunk of managed memory. The
 *             memory allocated with this function must be deallocated
 *             using the mmem_free() function.
 *
 *             \note This function does NOT return a pointer to the
 *             allocated memory, but a pointer to a structure that
 *             contains information about the managed memory. The
 *             macro MMEM_PTR() is used to get a pointer to the
 *             allocated memory.
 *
 */
mmem_t
mmem_alloc(struct mmem *m, mmem_t size)
{
  /* Check if we have enough memory left for this allocation. */
  if(avail_memory < size) {
	  PRINTF("[MMEM] req %u, failed, %u left",size,avail_memory);
    return 0;
  }

  /* We had enough memory so we add this memory block to the end of
     the list of allocated memory blocks. */
  list_add(mmemlist, m);

  /* Set up the pointer so that it points to the first available byte
     in the memory block. */
  m->ptr = &memory[MMEM_SIZE - avail_memory];

  /* Remember the size of this memory block. */
  m->size = size;

  /* Decrease the amount of available memory. */
  avail_memory -= size;

  /* Return non-zero to indicate that we were able to allocate
     memory. */
  PRINTF("[MMEM] req %u avail: %u \r\n",size,avail_memory);
  return 1;
}
/*---------------------------------------------------------------------------*/
/**
 * \brief      Deallocate a managed memory block
 * \param m    A pointer to the managed memory block
 * \author     Adam Dunkels
 *
 *             This function deallocates a managed memory block that
 *             previously has been allocated with mmem_alloc().
 *
 */
void
mmem_free(struct mmem *m)
{
  struct mmem *n;

  if(m->next != NULL) {
    /* Compact the memory after the allocation that is to be removed
       by moving it downwards. */
    memmove(m->ptr, m->ptr + m->size,
	    MMEM_SIZE-avail_memory - m->size - (m->ptr - (void*)memory));
    
    /* Update all the memory pointers that points to memory that is
       after the allocation that is to be removed. */
    for(n = m->next; n != NULL; n = n->next) {
      n->ptr = (void *)((char *)n->ptr - m->size);
    }
  }
  avail_memory += m->size;

  /* Remove the memory block from the list. */
  list_remove(mmemlist, m);
  PRINTF("[MMEM] release %u avail: %u \r\n",m->size,avail_memory);

}
/*---------------------------------------------------------------------------*/
/**
 * \brief      Initialize the managed memory module
 * \author     Adam Dunkels
 *
 *             This function initializes the managed memory module and
 *             should be called before any other function from the
 *             module.
 *
 */
void
mmem_init(void)
{
  list_init(mmemlist);
  avail_memory = MMEM_SIZE;
}

/*---------------------------------------------------------------------------*/
/**
 * \brief	return the currently available memory
 *
 * 			This function returns the currently available memory in the mmem module.
 */

mmem_t
mmem_freememory(void){
	return avail_memory;
}
/*---------------------------------------------------------------------------*/

/** @} */

/**
 * \addtogroup etimer
 * @{
 */

/**
 * \file
 * Event timer library implementation.
 * \author
 * Adam Dunkels <adam@sics.se>
 */

/*
 * Copyright (c) 2004, Swedish Institute of Computer Science.
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
 * Author: Adam Dunkels <adam@sics.se>
 *
 */

#include "contiki-conf.h"

#include "contiki.h"
#include "sys/ltimer.h"

static struct ltimer *ltimerlist;
static volatile uint8_t tcounter;
static struct rtimer t;

#define DEBUG 1
#if DEBUG
#include <stdio.h>
#define PRINTF(...) printf(__VA_ARGS__)
#else
#define PRINTF(...)
#endif

void ltimer_scheduler(struct rtimer *t, struct ltimer *l,void *ptr){





}


/*---------------------------------------------------------------------------*/
static void
add_timer(struct ltimer *timer,period_t time)
{
  struct ltimer *t;



  if(timer->p != PROCESS_NONE) {
    for(t = ltimerlist; t != NULL; t = t->next) {
      if(t == timer) {
	/* Timer already on list, bail out. */
        timer->p = PROCESS_CURRENT();
        PRINTF("Timer already on the list\n");
	return;
      }
    }
  }

  PRINTF("Process name is %s",PROCESS_NAME_STRING(timer->p));

  /* Timer not on list. */
  timer->p = PROCESS_CURRENT();
  timer->next = ltimerlist;
  timer->period=time;
  ltimerlist = timer;

  //PRINTF("Timer: counted '%u'\n", tcounter++);
}


void ltimer_set(struct ltimer *lt, period_t interval){
	add_timer(&lt,interval);
	rtimer_set(&t,RTIMER_NOW()+(RTIMER_ARCH_SECOND * interval), 1,
			ltimer_scheduler,NULL);
}

void ltimer_init(){
	ltimerlist = NULL;
}


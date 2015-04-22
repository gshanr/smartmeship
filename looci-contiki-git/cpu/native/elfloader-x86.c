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
 * Modified for LooCI.
 *
 * Copyright (c) 2009, Katholieke Universiteit Leuven
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of the Katholieke Universiteit Leuven nor the names of
 *       its contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
 * @(#)$Id: elfloader-x86.c,v 1.3 2009/02/27 14:28:02 nvt-se Exp $
 */
#include "elfloader-arch.h"
#include <sys/mman.h>
#include <fcntl.h>
#include <stdio.h>

#define R_386_NONE          0
#define R_386_32            1
#define R_386_PC32          2
#define R_386_GOT32         3
#define R_386_PLT32         4
#define R_386_COPY          5
#define R_386_GLOB_DATA     6
#define R_386_JMP_SLOT      7
#define R_386_RELATIVE      8
#define R_386_GOTOFF        9
#define R_386_GOTPC         10

#define ELF32_R_TYPE(info)      ((unsigned char)(info))

static char datamemory[ELFLOADER_SLOTS][ELFLOADER_DATAMEMORY_SIZE];

/*---------------------------------------------------------------------------*/
symbol_addr_t
elfloader_arch_allocate_ram(int size, uint8_t slot)
{
  if(size > ELFLOADER_DATAMEMORY_SIZE || slot >= ELFLOADER_SLOTS) { 
    return (symbol_addr_t)NULL;
  } else {
    return (symbol_addr_t)datamemory[slot];
  }
}
/*---------------------------------------------------------------------------*/
symbol_addr_t
elfloader_arch_allocate_rom(int size, uint8_t slot)
{
  int fd = open("/dev/zero", O_RDWR);
  char *mem = mmap(0, ELFLOADER_TEXTMEMORY_SIZE, PROT_WRITE | PROT_EXEC, MAP_PRIVATE, fd, 0);
  return (symbol_addr_t)mem;
}
/*---------------------------------------------------------------------------*/
void
elfloader_arch_write_rom(int fd, unsigned short textoff, unsigned int size, symbol_addr_t mem)
{
  cfs_seek(fd, textoff, CFS_SEEK_SET);
  cfs_read(fd, (unsigned char *)mem, size);
}
/*---------------------------------------------------------------------------*/
void
elfloader_arch_relocate(int fd, unsigned int sectionoffset, symbol_addr_t sectionaddress2, 
			struct elf32_rela *rela, symbol_addr_t addr2)
{
  unsigned int type;
  char * sectionaddress = (char *)sectionaddress2;
  char * addr = (char *) addr2;
  
  /* 
     Given value addr is S
     
     S = runtime address of destination = addr
     A = rela->r_addend 
     P = absolute address of relocation (section base address and rela->r_offset)
  */
  
  type = ELF32_R_TYPE(rela->r_info);
  
  switch(type) {
  case R_386_NONE:
  case R_386_COPY:
    /* printf("elfloader-x86.c: relocation calculation completed (none) %d\r\n", type); */
    break;
  case R_386_32:
    addr += rela->r_addend; /* +A */
    
    cfs_seek(fd, sectionoffset + rela->r_offset, CFS_SEEK_SET);
    cfs_write(fd, (char *)&addr, 4);
    /*printf("elfloader-x86.c: performed relocation type S + A (%d)\r\n", type);*/
    break;
  case R_386_PC32:
    addr -= (sectionaddress + rela->r_offset); /* -P */
    addr += rela->r_addend; /* +A */
    
    cfs_seek(fd, sectionoffset + rela->r_offset, CFS_SEEK_SET);
    cfs_write(fd, (char *)&addr, 4);
    /*printf("elfloader-x86.c: performed relocation type S + A - P (%d)\r\n", type);*/
    break;
  case R_386_GOT32:
    printf("elfloader-x86.c: unsupported relocation type G + A - P (%d)\r\n", type);
    break;
  case R_386_PLT32:
    printf("elfloader-x86.c: unsupported relocation type L + A - P (%d)\r\n", type);
    break;
  case R_386_GLOB_DATA:
  case R_386_JMP_SLOT:
    printf("elfloader-x86.c: unsupported relocation type S (%d)\r\n", type);
    break;
  case R_386_RELATIVE:
    printf("elfloader-x86.c: unsupported relocation type B + A (%d)\r\n", type);
    break;
  case R_386_GOTOFF:
    printf("elfloader-x86.c: unsupported relocation type S + A - GOT (%d)\r\n", type);
    break;
  case R_386_GOTPC:
    printf("elfloader-x86.c: unsupported relocation type GOT + A - P (%d)\r\n", type);
    break;
  default:
    printf("elfloader-x86.c: unknown type (%d)\r\n", type);
    break;
  }
  
}
/*---------------------------------------------------------------------------*/

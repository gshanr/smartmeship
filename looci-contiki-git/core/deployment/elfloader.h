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
 * \addtogroup loader
 * @{
 */

/**
 * \defgroup elfloader The Contiki ELF loader
 *
 * The Contiki ELF loader links, relocates, and loads ELF
 * (Executable Linkable Format) object files into a running Contiki
 * system.
 *
 * ELF is a standard format for relocatable object code and executable
 * files. ELF is the standard program format for Linux, Solaris, and
 * other operating systems.
 *
 * An ELF file contains either a standalone executable program or a
 * program module. The file contains both the program code, the
 * program data, as well as information about how to link, relocate,
 * and load the program into a running system.
 *
 * The ELF file is composed of a set of sections. The sections contain
 * program code, data, or relocation information, but can also contain
 * debugging information.
 *
 * To link and relocate an ELF file, the Contiki ELF loader first
 * parses the ELF file structure to find the appropriate ELF
 * sections. It then allocates memory for the program code and data in
 * ROM and RAM, respectively. After allocating memory, the Contiki ELF
 * loader starts relocating the code found in the ELF file.
 *
 * @{
 */

/**
 * \file
 *         Header file for the Contiki ELF loader.
 * \author
 *         Adam Dunkels <adam@sics.se>
 *
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
 * @(#)$Id: elfloader.h,v 1.3 2008/01/24 13:09:33 adamdunkels Exp $
 */
#ifndef __ELFLOADER_H__
#define __ELFLOADER_H__

#include "cfs/cfs.h"

/**
 * Return value from elfloader_load() indicating that loading worked.
 */
#define ELFLOADER_OK                  0
/**
 * Return value from elfloader_load() indicating that the ELF file had
 * a bad header.
 */
#define ELFLOADER_BAD_ELF_HEADER      1
/**
 * Return value from elfloader_load() indicating that no symbol table
 * could be find in the ELF file.
 */
#define ELFLOADER_NO_SYMTAB           2
/**
 * Return value from elfloader_load() indicating that no string table
 * could be find in the ELF file.
 */
#define ELFLOADER_NO_STRTAB           3
/**
 * Return value from elfloader_load() indicating that the size of the
 * .text segment was zero.
 */
#define ELFLOADER_NO_TEXT             4
/**
 * Return value from elfloader_load() indicating that a symbol
 * specific symbol could not be found.
 *
 * If this value is returned from elfloader_load(), the symbol has
 * been copied into the elfloader_unknown[] array.
 */
#define ELFLOADER_SYMBOL_NOT_FOUND    5
/**
 * Return value from elfloader_load() indicating that one of the
 * required segments (.data, .bss, or .text) could not be found.
 */
#define ELFLOADER_SEGMENT_NOT_FOUND   6
/**
 * Return value from elfloader_load() indicating that no starting
 * point could be found in the loaded module.
 */
#define ELFLOADER_NO_STARTPOINT       7
/**
 * Return value from elfloader_load() indicating that no slot is
 * available.
 */
#define ELFLOADER_NO_SLOT             8
/**
 * Return value from elfloader_load() indicating that no text or data
 * memory could be allocated.
 */
#define ELFLOADER_ALLOCATE_ERROR      9

/**
 * elfloader initialization function.
 *
 * This function should be called at boot up to initilize the elfloader.
 */
void elfloader_init(void);

/**
 * \brief      Load and relocate an ELF file.
 * \param fd   An open CFS file descriptor.
 * \param slot A pointer to a variable that will hold the allocated slot after
 *             the call. Can be passed to elfloader_unload later.
 * \return     ELFLOADER_OK if loading and relocation worked.
 *             Otherwise an error value.
 *
 *             This function loads and relocates an ELF file. The ELF
 *             file must have been opened with cfs_open() prior to
 *             calling this function.
 *
 *             If the function is able to load the ELF file, a pointer
 *             to the process structure in the model is stored in the
 *             elfloader_loaded_process variable.
 *
 * \note       This function modifies the ELF file opened with cfs_open()!
 *             If the contents of the file is required to be intact,
 *             the file must be backed up first.
 *
 */
int elfloader_load(int fd, uint8_t * slot);

void elfloader_unload(uint8_t slot);

#ifdef ELFLOADER_CONF_SLOTS
#define ELFLOADER_SLOTS ELFLOADER_CONF_SLOTS
#else
#define ELFLOADER_SLOTS 8
#endif

#if ELFLOADER_SLOTS > 8
#undef ELFLOADER_SLOTS
#define ELFLOADER_SLOTS 8
#endif

/**
 * A pointer to the processes loaded with elfloader_load().
 */
extern void* elfloader_autostart_processes[ELFLOADER_SLOTS];

/**
 * If elfloader_load() could not find a specific symbol, it is copied
 * into this array.
 */

#if ELFLOAD_DEBUG
extern char elfloader_unknown[30];
#endif

#ifdef ELFLOADER_CONF_DATAMEMORY_SIZE
#define ELFLOADER_DATAMEMORY_SIZE ELFLOADER_CONF_DATAMEMORY_SIZE
#else
#define ELFLOADER_DATAMEMORY_SIZE 0x030
#endif

#ifdef ELFLOADER_CONF_TEXTMEMORY_SIZE
#define ELFLOADER_TEXTMEMORY_SIZE ELFLOADER_CONF_TEXTMEMORY_SIZE
#else
#define ELFLOADER_TEXTMEMORY_SIZE 0x800
#endif



typedef unsigned short  elf32_word;
typedef   signed short  elf32_sword;
typedef unsigned short elf32_half;
typedef unsigned short  elf32_off;
typedef unsigned short  elf32_addr;

struct elf32_rela {
  elf32_addr      r_offset;       /* Location to be relocated. */
  elf32_word      r_info;         /* Relocation type and symbol index. */
  elf32_sword     r_addend;       /* Addend. */
};


#endif /* __ELFLOADER_H__ */

/** @} */
/** @} */

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
#include <stdint.h>

/**
 * Encode the input buffer from binary to hex
 *
 * @param in __do_copy_data
 * 	the input buffer : contains binary
 * @param out
 * 	the output buffer: will contain hex
 * @param length
 * 	the lenght of bytes to be encoded to hex
 * @return
 * 	the lenght of data put in out-buffer (double of the lenght that went in)
 */
uint16_t xEnc(const unsigned char *in, unsigned char *out, uint16_t len);

/**
 * Decode the input buffer from hex to bin
 *
 * @param in
 * 	the input buffer : contains hex
 * @param out
 * 	the output buffer: will contain bin
 * @param length
 * 	the lenght of bytes to be decoded from hex: should be a par number
 * @return
 * 	the lenght of data put in out-buffer(half of the lenght that went in)
 */
uint16_t xDec(const unsigned char *in, unsigned char *out, uint16_t len);

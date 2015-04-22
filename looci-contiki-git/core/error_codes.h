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
 * error_codes.h
 *
 *  Created on: Nov 21, 2012
 *      Author: root
 */

#ifndef ERROR_CODES_H_
#define ERROR_CODES_H_


#define MSG_SUCCESS_CONTINUE 2
#define MSG_SUCCESS 1
#define ERROR_FAILURE 0
#define ERROR_CB_NOT_FOUND -1
#define ERROR_CMP_NOT_FOUND -2
#define ERROR_ILLEGAL_ARG -3
#define ERROR_NO_MEMORY -4
#define ERROR_PARAMETER_NOT_FOUND -5
#define ERROR_WIRE_NOT_FOUND -6
#define ERROR_WIRE_DUPLICATE -7
#define ERROR_TIMEOUT -8
#define ERROR_PROVIDED_INTERFACE_NOT_FOUND -9
#define ERROR_REQUIRED_INTERFACE_NOT_FOUND -10
#define ERROR_ILLEGAL_STATE -11

#define LC_SUCCESS 1
#define LC_FAILURE 0

#endif /* ERROR_CODES_H_ */

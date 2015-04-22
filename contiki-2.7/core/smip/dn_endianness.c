/*
Copyright (c) 2014, Dust Networks. All rights reserved.

\license See attached DN_LICENSE.txt.
*/



#include "dn_common.h"

//=========================== defined =========================================

//=========================== typedef =========================================

//=========================== variables =======================================

//=========================== prototypes ======================================

void dn_write_uint16_t(uint8_t* ptr, uint16_t val) {
   // arduino Due is a little-endian platform
   ptr[0]     = (val>>8)  & 0xff;
   ptr[1]     = (val>>0)  & 0xff;
}

void dn_write_uint32_t(uint8_t* ptr, uint32_t val) {
   // arduino Due is a little-endian platform
   ptr[0]     = (val>>24) & 0xff;
   ptr[1]     = (val>>16) & 0xff;
   ptr[2]     = (val>>8)  & 0xff;
   ptr[3]     = (val>>0)  & 0xff;
}

void dn_read_uint16_t(uint16_t* to, uint8_t* from) {
   // arduino Due is a little endian platform
   *to        = 0;
   *to       |= (from[1]<<0);
   *to       |= (from[0]<<8);
}
void dn_read_uint32_t(uint32_t* to, uint8_t* from) {
   // arduino Due is a little endian platform
   *to        = 0;
   *to       |= (from[3]<<0);
   *to       |= (from[2]<<8);
   *to       |= ((uint32_t)from[1]<<16);
   *to       |= ((uint32_t)from[0]<<24);
}

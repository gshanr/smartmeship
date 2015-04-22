#include "symbols.h"
#include <avr/pgmspace.h>
static const char s___divmodhi4 [] PROGMEM = "__divmodhi4";
static const char s___divmodsi4 [] PROGMEM = "__divmodsi4";
static const char s___negsi2 [] PROGMEM = "__negsi2";
static const char s___udivmodhi4 [] PROGMEM = "__udivmodhi4";
static const char s___udivmodqi4 [] PROGMEM = "__udivmodqi4";
static const char s___udivmodsi4 [] PROGMEM = "__udivmodsi4";
static const char s___ultoa_invert [] PROGMEM = "__ultoa_invert";
static const char s___umulhisi3 [] PROGMEM = "__umulhisi3";
static const char s__call [] PROGMEM = "_call";
static const char s__div [] PROGMEM = "_div";
static const char s__lcc [] PROGMEM = "_lcc";
static const char s__lpt [] PROGMEM = "_lpt";
static const char s__p [] PROGMEM = "_p";
static const char s__r [] PROGMEM = "_r";
static const char s__tf [] PROGMEM = "_tf";
static const char s_api_bindSocket [] PROGMEM = "api_bindSocket";
static const char s_api_bindSocket_reply [] PROGMEM = "api_bindSocket_reply";
static const char s_api_closeSocket [] PROGMEM = "api_closeSocket";
static const char s_api_closeSocket_reply [] PROGMEM = "api_closeSocket_reply";
static const char s_api_getMoteStatus [] PROGMEM = "api_getMoteStatus";
static const char s_api_getMoteStatus_reply [] PROGMEM = "api_getMoteStatus_reply";
static const char s_api_getServiceInfo [] PROGMEM = "api_getServiceInfo";
static const char s_api_getServiceInfo_reply [] PROGMEM = "api_getServiceInfo_reply";
static const char s_api_get_ipv6addr [] PROGMEM = "api_get_ipv6addr";
static const char s_api_get_ipv6addr_reply [] PROGMEM = "api_get_ipv6addr_reply";
static const char s_api_join [] PROGMEM = "api_join";
static const char s_api_join_reply [] PROGMEM = "api_join_reply";
static const char s_api_openSocket [] PROGMEM = "api_openSocket";
static const char s_api_openSocket_reply [] PROGMEM = "api_openSocket_reply";
static const char s_api_requestService [] PROGMEM = "api_requestService";
static const char s_api_requestService_reply [] PROGMEM = "api_requestService_reply";
static const char s_api_sendTo [] PROGMEM = "api_sendTo";
static const char s_api_sendTo_reply [] PROGMEM = "api_sendTo_reply";
static const char s_api_timedout [] PROGMEM = "api_timedout";
static const char s_app_vars [] PROGMEM = "app_vars";
static const char s_chunk [] PROGMEM = "chunk";
static const char s_clock_delay_msec [] PROGMEM = "clock_delay_msec";
static const char s_clock_init [] PROGMEM = "clock_init";
static const char s_clock_seconds [] PROGMEM = "clock_seconds";
static const char s_clock_time [] PROGMEM = "clock_time";
static const char s_config_timeout [] PROGMEM = "config_timeout";
static const char s_deployReply [] PROGMEM = "deployReply";
static const char s_dn_ipmt_bindSocket [] PROGMEM = "dn_ipmt_bindSocket";
static const char s_dn_ipmt_bindSocket_reply [] PROGMEM = "dn_ipmt_bindSocket_reply";
static const char s_dn_ipmt_cancelTx [] PROGMEM = "dn_ipmt_cancelTx";
static const char s_dn_ipmt_closeSocket [] PROGMEM = "dn_ipmt_closeSocket";
static const char s_dn_ipmt_closeSocket_reply [] PROGMEM = "dn_ipmt_closeSocket_reply";
static const char s_dn_ipmt_getParameter_ipv6Address [] PROGMEM = "dn_ipmt_getParameter_ipv6Address";
static const char s_dn_ipmt_getParameter_ipv6Address_reply [] PROGMEM = "dn_ipmt_getParameter_ipv6Address_reply";
static const char s_dn_ipmt_getParameter_moteStatus [] PROGMEM = "dn_ipmt_getParameter_moteStatus";
static const char s_dn_ipmt_getParameter_moteStatus_reply [] PROGMEM = "dn_ipmt_getParameter_moteStatus_reply";
static const char s_dn_ipmt_getServiceInfo [] PROGMEM = "dn_ipmt_getServiceInfo";
static const char s_dn_ipmt_getServiceInfo_reply [] PROGMEM = "dn_ipmt_getServiceInfo_reply";
static const char s_dn_ipmt_init [] PROGMEM = "dn_ipmt_init";
static const char s_dn_ipmt_join [] PROGMEM = "dn_ipmt_join";
static const char s_dn_ipmt_join_reply [] PROGMEM = "dn_ipmt_join_reply";
static const char s_dn_ipmt_openSocket [] PROGMEM = "dn_ipmt_openSocket";
static const char s_dn_ipmt_openSocket_reply [] PROGMEM = "dn_ipmt_openSocket_reply";
static const char s_dn_ipmt_receive_notif [] PROGMEM = "dn_ipmt_receive_notif";
static const char s_dn_ipmt_requestService [] PROGMEM = "dn_ipmt_requestService";
static const char s_dn_ipmt_requestService_reply [] PROGMEM = "dn_ipmt_requestService_reply";
static const char s_dn_ipmt_rxSerialRequest [] PROGMEM = "dn_ipmt_rxSerialRequest";
static const char s_dn_ipmt_sendTo [] PROGMEM = "dn_ipmt_sendTo";
static const char s_dn_ipmt_sendTo_reply [] PROGMEM = "dn_ipmt_sendTo_reply";
static const char s_dn_ipmt_vars [] PROGMEM = "dn_ipmt_vars";
static const char s_dn_read_uint16_t [] PROGMEM = "dn_read_uint16_t";
static const char s_dn_read_uint32_t [] PROGMEM = "dn_read_uint32_t";
static const char s_dn_serial_mt_dispatch_response [] PROGMEM = "dn_serial_mt_dispatch_response";
static const char s_dn_serial_mt_init [] PROGMEM = "dn_serial_mt_init";
static const char s_dn_serial_mt_rxHdlcFrame [] PROGMEM = "dn_serial_mt_rxHdlcFrame";
static const char s_dn_serial_mt_sendRequest [] PROGMEM = "dn_serial_mt_sendRequest";
static const char s_dn_serial_mt_vars [] PROGMEM = "dn_serial_mt_vars";
static const char s_dn_serial_sendReply [] PROGMEM = "dn_serial_sendReply";
static const char s_dn_uart_init [] PROGMEM = "dn_uart_init";
static const char s_dn_uart_txByte [] PROGMEM = "dn_uart_txByte";
static const char s_dn_write_uint16_t [] PROGMEM = "dn_write_uint16_t";
static const char s_dn_write_uint32_t [] PROGMEM = "dn_write_uint32_t";
static const char s_doImageInit [] PROGMEM = "doImageInit";
static const char s_ds6_neighbors [] PROGMEM = "ds6_neighbors";
static const char s_error_check [] PROGMEM = "error_check";
static const char s_evpCM [] PROGMEM = "evpCM";
static const char s_evpNM [] PROGMEM = "evpNM";
static const char s_evpTM [] PROGMEM = "evpTM";
static const char s_fptr [] PROGMEM = "fptr";
static const char s_framer_802154 [] PROGMEM = "framer_802154";
static const char s_getFP [] PROGMEM = "getFP";
static const char s_htonsa [] PROGMEM = "htonsa";
static const char s_lcLL [] PROGMEM = "lcLL";
static const char s_lcpEvPub [] PROGMEM = "lcpEvPub";
static const char s_ldbuf [] PROGMEM = "ldbuf";
static const char s_ledtimer [] PROGMEM = "ledtimer";
static const char s_memcmp [] PROGMEM = "memcmp";
static const char s_memcpy [] PROGMEM = "memcpy";
static const char s_memcpy_F [] PROGMEM = "memcpy_F";
static const char s_memcpy_P [] PROGMEM = "memcpy_P";
static const char s_memmove [] PROGMEM = "memmove";
static const char s_memset [] PROGMEM = "memset";
static const char s_moteinit [] PROGMEM = "moteinit";
static const char s_nbr_table_add_lladdr [] PROGMEM = "nbr_table_add_lladdr";
static const char s_nbr_table_get_from_lladdr [] PROGMEM = "nbr_table_get_from_lladdr";
static const char s_nbr_table_get_lladdr [] PROGMEM = "nbr_table_get_lladdr";
static const char s_nbr_table_head [] PROGMEM = "nbr_table_head";
static const char s_nbr_table_next [] PROGMEM = "nbr_table_next";
static const char s_nbr_table_register [] PROGMEM = "nbr_table_register";
static const char s_nbr_table_remove [] PROGMEM = "nbr_table_remove";
static const char s_notifCbmt [] PROGMEM = "notifCbmt";
static const char s_printComponents [] PROGMEM = "printComponents";
static const char s_printf [] PROGMEM = "printf";
static const char s_printf_P [] PROGMEM = "printf_P";
static const char s_puts [] PROGMEM = "puts";
static const char s_rand [] PROGMEM = "rand";
static const char s_rand_r [] PROGMEM = "rand_r";
static const char s_readdata [] PROGMEM = "readdata";
static const char s_reconfig_Lc [] PROGMEM = "reconfig_Lc";
static const char s_replyCbmt [] PROGMEM = "replyCbmt";
static const char s_ringbuf_get [] PROGMEM = "ringbuf_get";
static const char s_ringbuf_put [] PROGMEM = "ringbuf_put";
static const char s_rng [] PROGMEM = "rng";
static const char s_rng_get_uint8 [] PROGMEM = "rng_get_uint8";
static const char s_rxpayload [] PROGMEM = "rxpayload";
static const char s_seconds [] PROGMEM = "seconds";
static const char s_sensors [] PROGMEM = "sensors";
static const char s_sensors_flags [] PROGMEM = "sensors_flags";
static const char s_sensors_process [] PROGMEM = "sensors_process";
static const char s_serial_line_event_message [] PROGMEM = "serial_line_event_message";
static const char s_serial_line_input_byte [] PROGMEM = "serial_line_input_byte";
static const char s_serial_line_process [] PROGMEM = "serial_line_process";
static const char s_shortLen_F [] PROGMEM = "shortLen_F";
static const char s_shortcmp_F [] PROGMEM = "shortcmp_F";
static const char s_size [] PROGMEM = "size";
static const char s_sleepseconds [] PROGMEM = "sleepseconds";
static const char s_smreceive [] PROGMEM = "smreceive";
static const char s_strLen_F [] PROGMEM = "strLen_F";
static const char s_strcmp [] PROGMEM = "strcmp";
static const char s_strcmp_F [] PROGMEM = "strcmp_F";
static const char s_strcmp_P [] PROGMEM = "strcmp_P";
static const char s_strcpy_F [] PROGMEM = "strcpy_F";
static const char s_strncmp [] PROGMEM = "strncmp";
static const char s_strnlen [] PROGMEM = "strnlen";
static const char s_strnlen_P [] PROGMEM = "strnlen_P";
static const char s_temp_sample_Lc [] PROGMEM = "temp_sample_Lc";
static const char s_vprintf [] PROGMEM = "vprintf";
static const char s_xEnc [] PROGMEM = "xEnc";
PROGMEM const struct symbols symbols[] = {
{(const char*)s___divmodhi4, (symbol_addr_t)0x0000ea48},
{(const char*)s___divmodsi4, (symbol_addr_t)0x0000eab2},
{(const char*)s___negsi2, (symbol_addr_t)0x0000eada},
{(const char*)s___udivmodhi4, (symbol_addr_t)0x0000ea20},
{(const char*)s___udivmodqi4, (symbol_addr_t)0x0000ea08},
{(const char*)s___udivmodsi4, (symbol_addr_t)0x0000ea6e},
{(const char*)s___ultoa_invert, (symbol_addr_t)0x0000e94c},
{(const char*)s___umulhisi3, (symbol_addr_t)0x0000eb92},
{(const char*)s__call, (symbol_addr_t)0x0000250e},
{(const char*)s__div, (symbol_addr_t)0x0000ea48},
{(const char*)s__lcc, (symbol_addr_t)0x000025c8},
{(const char*)s__lpt, (symbol_addr_t)0x000036d8},
{(const char*)s__p, (symbol_addr_t)0x00002c3a},
{(const char*)s__r, (symbol_addr_t)0x0000195e},
{(const char*)s__tf, (symbol_addr_t)0x000037bc},
{(const char*)s_api_bindSocket, (symbol_addr_t)0x00003e40},
{(const char*)s_api_bindSocket_reply, (symbol_addr_t)0x00003afa},
{(const char*)s_api_closeSocket, (symbol_addr_t)0x00003f0c},
{(const char*)s_api_closeSocket_reply, (symbol_addr_t)0x00003aa4},
{(const char*)s_api_getMoteStatus, (symbol_addr_t)0x00003cda},
{(const char*)s_api_getMoteStatus_reply, (symbol_addr_t)0x00003c0c},
{(const char*)s_api_getServiceInfo, (symbol_addr_t)0x00003d9a},
{(const char*)s_api_getServiceInfo_reply, (symbol_addr_t)0x00003c58},
{(const char*)s_api_get_ipv6addr, (symbol_addr_t)0x00003ef6},
{(const char*)s_api_get_ipv6addr_reply, (symbol_addr_t)0x00003b10},
{(const char*)s_api_join, (symbol_addr_t)0x00003d70},
{(const char*)s_api_join_reply, (symbol_addr_t)0x00003ab2},
{(const char*)s_api_openSocket, (symbol_addr_t)0x00003e14},
{(const char*)s_api_openSocket_reply, (symbol_addr_t)0x00003ad0},
{(const char*)s_api_requestService, (symbol_addr_t)0x00003dca},
{(const char*)s_api_requestService_reply, (symbol_addr_t)0x00003aba},
{(const char*)s_api_sendTo, (symbol_addr_t)0x00003e76},
{(const char*)s_api_sendTo_reply, (symbol_addr_t)0x00003b2c},
{(const char*)s_api_timedout, (symbol_addr_t)0x00003d04},
{(const char*)s_app_vars, (symbol_addr_t)0x00802d87},
{(const char*)s_chunk, (symbol_addr_t)0x00802f5f},
{(const char*)s_clock_delay_msec, (symbol_addr_t)0x00007436},
{(const char*)s_clock_init, (symbol_addr_t)0x000073ca},
{(const char*)s_clock_seconds, (symbol_addr_t)0x00007402},
{(const char*)s_clock_time, (symbol_addr_t)0x000073ea},
{(const char*)s_config_timeout, (symbol_addr_t)0x00802eb6},
{(const char*)s_deployReply, (symbol_addr_t)0x00802d75},
{(const char*)s_dn_ipmt_bindSocket, (symbol_addr_t)0x00006ad4},
{(const char*)s_dn_ipmt_bindSocket_reply, (symbol_addr_t)0x000066bc},
{(const char*)s_dn_ipmt_cancelTx, (symbol_addr_t)0x00006892},
{(const char*)s_dn_ipmt_closeSocket, (symbol_addr_t)0x00006a8c},
{(const char*)s_dn_ipmt_closeSocket_reply, (symbol_addr_t)0x00006690},
{(const char*)s_dn_ipmt_getParameter_ipv6Address, (symbol_addr_t)0x000068e6},
{(const char*)s_dn_ipmt_getParameter_ipv6Address_reply, (symbol_addr_t)0x00006714},
{(const char*)s_dn_ipmt_getParameter_moteStatus, (symbol_addr_t)0x00006898},
{(const char*)s_dn_ipmt_getParameter_moteStatus_reply, (symbol_addr_t)0x00006768},
{(const char*)s_dn_ipmt_getServiceInfo, (symbol_addr_t)0x000069ee},
{(const char*)s_dn_ipmt_getServiceInfo_reply, (symbol_addr_t)0x000067ee},
{(const char*)s_dn_ipmt_init, (symbol_addr_t)0x00006858},
{(const char*)s_dn_ipmt_join, (symbol_addr_t)0x00006934},
{(const char*)s_dn_ipmt_join_reply, (symbol_addr_t)0x000065fa},
{(const char*)s_dn_ipmt_openSocket, (symbol_addr_t)0x00006a44},
{(const char*)s_dn_ipmt_openSocket_reply, (symbol_addr_t)0x00006652},
{(const char*)s_dn_ipmt_receive_notif, (symbol_addr_t)0x00802d85},
{(const char*)s_dn_ipmt_requestService, (symbol_addr_t)0x00006978},
{(const char*)s_dn_ipmt_requestService_reply, (symbol_addr_t)0x00006626},
{(const char*)s_dn_ipmt_rxSerialRequest, (symbol_addr_t)0x00006430},
{(const char*)s_dn_ipmt_sendTo, (symbol_addr_t)0x00006b22},
{(const char*)s_dn_ipmt_sendTo_reply, (symbol_addr_t)0x000066e8},
{(const char*)s_dn_ipmt_vars, (symbol_addr_t)0x00802f90},
{(const char*)s_dn_read_uint16_t, (symbol_addr_t)0x00006be6},
{(const char*)s_dn_read_uint32_t, (symbol_addr_t)0x00006c04},
{(const char*)s_dn_serial_mt_dispatch_response, (symbol_addr_t)0x0000cf60},
{(const char*)s_dn_serial_mt_init, (symbol_addr_t)0x0000ce48},
{(const char*)s_dn_serial_mt_rxHdlcFrame, (symbol_addr_t)0x0000cf90},
{(const char*)s_dn_serial_mt_sendRequest, (symbol_addr_t)0x0000ce74},
{(const char*)s_dn_serial_mt_vars, (symbol_addr_t)0x00803832},
{(const char*)s_dn_serial_sendReply, (symbol_addr_t)0x0000cf0a},
{(const char*)s_dn_uart_init, (symbol_addr_t)0x00003cb6},
{(const char*)s_dn_uart_txByte, (symbol_addr_t)0x00003cd2},
{(const char*)s_dn_write_uint16_t, (symbol_addr_t)0x00006bd2},
{(const char*)s_dn_write_uint32_t, (symbol_addr_t)0x00006bda},
{(const char*)s_doImageInit, (symbol_addr_t)0x00001362},
{(const char*)s_ds6_neighbors, (symbol_addr_t)0x00800300},
{(const char*)s_error_check, (symbol_addr_t)0x00003ca8},
{(const char*)s_evpCM, (symbol_addr_t)0x0000281e},
{(const char*)s_evpNM, (symbol_addr_t)0x00002832},
{(const char*)s_evpTM, (symbol_addr_t)0x00002846},
{(const char*)s_fptr, (symbol_addr_t)0x00802f5a},
{(const char*)s_framer_802154, (symbol_addr_t)0x00800b40},
{(const char*)s_getFP, (symbol_addr_t)0x000036ac},
{(const char*)s_htonsa, (symbol_addr_t)0x00003564},
{(const char*)s_lcLL, (symbol_addr_t)0x000024ee},
{(const char*)s_lcpEvPub, (symbol_addr_t)0x00002cf0},
{(const char*)s_ldbuf, (symbol_addr_t)0x00802f5b},
{(const char*)s_ledtimer, (symbol_addr_t)0x00803020},
{(const char*)s_memcmp, (symbol_addr_t)0x0000e318},
{(const char*)s_memcpy, (symbol_addr_t)0x0000e332},
{(const char*)s_memcpy_F, (symbol_addr_t)0x00003580},
{(const char*)s_memcpy_P, (symbol_addr_t)0x0000e2f4},
{(const char*)s_memmove, (symbol_addr_t)0x0000e344},
{(const char*)s_memset, (symbol_addr_t)0x0000e366},
{(const char*)s_moteinit, (symbol_addr_t)0x00003d0a},
{(const char*)s_nbr_table_add_lladdr, (symbol_addr_t)0x0000db40},
{(const char*)s_nbr_table_get_from_lladdr, (symbol_addr_t)0x0000dcc8},
{(const char*)s_nbr_table_get_lladdr, (symbol_addr_t)0x0000dd5a},
{(const char*)s_nbr_table_head, (symbol_addr_t)0x0000db08},
{(const char*)s_nbr_table_next, (symbol_addr_t)0x0000dad0},
{(const char*)s_nbr_table_register, (symbol_addr_t)0x0000da90},
{(const char*)s_nbr_table_remove, (symbol_addr_t)0x0000dd22},
{(const char*)s_notifCbmt, (symbol_addr_t)0x00003b4a},
{(const char*)s_printComponents, (symbol_addr_t)0x00001d2e},
{(const char*)s_printf, (symbol_addr_t)0x0000e3a2},
{(const char*)s_printf_P, (symbol_addr_t)0x0000e3c4},
{(const char*)s_puts, (symbol_addr_t)0x0000e40a},
{(const char*)s_rand, (symbol_addr_t)0x0000e2d8},
{(const char*)s_rand_r, (symbol_addr_t)0x0000e2d6},
{(const char*)s_readdata, (symbol_addr_t)0x00003a82},
{(const char*)s_reconfig_Lc, (symbol_addr_t)0x0080027b},
{(const char*)s_replyCbmt, (symbol_addr_t)0x00003a98},
{(const char*)s_ringbuf_get, (symbol_addr_t)0x000083c4},
{(const char*)s_ringbuf_put, (symbol_addr_t)0x00008386},
{(const char*)s_rng, (symbol_addr_t)0x000025f6},
{(const char*)s_rng_get_uint8, (symbol_addr_t)0x00007022},
{(const char*)s_rxpayload, (symbol_addr_t)0x00802ec6},
{(const char*)s_seconds, (symbol_addr_t)0x00803029},
{(const char*)s_sensors, (symbol_addr_t)0x00803023},
{(const char*)s_sensors_flags, (symbol_addr_t)0x00803022},
{(const char*)s_sensors_process, (symbol_addr_t)0x008002b0},
{(const char*)s_serial_line_event_message, (symbol_addr_t)0x008030c1},
{(const char*)s_serial_line_input_byte, (symbol_addr_t)0x00008290},
{(const char*)s_serial_line_process, (symbol_addr_t)0x008002c0},
{(const char*)s_shortLen_F, (symbol_addr_t)0x000035c8},
{(const char*)s_shortcmp_F, (symbol_addr_t)0x000035ee},
{(const char*)s_size, (symbol_addr_t)0x00802f5d},
{(const char*)s_sleepseconds, (symbol_addr_t)0x00803025},
{(const char*)s_smreceive, (symbol_addr_t)0x000037fe},
{(const char*)s_strLen_F, (symbol_addr_t)0x000035a0},
{(const char*)s_strcmp, (symbol_addr_t)0x0000e374},
{(const char*)s_strcmp_F, (symbol_addr_t)0x00003674},
{(const char*)s_strcmp_P, (symbol_addr_t)0x0000e306},
{(const char*)s_strcpy_F, (symbol_addr_t)0x00003620},
{(const char*)s_strncmp, (symbol_addr_t)0x0000e386},
{(const char*)s_strnlen, (symbol_addr_t)0x0000e8d2},
{(const char*)s_strnlen_P, (symbol_addr_t)0x0000e8bc},
{(const char*)s_temp_sample_Lc, (symbol_addr_t)0x00800214},
{(const char*)s_vprintf, (symbol_addr_t)0x0000e464},
{(const char*)s_xEnc, (symbol_addr_t)0x00003742},
{(const char *)0, (symbol_addr_t)0} };

#include "smip/dn_uart.h"
#include "dev/rs232.h"

#include<stdio.h>

void dn_uart_init(dn_uart_rxByte_cbt rxByte_cb){

	rs232_init(RS232_PORT_1, USART_BAUD_115200,USART_PARITY_NONE | USART_STOP_BITS_1 | USART_DATA_BITS_8);

	rs232_set_input(RS232_PORT_1,rxByte_cb);

	printf("\n[SmartDust] Uart Initlialized\n");

}


void dn_uart_txByte(uint8_t byte){

	rs232_send(RS232_PORT_1,byte);
}






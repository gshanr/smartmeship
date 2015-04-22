#define F_CPU 16000000UL

#include <avr/io.h>
#include <util/delay.h>
#include <avr/interrupt.h>

void delay_ms(uint8_t ms) {
	uint16_t delay_count = 1000000 / 17500;
	volatile uint16_t i;

	while (ms != 0) {
		for (i=0; i != delay_count; i++) ;
		ms--;
	}
}

// timer1 overflw
ISR(TIMER1_COMPA_vect) {
	PORTB ^= 0x02;
	PORTD ^= 0x60;
}

int
main (void)
{
	DDRB=0x02;
	DDRD=0x60;

	TCCR1B |= (1 << WGM12); // Configure timer 1 for CTC mode

	TIMSK1 |= (1 << OCIE1A); // Enable CTC interrupt

	sei(); //  Enable global interrupts

	//OCR1A   = 15624; // Set CTC compare value to 1Hz at 1MHz AVR clock, with a prescaler of 64

	OCR1A   = 40000;

	TCCR1B |= ((1 << CS32) | (1 << CS30)); // Start timer at Fcpu/64

	PORTB=0x02;
	PORTD=0x60;

	while(1)
	{


	}
	return 0;
}
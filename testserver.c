#include <stdio.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>

int main( int argc, char *argv[] )
{
    unsigned long exptime;
    
    exptime=6;
    
    printf("Divide %u\n", (exptime/8));
    printf("Modulo %u\n", (exptime%8));
    
    return 0;
}

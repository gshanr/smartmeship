CFLAGS += -I../../core/commands -I../../core -I../../

VPATH=../../gateway/lib

all: looci_shell

looci_shell: looci_shell.o deployment.o 
#runtime_control.o introspection.o net.o 

clean:
	rm -f *.o

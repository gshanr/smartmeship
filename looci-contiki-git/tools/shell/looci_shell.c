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
 * Copyright (c) 2010, Katholieke Universiteit Leuven
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
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/stat.h>
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <stdbool.h>

#include "gateway/api/deployment.h"
//#include "gateway/api/runtime_control.h"
//#include "gateway/api/introspection.h"

// MAIN

static void usage(char * cmd) {
  // TODO write help
    fprintf(stderr, "Usage: %s <command> <command parameters>\r\n", cmd);
    fprintf(stderr, "Commands\r\n========\r\n");
    fprintf(stderr, "Deployment:\r\n-----------\r\n");
    fprintf(stderr, "   deploy <file> <node>\r\n");
    fprintf(stderr, "   removeComponent <component id> <node>\r\n");
    fprintf(stderr, "Runtime control:\r\n----------------\r\n");
    fprintf(stderr, "   activate <component id> <node>\r\n");
    fprintf(stderr, "   deactivate <component id> <node>\r\n");
    fprintf(stderr, "   resetWirings <component id> <node>\r\n");
    fprintf(stderr, "   wireLocal <interface event> <source component id> <receptacle event> <destination component id> <node>\r\n");
    fprintf(stderr, "   unwireLocal <interface event> <source component id> <receptacle event> <destination component id> <node>\r\n");
    fprintf(stderr, "   wireTo <interface event> <source component id> <source node> <destination node>\r\n");
    fprintf(stderr, "   unwireTo <interface event> <source component id> <source node> <destination node>\r\n");
    fprintf(stderr, "   wireFrom <interface event> <source component id> <source node> <receptacle event> <destination component id> <destination node>\r\n");
    fprintf(stderr, "   unwireFrom <interface event> <source component id> <source node> <receptacle event> <destination component id> <destination node>\r\n");
    fprintf(stderr, "   wireToAll <interface event> <source component id> <source node>\r\n");
    fprintf(stderr, "   unwireToAll <interface event> <source component id> <source node>\r\n");
    fprintf(stderr, "   wireFromAll <interface event> <receptacle event> <destination component id> <destination node>\r\n");
    fprintf(stderr, "   unwireFromAll <interface event> <receptacle event> <destination component id> <destination node>\r\n");
    fprintf(stderr, "Introspection:\r\n--------------\r\n");
    fprintf(stderr, "   getComponentIDs <component type> <node>\r\n");
    fprintf(stderr, "   getComponentIDs <node>\r\n");
    fprintf(stderr, "   getInstanceIDs <node>\r\n");
    fprintf(stderr, "   getComponentType <component id> <node>\r\n");
    fprintf(stderr, "   getInstanceType <instance id> <node>\r\n");
    fprintf(stderr, "   getState <component id> <node>\r\n");
    fprintf(stderr, "   getInterfaces <component id> <node>\r\n");
    fprintf(stderr, "   getReceptacles <component id> <node>\r\n");
    fprintf(stderr, "   getOutgoingRemoteWires <event id> <source component id> <source node id>\r\n");
    fprintf(stderr, "   getIncomingRemoteWires <interface event id> <destination component id> <destination node id>\r\n");
    fprintf(stderr, "   getLocalWires <event id> <source component id> <node>\r\n");
    fprintf(stderr, "   getProperties <instance id> <node>\r\n");
    fprintf(stderr, "   getPropertyString <propertyId> <instanceId> <node>\r\n");
    fprintf(stderr, "   getPropertyInt8<propertyId> <instanceId> <node>\r\n");
    fprintf(stderr, "   setPropertyString <property id> <instance id> <node> <parameterVal> \r\n");
    fprintf(stderr, "   setPropertyInt8<property id> <instance id> <node> <parameterVal> \r\n");
    fprintf(stderr, "   \r\n");
    exit(-1);
}

int main(int argc, char * argv[]) {
  // TODO use getopt ?
  if(argc < 2) {
    usage(argv[0]);
  }
  // deployment.h
  if(strcmp("deploy", argv[1])==0) {
    if(argc != 4) {
      usage(argv[0]);
    }
    uint8_t cid = looci_deploy(argv[2], argv[3]);
    if(cid == 0) {
      fprintf(stderr, "Deployment failed\r\n");
      return -1;
    } else {
      printf("%u\r\n", cid);
      return 0;
    }
  }

  /*
  else if(strcmp("removeComponent", argv[1]) == 0) {
    if(argc != 4) {
      usage(argv[0]);
    }
    bool success = looci_remove((uint8_t) atoi(argv[2]), argv[3]);
    if(success == true) {
      return 0;
    } else {
      fprintf(stderr, "Removing component failed\r\n");
      return -1;
    }
  } // runtime_control.h 
  else if(strcmp("activate", argv[1]) == 0) {
    if(argc != 4) {
      usage(argv[0]);
    }
    bool success = looci_activate((uint8_t) atoi(argv[2]), argv[3]);
    if(success == true) {
      return 0;
    } else {
      fprintf(stderr, "Activate component failed\r\n");
      return -1;
    }
  } else if(strcmp("deactivate", argv[1]) == 0) {
    if(argc != 4) {
      usage(argv[0]);
    }
    bool success = looci_deactivate((uint8_t) atoi(argv[2]), argv[3]);
    if(success == true) {
      return 0;
    } else {
      fprintf(stderr, "Deactivate component failed\r\n");
      return -1;
    }
  } else if(strcmp("instantiate", argv[1]) == 0) {
		if(argc != 4) {
		  usage(argv[0]);
		}
		uint8_t id = 0;
		bool success = looci_instantiate((uint8_t) atoi(argv[2]), argv[3],&id);
		if(success == true) {
			fprintf(stderr, "Instantiated component with id %u \r\n",id);
		  return id;
		} else {
		  fprintf(stderr, "Instantiate component failed\r\n");
		  return 0;
		}
  }  else if(strcmp("destroy", argv[1]) == 0) {
		if(argc != 4) {
		  usage(argv[0]);
		}
		bool success = looci_destroy((uint8_t) atoi(argv[2]), argv[3]);
		if(success == true) {
		  return 0;
		} else {
		  fprintf(stderr, "Destroy instance failed\r\n");
		  return -1;
		}
  }
	  else if(strcmp("resetWirings", argv[1]) == 0) {
    if(argc != 4) {
      usage(argv[0]);
    }
    bool success = looci_reset_wirings((uint8_t) atoi(argv[2]), argv[3]);
    if(success == true) {
      return 0;
    } else {
      fprintf(stderr, "Reset wirings failed\r\n");
      return -1;
    }
  } else if(strcmp("wireLocal", argv[1]) == 0) {
    if(argc!=6) {
      usage(argv[0]);
    }
    bool success = looci_wire_local((looci_eventtype_t) atoi(argv[2]), (uint8_t) atoi(argv[3]),(uint8_t) atoi(argv[4]), argv[5]);
    if(success == true) {
      return 0;
    } else {
      fprintf(stderr, "Wire local failed\r\n");
      return -1;
    }
  } else if(strcmp("unwireLocal", argv[1]) == 0) {
    if(argc!=6) {
      usage(argv[0]);
    }
    bool success = looci_unwire_local((looci_eventtype_t) atoi(argv[2]), (uint8_t) atoi(argv[3]), (uint8_t) atoi(argv[4]), argv[5]);
    if(success == true) {
      return 0;
    } else {
      fprintf(stderr, "Unwire local failed\r\n");
      return -1;
    }
  } else if(strcmp("wireTo", argv[1]) == 0) {
    if(argc!=6) {
      usage(argv[0]);
    }
    bool success = looci_wire_to((looci_eventtype_t) atoi(argv[2]), (uint8_t) atoi(argv[3]), argv[4], argv[5]);
    if(success == true) {
      return 0;
    } else {
      fprintf(stderr, "Wire to failed\r\n");
      return -1;
    }
  } else if(strcmp("unwireTo", argv[1]) == 0) {
    if(argc!=6) {
      usage(argv[0]);
    }
    bool success = looci_unwire_to((looci_eventtype_t) atoi(argv[2]), (uint8_t) atoi(argv[3]), argv[4], argv[5]);
    if(success == true) {
      return 0;
    } else {
      fprintf(stderr, "Unwire to failed\r\n");
      return -1;
    }
  } else if(strcmp("wireFrom", argv[1]) == 0) { 
    if(argc!=7) {
      usage(argv[0]);
    }
    bool success = looci_wire_from((looci_eventtype_t) atoi(argv[2]), (uint8_t) atoi(argv[3]), argv[4],  (uint8_t) atoi(argv[5]), argv[6]);
    if(success == true) {
      return 0;
    } else {
      fprintf(stderr, "Wire from failed\r\n");
      return -1;
    }
  } else if(strcmp("unwireFrom", argv[1]) == 0) {
    if(argc!=7) {
      usage(argv[0]);
    }
    bool success = looci_unwire_from((looci_eventtype_t) atoi(argv[2]), (uint8_t) atoi(argv[3]), argv[4],  (uint8_t) atoi(argv[5]), argv[6]);
    if(success == true) {
      return 0;
    } else {
      fprintf(stderr, "Unwire from failed\r\n");
      return -1;
    }
  } else if(strcmp("wireFromAll", argv[1]) == 0) {
    if(argc!=5) {
      usage(argv[0]);
    }
    bool success = looci_wire_from_all((looci_eventtype_t) atoi(argv[2]),  (uint8_t) atoi(argv[3]), argv[4]);
    if(success == true) {
      return 0;
    } else {
      fprintf(stderr, "Wire from all failed\r\n");
      return -1;
    }
  } else if(strcmp("unwireFromAll", argv[1]) == 0) {
    if(argc!=5) {
      usage(argv[0]);
    }
    bool success = looci_unwire_from_all((looci_eventtype_t) atoi(argv[2]), (uint8_t) atoi(argv[3]), argv[4]);
    if(success == true) {
      return 0;
    } else {
      fprintf(stderr, "Unwire from all failed\r\n");
      return -1;
    }
  } else if(strcmp("wireToAll", argv[1]) == 0) {
    if(argc!=5) {
      usage(argv[0]);
    }
    bool success = looci_wire_to_all((looci_eventtype_t) atoi(argv[2]), (uint8_t) atoi(argv[3]), argv[4]);
    if(success == true) {
      return 0;
    } else {
      fprintf(stderr, "Wire to all failed\r\n");
      return -1;
    }
  } else if(strcmp("unwireToAll", argv[1]) == 0) {
    if(argc!=5) {
      usage(argv[0]);
    }
    bool success = looci_unwire_to_all((looci_eventtype_t) atoi(argv[2]), (uint8_t) atoi(argv[3]), argv[4]);
    if(success == true) {
      return 0;
    } else {
      fprintf(stderr, "Unwire to all failed\r\n");
      return -1;
    }
  } // introspection.h 
  else if(strcmp("getComponentIDs", argv[1]) == 0) {
    if(argc != 4 && argc != 3) {
      usage(argv[0]);
    }
    if(argc == 4) {
      size_t len = 50;
      uint8_t buffer[50];
      bool success = false;
      success = looci_get_component_ids_by_type(argv[2], argv[3], buffer, &len);
      int i = 0;
      for(i = 0; i < len; ++i) {
        printf("%u\r\n", buffer[i]);
      }
      if(success == false) {
        fprintf(stderr, "The return value is false\r\n");
        return -1;
      } else {
        return 0;
      }
    } else if(argc == 3) {
      size_t len = 50;
      uint8_t buffer[50];
      bool success = false;
      success = looci_get_component_ids(argv[2], buffer, &len);
      int i = 0;
      for(i = 0; i < len; ++i) {
        printf("%u\r\n", buffer[i]);
      }
      if(success == false) {
        fprintf(stderr, "The return value is false\r\n");
        return -1;
      } else {
        return 0;
      }
    }
  }
  else if(strcmp("getInstanceIDs", argv[1]) == 0) {
     if(argc != 3) {
       usage(argv[0]);
     }
     if(argc == 3) {
       size_t len = 50;
       uint8_t buffer[50];
       bool success = false;
       success = looci_get_instance_ids(argv[2], buffer, &len);
       int i = 0;
       for(i = 0; i < len; ++i) {
         printf("%u\r\n", buffer[i]);
       }
       if(success == false) {
         fprintf(stderr, "The return value is false\r\n");
         return -1;
       } else {
         return 0;
       }
     }
   }
  else if(strcmp("getComponentType", argv[1])==0) {
    if(argc != 4) {
      usage(argv[0]);
    }
    char buffer[100];
    memset(buffer, 0, 100);
    bool success = false;
    success = looci_get_componenttype((uint8_t) atoi(argv[2]), argv[3], buffer, 100);
    printf("%s\r\n", buffer);
    if(success == false) {
      fprintf(stderr, "Get component type returned false\r\n");
      return -1;
    } else {
      return 0;
    }
  }  else if(strcmp("getInstanceType", argv[1])==0) {
	    if(argc != 4) {
	      usage(argv[0]);
	    }
	    char buffer[100];
	    memset(buffer, 0, 100);
	    bool success = false;
	    success = looci_get_instancetype((uint8_t) atoi(argv[2]), argv[3], buffer, 100);
	    printf("%s\r\n", buffer);
	    if(success == false) {
	      fprintf(stderr, "Get component type returned false\r\n");
	      return -1;
	    } else {
	      return 0;
	    }
	  } else if(strcmp("getState", argv[1]) == 0) {
    if(argc != 4) {
      usage(argv[0]);
    }
    uint8_t state = looci_get_state((uint8_t) atoi(argv[2]), argv[3]);
    if(state == COMPONENT_STATE_ACTIVE) {
       printf("active\r\n");
       return 0;
    } else if (state == COMPONENT_STATE_DEACTIVATED) {
       printf("deactivated\r\n");
       return 0;
    } else {
      fprintf(stderr, "Unknown state\r\n");
      return -1;
    }
  } else if((strcmp("getInterfaces", argv[1]) == 0)||(strcmp("getReceptacles", argv[1])==0)){
    if(argc != 4) {
      usage(argv[0]);
    }
    size_t len = 25;
    looci_eventtype_t buffer[50];
    bool success = false;
    if(strcmp("getInterfaces", argv[1]) == 0) success = looci_get_interfaces((uint8_t) atoi(argv[2]), argv[3], buffer, &len);
    else if(strcmp("getReceptacles", argv[1]) == 0) success = looci_get_receptacles((uint8_t) atoi(argv[2]), argv[3], buffer, &len);
    int i = 0;
    for(i = 0; i < len; ++i) {
      printf("%u\r\n", buffer[i]);
    }
    if(success == false) {
      fprintf(stderr, "The return value was false\r\n");
      return -1;
    } else {
      return 0;
    }
  } else if(strcmp("getOutgoingRemoteWires", argv[1])==0) {
    if(argc != 5) {
      usage(argv[0]);
    }
    size_t len = 10;
    struct looci_outgoing_wire buffer[len];
    bool success = looci_get_outgoing_wires((looci_eventtype_t) atoi(argv[2]), (uint8_t) atoi(argv[3]), argv[4], buffer, &len);
    int i = 0;
    for(i=0; i < len; ++i) {
      printf("%s\r\n", buffer[i].nodeID);
    }
    if(success == false) {
      fprintf(stderr, "The return value was false\r\n");
      return -1;
    } else {
      return 0;
    }
  } else if(strcmp("getIncomingRemoteWires", argv[1])==0) {
    if(argc != 5){
      usage(argv[0]);
    }
    size_t len = 10;
    struct looci_incoming_wire wires[len];
    bool success = looci_get_incoming_wires((looci_eventtype_t) atoi(argv[2]), (uint8_t) atoi(argv[3]), argv[4], wires, &len);
    int i = 0;
    for(i=0; i < len; ++i) {
      printf("%s %u\r\n", wires[i].nodeID, wires[i].componentID);
    }
    if(success == false) {
      fprintf(stderr, "The return value was false\r\n");
      return -1;
    } else {
      return 0;
    }
  } else if(strcmp("getLocalWires", argv[1])==0) {
    if(argc != 5) {
      usage(argv[0]);
    }
    size_t len = 50;
    uint8_t buffer[50];
    bool success = looci_get_local_wires((looci_eventtype_t) atoi(argv[2]), (uint8_t) atoi(argv[3]), argv[4], buffer, &len);
    int i = 0;
    for(i = 0; i < len; ++i) {
      printf("%u\r\n", buffer[i]);
    }
    if(success == false) {
      fprintf(stderr, "The return value was false\r\n");
      return -1;
    } else {
      return 0;
    }
  }
else if(strcmp("getProperties", argv[1])==0) {
	if(argc != 4) {
	  usage(argv[0]);
	}
	size_t len = 25;
	looci_prop_t buffer[25];
	bool success = looci_get_properties((uint8_t) atoi(argv[2]), argv[3], buffer, &len);
	printf("len: %u \r\n",len);
	int i = 0;
	for(i = 0; i < len; ++i) {
	  printf("%u\r\n", buffer[i]);
	}
	if(success == false) {
	  fprintf(stderr, "The return value was false\r\n");
	  return -1;
	} else {
	  return 0;
	}
  }
else if(strcmp("getPropertyString", argv[1])==0) {
	if(argc != 5) {
	  usage(argv[0]);
	}
	size_t len = 50;
	uint8_t buffer[50];
	bool success = looci_get_property((looci_prop_t) atoi(argv[2]),(uint8_t) atoi(argv[3]), argv[4], buffer, &len);
	int i = 0;
	for(i = 0; i < len; ++i) {
	  printf("%c\r\n", buffer[i]);
	}
	printf("\r\n");
	if(success == false) {
	  fprintf(stderr, "The return value was false\r\n");
	  return -1;
	} else {
	  return 0;
	}
  }
else if(strcmp("getPropertyInt8", argv[1])==0) {
	if(argc != 5) {
	  usage(argv[0]);
	}
	size_t len = 50;
	uint8_t buffer[50];
	bool success = looci_get_property((looci_prop_t) atoi(argv[2]),(uint8_t) atoi(argv[3]), argv[4], buffer, &len);
	int i = 0;
	for(i = 0; i < len; ++i) {
	  printf("%u\r\n", buffer[i]);
	}
	printf("\r\n");
	if(success == false) {
	  fprintf(stderr, "The return value was false\r\n");
	  return -1;
	} else {
	  return 0;
	}
  }
else if(strcmp("setPropertyString", argv[1])==0) {
	if(argc != 6) {
	  usage(argv[0]);
	}

	uint8_t len = (uint8_t) strlen(argv[5]);
	bool success = looci_set_property((looci_prop_t) atoi(argv[2]),(uint8_t) atoi(argv[3]), argv[4], argv[5], &len);
	if(success == false) {
	  fprintf(stderr, "The return value was false\r\n");
	  return -1;
	} else {
		fprintf(stderr, "Property set\r\n");
	  return 0;
	}
  }
else if(strcmp("setPropertyInt8", argv[1])==0) {
	if(argc != 6) {
	  usage(argv[0]);
	}
	uint8_t len = 1;
	uint8_t val = (uint8_t) atoi(argv[5]);
	bool success = looci_set_property((looci_prop_t) atoi(argv[2]),(uint8_t) atoi(argv[3]), argv[4], &val, &len);
	if(success == false) {
	  fprintf(stderr, "The return value was false\r\n");
	  return -1;
	} else {
		fprintf(stderr, "Property set\r\n");
	  return 0;
	}
  }
  */

  usage(argv[0]);
  return -1;
}


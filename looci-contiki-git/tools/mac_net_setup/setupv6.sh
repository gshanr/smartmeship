#!/bin/bash

control_c()
{
    echo -en  "\n*** Cleaning up ***\n"

    # Remove firewall rule and lower reference count
    pfctl -a com.apple/250.ApplicationFirewall -F rules
    pfctl -X $token

    # Remove rtadvd_tmp.conf
    rm rtadvd_tmp.conf

    exit $?
}

# Trap keyboard interrupt (control-c)
trap control_c SIGINT

# Check if root
if [[ $EUID -ne 0 ]]; then
    echo "*** You must be a root user, use sudo ***"
    exit
fi

# Extract interface name based on MAC address
if_name=$(ifconfig | grep -B 1 "ether 02:12:13:14:15:16" | head -n1 | cut -c1-3)

# Check if Jackdaw is connected
if [ -z "$if_name" ]; then
    echo "*** No interface found with MAC 02::12:13:14:15:16  ***"
    echo "*** Is the Jackdaw plugged in? ***"
    exit
fi

ifconfig $if_name up
ifconfig $if_name inet6 aaaa::1/64

#### Undocumented ifconfig flag "router" ####
# For each ipv6 interface the xnu kernel has a router flag, which is used
# to decide if NDP advertisements have the RTR flag set (required for contiki
# AND the RFC standards).
# The only way of setting it is this obscure ifconfig call, or an even more
# hackish private syscal
ifconfig $if_name inet6 router

# This option will make no difference in suppressing autoconf addresses, the xnu
# kernel will ALWAYS generate autoconf addresses for locally generated RA's for
# an interface in router mode.
#ifconfig $if_name inet6 -autoconf

# Kernel driver/rzusb firmware needs to be kicked to autodetect media type
# Without quirk, one incoming packet needs to be received on rzusb to autodetect
ifconfig $if_name media none
ifconfig $if_name media autoselect

# No need for Bonjour spamming in the WSN
# Set up pf firewall to disallow MDNS packets
# Use com.apple/250.ApplicationFirewall anchor for rule, this anchor is
# typically empty and can be used
token=$(pfctl -E  2>&1 | awk 'END{print $NF}')
echo "block out on " $if_name  " inet6 proto udp to any port 5353" | pfctl -a \
    com.apple/250.ApplicationFirewall -f -  

# Enable ipv6 forwarding in kernel
sysctl -w net.inet6.ip6.forwarding=1

echo -en "\n*** Networking set up but this script needs to keep running to send router advertisements ***\n"
echo -en "*** Use C-c to exit and tear down ***\n"

# Generate rtadvd conf file with correct iface
sed -e "s/enX/$if_name/g" rtadvd.conf > rtadvd_tmp.conf

# Run userspace RA advertisement daemon in foreground mode
rtadvd -s -d -f -c ./rtadvd_tmp.conf $if_name


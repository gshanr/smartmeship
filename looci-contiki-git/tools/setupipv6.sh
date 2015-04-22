#!/bin/bash

sysctl -w net.ipv6.conf.all.forwarding=1
ip -6 address add aaaa::1/64 dev usb0
killall radvd
/etc/init.d/radvd restart

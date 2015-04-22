#!/bin/bash

sysctl -w net.ipv6.conf.all.forwarding=1
ip -6 addr add fddb:e5ed:6fd8:1::1/64 dev tap0
/etc/init.d/radvd restart

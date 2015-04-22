#!/bin/bash
read -p "please enter your own number: " nr
while [[ $nr != [0-9]* ]] || [ $nr -lt "1" ] || [ $nr -gt "50" ]
do
read -p "please enter your correct number: " nr
done


addr=$(ifconfig -a | grep eth | awk '{print $1}')

arr=($addr)
nrInt=${#arr[@]}
echo "nr interfaces: "$nrInt

if [ $nrInt -gt "1" ]; then
	echo "select interface (index of list): "$addr
	read -p "please enter your own number: " interfaceIndex
	while [[ $interfaceIndex != [0-9]* ]] || [ $interfaceIndex -lt "0" ] || [ $interfaceIndex -gt $nrInt ]
	do
	read -p "please enter your correct number: " interfaceIndex
	done
	addr=${arr[$interfaceIndex]}	
fi

echo "addr "$addr
ifconfig $addr up
read -p "please press enter:" 

#remove this line for nr storage
#echo $nr > nr.txt
echo $addr
ifconfig $addr inet6 add aaab::$nr/64
route -A inet6 add aaaa::/64 gw aaab::100
route -A inet6 add aaac::/64 gw aaab::100
echo "your address has been set to aaab::"$nr


lists=$(ifconfig | grep aaab:: | grep -v aaab::$nr | awk '{print $3}')
for myAddr in $lists
	do
		echo "deleting " $myAddr
		ifconfig $addr inet6 del $myAddr
	done

read -p "please press enter:" 



#!/bin/bash
nr=$(cat nr.txt)
echo $nr
pscp -pw user2 /home/user/looci/looci.osgi/LoociCore/Felix/looci/log.txt user2@\[aaab::1\]:log$nr.txt


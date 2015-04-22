#bash

#setup for contiki 2.6

if [ -z "$LOOCI" ]; then
	echo "please set LOOCI path variable"
	exit 1
fi


if [ -d "$LOOCI/lnk" ]; then
	echo "looci lnk dir exists"
else
	echo "creating link dir"
	mkdir $LOOCI/bin
	mkdir $LOOCI/components
	mkdir $LOOCI/lnk
fi

cur_dir=$(pwd)
rm $LOOCI/lnk/lc_contiki_os
echo making link from looci to: $cur_dir
ln -sf $cur_dir $LOOCI/lnk/lc_contiki_os

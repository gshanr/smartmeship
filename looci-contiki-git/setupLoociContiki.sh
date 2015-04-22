#bash


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
echo making link from looci to: $cur_dir
rm -f $LOOCI/lnk/lc_contiki
ln -sf $cur_dir $LOOCI/lnk/lc_contiki
ln -sf $cur_dir/1scripts/loociContiki.py $LOOCI/bin/loociContiki
ln -sf $cur_dir/1scripts/loociContiki.py $LOOCI/bin/lcco

cp -i build/defaults/Makefile.target build/Makefile.target
cp -i build/defaults/Makefile.programmer build/Makefile.programmer
cp -i build/defaults/Makefile.options build/Makefile.options
cp -i build/defaults/Makefile.debug build/Makefile.debug

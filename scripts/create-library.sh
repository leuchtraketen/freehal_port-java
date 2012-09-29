#!/bin/bash

# if this script is invoked in the scripts/ directory, go back
if ! test -e ./src
then
	cd ..
fi

# build the android app library
if test -e ../FreehalApp
then
	mkdir -p ../FreehalApp/res/raw
	rm -f ../FreehalApp/res/raw/database* 2>/dev/null
	zip -r ../FreehalApp/res/raw/database.zip lang_de/ lang_en/
#	mv -f ../FreehalApp/res/raw/database.zip ../FreehalApp/res/raw/database

	cd bin
	rm -f ../../FreehalApp/libs/FreehalCore.jar 2>/dev/null
	zip -r ../../FreehalApp/libs/FreehalCore.jar net/
	cd ..
fi

# build a library for general use
cd bin
rm -f ../FreehalCore.jar 2>/dev/null
zip -r ../FreehalCore.jar net/
cd ..

#!/bin/bash

if [ "x$1" = "x" ]
then
	echo "Please run this script with the source directory as first parameter!"
	echo "Using ./src/ as source directory..."
	$0 src/
	exit 0
fi

if [ "x$2" = "x" ]
then
	echo "Please run this script with the output directory as second parameter!"
	echo "Using ./doc/javadoc/ as output directory..."
	$0 "$1" ./doc/javadoc/
	exit 0
fi

for x in src bin doc scripts
do
	if [ "x$x" = "x"$(basename $(pwd)) ]
	then
		cd ..
	fi
done
pwd


export JAVA_HOME=$(readlink -f /usr/bin/javac | sed "s:/bin/javac::")
export doclet="-doclet com.google.doclava.Doclava -docletpath $(dirname $0)/doclava-1.0.6.jar -bootclasspath $JAVA_HOME/jre/lib/rt.jar"
echo $doclet
mkdir -p $2/
javadoc -hdf project.name "FreeHAL Library" -d $2/ $doclet $(find $1 -name "*.java")


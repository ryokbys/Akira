#!/bin/bash


#set directories
AKIRADIR="$HOME/Akira"
JOGL="$HOME/Akira/jogl"

#set path
DYLD_LIBRARY_PATH=${DYLD_LIBRARY_PATH:-$JOGL}
if [ `echo $DYLD_LIBRARY_PATH | grep $JOGL` ];then
    echo OK > /dev/null
else
    export DYLD_LIBRARY_PATH=$DYLD_LIBRARY_PATH:$JOGL
fi


JARS="$JOGL/gluegen-rt.jar:\
$JOGL/gluegen-rt.jar:\
$JOGL/newt.all.jar:\
$JOGL/jogl.all.jar:\
$JOGL/nativewindow.all.jar:\
$AKIRADIR/Akira.jar"

#execute
java -Djava.library.path=$DYLD_LIBRARY_PATH -Xmx1024m -Xdock:icon=$AKIRADIR/Akira.icns -Xdock:name=Akira -cp $JARS viewer.AkiraViewer $@

#!/bin/bash

#set directories
AKIRADIR="$HOME/Akira"
JOGL="$HOME/Akira/jogl"

LD_LIBRARY_PATH=${LD_LIBRARY_PATH:-$JOGL}
if [ `echo $LD_LIBRARY_PATH | grep $JOGL` ];then
    echo OK > /dev/null
else
    export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$JOGL
fi


JARS="$JOGL/gluegen-rt.jar:\
$JOGL/gluegen-rt.jar:\
$JOGL/newt.all.jar:\
$JOGL/jogl.all.jar:\
$JOGL/nativewindow.all.jar:\
$AKIRADIR/Akira.jar"

#execute
java -Djava.library.path=$LD_LIBRARY_PATH -Xmx1024m -cp $JARS viewer.AkiraViewer $@

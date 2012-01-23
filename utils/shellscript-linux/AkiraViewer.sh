#!/bin/bash

#set directories
AKIRADIR="$HOME/Akira"
JOGL="$HOME/Akira/jogl"

export LD_LIBRARY_PATH=${LD_LIBRARY_PATH:-$JOGL}
if [ -z "${LD_LIBRARY_PATH%%JOGL}" ];then
    echo already set >/dev/null
else
    export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$JOGL
fi

JARS="$JOGL/gluegen-rt.jar:\
$JOGL/gluegen-rt.jar:\
$JOGL/newt.all.jar:\
$JOGL/jogl.all.jar:\
$JOGL/nativewindow.all.jar:\
$AKIRADIR/Akira.jar"

#set command
cmd="java -Xmx1024m -cp $JARS viewer.AkiraViewer"

#execute
$cmd $@

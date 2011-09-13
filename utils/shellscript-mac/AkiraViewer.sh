#!/bin/bash

#set directories
AKIRADIR="$HOME/Akira"
JOGL="$HOME/myLocal/jogl"
JARS="$JOGL/gluegen-rt.jar:\
$JOGL/gluegen-rt.jar:\
$JOGL/newt.all.jar:\
$JOGL/jogl.all.jar:\
$JOGL/nativewindow.all.jar:\
$AKIRADIR/Akira.jar"

#set command
cmd="java -Xmx1024m -Xdock:icon=$AKIRADIR/Akira.icns -Xdock:name=Akira \
     -cp $JARS viewer.AkiraViewer"

#execute
$cmd $@

#!/bin/bash

#set directories
AKIRADIR="$HOME/myLocal/Akira"
JOGL="$HOME/myLocal/javalib/jogl"
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

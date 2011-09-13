#!/bin/bash
AKIRADIR="$HOME/Akira"

if [ ! -f $PWD/AkiraConverter.conf ]; then
    cp $AKIRADIR/AkiraConverter.conf $PWD/
    emacs $PWD/AkiraConverter.conf
fi

java -Xmx1024m -cp $AKIRADIR/Akira.jar converter.AkiraConverter

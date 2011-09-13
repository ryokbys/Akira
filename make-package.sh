#!/bin/bash

echo "#################"
echo "making AKIRA.zip"
echo "#################"
echo ""

emacs viewer/UpdateManager.java

ver=`grep -e "thisVersion=" viewer/UpdateManager.java |grep -o "[0-9].*" |sed s/\;//`

ant clean
ant jar
mkdir Akira
cp Akira.jar Akira/
cp converter/AkiraConverter.conf Akira/
cp img/Akira.icns Akira/
cp -r utils/shellscript-* Akira/
mkdir Akira/plugin
cp -r plugin-sample/* Akira/plugin/
zip -r Akira-$ver.zip Akira/
rm -rfv Akira/


# find . -name '*.java' -exec sed -i -e 's/jogamp/sun/' {} \;
# find . -name "*.java-e" -exec rm -v {} \;

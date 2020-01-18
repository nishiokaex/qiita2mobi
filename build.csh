#!/bin/csh

cd GitBookConverter
mkdir -p build/classes
javac src/*.java -d build/classes

rm -rf out
mkdir out
java -cp build/classes QiitaConverter qiita.txt out/

rm -rf ../qiita
cp -r out ../qiita

cd ../qiita
rm -rf _book *.mobi
gitbook mobi

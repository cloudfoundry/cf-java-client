#!/bin/sh
VERSION=2.6.1
wget https://github.com/google/protobuf/releases/download/$VERSION/protobuf-$VERSION.tar.gz
tar xvfz protobuf-$VERSION.tar.gz
cd protobuf-$VERSION
./configure --prefix=/usr
make
make check
sudo make install
cd ..
rm protobuf-$VERSION.tar.gz
rm -rf protobuf-$VERSION
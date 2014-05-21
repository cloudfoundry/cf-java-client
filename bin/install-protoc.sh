sudo apt-get -y install build-essential
wget https://protobuf.googlecode.com/files/protobuf-2.5.0.tar.gz
tar xvfz protobuf-2.5.0.tar.gz
cd protobuf-2.5.0
./configure --prefix=/usr
make
sudo make install
protoc --version

# unlink any current protobuf installation
brew unlink protobuf
cd $( brew --prefix )
# checkout the protobuf 2.5.0 homebrew formula
git checkout e2d162d Library/Formula/protobuf.rb
# brew install protobuf 2.5.0
brew install protobuf
# get back to the current protobuf formula
git checkout -- Library/Formula/protobuf.rb
# activate protobuf 2.5.0
brew switch protobuf 2.5.0

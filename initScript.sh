cat > ./bin/computePatchOpt << "EOF1"
java -cp .:../lib/commons-io-2.4/commons-io-2.4.jar computePatchOpt "$1" "$2"
EOF1

cat > ./bin/ma << "EOF2"
cd ..
make clean
make binary
cd ./bin
EOF2

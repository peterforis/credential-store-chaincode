#!/bin/bash

rm -rf $HOME/git/fabric-samples/asset-transfer-basic/chaincode-java || echo "Failed rm"
cp -r $HOME/git/chaincode-java $HOME/git/fabric-samples/asset-transfer-basic || echo "Failed cp"

echo done

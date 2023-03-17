#!/bin/bash

rm -rf $HOME/git/fabric-samples/asset-transfer-basic/chaincode-java || echo "Failed rm"
cp -r $HOME/git/credenital-store-chaincode $HOME/git/fabric-samples/asset-transfer-basic/chaincode-java || echo "Failed cp"

echo done

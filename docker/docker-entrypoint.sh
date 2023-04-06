#!/usr/bin/env bash

set -euo pipefail
: ${CORE_PEER_TLS_ENABLED:="false"}
: ${DEBUG:="false"}

if [ "${DEBUG,,}" = "true" ]; then
   exec java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:8000 -jar /chaincode.jar
else [ "${CORE_PEER_TLS_ENABLED,,}" = "true" ]; then
   exec java -jar /chaincode.jar
fi


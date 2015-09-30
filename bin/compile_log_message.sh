#!/usr/bin/env bash

SRC_DIR=cloudfoundry-client/src/main/protocol-buffers
DEST_DIR=cloudfoundry-client/src/main/java

protoc -I="${SRC_DIR}" --java_out="${DEST_DIR}" "${SRC_DIR}/log_message.proto"

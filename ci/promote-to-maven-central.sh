#!/usr/bin/env bash

set -euo pipefail

GROUP_ID=$(jq -r .modules[0].id build-info/build-info.json | cut -d ':' -f 1)
VERSION=$(jq -r .modules[0].id build-info/build-info.json | cut -d ':' -f 3)

## Promote to Maven Central
curl --request "POST" "https://api.bintray.com/maven_central_sync/spring/jars/$GROUP_ID/versions/$VERSION" \
     --header "Content-Type: application/json; charset=utf-8" \
     --user $BINTRAY_USERNAME:$BINTRAY_API_KEY \
     --data "{ \"username\": \"$MAVEN_CENTRAL_USERNAME\", \"password\": \"$MAVEN_CENTRAL_PASSWORD\" }" \
     --fail

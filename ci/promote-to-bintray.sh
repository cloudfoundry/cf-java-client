#!/usr/bin/env bash

set -euo pipefail

NAME=$(jq -r .name build-info/build-info.json)
NUMBER=$(jq -r .number build-info/build-info.json)

## Promote to Bintray
curl --request "POST" "https://repo.spring.io/api/build/distribute/$NAME/$NUMBER" \
     --header "Content-Type: application/json; charset=utf-8" \
     --user $ARTIFACTORY_USERNAME:$ARTIFACTORY_PASSWORD \
     --data "{ \"sourceRepos\": [ \"$SOURCE_REPOSITORY\" ], \"targetRepo\": \"$TARGET_REPOSITORY\" }" \
     --fail

#!/usr/bin/env sh

set -e

NAME=$(jq -r .name cf-java-client/target/build-info.json)
NUMBER=$(jq -r .number cf-java-client/target/build-info.json)

## Promote to Bintray
curl -X "POST" "https://repo.spring.io/api/build/distribute/$NAME/$NUMBER" \
     -H "Content-Type: application/json; charset=utf-8" \
     -u $ARTIFACTORY_USERNAME:$ARTIFACTORY_PASSWORD \
     -d $"{ \"sourceRepos\": [ \"$SOURCE_REPOSITORY\" ], \"targetRepo\": \"$TARGET_REPOSITORY\" }"

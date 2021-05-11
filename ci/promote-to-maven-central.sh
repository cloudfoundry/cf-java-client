#!/usr/bin/env bash

set -euo pipefail

export BUILD_INFO_LOCATION=$(pwd)/artifactory-repo/build-info.json

java -jar /opt/concourse-release-scripts.jar publishToCentral 'RELEASE' "$BUILD_INFO_LOCATION" artifactory-repo

echo "Sync complete"

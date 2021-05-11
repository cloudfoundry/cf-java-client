#!/usr/bin/env bash

set -euo pipefail

export BUILD_INFO_LOCATION=$(pwd)/build-info/build-info.json

ls -la "$(pwd)/build-info"
cat "$BUILD_INFO_LOCATION"

java -jar /opt/concourse-release-scripts.jar publishToCentral 'RELEASE' "$BUILD_INFO_LOCATION" artifactory-repo

echo "Sync complete"

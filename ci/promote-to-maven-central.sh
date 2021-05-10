#!/usr/bin/env bash

set -euo pipefail

java -jar /opt/spring-boot-release-scripts.jar publishToCentral 'RELEASE' build-info/build-info.json artifactory-repo

echo "Sync complete"

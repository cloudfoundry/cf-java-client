#!/usr/bin/env bash

set -euo pipefail

# update & install jq
apt-get update && apt-get -y install jq

[[ -d $PWD/maven && ! -d $HOME/.m2 ]] && ln -s $PWD/maven $HOME/.m2

ROOT=$(realpath "$(dirname "${BASH_SOURCE[0]}")"/../..)

if [[ -d "${ROOT}"/om ]]; then
  printf "➜ Expanding om\n"
  tar xzf "${ROOT}"/om/om-linux-*.tar.gz -C "${ROOT}"/om
  export PATH="${ROOT}"/om:${PATH}
fi

CF_CREDS=$(cat "${ROOT}"/environment/cf-creds.json)

TEST_APIHOST=$(jq -n -r --argjson credentials "${CF_CREDS}" '"api.sys.\($credentials.name).cf-app.com"')
export TEST_APIHOST

TEST_ADMIN_USERNAME=$(jq -n -r --argjson credentials "${CF_CREDS}" '$credentials.username')
export TEST_ADMIN_USERNAME

TEST_ADMIN_PASSWORD=$(jq -n -r --argjson credentials "${CF_CREDS}" '$credentials.password')
export TEST_ADMIN_PASSWORD

UAA_CREDS=$(cat "${ROOT}"/environment/uaa-creds.json)

TEST_ADMIN_CLIENTID=$(jq -n -r --argjson credentials "${UAA_CREDS}" '$credentials.client')
export TEST_ADMIN_CLIENTID

TEST_ADMIN_CLIENTSECRET=$(jq -n -r --argjson credentials "${UAA_CREDS}" '$credentials.secret')
export TEST_ADMIN_CLIENTSECRET

cd cf-java-client
./mvnw -q -P integration-test test

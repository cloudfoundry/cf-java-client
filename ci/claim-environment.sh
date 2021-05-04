#!/usr/bin/env bash

set -euo pipefail

ROOT=$(realpath "$(dirname "${BASH_SOURCE[0]}")"/../..)

if [[ -d "${ROOT}"/om ]]; then
  printf "➜ Expanding om\n"
  tar xzf "${ROOT}"/om/om-linux-*.tar.gz -C "${ROOT}"/om
  export PATH="${ROOT}"/om:${PATH}
fi

printf "Claiming environment from %s\n" "${POOL}"

CLAIM=$(curl \
  --fail \
  --location \
  --show-error \
  --silent \
  --header 'Accept: application/json' \
  --request "POST" \
  "https://environments.toolsmiths.cf-app.com/pooled_gcp_engineering_environments/claim?api_token=${API_TOKEN}&pool_name=${POOL}&notes=Claimed%20by%20Java%20Buildpack%20CI")

printf "Claimed %s\n" "$(jq -n -r --argjson claim "${CLAIM}" '$claim.name')"

CREDENTIALS=$(om \
  --target "$(jq -n -r --argjson claim "${CLAIM}" '$claim.ops_manager.url')" \
  --username "$(jq -n -r --argjson claim "${CLAIM}" '$claim.ops_manager.username')" \
  --password "$(jq -n -r --argjson claim "${CLAIM}" '$claim.ops_manager.password')" \
  credentials \
  --product-name cf \
  --credential-reference .uaa.admin_credentials \
  --format json)

  jq \
    -n -r \
    --argjson claim "${CLAIM}" \
    --argjson credentials "${CREDENTIALS}" \
    '{ name: $claim.name, username: $credentials.identity, password: $credentials.password }' \
    > "${ROOT}"/environment/cf-creds.json


CREDENTIALS=$(om \
  --target "$(jq -n -r --argjson claim "${CLAIM}" '$claim.ops_manager.url')" \
  --username "$(jq -n -r --argjson claim "${CLAIM}" '$claim.ops_manager.username')" \
  --password "$(jq -n -r --argjson claim "${CLAIM}" '$claim.ops_manager.password')" \
  credentials \
  --product-name cf \
  --credential-reference .uaa.admin_client_credentials \
  --format json)

  jq \
    -n -r \
    --argjson claim "${CLAIM}" \
    --argjson credentials "${CREDENTIALS}" \
    '{ client: $credentials.identity, secret: $credentials.password }' \
    > "${ROOT}"/environment/uaa-creds.json

printf "Patching for TCP Routing Support\n"

om \
  --target "$(jq -n -r --argjson claim "${CLAIM}" '$claim.ops_manager.url')" \
  --username "$(jq -n -r --argjson claim "${CLAIM}" '$claim.ops_manager.username')" \
  --password "$(jq -n -r --argjson claim "${CLAIM}" '$claim.ops_manager.password')" \
  staged-config \
  --product-name cf > /tmp/cf.yml

TCP_ROUTES_LB="$(jq -n -r --argjson claim "${CLAIM}" '$claim.tcp_router_pool')"

om \
  --target "$(jq -n -r --argjson claim "${CLAIM}" '$claim.ops_manager.url')" \
  --username "$(jq -n -r --argjson claim "${CLAIM}" '$claim.ops_manager.username')" \
  --password "$(jq -n -r --argjson claim "${CLAIM}" '$claim.ops_manager.password')" \
  configure-product \
  --config /tmp/cf.yml \
  --ops-file cf-java-client/ci/tcp-routes.yml \
  --var TCP_ROUTES_LB="tcp:${TCP_ROUTES_LB}"

om \
  --target "$(jq -n -r --argjson claim "${CLAIM}" '$claim.ops_manager.url')" \
  --username "$(jq -n -r --argjson claim "${CLAIM}" '$claim.ops_manager.username')" \
  --password "$(jq -n -r --argjson claim "${CLAIM}" '$claim.ops_manager.password')" \
  apply-changes \
  -n cf

printf "Environment updated with TCP Routes\n"

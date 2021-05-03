#!/usr/bin/env bash

set -euo pipefail

ROOT=$(realpath "$(dirname "${BASH_SOURCE[0]}")"/../..)

CREDENTIALS=$(cat "${ROOT}"/environment/cf-creds.json)

printf "Unclaiming %s\n" "$(jq -n -r --argjson credentials "${CREDENTIALS}" '$credentials.name')"

curl \
  --fail \
  --location \
  --show-error \
  --silent \
  --request "POST" \
  "https://environments.toolsmiths.cf-app.com/pooled_gcp_engineering_environments/unclaim?api_token=${API_TOKEN}&name=$(jq -n -r --argjson credentials "${CREDENTIALS}" '$credentials.name')"

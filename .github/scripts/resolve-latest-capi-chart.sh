#!/usr/bin/env bash
# Prints the newest version of the capi Helm chart (ghcr.io/cloudfoundry/helm/capi) that
# kind-deployment deploys. Needs only curl and python3, the registry allows anonymous pulls.
#
# Only release versions (1.252.0) and chart revisions of a release (1.252.1-1) count. Other tags,
# such as 1.237.1-fix1, are skipped.
set -euo pipefail

repository="cloudfoundry/helm/capi"
token="$(curl -fsSL --retry 3 "https://ghcr.io/token?scope=repository:$repository:pull" |
  python3 -c 'import json, sys; print(json.load(sys.stdin)["token"])')"

headers="$(mktemp)"
trap 'rm -f "$headers"' EXIT
tags="$(curl -fsSL --retry 3 -D "$headers" -H "Authorization: Bearer $token" "https://ghcr.io/v2/$repository/tags/list?n=10000" |
  python3 -c 'import json, sys; print("\n".join(json.load(sys.stdin)["tags"] or []))')"

# The registry lists the tags in push order, so a truncated (paginated) list would miss the newest
if grep -qi '^link:.*rel="next"' "$headers"; then
  echo "The tag list of ghcr.io/$repository is paginated, which this script doesn't support" >&2
  exit 1
fi

# grep fails without a match, which would end the script (pipefail) before the error below
version="$(printf '%s\n' "$tags" | { grep -E '^[0-9]+\.[0-9]+\.[0-9]+(-[0-9]+)?$' || true; } | sort -V | tail -1)"

[ -n "$version" ] || { echo "No capi chart version found in ghcr.io/$repository" >&2; exit 1; }

echo "latest capi chart -> $version" >&2
echo "$version"

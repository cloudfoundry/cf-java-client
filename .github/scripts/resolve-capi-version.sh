#!/usr/bin/env bash
# Prints the newest capi-release version whose CC API (v2) version equals SUPPORTED_API_VERSION
# from CloudFoundryClient.java. Needs only git and curl, no GitHub API and so no token.
#
# A capi-release tag pins cloud_controller_ng as a submodule, and that repo records its v2 API
# version in config/version_v2. The release notes aren't reliable for this. The v2 version only
# grows with the tags, so a binary search over the sorted tags is enough.
set -euo pipefail

source_file="$(dirname "$0")/../../cloudfoundry-client/src/main/java/org/cloudfoundry/client/CloudFoundryClient.java"
target="$(sed -n 's/.*String SUPPORTED_API_VERSION = "\([0-9.]*\)";.*/\1/p' "$source_file")"
[ -n "$target" ] || { echo "SUPPORTED_API_VERSION not found in $source_file" >&2; exit 1; }

workdir="$(mktemp -d)"
trap 'rm -rf "$workdir"' EXIT
# commits and trees only, which is all it takes to read the submodule commit of a tag
git clone --quiet --bare --filter=blob:none https://github.com/cloudfoundry/capi-release.git "$workdir/capi-release"

mapfile -t tags < <(git -C "$workdir/capi-release" tag -l | grep -E '^[0-9]+\.[0-9]+\.[0-9]+$' | sort -V)

v2_version() {
  local sha
  sha="$(git -C "$workdir/capi-release" ls-tree "$1" src/cloud_controller_ng | awk '{print $3}')"
  curl -fsSL --retry 3 "https://raw.githubusercontent.com/cloudfoundry/cloud_controller_ng/$sha/config/version_v2" | tr -d '[:space:]'
}

# highest index whose v2 version is <= target
lo=0
hi=$((${#tags[@]} - 1))
found=-1
while [ "$lo" -le "$hi" ]; do
  mid=$(((lo + hi) / 2))
  v="$(v2_version "${tags[$mid]}")"
  if [ "$(printf '%s\n%s\n' "$v" "$target" | sort -V | tail -1)" = "$target" ]; then
    found=$mid
    lo=$((mid + 1))
  else
    hi=$((mid - 1))
  fi
done

if [ "$found" -lt 0 ] || [ "$(v2_version "${tags[$found]}")" != "$target" ]; then
  echo "No capi-release found for CC API version $target" >&2
  exit 1
fi

echo "CC API $target -> capi-release ${tags[$found]}" >&2
echo "${tags[$found]}"

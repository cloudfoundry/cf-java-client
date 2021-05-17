#!/usr/bin/env bash

set -euo pipefail

[[ -d $PWD/maven && ! -d $HOME/.m2 ]] && ln -s $PWD/maven $HOME/.m2

function clean_gpg {
    FINGERPRINT=$(gpg --list-keys | head -4 | tail -1 | tr -d ' ')
    gpg --batch --yes --delete-secret-keys "$FINGERPRINT"
    gpg --batch --yes --delete-keys "$FINGERPRINT"
}

trap clean_gpg EXIT
gpg --batch --import-options import-show --import <(echo "$MAVEN_GPG_PRIVATE_KEY")

mkdir -p ~/.m2
cat <<EOF > ~/.m2/settings.xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>gpg.passphrase</id>
      <passphrase>${MAVEN_GPG_PASSPHRASE}</passphrase>
    </server>
  </servers>
</settings>
EOF

cd cf-java-client
./mvnw -q -Dmaven.test.skip=true deploy

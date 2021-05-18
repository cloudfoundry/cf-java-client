ARG base_image=openjdk:8-jre
FROM ${base_image}

ADD https://repo.spring.io/libs-release/io/spring/concourse/releasescripts/concourse-release-scripts/0.3.2/concourse-release-scripts-0.3.2.jar /opt/concourse-release-scripts.jar

RUN wget -qO - https://releases.jfrog.io/artifactory/api/gpg/key/public | sudo apt-key add -; \
    echo "deb https://releases.jfrog.io/artifactory/jfrog-debs xenial contrib" | sudo tee -a /etc/apt/sources.list; \
    apt-get update && apt-get install --no-install-recommends -y \
    ca-certificates \
    curl \
    jq \
    gnupg \
    jfrog-cli \
    && rm -rf /var/lib/apt/lists/*

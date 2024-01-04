ARG base_image=openjdk:8-jre
ARG script_image=springio/concourse-release-scripts:0.3.4

FROM ${script_image}

FROM ${base_image}

COPY --from=0 /concourse-release-scripts.jar /opt/

RUN apt-get update && apt-get install --no-install-recommends -y \
    ca-certificates \
    curl \
    jq \
    gnupg \
    && rm -rf /var/lib/apt/lists/*

RUN curl -fL https://getcli.jfrog.io | sh && \
    mv jfrog /usr/local/bin/

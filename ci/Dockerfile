ARG base_image=ubuntu:bionic
FROM ${base_image}

RUN apt-get update && apt-get install --no-install-recommends -y \
    ca-certificates \
    curl \
    jq \
    && rm -rf /var/lib/apt/lists/*

/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.util;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.resourcematch.ListMatchingResourcesRequest;
import org.cloudfoundry.client.v2.resourcematch.ListMatchingResourcesResponse;
import org.cloudfoundry.client.v2.resourcematch.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utilities for matching resources
 */
public final class ResourceMatchingUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger("cloudfoundry-client.resource-matching");

    private ResourceMatchingUtils() {
    }

    public static Mono<List<ArtifactMetadata>> getMatchedResources(CloudFoundryClient cloudFoundryClient, Path application) {
        Map<String, ArtifactMetadata> artifactMetadatas;
        try {
            Path root = FileUtils.normalize(application);

            artifactMetadatas = Files.walk(root).parallel()
                .filter(path -> !Files.isDirectory(path))
                .map(path -> new ArtifactMetadata(FileUtils.hash(path), FileUtils.getRelativePathName(root, path), FileUtils.permissions(path), FileUtils.size(path)))
                .collect(Collectors.toMap(ArtifactMetadata::getHash, Function.identity()));
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }

        return requestListMatchingResources(cloudFoundryClient, artifactMetadatas.values())
            .flatMapIterable(ListMatchingResourcesResponse::getResources)
            .map(resource -> artifactMetadatas.get(resource.getHash()))
            .collectList()
            .doOnNext(matched -> LOGGER.debug("{} resources matched totaling {}", matched.size(), SizeUtils.asIbi(matched.stream()
                .mapToInt(ArtifactMetadata::getSize)
                .sum())));
    }

    private static Mono<ListMatchingResourcesResponse> requestListMatchingResources(CloudFoundryClient cloudFoundryClient, Collection<ArtifactMetadata> artifactMetadatas) {
        ListMatchingResourcesRequest request = artifactMetadatas.stream()
            .reduce(ListMatchingResourcesRequest.builder(), (builder, artifactMetadata) -> builder.resource(Resource.builder()
                .hash(artifactMetadata.getHash())
                .mode(artifactMetadata.getPermissions())
                .size(artifactMetadata.getSize())
                .build()), (a, b) -> a.addAllResources(b.build().getResources()))
            .build();

        return cloudFoundryClient.resourceMatch()
            .list(request);
    }

    /**
     * Metadata information about a given artifact
     */
    public static final class ArtifactMetadata {

        private final String hash;

        private final String path;

        private final String permissions;

        private final int size;

        /**
         * Creates a new instance
         *
         * @param hash        the SHA-1 hash of the artifact
         * @param path        the relative path of the artifact
         * @param permissions the UNIX permissions of the artifact
         * @param size        the size of the artifact
         */
        public ArtifactMetadata(String hash, String path, String permissions, int size) {
            this.hash = hash;
            this.path = path;
            this.permissions = permissions;
            this.size = size;
        }

        /**
         * Returns the SHA-1 hash of the artifact
         */
        public String getHash() {
            return this.hash;
        }

        /**
         * Returns the path of the artifact
         */
        public String getPath() {
            return this.path;
        }

        /**
         * Returns the permissions of the artifact
         */
        public String getPermissions() {
            return this.permissions;
        }


        /**
         * Returns the size of the artifact
         */
        public int getSize() {
            return this.size;
        }

    }


}

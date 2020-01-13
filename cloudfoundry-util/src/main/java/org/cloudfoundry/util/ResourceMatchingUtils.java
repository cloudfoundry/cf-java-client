/*
 * Copyright 2013-2020 the original author or authors.
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

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.resourcematch.ListMatchingResourcesRequest;
import org.cloudfoundry.client.v2.resourcematch.ListMatchingResourcesResponse;
import org.cloudfoundry.client.v2.resourcematch.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

/**
 * Utilities for matching resources
 */
public final class ResourceMatchingUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger("cloudfoundry-client.resource-matching");

    private ResourceMatchingUtils() {
    }

    public static Mono<List<ArtifactMetadata>> getMatchedResources(CloudFoundryClient cloudFoundryClient, Path application) {
        return (Files.isDirectory(application) ? getArtifactMetadataFromDirectory(application) : getArtifactMetadataFromZip(application))
            .collectMap(ArtifactMetadata::getHash)
            .flatMapMany(artifactMetadatas -> requestListMatchingResources(cloudFoundryClient, artifactMetadatas.values())
                .flatMapIterable(ListMatchingResourcesResponse::getResources)
                .map(resource -> artifactMetadatas.get(resource.getHash())))
            .collectList()
            .doOnNext(matched -> LOGGER.debug("{} resources matched totaling {}", matched.size(), SizeUtils.asIbi(matched.stream()
                .mapToInt(ArtifactMetadata::getSize)
                .sum())))
            .subscribeOn(Schedulers.elastic());
    }

    private static Flux<ArtifactMetadata> getArtifactMetadataFromDirectory(Path application) {
        return Flux
            .defer(() -> {
                try {
                    return Flux.fromStream(Files.walk(application));
                } catch (IOException e) {
                    throw Exceptions.propagate(e);
                }
            })
            .filter(path -> !Files.isDirectory(path))
            .map(path -> new ArtifactMetadata(FileUtils.hash(path), FileUtils.getRelativePathName(application, path), FileUtils.permissions(path), FileUtils.size(path)));
    }

    private static Flux<ArtifactMetadata> getArtifactMetadataFromZip(Path application) {
        List<ArtifactMetadata> artifactMetadatas = new ArrayList<>();

        try (ZipFile zipFile = new ZipFile(application.toFile())) {
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();

            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();

                if (!entry.isDirectory()) {
                    try (InputStream in = zipFile.getInputStream(entry)) {
                        String hash = FileUtils.hash(in);
                        String path = entry.getName();
                        String permissions = FileUtils.permissions(entry.getUnixMode());
                        int size = (int) entry.getSize();

                        artifactMetadatas.add(new ArtifactMetadata(hash, path, permissions, size));
                    }

                }
            }
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }

        return Flux.fromIterable(artifactMetadatas);
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
         *
         * @return the SHA-1 hash of the artifact
         */
        public String getHash() {
            return this.hash;
        }

        /**
         * Returns the path of the artifact
         *
         * @return the path of the artifact
         */
        public String getPath() {
            return this.path;
        }

        /**
         * Returns the permissions of the artifact
         *
         * @return the permissions of the artifact
         */
        public String getPermissions() {
            return this.permissions;
        }


        /**
         * Returns the size of the artifact
         *
         * @return the size of the artifact
         */
        public int getSize() {
            return this.size;
        }

    }


}

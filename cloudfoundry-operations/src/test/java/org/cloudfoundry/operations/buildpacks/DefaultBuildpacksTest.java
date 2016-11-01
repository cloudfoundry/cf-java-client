/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry.operations.buildpacks;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.buildpacks.BuildpackResource;
import org.cloudfoundry.client.v2.buildpacks.CreateBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksRequest;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksResponse;
import org.cloudfoundry.client.v2.buildpacks.UploadBuildpackResponse;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Duration;

import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DefaultBuildpacksTest extends AbstractOperationsTest {

    private final ByteArrayInputStream stream = new ByteArrayInputStream(new byte[0]);

    private final DefaultBuildpacks buildpacks = new DefaultBuildpacks(Mono.just(this.cloudFoundryClient), p -> this.stream);

    @Test
    public void create() {
        requestCreateBuildpack(this.cloudFoundryClient, "test-buildpack", 1, true);
        requestUploadBuildpack(this.cloudFoundryClient, "test-buildpack-id", this.stream, "test-buildpack.zip");

        this.buildpacks
            .create(CreateBuildpackRequest.builder()
                .buildpack(Paths.get("test-buildpack"))
                .fileName("test-buildpack.zip")
                .name("test-buildpack")
                .enable(true)
                .position(1)
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        requestBuildpacks(this.cloudFoundryClient);

        this.buildpacks
            .list()
            .as(StepVerifier::create)
            .expectNext(Buildpack.builder()
                .enabled(true)
                .filename("test-buildpack-filename")
                .id("test-buildpack-id")
                .locked(true)
                .name("test-buildpack-name")
                .position(1)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    private static void requestBuildpacks(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.buildpacks()
            .list(ListBuildpacksRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListBuildpacksResponse.builder())
                    .resource(fill(BuildpackResource.builder(), "buildpack-")
                        .build())
                    .build()));
    }

    private static void requestCreateBuildpack(CloudFoundryClient cloudFoundryClient, String name, Integer position, Boolean enable) {
        when(cloudFoundryClient.buildpacks()
            .create(org.cloudfoundry.client.v2.buildpacks.CreateBuildpackRequest.builder()
                .name(name)
                .position(position)
                .enabled(enable)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateBuildpackResponse.builder(), "buildpack-")
                    .build()));
    }

    private static void requestUploadBuildpack(CloudFoundryClient cloudFoundryClient, String buildpackId, InputStream buildpack, String filename) {
        when(cloudFoundryClient.buildpacks()
            .upload(org.cloudfoundry.client.v2.buildpacks.UploadBuildpackRequest.builder()
                .buildpackId(buildpackId)
                .buildpack(buildpack)
                .filename(filename)
                .build()))
            .thenReturn(Mono
                .just(fill(UploadBuildpackResponse.builder())
                    .build()));
    }

}

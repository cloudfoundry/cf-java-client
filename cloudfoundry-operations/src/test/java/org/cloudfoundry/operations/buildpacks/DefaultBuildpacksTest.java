/*
 * Copyright 2013-2021 the original author or authors.
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

import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.buildpacks.BuildpackResource;
import org.cloudfoundry.client.v3.buildpacks.BuildpackState;
import org.cloudfoundry.client.v3.buildpacks.CreateBuildpackResponse;
import org.cloudfoundry.client.v3.buildpacks.GetBuildpackRequest;
import org.cloudfoundry.client.v3.buildpacks.GetBuildpackResponse;
import org.cloudfoundry.client.v3.buildpacks.ListBuildpacksRequest;
import org.cloudfoundry.client.v3.buildpacks.ListBuildpacksResponse;
import org.cloudfoundry.client.v3.buildpacks.UpdateBuildpackResponse;
import org.cloudfoundry.client.v3.buildpacks.UploadBuildpackResponse;
import org.cloudfoundry.client.v3.jobs.GetJobRequest;
import org.cloudfoundry.client.v3.jobs.GetJobResponse;
import org.cloudfoundry.client.v3.jobs.JobState;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

final class DefaultBuildpacksTest extends AbstractOperationsTest {

    private final DefaultBuildpacks buildpacks = new DefaultBuildpacks(this.cloudFoundryClient);

    @Test
    void create() {
        requestBuildpack(this.cloudFoundryClient);
        requestCreateBuildpack(this.cloudFoundryClient, "test-buildpack", 1, true);
        requestUploadBuildpack(
                this.cloudFoundryClient, "test-buildpack-id", Paths.get("test-buildpack"));

        this.buildpacks
                .create(
                        CreateBuildpackRequest.builder()
                                .buildpack(Paths.get("test-buildpack"))
                                .name("test-buildpack")
                                .enable(true)
                                .position(1)
                                .build())
                .as(StepVerifier::create)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void delete() {
        requestListBuildpacks(this.cloudFoundryClient, "test-buildpack");
        requestDeleteBuildpack(this.cloudFoundryClient, "test-buildpack-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-id");

        StepVerifier.withVirtualTime(
                        () ->
                                this.buildpacks.delete(
                                        DeleteBuildpackRequest.builder()
                                                .name("test-buildpack")
                                                .build()))
                .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void list() {
        requestBuildpacks(this.cloudFoundryClient);

        this.buildpacks
                .list()
                .as(StepVerifier::create)
                .expectNext(
                        Buildpack.builder()
                                .enabled(true)
                                .filename("test-buildpack-filename")
                                .id("test-buildpack-id")
                                .locked(true)
                                .name("test-buildpack-name")
                                .position(1)
                                .stack("test-buildpack-stack")
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void rename() {
        requestListBuildpacks(this.cloudFoundryClient, "test-buildpack");
        requestUpdateBuildpack(this.cloudFoundryClient, "test-buildpack-id", "test-buildpack-new");

        this.buildpacks
                .rename(
                        RenameBuildpackRequest.builder()
                                .name("test-buildpack")
                                .newName("test-buildpack-new")
                                .build())
                .as(StepVerifier::create)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void update() {
        requestBuildpack(this.cloudFoundryClient);
        requestListBuildpacks(this.cloudFoundryClient, "test-buildpack");
        requestUpdateBuildpack(this.cloudFoundryClient, "test-buildpack-id", true, true, 5);

        this.buildpacks
                .update(
                        UpdateBuildpackRequest.builder()
                                .enable(true)
                                .lock(true)
                                .name("test-buildpack")
                                .position(5)
                                .build())
                .as(StepVerifier::create)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void updateWithBits() {
        requestBuildpack(this.cloudFoundryClient);
        requestListBuildpacks(this.cloudFoundryClient, "test-buildpack");
        requestUpdateBuildpack(this.cloudFoundryClient, "test-buildpack-id", true, true, 5);
        requestUploadBuildpack(
                this.cloudFoundryClient, "test-buildpack-id", Paths.get("test-buildpack"));

        this.buildpacks
                .update(
                        UpdateBuildpackRequest.builder()
                                .buildpack(Paths.get("test-buildpack"))
                                .enable(true)
                                .lock(true)
                                .name("test-buildpack")
                                .position(5)
                                .build())
                .as(StepVerifier::create)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    private static void requestBuildpack(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.buildpacksV3().get(any(GetBuildpackRequest.class)))
                .thenReturn(
                        Mono.just(
                                fill(GetBuildpackResponse.builder(), "buildpack-")
                                        .state(BuildpackState.READY)
                                        .build()));
    }

    private static void requestBuildpacks(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient
                        .buildpacksV3()
                        .list(ListBuildpacksRequest.builder().page(1).build()))
                .thenReturn(
                        Mono.just(
                                fill(ListBuildpacksResponse.builder())
                                        .resource(
                                                fill(BuildpackResource.builder(), "buildpack-")
                                                        .state(BuildpackState.READY)
                                                        .build())
                                        .build()));
    }

    private static void requestCreateBuildpack(
            CloudFoundryClient cloudFoundryClient, String name, Integer position, Boolean enable) {
        when(cloudFoundryClient
                        .buildpacksV3()
                        .create(
                                org.cloudfoundry.client.v3.buildpacks.CreateBuildpackRequest
                                        .builder()
                                        .name(name)
                                        .position(position)
                                        .enabled(enable)
                                        .build()))
                .thenReturn(
                        Mono.just(fill(CreateBuildpackResponse.builder(), "buildpack-").build()));
    }

    private static void requestDeleteBuildpack(
            CloudFoundryClient cloudFoundryClient, String buildpackId) {
        when(cloudFoundryClient
                        .buildpacksV3()
                        .delete(
                                org.cloudfoundry.client.v3.buildpacks.DeleteBuildpackRequest
                                        .builder()
                                        .buildpackId(buildpackId)
                                        .build()))
                .thenReturn(Mono.just("test-job-id"));
    }

    private static void requestJobSuccess(CloudFoundryClient cloudFoundryClient, String jobId) {
        when(cloudFoundryClient.jobsV3().get(GetJobRequest.builder().jobId(jobId).build()))
                .thenReturn(
                        Mono.defer(
                                new Supplier<Mono<GetJobResponse>>() {

                                    private final Queue<GetJobResponse> responses =
                                            new LinkedList<>(
                                                    Arrays.asList(
                                                            fill(
                                                                            GetJobResponse
                                                                                    .builder(),
                                                                            "test-job-")
                                                                    .state(JobState.PROCESSING)
                                                                    .build(),
                                                            fill(GetJobResponse.builder(), "job-")
                                                                    .state(JobState.COMPLETE)
                                                                    .build()));

                                    @Override
                                    public Mono<GetJobResponse> get() {
                                        return Mono.just(this.responses.poll());
                                    }
                                }));
    }

    private static void requestListBuildpacks(CloudFoundryClient cloudFoundryClient, String name) {
        when(cloudFoundryClient
                        .buildpacksV3()
                        .list(ListBuildpacksRequest.builder().name(name).page(1).build()))
                .thenReturn(
                        Mono.just(
                                fill(ListBuildpacksResponse.builder())
                                        .resource(
                                                fill(BuildpackResource.builder())
                                                        .id("test-buildpack-id")
                                                        .name(name)
                                                        .build())
                                        .build()));
    }

    private static void requestUpdateBuildpack(
            CloudFoundryClient cloudFoundryClient, String buildpackId, String name) {
        when(cloudFoundryClient
                        .buildpacksV3()
                        .update(
                                org.cloudfoundry.client.v3.buildpacks.UpdateBuildpackRequest
                                        .builder()
                                        .buildpackId(buildpackId)
                                        .name(name)
                                        .build()))
                .thenReturn(Mono.just(fill(UpdateBuildpackResponse.builder()).build()));
    }

    private static void requestUpdateBuildpack(
            CloudFoundryClient cloudFoundryClient,
            String buildpackId,
            boolean enabled,
            boolean locked,
            Integer position) {
        when(cloudFoundryClient
                        .buildpacksV3()
                        .update(
                                org.cloudfoundry.client.v3.buildpacks.UpdateBuildpackRequest
                                        .builder()
                                        .buildpackId(buildpackId)
                                        .enabled(enabled)
                                        .locked(locked)
                                        .position(position)
                                        .build()))
                .thenReturn(Mono.just(fill(UpdateBuildpackResponse.builder()).build()));
    }

    private static void requestUploadBuildpack(
            CloudFoundryClient cloudFoundryClient, String buildpackId, Path buildpack) {
        when(cloudFoundryClient
                        .buildpacksV3()
                        .upload(
                                org.cloudfoundry.client.v3.buildpacks.UploadBuildpackRequest
                                        .builder()
                                        .buildpackId(buildpackId)
                                        .bits(buildpack)
                                        .build()))
                .thenReturn(Mono.just(fill(UploadBuildpackResponse.builder()).build()));
    }
}

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

package org.cloudfoundry.operations.buildpacks;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.buildpacks.BuildpackEntity;
import org.cloudfoundry.client.v2.buildpacks.BuildpackResource;
import org.cloudfoundry.client.v2.buildpacks.CreateBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.DeleteBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksRequest;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksResponse;
import org.cloudfoundry.client.v2.buildpacks.UpdateBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.UploadBuildpackResponse;
import org.cloudfoundry.client.v2.jobs.GetJobRequest;
import org.cloudfoundry.client.v2.jobs.GetJobResponse;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DefaultBuildpacksTest extends AbstractOperationsTest {

    private final DefaultBuildpacks buildpacks = new DefaultBuildpacks(Mono.just(this.cloudFoundryClient));

    @Test
    public void create() {
        requestCreateBuildpack(this.cloudFoundryClient, "test-buildpack", 1, true);
        requestUploadBuildpack(this.cloudFoundryClient, "test-buildpack-id", Paths.get("test-buildpack"), "test-buildpack");

        this.buildpacks
            .create(CreateBuildpackRequest.builder()
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
    public void delete() {
        requestListBuildpacks(this.cloudFoundryClient, "test-buildpack");
        requestDeleteBuildpack(this.cloudFoundryClient, "test-buildpack-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-id");

        StepVerifier.withVirtualTime(() -> this.buildpacks
            .delete(DeleteBuildpackRequest.builder()
                .name("test-buildpack")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
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
                .stack("test-buildpack-stack")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void rename() {
        requestListBuildpacks(this.cloudFoundryClient, "test-buildpack");
        requestUpdateBuildpack(this.cloudFoundryClient, "test-buildpack-id", "test-buildpack-new");

        this.buildpacks
            .rename(RenameBuildpackRequest.builder()
                .name("test-buildpack")
                .newName("test-buildpack-new")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        requestListBuildpacks(this.cloudFoundryClient, "test-buildpack");
        requestUpdateBuildpack(this.cloudFoundryClient, "test-buildpack-id", true, true, 5);

        this.buildpacks
            .update(UpdateBuildpackRequest.builder()
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
    public void updateWithBits() {
        requestListBuildpacks(this.cloudFoundryClient, "test-buildpack");
        requestUpdateBuildpack(this.cloudFoundryClient, "test-buildpack-id", true, true, 5);
        requestUploadBuildpack(this.cloudFoundryClient, "test-buildpack-id", Paths.get("test-buildpack"), "test-buildpack");

        this.buildpacks
            .update(UpdateBuildpackRequest.builder()
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

    private static void requestDeleteBuildpack(CloudFoundryClient cloudFoundryClient, String buildpackId) {
        when(cloudFoundryClient.buildpacks()
            .delete(org.cloudfoundry.client.v2.buildpacks.DeleteBuildpackRequest.builder()
                .async(true)
                .buildpackId(buildpackId)
                .build()))
            .thenReturn(Mono
                .just(fill(DeleteBuildpackResponse.builder())
                    .entity(fill(JobEntity.builder())
                        .id("test-job-id")
                        .build())
                    .build()));
    }

    private static void requestJobSuccess(CloudFoundryClient cloudFoundryClient, String jobId) {
        when(cloudFoundryClient.jobs()
            .get(GetJobRequest.builder()
                .jobId(jobId)
                .build()))
            .thenReturn(Mono
                .defer(new Supplier<Mono<GetJobResponse>>() {

                    private final Queue<GetJobResponse> responses = new LinkedList<>(Arrays.asList(
                        fill(GetJobResponse.builder(), "test-job-")
                            .entity(fill(JobEntity.builder())
                                .status("running")
                                .build())
                            .build(),
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("finished")
                                .build())
                            .build()
                    ));

                    @Override
                    public Mono<GetJobResponse> get() {
                        return Mono.just(this.responses.poll());
                    }

                }));
    }

    private static void requestListBuildpacks(CloudFoundryClient cloudFoundryClient, String name) {
        when(cloudFoundryClient.buildpacks()
            .list(ListBuildpacksRequest.builder()
                .name(name)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(ListBuildpacksResponse.builder()
                    .resource(BuildpackResource.builder()
                        .metadata(Metadata.builder()
                            .id("test-buildpack-id")
                            .build())
                        .entity(BuildpackEntity.builder()
                            .name(name)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestUpdateBuildpack(CloudFoundryClient cloudFoundryClient, String buildpackId, String name) {
        when(cloudFoundryClient.buildpacks()
            .update(org.cloudfoundry.client.v2.buildpacks.UpdateBuildpackRequest.builder()
                .buildpackId(buildpackId)
                .name(name)
                .build()))
            .thenReturn(Mono
                .just(fill(UpdateBuildpackResponse.builder())
                    .build()));
    }

    private static void requestUpdateBuildpack(CloudFoundryClient cloudFoundryClient, String buildpackId, boolean enabled, boolean locked, Integer position) {
        when(cloudFoundryClient.buildpacks()
            .update(org.cloudfoundry.client.v2.buildpacks.UpdateBuildpackRequest.builder()
                .buildpackId(buildpackId)
                .enabled(enabled)
                .locked(locked)
                .position(position)
                .build()))
            .thenReturn(Mono
                .just(fill(UpdateBuildpackResponse.builder())
                    .build()));
    }

    private static void requestUploadBuildpack(CloudFoundryClient cloudFoundryClient, String buildpackId, Path buildpack, String filename) {
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

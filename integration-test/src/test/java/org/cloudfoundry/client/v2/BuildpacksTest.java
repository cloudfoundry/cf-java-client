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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.buildpacks.BuildpackEntity;
import org.cloudfoundry.client.v2.buildpacks.BuildpackResource;
import org.cloudfoundry.client.v2.buildpacks.CreateBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.CreateBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.DeleteBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.GetBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksRequest;
import org.cloudfoundry.client.v2.buildpacks.UpdateBuildpackRequest;
import org.cloudfoundry.client.v2.buildpacks.UploadBuildpackRequest;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

public final class BuildpacksTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Test
    public void create() throws TimeoutException, InterruptedException {
        String buildpackName = this.nameFactory.getBuildpackName();

        this.cloudFoundryClient.buildpacks()
            .create(CreateBuildpackRequest.builder()
                .enabled(false)
                .locked(true)
                .name(buildpackName)
                .position(2)
                .build())
            .map(ResourceUtils::getEntity)
            .as(StepVerifier::create)
            .expectNext(BuildpackEntity.builder()
                .enabled(false)
                .locked(true)
                .name(buildpackName)
                .position(2)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() throws TimeoutException, InterruptedException {
        String buildpackName = this.nameFactory.getBuildpackName();

        createBuildpackId(this.cloudFoundryClient, buildpackName)
            .flatMap(buildpackId -> this.cloudFoundryClient.buildpacks()
                .delete(DeleteBuildpackRequest.builder()
                    .async(false)
                    .buildpackId(buildpackId)
                    .build()))
            .then(getBuildpackEntity(this.cloudFoundryClient, buildpackName))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAsync() throws TimeoutException, InterruptedException {
        String buildpackName = this.nameFactory.getBuildpackName();

        createBuildpackId(this.cloudFoundryClient, buildpackName)
            .flatMap(buildpackId -> this.cloudFoundryClient.buildpacks()
                .delete(DeleteBuildpackRequest.builder()
                    .async(true)
                    .buildpackId(buildpackId)
                    .build()))
            .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job))
            .then(getBuildpackEntity(this.cloudFoundryClient, buildpackName))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws TimeoutException, InterruptedException {
        String buildpackName = this.nameFactory.getBuildpackName();

        createBuildpackId(this.cloudFoundryClient, buildpackName)
            .flatMap(buildpackId -> this.cloudFoundryClient.buildpacks()
                .get(GetBuildpackRequest.builder()
                    .buildpackId(buildpackId)
                    .build()))
            .map(ResourceUtils::getEntity)
            .as(StepVerifier::create)
            .expectNext(BuildpackEntity.builder()
                .enabled(false)
                .locked(false)
                .name(buildpackName)
                .position(3)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws TimeoutException, InterruptedException {
        String buildpackName = this.nameFactory.getBuildpackName();

        createBuildpackId(this.cloudFoundryClient, buildpackName)
            .flatMapMany(buildpackId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.buildpacks()
                    .list(ListBuildpacksRequest.builder()
                        .build()))
                .map(ResourceUtils::getEntity)
                .filter(entity -> buildpackName.equals(entity.getName())))
            .as(StepVerifier::create)
            .expectNext(BuildpackEntity.builder()
                .enabled(false)
                .locked(false)
                .name(buildpackName)
                .position(3)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByName() throws TimeoutException, InterruptedException {
        String buildpackName = this.nameFactory.getBuildpackName();

        createBuildpackId(this.cloudFoundryClient, buildpackName)
            .flatMapMany(buildpackId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.buildpacks()
                    .list(ListBuildpacksRequest.builder()
                        .name(buildpackName)
                        .build()))
                .map(ResourceUtils::getEntity))
            .as(StepVerifier::create)
            .expectNext(BuildpackEntity.builder()
                .enabled(false)
                .locked(false)
                .name(buildpackName)
                .position(3)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() throws TimeoutException, InterruptedException {
        String buildpackName = this.nameFactory.getBuildpackName();

        createBuildpackId(this.cloudFoundryClient, buildpackName)
            .flatMap(buildpackId -> this.cloudFoundryClient.buildpacks()
                .update(UpdateBuildpackRequest.builder()
                    .buildpackId(buildpackId)
                    .enabled(true)
                    .locked(true)
                    .position(2)
                    .build()))
            .map(ResourceUtils::getEntity)
            .as(StepVerifier::create)
            .expectNext(BuildpackEntity.builder()
                .enabled(true)
                .locked(true)
                .name(buildpackName)
                .position(2)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void upload() throws TimeoutException, InterruptedException, IOException {
        Path buildpack = new ClassPathResource("test-buildpack.zip").getFile().toPath();
        String buildpackName = this.nameFactory.getBuildpackName();

        createBuildpackId(this.cloudFoundryClient, buildpackName)
            .flatMap(buildpackId -> this.cloudFoundryClient.buildpacks()
                .upload(UploadBuildpackRequest.builder()
                    .buildpack(buildpack)
                    .buildpackId(buildpackId)
                    .filename(buildpack.getFileName().toString())
                    .build()))
            .map(ResourceUtils::getEntity)
            .as(StepVerifier::create)
            .expectNext(BuildpackEntity.builder()
                .enabled(false)
                .filename(buildpack.getFileName().toString())
                .locked(false)
                .name(buildpackName)
                .position(3)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void uploadDirectory() throws TimeoutException, InterruptedException, IOException {
        Path buildpack = new ClassPathResource("test-buildpack").getFile().toPath();
        String buildpackName = this.nameFactory.getBuildpackName();
        String filename = buildpack.getFileName().toString();

        createBuildpackId(this.cloudFoundryClient, buildpackName)
            .flatMap(buildpackId -> this.cloudFoundryClient.buildpacks()
                .upload(UploadBuildpackRequest.builder()
                    .buildpack(buildpack)
                    .buildpackId(buildpackId)
                    .filename(filename)
                    .build()))
            .map(ResourceUtils::getEntity)
            .as(StepVerifier::create)
            .expectNext(BuildpackEntity.builder()
                .enabled(false)
                .filename(filename + ".zip")
                .locked(false)
                .name(buildpackName)
                .position(3)
                .build())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createBuildpackId(CloudFoundryClient cloudFoundryClient, String buildpackName) {
        return requestCreateBuildpack(cloudFoundryClient, buildpackName)
            .map(ResourceUtils::getId);
    }

    private static Mono<BuildpackEntity> getBuildpackEntity(CloudFoundryClient cloudFoundryClient, String buildpackName) {
        return requestListBuildpacks(cloudFoundryClient, buildpackName)
            .map(ResourceUtils::getEntity)
            .singleOrEmpty();
    }

    private static Mono<CreateBuildpackResponse> requestCreateBuildpack(CloudFoundryClient cloudFoundryClient, String buildpackName) {
        return cloudFoundryClient.buildpacks()
            .create(CreateBuildpackRequest.builder()
                .enabled(false)
                .locked(false)
                .name(buildpackName)
                .position(3)
                .build());
    }

    private static Flux<BuildpackResource> requestListBuildpacks(CloudFoundryClient cloudFoundryClient, String buildpackName) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.buildpacks()
                .list(ListBuildpacksRequest.builder()
                    .name(buildpackName)
                    .build()));
    }

}

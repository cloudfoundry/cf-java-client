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

package org.cloudfoundry.client.v3;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.buildpacks.BuildpackResource;
import org.cloudfoundry.client.v3.buildpacks.CreateBuildpackRequest;
import org.cloudfoundry.client.v3.buildpacks.CreateBuildpackResponse;
import org.cloudfoundry.client.v3.buildpacks.DeleteBuildpackRequest;
import org.cloudfoundry.client.v3.buildpacks.GetBuildpackRequest;
import org.cloudfoundry.client.v3.buildpacks.GetBuildpackResponse;
import org.cloudfoundry.client.v3.buildpacks.ListBuildpacksRequest;
import org.cloudfoundry.client.v3.buildpacks.UpdateBuildpackRequest;
import org.cloudfoundry.client.v3.buildpacks.UploadBuildpackRequest;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

public final class BuildpacksTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Test
    public void create() {
        String buildpackName = this.nameFactory.getBuildpackName();

        this.cloudFoundryClient.buildpacksV3()
            .create(CreateBuildpackRequest.builder()
                .enabled(false)
                .locked(true)
                .name(buildpackName)
                .position(2)
                .build())
            .thenMany(requestListBuildpacks(this.cloudFoundryClient, buildpackName)
                .map(BuildpackResource::getName))
            .as(StepVerifier::create)
            .expectNext(buildpackName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String buildpackName = this.nameFactory.getBuildpackName();

        createBuildpackId(this.cloudFoundryClient, buildpackName)
            .flatMap(buildpackId -> this.cloudFoundryClient.buildpacksV3()
                .delete(DeleteBuildpackRequest.builder()
                    .buildpackId(buildpackId)
                    .build())
                .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job)))
            .thenMany(requestListBuildpacks(this.cloudFoundryClient, buildpackName))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String buildpackName = this.nameFactory.getBuildpackName();

        createBuildpackId(this.cloudFoundryClient, buildpackName)
            .flatMap(buildpackId -> this.cloudFoundryClient.buildpacksV3()
                .get(GetBuildpackRequest.builder()
                    .buildpackId(buildpackId)
                    .build()))
            .map(GetBuildpackResponse::getName)
            .as(StepVerifier::create)
            .expectNext(buildpackName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String buildpackName = this.nameFactory.getBuildpackName();

        createBuildpackId(this.cloudFoundryClient, buildpackName)
            .flatMapMany(buildpackId -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.buildpacksV3()
                    .list(ListBuildpacksRequest.builder()
                        .page(page)
                        .build()))
                .filter(resource -> buildpackName.equals(resource.getName()))
                .map(BuildpackResource::getPosition))
            .as(StepVerifier::create)
            .expectNext(3)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByName() {
        String buildpackName = this.nameFactory.getBuildpackName();

        createBuildpackId(this.cloudFoundryClient, buildpackName)
            .flatMapMany(buildpackId -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.buildpacksV3()
                    .list(ListBuildpacksRequest.builder()
                        .name(buildpackName)
                        .page(page)
                        .build())))
            .map(BuildpackResource::getName)
            .as(StepVerifier::create)
            .expectNext(buildpackName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String buildpackName = this.nameFactory.getBuildpackName();

        createBuildpackId(this.cloudFoundryClient, buildpackName)
            .flatMap(buildpackId -> this.cloudFoundryClient.buildpacksV3()
                .update(UpdateBuildpackRequest.builder()
                    .buildpackId(buildpackId)
                    .enabled(true)
                    .locked(true)
                    .position(4)
                    .build()))
            .thenMany(requestListBuildpacks(this.cloudFoundryClient, buildpackName)
                .map(BuildpackResource::getPosition))
            .as(StepVerifier::create)
            .expectNext(4)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void upload() throws IOException {
        Path buildpack = new ClassPathResource("test-buildpack.zip").getFile().toPath();
        String buildpackName = this.nameFactory.getBuildpackName();

        createBuildpackId(this.cloudFoundryClient, buildpackName)
            .flatMap(buildpackId -> this.cloudFoundryClient.buildpacksV3()
                .upload(UploadBuildpackRequest.builder()
                    .bits(buildpack)
                    .buildpackId(buildpackId)
                    .build()))
            .thenMany(requestListBuildpacks(this.cloudFoundryClient, buildpackName)
                .map(BuildpackResource::getName))
            .as(StepVerifier::create)
            .expectNext(buildpackName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void uploadDirectory() throws IOException {
        Path buildpack = new ClassPathResource("test-buildpack").getFile().toPath();
        String buildpackName = this.nameFactory.getBuildpackName();

        createBuildpackId(this.cloudFoundryClient, buildpackName)
            .flatMap(buildpackId -> this.cloudFoundryClient.buildpacksV3()
                .upload(UploadBuildpackRequest.builder()
                    .bits(buildpack)
                    .buildpackId(buildpackId)
                    .build()))
            .thenMany(requestListBuildpacks(this.cloudFoundryClient, buildpackName)
                .map(BuildpackResource::getName))
            .as(StepVerifier::create)
            .expectNext(buildpackName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createBuildpackId(CloudFoundryClient cloudFoundryClient, String buildpackName) {
        return requestCreateBuildpack(cloudFoundryClient, buildpackName)
            .map(CreateBuildpackResponse::getId);
    }

    private static Mono<CreateBuildpackResponse> requestCreateBuildpack(CloudFoundryClient cloudFoundryClient, String buildpackName) {
        return cloudFoundryClient.buildpacksV3()
            .create(CreateBuildpackRequest.builder()
                .enabled(false)
                .locked(false)
                .name(buildpackName)
                .position(3)
                .stack("cflinuxfs3")
                .build());
    }

    private static Flux<BuildpackResource> requestListBuildpacks(CloudFoundryClient cloudFoundryClient, String buildpackName) {
        return PaginationUtils.requestClientV3Resources(page ->
            cloudFoundryClient.buildpacksV3()
                .list(ListBuildpacksRequest.builder()
                    .name(buildpackName)
                    .page(page)
                    .build()));
    }

}

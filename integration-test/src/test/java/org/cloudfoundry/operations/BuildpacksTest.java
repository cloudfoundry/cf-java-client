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

package org.cloudfoundry.operations;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.operations.buildpacks.Buildpack;
import org.cloudfoundry.operations.buildpacks.CreateBuildpackRequest;
import org.cloudfoundry.operations.buildpacks.DeleteBuildpackRequest;
import org.cloudfoundry.operations.buildpacks.UpdateBuildpackRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public final class BuildpacksTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Test
    public void create() throws IOException {
        String buildpackName = this.nameFactory.getBuildpackName();

        this.cloudFoundryOperations.buildpacks()
            .create(CreateBuildpackRequest.builder()
                .buildpack(new ClassPathResource("test-buildpack.zip").getFile().toPath())
                .name(buildpackName)
                .position(Integer.MAX_VALUE)
                .build())
            .thenMany(this.cloudFoundryOperations.buildpacks()
                .list())
            .filter(buildpack -> buildpackName.equals(buildpack.getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void createFromDirectory() throws IOException {
        String buildpackName = this.nameFactory.getBuildpackName();

        this.cloudFoundryOperations.buildpacks()
            .create(CreateBuildpackRequest.builder()
                .buildpack(new ClassPathResource("test-buildpack").getFile().toPath())
                .name(buildpackName)
                .position(Integer.MAX_VALUE)
                .build())
            .thenMany(this.cloudFoundryOperations.buildpacks()
                .list())
            .filter(buildpack -> buildpackName.equals(buildpack.getName()))
            .map(Buildpack::getFilename)
            .as(StepVerifier::create)
            .expectNext("test-buildpack.zip")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() throws IOException {
        String buildpackName = this.nameFactory.getBuildpackName();

        createBuildpack(this.cloudFoundryOperations, buildpackName)
            .then(this.cloudFoundryOperations.buildpacks()
                .delete(DeleteBuildpackRequest.builder()
                    .name(buildpackName)
                    .build()))
            .thenMany(this.cloudFoundryOperations.buildpacks()
                .list())
            .filter(buildpack -> buildpackName.equals(buildpack.getName()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteBuildpackNotFound() {
        String buildpackName = this.nameFactory.getBuildpackName();

        this.cloudFoundryOperations.buildpacks()
            .delete(DeleteBuildpackRequest.builder()
                .name(buildpackName)
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Buildpack %s not found", buildpackName))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws IOException {
        String buildpackName = this.nameFactory.getBuildpackName();

        createBuildpack(this.cloudFoundryOperations, buildpackName)
            .thenMany(this.cloudFoundryOperations.buildpacks()
                .list())
            .filter(buildpack -> buildpackName.equals(buildpack.getName()))
            .map(Buildpack::getFilename)
            .as(StepVerifier::create)
            .expectNext("test-buildpack.zip")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() throws IOException {
        String buildpackName = this.nameFactory.getBuildpackName();

        createBuildpack(this.cloudFoundryOperations, buildpackName)
            .then(this.cloudFoundryOperations.buildpacks()
                .update(UpdateBuildpackRequest.builder()
                    .enable(true)
                    .name(buildpackName)
                    .build()))
            .thenMany(this.cloudFoundryOperations.buildpacks()
                .list())
            .filter(buildpack -> buildpackName.equals(buildpack.getName()))
            .map(Buildpack::getEnabled)
            .as(StepVerifier::create)
            .expectNext(true)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void updateFromDirectory() throws IOException {
        String buildpackName = this.nameFactory.getBuildpackName();

        createBuildpack(this.cloudFoundryOperations, buildpackName)
            .then(this.cloudFoundryOperations.buildpacks()
                .update(UpdateBuildpackRequest.builder()
                    .buildpack(new ClassPathResource("test-buildpack").getFile().toPath())
                    .enable(true)
                    .name(buildpackName)
                    .build()))
            .thenMany(this.cloudFoundryOperations.buildpacks()
                .list())
            .filter(buildpack -> buildpackName.equals(buildpack.getName()))
            .map(Buildpack::getFilename)
            .as(StepVerifier::create)
            .expectNext("test-buildpack.zip")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<Void> createBuildpack(CloudFoundryOperations cloudFoundryOperations, String buildpackName) throws IOException {
        return cloudFoundryOperations.buildpacks()
            .create(CreateBuildpackRequest.builder()
                .buildpack(new ClassPathResource("test-buildpack.zip").getFile().toPath())
                .enable(false)
                .name(buildpackName)
                .position(Integer.MAX_VALUE)
                .build());
    }

}

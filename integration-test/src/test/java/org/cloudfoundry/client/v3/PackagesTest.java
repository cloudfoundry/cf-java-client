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
import org.cloudfoundry.client.v3.applications.Application;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.ApplicationRelationships;
import org.cloudfoundry.client.v3.packages.CreatePackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageRequest;
import org.cloudfoundry.client.v3.packages.Package;
import org.cloudfoundry.client.v3.packages.PackageType;
import org.cloudfoundry.client.v3.packages.UploadPackageRequest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.client.v3.packages.PackageState.PROCESSING_UPLOAD;
import static org.cloudfoundry.client.v3.packages.PackageState.READY;

@Ignore("Until Packages are no longer experimental")
public final class PackagesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> spaceId;

    @Test
    public void upload() {
        String applicationName = this.nameFactory.getApplicationName();

        this.spaceId
            .flatMap(spaceId -> this.cloudFoundryClient.applicationsV3()
                .create(CreateApplicationRequest.builder()
                    .name(applicationName)
                    .relationships(ApplicationRelationships.builder()
                        .space(ToOneRelationship.builder()
                            .data(Relationship.builder()
                                .id(spaceId)
                                .build())
                            .build())
                        .build())
                    .build()))
            .map(Application::getId)
            .flatMap(applicationId -> this.cloudFoundryClient.packages()
                .create(CreatePackageRequest.builder()
                    .type(PackageType.BITS)
                    .build()))
            .map(Package::getId)
            .flatMap(packageId -> {
                try {
                    return this.cloudFoundryClient.packages()
                        .upload(UploadPackageRequest.builder()
                            .packageId(packageId)
                            .bits(new ClassPathResource("test-application.zip").getFile().toPath())
                            .build());
                } catch (IOException e) {
                    throw Exceptions.propagate(e);
                }
            })
            .map(Package::getId)
            .flatMap(packageId -> this.cloudFoundryClient.packages()
                .get(GetPackageRequest.builder()
                    .packageId(packageId)
                    .build()))
            .map(Package::getState)
            .as(StepVerifier::create)
            .consumeNextWith(state -> assertThat(state).isIn(PROCESSING_UPLOAD, READY))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

}

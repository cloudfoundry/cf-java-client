/*
 * Copyright 2013-2022 the original author or authors.
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
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.applications.Application;
import org.cloudfoundry.client.v3.applications.ApplicationRelationships;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.packages.CreatePackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageResponse;
import org.cloudfoundry.client.v3.packages.Package;
import org.cloudfoundry.client.v3.packages.PackageState;
import org.cloudfoundry.client.v3.packages.PackageType;
import org.cloudfoundry.client.v3.packages.UploadPackageRequest;
import org.cloudfoundry.util.DelayTimeoutException;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.ResourceMatchingUtilsV3;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;
import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.DelayUtils.exponentialBackOff;

public class ResourceMatchTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> spaceId;

    //TODO how to check if resource matching is enabled on this CF instance?
    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.UNSPECIFIED) //TODO how to select this version?
    @Test
    public void upload() throws IOException {
        createAndUploadPackage()
            .flatMap(this::waitForReady)
            .then(ResourceMatchingUtilsV3.getMatchedResources(this.cloudFoundryClient, new ClassPathResource("test-application.zip").getFile().toPath()))
            .as(StepVerifier::create)
            .consumeNextWith(matchedResources -> assertThat(matchedResources).isNotEmpty())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private Mono<String> createAndUploadPackage() {
        String applicationName = this.nameFactory.getApplicationName();

        return this.spaceId
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
                    return Mono.error(Exceptions.propagate(e));
                }
            })
            .map(Package::getId);
    }

    private Mono<GetPackageResponse> waitForReady(String packageId) {
        return this.cloudFoundryClient.packages().get(GetPackageRequest.builder().packageId(packageId).build())
            .filter(packageResponse -> EnumSet.of(PackageState.READY, PackageState.FAILED, PackageState.EXPIRED).contains(packageResponse.getState()))
            .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofMinutes(5)))
            .filter(packageResponse -> packageResponse.getState() == PackageState.READY)
            .switchIfEmpty(ExceptionUtils.illegalState("Package %s failed upload processing", packageId))
            .onErrorResume(DelayTimeoutException.class, t -> ExceptionUtils.illegalState("Package %s timed out during upload processing", packageId));
    }
}

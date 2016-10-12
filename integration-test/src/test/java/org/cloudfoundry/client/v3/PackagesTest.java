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

package org.cloudfoundry.client.v3;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.applications.Application;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.Relationships;
import org.cloudfoundry.client.v3.packages.CreatePackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageRequest;
import org.cloudfoundry.client.v3.packages.Package;
import org.cloudfoundry.client.v3.packages.PackageType;
import org.cloudfoundry.client.v3.packages.State;
import org.cloudfoundry.client.v3.packages.UploadPackageRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.test.subscriber.ScriptedSubscriber;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.cloudfoundry.client.v3.packages.State.PROCESSING_UPLOAD;
import static org.cloudfoundry.client.v3.packages.State.READY;

public final class PackagesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> spaceId;

    @Test
    public void upload() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<State> subscriber = ScriptedSubscriber.<State>create()
            .expectValueWith(state -> PROCESSING_UPLOAD == state || READY == state, state -> String.format("expected state: PROCESSING_UPLOAD or READY: actual state: %s", state))
            .expectComplete();

        this.spaceId
            .then(spaceId -> this.cloudFoundryClient.applicationsV3()
                .create(CreateApplicationRequest.builder()
                    .name(applicationName)
                    .relationships(Relationships.builder()
                        .space(Relationship.builder()
                            .id(spaceId)
                            .build())
                        .build())
                    .build()))
            .map(Application::getId)
            .then(applicationId -> this.cloudFoundryClient.packages()
                .create(CreatePackageRequest.builder()
                    .applicationId(applicationId)
                    .type(PackageType.BITS)
                    .build()))
            .map(Package::getId)
            .then(packageId -> {
                try {
                    return this.cloudFoundryClient.packages()
                        .upload(UploadPackageRequest.builder()
                            .packageId(packageId)
                            .bits(new ClassPathResource("test-application.zip").getInputStream())
                            .build());
                } catch (IOException e) {
                    throw Exceptions.propagate(e);
                }
            })
            .map(Package::getId)
            .then(packageId -> this.cloudFoundryClient.packages()
                .get(GetPackageRequest.builder()
                    .packageId(packageId)
                    .build()))
            .map(Package::getState)
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

}

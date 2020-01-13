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
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.operations.networkpolicies.AddNetworkPolicyRequest;
import org.cloudfoundry.operations.networkpolicies.ListNetworkPoliciesRequest;
import org.cloudfoundry.operations.networkpolicies.Policy;
import org.cloudfoundry.operations.networkpolicies.RemoveNetworkPolicyRequest;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

public final class NetworkPoliciesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Autowired
    private Mono<String> spaceId;

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_1_12)
    @Test
    public void add() {
        String destinationApplicationName = this.nameFactory.getApplicationName();
        String sourceApplicationName = this.nameFactory.getApplicationName();
        Integer port = this.nameFactory.getPort();

        this.spaceId
            .flatMapMany(spaceId -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, destinationApplicationName, spaceId),
                createApplicationId(this.cloudFoundryClient, sourceApplicationName, spaceId)
            ))
            .thenMany(this.cloudFoundryOperations.networkPolicies()
                .add(AddNetworkPolicyRequest.builder()
                    .destination(destinationApplicationName)
                    .source(sourceApplicationName)
                    .startPort(port)
                    .build()))
            .thenMany(requestListNetworkPolicies(this.cloudFoundryOperations, sourceApplicationName))
            .map(Policy::getDestination)
            .as(StepVerifier::create)
            .expectNext(destinationApplicationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_1_12)
    @Test
    public void list() {
        String destinationApplicationName = this.nameFactory.getApplicationName();
        String sourceApplicationName = this.nameFactory.getApplicationName();
        Integer port = this.nameFactory.getPort();

        this.spaceId
            .flatMapMany(spaceId -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, destinationApplicationName, spaceId),
                createApplicationId(this.cloudFoundryClient, sourceApplicationName, spaceId)
            ))
            .thenMany(requestAddNetworkPolicy(this.cloudFoundryOperations, destinationApplicationName, port, sourceApplicationName))
            .thenMany(this.cloudFoundryOperations.networkPolicies()
                .list(ListNetworkPoliciesRequest.builder()
                    .source(sourceApplicationName)
                    .build()))
            .map(Policy::getStartPort)
            .as(StepVerifier::create)
            .expectNext(port)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_1_12)
    @Test
    public void remove() {
        String destinationApplicationName = this.nameFactory.getApplicationName();
        String sourceApplicationName = this.nameFactory.getApplicationName();
        Integer port = this.nameFactory.getPort();

        this.spaceId
            .flatMapMany(spaceId -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, destinationApplicationName, spaceId),
                createApplicationId(this.cloudFoundryClient, sourceApplicationName, spaceId)
            ))
            .thenMany(requestAddNetworkPolicy(this.cloudFoundryOperations, destinationApplicationName, port, sourceApplicationName))
            .thenMany(this.cloudFoundryOperations.networkPolicies()
                .remove(RemoveNetworkPolicyRequest.builder()
                    .destination(destinationApplicationName)
                    .source(sourceApplicationName)
                    .startPort(port)
                    .protocol("tcp")
                    .build()))
            .thenMany(requestListNetworkPolicies(this.cloudFoundryOperations, sourceApplicationName))
            .map(Policy::getStartPort)
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName)
            .map(ResourceUtils::getId);
    }

    private static Flux<Void> requestAddNetworkPolicy(CloudFoundryOperations cloudFoundryOperations, String destinationApplicationName, Integer port, String sourceApplicationName) {
        return cloudFoundryOperations.networkPolicies()
            .add(AddNetworkPolicyRequest.builder()
                .destination(destinationApplicationName)
                .startPort(port)
                .source(sourceApplicationName)
                .build());
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .buildpack("staticfile_buildpack")
                .diego(true)
                .diskQuota(512)
                .memory(64)
                .name(applicationName)
                .spaceId(spaceId)
                .build());
    }

    private static Flux<Policy> requestListNetworkPolicies(CloudFoundryOperations cloudFoundryOperations, String sourceApplicationName) {
        return cloudFoundryOperations.networkPolicies()
            .list(ListNetworkPoliciesRequest.builder()
                .source(sourceApplicationName)
                .build());
    }

}

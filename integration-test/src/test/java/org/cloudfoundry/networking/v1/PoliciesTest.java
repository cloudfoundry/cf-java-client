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

package org.cloudfoundry.networking.v1;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.networking.NetworkingClient;
import org.cloudfoundry.networking.v1.policies.CreatePoliciesRequest;
import org.cloudfoundry.networking.v1.policies.DeletePoliciesRequest;
import org.cloudfoundry.networking.v1.policies.Destination;
import org.cloudfoundry.networking.v1.policies.ListPoliciesRequest;
import org.cloudfoundry.networking.v1.policies.ListPoliciesResponse;
import org.cloudfoundry.networking.v1.policies.Policy;
import org.cloudfoundry.networking.v1.policies.Ports;
import org.cloudfoundry.networking.v1.policies.Source;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class PoliciesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private NetworkingClient networkingClient;

    @Autowired
    private Mono<String> spaceId;

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_1_12)
    @Test
    public void create() {
        String destinationApplicationName = this.nameFactory.getApplicationName();
        String sourceApplicationName = this.nameFactory.getApplicationName();
        Integer startPort = this.nameFactory.getPort();
        Integer endPort = this.nameFactory.getPort();

        this.spaceId
            .flatMapMany(spaceId -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, destinationApplicationName, spaceId),
                createApplicationId(this.cloudFoundryClient, sourceApplicationName, spaceId)
            ))
            .flatMap(function((destinationApplicationId, sourceApplicationId) -> this.networkingClient.policies()
                .create(CreatePoliciesRequest.builder()
                    .policy(Policy.builder()
                        .destination(Destination.builder()
                            .id(destinationApplicationId)
                            .ports(Ports.builder()
                                .end(endPort)
                                .start(startPort)
                                .build())
                            .protocol("tcp")
                            .build())
                        .source(Source.builder()
                            .id(sourceApplicationId)
                            .build())
                        .build())
                    .build())
                .thenReturn(destinationApplicationId)))
            .flatMap(destinationApplicationId -> requestListPolicies(this.networkingClient)
                .flatMapIterable(ListPoliciesResponse::getPolicies)
                .filter(policy -> destinationApplicationId.equals(policy.getDestination().getId()))
                .single())
            .map(policy -> policy.getDestination().getPorts().getStart())
            .as(StepVerifier::create)
            .expectNext(startPort)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_1_12)
    @Test
    public void delete() {
        String destinationApplicationName = this.nameFactory.getApplicationName();
        String sourceApplicationName = this.nameFactory.getApplicationName();
        Integer port = this.nameFactory.getPort();

        this.spaceId
            .flatMapMany(spaceId -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, destinationApplicationName, spaceId),
                createApplicationId(this.cloudFoundryClient, sourceApplicationName, spaceId)
            ))
            .delayUntil(function((destinationApplicationId, sourceApplicationId) -> requestCreatePolicy(this.networkingClient, destinationApplicationId, port, sourceApplicationId)))
            .flatMap(function((destinationApplicationId, sourceApplicationId) -> this.networkingClient.policies()
                .delete(DeletePoliciesRequest.builder()
                    .policy(Policy.builder()
                        .destination(Destination.builder()
                            .id(destinationApplicationId)
                            .ports(Ports.builder()
                                .end(port)
                                .start(port)
                                .build())
                            .protocol("tcp")
                            .build())
                        .source(Source.builder()
                            .id(sourceApplicationId)
                            .build())
                        .build())
                    .build())))
            .then(requestListPolicies(this.networkingClient)
                .flatMapIterable(ListPoliciesResponse::getPolicies)
                .filter(policy -> port.equals(policy.getDestination().getPorts().getStart()))
                .singleOrEmpty())
            .as(StepVerifier::create)
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
            .flatMap(function((destinationApplicationId, sourceApplicationId) -> requestCreatePolicy(this.networkingClient, destinationApplicationId, port, sourceApplicationId)))
            .then(this.networkingClient.policies()
                .list(ListPoliciesRequest.builder()
                    .build())
                .flatMapIterable(ListPoliciesResponse::getPolicies)
                .filter(policy -> port.equals(policy.getDestination().getPorts().getStart()))
                .single())
            .map(policy -> policy.getDestination().getPorts().getStart())
            .as(StepVerifier::create)
            .expectNext(port)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_1_12)
    @Test
    public void listFiltered() {
        String destinationApplicationName = this.nameFactory.getApplicationName();
        String sourceApplicationName = this.nameFactory.getApplicationName();
        Integer port = this.nameFactory.getPort();

        this.spaceId
            .flatMapMany(spaceId -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, destinationApplicationName, spaceId),
                createApplicationId(this.cloudFoundryClient, sourceApplicationName, spaceId)
            ))
            .flatMap(function((destinationApplicationId, sourceApplicationId) -> requestCreatePolicy(this.networkingClient, destinationApplicationId, port, sourceApplicationId)
                .thenReturn(destinationApplicationId)))
            .flatMap(destinationApplicationId -> this.networkingClient.policies()
                .list(ListPoliciesRequest.builder()
                    .policyGroupId(destinationApplicationId)
                    .build())
                .flatMapIterable(ListPoliciesResponse::getPolicies)
                .map(policy -> policy.getDestination().getPorts().getStart()))
            .as(StepVerifier::create)
            .expectNext(port)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName)
            .map(ResourceUtils::getId);
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

    private static Mono<Void> requestCreatePolicy(NetworkingClient networkingClient, String destinationApplicationId, Integer port, String sourceApplicationId) {
        return networkingClient.policies()
            .create(CreatePoliciesRequest.builder()
                .policy(Policy.builder()
                    .destination(Destination.builder()
                        .id(destinationApplicationId)
                        .ports(Ports.builder()
                            .end(port)
                            .start(port)
                            .build())
                        .protocol("tcp")
                        .build())
                    .source(Source.builder()
                        .id(sourceApplicationId)
                        .build())
                    .build())
                .build());
    }

    private static Mono<ListPoliciesResponse> requestListPolicies(NetworkingClient networkingClient) {
        return networkingClient.policies()
            .list(ListPoliciesRequest.builder()
                .build());
    }

}

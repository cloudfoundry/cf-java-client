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
import org.cloudfoundry.ServiceBrokerUtils;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerRequest;
import org.cloudfoundry.client.v2.servicebrokers.DeleteServiceBrokerRequest;
import org.cloudfoundry.client.v2.servicebrokers.GetServiceBrokerRequest;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v2.servicebrokers.UpdateServiceBrokerRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceResponse;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.ServiceBrokerUtils.createServiceBroker;
import static org.cloudfoundry.ServiceBrokerUtils.deleteServiceBroker;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class ServiceBrokersTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private Mono<String> serviceBrokerId;

    @Autowired
    private String serviceBrokerName;

    @Test
    public void create() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        Path application;
        try {
            application = new ClassPathResource("test-service-broker.jar").getFile().toPath();
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }

        ServiceBrokerUtils.ApplicationMetadata applicationMetadata = this.organizationId
            .flatMap(organizationId -> Mono
                .when(
                    createSpaceId(this.cloudFoundryClient, organizationId, spaceName),
                    getSharedDomain(this.cloudFoundryClient)
                ))
            .flatMap(function((spaceId, domain) -> ServiceBrokerUtils.pushServiceBrokerApplication(this.cloudFoundryClient, application, domain, this.nameFactory, planName, serviceName, spaceId)))
            .block(Duration.ofMinutes(5));

        this.cloudFoundryClient.serviceBrokers()
            .create(CreateServiceBrokerRequest.builder()
                .authenticationPassword("test-authentication-password")
                .authenticationUsername("test-authentication-username")
                .brokerUrl(applicationMetadata.uri)
                .name(serviceBrokerName)
                .spaceId(applicationMetadata.spaceId)
                .build())
            .flatMapMany(response -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceBrokers()
                    .list(ListServiceBrokersRequest.builder()
                        .name(serviceBrokerName)
                        .page(page)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, true))
            .block(Duration.ofMinutes(5));

        this.cloudFoundryClient.serviceBrokers()
            .delete(DeleteServiceBrokerRequest.builder()
                .serviceBrokerId(serviceBrokerMetadata.serviceBrokerId)
                .build())
            .flatMapMany(response -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceBrokers()
                    .list(ListServiceBrokersRequest.builder()
                        .name(serviceBrokerName)
                        .page(page)
                        .build())))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        this.serviceBrokerId
            .flatMap(serviceBrokerId -> this.cloudFoundryClient.serviceBrokers()
                .get(GetServiceBrokerRequest.builder()
                    .serviceBrokerId(serviceBrokerId)
                    .build()))
            .as(StepVerifier::create)
            .assertNext(serviceBroker -> assertThat(ResourceUtils.getEntity(serviceBroker).getName()).isEqualTo(this.serviceBrokerName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        PaginationUtils
            .requestClientV2Resources(page -> this.cloudFoundryClient.serviceBrokers()
                .list(ListServiceBrokersRequest.builder()
                    .name(this.serviceBrokerName)
                    .page(page)
                    .build()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName1 = this.nameFactory.getServiceBrokerName();
        String serviceBrokerName2 = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName1, serviceName, spaceId, true))
            .block(Duration.ofMinutes(5));

        this.cloudFoundryClient.serviceBrokers()
            .update(UpdateServiceBrokerRequest.builder()
                .serviceBrokerId(serviceBrokerMetadata.serviceBrokerId)
                .name(serviceBrokerName2)
                .build())
            .flatMapMany(serviceBrokerId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.serviceBrokers()
                    .list(ListServiceBrokersRequest.builder()
                        .name(serviceBrokerName2)
                        .page(page)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
            .map(ResourceUtils::getId);
    }

    private static Mono<SharedDomainResource> getSharedDomain(CloudFoundryClient cloudFoundryClient) {
        return requestListSharedDomains(cloudFoundryClient)
            .next();
    }

    private static Mono<CreateSpaceResponse> requestCreateSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return cloudFoundryClient.spaces()
            .create(CreateSpaceRequest.builder()
                .name(spaceName)
                .organizationId(organizationId)
                .build());
    }

    private static Flux<SharedDomainResource> requestListSharedDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.sharedDomains()
                .list(ListSharedDomainsRequest.builder()
                    .page(page)
                    .build()));
    }

}

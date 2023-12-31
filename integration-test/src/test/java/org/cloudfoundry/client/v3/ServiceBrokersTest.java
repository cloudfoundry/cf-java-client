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
import org.cloudfoundry.ApplicationUtils;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.ServiceBrokerUtils;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.servicebrokers.BasicAuthentication;
import org.cloudfoundry.client.v3.servicebrokers.CreateServiceBrokerRequest;
import org.cloudfoundry.client.v3.servicebrokers.DeleteServiceBrokerRequest;
import org.cloudfoundry.client.v3.servicebrokers.GetServiceBrokerRequest;
import org.cloudfoundry.client.v3.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v3.servicebrokers.ServiceBrokerRelationships;
import org.cloudfoundry.client.v3.servicebrokers.UpdateServiceBrokerRequest;
import org.cloudfoundry.client.v3.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v3.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v3.spaces.SpaceRelationships;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.ServiceBrokerUtils.createServiceBroker;
import static org.cloudfoundry.ServiceBrokerUtils.deleteServiceBroker;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_10)
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

        ApplicationUtils.ApplicationMetadata applicationMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> ServiceBrokerUtils.pushServiceBrokerApplication(this.cloudFoundryClient, application, this.nameFactory, planName, serviceName, spaceId))
            .block(Duration.ofMinutes(5));

        this.cloudFoundryClient.serviceBrokersV3()
            .create(CreateServiceBrokerRequest.builder()
        	.authentication(BasicAuthentication.builder()
        	        .password("test-authentication-password")
        	        .username("test-authentication-username")
        	.build())
                .url(applicationMetadata.uri)
                .name(serviceBrokerName)
                .relationships(ServiceBrokerRelationships.builder()
                        .space(ToOneRelationship.builder()
                        	.data(Relationship.builder()
                        		.id(applicationMetadata.spaceId)
                        		.build())
                        	.build())
                        .build())
                .build())
            .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job))
            .then(PaginationUtils
                .requestClientV3Resources(page -> this.cloudFoundryClient.serviceBrokersV3()
                    .list(ListServiceBrokersRequest.builder()
                        .name(serviceBrokerName)
                        .page(page)
                        .build()))
                .singleOrEmpty())
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

        this.cloudFoundryClient.serviceBrokersV3()
            .delete(DeleteServiceBrokerRequest.builder()
                .serviceBrokerId(serviceBrokerMetadata.serviceBrokerId)
                .build())
            .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job))
            .then(PaginationUtils
                .requestClientV3Resources(page -> this.cloudFoundryClient.serviceBrokersV3()
                    .list(ListServiceBrokersRequest.builder()
                        .name(serviceBrokerName)
                        .page(page)
                        .build()))
                .singleOrEmpty())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        this.serviceBrokerId
            .flatMap(serviceBrokerId -> this.cloudFoundryClient.serviceBrokersV3()
                .get(GetServiceBrokerRequest.builder()
                    .serviceBrokerId(serviceBrokerId)
                    .build()))
            .as(StepVerifier::create)
            .assertNext(serviceBroker -> assertThat(serviceBroker.getName()).isEqualTo(this.serviceBrokerName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        PaginationUtils
            .requestClientV3Resources(page -> this.cloudFoundryClient.serviceBrokersV3()
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

        this.cloudFoundryClient.serviceBrokersV3()
            .update(UpdateServiceBrokerRequest.builder()
                .serviceBrokerId(serviceBrokerMetadata.serviceBrokerId)
                .name(serviceBrokerName2)
                .build())
            .filter(responseUpdate -> responseUpdate.jobId().isPresent())
            .map(responseUpdate -> responseUpdate.jobId().get())
            .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job))
            .then(PaginationUtils
                .requestClientV3Resources(page -> this.cloudFoundryClient.serviceBrokersV3()
                    .list(ListServiceBrokersRequest.builder()
                        .name(serviceBrokerName2)
                        .page(page)
                        .build()))
                .singleOrEmpty())
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    @Test
    public void updateMetadata() {
        String planName = this.nameFactory.getPlanName();
        String serviceBrokerName = this.nameFactory.getServiceBrokerName();
        String serviceName = this.nameFactory.getServiceName();
        String spaceName = this.nameFactory.getSpaceName();

        ServiceBrokerUtils.ServiceBrokerMetadata serviceBrokerMetadata = this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> createServiceBroker(this.cloudFoundryClient, this.nameFactory, planName, serviceBrokerName, serviceName, spaceId, true))
            .block(Duration.ofMinutes(5));

        this.cloudFoundryClient.serviceBrokersV3()
            .update(UpdateServiceBrokerRequest.builder()
                .serviceBrokerId(serviceBrokerMetadata.serviceBrokerId)
                .metadata(Metadata.builder().label("type", "dev").build())
                .build())
            .filter(responseUpdate -> responseUpdate.jobId().isPresent())
            .map(responseUpdate -> responseUpdate.jobId().get())
            .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job))
            .then(PaginationUtils
                .requestClientV3Resources(page -> this.cloudFoundryClient.serviceBrokersV3()
                    .list(ListServiceBrokersRequest.builder()
                        .labelSelector("type=dev")
                        .name(serviceBrokerName)
                        .page(page)
                        .build()))
                .singleOrEmpty())
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

        deleteServiceBroker(this.cloudFoundryClient, serviceBrokerMetadata.applicationMetadata.applicationId)
            .block(Duration.ofMinutes(5));
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
            .map(CreateSpaceResponse::getId);
    }

    private static Mono<CreateSpaceResponse> requestCreateSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return cloudFoundryClient.spacesV3()
            .create(CreateSpaceRequest.builder()
                .name(spaceName)
                .relationships(SpaceRelationships.builder()
                        .organization(ToOneRelationship.builder()
                            .data(Relationship.builder()
                                .id(organizationId)
                                .build())
                            .build())
                        .build())
                .build());
    }

}

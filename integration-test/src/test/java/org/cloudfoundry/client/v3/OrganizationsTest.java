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

package org.cloudfoundry.client.v3;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.domains.CreateDomainRequest;
import org.cloudfoundry.client.v3.domains.CreateDomainResponse;
import org.cloudfoundry.client.v3.domains.DomainRelationships;
import org.cloudfoundry.client.v3.isolationsegments.AddIsolationSegmentOrganizationEntitlementRequest;
import org.cloudfoundry.client.v3.isolationsegments.AddIsolationSegmentOrganizationEntitlementResponse;
import org.cloudfoundry.client.v3.isolationsegments.CreateIsolationSegmentRequest;
import org.cloudfoundry.client.v3.isolationsegments.CreateIsolationSegmentResponse;
import org.cloudfoundry.client.v3.organizations.AssignOrganizationDefaultIsolationSegmentRequest;
import org.cloudfoundry.client.v3.organizations.AssignOrganizationDefaultIsolationSegmentResponse;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v3.organizations.GetOrganizationDefaultDomainRequest;
import org.cloudfoundry.client.v3.organizations.GetOrganizationDefaultDomainResponse;
import org.cloudfoundry.client.v3.organizations.GetOrganizationDefaultIsolationSegmentRequest;
import org.cloudfoundry.client.v3.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v3.organizations.GetOrganizationResponse;
import org.cloudfoundry.client.v3.organizations.ListOrganizationDomainsRequest;
import org.cloudfoundry.client.v3.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v3.organizations.OrganizationResource;
import org.cloudfoundry.client.v3.organizations.UpdateOrganizationRequest;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_1_12)
public final class OrganizationsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Test
    public void assignDefaultIsolationSegment() {
        String isolationSegmentName = this.nameFactory.getIsolationSegmentName();
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                createEntitledIsolationSegmentId(this.cloudFoundryClient, isolationSegmentName, organizationId),
                Mono.just(organizationId)
            ))
            .flatMap(function((isolationSegmentId, organizationId) -> Mono.zip(
                Mono.just(isolationSegmentId),
                this.cloudFoundryClient.organizationsV3()
                    .assignDefaultIsolationSegment(AssignOrganizationDefaultIsolationSegmentRequest.builder()
                        .organizationId(organizationId)
                        .data(Relationship.builder()
                            .id(isolationSegmentId)
                            .build())
                        .build())
                    .map(r -> r.getData().getId()))))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() {
        String organizationName = this.nameFactory.getOrganizationName();

        this.cloudFoundryClient.organizationsV3()
            .create(CreateOrganizationRequest.builder()
                .name(organizationName)
                .build())
            .thenMany(requestListOrganizations(this.cloudFoundryClient, organizationName))
            .single()
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> this.cloudFoundryClient.organizationsV3()
                .get(GetOrganizationRequest.builder()
                    .organizationId(organizationId)
                    .build()))
            .map(GetOrganizationResponse::getName)
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_7)
    @Test
    public void getDefaultDomain() {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> this.cloudFoundryClient.organizationsV3()
                .getDefaultDomain(GetOrganizationDefaultDomainRequest.builder()
                    .organizationId(organizationId)
                    .build()))
            .map(GetOrganizationDefaultDomainResponse::getName)
            .as(StepVerifier::create)
            .consumeNextWith(name -> assertThat(name).contains("apps.", ".springapps.io"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getDefaultIsolationSegment() {
        String isolationSegmentName = this.nameFactory.getIsolationSegmentName();
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                createEntitledIsolationSegmentId(this.cloudFoundryClient, isolationSegmentName, organizationId),
                Mono.just(organizationId)
            ))
            .delayUntil(function((isolationSegmentId, organizationId) -> requestAssignDefaultIsolationSegment(this.cloudFoundryClient, isolationSegmentId, organizationId)))
            .flatMap(function((isolationSegmentId, organizationId) -> Mono.zip(
                Mono.just(isolationSegmentId),
                this.cloudFoundryClient.organizationsV3()
                    .getDefaultIsolationSegment(GetOrganizationDefaultIsolationSegmentRequest.builder()
                        .organizationId(organizationId)
                        .build())
                    .map(r -> r.getData().getId()))))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String organizationName = this.nameFactory.getOrganizationName();

        requestCreateOrganization(this.cloudFoundryClient, organizationName)
            .thenMany(PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.organizationsV3()
                .list(ListOrganizationsRequest.builder()
                    .page(page)
                    .build())))
            .filter(resource -> organizationName.equals(resource.getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listDomains() {
        String domainName = this.nameFactory.getDomainName();
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateDomain(this.cloudFoundryClient, domainName, organizationId))
            .flatMapMany(organizationId -> PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.organizationsV3()
                .listDomains(ListOrganizationDomainsRequest.builder()
                    .organizationId(organizationId)
                    .page(page)
                    .build())))
            .filter(resource -> domainName.equals(resource.getName()))
            .as(StepVerifier::create)
            .consumeNextWith(resource -> assertThat(resource.getName()).isEqualTo(domainName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listDomainsFilterById() {
        String domainName = this.nameFactory.getDomainName();
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                Mono.just(organizationId)
            ))
            .flatMapMany(function((domainId, organizationId) -> PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.organizationsV3()
                .listDomains(ListOrganizationDomainsRequest.builder()
                    .domainId(domainId)
                    .organizationId(organizationId)
                    .page(page)
                    .build()))))
            .as(StepVerifier::create)
            .consumeNextWith(resource -> assertThat(resource.getName()).isEqualTo(domainName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listDomainsFilterByName() {
        String domainName = this.nameFactory.getDomainName();
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateDomain(this.cloudFoundryClient, domainName, organizationId))
            .flatMapMany(organizationId -> PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.organizationsV3()
                .listDomains(ListOrganizationDomainsRequest.builder()
                    .name(domainName)
                    .organizationId(organizationId)
                    .page(page)
                    .build())))
            .as(StepVerifier::create)
            .consumeNextWith(resource -> assertThat(resource.getName()).isEqualTo(domainName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listDomainsFilterByOwningOrganizationIds() {
        String domainName = this.nameFactory.getDomainName();
        String globalDomainName = this.nameFactory.getDomainName();
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> Mono.when(
                requestCreateDomain(this.cloudFoundryClient, domainName, organizationId),
                requestCreateDomain(this.cloudFoundryClient, globalDomainName)))
            .flatMapMany(organizationId -> PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.organizationsV3()
                .listDomains(ListOrganizationDomainsRequest.builder()
                    .organizationId(organizationId)
                    .owningOrganizationId(organizationId)
                    .page(page)
                    .build())))
            .filter(resource -> domainName.equals(resource.getName()) || globalDomainName.equals(resource.getName()))
            .as(StepVerifier::create)
            .consumeNextWith(resource -> assertThat(resource.getName()).isEqualTo(domainName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_6)
    @Test
    public void listDomainsReturningGlobalDomains() {
        String globalDomainName = this.nameFactory.getDomainName();
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .delayUntil(organizationId -> requestCreateDomain(this.cloudFoundryClient, globalDomainName))
            .flatMapMany(organizationId -> PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.organizationsV3()
                .listDomains(ListOrganizationDomainsRequest.builder()
                    .organizationId(organizationId)
                    .page(page)
                    .build())))
            .filter(resource -> globalDomainName.equals(resource.getName()))
            .as(StepVerifier::create)
            .consumeNextWith(resource -> assertThat(resource.getName()).isEqualTo(globalDomainName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
    @Test
    public void listDomainsReturningSharedDomains() {
        String domainName1 = this.nameFactory.getDomainName();
        String domainName2 = this.nameFactory.getDomainName();
        String organizationName1 = this.nameFactory.getOrganizationName();
        String organizationName2 = this.nameFactory.getOrganizationName();

        Mono.zip(
            createOrganizationId(this.cloudFoundryClient, organizationName1),
            createOrganizationId(this.cloudFoundryClient, organizationName2))
            .flatMap(function((organizationId1, organizationId2) -> Mono
                .when(
                    requestCreateDomain(this.cloudFoundryClient, domainName1, organizationId1),
                    requestCreateDomain(this.cloudFoundryClient, domainName2, organizationId2))
                .thenReturn(organizationId1)))
            .flatMapMany(organizationId -> PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.organizationsV3()
                .listDomains(ListOrganizationDomainsRequest.builder()
                    .organizationId(organizationId)
                    .page(page)
                    .build())))
            .filter(resource -> domainName1.equals(resource.getName()) || domainName2.equals(resource.getName()))
            .as(StepVerifier::create)
            .consumeNextWith(resource -> assertThat(resource.getName()).isEqualTo(domainName1))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByName() {
        String organizationName = this.nameFactory.getOrganizationName();

        requestCreateOrganization(this.cloudFoundryClient, organizationName)
            .thenMany(PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.organizationsV3()
                .list(ListOrganizationsRequest.builder()
                    .name(organizationName)
                    .page(page)
                    .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_8)
    @Test
    public void update() {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> this.cloudFoundryClient.organizationsV3().update(UpdateOrganizationRequest.builder()
                .metadata(Metadata.builder()
                    .annotation("annotationKey", "annotationValue")
                    .label("labelKey", "labelValue")
                    .build())
                .organizationId(organizationId)
                .build()))
            .thenMany(requestListOrganizations(this.cloudFoundryClient, organizationName))
            .map(OrganizationResource::getMetadata)
            .as(StepVerifier::create)
            .consumeNextWith(metadata -> {
                assertThat(metadata.getAnnotations().get("annotationKey")).isEqualTo("annotationValue");
                assertThat(metadata.getLabels().get("labelKey")).isEqualTo("labelValue");
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createDomainId(CloudFoundryClient cloudFoundryClient, String domainName, String organizationId) {
        return requestCreateDomain(cloudFoundryClient, domainName, organizationId)
            .map(CreateDomainResponse::getId);
    }

    private static Mono<String> createEntitledIsolationSegmentId(CloudFoundryClient cloudFoundryClient, String isolationSegmentName, String organizationId) {
        return createIsolationSegmentId(cloudFoundryClient, isolationSegmentName)
            .delayUntil(isolationSegmentId -> requestAddIsolationSegmentOrganizationEntitlement(cloudFoundryClient, isolationSegmentId, organizationId));
    }

    private static Mono<String> createIsolationSegmentId(CloudFoundryClient cloudFoundryClient, String isolationSegmentName) {
        return requestCreateIsolationSegment(cloudFoundryClient, isolationSegmentName)
            .map(CreateIsolationSegmentResponse::getId);
    }

    private static Mono<String> createOrganizationId(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return requestCreateOrganization(cloudFoundryClient, organizationName)
            .map(CreateOrganizationResponse::getId);
    }

    private static Mono<AddIsolationSegmentOrganizationEntitlementResponse> requestAddIsolationSegmentOrganizationEntitlement(CloudFoundryClient cloudFoundryClient, String isolationSegmentId,
                                                                                                                              String organizationId) {
        return cloudFoundryClient.isolationSegments()
            .addOrganizationEntitlement(AddIsolationSegmentOrganizationEntitlementRequest.builder()
                .isolationSegmentId(isolationSegmentId)
                .data(Relationship.builder()
                    .id(organizationId)
                    .build())
                .build());
    }

    private static Mono<AssignOrganizationDefaultIsolationSegmentResponse> requestAssignDefaultIsolationSegment(CloudFoundryClient cloudFoundryClient, String isolationSegmentId,
                                                                                                                String organizationId) {
        return cloudFoundryClient.organizationsV3()
            .assignDefaultIsolationSegment(AssignOrganizationDefaultIsolationSegmentRequest.builder()
                .organizationId(organizationId)
                .data(Relationship.builder()
                    .id(isolationSegmentId)
                    .build())
                .build());
    }

    private static Mono<CreateDomainResponse> requestCreateDomain(CloudFoundryClient cloudFoundryClient, String domainName, String organizationId) {
        return cloudFoundryClient.domainsV3()
            .create(CreateDomainRequest.builder()
                .internal(false)
                .name(domainName)
                .relationships(DomainRelationships.builder()
                    .organization(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id(organizationId)
                            .build())
                        .build())
                    .build())
                .build());
    }

    private static Mono<CreateDomainResponse> requestCreateDomain(CloudFoundryClient cloudFoundryClient, String domainName) {
        return cloudFoundryClient.domainsV3()
            .create(CreateDomainRequest.builder()
                .internal(false)
                .name(domainName)
                .build());
    }

    private static Mono<CreateIsolationSegmentResponse> requestCreateIsolationSegment(CloudFoundryClient cloudFoundryClient, String isolationSegmentName) {
        return cloudFoundryClient.isolationSegments()
            .create(CreateIsolationSegmentRequest.builder()
                .name(isolationSegmentName)
                .build());
    }

    private static Mono<CreateOrganizationResponse> requestCreateOrganization(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return cloudFoundryClient.organizationsV3()
            .create(CreateOrganizationRequest.builder()
                .name(organizationName)
                .build());
    }

    private static Flux<OrganizationResource> requestListOrganizations(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return PaginationUtils.requestClientV3Resources(page -> cloudFoundryClient.organizationsV3()
            .list(ListOrganizationsRequest.builder()
                .name(organizationName)
                .page(page)
                .build()));
    }

}

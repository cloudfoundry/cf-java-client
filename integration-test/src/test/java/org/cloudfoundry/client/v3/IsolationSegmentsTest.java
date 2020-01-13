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
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v3.isolationsegments.AddIsolationSegmentOrganizationEntitlementRequest;
import org.cloudfoundry.client.v3.isolationsegments.AddIsolationSegmentOrganizationEntitlementResponse;
import org.cloudfoundry.client.v3.isolationsegments.CreateIsolationSegmentRequest;
import org.cloudfoundry.client.v3.isolationsegments.CreateIsolationSegmentResponse;
import org.cloudfoundry.client.v3.isolationsegments.DeleteIsolationSegmentRequest;
import org.cloudfoundry.client.v3.isolationsegments.GetIsolationSegmentRequest;
import org.cloudfoundry.client.v3.isolationsegments.GetIsolationSegmentResponse;
import org.cloudfoundry.client.v3.isolationsegments.IsolationSegmentResource;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentEntitledOrganizationsRequest;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentOrganizationsRelationshipRequest;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentSpacesRelationshipRequest;
import org.cloudfoundry.client.v3.isolationsegments.ListIsolationSegmentsRequest;
import org.cloudfoundry.client.v3.isolationsegments.RemoveIsolationSegmentOrganizationEntitlementRequest;
import org.cloudfoundry.client.v3.isolationsegments.UpdateIsolationSegmentRequest;
import org.cloudfoundry.client.v3.organizations.OrganizationResource;
import org.cloudfoundry.client.v3.spaces.AssignSpaceIsolationSegmentRequest;
import org.cloudfoundry.client.v3.spaces.AssignSpaceIsolationSegmentResponse;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_1_11)
public final class IsolationSegmentsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Test
    public void addOrganizationEntitlement() {
        String isolationSegmentName = this.nameFactory.getIsolationSegmentName();
        String organizationName = this.nameFactory.getOrganizationName();

        Mono.zip(
            createIsolationSegmentId(this.cloudFoundryClient, isolationSegmentName),
            createOrganizationId(this.cloudFoundryClient, organizationName)
        )
            .flatMap(function((isolationSegmentId, organizationId) -> Mono.zip(
                Mono.just(organizationId),
                this.cloudFoundryClient.isolationSegments()
                    .addOrganizationEntitlement(AddIsolationSegmentOrganizationEntitlementRequest.builder()
                        .data(Relationship.builder()
                            .id(organizationId)
                            .build())
                        .isolationSegmentId(isolationSegmentId)
                        .build())
                    .map(response -> response.getData().get(0).getId()))
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() {
        String isolationSegmentName = this.nameFactory.getIsolationSegmentName();

        this.cloudFoundryClient.isolationSegments()
            .create(CreateIsolationSegmentRequest.builder()
                .name(isolationSegmentName)
                .build())
            .thenMany(requestListIsolationSegments(this.cloudFoundryClient, isolationSegmentName))
            .map(IsolationSegmentResource::getName)
            .as(StepVerifier::create)
            .expectNext(isolationSegmentName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

    }

    @Test
    public void delete() {
        String isolationSegmentName = this.nameFactory.getIsolationSegmentName();

        createIsolationSegmentId(this.cloudFoundryClient, isolationSegmentName)
            .flatMap(isolationSegmentId -> this.cloudFoundryClient.isolationSegments()
                .delete(DeleteIsolationSegmentRequest.builder()
                    .isolationSegmentId(isolationSegmentId)
                    .build()))
            .thenMany(requestListIsolationSegments(this.cloudFoundryClient, isolationSegmentName))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String isolationSegmentName = this.nameFactory.getIsolationSegmentName();

        createIsolationSegmentId(this.cloudFoundryClient, isolationSegmentName)
            .flatMap(isolationSegmentId -> this.cloudFoundryClient.isolationSegments()
                .get(GetIsolationSegmentRequest.builder()
                    .isolationSegmentId(isolationSegmentId)
                    .build())
                .map(GetIsolationSegmentResponse::getName))
            .as(StepVerifier::create)
            .expectNext(isolationSegmentName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String isolationSegmentName = this.nameFactory.getIsolationSegmentName();

        requestCreateIsolationSegment(this.cloudFoundryClient, isolationSegmentName)
            .thenMany(PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.isolationSegments()
                .list(ListIsolationSegmentsRequest.builder()
                    .page(page)
                    .build())))
            .filter(response -> isolationSegmentName.equals(response.getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listEntitledOrganizations() {
        String isolationSegmentName = this.nameFactory.getIsolationSegmentName();
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                createEntitledIsolationSegmentId(this.cloudFoundryClient, isolationSegmentName, organizationId),
                Mono.just(organizationId)
            ))
            .flatMapMany(function((isolationSegmentId, organizationId) -> Mono.zip(
                Mono.just(organizationId),
                PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.isolationSegments()
                    .listEntitledOrganizations(ListIsolationSegmentEntitledOrganizationsRequest.builder()
                        .isolationSegmentId(isolationSegmentId)
                        .page(page)
                        .build()))
                    .map(OrganizationResource::getId)
                    .single()
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listEntitledOrganizationsFilterByName() {
        String isolationSegmentName = this.nameFactory.getIsolationSegmentName();
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                createEntitledIsolationSegmentId(this.cloudFoundryClient, isolationSegmentName, organizationId),
                Mono.just(organizationId)
            ))
            .flatMapMany(function((isolationSegmentId, organizationId) -> Mono.zip(
                Mono.just(organizationId),
                PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.isolationSegments()
                    .listEntitledOrganizations(ListIsolationSegmentEntitledOrganizationsRequest.builder()
                        .isolationSegmentId(isolationSegmentId)
                        .name(organizationName)
                        .page(page)
                        .build()))
                    .map(OrganizationResource::getId)
                    .single()
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterById() {
        String isolationSegmentName = this.nameFactory.getIsolationSegmentName();

        createIsolationSegmentId(this.cloudFoundryClient, isolationSegmentName)
            .flatMapMany(isolationSegmentId -> PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.isolationSegments()
                .list(ListIsolationSegmentsRequest.builder()
                    .isolationSegmentId(isolationSegmentId)
                    .page(page)
                    .build()))
                .map(IsolationSegmentResource::getName))
            .as(StepVerifier::create)
            .expectNext(isolationSegmentName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByName() {
        String isolationSegmentName = this.nameFactory.getIsolationSegmentName();

        requestCreateIsolationSegment(this.cloudFoundryClient, isolationSegmentName)
            .thenMany(PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.isolationSegments()
                .list(ListIsolationSegmentsRequest.builder()
                    .name(isolationSegmentName)
                    .page(page)
                    .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByOrganizationId() {
        String isolationSegmentName = this.nameFactory.getIsolationSegmentName();
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> createEntitledIsolationSegmentId(this.cloudFoundryClient, isolationSegmentName, organizationId)
                .thenReturn(organizationId))
            .flatMapMany(organizationId -> PaginationUtils
                .requestClientV3Resources(page -> this.cloudFoundryClient.isolationSegments()
                    .list(ListIsolationSegmentsRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build())))
            .filter(resource -> isolationSegmentName.equals(resource.getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listOrganizationsRelationship() {
        String isolationSegmentName = this.nameFactory.getIsolationSegmentName();
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((organizationId, spaceId) -> Mono.zip(
                createEntitledIsolationSegmentId(this.cloudFoundryClient, isolationSegmentName, organizationId),
                Mono.just(organizationId),
                Mono.just(spaceId)
            )))
            .flatMap(function((isolationSegmentId, organizationId, spaceId) -> Mono.zip(
                requestAssignIsolationSegment(this.cloudFoundryClient, isolationSegmentId, spaceId)
                    .thenReturn(isolationSegmentId),
                Mono.just(organizationId)
            )))
            .flatMapMany(function((isolationSegmentId, organizationId) -> Mono.zip(
                Mono.just(organizationId),
                this.cloudFoundryClient.isolationSegments()
                    .listOrganizationsRelationship(ListIsolationSegmentOrganizationsRelationshipRequest.builder()
                        .isolationSegmentId(isolationSegmentId)
                        .build())
                    .map(response -> response.getData().get(0).getId()))))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpacesRelationship() {
        String isolationSegmentName = this.nameFactory.getIsolationSegmentName();
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((organizationId, spaceId) -> Mono.zip(
                createEntitledIsolationSegmentId(this.cloudFoundryClient, isolationSegmentName, organizationId),
                Mono.just(spaceId)
            )))
            .delayUntil(function((isolationSegmentId, spaceId) -> requestAssignIsolationSegment(this.cloudFoundryClient, isolationSegmentId, spaceId)))
            .flatMapMany(function((isolationSegmentId, spaceId) -> Mono.zip(
                Mono.just(spaceId),
                this.cloudFoundryClient.isolationSegments()
                    .listSpacesRelationship(ListIsolationSegmentSpacesRelationshipRequest.builder()
                        .isolationSegmentId(isolationSegmentId)
                        .build())
                    .map(response -> response.getData().get(0).getId()))))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeOrganizationEntitlement() {
        String isolationSegmentName = this.nameFactory.getIsolationSegmentName();
        String organizationName = this.nameFactory.getOrganizationName();

        Mono.zip(
            createIsolationSegmentId(this.cloudFoundryClient, isolationSegmentName),
            createOrganizationId(this.cloudFoundryClient, organizationName)
        )
            .delayUntil(function((isolationSegmentId, organizationId) -> requestAddOrganizationEntitlement(this.cloudFoundryClient, isolationSegmentId, organizationId)))
            .flatMap(function((isolationSegmentId, organizationId) -> this.cloudFoundryClient.isolationSegments()
                .removeOrganizationEntitlement(RemoveIsolationSegmentOrganizationEntitlementRequest.builder()
                    .isolationSegmentId(isolationSegmentId)
                    .organizationId(organizationId)
                    .build())
                .thenReturn(isolationSegmentId)))
            .flatMapMany(isolationSegmentId -> PaginationUtils
                .requestClientV3Resources(page -> this.cloudFoundryClient.isolationSegments()
                    .listEntitledOrganizations(ListIsolationSegmentEntitledOrganizationsRequest.builder()
                        .isolationSegmentId(isolationSegmentId)
                        .build())))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String isolationSegmentName1 = this.nameFactory.getIsolationSegmentName();
        String isolationSegmentName2 = this.nameFactory.getIsolationSegmentName();

        createIsolationSegmentId(this.cloudFoundryClient, isolationSegmentName1)
            .flatMap(isolationSegmentId -> this.cloudFoundryClient.isolationSegments()
                .update(UpdateIsolationSegmentRequest.builder()
                    .isolationSegmentId(isolationSegmentId)
                    .name(isolationSegmentName2)
                    .build()))
            .thenMany(requestListIsolationSegments(this.cloudFoundryClient, isolationSegmentName2))
            .map(IsolationSegmentResource::getName)
            .as(StepVerifier::create)
            .expectNext(isolationSegmentName2)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
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
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
            .map(ResourceUtils::getId);
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

    private static Mono<AddIsolationSegmentOrganizationEntitlementResponse> requestAddOrganizationEntitlement(CloudFoundryClient cloudFoundryClient, String isolationSegmentId, String organizationId) {
        return cloudFoundryClient.isolationSegments()
            .addOrganizationEntitlement(AddIsolationSegmentOrganizationEntitlementRequest.builder()
                .data(Relationship.builder()
                    .id(organizationId)
                    .build())
                .isolationSegmentId(isolationSegmentId)
                .build());
    }

    private static Mono<AssignSpaceIsolationSegmentResponse> requestAssignIsolationSegment(CloudFoundryClient cloudFoundryClient, String isolationSegmentId, String spaceId) {
        return cloudFoundryClient.spacesV3()
            .assignIsolationSegment(AssignSpaceIsolationSegmentRequest.builder()
                .data(Relationship.builder()
                    .id(isolationSegmentId)
                    .build())
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateIsolationSegmentResponse> requestCreateIsolationSegment(CloudFoundryClient cloudFoundryClient, String isolationSegmentName) {
        return cloudFoundryClient.isolationSegments()
            .create(CreateIsolationSegmentRequest.builder()
                .name(isolationSegmentName)
                .build());
    }

    private static Mono<CreateOrganizationResponse> requestCreateOrganization(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return cloudFoundryClient.organizations()
            .create(CreateOrganizationRequest.builder()
                .name(organizationName)
                .build());
    }

    private static Mono<CreateSpaceResponse> requestCreateSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return cloudFoundryClient.spaces()
            .create(CreateSpaceRequest.builder()
                .name(spaceName)
                .organizationId(organizationId)
                .build());
    }

    private static Flux<IsolationSegmentResource> requestListIsolationSegments(CloudFoundryClient cloudFoundryClient, String isolationSegmentName) {
        return PaginationUtils.requestClientV3Resources(page -> cloudFoundryClient.isolationSegments()
            .list(ListIsolationSegmentsRequest.builder()
                .name(isolationSegmentName)
                .page(page)
                .build()));
    }

}

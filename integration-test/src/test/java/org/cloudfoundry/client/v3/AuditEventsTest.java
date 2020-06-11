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
import org.cloudfoundry.client.v3.auditevents.AuditEventResource;
import org.cloudfoundry.client.v3.auditevents.GetAuditEventRequest;
import org.cloudfoundry.client.v3.auditevents.GetAuditEventResponse;
import org.cloudfoundry.client.v3.auditevents.ListAuditEventsRequest;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v3.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v3.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v3.spaces.SpaceRelationships;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_8)
public final class AuditEventsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Test
    public void get() {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMapMany(organizationId -> getEventId(this.cloudFoundryClient, organizationId))
            .flatMap(eventId -> this.cloudFoundryClient.auditEventsV3()
                .get(GetAuditEventRequest.builder()
                    .eventId(eventId)
                    .build()))
            .map(GetAuditEventResponse::getType)
            .as(StepVerifier::create)
            .expectNext("audit.organization.create")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //Note: Basic list() test is omitted as the potential volume of data means it take several minutes, with little benefit over the listFilterBy... tests.

    @Test
    public void lisFilterByOrganization() {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMapMany(organizationId -> PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.auditEventsV3()
                .list(ListAuditEventsRequest.builder()
                    .organizationId(organizationId)
                    .page(page)
                    .build())))
            .map(resource -> resource.getAuditEventTarget().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void lisFilterByType() {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMapMany(organizationId -> PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.auditEventsV3()
                .list(ListAuditEventsRequest.builder()
                    .type("audit.organization.create")
                    .page(page)
                    .build())))
            .map(resource -> resource.getAuditEventTarget().getName())
            .filter(organizationName::equals)
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterBySpace() {
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMapMany(spaceId -> PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.auditEventsV3()
                .list(ListAuditEventsRequest.builder()
                    .page(page)
                    .spaceId(spaceId)
                    .build())))
            .map(resource -> resource.getAuditEventTarget().getName())
            .as(StepVerifier::create)
            .expectNext(spaceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByTarget() {
        String organizationName = this.nameFactory.getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMapMany(organizationId -> PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.auditEventsV3()
                .list(ListAuditEventsRequest.builder()
                    .targetId(organizationId)
                    .page(page)
                    .build())))
            .map(resource -> resource.getAuditEventTarget().getName())
            .as(StepVerifier::create)
            .expectNext(organizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createOrganizationId(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return requestCreateOrganization(cloudFoundryClient, organizationName)
            .map(CreateOrganizationResponse::getId);
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return requestCreateSpace(cloudFoundryClient, organizationId, spaceName)
            .map(CreateSpaceResponse::getId);
    }

    private static Flux<String> getEventId(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestListEvents(cloudFoundryClient, organizationId)
            .map(AuditEventResource::getId);
    }

    private static Mono<CreateOrganizationResponse> requestCreateOrganization(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return cloudFoundryClient.organizationsV3()
            .create(CreateOrganizationRequest.builder()
                .name(organizationName)
                .build());
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

    private static Flux<AuditEventResource> requestListEvents(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils.requestClientV3Resources(page -> cloudFoundryClient.auditEventsV3()
            .list(ListAuditEventsRequest.builder()
                .organizationId(organizationId)
                .page(page)
                .build()));
    }

}

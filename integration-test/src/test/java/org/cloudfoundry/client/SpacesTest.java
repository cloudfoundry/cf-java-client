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

package org.cloudfoundry.client;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.domains.CreateDomainRequest;
import org.cloudfoundry.client.v2.domains.DomainEntity;
import org.cloudfoundry.client.v2.events.EventEntity;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerResponse;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.DeleteSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceAuditorsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceDevelopersRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceDomainsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceEventsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceManagersRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceUserRolesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceAuditorRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceDeveloperByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceDeveloperRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceManagerByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceManagerRequest;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.spaces.UpdateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.UserSpaceRoleEntity;
import org.cloudfoundry.client.v2.stacks.ListStacksRequest;
import org.cloudfoundry.client.v2.users.ListUsersRequest;
import org.cloudfoundry.client.v2.users.UserEntity;
import org.cloudfoundry.operations.util.Dates;
import org.cloudfoundry.operations.util.v2.Paginated;
import org.cloudfoundry.operations.util.v2.Resources;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.Predicate;
import reactor.fn.tuple.Tuple2;
import reactor.rx.Stream;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.cloudfoundry.operations.util.Tuples.function;
import static org.junit.Assert.assertEquals;

public final class SpacesTest extends AbstractIntegrationTest {

    private static final String TEST_APPLICATION_NAME = "space-test-application-name";

    private static final String TEST_HOST_NAME = "space-test-host-name";

    private static final String TEST_NEW_DOMAIN_NAME = "space.test.domain.name";

    private static final String TEST_NEW_SPACE_NAME = "space-test-new-space-name";

    private static final String TEST_ORGANIZATION_NAME = "space-test-organization";

    private static final String TEST_PATH = "/space/test/path";

    private static final String TEST_STACK_NAME = "cflinuxfs2";  //TODO: eliminate reference to a specific stack name

    private String spacesTestUsername;

    @Test
    public void associateAuditor() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .then(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateAuditor(AssociateSpaceAuditorRequest.builder()
                    .spaceId(spaceId)
                    .auditorId(userId)
                    .build())
                .map(Resources::getId)
                .and(Mono.just(spaceId))))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateAuditorByUsername() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateAuditorByUsername(AssociateSpaceAuditorByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(this.spacesTestUsername)
                    .build()))))
            .flatMap(function((userId, spaceId) -> getAuditorNames(this.cloudFoundryClient, spaceId)))
            .subscribe(this.testSubscriber()
                .assertEquals(this.spacesTestUsername));
    }

    @Test
    public void associateDeveloper() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .then(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateDeveloper(AssociateSpaceDeveloperRequest.builder()
                    .spaceId(spaceId)
                    .developerId(userId)
                    .build())
                .map(Resources::getId)
                .and(Mono.just(spaceId))))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateDeveloperByUsername() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateDeveloperByUsername(AssociateSpaceDeveloperByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(this.spacesTestUsername)
                    .build()))))
            .flatMap(function((userId, spaceId) -> getDeveloperNames(this.cloudFoundryClient, spaceId)))
            .subscribe(this.testSubscriber()
                .assertEquals(this.spacesTestUsername));
    }

    @Test
    public void associateManager() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .then(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateManager(AssociateSpaceManagerRequest.builder()
                    .spaceId(spaceId)
                    .managerId(userId)
                    .build())
                .map(Resources::getId)
                .and(Mono.just(spaceId))))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateManagerByUsername() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateManagerByUsername(AssociateSpaceManagerByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(this.userName)
                    .build()))))
            .flatMap(function((userId, spaceId) -> getManagerNames(this.cloudFoundryClient, spaceId)))
            .subscribe(this.testSubscriber()
                .assertEquals(this.spacesTestUsername));
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522656")
    @Test
    public void associateSecurityGroup() {

    }

    @Test
    public void create() {
        this.organizationId
            .then(organizationId -> this.cloudFoundryClient.spaces()
                .create(CreateSpaceRequest.builder()
                    .organizationId(organizationId)
                    .name(TEST_NEW_SPACE_NAME)
                    .build()))
            .map(Resources::getEntity)
            .map(SpaceEntity::getName)
            .subscribe(this.<String>testSubscriber()
                .assertEquals(TEST_NEW_SPACE_NAME));
    }

    @Test
    public void delete() {
        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId))
            .as(thenCompleteKeep(spaceId -> this.cloudFoundryClient.spaces()
                .delete(DeleteSpaceRequest.builder()
                    .spaceId(spaceId)
                    .async(false)
                    .build())))
            .flatMap(spaceId -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .list(ListSpacesRequest.builder()
                        .page(page)
                        .build()))
                .map(Resources::getId)
                .filter(spaceId::equals))
            .subscribe(this.testSubscriber());
    }

    @Test
    public void get() {
        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId))
            .then(spaceId -> this.cloudFoundryClient.spaces()
                .get(GetSpaceRequest.builder()
                    .spaceId(spaceId)
                    .build()))
            .map(Resources::getEntity)
            .map(SpaceEntity::getName)
            .subscribe(this.<String>testSubscriber()
                .assertEquals(TEST_NEW_SPACE_NAME));
    }

    @Test
    public void getSummary() {
        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId))
            .then(spaceId -> this.cloudFoundryClient.spaces()
                .getSummary(GetSpaceSummaryRequest.builder()
                    .spaceId(spaceId)
                    .build()))
            .map(GetSpaceSummaryResponse::getName)
            .and(Mono.just(TEST_NEW_SPACE_NAME))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void list() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    Mono.just(organizationId),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .then(function((organizationId, spaceId) -> Mono
                .when(
                    Mono.just(spaceId),
                    Paginated
                        .requestResources(page -> this.cloudFoundryClient.spaces()
                            .list(ListSpacesRequest.builder()
                                .page(page)
                                .build()))
                        .filter(hasOrganizationId(organizationId))
                        .single()
                        .map(Resources::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listApplications() {
        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId))
            .as(thenKeep(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId)))
            .then(spaceId -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listApplications(ListSpaceApplicationsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))
                .single())
            .map(ApplicationResource::getEntity)
            .map(ApplicationEntity::getName)
            .and(Mono.just(TEST_APPLICATION_NAME))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listApplicationsFilterByDiego() {
        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId))
            .as(thenKeep(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId)))
            .then(spaceId -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listApplications(ListSpaceApplicationsRequest.builder()
                        .page(page)
                        .diego(true)
                        .spaceId(spaceId)
                        .build()))
                .single())
            .map(ApplicationResource::getEntity)
            .map(ApplicationEntity::getName)
            .and(Mono.just(TEST_APPLICATION_NAME))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listApplicationsFilterByName() {
        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId))
            .then(spaceId -> Mono
                .when(
                    createApplicationId(this.cloudFoundryClient, spaceId),
                    Mono.just(spaceId)
                ))
            .then(function((applicationId, spaceId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listApplications(ListSpaceApplicationsRequest.builder()
                        .page(page)
                        .name(TEST_APPLICATION_NAME)
                        .spaceId(spaceId)
                        .build()))
                .single()
                .map(Resources::getId)
                .and(Mono.just(applicationId))))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listApplicationsFilterByOrganizationId() {
        this.organizationId
            .then(organizationId -> Mono
                .when(
                    Mono.just(organizationId),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .as(thenKeep(function((organizationId, spaceId) -> createApplicationId(this.cloudFoundryClient, spaceId))))
            .flatMap(function((organizationId, spaceId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listApplications(ListSpaceApplicationsRequest.builder()
                        .page(page)
                        .organizationId(organizationId)
                        .spaceId(spaceId)
                        .build()))))
            .map(Resources::getEntity)
            .map(ApplicationEntity::getName)
            .subscribe(this.<String>testSubscriber()
                .assertEquals(TEST_APPLICATION_NAME));
    }

    @Test
    public void listApplicationsFilterByStackId() {
        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId))
            .as(thenKeep(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId)))
            .then(spaceId -> Mono
                .when(
                    Mono.just(spaceId),
                    Paginated
                        .requestResources(page -> this.cloudFoundryClient.stacks()
                            .list(ListStacksRequest.builder()
                                .name(TEST_STACK_NAME)
                                .page(page)
                                .build()))
                        .map(Resources::getId)
                        .single()
                ))
            .flatMap(function((spaceId, stackId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listApplications(ListSpaceApplicationsRequest.builder()
                        .page(page)
                        .stackId(stackId)
                        .spaceId(spaceId)
                        .build()))))
            .map(Resources::getEntity)
            .map(ApplicationEntity::getName)
            .subscribe(this.<String>testSubscriber()
                .assertEquals(TEST_APPLICATION_NAME));
    }

    @Test
    public void listAuditors() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateAuditor(AssociateSpaceAuditorRequest.builder()
                    .spaceId(spaceId)
                    .auditorId(userId)
                    .build()))))
            .flatMap(function((userId, spaceId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listAuditors(ListSpaceAuditorsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))))
            .map(Resources::getEntity)
            .map(UserEntity::getUsername)
            .subscribe(this.testSubscriber()
                .assertEquals(this.spacesTestUsername));
    }

    @Test
    public void listDevelopers() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateDeveloper(AssociateSpaceDeveloperRequest.builder()
                    .spaceId(spaceId)
                    .developerId(userId)
                    .build()))))
            .flatMap(function((userId, spaceId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listDevelopers(ListSpaceDevelopersRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))))
            .map(Resources::getEntity)
            .map(UserEntity::getUsername)
            .subscribe(this.testSubscriber()
                .assertEquals(this.spacesTestUsername));
    }

    @Test
    public void listDomains() {
        this.organizationId
            .then(organizationId -> createSpaceIdWithDomain(this.cloudFoundryClient, organizationId))
            .then(spaceId -> Mono
                .when(
                    Mono.just(spaceId),
                    getDomainId(this.cloudFoundryClient, spaceId, TEST_NEW_DOMAIN_NAME)
                ))
            .flatMap(function((spaceId, domainId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listDomains(ListSpaceDomainsRequest.builder()
                        .spaceId(spaceId)
                        .page(page)
                        .build()))
                .filter(this.domainsPredicate))) // eliminate default domain
            .map(Resources::getEntity)
            .map(DomainEntity::getName)
            .subscribe(this.<String>testSubscriber()
                .assertEquals(TEST_NEW_DOMAIN_NAME));
    }

    @Test
    public void listDomainsFilterByName() {
        this.organizationId
            .then(organizationId -> createSpaceIdWithDomain(this.cloudFoundryClient, organizationId))
            .flatMap(spaceId -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listDomains(ListSpaceDomainsRequest.builder()
                        .spaceId(spaceId)
                        .name(TEST_NEW_DOMAIN_NAME)
                        .page(page)
                        .build())))
            .map(Resources::getEntity)
            .map(DomainEntity::getName)
            .subscribe(this.<String>testSubscriber()
                .assertEquals(TEST_NEW_DOMAIN_NAME));
    }

    @Test
    public void listDomainsFilterByOwningOrganizationId() {
        this.organizationId
            .then(organizationId -> Mono
                .when(
                    Mono.just(organizationId),
                    createSpaceIdWithDomain(this.cloudFoundryClient, organizationId)
                ))
            .flatMap(function((organizationId, spaceId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listDomains(ListSpaceDomainsRequest.builder()
                        .spaceId(spaceId)
                        .owningOrganizationId(organizationId)
                        .page(page)
                        .build()))))
            .map(Resources::getEntity)
            .map(DomainEntity::getName)
            .subscribe(this.<String>testSubscriber()
                .assertEquals(TEST_NEW_DOMAIN_NAME));
    }

    @Test
    public void listEvents() {
        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId))
            .flatMap(spaceId -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listEvents(ListSpaceEventsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build())))
            .map(Resources::getEntity)
            .map(EventEntity::getType)
            .subscribe(this.<String>testSubscriber()
                .assertEquals("audit.space.create"));
    }

    @Test
    public void listEventsFilterByActee() {
        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId))
            .flatMap(spaceId -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listEvents(ListSpaceEventsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .actee(spaceId)
                        .build())))
            .map(Resources::getEntity)
            .map(EventEntity::getType)
            .subscribe(this.<String>testSubscriber()
                .assertEquals("audit.space.create"));
    }

    @Test
    public void listEventsFilterByTimestamp() {
        this.organizationId
            .then(organizationId -> Mono
                .when(
                    createSpaceId(this.cloudFoundryClient, organizationId),
                    getPastTimestamp()
                ))
            .flatMap(function((spaceId, timestamp) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listEvents(ListSpaceEventsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .timestamp(timestamp)
                        .build()))))
            .map(Resources::getEntity)
            .map(EventEntity::getType)
            .subscribe(this.<String>testSubscriber()
                .assertEquals("audit.space.create"));
    }

    @Test
    public void listEventsFilterByType() {
        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId))
            .then(spaceId -> Mono
                .when(
                    Mono.just(spaceId),
                    Paginated
                        .requestResources(page -> this.cloudFoundryClient.spaces()
                            .listEvents(ListSpaceEventsRequest.builder()
                                .page(page)
                                .spaceId(spaceId)
                                .type("audit.space.create")
                                .build()))
                        .single()
                        .map(Resources::getEntity)
                        .map(EventEntity::getSpaceId)
                ))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listFilterByApplicationId() {
        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId))
            .then(spaceId -> Mono
                .when(
                    Mono.just(spaceId),
                    createApplicationId(this.cloudFoundryClient, spaceId)
                ))
            .then(function((spaceId, applicationId) -> Mono
                .when(
                    Mono.just(spaceId),
                    Paginated
                        .requestResources(page -> this.cloudFoundryClient.spaces()
                            .list(ListSpacesRequest.builder()
                                .page(page)
                                .applicationId(applicationId)
                                .build()))
                        .map(Resources::getId)
                        .single()
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522686 really create a new user")
    @Test
    public void listFilterByDeveloperId() {
        // this test will break if userId is a developerId for another space; see above
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateDeveloper(AssociateSpaceDeveloperRequest.builder()
                    .spaceId(spaceId)
                    .developerId(userId)
                    .build()))))
            .then(function((userId, spaceId) -> Mono
                .when(
                    Mono.just(spaceId),
                    Paginated
                        .requestResources(page -> this.cloudFoundryClient.spaces()
                            .list(ListSpacesRequest.builder()
                                .page(page)
                                .developerId(userId)
                                .build()))
                        .map(Resources::getId)
                        .single()
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listFilterByName() {
        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId))
            .then(spaceId -> Mono
                .when(
                    Mono.just(spaceId),
                    Paginated
                        .requestResources(page -> this.cloudFoundryClient.spaces()
                            .list(ListSpacesRequest.builder()
                                .name(TEST_NEW_SPACE_NAME)
                                .page(page)
                                .build()))
                        .map(Resources::getId)
                        .single()
                ))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listFilterByOrganizationId() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    Mono.just(organizationId),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .then(function((organizationId, spaceId) -> Mono
                .when(
                    Mono.just(spaceId),
                    Paginated
                        .requestResources(page -> this.cloudFoundryClient.spaces()
                            .list(ListSpacesRequest.builder()
                                .page(page)
                                .organizationId(organizationId)
                                .build()))
                        .single()
                        .map(Resources::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listManagers() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .as(thenKeep(function((userId, spaceId) -> associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId))))
            .flatMap(function((userId, spaceId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listManagers(ListSpaceManagersRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))
                .map(Resources::getEntity)
                .map(UserEntity::getUsername)))
            .subscribe(this.testSubscriber()
                .assertEquals(this.spacesTestUsername));
    }

    @Test
    public void listManagersFilterByAuditedOrganizationId() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId),
                    Mono.just(organizationId)
                ))
            .as(thenKeep(function((userId, spaceId, organizationId) -> Mono
                .when(
                    associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId),
                    associateAuditorWithOrganization(this.cloudFoundryClient, organizationId, userId)
                ))))
            .flatMap(function((userId, spaceId, organizationId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listManagers(ListSpaceManagersRequest.builder()
                        .spaceId(spaceId)
                        .page(page)
                        .auditedOrganizationId(organizationId)
                        .build()))
                .map(Resources::getEntity)
                .map(UserEntity::getUsername)))
            .subscribe(this.testSubscriber()
                .assertEquals(this.spacesTestUsername));
    }

    @Test
    public void listManagersFilterByAuditedSpaceId() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .as(thenKeep(function((userId, spaceId) -> Mono
                .when(
                    associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId),
                    associateAuditorWithSpace(this.cloudFoundryClient, spaceId, userId)
                ))))
            .flatMap(function((userId, spaceId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listManagers(ListSpaceManagersRequest.builder()
                        .spaceId(spaceId)
                        .page(page)
                        .auditedSpaceId(spaceId)
                        .build()))
                .map(Resources::getEntity)
                .map(UserEntity::getUsername)))
            .subscribe(this.testSubscriber()
                .assertEquals(this.spacesTestUsername));
    }

    @Test
    public void listManagersFilterByBillingManagedOrganizationId() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId),
                    Mono.just(organizationId)
                ))
            .as(thenKeep(function((userId, spaceId, organizationId) -> Mono
                .when(
                    associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId),
                    associateBillingManagerWithOrganization(this.cloudFoundryClient, organizationId, userId)
                ))))
            .then(function((userId, spaceId, organizationId) -> Mono
                .when(
                    Mono.just(userId),
                    Paginated
                        .requestResources(page -> this.cloudFoundryClient.spaces()
                            .listManagers(ListSpaceManagersRequest.builder()
                                .spaceId(spaceId)
                                .page(page)
                                .billingManagedOrganizationId(organizationId)
                                .build()))
                        .single()
                        .map(Resources::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listManagersFilterByManagedOrganizationId() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId),
                    Mono.just(organizationId)
                ))
            .as(thenKeep(function((userId, spaceId, organizationId) -> Mono
                .when(
                    associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId),
                    associateManagerWithOrganization(this.cloudFoundryClient, organizationId, userId)
                ))))
            .then(function((userId, spaceId, organizationId) -> Mono
                .when(
                    Mono.just(userId),
                    Paginated
                        .requestResources(page -> this.cloudFoundryClient.spaces()
                            .listManagers(ListSpaceManagersRequest.builder()
                                .spaceId(spaceId)
                                .page(page)
                                .managedOrganizationId(organizationId)
                                .build()))
                        .single()
                        .map(Resources::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listManagersFilterByManagedSpaceId() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .as(thenKeep(function((userId, spaceId) -> associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId))))
            .flatMap(function((userId, spaceId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listManagers(ListSpaceManagersRequest.builder()
                        .spaceId(spaceId)
                        .page(page)
                        .managedSpaceId(spaceId)
                        .build()))
                .map(Resources::getEntity)
                .map(UserEntity::getUsername)))
            .subscribe(this.testSubscriber()
                .assertEquals(this.spacesTestUsername));
    }

    @Test
    public void listManagersFilterByOrganizationId() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId),
                    Mono.just(organizationId)
                ))
            .as(thenKeep(function((userId, spaceId, organizationId) -> associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId))))
            .then(function((userId, spaceId, organizationId) -> Mono
                .when(
                    Mono.just(userId),
                    Paginated
                        .requestResources(page -> this.cloudFoundryClient.spaces()
                            .listManagers(ListSpaceManagersRequest.builder()
                                .spaceId(spaceId)
                                .page(page)
                                .organizationId(organizationId)
                                .build()))
                        .single()
                        .map(Resources::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listRoutes() {
        this.organizationId
            .then(organizationId -> createSpaceIdWithDomain(this.cloudFoundryClient, organizationId))
            .then(spaceId -> Mono
                .when(
                    Mono.just(spaceId),
                    createRouteId(this.cloudFoundryClient, spaceId)
                ))
            .flatMap(function((spaceId, routeId) -> Mono
                .when(
                    Mono.just(routeId),
                    Paginated
                        .requestResources(page -> this.cloudFoundryClient.spaces()
                            .listRoutes(ListSpaceRoutesRequest.builder()
                                .spaceId(spaceId)
                                .build()))
                        .map(Resources::getId)
                        .single()
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listRoutesFilterByDomainId() {
        this.organizationId
            .then(organizationId -> createSpaceIdWithDomain(this.cloudFoundryClient, organizationId))
            .then(spaceId -> Mono
                .when(
                    Mono.just(spaceId),
                    getDomainId(this.cloudFoundryClient, spaceId, TEST_NEW_DOMAIN_NAME),
                    createRouteId(this.cloudFoundryClient, spaceId)
                ))
            .flatMap(function((spaceId, domainId, routeId) -> Mono
                .when(
                    Mono.just(routeId),
                    Paginated
                        .requestResources(page -> this.cloudFoundryClient.spaces()
                            .listRoutes(ListSpaceRoutesRequest.builder()
                                .spaceId(spaceId)
                                .domainId(domainId)
                                .build()))
                        .map(Resources::getId)
                        .single()
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listRoutesFilterByHost() {
        this.organizationId
            .then(organizationId -> createSpaceIdWithDomain(this.cloudFoundryClient, organizationId))
            .then(spaceId -> Mono
                .when(
                    Mono.just(spaceId),
                    createRouteId(this.cloudFoundryClient, spaceId)
                ))
            .flatMap(function((spaceId, routeId) -> Mono
                .when(
                    Mono.just(routeId),
                    Paginated
                        .requestResources(page -> this.cloudFoundryClient.spaces()
                            .listRoutes(ListSpaceRoutesRequest.builder()
                                .spaceId(spaceId)
                                .host(TEST_HOST_NAME)
                                .build()))
                        .map(Resources::getId)
                        .single()
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Ignore("TODO: See https://github.com/cloudfoundry/cloud_controller_ng/issues/525")
    @Test
    public void listRoutesFilterByOrganizationId() {
        this.organizationId
            .then(organizationId -> Mono
                .when(
                    Mono.just(organizationId),
                    createSpaceIdWithDomain(this.cloudFoundryClient, organizationId)
                ))
            .then(function((organizationId, spaceId) -> Mono
                .when(
                    Mono.just(organizationId),
                    Mono.just(spaceId),
                    createRouteId(this.cloudFoundryClient, spaceId)
                )))
            .flatMap(function((organizationId, spaceId, routeId) -> Mono
                .when(
                    Mono.just(routeId),
                    Paginated
                        .requestResources(page -> this.cloudFoundryClient.spaces()
                            .listRoutes(ListSpaceRoutesRequest.builder()
                                .spaceId(spaceId)
                                .organizationId(organizationId)
                                .build()))
                        .map(Resources::getId)
                        .single()
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listRoutesFilterByPath() {
        this.organizationId
            .then(organizationId -> createSpaceIdWithDomain(this.cloudFoundryClient, organizationId))
            .then(spaceId -> Mono
                .when(
                    Mono.just(spaceId),
                    createRouteId(this.cloudFoundryClient, spaceId)
                ))
            .flatMap(function((spaceId, routeId) -> Mono
                .when(
                    Mono.just(routeId),
                    Paginated
                        .requestResources(page -> this.cloudFoundryClient.spaces()
                            .listRoutes(ListSpaceRoutesRequest.builder()
                                .spaceId(spaceId)
                                .path(TEST_PATH)
                                .build()))
                        .map(Resources::getId)
                        .single()
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522656")
    @Test
    public void listSecurityGroups() {

    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522656")
    @Test
    public void listSecurityGroupsFilterByName() {

    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/112665039")
    @Test
    public void listServiceInstances() {
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/112665039")
    @Test
    public void listServiceInstancesFilterByGatewayName() {

    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/112665039")
    @Test
    public void listServiceInstancesFilterByName() {

    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/112665039")
    @Test
    public void listServiceInstancesFilterByOrganizationId() {

    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/112665039")
    @Test
    public void listServiceInstancesFilterByServiceBindingId() {

    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/112665039")
    @Test
    public void listServiceInstancesFilterByServiceKeyId() {

    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/112665039")
    @Test
    public void listServiceInstancesFilterByServicePlanId() {

    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/112665039")
    @Test
    public void listServices() {

    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/112665039")
    @Test
    public void listServicesFilterByActive() {

    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/112665039")
    @Test
    public void listServicesFilterByLabel() {

    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/112665039")
    @Test
    public void listServicesFilterByProvider() {

    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/112665039")
    @Test
    public void listServicesFilterByServiceBrokerId() {

    }

    @Test
    public void listUserRoles() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .as(thenKeep(function((userId, spaceId) -> associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId))))
            .flatMap(function((userId, spaceId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listUserRoles(ListSpaceUserRolesRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))
                .map(Resources::getEntity)))
            .subscribe(this.<UserSpaceRoleEntity>testSubscriber()
                .assertThat(userSpaceRoleEntity -> {
                    assertEquals(this.spacesTestUsername, userSpaceRoleEntity.getUsername());
                    assertEquals(Collections.singletonList("space_manager"), userSpaceRoleEntity.getSpaceRoles());
                }));
    }

    @Test
    public void removeAuditor() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .as(thenKeep(function((userId, spaceId) -> associateAuditorWithSpace(this.cloudFoundryClient, spaceId, userId))))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .removeAuditor(RemoveSpaceAuditorRequest.builder()
                    .spaceId(spaceId)
                    .auditorId(userId)
                    .build()))))
            .flatMap(function((userId, spaceId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listAuditors(ListSpaceAuditorsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))))
            .subscribe(this.testSubscriber());
    }

    @Test
    public void removeAuditorByUsername() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .as(thenKeep(function((userId, spaceId) -> associateAuditorWithSpace(this.cloudFoundryClient, spaceId, userId))))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .removeAuditorByUsername(RemoveSpaceAuditorByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(this.spacesTestUsername)
                    .build()))))
            .flatMap(function((userId, spaceId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listAuditors(ListSpaceAuditorsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))))
            .subscribe(this.testSubscriber());
    }

    @Test
    public void removeDeveloper() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .as(thenKeep(function((userId, spaceId) -> associateDeveloperWithSpace(this.cloudFoundryClient, spaceId, userId))))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .removeDeveloper(RemoveSpaceDeveloperRequest.builder()
                    .spaceId(spaceId)
                    .developerId(userId)
                    .build()))))
            .flatMap(function((userId, spaceId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listDevelopers(ListSpaceDevelopersRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))))
            .subscribe(this.testSubscriber());
    }

    @Test
    public void removeDeveloperByUsername() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .as(thenKeep(function((userId, spaceId) -> associateDeveloperWithSpace(this.cloudFoundryClient, spaceId, userId))))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .removeDeveloperByUsername(RemoveSpaceDeveloperByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(this.spacesTestUsername)
                    .build()))))
            .flatMap(function((userId, spaceId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listDevelopers(ListSpaceDevelopersRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))))
            .subscribe(this.testSubscriber());
    }

    @Test
    public void removeManager() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .as(thenKeep(function((userId, spaceId) -> associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId))))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .removeManager(RemoveSpaceManagerRequest.builder()
                    .spaceId(spaceId)
                    .managerId(userId)
                    .build()))))
            .flatMap(function((userId, spaceId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listManagers(ListSpaceManagersRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))
                .map(Resources::getEntity)
                .map(UserEntity::getUsername)
            ))
            .subscribe(this.testSubscriber());
    }

    @Test
    public void removeManagerByUsername() {
        createOrganizationId(this.cloudFoundryClient, TEST_ORGANIZATION_NAME)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.spacesTestUsername),
                    createSpaceId(this.cloudFoundryClient, organizationId)
                ))
            .as(thenKeep(function((userId, spaceId) -> associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId))))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .removeManagerByUsername(RemoveSpaceManagerByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(this.spacesTestUsername)
                    .build()))))
            .flatMap(function((userId, spaceId) -> Paginated
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listManagers(ListSpaceManagersRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))
                .map(Resources::getEntity)
                .map(UserEntity::getUsername)))
            .subscribe(this.testSubscriber());
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522658")
    @Test
    public void removeSecurityGroup() {

    }

    @Before
    public void setup() throws Exception {
        this.spacesTestUsername = this.userName; // TODO: avoid spacesTestUsername when we can create a new user; see createUserId()
    }

    @Test
    public void update() {
        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId))
            .as(thenKeep(spaceId -> this.cloudFoundryClient.spaces()
                .update(UpdateSpaceRequest.builder()
                    .spaceId(spaceId)
                    .name("new-name")
                    .build())))
            .then(spaceId -> this.cloudFoundryClient.spaces()
                .getSummary(GetSpaceSummaryRequest.builder()
                    .spaceId(spaceId)
                    .build()))
            .map(GetSpaceSummaryResponse::getName)
            .and(Mono.just("new-name"))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    private static Mono<AssociateOrganizationAuditorResponse> associateAuditorWithOrganization(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateAuditor(AssociateOrganizationAuditorRequest.builder()
                .organizationId(organizationId)
                .auditorId(userId)
                .build());
    }

    private static Mono<AssociateSpaceAuditorResponse> associateAuditorWithSpace(CloudFoundryClient cloudFoundryClient, String spaceId, String userId) {
        return cloudFoundryClient.spaces()
            .associateAuditor(AssociateSpaceAuditorRequest.builder()
                .spaceId(spaceId)
                .auditorId(userId)
                .build());
    }

    private static Mono<AssociateOrganizationBillingManagerResponse> associateBillingManagerWithOrganization(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateBillingManager(AssociateOrganizationBillingManagerRequest.builder()
                .organizationId(organizationId)
                .billingManagerId(userId)
                .build());
    }

    private static Mono<AssociateSpaceDeveloperResponse> associateDeveloperWithSpace(CloudFoundryClient cloudFoundryClient, String spaceId, String userId) {
        return cloudFoundryClient.spaces()
            .associateDeveloper(AssociateSpaceDeveloperRequest.builder()
                .spaceId(spaceId)
                .developerId(userId)
                .build());
    }

    private static Mono<AssociateOrganizationManagerResponse> associateManagerWithOrganization(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateManager(AssociateOrganizationManagerRequest.builder()
                .organizationId(organizationId)
                .managerId(userId)
                .build());
    }

    private static Mono<AssociateSpaceManagerResponse> associateManagerWithSpace(CloudFoundryClient cloudFoundryClient, String spaceId, String userId) {
        return cloudFoundryClient.spaces()
            .associateManager(AssociateSpaceManagerRequest.builder()
                .spaceId(spaceId)
                .managerId(userId)
                .build());
    }

    private static Mono<AssociateOrganizationUserByUsernameResponse> associateOrganizationUser(CloudFoundryClient cloudFoundryClient, String organizationId, String username) {
        return cloudFoundryClient.organizations()
            .associateUserByUsername(AssociateOrganizationUserByUsernameRequest.builder()
                .organizationId(organizationId)
                .username(username)
                .build());
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .diego(true)
                .name(TEST_APPLICATION_NAME)
                .spaceId(spaceId)
                .build())
            .map(Resources::getId);
    }

    private static Mono<String> createDomainId(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return cloudFoundryClient.domains()
            .create(CreateDomainRequest.builder()
                .name(TEST_NEW_DOMAIN_NAME)
                .owningOrganizationId(organizationId)
                .wildcard(true)
                .build())
            .map(Resources::getId);
    }

    private static Mono<String> createOrganizationId(CloudFoundryClient cloudFoundryClient, String organization) {
        return cloudFoundryClient.organizations()
            .create(CreateOrganizationRequest.builder()
                .name(organization)
                .build())
            .map(Resources::getId);
    }

    private static Mono<String> createRouteId(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return Paginated
            .requestResources(page -> cloudFoundryClient.spaces()
                .listDomains(ListSpaceDomainsRequest.builder()
                    .name(TEST_NEW_DOMAIN_NAME)
                    .spaceId(spaceId)
                    .page(page)
                    .build()))
            .single()
            .map(Resources::getId)
            .then(domainId -> cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .spaceId(spaceId)
                    .domainId(domainId)
                    .host(TEST_HOST_NAME)
                    .path(TEST_PATH)
                    .build()))
            .map(Resources::getId);
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return cloudFoundryClient.spaces()
            .create(CreateSpaceRequest.builder()
                .organizationId(organizationId)
                .name(TEST_NEW_SPACE_NAME)
                .build())
            .map(Resources::getId);
    }

    private static Mono<String> createSpaceIdWithDomain(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return createDomainId(cloudFoundryClient, organizationId)
            .then(domainId -> cloudFoundryClient.spaces()
                .create(CreateSpaceRequest.builder()
                    .organizationId(organizationId)
                    .name(TEST_NEW_SPACE_NAME)
                    .build()))
            .map(Resources::getId);
    }

    // TODO: after: https://www.pivotaltracker.com/story/show/101522686 really create a new user
    private static Mono<String> createUserId(CloudFoundryClient cloudFoundryClient, String organizationId, String username) {
        return associateOrganizationUser(cloudFoundryClient, organizationId, username)
            .after(() -> Paginated
                .requestResources(page -> cloudFoundryClient.users()
                    .listUsers(ListUsersRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build()))
                .filter(resource -> Resources.getEntity(resource).getUsername().equals(username))
                .single()
                .map(Resources::getId)
            );
    }

    private static Stream<String> getAuditorNames(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return Paginated
            .requestResources(page -> cloudFoundryClient.spaces()
                .listAuditors(ListSpaceAuditorsRequest.builder()
                    .page(page)
                    .spaceId(spaceId)
                    .build()))
            .map(Resources::getEntity)
            .map(UserEntity::getUsername);
    }

    private static Stream<String> getDeveloperNames(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return Paginated
            .requestResources(page -> cloudFoundryClient.spaces()
                .listDevelopers(ListSpaceDevelopersRequest.builder()
                    .page(page)
                    .spaceId(spaceId)
                    .build()))
            .map(Resources::getEntity)
            .map(UserEntity::getUsername);
    }

    private static Mono<String> getDomainId(CloudFoundryClient cloudFoundryClient, String spaceId, String domain) {
        return Paginated
            .requestResources(page -> cloudFoundryClient.spaces()
                .listDomains(ListSpaceDomainsRequest.builder()
                    .name(domain)
                    .spaceId(spaceId)
                    .page(page)
                    .build()))
            .single()
            .map(Resources::getId);
    }

    private static Stream<String> getManagerNames(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return Paginated
            .requestResources(page -> cloudFoundryClient.spaces()
                .listManagers(ListSpaceManagersRequest.builder()
                    .page(page)
                    .spaceId(spaceId)
                    .build()))
            .map(Resources::getEntity)
            .map(UserEntity::getUsername);
    }

    private static Mono<String> getPastTimestamp() {
        return Mono
            .fromCallable(() -> {
                Date past = Date.from(Instant.now().minus(1, HOURS));
                return Dates.format(past);
            });
    }

    private static Predicate<SpaceResource> hasOrganizationId(String organizationId) {
        return spaceResource -> Resources.getEntity(spaceResource).getOrganizationId().equals(organizationId);
    }

    /**
     * Produces a Mono transformer that preserves the type of the source {@code Mono<IN>}.
     *
     * <p> The Mono produced expects a single element from the source, and calls the thenFunction with it.  Signals the original input element after the resulting {@code Mono<OUT>} <b>completes</b>.
     * </p>
     *
     * <p> <b>Summary:</b> does an {@code .after} on the new Mono which keeps the input to pass on unchanged. </p>
     *
     * <p> <b>Usage:</b> Can be used inline thus: {@code .as(thenCompleteKeep(in -> funcOf(in)))} </p>
     *
     * @param thenFunction from source input element to some {@code Mono<OUT>}
     * @param <IN>         the source element type
     * @param <OUT>        the element type of the Mono produced by {@code thenFunction}
     * @return a Mono transformer
     */
    private static <IN, OUT> Function<Mono<IN>, Mono<IN>> thenCompleteKeep(Function<IN, Mono<OUT>> thenFunction) {
        return source -> source
            .then(in -> thenFunction
                .apply(in)
                .after(() -> Mono.just(in)));
    }

    /**
     * Produces a Mono transformer that preserves the type of the source {@code Mono<IN>}.
     *
     * <p> The Mono produced expects a single element from the source, passes this to the function (as in {@code .then}) and requests an element from the resulting {@code Mono<OUT>}. When successful,
     * the result is discarded and input value is signalled. </p>
     *
     * <p> <b>Summary:</b> does a {@code .then} on the new Mono but keeps the input to pass on unchanged. </p>
     *
     * <p> <b>Usage:</b> Can be used inline thus: {@code .as(thenKeep(in -> funcOf(in)))} </p>
     *
     * @param thenFunction from source input element to some {@code Mono<OUT>}
     * @param <IN>         the source element type
     * @param <OUT>        the element type of the Mono produced by {@code thenFunction}
     * @return a Mono transformer
     */
    private static <IN, OUT> Function<Mono<IN>, Mono<IN>> thenKeep(Function<IN, Mono<OUT>> thenFunction) {
        return source -> source
            .then(in -> thenFunction
                .apply(in)
                .map(ignore -> in));
    }

}

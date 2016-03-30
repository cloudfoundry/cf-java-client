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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.domains.CreateDomainRequest;
import org.cloudfoundry.client.v2.domains.DomainEntity;
import org.cloudfoundry.client.v2.events.EventEntity;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperRequest;
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
import org.cloudfoundry.util.DateUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.core.tuple.Tuple2;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.function.Predicate;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.cloudfoundry.util.OperationUtils.thenKeep;
import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public final class SpacesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private String spaceName;

    @Autowired
    private String stackName;

    @Autowired
    private String userName;

    @Test
    public void associateAuditor() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> createUserId(this.cloudFoundryClient, organizationId, this.userName)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .then(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateAuditor(AssociateSpaceAuditorRequest.builder()
                    .spaceId(spaceId)
                    .auditorId(userId)
                    .build())
                .map(ResourceUtils::getId)
                .and(Mono.just(spaceId))))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateAuditorByUsername() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> createUserId(this.cloudFoundryClient, organizationId, this.userName)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateAuditorByUsername(AssociateSpaceAuditorByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(this.userName)
                    .build()))))
            .flatMap(function((userId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listAuditors(ListSpaceAuditorsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))
                .map(ResourceUtils::getEntity)
                .map(UserEntity::getUsername)))
            .subscribe(this.testSubscriber()
                .assertEquals(this.userName));
    }

    @Test
    public void associateDeveloper() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> createUserId(this.cloudFoundryClient, organizationId, this.userName)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .then(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateDeveloper(AssociateSpaceDeveloperRequest.builder()
                    .spaceId(spaceId)
                    .developerId(userId)
                    .build())
                .map(ResourceUtils::getId)
                .and(Mono.just(spaceId))))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateDeveloperByUsername() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> createUserId(this.cloudFoundryClient, organizationId, this.userName)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateDeveloperByUsername(AssociateSpaceDeveloperByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(this.userName)
                    .build()))))
            .flatMap(function((userId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listDevelopers(ListSpaceDevelopersRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))
                .map(ResourceUtils::getEntity)
                .map(UserEntity::getUsername)))
            .subscribe(this.testSubscriber()
                .assertEquals(this.userName));
    }

    @Test
    public void associateManager() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> createUserId(this.cloudFoundryClient, organizationId, this.userName)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .then(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateManager(AssociateSpaceManagerRequest.builder()
                    .spaceId(spaceId)
                    .managerId(userId)
                    .build())
                .map(ResourceUtils::getId)
                .and(Mono.just(spaceId))))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void associateManagerByUsername() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> createUserId(this.cloudFoundryClient, organizationId, this.userName)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateManagerByUsername(AssociateSpaceManagerByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(this.userName)
                    .build()))))
            .flatMap(function((userId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listManagers(ListSpaceManagersRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))
                .map(ResourceUtils::getEntity)
                .map(UserEntity::getUsername)))
            .subscribe(this.testSubscriber()
                .assertEquals(this.userName));
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522656")
    @Test
    public void associateSecurityGroup() {
        fail();
    }

    @Test
    public void create() {
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> this.cloudFoundryClient.spaces()
                .create(CreateSpaceRequest.builder()
                    .organizationId(organizationId)
                    .name(spaceName)
                    .build()))
            .map(ResourceUtils::getEntity)
            .map(SpaceEntity::getName)
            .subscribe(this.<String>testSubscriber()
                .assertEquals(spaceName));
    }

    @Test
    public void delete() {
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> this.cloudFoundryClient.spaces()
                .delete(DeleteSpaceRequest.builder()
                    .spaceId(spaceId)
                    .async(true)
                    .build())
                .then(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, job))
                .after(() -> Mono.just(spaceId)))
            .flatMap(spaceId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .list(ListSpacesRequest.builder()
                        .page(page)
                        .build()))
                .map(ResourceUtils::getId)
                .filter(spaceId::equals))
            .subscribe(testSubscriber());
    }

    @Test
    public void get() {
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .then(spaceId -> this.cloudFoundryClient.spaces()
                .get(GetSpaceRequest.builder()
                    .spaceId(spaceId)
                    .build()))
            .map(ResourceUtils::getEntity)
            .map(SpaceEntity::getName)
            .subscribe(this.<String>testSubscriber()
                .assertEquals(spaceName));
    }

    @Test
    public void getSummary() {
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .then(spaceId -> this.cloudFoundryClient.spaces()
                .getSummary(GetSpaceSummaryRequest.builder()
                    .spaceId(spaceId)
                    .build()))
            .map(GetSpaceSummaryResponse::getName)
            .and(Mono.just(spaceName))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void list() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> Mono.just(organizationId)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .then(function((organizationId, spaceId) -> Mono.just(spaceId)
                .and(PaginationUtils
                    .requestResources(page -> this.cloudFoundryClient.spaces()
                        .list(ListSpacesRequest.builder()
                            .page(page)
                            .build()))
                    .filter(hasOrganizationId(organizationId))
                    .single()
                    .map(ResourceUtils::getId))))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listApplications() {
        String applicationName = getApplicationName();
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .as(thenKeep(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName)))
            .then(spaceId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listApplications(ListSpaceApplicationsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))
                .single())
            .map(ApplicationResource::getEntity)
            .map(ApplicationEntity::getName)
            .and(Mono.just(applicationName))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listApplicationsFilterByDiego() {
        String applicationName = getApplicationName();
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .as(thenKeep(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName)))
            .then(spaceId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listApplications(ListSpaceApplicationsRequest.builder()
                        .page(page)
                        .diego(true)
                        .spaceId(spaceId)
                        .build()))
                .single())
            .map(ApplicationResource::getEntity)
            .map(ApplicationEntity::getName)
            .and(Mono.just(applicationName))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listApplicationsFilterByName() {
        String applicationName = getApplicationName();
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .then(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
                .and(Mono.just(spaceId)))
            .flatMap(function((applicationId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listApplications(ListSpaceApplicationsRequest.builder()
                        .page(page)
                        .name(applicationName)
                        .spaceId(spaceId)
                        .build()))
                .map(ResourceUtils::getId)
                .zipWith(Mono.just(applicationId))))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listApplicationsFilterByOrganizationId() {
        String applicationName = getApplicationName();
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> Mono.just(organizationId)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .as(thenKeep(function((organizationId, spaceId) -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))))
            .flatMap(function((organizationId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listApplications(ListSpaceApplicationsRequest.builder()
                        .page(page)
                        .organizationId(organizationId)
                        .spaceId(spaceId)
                        .build()))))
            .map(ResourceUtils::getEntity)
            .map(ApplicationEntity::getName)
            .subscribe(this.<String>testSubscriber()
                .assertEquals(applicationName));
    }

    @Test
    public void listApplicationsFilterByStackId() {
        String applicationName = getApplicationName();
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .as(thenKeep(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName)))
            .then(spaceId -> Mono.just(spaceId)
                .and(PaginationUtils
                    .requestResources(page -> this.cloudFoundryClient.stacks()
                        .list(ListStacksRequest.builder()
                            .name(stackName)
                            .page(page)
                            .build()))
                    .map(ResourceUtils::getId)
                    .single()))
            .flatMap(function((spaceId, stackId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listApplications(ListSpaceApplicationsRequest.builder()
                        .page(page)
                        .stackId(stackId)
                        .spaceId(spaceId)
                        .build()))))
            .map(ResourceUtils::getEntity)
            .map(ApplicationEntity::getName)
            .subscribe(this.<String>testSubscriber()
                .assertEquals(applicationName));
    }

    @Test
    public void listAuditors() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> createUserId(this.cloudFoundryClient, organizationId, this.userName)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateAuditor(AssociateSpaceAuditorRequest.builder()
                    .spaceId(spaceId)
                    .auditorId(userId)
                    .build()))))
            .flatMap(function((userId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listAuditors(ListSpaceAuditorsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))))
            .map(ResourceUtils::getEntity)
            .map(UserEntity::getUsername)
            .subscribe(this.testSubscriber()
                .assertEquals(this.userName));
    }

    @Test
    public void listDevelopers() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> createUserId(this.cloudFoundryClient, organizationId, this.userName)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateDeveloper(AssociateSpaceDeveloperRequest.builder()
                    .spaceId(spaceId)
                    .developerId(userId)
                    .build()))))
            .flatMap(function((userId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listDevelopers(ListSpaceDevelopersRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))))
            .map(ResourceUtils::getEntity)
            .map(UserEntity::getUsername)
            .subscribe(this.testSubscriber()
                .assertEquals(this.userName));
    }

    @Test
    public void listDomains() {
        String domainName = getDomainName();
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceIdWithDomain(this.cloudFoundryClient, organizationId, spaceName, domainName))
            .then(spaceId -> Mono.just(spaceId)
                .and(PaginationUtils
                    .requestResources(page -> this.cloudFoundryClient.spaces()
                        .listDomains(ListSpaceDomainsRequest.builder()
                            .name(domainName)
                            .spaceId(spaceId)
                            .page(page)
                            .build()))
                    .single()
                    .map(ResourceUtils::getId)
                ))
            .flatMap(function((spaceId, domainId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listDomains(ListSpaceDomainsRequest.builder()
                        .spaceId(spaceId)
                        .page(page)
                        .build()))))
            .filter(domainResource -> domainName.equals(ResourceUtils.getEntity(domainResource).getName()))
            .subscribe(testSubscriber()
                .assertCount(1));
    }

    @Test
    public void listDomainsFilterByName() {
        String domainName = getDomainName();
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceIdWithDomain(this.cloudFoundryClient, organizationId, spaceName, domainName))
            .flatMap(spaceId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listDomains(ListSpaceDomainsRequest.builder()
                        .spaceId(spaceId)
                        .name(domainName)
                        .page(page)
                        .build())))
            .map(ResourceUtils::getEntity)
            .map(DomainEntity::getName)
            .subscribe(this.<String>testSubscriber()
                .assertEquals(domainName));
    }

    @Test
    public void listDomainsFilterByOwningOrganizationId() {
        String domainName = getDomainName();
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> Mono.just(organizationId)
                .and(createSpaceIdWithDomain(this.cloudFoundryClient, organizationId, spaceName, domainName)))
            .flatMap(function((organizationId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listDomains(ListSpaceDomainsRequest.builder()
                        .spaceId(spaceId)
                        .owningOrganizationId(organizationId)
                        .page(page)
                        .build()))))
            .map(ResourceUtils::getEntity)
            .map(DomainEntity::getName)
            .subscribe(this.<String>testSubscriber()
                .assertEquals(domainName));
    }

    @Test
    public void listEvents() {
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listEvents(ListSpaceEventsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build())))
            .map(ResourceUtils::getEntity)
            .map(EventEntity::getType)
            .subscribe(this.<String>testSubscriber()
                .assertEquals("audit.space.create"));
    }

    @Test
    public void listEventsFilterByActee() {
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listEvents(ListSpaceEventsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .actee(spaceId)
                        .build())))
            .map(ResourceUtils::getEntity)
            .map(EventEntity::getType)
            .subscribe(this.<String>testSubscriber()
                .assertEquals("audit.space.create"));
    }

    @Test
    public void listEventsFilterByTimestamp() {
        String spaceName = getSpaceName();
        String timestamp = getPastTimestamp();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listEvents(ListSpaceEventsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .timestamp(timestamp)
                        .build())))
            .map(ResourceUtils::getEntity)
            .map(EventEntity::getType)
            .subscribe(this.<String>testSubscriber()
                .assertEquals("audit.space.create"));
    }

    @Test
    public void listEventsFilterByType() {
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .then(spaceId -> Mono.just(spaceId)
                .and(PaginationUtils
                    .requestResources(page -> this.cloudFoundryClient.spaces()
                        .listEvents(ListSpaceEventsRequest.builder()
                            .page(page)
                            .spaceId(spaceId)
                            .type("audit.space.create")
                            .build()))
                    .single()
                    .map(ResourceUtils::getEntity)
                    .map(EventEntity::getSpaceId)
                ))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listFilterByApplicationId() {
        String applicationName = getApplicationName();
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .then(spaceId -> Mono.just(spaceId)
                .and(createApplicationId(this.cloudFoundryClient, spaceId, applicationName)))
            .then(function((spaceId, applicationId) -> Mono.just(spaceId)
                .and(PaginationUtils
                    .requestResources(page -> this.cloudFoundryClient.spaces()
                        .list(ListSpacesRequest.builder()
                            .page(page)
                            .applicationId(applicationId)
                            .build()))
                    .map(ResourceUtils::getId)
                    .single())))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522686 really create a new user")
    @Test
    public void listFilterByDeveloperId() {
        fail();
    }

    @Test
    public void listFilterByName() {
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .then(spaceId -> Mono.just(spaceId)
                .and(PaginationUtils
                    .requestResources(page -> this.cloudFoundryClient.spaces()
                        .list(ListSpacesRequest.builder()
                            .name(spaceName)
                            .page(page)
                            .build()))
                    .map(ResourceUtils::getId)
                    .single()))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listFilterByOrganizationId() {
        String organizationName = getOrganizationName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> Mono.just(organizationId)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .then(function((organizationId, spaceId) -> Mono.just(spaceId)
                .and(PaginationUtils
                    .requestResources(page -> this.cloudFoundryClient.spaces()
                        .list(ListSpacesRequest.builder()
                            .page(page)
                            .organizationId(organizationId)
                            .build()))
                    .single()
                    .map(ResourceUtils::getId))))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listManagers() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> createUserId(this.cloudFoundryClient, organizationId, this.userName)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .as(thenKeep(function((userId, spaceId) -> associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId))))
            .flatMap(function((userId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listManagers(ListSpaceManagersRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))
                .map(ResourceUtils::getEntity)
                .map(UserEntity::getUsername)))
            .subscribe(this.testSubscriber()
                .assertEquals(this.userName));
    }

    @Test
    public void listManagersFilterByAuditedOrganizationId() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.userName),
                    createSpaceId(this.cloudFoundryClient, organizationId, spaceName),
                    Mono.just(organizationId)
                ))
            .as(thenKeep(function((userId, spaceId, organizationId) -> associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId)
                .and(this.cloudFoundryClient.organizations()
                    .associateAuditor(AssociateOrganizationAuditorRequest.builder()
                        .organizationId(organizationId)
                        .auditorId(userId)
                        .build())))))
            .flatMap(function((userId, spaceId, organizationId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listManagers(ListSpaceManagersRequest.builder()
                        .spaceId(spaceId)
                        .page(page)
                        .auditedOrganizationId(organizationId)
                        .build()))
                .map(ResourceUtils::getEntity)
                .map(UserEntity::getUsername)))
            .subscribe(this.testSubscriber()
                .assertEquals(this.userName));
    }

    @Test
    public void listManagersFilterByAuditedSpaceId() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> createUserId(this.cloudFoundryClient, organizationId, this.userName)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .as(thenKeep(function((userId, spaceId) -> associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId)
                .and(associateAuditorWithSpace(this.cloudFoundryClient, spaceId, userId)))))
            .flatMap(function((userId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listManagers(ListSpaceManagersRequest.builder()
                        .spaceId(spaceId)
                        .page(page)
                        .auditedSpaceId(spaceId)
                        .build()))
                .map(ResourceUtils::getEntity)
                .map(UserEntity::getUsername)))
            .subscribe(this.testSubscriber()
                .assertEquals(this.userName));
    }

    @Test
    public void listManagersFilterByBillingManagedOrganizationId() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.userName),
                    createSpaceId(this.cloudFoundryClient, organizationId, spaceName),
                    Mono.just(organizationId)
                ))
            .as(thenKeep(function((userId, spaceId, organizationId) -> associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId)
                .and(this.cloudFoundryClient.organizations()
                    .associateBillingManager(AssociateOrganizationBillingManagerRequest.builder()
                        .organizationId(organizationId)
                        .billingManagerId(userId)
                        .build())))))
            .then(function((userId, spaceId, organizationId) -> Mono.just(userId)
                .and(PaginationUtils
                    .requestResources(page -> this.cloudFoundryClient.spaces()
                        .listManagers(ListSpaceManagersRequest.builder()
                            .spaceId(spaceId)
                            .page(page)
                            .billingManagedOrganizationId(organizationId)
                            .build()))
                    .single()
                    .map(ResourceUtils::getId))))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listManagersFilterByManagedOrganizationId() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.userName),
                    createSpaceId(this.cloudFoundryClient, organizationId, spaceName),
                    Mono.just(organizationId)
                ))
            .as(thenKeep(function((userId, spaceId, organizationId) -> associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId)
                .and(this.cloudFoundryClient.organizations()
                    .associateManager(AssociateOrganizationManagerRequest.builder()
                        .organizationId(organizationId)
                        .managerId(userId)
                        .build())))))
            .then(function((userId, spaceId, organizationId) -> Mono.just(userId)
                .and(PaginationUtils
                    .requestResources(page -> this.cloudFoundryClient.spaces()
                        .listManagers(ListSpaceManagersRequest.builder()
                            .spaceId(spaceId)
                            .page(page)
                            .managedOrganizationId(organizationId)
                            .build()))
                    .single()
                    .map(ResourceUtils::getId)
                )))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listManagersFilterByManagedSpaceId() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> createUserId(this.cloudFoundryClient, organizationId, this.userName)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .as(thenKeep(function((userId, spaceId) -> associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId))))
            .flatMap(function((userId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listManagers(ListSpaceManagersRequest.builder()
                        .spaceId(spaceId)
                        .page(page)
                        .managedSpaceId(spaceId)
                        .build()))
                .map(ResourceUtils::getEntity)
                .map(UserEntity::getUsername)))
            .subscribe(this.testSubscriber()
                .assertEquals(this.userName));
    }

    @Test
    public void listManagersFilterByOrganizationId() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> Mono
                .when(
                    createUserId(this.cloudFoundryClient, organizationId, this.userName),
                    createSpaceId(this.cloudFoundryClient, organizationId, spaceName),
                    Mono.just(organizationId)
                ))
            .as(thenKeep(function((userId, spaceId, organizationId) -> associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId))))
            .then(function((userId, spaceId, organizationId) -> Mono.just(userId)
                .and(PaginationUtils
                    .requestResources(page -> this.cloudFoundryClient.spaces()
                        .listManagers(ListSpaceManagersRequest.builder()
                            .spaceId(spaceId)
                            .page(page)
                            .organizationId(organizationId)
                            .build()))
                    .single()
                    .map(ResourceUtils::getId))))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listRoutes() {
        String domainName = getDomainName();
        String hostName = getHostName();
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceIdWithDomain(this.cloudFoundryClient, organizationId, spaceName, domainName))
            .then(spaceId -> Mono.just(spaceId)
                .and(createRouteId(this.cloudFoundryClient, spaceId, domainName, hostName, "/test-path")))
            .flatMap(function((spaceId, routeId) -> Mono.just(routeId)
                .and(PaginationUtils
                    .requestResources(page -> this.cloudFoundryClient.spaces()
                        .listRoutes(ListSpaceRoutesRequest.builder()
                            .spaceId(spaceId)
                            .build()))
                    .map(ResourceUtils::getId)
                    .single())))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listRoutesFilterByDomainId() {
        String domainName = getDomainName();
        String hostName = getHostName();
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceIdWithDomain(this.cloudFoundryClient, organizationId, spaceName, domainName))
            .then(spaceId -> Mono
                .when(
                    Mono.just(spaceId),
                    PaginationUtils
                        .requestResources(page -> this.cloudFoundryClient.spaces()
                            .listDomains(ListSpaceDomainsRequest.builder()
                                .name(domainName)
                                .spaceId(spaceId)
                                .page(page)
                                .build()))
                        .single()
                        .map(ResourceUtils::getId),
                    createRouteId(this.cloudFoundryClient, spaceId, domainName, hostName, "/test-path")
                ))
            .flatMap(function((spaceId, domainId, routeId) -> Mono.just(routeId)
                .and(PaginationUtils
                    .requestResources(page -> this.cloudFoundryClient.spaces()
                        .listRoutes(ListSpaceRoutesRequest.builder()
                            .spaceId(spaceId)
                            .domainId(domainId)
                            .build()))
                    .map(ResourceUtils::getId)
                    .single())))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listRoutesFilterByHost() {
        String domainName = getDomainName();
        String hostName = getHostName();
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceIdWithDomain(this.cloudFoundryClient, organizationId, spaceName, domainName))
            .then(spaceId -> Mono.just(spaceId)
                .and(createRouteId(this.cloudFoundryClient, spaceId, domainName, hostName, "/test-path")))
            .flatMap(function((spaceId, routeId) -> Mono.just(routeId)
                .and(PaginationUtils
                    .requestResources(page -> this.cloudFoundryClient.spaces()
                        .listRoutes(ListSpaceRoutesRequest.builder()
                            .spaceId(spaceId)
                            .host(hostName)
                            .build()))
                    .map(ResourceUtils::getId)
                    .single())))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    @Test
    public void listRoutesFilterByPath() {
        String domainName = getDomainName();
        String hostName = getHostName();
        String spaceName = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceIdWithDomain(this.cloudFoundryClient, organizationId, spaceName, domainName))
            .then(spaceId -> Mono.just(spaceId)
                .and(createRouteId(this.cloudFoundryClient, spaceId, domainName, hostName, "/test-path")))
            .flatMap(function((spaceId, routeId) -> Mono.just(routeId)
                .and(PaginationUtils
                    .requestResources(page -> this.cloudFoundryClient.spaces()
                        .listRoutes(ListSpaceRoutesRequest.builder()
                            .spaceId(spaceId)
                            .path("/test-path")
                            .build()))
                    .map(ResourceUtils::getId)
                    .single())))
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
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> createUserId(this.cloudFoundryClient, organizationId, this.userName)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .as(thenKeep(function((userId, spaceId) -> associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId))))
            .flatMap(function((userId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listUserRoles(ListSpaceUserRolesRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))
                .map(ResourceUtils::getEntity)))
            .subscribe(this.<UserSpaceRoleEntity>testSubscriber()
                .assertThat(userSpaceRoleEntity -> {
                    assertEquals(this.userName, userSpaceRoleEntity.getUsername());
                    assertEquals(Collections.singletonList("space_manager"), userSpaceRoleEntity.getSpaceRoles());
                }));
    }

    @Test
    public void removeAuditor() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> createUserId(this.cloudFoundryClient, organizationId, this.userName)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .as(thenKeep(function((userId, spaceId) -> associateAuditorWithSpace(this.cloudFoundryClient, spaceId, userId))))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .removeAuditor(RemoveSpaceAuditorRequest.builder()
                    .spaceId(spaceId)
                    .auditorId(userId)
                    .build()))))
            .flatMap(function((userId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listAuditors(ListSpaceAuditorsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))))
            .subscribe(this.testSubscriber());
    }

    @Test
    public void removeAuditorByUsername() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> createUserId(this.cloudFoundryClient, organizationId, this.userName)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .as(thenKeep(function((userId, spaceId) -> associateAuditorWithSpace(this.cloudFoundryClient, spaceId, userId))))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .removeAuditorByUsername(RemoveSpaceAuditorByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(this.userName)
                    .build()))))
            .flatMap(function((userId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listAuditors(ListSpaceAuditorsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))))
            .subscribe(this.testSubscriber());
    }

    @Test
    public void removeDeveloper() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> createUserId(this.cloudFoundryClient, organizationId, this.userName)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateDeveloper(AssociateSpaceDeveloperRequest.builder()
                    .spaceId(spaceId)
                    .developerId(userId)
                    .build()))))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .removeDeveloper(RemoveSpaceDeveloperRequest.builder()
                    .spaceId(spaceId)
                    .developerId(userId)
                    .build()))))
            .flatMap(function((userId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listDevelopers(ListSpaceDevelopersRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))))
            .subscribe(this.testSubscriber());
    }

    @Test
    public void removeDeveloperByUsername() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> createUserId(this.cloudFoundryClient, organizationId, this.userName)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateDeveloper(AssociateSpaceDeveloperRequest.builder()
                    .spaceId(spaceId)
                    .developerId(userId)
                    .build()))))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .removeDeveloperByUsername(RemoveSpaceDeveloperByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(this.userName)
                    .build()))))
            .flatMap(function((userId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listDevelopers(ListSpaceDevelopersRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))))
            .subscribe(this.testSubscriber());
    }

    @Test
    public void removeManager() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> createUserId(this.cloudFoundryClient, organizationId, this.userName)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .as(thenKeep(function((userId, spaceId) -> associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId))))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .removeManager(RemoveSpaceManagerRequest.builder()
                    .spaceId(spaceId)
                    .managerId(userId)
                    .build()))))
            .flatMap(function((userId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listManagers(ListSpaceManagersRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))
                .map(ResourceUtils::getEntity)
                .map(UserEntity::getUsername)
            ))
            .subscribe(this.testSubscriber());
    }

    @Test
    public void removeManagerByUsername() {
        String organizationName = getOrganizationName();
        String spaceName = getSpaceName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .then(organizationId -> createUserId(this.cloudFoundryClient, organizationId, this.userName)
                .and(createSpaceId(this.cloudFoundryClient, organizationId, spaceName)))
            .as(thenKeep(function((userId, spaceId) -> associateManagerWithSpace(this.cloudFoundryClient, spaceId, userId))))
            .as(thenKeep(function((userId, spaceId) -> this.cloudFoundryClient.spaces()
                .removeManagerByUsername(RemoveSpaceManagerByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(this.userName)
                    .build()))))
            .flatMap(function((userId, spaceId) -> PaginationUtils
                .requestResources(page -> this.cloudFoundryClient.spaces()
                    .listManagers(ListSpaceManagersRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))
                .map(ResourceUtils::getEntity)
                .map(UserEntity::getUsername)))
            .subscribe(this.testSubscriber());
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522658")
    @Test
    public void removeSecurityGroup() {

    }

    @Test
    public void update() {
        String spaceName = getSpaceName();
        String spaceName2 = getSpaceName();

        this.organizationId
            .then(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .as(thenKeep(spaceId -> this.cloudFoundryClient.spaces()
                .update(UpdateSpaceRequest.builder()
                    .spaceId(spaceId)
                    .name(spaceName2)
                    .build())))
            .then(spaceId -> this.cloudFoundryClient.spaces()
                .getSummary(GetSpaceSummaryRequest.builder()
                    .spaceId(spaceId)
                    .build()))
            .map(GetSpaceSummaryResponse::getName)
            .and(Mono.just(spaceName2))
            .subscribe(this.<Tuple2<String, String>>testSubscriber()
                .assertThat(this::assertTupleEquality));
    }

    private static Mono<AssociateSpaceAuditorResponse> associateAuditorWithSpace(CloudFoundryClient cloudFoundryClient, String spaceId, String userId) {
        return cloudFoundryClient.spaces()
            .associateAuditor(AssociateSpaceAuditorRequest.builder()
                .spaceId(spaceId)
                .auditorId(userId)
                .build());
    }

    private static Mono<AssociateSpaceManagerResponse> associateManagerWithSpace(CloudFoundryClient cloudFoundryClient, String spaceId, String userId) {
        return cloudFoundryClient.spaces()
            .associateManager(AssociateSpaceManagerRequest.builder()
                .spaceId(spaceId)
                .managerId(userId)
                .build());
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .diego(true)
                .name(applicationName)
                .spaceId(spaceId)
                .build())
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createOrganizationId(CloudFoundryClient cloudFoundryClient, String organization) {
        return cloudFoundryClient.organizations()
            .create(CreateOrganizationRequest.builder()
                .name(organization)
                .build())
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createRouteId(CloudFoundryClient cloudFoundryClient, String spaceId, String domainName, String host, String path) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.spaces()
                .listDomains(ListSpaceDomainsRequest.builder()
                    .name(domainName)
                    .spaceId(spaceId)
                    .page(page)
                    .build()))
            .single()
            .map(ResourceUtils::getId)
            .then(domainId -> cloudFoundryClient.routes()
                .create(CreateRouteRequest.builder()
                    .spaceId(spaceId)
                    .domainId(domainId)
                    .host(host)
                    .path(path)
                    .build()))
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return cloudFoundryClient.spaces()
            .create(CreateSpaceRequest.builder()
                .organizationId(organizationId)
                .name(spaceName)
                .build())
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createSpaceIdWithDomain(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName, String domainName) {
        return cloudFoundryClient.domains()
            .create(CreateDomainRequest.builder()
                .name(domainName)
                .owningOrganizationId(organizationId)
                .wildcard(true)
                .build())
            .map(ResourceUtils::getId)
            .then(domainId -> cloudFoundryClient.spaces()
                .create(CreateSpaceRequest.builder()
                    .organizationId(organizationId)
                    .name(spaceName)
                    .build()))
            .map(ResourceUtils::getId);
    }

    // TODO: after: https://www.pivotaltracker.com/story/show/101522686 really create a new user
    private static Mono<String> createUserId(CloudFoundryClient cloudFoundryClient, String organizationId, String username) {
        return cloudFoundryClient.organizations()
            .associateUserByUsername(AssociateOrganizationUserByUsernameRequest.builder()
                .organizationId(organizationId)
                .username(username)
                .build())
            .after(() -> PaginationUtils
                .requestResources(page -> cloudFoundryClient.users()
                    .list(ListUsersRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build()))
                .filter(resource -> ResourceUtils.getEntity(resource).getUsername().equals(username))
                .single()
                .map(ResourceUtils::getId)
            );
    }

    private static String getPastTimestamp() {
        Date past = Date.from(Instant.now().minus(1, HOURS));
        return DateUtils.formatToIso8601(past);
    }

    private static Predicate<SpaceResource> hasOrganizationId(String organizationId) {
        return spaceResource -> ResourceUtils.getEntity(spaceResource).getOrganizationId().equals(organizationId);
    }

}

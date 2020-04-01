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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.events.EventResource;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserRequest;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.securitygroups.CreateSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.CreateSecurityGroupResponse;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupResource;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.UnionServiceInstanceResource;
import org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyRequest;
import org.cloudfoundry.client.v2.servicekeys.CreateServiceKeyResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.services.ListServicesRequest;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceSecurityGroupRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceSecurityGroupResponse;
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
import org.cloudfoundry.client.v2.spaces.ListSpaceSecurityGroupsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServicesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceUserRolesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceAuditorRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceDeveloperByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceDeveloperRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceManagerByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceManagerRequest;
import org.cloudfoundry.client.v2.spaces.RemoveSpaceSecurityGroupRequest;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.spaces.UpdateSpaceRequest;
import org.cloudfoundry.client.v2.users.CreateUserRequest;
import org.cloudfoundry.client.v2.users.UserEntity;
import org.cloudfoundry.client.v2.users.UserResource;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.users.CreateUserResponse;
import org.cloudfoundry.uaa.users.Email;
import org.cloudfoundry.uaa.users.Name;
import org.cloudfoundry.util.DateUtils;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class SpacesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private Mono<String> serviceBrokerId;

    @Autowired
    private String serviceName;

    @Autowired
    private Mono<String> spaceId;

    @Autowired
    private Mono<String> stackId;

    @Autowired
    private UaaClient uaaClient;

    @Test
    public void associateAuditor() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .flatMap(function((spaceId, userId) -> Mono.zip(
                Mono.just(spaceId),
                this.cloudFoundryClient.spaces()
                    .associateAuditor(AssociateSpaceAuditorRequest.builder()
                        .spaceId(spaceId)
                        .auditorId(userId)
                        .build())
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateAuditorByUsername() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .delayUntil(function((spaceId, userId) -> this.cloudFoundryClient.spaces()
                .associateAuditorByUsername(AssociateSpaceAuditorByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(userName)
                    .build())))
            .flatMapMany(function((spaceId, userId) -> requestListSpaceAuditors(this.cloudFoundryClient, spaceId)
                .map(ResourceUtils::getEntity)
                .map(UserEntity::getUsername)))
            .as(StepVerifier::create)
            .expectNext(userName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateDeveloper() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .flatMap(function((spaceId, userId) -> Mono.zip(
                Mono.just(spaceId),
                this.cloudFoundryClient.spaces()
                    .associateDeveloper(AssociateSpaceDeveloperRequest.builder()
                        .spaceId(spaceId)
                        .developerId(userId)
                        .build())
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateDeveloperByUsername() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .delayUntil(function((spaceId, userId) -> this.cloudFoundryClient.spaces()
                .associateDeveloperByUsername(AssociateSpaceDeveloperByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(userName)
                    .build())))
            .flatMapMany(function((spaceId, userId) -> requestListSpaceDevelopers(this.cloudFoundryClient, spaceId)
                .map(response -> ResourceUtils.getEntity(response).getUsername())))
            .as(StepVerifier::create)
            .expectNext(userName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateManager() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .flatMap(function((spaceId, userId) -> Mono.zip(
                Mono.just(spaceId),
                this.cloudFoundryClient.spaces()
                    .associateManager(AssociateSpaceManagerRequest.builder()
                        .spaceId(spaceId)
                        .managerId(userId)
                        .build())
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateManagerByUsername() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .delayUntil(function((spaceId, userId) -> this.cloudFoundryClient.spaces()
                .associateManagerByUsername(AssociateSpaceManagerByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(userName)
                    .build())))
            .flatMapMany(function((spaceId, userId) -> requestListSpaceManagers(this.cloudFoundryClient, spaceId)
                .map(response -> ResourceUtils.getEntity(response).getUsername())))
            .as(StepVerifier::create)
            .expectNext(userName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void associateSecurityGroup() {
        String securityGroupName = this.nameFactory.getSecurityGroupName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createSecurityGroupId(this.cloudFoundryClient, securityGroupName),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((securityGroupId, spaceId) -> this.cloudFoundryClient.spaces()
                .associateSecurityGroup(AssociateSpaceSecurityGroupRequest.builder()
                    .securityGroupId(securityGroupId)
                    .spaceId(spaceId)
                    .build())
                .thenReturn(spaceId)))
            .flatMapMany(spaceId -> requestListSecurityGroups(this.cloudFoundryClient, spaceId))
            .filter(response -> securityGroupName.equals(response.getEntity().getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() {
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> this.cloudFoundryClient.spaces()
                .create(CreateSpaceRequest.builder()
                    .organizationId(organizationId)
                    .name(spaceName)
                    .build()))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .as(StepVerifier::create)
            .expectNext(spaceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .delayUntil(spaceId -> this.cloudFoundryClient.spaces()
                .delete(DeleteSpaceRequest.builder()
                    .spaceId(spaceId)
                    .async(true)
                    .build())
                .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job)))
            .flatMapMany(spaceId -> requestListSpaces(this.cloudFoundryClient)
                .map(ResourceUtils::getId)
                .filter(spaceId::equals))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAsyncFalse() {
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .delayUntil(spaceId -> this.cloudFoundryClient.spaces()
                .delete(DeleteSpaceRequest.builder()
                    .spaceId(spaceId)
                    .async(false)
                    .build()))
            .flatMapMany(spaceId -> requestListSpaces(this.cloudFoundryClient)
                .map(ResourceUtils::getId)
                .filter(spaceId::equals))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> this.cloudFoundryClient.spaces()
                .get(GetSpaceRequest.builder()
                    .spaceId(spaceId)
                    .build()))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .as(StepVerifier::create)
            .expectNext(spaceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getSummary() {
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> this.cloudFoundryClient.spaces()
                .getSummary(GetSpaceSummaryRequest.builder()
                    .spaceId(spaceId)
                    .build()))
            .map(GetSpaceSummaryResponse::getName)
            .as(StepVerifier::create)
            .expectNext(spaceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        createOrganizationIdAndSpaceId(this.cloudFoundryClient, organizationName, spaceName)
            .flatMap(function((organizationId, spaceId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.spaces()
                    .list(ListSpacesRequest.builder()
                        .page(page)
                        .build()))
                .filter(response -> organizationId.equals(ResourceUtils.getEntity(response).getOrganizationId()))
                .single()
                .map(response -> ResourceUtils.getEntity(response).getName())))
            .as(StepVerifier::create)
            .expectNext(spaceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listApplications() {
        String applicationName = this.nameFactory.getApplicationName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .delayUntil(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .flatMap(spaceId -> requestListSpaceApplications(this.cloudFoundryClient, spaceId)
                .single())
            .map(response -> ResourceUtils.getEntity(response).getName())
            .as(StepVerifier::create)
            .expectNext(applicationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listApplicationsFilterByDiego() {
        String applicationName = this.nameFactory.getApplicationName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .delayUntil(spaceId -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            .flatMap(spaceId -> requestListSpaceApplications(this.cloudFoundryClient, spaceId, builder -> builder.diego(true))
                .single())
            .map(response -> ResourceUtils.getEntity(response).getName())
            .as(StepVerifier::create)
            .expectNext(applicationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listApplicationsFilterByName() {
        String applicationName = this.nameFactory.getApplicationName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> Mono.zip(
                Mono.just(spaceId),
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
            ))
            .flatMapMany(function((spaceId, applicationId) -> Mono.zip(
                Mono.just(applicationId),
                requestListSpaceApplications(this.cloudFoundryClient, spaceId, builder -> builder.name(applicationName))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listApplicationsFilterByOrganizationId() {
        String applicationName = this.nameFactory.getApplicationName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .delayUntil(function((organizationId, spaceId) -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName)))
            .flatMapMany(function((organizationId, spaceId) -> requestListSpaceApplications(this.cloudFoundryClient, spaceId, builder -> builder.organizationId(organizationId))))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .as(StepVerifier::create)
            .expectNext(applicationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listApplicationsFilterByStackId() {
        String applicationName = this.nameFactory.getApplicationName();
        String spaceName = this.nameFactory.getSpaceName();

        Mono.zip(this.organizationId, this.stackId)
            .flatMap(function((organizationId, stackId) -> Mono.zip(
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName),
                Mono.just(stackId)
            )))
            .delayUntil(function((spaceId, stackId) -> createApplicationId(this.cloudFoundryClient, spaceId, applicationName, stackId)))
            .flatMapMany(function((spaceId, stackId) -> requestListSpaceApplications(this.cloudFoundryClient, spaceId, builder -> builder.stackId(stackId))))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .as(StepVerifier::create)
            .expectNext(applicationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listAuditors() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .delayUntil(function((spaceId, userId) -> this.cloudFoundryClient.spaces()
                .associateAuditor(AssociateSpaceAuditorRequest.builder()
                    .spaceId(spaceId)
                    .auditorId(userId)
                    .build())))
            .flatMapMany(function((spaceId, userId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.spaces()
                    .listAuditors(ListSpaceAuditorsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))))
            .map(response -> ResourceUtils.getEntity(response).getUsername())
            .as(StepVerifier::create)
            .expectNext(userName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listDevelopers() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .delayUntil(function((spaceId, userId) -> this.cloudFoundryClient.spaces()
                .associateDeveloper(AssociateSpaceDeveloperRequest.builder()
                    .spaceId(spaceId)
                    .developerId(userId)
                    .build())))
            .flatMapMany(function((spaceId, userId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.spaces()
                    .listDevelopers(ListSpaceDevelopersRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))))
            .map(response -> ResourceUtils.getEntity(response).getUsername())
            .as(StepVerifier::create)
            .expectNext(userName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void listDomains() {
        String domainName = this.nameFactory.getDomainName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceIdWithDomain(this.cloudFoundryClient, organizationId, spaceName, domainName))
            .flatMapMany(spaceId -> requestListSpaceDomains(this.cloudFoundryClient, spaceId))
            .filter(domainResource -> domainName.equals(ResourceUtils.getEntity(domainResource).getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void listDomainsFilterByName() {
        String domainName = this.nameFactory.getDomainName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceIdWithDomain(this.cloudFoundryClient, organizationId, spaceName, domainName))
            .flatMapMany(spaceId -> requestListSpaceDomains(this.cloudFoundryClient, spaceId, builder -> builder.name(domainName)))
            .map(response -> ResourceUtils.getEntity(response).getName())
            .as(StepVerifier::create)
            .expectNext(domainName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listEvents() {
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMapMany(spaceId -> requestListSpaceEvents(this.cloudFoundryClient, spaceId))
            .map(response -> ResourceUtils.getEntity(response).getType())
            .as(StepVerifier::create)
            .expectNext("audit.space.create")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listEventsFilterByActee() {
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMapMany(spaceId -> requestListSpaceEvents(this.cloudFoundryClient, spaceId, builder -> builder.actee(spaceId)))
            .map(response -> ResourceUtils.getEntity(response).getType())
            .as(StepVerifier::create)
            .expectNext("audit.space.create")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listEventsFilterByTimestamp() {
        String spaceName = this.nameFactory.getSpaceName();
        String timestamp = getPastTimestamp();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMapMany(spaceId -> requestListSpaceEvents(this.cloudFoundryClient, spaceId, builder -> builder.timestamp(timestamp)))
            .map(response -> ResourceUtils.getEntity(response).getType())
            .as(StepVerifier::create)
            .expectNext("audit.space.create")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listEventsFilterByType() {
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> Mono.zip(
                Mono.just(spaceId),
                requestListSpaceEvents(this.cloudFoundryClient, spaceId, builder -> builder.type("audit.space.create"))
                    .single()
                    .map(response -> ResourceUtils.getEntity(response).getSpaceId())
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByApplicationId() {
        String applicationName = this.nameFactory.getApplicationName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> Mono.zip(
                Mono.just(spaceId),
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName)
            ))
            .flatMap(function((spaceId, applicationId) -> Mono.zip(
                Mono.just(spaceId),
                requestListSpaces(this.cloudFoundryClient, builder -> builder.applicationId(applicationId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByDeveloperId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .delayUntil(function((spaceId, userId) -> requestAssociateSpaceDeveloper(this.cloudFoundryClient, userId, spaceId)))
            .flatMap(function((spaceId, userId) -> Mono.zip(
                Mono.just(spaceId),
                requestListSpaces(this.cloudFoundryClient, builder -> builder.developerId(userId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByName() {
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .flatMap(spaceId -> Mono.zip(
                Mono.just(spaceId),
                requestListSpaces(this.cloudFoundryClient, builder -> builder.name(spaceName))
                    .single()
                    .map(ResourceUtils::getId)
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByOrganizationId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        createOrganizationIdAndSpaceId(this.cloudFoundryClient, organizationName, spaceName)
            .flatMap(function((organizationId, spaceId) -> Mono.zip(
                Mono.just(spaceId),
                requestListSpaces(this.cloudFoundryClient, builder -> builder.organizationId(organizationId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagers() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .delayUntil(function((spaceId, userId) -> requestAssociateSpaceManager(this.cloudFoundryClient, spaceId, userId)))
            .flatMapMany(function((spaceId, userId) -> requestListSpaceManagers(this.cloudFoundryClient, spaceId)
                .map(response -> ResourceUtils.getEntity(response).getUsername())))
            .as(StepVerifier::create)
            .expectNext(userName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagersFilterByAuditedOrganizationId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                createUserId(this.cloudFoundryClient, this.uaaClient, organizationId, userName),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName),
                Mono.just(organizationId)
            ))
            .delayUntil(function((userId, spaceId, organizationId) -> Mono.zip(
                requestAssociateSpaceManager(this.cloudFoundryClient, spaceId, userId),
                this.cloudFoundryClient.organizations()
                    .associateAuditor(AssociateOrganizationAuditorRequest.builder()
                        .organizationId(organizationId)
                        .auditorId(userId)
                        .build()))))
            .flatMapMany(function((userId, spaceId, organizationId) -> requestListSpaceManagers(this.cloudFoundryClient, spaceId, builder -> builder.auditedOrganizationId(organizationId))
                .map(response -> ResourceUtils.getEntity(response).getUsername())))
            .as(StepVerifier::create)
            .expectNext(userName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagersFilterByAuditedSpaceId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .delayUntil(function((spaceId, userId) -> Mono.zip(
                requestAssociateSpaceManager(this.cloudFoundryClient, spaceId, userId),
                requestAssociateSpaceAuditor(this.cloudFoundryClient, spaceId, userId)
            )))
            .flatMapMany(function((spaceId, userId) -> requestListSpaceManagers(this.cloudFoundryClient, spaceId, builder -> builder.auditedSpaceId(spaceId))
                .map(response -> ResourceUtils.getEntity(response).getUsername())))
            .as(StepVerifier::create)
            .expectNext(userName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagersFilterByBillingManagedOrganizationId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                createUserId(this.cloudFoundryClient, this.uaaClient, organizationId, userName),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName),
                Mono.just(organizationId)
            ))
            .delayUntil(function((userId, spaceId, organizationId) -> Mono.zip(
                requestAssociateSpaceManager(this.cloudFoundryClient, spaceId, userId),
                this.cloudFoundryClient.organizations()
                    .associateBillingManager(AssociateOrganizationBillingManagerRequest.builder()
                        .organizationId(organizationId)
                        .billingManagerId(userId)
                        .build())
            )))
            .flatMap(function((userId, spaceId, organizationId) -> Mono.zip(
                Mono.just(userId),
                requestListSpaceManagers(this.cloudFoundryClient, spaceId, builder -> builder.billingManagedOrganizationId(organizationId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagersFilterByManagedOrganizationId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                createUserId(this.cloudFoundryClient, this.uaaClient, organizationId, userName),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName),
                Mono.just(organizationId)
            ))
            .delayUntil(function((userId, spaceId, organizationId) -> Mono.zip(
                requestAssociateSpaceManager(this.cloudFoundryClient, spaceId, userId),
                this.cloudFoundryClient.organizations()
                    .associateManager(AssociateOrganizationManagerRequest.builder()
                        .organizationId(organizationId)
                        .managerId(userId)
                        .build())
            )))
            .flatMap(function((userId, spaceId, organizationId) -> Mono.zip(
                Mono.just(userId),
                requestListSpaceManagers(this.cloudFoundryClient, spaceId, builder -> builder.managedOrganizationId(organizationId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagersFilterByManagedSpaceId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .delayUntil(function((spaceId, userId) -> requestAssociateSpaceManager(this.cloudFoundryClient, spaceId, userId)))
            .flatMapMany(function((spaceId, userId) -> requestListSpaceManagers(this.cloudFoundryClient, spaceId, builder -> builder.managedSpaceId(spaceId))
                .map(response -> ResourceUtils.getEntity(response).getUsername())))
            .as(StepVerifier::create)
            .expectNext(userName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listManagersFilterByOrganizationId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                createUserId(this.cloudFoundryClient, this.uaaClient, organizationId, userName),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName),
                Mono.just(organizationId)
            ))
            .delayUntil(function((userId, spaceId, organizationId) -> requestAssociateSpaceManager(this.cloudFoundryClient, spaceId, userId)))
            .flatMap(function((userId, spaceId, organizationId) -> Mono.zip(
                Mono.just(userId),
                requestListSpaceManagers(this.cloudFoundryClient, spaceId, builder -> builder.organizationId(organizationId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listRoutes() {
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceIdWithDomain(this.cloudFoundryClient, organizationId, spaceName, domainName))
            .flatMap(spaceId -> Mono.zip(
                Mono.just(spaceId),
                createRouteId(this.cloudFoundryClient, spaceId, domainName, hostName, "/test-path")
            ))
            .flatMapMany(function((spaceId, routeId) -> Mono.zip(
                Mono.just(routeId),
                requestListSpaceRoutes(this.cloudFoundryClient, spaceId)
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listRoutesFilterByDomainId() {
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceIdWithDomain(this.cloudFoundryClient, organizationId, spaceName, domainName))
            .flatMap(spaceId -> Mono.zip(
                Mono.just(spaceId),
                getSharedDomainId(this.cloudFoundryClient, domainName),
                createRouteId(this.cloudFoundryClient, spaceId, domainName, hostName, "/test-path")
            ))
            .flatMapMany(function((spaceId, domainId, routeId) -> Mono.zip(
                Mono.just(routeId),
                requestListSpaceRoutes(this.cloudFoundryClient, spaceId, builder -> builder.domainId(domainId))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listRoutesFilterByHost() {
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceIdWithDomain(this.cloudFoundryClient, organizationId, spaceName, domainName))
            .flatMap(spaceId -> Mono.zip(
                Mono.just(spaceId),
                createRouteId(this.cloudFoundryClient, spaceId, domainName, hostName, "/test-path")
            ))
            .flatMapMany(function((spaceId, routeId) -> Mono.zip(
                Mono.just(routeId),
                requestListSpaceRoutes(this.cloudFoundryClient, spaceId, builder -> builder.host(hostName))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listRoutesFilterByPath() {
        String domainName = this.nameFactory.getDomainName();
        String hostName = this.nameFactory.getHostName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceIdWithDomain(this.cloudFoundryClient, organizationId, spaceName, domainName))
            .flatMap(spaceId -> Mono.zip(
                Mono.just(spaceId),
                createRouteId(this.cloudFoundryClient, spaceId, domainName, hostName, "/test-path")
            ))
            .flatMapMany(function((spaceId, routeId) -> Mono.zip(
                Mono.just(routeId),
                requestListSpaceRoutes(this.cloudFoundryClient, spaceId, builder -> builder.path("/test-path"))
                    .single()
                    .map(ResourceUtils::getId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSecurityGroups() {
        String securityGroupName = this.nameFactory.getSecurityGroupName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createSecurityGroupId(this.cloudFoundryClient, securityGroupName),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((securityGroupId, spaceId) -> requestAssociateSpaceSecurityGroup(this.cloudFoundryClient, spaceId, securityGroupId)
                .thenReturn(spaceId)))
            .flatMapMany(spaceId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.spaces()
                    .listSecurityGroups(ListSpaceSecurityGroupsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build())))
            .filter(response -> securityGroupName.equals(response.getEntity().getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSecurityGroupsFilterByName() {
        String securityGroupName = this.nameFactory.getSecurityGroupName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createSecurityGroupId(this.cloudFoundryClient, securityGroupName),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((securityGroupId, spaceId) -> requestAssociateSpaceSecurityGroup(this.cloudFoundryClient, spaceId, securityGroupId)
                .thenReturn(spaceId)))
            .flatMapMany(spaceId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.spaces()
                    .listSecurityGroups(ListSpaceSecurityGroupsRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .name(securityGroupName)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceInstances() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                this.serviceBrokerId,
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((serviceBrokerId, spaceId) -> createServiceInstanceId(this.cloudFoundryClient, serviceBrokerId, serviceInstanceName, this.serviceName, spaceId)
                .thenReturn(spaceId)))
            .flatMapMany(spaceId -> requestListServiceInstances(this.cloudFoundryClient, spaceId))
            .filter(resource -> serviceInstanceName.equals(ResourceUtils.getEntity(resource).getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceInstancesFilterByGatewayName() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                this.serviceBrokerId,
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((serviceBrokerId, spaceId) -> createServiceInstanceId(this.cloudFoundryClient, serviceBrokerId, serviceInstanceName, this.serviceName, spaceId)
                .thenReturn(spaceId)))
            .flatMapMany(spaceId -> requestListServiceInstances(this.cloudFoundryClient, spaceId, builder -> builder.gatewayName("test-gateway-name")))
            .as(StepVerifier::create)
            .expectNextCount(0)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceInstancesFilterByName() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                this.serviceBrokerId,
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((serviceBrokerId, spaceId) -> createServiceInstanceId(this.cloudFoundryClient, serviceBrokerId, serviceInstanceName, this.serviceName, spaceId)
                .thenReturn(spaceId)))
            .flatMapMany(spaceId -> requestListServiceInstances(this.cloudFoundryClient, spaceId, builder -> builder.name(serviceInstanceName)))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceInstancesFilterByServiceBindingId() {
        String applicationName = this.nameFactory.getApplicationName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                this.serviceBrokerId,
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((serviceBrokerId, spaceId) -> Mono.zip(
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName),
                createServiceInstanceId(this.cloudFoundryClient, serviceBrokerId, serviceInstanceName, this.serviceName, spaceId),
                Mono.just(spaceId)
            )))
            .flatMap(function((applicationId, serviceInstanceId, spaceId) -> Mono.zip(
                createServiceBindingId(this.cloudFoundryClient, applicationId, serviceInstanceId),
                Mono.just(spaceId)
            )))
            .flatMapMany(function((serviceBindingId, spaceId) -> requestListServiceInstances(this.cloudFoundryClient, spaceId, builder -> builder.serviceBindingId(serviceBindingId))))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceInstancesFilterByServiceKeyId() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String serviceKeyName = this.nameFactory.getServiceKeyName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                this.serviceBrokerId,
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((serviceBrokerId, spaceId) -> Mono.zip(
                createServiceInstanceId(this.cloudFoundryClient, serviceBrokerId, serviceInstanceName, this.serviceName, spaceId),
                Mono.just(spaceId)
            )))
            .flatMap(function((serviceInstanceId, spaceId) -> Mono.zip(
                createServiceKeyId(this.cloudFoundryClient, serviceInstanceId, serviceKeyName),
                Mono.just(spaceId)
            )))
            .flatMapMany(function((serviceKeyId, spaceId) -> requestListServiceInstances(this.cloudFoundryClient, spaceId, builder -> builder.serviceKeyId(serviceKeyId))))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServiceInstancesFilterByServicePlanId() {
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                this.serviceBrokerId,
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((serviceBrokerId, spaceId) -> Mono.zip(
                createServiceInstanceId(this.cloudFoundryClient, serviceBrokerId, serviceInstanceName, this.serviceName, spaceId),
                getServicePlanId(this.cloudFoundryClient, serviceBrokerId, this.serviceName),
                Mono.just(spaceId)
            )))
            .flatMapMany(function((ignore, servicePlanId, spaceId) -> requestListServiceInstances(this.cloudFoundryClient, spaceId, builder -> builder.servicePlanId(servicePlanId))))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServices() {
        this.spaceId
            .flatMapMany(spaceId -> requestListSpaceServices(this.cloudFoundryClient, spaceId)
                .filter(resource -> this.serviceName.equals(ResourceUtils.getEntity(resource).getLabel())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServicesFilterByActive() {
        this.spaceId
            .flatMapMany(spaceId -> requestListSpaceServices(this.cloudFoundryClient, spaceId)
                .filter(resource -> this.serviceName.equals(ResourceUtils.getEntity(resource).getLabel())))
            .map(response -> response.getEntity().getLabel())
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listServicesFilterByLabel() {
        Mono
            .zip(this.serviceBrokerId, this.spaceId)
            .flatMapMany(function((serviceBrokerId, spaceId) -> requestListSpaceServices(this.cloudFoundryClient, spaceId, builder -> builder.label(this.serviceName))
                .filter(resource -> serviceBrokerId.equals(ResourceUtils.getEntity(resource).getServiceBrokerId()))))
            .map(response -> response.getEntity().getLabel())
            .as(StepVerifier::create)
            .expectNext(this.serviceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Await https://github.com/cloudfoundry/cloud_controller_ng/issues/856 for this test to work
    @Ignore("Await https://github.com/cloudfoundry/cloud_controller_ng/issues/856 for this test to work")
    @Test
    public void listServicesFilterByServiceBrokerId() {
        Mono
            .zip(this.serviceBrokerId, this.spaceId)
            .flatMapMany(function((serviceBrokerId, spaceId) -> requestListSpaceServices(this.cloudFoundryClient, spaceId, builder -> builder.serviceBrokerId(serviceBrokerId))))
            .map(response -> response.getEntity().getLabel())
            .as(StepVerifier::create)
            .expectNext(this.serviceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listUserRoles() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .delayUntil(function((spaceId, userId) -> requestAssociateSpaceManager(this.cloudFoundryClient, spaceId, userId)))
            .flatMapMany(function((spaceId, userId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.spaces()
                    .listUserRoles(ListSpaceUserRolesRequest.builder()
                        .page(page)
                        .spaceId(spaceId)
                        .build()))
                .map(ResourceUtils::getEntity)))
            .as(StepVerifier::create)
            .consumeNextWith(entity -> {
                assertThat(entity.getUsername()).isEqualTo(userName);
                assertThat(entity.getSpaceRoles()).containsExactly("space_manager");
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeAuditor() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .delayUntil(function((spaceId, userId) -> requestAssociateSpaceAuditor(this.cloudFoundryClient, spaceId, userId)))
            .delayUntil(function((spaceId, userId) -> this.cloudFoundryClient.spaces()
                .removeAuditor(RemoveSpaceAuditorRequest.builder()
                    .spaceId(spaceId)
                    .auditorId(userId)
                    .build())))
            .flatMapMany(function((spaceId, userId) -> requestListSpaceAuditors(this.cloudFoundryClient, spaceId)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeAuditorByUsername() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .delayUntil(function((spaceId, userId) -> requestAssociateSpaceAuditor(this.cloudFoundryClient, spaceId, userId)))
            .delayUntil(function((spaceId, userId) -> this.cloudFoundryClient.spaces()
                .removeAuditorByUsername(RemoveSpaceAuditorByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(userName)
                    .build())))
            .flatMapMany(function((spaceId, userId) -> requestListSpaceAuditors(this.cloudFoundryClient, spaceId)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeDeveloper() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .delayUntil(function((spaceId, userId) -> requestAssociateSpaceDeveloper(this.cloudFoundryClient, userId, spaceId)))
            .delayUntil(function((spaceId, userId) -> this.cloudFoundryClient.spaces()
                .removeDeveloper(RemoveSpaceDeveloperRequest.builder()
                    .spaceId(spaceId)
                    .developerId(userId)
                    .build())))
            .flatMapMany(function((spaceId, userId) -> requestListSpaceDevelopers(this.cloudFoundryClient, spaceId)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeDeveloperByUsername() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .delayUntil(function((spaceId, userId) -> requestAssociateSpaceDeveloper(this.cloudFoundryClient, userId, spaceId)))
            .delayUntil(function((spaceId, userId) -> this.cloudFoundryClient.spaces()
                .removeDeveloperByUsername(RemoveSpaceDeveloperByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(userName)
                    .build())))
            .flatMapMany(function((spaceId, userId) -> requestListSpaceDevelopers(this.cloudFoundryClient, spaceId)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeManager() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .delayUntil(function((spaceId, userId) -> requestAssociateSpaceManager(this.cloudFoundryClient, spaceId, userId)))
            .delayUntil(function((spaceId, userId) -> this.cloudFoundryClient.spaces()
                .removeManager(RemoveSpaceManagerRequest.builder()
                    .spaceId(spaceId)
                    .managerId(userId)
                    .build())))
            .flatMapMany(function((spaceId, userId) -> requestListSpaceManagers(this.cloudFoundryClient, spaceId)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeManagerByUsername() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .delayUntil(function((spaceId, userId) -> requestAssociateSpaceManager(this.cloudFoundryClient, spaceId, userId)))
            .delayUntil(function((spaceId, userId) -> this.cloudFoundryClient.spaces()
                .removeManagerByUsername(RemoveSpaceManagerByUsernameRequest.builder()
                    .spaceId(spaceId)
                    .username(userName)
                    .build())))
            .flatMapMany(function((spaceId, userId) -> requestListSpaceManagers(this.cloudFoundryClient, spaceId)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeSecurityGroup() {
        String securityGroupName = this.nameFactory.getSecurityGroupName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                createSecurityGroupId(this.cloudFoundryClient, securityGroupName),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .delayUntil(function((securityGroupId, spaceId) -> requestAssociateSpaceSecurityGroup(this.cloudFoundryClient, spaceId, securityGroupId)))
            .flatMap(function((securityGroupId, spaceId) -> this.cloudFoundryClient.spaces()
                .removeSecurityGroup(RemoveSpaceSecurityGroupRequest.builder()
                    .securityGroupId(securityGroupId)
                    .spaceId(spaceId)
                    .build())
                .thenReturn(spaceId)))
            .flatMapMany(spaceId -> requestListSecurityGroups(this.cloudFoundryClient, spaceId))
            .filter(response -> securityGroupName.equals(response.getEntity().getName()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String spaceName = this.nameFactory.getSpaceName();
        String spaceName2 = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> createSpaceId(this.cloudFoundryClient, organizationId, spaceName))
            .delayUntil(spaceId -> this.cloudFoundryClient.spaces()
                .update(UpdateSpaceRequest.builder()
                    .spaceId(spaceId)
                    .name(spaceName2)
                    .build()))
            .flatMap(spaceId -> this.cloudFoundryClient.spaces()
                .getSummary(GetSpaceSummaryRequest.builder()
                    .spaceId(spaceId)
                    .build()))
            .map(GetSpaceSummaryResponse::getName)
            .as(StepVerifier::create)
            .expectNext(spaceName2)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void updateEmptyManagers() {
        String organizationName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();
        String userName = this.nameFactory.getUserName();

        createSpaceIdAndUserId(this.cloudFoundryClient, this.uaaClient, organizationName, spaceName, userName)
            .delayUntil(function((spaceId, userId) -> requestAssociateSpaceManager(this.cloudFoundryClient, spaceId, userId)))
            .delayUntil(function((spaceId, userId) -> this.cloudFoundryClient.spaces()
                .update(UpdateSpaceRequest.builder()
                    .spaceId(spaceId)
                    .managerIds(Collections.emptyList())
                    .build())))
            .flatMapMany(function((spaceId, userId) -> requestListSpaceManagers(this.cloudFoundryClient, spaceId)
                .map(response -> ResourceUtils.getEntity(response).getUsername())))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName, String stackId) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName, stackId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createOrganizationId(CloudFoundryClient cloudFoundryClient, String organization) {
        return requestCreateOrganization(cloudFoundryClient, organization)
            .map(ResourceUtils::getId);
    }

    private static Mono<Tuple2<String, String>> createOrganizationIdAndSpaceId(CloudFoundryClient cloudFoundryClient, String organizationName, String spaceName) {
        return createOrganizationId(cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                Mono.just(organizationId),
                createSpaceId(cloudFoundryClient, organizationId, spaceName)));
    }

    private static Mono<String> createRouteId(CloudFoundryClient cloudFoundryClient, String spaceId, String domainName, String host, String path) {
        return getSharedDomainId(cloudFoundryClient, domainName)
            .flatMap(domainId -> requestCreateRoute(cloudFoundryClient, spaceId, domainId, path, host))
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createSecurityGroupId(CloudFoundryClient cloudFoundryClient, String securityGroupName) {
        return requestCreateSecurityGroup(cloudFoundryClient, securityGroupName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createServiceBindingId(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId) {
        return requestCreateServiceBinding(cloudFoundryClient, applicationId, serviceInstanceId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createServiceInstanceId(CloudFoundryClient cloudFoundryClient, String serviceBrokerId, String serviceInstanceName, String serviceName, String spaceId) {
        return getServicePlanId(cloudFoundryClient, serviceBrokerId, serviceName)
            .flatMap(planId -> requestCreateServiceInstance(cloudFoundryClient, planId, serviceInstanceName, spaceId))
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createServiceKeyId(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String serviceKeyName) {
        return requestCreateServiceKey(cloudFoundryClient, serviceInstanceId, serviceKeyName)
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

    private static Mono<Tuple2<String, String>> createSpaceIdAndUserId(CloudFoundryClient cloudFoundryClient, UaaClient uaaClient, String organizationName, String spaceName, String userName) {
        return createOrganizationId(cloudFoundryClient, organizationName)
            .flatMap(organizationId -> Mono.zip(
                createSpaceId(cloudFoundryClient, organizationId, spaceName),
                createUserId(cloudFoundryClient, uaaClient, organizationId, userName)
            ));
    }

    private static Mono<String> createSpaceIdWithDomain(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName, String domainName) {
        return cloudFoundryClient.sharedDomains()
            .create(CreateSharedDomainRequest.builder()
                .name(domainName)
                .build())
            .map(ResourceUtils::getId)
            .flatMap(domainId -> cloudFoundryClient.spaces()
                .create(CreateSpaceRequest.builder()
                    .domainId(domainId)
                    .organizationId(organizationId)
                    .name(spaceName)
                    .build()))
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createUserId(CloudFoundryClient cloudFoundryClient, UaaClient uaaClient, String organizationId, String username) {
        return uaaClient.users()
            .create(org.cloudfoundry.uaa.users.CreateUserRequest.builder()
                .email(Email.builder()
                    .primary(true)
                    .value(String.format("%s@%s.com", username, username))
                    .build())
                .name(Name.builder()
                    .givenName("Test")
                    .familyName("User")
                    .build())
                .password("test-password")
                .userName(username)
                .build())
            .map(CreateUserResponse::getId)
            .flatMap(uaaId -> cloudFoundryClient.users()
                .create(CreateUserRequest.builder()
                    .uaaId(uaaId)
                    .build())
                .map(ResourceUtils::getId))
            .flatMap(userId -> cloudFoundryClient.organizations()
                .associateUser(AssociateOrganizationUserRequest.builder()
                    .organizationId(organizationId)
                    .userId(userId)
                    .build())
                .thenReturn(userId));
    }

    private static String getPastTimestamp() {
        Date past = Date.from(Instant.now().minus(61, MINUTES));
        return DateUtils.formatToIso8601(past);
    }

    private static Mono<String> getServicePlanId(CloudFoundryClient cloudFoundryClient, String serviceBrokerId, String serviceName) {
        return requestListServices(cloudFoundryClient, serviceBrokerId, serviceName)
            .single()
            .map(ResourceUtils::getId)
            .flatMapMany(serviceId -> requestListServicePlans(cloudFoundryClient, serviceId))
            .single()
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getSharedDomainId(CloudFoundryClient cloudFoundryClient, String domain) {
        return requestSharedDomain(cloudFoundryClient, domain)
            .map(ResourceUtils::getId)
            .single()
            .switchIfEmpty(ExceptionUtils.illegalArgument("Domain %s not found", domain));
    }

    private static Mono<AssociateSpaceAuditorResponse> requestAssociateSpaceAuditor(CloudFoundryClient cloudFoundryClient, String spaceId, String userId) {
        return cloudFoundryClient.spaces()
            .associateAuditor(AssociateSpaceAuditorRequest.builder()
                .spaceId(spaceId)
                .auditorId(userId)
                .build());
    }

    private static Mono<AssociateSpaceDeveloperResponse> requestAssociateSpaceDeveloper(CloudFoundryClient cloudFoundryClient, String developerId, String spaceId) {
        return cloudFoundryClient.spaces()
            .associateDeveloper(AssociateSpaceDeveloperRequest.builder()
                .developerId(developerId)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<AssociateSpaceManagerResponse> requestAssociateSpaceManager(CloudFoundryClient cloudFoundryClient, String spaceId, String userId) {
        return cloudFoundryClient.spaces()
            .associateManager(AssociateSpaceManagerRequest.builder()
                .spaceId(spaceId)
                .managerId(userId)
                .build());
    }

    private static Mono<AssociateSpaceSecurityGroupResponse> requestAssociateSpaceSecurityGroup(CloudFoundryClient cloudFoundryClient, String spaceId, String securityGroupId) {
        return cloudFoundryClient.spaces()
            .associateSecurityGroup(AssociateSpaceSecurityGroupRequest.builder()
                .securityGroupId(securityGroupId)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .diego(true)
                .name(applicationName)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName, String stackId) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .diego(true)
                .name(applicationName)
                .spaceId(spaceId)
                .stackId(stackId)
                .build());
    }

    private static Mono<CreateOrganizationResponse> requestCreateOrganization(CloudFoundryClient cloudFoundryClient, String organization) {
        return cloudFoundryClient.organizations()
            .create(CreateOrganizationRequest.builder()
                .name(organization)
                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(CloudFoundryClient cloudFoundryClient, String spaceId, String domainId, String path, String host) {
        return cloudFoundryClient.routes()
            .create(CreateRouteRequest.builder()
                .spaceId(spaceId)
                .domainId(domainId)
                .host(host)
                .path(path)
                .build());
    }

    private static Mono<CreateSecurityGroupResponse> requestCreateSecurityGroup(CloudFoundryClient cloudFoundryClient, String securityGroupName) {
        return cloudFoundryClient.securityGroups()
            .create(CreateSecurityGroupRequest.builder()
                .name(securityGroupName)
                .build());
    }

    private static Mono<CreateServiceBindingResponse> requestCreateServiceBinding(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId) {
        return cloudFoundryClient.serviceBindingsV2()
            .create(CreateServiceBindingRequest.builder()
                .applicationId(applicationId)
                .serviceInstanceId(serviceInstanceId)
                .build());
    }

    private static Mono<CreateServiceInstanceResponse> requestCreateServiceInstance(CloudFoundryClient cloudFoundryClient, String planId, String serviceInstanceName, String spaceId) {
        return cloudFoundryClient.serviceInstances()
            .create(CreateServiceInstanceRequest.builder()
                .name(serviceInstanceName)
                .parameter("test-key", "test-value")
                .servicePlanId(planId)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateServiceKeyResponse> requestCreateServiceKey(CloudFoundryClient cloudFoundryClient, String serviceInstanceId, String serviceKeyName) {
        return cloudFoundryClient.serviceKeys()
            .create(CreateServiceKeyRequest.builder()
                .name(serviceKeyName)
                .serviceInstanceId(serviceInstanceId)
                .build());
    }

    private static Flux<SecurityGroupResource> requestListSecurityGroups(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listSecurityGroups(ListSpaceSecurityGroupsRequest.builder()
                    .page(page)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Flux<UnionServiceInstanceResource> requestListServiceInstances(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestListServiceInstances(cloudFoundryClient, spaceId, UnaryOperator.identity());
    }

    private static Flux<UnionServiceInstanceResource> requestListServiceInstances(CloudFoundryClient cloudFoundryClient, String spaceId,
                                                                                  UnaryOperator<ListSpaceServiceInstancesRequest.Builder> transformer) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listServiceInstances(transformer.apply(ListSpaceServiceInstancesRequest.builder())
                    .page(page)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Flux<ServicePlanResource> requestListServicePlans(CloudFoundryClient cloudFoundryClient, String serviceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.servicePlans()
                .list(ListServicePlansRequest.builder()
                    .page(page)
                    .serviceId(serviceId)
                    .build()));
    }

    private static Flux<ServiceResource> requestListServices(CloudFoundryClient cloudFoundryClient, String serviceBrokerId, String serviceName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.services()
                .list(ListServicesRequest.builder()
                    .label(serviceName)
                    .page(page)
                    .serviceBrokerId(serviceBrokerId)
                    .build()));
    }

    private static Flux<ApplicationResource> requestListSpaceApplications(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestListSpaceApplications(cloudFoundryClient, spaceId, UnaryOperator.identity());
    }

    private static Flux<ApplicationResource> requestListSpaceApplications(CloudFoundryClient cloudFoundryClient, String spaceId, UnaryOperator<ListSpaceApplicationsRequest.Builder> transformer) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listApplications(transformer.apply(ListSpaceApplicationsRequest.builder())
                    .page(page)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Flux<UserResource> requestListSpaceAuditors(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listAuditors(ListSpaceAuditorsRequest.builder()
                    .page(page)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Flux<UserResource> requestListSpaceDevelopers(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listDevelopers(ListSpaceDevelopersRequest.builder()
                    .page(page)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Flux<DomainResource> requestListSpaceDomains(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestListSpaceDomains(cloudFoundryClient, spaceId, UnaryOperator.identity());
    }

    @SuppressWarnings("deprecation")
    private static Flux<DomainResource> requestListSpaceDomains(CloudFoundryClient cloudFoundryClient, String spaceId, UnaryOperator<ListSpaceDomainsRequest.Builder> transformer) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listDomains(transformer.apply(ListSpaceDomainsRequest.builder())
                    .page(page)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Flux<EventResource> requestListSpaceEvents(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestListSpaceEvents(cloudFoundryClient, spaceId, UnaryOperator.identity());
    }

    private static Flux<EventResource> requestListSpaceEvents(CloudFoundryClient cloudFoundryClient, String spaceId, UnaryOperator<ListSpaceEventsRequest.Builder> transformer) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listEvents(transformer.apply(ListSpaceEventsRequest.builder())
                    .page(page)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Flux<UserResource> requestListSpaceManagers(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestListSpaceManagers(cloudFoundryClient, spaceId, Function.identity());
    }

    private static Flux<UserResource> requestListSpaceManagers(CloudFoundryClient cloudFoundryClient, String spaceId, Function<ListSpaceManagersRequest.Builder, ListSpaceManagersRequest.Builder>
        transformer) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listManagers(transformer.apply(ListSpaceManagersRequest.builder())
                    .page(page)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Flux<RouteResource> requestListSpaceRoutes(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestListSpaceRoutes(cloudFoundryClient, spaceId, UnaryOperator.identity());
    }

    private static Flux<RouteResource> requestListSpaceRoutes(CloudFoundryClient cloudFoundryClient, String spaceId, UnaryOperator<ListSpaceRoutesRequest.Builder> transformer) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listRoutes(transformer.apply(ListSpaceRoutesRequest.builder())
                    .spaceId(spaceId)
                    .build()));
    }

    private static Flux<ServiceResource> requestListSpaceServices(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestListSpaceServices(cloudFoundryClient, organizationId, UnaryOperator.identity());
    }

    private static Flux<ServiceResource> requestListSpaceServices(CloudFoundryClient cloudFoundryClient, String spaceId,
                                                                  UnaryOperator<ListSpaceServicesRequest.Builder> transformer) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listServices(transformer.apply(ListSpaceServicesRequest.builder())
                    .spaceId(spaceId)
                    .page(page)
                    .build()));
    }

    private static Flux<SpaceResource> requestListSpaces(CloudFoundryClient cloudFoundryClient) {
        return requestListSpaces(cloudFoundryClient, UnaryOperator.identity());
    }

    private static Flux<SpaceResource> requestListSpaces(CloudFoundryClient cloudFoundryClient, UnaryOperator<ListSpacesRequest.Builder> transformer) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .list(transformer.apply(ListSpacesRequest.builder())
                    .page(page)
                    .build()));
    }

    private static Flux<SharedDomainResource> requestSharedDomain(CloudFoundryClient cloudFoundryClient, String domain) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.sharedDomains()
                .list(ListSharedDomainsRequest.builder()
                    .name(domain)
                    .page(page)
                    .build()));
    }

}

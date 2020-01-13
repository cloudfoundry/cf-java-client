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

package org.cloudfoundry.operations.spaces;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.ClientV2Exception;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.jobs.ErrorDetails;
import org.cloudfoundry.client.v2.jobs.GetJobRequest;
import org.cloudfoundry.client.v2.jobs.GetJobResponse;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainEntity;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.securitygroups.RuleEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupResource;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsResponse;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainEntity;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.spacequotadefinitions.GetSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.GetSpaceQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperByUsernameResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerByUsernameRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerByUsernameResponse;
import org.cloudfoundry.client.v2.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v2.spaces.DeleteSpaceResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceSecurityGroupsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceSecurityGroupsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceServicesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServicesResponse;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.spaces.UpdateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.UpdateSpaceResponse;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.cloudfoundry.operations.spaceadmin.SpaceQuota;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DefaultSpacesTest extends AbstractOperationsTest {

    private final DefaultSpaces spaces = new DefaultSpaces(Mono.just(this.cloudFoundryClient), Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_USERNAME));

    @Test
    public void allowSsh() {
        requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", "test-space-spaceQuotaDefinitionId");
        requestUpdateSpaceSsh(this.cloudFoundryClient, "test-space-id", true);

        this.spaces
            .allowSsh(AllowSpaceSshRequest.builder()
                .name("test-space-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void allowSshAlreadyAllowed() {
        requestOrganizationSpacesWithSsh(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", true);

        this.spaces
            .allowSsh(AllowSpaceSshRequest.builder()
                .name("test-space-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void allowSshNoSpace() {
        requestOrganizationSpacesEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name");

        this.spaces
            .allowSsh(AllowSpaceSshRequest.builder()
                .name("test-space-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Space test-space-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createNoOrgNoQuota() {
        requestCreateSpace(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", null, "test-space-id");
        requestAssociateOrganizationUserByUsername(this.cloudFoundryClient, TEST_ORGANIZATION_ID, TEST_USERNAME);
        requestAssociateSpaceManagerByUsername(this.cloudFoundryClient, "test-space-id", TEST_USERNAME);
        requestAssociateSpaceDeveloperByUsername(this.cloudFoundryClient, "test-space-id", TEST_USERNAME);

        this.spaces
            .create(CreateSpaceRequest.builder()
                .name("test-space-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createNoOrgQuota() {
        requestOrganizationSpaceQuotas(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-quota", "test-space-quota-id");
        requestCreateSpace(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", "test-space-quota-id", "test-space-id");
        requestAssociateOrganizationUserByUsername(this.cloudFoundryClient, TEST_ORGANIZATION_ID, TEST_USERNAME);
        requestAssociateSpaceManagerByUsername(this.cloudFoundryClient, "test-space-id", TEST_USERNAME);
        requestAssociateSpaceDeveloperByUsername(this.cloudFoundryClient, "test-space-id", TEST_USERNAME);

        this.spaces
            .create(CreateSpaceRequest.builder()
                .name("test-space-name")
                .spaceQuota("test-space-quota")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createNoOrgQuotaNotFound() {
        requestOrganizationSpaceQuotas(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-quota", null);

        this.spaces
            .create(CreateSpaceRequest.builder()
                .name("test-space-name")
                .spaceQuota("test-space-quota")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Space quota definition test-space-quota does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createOrgNotFound() {
        requestOrganizations(this.cloudFoundryClient, "test-other-organization", null);

        this.spaces
            .create(CreateSpaceRequest.builder()
                .name("test-space-name")
                .spaceQuota("test-space-quota")
                .organization("test-other-organization")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Organization test-other-organization does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createOrgQuota() {
        requestOrganizations(this.cloudFoundryClient, "test-other-organization", "test-other-organization-id");
        requestOrganizationSpaceQuotas(this.cloudFoundryClient, "test-other-organization-id", "test-space-quota", "test-space-quota-id");
        requestCreateSpace(this.cloudFoundryClient, "test-other-organization-id", "test-space-name", "test-space-quota-id", "test-space-id");
        requestAssociateOrganizationUserByUsername(this.cloudFoundryClient, "test-other-organization-id", TEST_USERNAME);
        requestAssociateSpaceManagerByUsername(this.cloudFoundryClient, "test-space-id", TEST_USERNAME);
        requestAssociateSpaceDeveloperByUsername(this.cloudFoundryClient, "test-space-id", TEST_USERNAME);

        this.spaces
            .create(CreateSpaceRequest.builder()
                .name("test-space-name")
                .organization("test-other-organization")
                .spaceQuota("test-space-quota")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", "test-space-spaceQuotaDefinitionId");
        requestDeleteSpace(this.cloudFoundryClient, "test-space-id");
        requestJobSuccess(this.cloudFoundryClient, "test-job-entity-id");

        StepVerifier.withVirtualTime(() -> this.spaces
            .delete(DeleteSpaceRequest.builder()
                .name("test-space-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteFailure() {
        requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", "test-space-spaceQuotaDefinitionId");
        requestDeleteSpace(this.cloudFoundryClient, "test-space-id");
        requestJobFailure(this.cloudFoundryClient, "test-job-entity-id");

        StepVerifier.withVirtualTime(() -> this.spaces
            .delete(DeleteSpaceRequest.builder()
                .name("test-space-name")
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessage("test-error-details-errorCode(1): test-error-details-description"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteInvalidSpace() {
        requestOrganizationSpacesEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name");

        this.spaces
            .delete(DeleteSpaceRequest.builder()
                .name("test-space-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Space test-space-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void disallowSsh() {
        requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", "test-space-spaceQuotaDefinitionId");
        requestUpdateSpaceSsh(this.cloudFoundryClient, "test-space-id", false);

        this.spaces
            .disallowSsh(DisallowSpaceSshRequest.builder()
                .name("test-space-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void disallowSshAlreadyDisallowed() {
        requestOrganizationSpacesWithSsh(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", false);

        this.spaces
            .disallowSsh(DisallowSpaceSshRequest.builder()
                .name("test-space-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void disallowSshNoSpace() {
        requestOrganizationSpacesEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name");

        this.spaces
            .disallowSsh(DisallowSpaceSshRequest.builder()
                .name("test-space-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Space test-space-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        requestOrganization(this.cloudFoundryClient, "test-space-organizationId");
        requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, TEST_SPACE_NAME, "test-space-spaceQuotaDefinitionId");
        requestSpaceApplications(this.cloudFoundryClient, TEST_SPACE_ID);
        requestPrivateDomains(this.cloudFoundryClient, "test-space-organizationId");
        requestSharedDomains(this.cloudFoundryClient);
        requestSpaceSecurityGroups(this.cloudFoundryClient, TEST_SPACE_ID);
        requestSpaceServices(this.cloudFoundryClient, TEST_SPACE_ID);
        requestSpaceQuotaDefinition(this.cloudFoundryClient, "test-space-spaceQuotaDefinitionId");

        this.spaces
            .get(GetSpaceRequest.builder()
                .name("test-space-name")
                .securityGroupRules(true)
                .build())
            .as(StepVerifier::create)
            .expectNext(SpaceDetail.builder()
                .application("test-application-name")
                .domains("test-private-domain-name", "test-shared-domain-name")
                .id(TEST_SPACE_ID)
                .name(TEST_SPACE_NAME)
                .organization("test-organization-name")
                .securityGroup(SecurityGroup.builder()
                    .name("test-security-group-name")
                    .rule(fill(Rule.builder(), "security-group-")
                        .build())
                    .build())
                .service("test-service-label")
                .spaceQuota(Optional
                    .of(fill(SpaceQuota.builder(), "space-quota-definition-")
                        .build()))
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getNoSecurityGroupRules() {
        requestOrganization(this.cloudFoundryClient, "test-space-organizationId");
        requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, TEST_SPACE_NAME, "test-space-spaceQuotaDefinitionId");
        requestSpaceApplications(this.cloudFoundryClient, TEST_SPACE_ID);
        requestPrivateDomains(this.cloudFoundryClient, "test-space-organizationId");
        requestSharedDomains(this.cloudFoundryClient);
        requestSpaceSecurityGroups(this.cloudFoundryClient, TEST_SPACE_ID);
        requestSpaceServices(this.cloudFoundryClient, TEST_SPACE_ID);
        requestSpaceQuotaDefinition(this.cloudFoundryClient, "test-space-spaceQuotaDefinitionId");

        this.spaces
            .get(GetSpaceRequest.builder()
                .name(TEST_SPACE_NAME)
                .build())
            .as(StepVerifier::create)
            .expectNext(SpaceDetail.builder()
                .application("test-application-name")
                .domains("test-private-domain-name", "test-shared-domain-name")
                .id(TEST_SPACE_ID)
                .name(TEST_SPACE_NAME)
                .organization("test-organization-name")
                .securityGroup(SecurityGroup.builder()
                    .name("test-security-group-name")
                    .build())
                .service("test-service-label")
                .spaceQuota(Optional
                    .of(fill(SpaceQuota.builder(), "space-quota-definition-")
                        .build()))
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getSpaceQuotaNull() {
        requestOrganization(this.cloudFoundryClient, "test-space-organizationId");
        requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, TEST_SPACE_NAME, null);
        requestSpaceApplications(this.cloudFoundryClient, TEST_SPACE_ID);
        requestPrivateDomains(this.cloudFoundryClient, "test-space-organizationId");
        requestSharedDomains(this.cloudFoundryClient);
        requestSpaceSecurityGroups(this.cloudFoundryClient, TEST_SPACE_ID);
        requestSpaceServices(this.cloudFoundryClient, TEST_SPACE_ID);

        this.spaces
            .get(GetSpaceRequest.builder()
                .name(TEST_SPACE_NAME)
                .securityGroupRules(false)
                .build())
            .as(StepVerifier::create)
            .expectNext(SpaceDetail.builder()
                .application("test-application-name")
                .domains("test-private-domain-name", "test-shared-domain-name")
                .id(TEST_SPACE_ID)
                .name(TEST_SPACE_NAME)
                .organization("test-organization-name")
                .securityGroup(SecurityGroup.builder()
                    .name("test-security-group-name")
                    .build())
                .service("test-service-label")
                .spaceQuota(Optional.empty())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        requestSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID);

        this.spaces
            .list()
            .as(StepVerifier::create)
            .expectNext(fill(SpaceSummary.builder(), "space-")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void rename() {
        requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", "test-space-spaceQuotaDefinitionId");
        requestUpdateSpace(this.cloudFoundryClient, "test-space-id", "test-new-space-name");

        this.spaces
            .rename(RenameSpaceRequest.builder()
                .name("test-space-name")
                .newName("test-new-space-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void renameNoSpace() {
        requestOrganizationSpacesEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name");

        this.spaces
            .rename(RenameSpaceRequest.builder()
                .name("test-space-name")
                .newName("test-new-space-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Space test-space-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void sshAllowed() {
        requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", "test-space-spaceQuotaDefinitionId");

        this.spaces
            .sshAllowed(SpaceSshAllowedRequest.builder()
                .name("test-space-name")
                .build())
            .as(StepVerifier::create)
            .expectNext(true)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void sshAllowedNoSpace() {
        requestOrganizationSpacesEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name");

        this.spaces
            .sshAllowed(SpaceSshAllowedRequest.builder()
                .name("test-space-name")
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Space test-space-name does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    private static void requestAssociateOrganizationUserByUsername(CloudFoundryClient cloudFoundryClient, String organizationId, String username) {
        when(cloudFoundryClient.organizations()
            .associateUserByUsername(AssociateOrganizationUserByUsernameRequest.builder()
                .organizationId(organizationId)
                .username(username)
                .build()))
            .thenReturn(Mono
                .just(fill(AssociateOrganizationUserByUsernameResponse.builder(), "associate-user-")
                    .build()));
    }

    private static void requestAssociateSpaceDeveloperByUsername(CloudFoundryClient cloudFoundryClient, String spaceId, String username) {
        when(cloudFoundryClient.spaces()
            .associateDeveloperByUsername(AssociateSpaceDeveloperByUsernameRequest.builder()
                .spaceId(spaceId)
                .username(username)
                .build()))
            .thenReturn(Mono
                .just(fill(AssociateSpaceDeveloperByUsernameResponse.builder(), "associate-developer-")
                    .build()));
    }

    private static void requestAssociateSpaceManagerByUsername(CloudFoundryClient cloudFoundryClient, String spaceId, String username) {
        when(cloudFoundryClient.spaces()
            .associateManagerByUsername(AssociateSpaceManagerByUsernameRequest.builder()
                .spaceId(spaceId)
                .username(username)
                .build()))
            .thenReturn(Mono
                .just(fill(AssociateSpaceManagerByUsernameResponse.builder(), "associate-manager-")
                    .build()));
    }

    private static void requestCreateSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String space, String spaceQuotaId, String spaceId) {
        when(cloudFoundryClient.spaces()
            .create(org.cloudfoundry.client.v2.spaces.CreateSpaceRequest.builder()
                .name(space)
                .organizationId(organizationId)
                .spaceQuotaDefinitionId(spaceQuotaId)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateSpaceResponse.builder())
                    .metadata(fill(Metadata.builder())
                        .id(spaceId)
                        .build())
                    .build()));
    }

    private static void requestDeleteSpace(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .delete(org.cloudfoundry.client.v2.spaces.DeleteSpaceRequest.builder()
                .async(true)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(DeleteSpaceResponse.builder())
                    .entity(fill(JobEntity.builder(), "job-entity-")
                        .build())
                    .build()));
    }

    private static void requestJobFailure(CloudFoundryClient cloudFoundryClient, String jobId) {
        when(cloudFoundryClient.jobs()
            .get(GetJobRequest.builder()
                .jobId(jobId)
                .build()))
            .thenReturn(Mono
                .defer(new Supplier<Mono<GetJobResponse>>() {

                    private final Queue<GetJobResponse> responses = new LinkedList<>(Arrays.asList(
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("running")
                                .build())
                            .build(),
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .errorDetails(fill(ErrorDetails.builder(), "error-details-")
                                    .build())
                                .status("failed")
                                .build())
                            .build()
                    ));

                    @Override
                    public Mono<GetJobResponse> get() {
                        return Mono.just(this.responses.poll());
                    }

                }));
    }

    private static void requestJobSuccess(CloudFoundryClient cloudFoundryClient, String jobId) {
        when(cloudFoundryClient.jobs()
            .get(GetJobRequest.builder()
                .jobId(jobId)
                .build()))
            .thenReturn(Mono
                .defer(new Supplier<Mono<GetJobResponse>>() {

                    private final Queue<GetJobResponse> responses = new LinkedList<>(Arrays.asList(
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("running")
                                .build())
                            .build(),
                        fill(GetJobResponse.builder(), "job-")
                            .entity(fill(JobEntity.builder())
                                .status("finished")
                                .build())
                            .build()
                    ));

                    @Override
                    public Mono<GetJobResponse> get() {
                        return Mono.just(this.responses.poll());
                    }

                }));
    }

    private static void requestOrganization(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.organizations()
            .get(GetOrganizationRequest.builder()
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetOrganizationResponse.builder(), "organization-")
                    .build()));
    }

    private static void requestOrganizationSpaceQuotas(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceQuota, String spaceQuotaId) {
        ListOrganizationSpaceQuotaDefinitionsResponse.Builder responseBuilder = fill(ListOrganizationSpaceQuotaDefinitionsResponse.builder());

        if (spaceQuotaId != null) {
            responseBuilder
                .resource(SpaceQuotaDefinitionResource.builder()
                    .metadata(fill(Metadata.builder())
                        .id(spaceQuotaId)
                        .build())
                    .entity(SpaceQuotaDefinitionEntity.builder()
                        .name(spaceQuota)
                        .build())
                    .build());
        }

        when(cloudFoundryClient.organizations()
            .listSpaceQuotaDefinitions(ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                .organizationId(organizationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(responseBuilder.build()));
    }

    private static void requestOrganizationSpaces(CloudFoundryClient cloudFoundryClient, String organizationId, String space, String spaceQuotaDefinitionId) {
        when(cloudFoundryClient.organizations()
            .listSpaces(ListOrganizationSpacesRequest.builder()
                .name(space)
                .organizationId(organizationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationSpacesResponse.builder())
                    .resource(fill(SpaceResource.builder(), "space-")
                        .entity(fill(SpaceEntity.builder(), "space-")
                            .spaceQuotaDefinitionId(spaceQuotaDefinitionId)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestOrganizationSpacesEmpty(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        when(cloudFoundryClient.organizations()
            .listSpaces(ListOrganizationSpacesRequest.builder()
                .name(space)
                .organizationId(organizationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationSpacesResponse.builder())
                    .build()));
    }

    private static void requestOrganizationSpacesWithSsh(CloudFoundryClient cloudFoundryClient, String organizationId, String space, Boolean allowSsh) {
        when(cloudFoundryClient.organizations()
            .listSpaces(ListOrganizationSpacesRequest.builder()
                .name(space)
                .organizationId(organizationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationSpacesResponse.builder())
                    .resource(fill(SpaceResource.builder(), "space-")
                        .entity(fill(SpaceEntity.builder(), "space-entity-")
                            .allowSsh(allowSsh)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestOrganizations(CloudFoundryClient cloudFoundryClient, String organization, String organizationId) {
        ListOrganizationsResponse.Builder responseBuilder = fill(ListOrganizationsResponse.builder(), "organization-");

        if (organizationId != null) {
            responseBuilder
                .resource(fill(OrganizationResource.builder())
                    .metadata(fill(Metadata.builder())
                        .id(organizationId)
                        .build())
                    .build());
        }

        when(cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .name(organization)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(responseBuilder
                    .build()));
    }

    private static void requestPrivateDomains(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.organizations()
            .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                .organizationId(organizationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationPrivateDomainsResponse.builder())
                    .resource(fill(PrivateDomainResource.builder())
                        .entity(fill(PrivateDomainEntity.builder())
                            .name("test-private-domain-name")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestSharedDomains(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.sharedDomains()
            .list(ListSharedDomainsRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(ListSharedDomainsResponse.builder()
                    .resource(fill(SharedDomainResource.builder())
                        .entity(fill(SharedDomainEntity.builder())
                            .name("test-shared-domain-name")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestSpaceApplications(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listApplications(ListSpaceApplicationsRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder(), "application-")
                        .build())
                    .build()));
    }

    private static void requestSpaceQuotaDefinition(CloudFoundryClient cloudFoundryClient, String spaceQuotaDefinitionId) {
        when(cloudFoundryClient.spaceQuotaDefinitions()
            .get(GetSpaceQuotaDefinitionRequest.builder()
                .spaceQuotaDefinitionId(spaceQuotaDefinitionId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetSpaceQuotaDefinitionResponse.builder(), "space-quota-definition-")
                    .build()));

    }

    private static void requestSpaceSecurityGroups(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listSecurityGroups(ListSpaceSecurityGroupsRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceSecurityGroupsResponse.builder())
                    .resource(fill(SecurityGroupResource.builder(), "security-group-")
                        .entity(fill(SecurityGroupEntity.builder(), "security-group-")
                            .rule(fill(RuleEntity.builder(), "security-group-")
                                .build())
                            .build())
                        .build())
                    .build()));
    }

    private static void requestSpaceServices(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServices(ListSpaceServicesRequest.builder()
                .page(1)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpaceServicesResponse.builder())
                    .resource(fill(ServiceResource.builder(), "service-")
                        .build())
                    .build()));
    }

    private static void requestSpaces(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.spaces()
            .list(ListSpacesRequest.builder()
                .organizationId(organizationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSpacesResponse.builder())
                    .resource(fill(SpaceResource.builder(), "space-")
                        .build())
                    .build()));
    }

    private static void requestUpdateSpace(CloudFoundryClient cloudFoundryClient, String spaceId, String newName) {
        when(cloudFoundryClient.spaces()
            .update(UpdateSpaceRequest.builder()
                .name(newName)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono.empty());
    }

    private static void requestUpdateSpaceSsh(CloudFoundryClient cloudFoundryClient, String spaceId, Boolean allowed) {
        when(cloudFoundryClient.spaces()
            .update(UpdateSpaceRequest.builder()
                .allowSsh(allowed)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fill(UpdateSpaceResponse.builder())
                    .entity(fill(SpaceEntity.builder(), "space-entity-")
                        .build())
                    .build()));
    }

}

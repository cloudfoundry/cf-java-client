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

package org.cloudfoundry.operations.organizations;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.ClientV2Exception;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagRequest;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagResponse;
import org.cloudfoundry.client.v2.jobs.ErrorDetails;
import org.cloudfoundry.client.v2.jobs.GetJobRequest;
import org.cloudfoundry.client.v2.jobs.GetJobResponse;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.organizationquotadefinitions.GetOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.GetOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitionResource;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.DeleteOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationEntity;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationRequest;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainEntity;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsResponse;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainEntity;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.cloudfoundry.operations.spaceadmin.SpaceQuota;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DefaultOrganizationsTest extends AbstractOperationsTest {

    private final DefaultOrganizations organizations = new DefaultOrganizations(Mono.just(this.cloudFoundryClient), Mono.just(TEST_USERNAME));

    @Test
    public void create() {
        requestCreateOrganization(this.cloudFoundryClient, TEST_ORGANIZATION_NAME, null);
        requestGetFeatureFlagEnabled(this.cloudFoundryClient, "set_roles_by_username");
        requestAssociateOrganizationManagerByUsername(this.cloudFoundryClient, TEST_USERNAME);
        requestAssociateOrganizationUserByUsername(this.cloudFoundryClient, TEST_USERNAME);

        this.organizations
            .create(CreateOrganizationRequest.builder()
                .organizationName(TEST_ORGANIZATION_NAME)
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createSetRolesByUsernameDisabled() {
        requestCreateOrganization(this.cloudFoundryClient, TEST_ORGANIZATION_NAME, null);
        requestGetFeatureFlagDisabled(this.cloudFoundryClient, "set_roles_by_username");

        this.organizations
            .create(CreateOrganizationRequest.builder()
                .organizationName(TEST_ORGANIZATION_NAME)
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createWithQuota() {
        requestOrganizationQuotaDefinitions(this.cloudFoundryClient, "test-quota-definition-name");
        requestCreateOrganization(this.cloudFoundryClient, TEST_ORGANIZATION_NAME, "test-organization-quota-definition-id");
        requestGetFeatureFlagEnabled(this.cloudFoundryClient, "set_roles_by_username");
        requestAssociateOrganizationManagerByUsername(this.cloudFoundryClient, TEST_USERNAME);
        requestAssociateOrganizationUserByUsername(this.cloudFoundryClient, TEST_USERNAME);

        this.organizations.create(CreateOrganizationRequest.builder()
            .organizationName(TEST_ORGANIZATION_NAME)
            .quotaDefinitionName("test-quota-definition-name")
            .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        requestOrganizations(this.cloudFoundryClient, TEST_ORGANIZATION_NAME);
        requestDeleteOrganization(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestJobSuccess(this.cloudFoundryClient, "test-id");

        StepVerifier.withVirtualTime(() -> this.organizations
            .delete(DeleteOrganizationRequest.builder()
                .name(TEST_ORGANIZATION_NAME)
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteFailure() {
        requestOrganizations(this.cloudFoundryClient, TEST_ORGANIZATION_NAME);
        requestDeleteOrganization(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestJobFailure(this.cloudFoundryClient, "test-id");

        StepVerifier.withVirtualTime(() -> this.organizations
            .delete(DeleteOrganizationRequest.builder()
                .name(TEST_ORGANIZATION_NAME)
                .build()))
            .then(() -> VirtualTimeScheduler.get().advanceTimeBy(Duration.ofSeconds(3)))
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessage("test-error-details-errorCode(1): test-error-details-description"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void info() {
        requestOrganizations(this.cloudFoundryClient, TEST_ORGANIZATION_NAME);
        requestPrivateDomains(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSharedDomains(this.cloudFoundryClient);
        requestOrganizationQuotaDefinition(this.cloudFoundryClient, "test-organization-entity-quotaDefinitionId");
        requestSpaceQuotaDefinitions(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        requestSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID);

        this.organizations
            .get(OrganizationInfoRequest.builder()
                .name(TEST_ORGANIZATION_NAME)
                .build())
            .as(StepVerifier::create)
            .expectNext(fill(OrganizationDetail.builder())
                .domains("test-private-domain-name", "test-shared-domain-name")
                .id(TEST_ORGANIZATION_ID)
                .name(TEST_ORGANIZATION_NAME)
                .quota(fill(OrganizationQuota.builder())
                    .organizationId(TEST_ORGANIZATION_ID)
                    .build())
                .space("test-name")
                .spaceQuota(fill(SpaceQuota.builder())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        requestOrganizations(this.cloudFoundryClient);

        this.organizations
            .list()
            .as(StepVerifier::create)
            .expectNext(fill(OrganizationSummary.builder(), "organization-")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void rename() {
        requestOrganizations(this.cloudFoundryClient, TEST_ORGANIZATION_NAME);
        requestUpdateOrganization(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-new-organization-name");

        this.organizations
            .rename(RenameOrganizationRequest.builder()
                .name(TEST_ORGANIZATION_NAME)
                .newName("test-new-organization-name")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    private static void requestAssociateOrganizationManagerByUsername(CloudFoundryClient cloudFoundryClient, String username) {
        when(cloudFoundryClient.organizations()
            .associateManagerByUsername(AssociateOrganizationManagerByUsernameRequest.builder()
                .organizationId("test-id")
                .username(username)
                .build()))
            .thenReturn(Mono
                .just(fill(AssociateOrganizationManagerByUsernameResponse.builder())
                    .build()));
    }

    private static void requestAssociateOrganizationUserByUsername(CloudFoundryClient cloudFoundryClient, String username) {
        when(cloudFoundryClient.organizations()
            .associateUserByUsername(AssociateOrganizationUserByUsernameRequest.builder()
                .organizationId("test-id")
                .username(username)
                .build()))
            .thenReturn(Mono
                .just(fill(AssociateOrganizationUserByUsernameResponse.builder())
                    .build()));
    }

    private static void requestCreateOrganization(CloudFoundryClient cloudFoundryClient, String organization, String organizationQuotaDefinitionId) {
        when(cloudFoundryClient.organizations()
            .create(org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest.builder()
                .name(organization)
                .quotaDefinitionId(organizationQuotaDefinitionId)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateOrganizationResponse.builder())
                    .build()));
    }

    private static void requestDeleteOrganization(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.organizations()
            .delete(org.cloudfoundry.client.v2.organizations.DeleteOrganizationRequest.builder()
                .organizationId(organizationId)
                .async(true)
                .build()))
            .thenReturn(Mono
                .just(fill(DeleteOrganizationResponse.builder())
                    .build()));
    }

    private static void requestGetFeatureFlagDisabled(CloudFoundryClient cloudFoundryClient, String featureFlag) {
        when(cloudFoundryClient.featureFlags()
            .get(GetFeatureFlagRequest.builder()
                .name(featureFlag)
                .build()))
            .thenReturn(Mono.just(GetFeatureFlagResponse.builder()
                .enabled(false)
                .build()));
    }

    private static void requestGetFeatureFlagEnabled(CloudFoundryClient cloudFoundryClient, String featureFlag) {
        when(cloudFoundryClient.featureFlags()
            .get(GetFeatureFlagRequest.builder()
                .name(featureFlag)
                .build()))
            .thenReturn(Mono.just(GetFeatureFlagResponse.builder()
                .enabled(true)
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
                        GetJobResponse.builder()
                            .metadata(fill(Metadata.builder()).build())
                            .entity(fill(JobEntity.builder())
                                .status("running")
                                .build())
                            .build(),
                        GetJobResponse.builder()
                            .metadata(fill(Metadata.builder()).build())
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
                        GetJobResponse.builder()
                            .metadata(fill(Metadata.builder()).build())
                            .entity(fill(JobEntity.builder())
                                .status("running")
                                .build())
                            .build(),
                        GetJobResponse.builder()
                            .metadata(fill(Metadata.builder()).build())
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

    private static void requestOrganizationQuotaDefinition(CloudFoundryClient cloudFoundryClient, String quotaDefinitionId) {
        when(cloudFoundryClient.organizationQuotaDefinitions()
            .get(GetOrganizationQuotaDefinitionRequest.builder()
                .organizationQuotaDefinitionId(quotaDefinitionId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetOrganizationQuotaDefinitionResponse.builder())
                    .entity(fill(OrganizationQuotaDefinitionEntity.builder())
                        .build())
                    .build()));
    }

    private static void requestOrganizationQuotaDefinitions(CloudFoundryClient cloudFoundryClient, String organizationQuotaDefinition) {
        when(cloudFoundryClient.organizationQuotaDefinitions()
            .list(ListOrganizationQuotaDefinitionsRequest.builder()
                .name(organizationQuotaDefinition)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationQuotaDefinitionsResponse.builder())
                    .resource(fill(OrganizationQuotaDefinitionResource.builder(), "organization-quota-definition-")
                        .entity(fill(OrganizationQuotaDefinitionEntity.builder(), "organization-quota-definition-entity-")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestOrganizations(CloudFoundryClient cloudFoundryClient, String organizationName) {
        when(cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .name(organizationName)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationsResponse.builder())
                    .resource(fill(OrganizationResource.builder(), "organization-")
                        .entity(fill(OrganizationEntity.builder(), "organization-entity-")
                            .build())
                        .build())
                    .build()));

    }

    private static void requestOrganizations(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationsResponse.builder())
                    .resource(fill(OrganizationResource.builder(), "organization-")
                        .build())
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

    private static void requestSpaceQuotaDefinitions(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.organizations()
            .listSpaceQuotaDefinitions(ListOrganizationSpaceQuotaDefinitionsRequest.builder()
                .organizationId(organizationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationSpaceQuotaDefinitionsResponse.builder())
                    .resource(fill(SpaceQuotaDefinitionResource.builder())
                        .build())
                    .build()));
    }

    private static void requestSpaces(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.organizations()
            .listSpaces(ListOrganizationSpacesRequest.builder()
                .organizationId(organizationId)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationSpacesResponse.builder())
                    .resource(fill(SpaceResource.builder())
                        .build())
                    .build()));
    }

    private static void requestUpdateOrganization(CloudFoundryClient cloudFoundryClient, String organizationId, String newName) {
        when(cloudFoundryClient.organizations()
            .update(UpdateOrganizationRequest.builder()
                .name(newName)
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono.empty());
    }

}

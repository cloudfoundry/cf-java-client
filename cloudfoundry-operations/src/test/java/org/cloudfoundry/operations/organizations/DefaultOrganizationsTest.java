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

package org.cloudfoundry.operations.organizations;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagRequest;
import org.cloudfoundry.client.v2.featureflags.GetFeatureFlagResponse;
import org.cloudfoundry.client.v2.job.GetJobRequest;
import org.cloudfoundry.client.v2.job.GetJobResponse;
import org.cloudfoundry.client.v2.job.JobEntity;
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
import org.cloudfoundry.client.v2.organizations.ListOrganizationDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationDomainsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationEntity;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.operations.spacequotas.SpaceQuota;
import org.cloudfoundry.util.RequestValidationException;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.cloudfoundry.util.test.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultOrganizationsTest {

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

    private static void requestDomains(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.organizations()
            .listDomains(fillPage(ListOrganizationDomainsRequest.builder())
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationDomainsResponse.builder())
                    .resource(fill(DomainResource.builder())
                        .build())
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
                            .metadata(fill(Resource.Metadata.builder()).build())
                            .entity(fill(JobEntity.builder())
                                .status("running")
                                .build())
                            .build(),
                        GetJobResponse.builder()
                            .metadata(fill(Resource.Metadata.builder()).build())
                            .entity(fill(JobEntity.builder())
                                .errorDetails(fill(JobEntity.ErrorDetails.builder(), "error-details-")
                                    .build())
                                .status("failed")
                                .build())
                            .build()
                    ));

                    @Override
                    public Mono<GetJobResponse> get() {
                        return Mono.just(responses.poll());
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
                            .metadata(fill(Resource.Metadata.builder()).build())
                            .entity(fill(JobEntity.builder())
                                .status("running")
                                .build())
                            .build(),
                        GetJobResponse.builder()
                            .metadata(fill(Resource.Metadata.builder()).build())
                            .entity(fill(JobEntity.builder())
                                .status("finished")
                                .build())
                            .build()
                    ));

                    @Override
                    public Mono<GetJobResponse> get() {
                        return Mono.just(responses.poll());
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
            .list(fillPage(ListOrganizationQuotaDefinitionsRequest.builder())
                .name(organizationQuotaDefinition)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationQuotaDefinitionsResponse.builder())
                    .resource(fill(OrganizationQuotaDefinitionResource.builder(), "organization-quota-definition-")
                        .entity(fill(OrganizationQuotaDefinitionEntity.builder(), "organization-quota-definition-entity-")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestOrganizations(CloudFoundryClient cloudFoundryClient, String organizationName) {
        when(cloudFoundryClient.organizations()
            .list(fillPage(ListOrganizationsRequest.builder())
                .name(organizationName)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationsResponse.builder())
                    .resource(fill(OrganizationResource.builder(), "organization-")
                        .entity(fill(OrganizationEntity.builder(), "organization-entity-")
                            .build())
                        .build())
                    .build()));

    }

    private static void requestOrganizations(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.organizations()
            .list(fillPage(ListOrganizationsRequest.builder())
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationsResponse.builder())
                    .resource(fill(OrganizationResource.builder(), "organization-")
                        .build())
                    .build()));
    }

    private static void requestSpaceQuotaDefinitions(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.organizations()
            .listSpaceQuotaDefinitions(fillPage(ListOrganizationSpaceQuotaDefinitionsRequest.builder())
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationSpaceQuotaDefinitionsResponse.builder())
                    .resource(fill(SpaceQuotaDefinitionResource.builder())
                        .build())
                    .build()));
    }

    private static void requestSpaces(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.organizations()
            .listSpaces(fillPage(ListOrganizationSpacesRequest.builder())
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationSpacesResponse.builder())
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
            .thenReturn(Mono
                .<UpdateOrganizationResponse>empty());
    }

    public static final class Create extends AbstractOperationsApiTest<Void> {

        private final DefaultOrganizations organizations = new DefaultOrganizations(this.cloudFoundryClient, Mono.just(TEST_USERNAME));

        @Before
        public void setUp() throws Exception {
            requestCreateOrganization(this.cloudFoundryClient, TEST_ORGANIZATION_NAME, null);
            requestGetFeatureFlagEnabled(this.cloudFoundryClient, "set_roles_by_username");
            requestAssociateOrganizationManagerByUsername(this.cloudFoundryClient, TEST_USERNAME);
            requestAssociateOrganizationUserByUsername(this.cloudFoundryClient, TEST_USERNAME);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.organizations
                .create(CreateOrganizationRequest.builder()
                    .organizationName(TEST_ORGANIZATION_NAME)
                    .build());
        }

    }

    public static final class CreateInvalid extends AbstractOperationsApiTest<Void> {

        private final DefaultOrganizations organizations = new DefaultOrganizations(this.cloudFoundryClient, Mono.just(TEST_USERNAME));

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(RequestValidationException.class, "Request is invalid: organization name must be specified");
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.organizations.
                create(CreateOrganizationRequest.builder()
                    .build());
        }

    }

    public static final class CreateSetRolesByUsernameDisabled extends AbstractOperationsApiTest<Void> {

        private final DefaultOrganizations organizations = new DefaultOrganizations(this.cloudFoundryClient, Mono.just(TEST_USERNAME));

        @Before
        public void setUp() throws Exception {
            requestCreateOrganization(this.cloudFoundryClient, TEST_ORGANIZATION_NAME, null);
            requestGetFeatureFlagDisabled(this.cloudFoundryClient, "set_roles_by_username");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.organizations
                .create(CreateOrganizationRequest.builder()
                    .organizationName(TEST_ORGANIZATION_NAME)
                    .build());
        }

    }

    public static final class CreateWithQuota extends AbstractOperationsApiTest<Void> {

        private final DefaultOrganizations organizations = new DefaultOrganizations(this.cloudFoundryClient, Mono.just(TEST_USERNAME));

        @Before
        public void setUp() throws Exception {
            requestOrganizationQuotaDefinitions(this.cloudFoundryClient, "test-quota-definition-name");
            requestCreateOrganization(this.cloudFoundryClient, TEST_ORGANIZATION_NAME, "test-organization-quota-definition-id");
            requestGetFeatureFlagEnabled(this.cloudFoundryClient, "set_roles_by_username");
            requestAssociateOrganizationManagerByUsername(this.cloudFoundryClient, TEST_USERNAME);
            requestAssociateOrganizationUserByUsername(this.cloudFoundryClient, TEST_USERNAME);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.organizations.create(CreateOrganizationRequest.builder()
                .organizationName(TEST_ORGANIZATION_NAME)
                .quotaDefinitionName("test-quota-definition-name")
                .build());
        }

    }

    public static final class Delete extends AbstractOperationsApiTest<Void> {

        private final DefaultOrganizations organizations = new DefaultOrganizations(this.cloudFoundryClient, Mono.just(TEST_USERNAME));

        @Before
        public void setUp() throws Exception {
            requestOrganizations(this.cloudFoundryClient, "test-organization-name");
            requestDeleteOrganization(this.cloudFoundryClient, "test-organization-id");
            requestJobSuccess(this.cloudFoundryClient, "test-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // onComplete and no onNext
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.organizations
                .delete(DeleteOrganizationRequest.builder()
                    .name("test-organization-name")
                    .build());
        }

    }

    public static final class DeleteFailure extends AbstractOperationsApiTest<Void> {

        private final DefaultOrganizations organizations = new DefaultOrganizations(this.cloudFoundryClient, Mono.just(TEST_USERNAME));

        @Before
        public void setUp() throws Exception {
            requestOrganizations(this.cloudFoundryClient, "test-organization-name");
            requestDeleteOrganization(this.cloudFoundryClient, "test-organization-id");
            requestJobFailure(this.cloudFoundryClient, "test-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(CloudFoundryException.class, "test-error-details-errorCode(1): test-error-details-description");
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.organizations
                .delete(DeleteOrganizationRequest.builder()
                    .name("test-organization-name")
                    .build());
        }

    }

    public static final class Info extends AbstractOperationsApiTest<OrganizationDetail> {

        private final DefaultOrganizations organizations = new DefaultOrganizations(this.cloudFoundryClient, Mono.just(TEST_USERNAME));

        @Before
        public void setUp() throws Exception {
            requestOrganizations(this.cloudFoundryClient, "test-organization-name");
            requestDomains(this.cloudFoundryClient, "test-organization-id");
            requestOrganizationQuotaDefinition(this.cloudFoundryClient, "test-organization-entity-quotaDefinitionId");
            requestSpaceQuotaDefinitions(this.cloudFoundryClient, "test-organization-id");
            requestSpaces(this.cloudFoundryClient, "test-organization-id");
        }

        @Override
        protected void assertions(TestSubscriber<OrganizationDetail> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(fill(OrganizationDetail.builder())
                    .domain("test-name")
                    .id("test-organization-id")
                    .name("test-organization-name")
                    .quota(fill(OrganizationQuota.builder())
                        .organizationId("test-organization-id")
                        .build())
                    .space("test-name")
                    .spacesQuota(fill(SpaceQuota.builder())
                        .build())
                    .build());
        }

        @Override
        protected Publisher<OrganizationDetail> invoke() {
            return this.organizations
                .get(OrganizationInfoRequest.builder()
                    .name("test-organization-name")
                    .build());
        }

    }

    public static final class List extends AbstractOperationsApiTest<OrganizationSummary> {

        private final DefaultOrganizations organizations = new DefaultOrganizations(this.cloudFoundryClient, Mono.just(TEST_USERNAME));

        @Before
        public void setUp() throws Exception {
            requestOrganizations(this.cloudFoundryClient);
        }

        @Override
        protected void assertions(TestSubscriber<OrganizationSummary> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(fill(OrganizationSummary.builder(), "organization-")
                    .build());
        }

        @Override
        protected Publisher<OrganizationSummary> invoke() {
            return this.organizations
                .list();
        }

    }

    public static final class Rename extends AbstractOperationsApiTest<Void> {

        private final DefaultOrganizations organizations = new DefaultOrganizations(this.cloudFoundryClient, Mono.just(TEST_USERNAME));

        @Before
        public void setUp() throws Exception {
            requestOrganizations(this.cloudFoundryClient, "test-organization-name");
            requestUpdateOrganization(this.cloudFoundryClient, "test-organization-id", "test-new-organization-name");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.organizations
                .rename(RenameOrganizationRequest.builder()
                    .name("test-organization-name")
                    .newName("test-new-organization-name")
                    .build());
        }

    }

    public static final class RenameInvalid extends AbstractOperationsApiTest<Void> {

        private final DefaultOrganizations organizations = new DefaultOrganizations(this.cloudFoundryClient, Mono.just(TEST_USERNAME));

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(RequestValidationException.class, "Request is invalid: name must be specified, new name must be specified");
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.organizations
                .rename(RenameOrganizationRequest.builder()
                    .build());
        }

    }

}

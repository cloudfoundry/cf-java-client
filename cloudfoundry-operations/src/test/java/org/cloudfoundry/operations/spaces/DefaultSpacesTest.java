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

package org.cloudfoundry.operations.spaces;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.job.GetJobRequest;
import org.cloudfoundry.client.v2.job.GetJobResponse;
import org.cloudfoundry.client.v2.job.JobEntity;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupResource;
import org.cloudfoundry.client.v2.services.ServiceResource;
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
import org.cloudfoundry.client.v2.spaces.ListSpaceDomainsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceDomainsResponse;
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
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.operations.spacequotas.SpaceQuota;
import org.cloudfoundry.util.RequestValidationException;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Supplier;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.cloudfoundry.util.test.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultSpacesTest {

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
                    .metadata(Resource.Metadata.builder()
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
                        return Mono.just(responses.poll());
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
        final ListOrganizationSpaceQuotaDefinitionsResponse.ListOrganizationSpaceQuotaDefinitionsResponseBuilder responseBuilder =
            fillPage(ListOrganizationSpaceQuotaDefinitionsResponse.builder());

        if (spaceQuotaId != null) {
            responseBuilder
                .resource(SpaceQuotaDefinitionResource.builder()
                    .metadata(Resource.Metadata.builder()
                        .id(spaceQuotaId)
                        .build())
                    .entity(SpaceQuotaDefinitionEntity.builder()
                        .name(spaceQuota)
                        .build())
                    .build());
        }

        when(cloudFoundryClient.organizations()
            .listSpaceQuotaDefinitions(fillPage(ListOrganizationSpaceQuotaDefinitionsRequest.builder())
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(responseBuilder.build()));
    }

    private static void requestOrganizationSpaces(CloudFoundryClient cloudFoundryClient, String organizationId, String space, String spaceQuotaDefinitionId) {
        when(cloudFoundryClient.organizations()
            .listSpaces(fillPage(ListOrganizationSpacesRequest.builder())
                .name(space)
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationSpacesResponse.builder())
                    .resource(fill(SpaceResource.builder(), "space-")
                        .entity(fill(SpaceEntity.builder(), "space-")
                            .spaceQuotaDefinitionId(spaceQuotaDefinitionId)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestOrganizationSpacesEmpty(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        when(cloudFoundryClient.organizations()
            .listSpaces(fillPage(ListOrganizationSpacesRequest.builder())
                .name(space)
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationSpacesResponse.builder())
                    .build()));
    }

    private static void requestOrganizationSpacesWithSsh(CloudFoundryClient cloudFoundryClient, String organizationId, String space, Boolean allowSsh) {
        when(cloudFoundryClient.organizations()
            .listSpaces(fillPage(ListOrganizationSpacesRequest.builder())
                .name(space)
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationSpacesResponse.builder())
                    .resource(fill(SpaceResource.builder(), "space-")
                        .entity(fill(SpaceEntity.builder(), "space-entity-")
                            .allowSsh(allowSsh)
                            .build())
                        .build())
                    .build()));
    }

    private static void requestOrganizations(CloudFoundryClient cloudFoundryClient, String organization, String organizationId) {
        final ListOrganizationsResponse.ListOrganizationsResponseBuilder responseBuilder = fillPage(ListOrganizationsResponse.builder(), "organization-");

        if (organizationId != null) {
            responseBuilder
                .resource(OrganizationResource.builder()
                    .metadata(Resource.Metadata.builder()
                        .id(organizationId)
                        .build())
                    .build());
        }

        when(cloudFoundryClient.organizations()
            .list(fillPage(ListOrganizationsRequest.builder())
                .name(organization)
                .build()))
            .thenReturn(Mono
                .just(responseBuilder
                    .build()));
    }

    private static void requestSpaceApplications(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listApplications(fillPage(ListSpaceApplicationsRequest.builder(), "application-")
                .diego(null)
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceApplicationsResponse.builder())
                    .resource(fill(ApplicationResource.builder(), "application-")
                        .build())
                    .build()));
    }

    private static void requestSpaceDomains(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listDomains(fillPage(ListSpaceDomainsRequest.builder())
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceDomainsResponse.builder())
                    .resource(fill(DomainResource.builder(), "domain-")
                        .build())
                    .build()));
    }

    private static void requestSpaceQuotaDefinition(CloudFoundryClient cloudFoundryClient, String spaceQuotaDefinitionId) {
        when(cloudFoundryClient.spaceQuotaDefinitions()
            .get(fill(GetSpaceQuotaDefinitionRequest.builder())
                .spaceQuotaDefinitionId(spaceQuotaDefinitionId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetSpaceQuotaDefinitionResponse.builder(), "space-quota-definition-")
                    .build()));

    }

    private static void requestSpaceSecurityGroups(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listSecurityGroups(fillPage(ListSpaceSecurityGroupsRequest.builder())
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceSecurityGroupsResponse.builder())
                    .resource(fill(SecurityGroupResource.builder(), "security-group-")
                        .entity(fill(SecurityGroupEntity.builder(), "security-group-")
                            .build())
                        .build())
                    .build()));
    }

    private static void requestSpaceServices(CloudFoundryClient cloudFoundryClient, String spaceId) {
        when(cloudFoundryClient.spaces()
            .listServices(fillPage(ListSpaceServicesRequest.builder())
                .spaceId(spaceId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpaceServicesResponse.builder())
                    .resource(fill(ServiceResource.builder(), "service-")
                        .build())
                    .build()));
    }

    private static void requestSpaces(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.spaces()
            .list(fillPage(ListSpacesRequest.builder())
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSpacesResponse.builder())
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
            .thenReturn(Mono
                .<UpdateSpaceResponse>empty());
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

    public static final class AllowSsh extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_USERNAME);

        @Before
        public void setUp() throws Exception {
            requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", "test-space-spaceQuotaDefinitionId");
            requestUpdateSpaceSsh(this.cloudFoundryClient, "test-space-id", true);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .allowSsh(AllowSpaceSshRequest.builder()
                    .name("test-space-name")
                    .build());
        }

    }

    public static final class AllowSshAlreadyAllowed extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_USERNAME);

        @Before
        public void setUp() throws Exception {
            requestOrganizationSpacesWithSsh(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", true);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .allowSsh(AllowSpaceSshRequest.builder()
                    .name("test-space-name")
                    .build());
        }

    }

    public static final class AllowSshNoSpace extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_USERNAME);

        @Before
        public void setUp() throws Exception {
            requestOrganizationSpacesEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Space test-space-name does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .allowSsh(AllowSpaceSshRequest.builder()
                    .name("test-space-name")
                    .build());
        }

    }

    public static final class CreateInvalid extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, MISSING_ORGANIZATION_ID, MISSING_USERNAME);

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(RequestValidationException.class, "Request is invalid: name must be specified");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .create(CreateSpaceRequest.builder()
                    .organization("test-organization")
                    .spaceQuota("test-space-quota")
                    .build());
        }

    }

    public static final class CreateNoOrgNoQuota extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_USERNAME));

        @Before
        public void setUp() {
            requestCreateSpace(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", null, "test-space-id");
            requestAssociateOrganizationUserByUsername(this.cloudFoundryClient, TEST_ORGANIZATION_ID, TEST_USERNAME);
            requestAssociateSpaceManagerByUsername(this.cloudFoundryClient, "test-space-id", TEST_USERNAME);
            requestAssociateSpaceDeveloperByUsername(this.cloudFoundryClient, "test-space-id", TEST_USERNAME);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .create(CreateSpaceRequest.builder()
                    .name("test-space-name")
                    .build());
        }

    }

    public static final class CreateNoOrgQuota extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_USERNAME));

        @Before
        public void setUp() {
            requestOrganizationSpaceQuotas(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-quota", "test-space-quota-id");
            requestCreateSpace(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", "test-space-quota-id", "test-space-id");
            requestAssociateOrganizationUserByUsername(this.cloudFoundryClient, TEST_ORGANIZATION_ID, TEST_USERNAME);
            requestAssociateSpaceManagerByUsername(this.cloudFoundryClient, "test-space-id", TEST_USERNAME);
            requestAssociateSpaceDeveloperByUsername(this.cloudFoundryClient, "test-space-id", TEST_USERNAME);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .create(CreateSpaceRequest.builder()
                    .name("test-space-name")
                    .spaceQuota("test-space-quota")
                    .build());
        }

    }

    public static final class CreateNoOrgQuotaNotFound extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_USERNAME));

        @Before
        public void setUp() {
            requestOrganizationSpaceQuotas(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-quota", null);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Space quota definition test-space-quota does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .create(CreateSpaceRequest.builder()
                    .name("test-space-name")
                    .spaceQuota("test-space-quota")
                    .build());
        }

    }

    public static final class CreateOrgNotFound extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_USERNAME));

        @Before
        public void setUp() {
            requestOrganizations(this.cloudFoundryClient, "test-other-organization", null);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Organization test-other-organization does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .create(CreateSpaceRequest.builder()
                    .name("test-space-name")
                    .spaceQuota("test-space-quota")
                    .organization("test-other-organization")
                    .build());
        }

    }

    public static final class CreateOrgQuota extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), Mono.just(TEST_USERNAME));

        @Before
        public void setUp() {
            requestOrganizations(this.cloudFoundryClient, "test-other-organization", "test-other-organization-id");
            requestOrganizationSpaceQuotas(this.cloudFoundryClient, "test-other-organization-id", "test-space-quota", "test-space-quota-id");
            requestCreateSpace(this.cloudFoundryClient, "test-other-organization-id", "test-space-name", "test-space-quota-id", "test-space-id");
            requestAssociateOrganizationUserByUsername(this.cloudFoundryClient, "test-other-organization-id", TEST_USERNAME);
            requestAssociateSpaceManagerByUsername(this.cloudFoundryClient, "test-space-id", TEST_USERNAME);
            requestAssociateSpaceDeveloperByUsername(this.cloudFoundryClient, "test-space-id", TEST_USERNAME);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .create(CreateSpaceRequest.builder()
                    .name("test-space-name")
                    .organization("test-other-organization")
                    .spaceQuota("test-space-quota")
                    .build());
        }

    }

    public static final class Delete extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_USERNAME);

        @Before
        public void setUp() {
            requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", "test-space-spaceQuotaDefinitionId");
            requestDeleteSpace(this.cloudFoundryClient, "test-space-id");
            requestJobSuccess(this.cloudFoundryClient, "test-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .delete(DeleteSpaceRequest.builder()
                    .name("test-space-name")
                    .build());
        }

    }

    public static final class DeleteFailure extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_USERNAME);

        @Before
        public void setUp() {
            requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", "test-space-spaceQuotaDefinitionId");
            requestDeleteSpace(this.cloudFoundryClient, "test-space-id");
            requestJobFailure(this.cloudFoundryClient, "test-id");
        }


        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(CloudFoundryException.class, "test-error-details-errorCode(1): test-error-details-description");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .delete(DeleteSpaceRequest.builder()
                    .name("test-space-name")
                    .build());
        }

    }

    public static final class DeleteInvalidRequest extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, MISSING_ORGANIZATION_ID, MISSING_USERNAME);

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(RequestValidationException.class, "Request is invalid: name must be specified");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .delete(DeleteSpaceRequest.builder()
                    .build());
        }

    }

    public static final class DeleteInvalidSpace extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_USERNAME);

        @Before
        public void setUp() {
            requestOrganizationSpacesEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Space test-space-name does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .delete(DeleteSpaceRequest.builder()
                    .name("test-space-name")
                    .build());
        }

    }

    public static final class DeleteNoOrganization extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, MISSING_ORGANIZATION_ID, MISSING_USERNAME);

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_ORGANIZATION_ID");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .delete(DeleteSpaceRequest.builder()
                    .name("test-space-name")
                    .build());
        }

    }

    public static final class DisallowSsh extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_USERNAME);

        @Before
        public void setUp() throws Exception {
            requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", "test-space-spaceQuotaDefinitionId");
            requestUpdateSpaceSsh(this.cloudFoundryClient, "test-space-id", false);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .disallowSsh(DisallowSpaceSshRequest.builder()
                    .name("test-space-name")
                    .build());
        }

    }

    public static final class DisallowSshAlreadyDisallowed extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_USERNAME);

        @Before
        public void setUp() throws Exception {
            requestOrganizationSpacesWithSsh(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", false);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .disallowSsh(DisallowSpaceSshRequest.builder()
                    .name("test-space-name")
                    .build());
        }

    }

    public static final class DisallowSshNoSpace extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_USERNAME);

        @Before
        public void setUp() throws Exception {
            requestOrganizationSpacesEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Space test-space-name does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .disallowSsh(DisallowSpaceSshRequest.builder()
                    .name("test-space-name")
                    .build());
        }

    }

    public static final class Get extends AbstractOperationsApiTest<SpaceDetail> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_USERNAME);

        @Before
        public void setUp() throws Exception {
            requestOrganization(this.cloudFoundryClient, "test-space-organizationId");
            requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, TEST_SPACE_NAME, "test-space-spaceQuotaDefinitionId");
            requestSpaceApplications(this.cloudFoundryClient, TEST_SPACE_ID);
            requestSpaceDomains(this.cloudFoundryClient, TEST_SPACE_ID);
            requestSpaceSecurityGroups(this.cloudFoundryClient, TEST_SPACE_ID);
            requestSpaceServices(this.cloudFoundryClient, TEST_SPACE_ID);
            requestSpaceQuotaDefinition(this.cloudFoundryClient, "test-space-spaceQuotaDefinitionId");
        }

        @Override
        protected void assertions(TestSubscriber<SpaceDetail> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(SpaceDetail.builder()
                    .application("test-application-name")
                    .domain("test-domain-name")
                    .id(TEST_SPACE_ID)
                    .name(TEST_SPACE_NAME)
                    .organization("test-organization-name")
                    .securityGroup(SpaceDetail.SecurityGroup.builder()
                        .name("test-security-group-name")
                        .rule(fill(SpaceDetail.SecurityGroup.Rule.builder(), "security-group-")
                            .build())
                        .build())
                    .service("test-service-label")
                    .spaceQuota(Optional
                        .of(fill(SpaceQuota.builder(), "space-quota-definition-")
                            .build()))
                    .build());
        }

        @Override
        protected Publisher<SpaceDetail> invoke() {
            return this.spaces
                .get(GetSpaceRequest.builder()
                    .name("test-space-name")
                    .securityGroupRules(true)
                    .build());
        }

    }

    public static final class GetNoOrganization extends AbstractOperationsApiTest<SpaceDetail> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, MISSING_ORGANIZATION_ID, MISSING_USERNAME);

        @Override
        protected void assertions(TestSubscriber<SpaceDetail> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_ORGANIZATION_ID");
        }

        @Override
        protected Publisher<SpaceDetail> invoke() {
            return this.spaces
                .get(fill(GetSpaceRequest.builder())
                    .build());
        }

    }

    public static final class GetNoSecurityGroupRules extends AbstractOperationsApiTest<SpaceDetail> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_ID);

        @Before
        public void setUp() throws Exception {
            requestOrganization(this.cloudFoundryClient, "test-space-organizationId");
            requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, TEST_SPACE_NAME, "test-space-spaceQuotaDefinitionId");
            requestSpaceApplications(this.cloudFoundryClient, TEST_SPACE_ID);
            requestSpaceDomains(this.cloudFoundryClient, TEST_SPACE_ID);
            requestSpaceSecurityGroups(this.cloudFoundryClient, TEST_SPACE_ID);
            requestSpaceServices(this.cloudFoundryClient, TEST_SPACE_ID);
            requestSpaceQuotaDefinition(this.cloudFoundryClient, "test-space-spaceQuotaDefinitionId");
        }

        @Override
        protected void assertions(TestSubscriber<SpaceDetail> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(SpaceDetail.builder()
                    .application("test-application-name")
                    .domain("test-domain-name")
                    .id(TEST_SPACE_ID)
                    .name(TEST_SPACE_NAME)
                    .organization("test-organization-name")
                    .securityGroup(SpaceDetail.SecurityGroup.builder()
                        .name("test-security-group-name")
                        .build())
                    .service("test-service-label")
                    .spaceQuota(Optional
                        .of(fill(SpaceQuota.builder(), "space-quota-definition-")
                            .build()))
                    .build());
        }

        @Override
        protected Publisher<SpaceDetail> invoke() {
            return this.spaces
                .get(GetSpaceRequest.builder()
                    .name(TEST_SPACE_NAME)
                    .build());
        }
    }

    public static final class GetSpaceQuotaNull extends AbstractOperationsApiTest<SpaceDetail> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_USERNAME);

        @Before
        public void setUp() throws Exception {
            requestOrganization(this.cloudFoundryClient, "test-space-organizationId");
            requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, TEST_SPACE_NAME, null);
            requestSpaceApplications(this.cloudFoundryClient, TEST_SPACE_ID);
            requestSpaceDomains(this.cloudFoundryClient, TEST_SPACE_ID);
            requestSpaceSecurityGroups(this.cloudFoundryClient, TEST_SPACE_ID);
            requestSpaceServices(this.cloudFoundryClient, TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<SpaceDetail> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(SpaceDetail.builder()
                    .application("test-application-name")
                    .domain("test-domain-name")
                    .id(TEST_SPACE_ID)
                    .name(TEST_SPACE_NAME)
                    .organization("test-organization-name")
                    .securityGroup(SpaceDetail.SecurityGroup.builder()
                        .name("test-security-group-name")
                        .build())
                    .service("test-service-label")
                    .spaceQuota(Optional.<SpaceQuota>empty())
                    .build());
        }

        @Override
        protected Publisher<SpaceDetail> invoke() {
            return this.spaces
                .get(GetSpaceRequest.builder()
                    .name(TEST_SPACE_NAME)
                    .securityGroupRules(false)
                    .build());
        }
    }

    public static final class List extends AbstractOperationsApiTest<SpaceSummary> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_USERNAME);

        @Before
        public void setUp() throws Exception {
            requestSpaces(cloudFoundryClient, TEST_ORGANIZATION_ID);
        }

        @Override
        protected void assertions(TestSubscriber<SpaceSummary> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(fill(SpaceSummary.builder(), "space-")
                    .build());
        }

        @Override
        protected Publisher<SpaceSummary> invoke() {
            return this.spaces
                .list();
        }

    }

    public static final class ListNoOrganization extends AbstractOperationsApiTest<SpaceSummary> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, MISSING_ORGANIZATION_ID, MISSING_USERNAME);

        @Override
        protected void assertions(TestSubscriber<SpaceSummary> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_ORGANIZATION_ID");
        }

        @Override
        protected Publisher<SpaceSummary> invoke() {
            return this.spaces
                .list();
        }

    }

    public static final class Rename extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_USERNAME);

        @Before
        public void setUp() throws Exception {
            requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", "test-space-spaceQuotaDefinitionId");
            requestUpdateSpace(this.cloudFoundryClient, "test-space-id", "test-new-space-name");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.spaces
                .rename(RenameSpaceRequest.builder()
                    .name("test-space-name")
                    .newName("test-new-space-name")
                    .build());
        }

    }

    public static final class RenameInvalid extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_USERNAME);

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(RequestValidationException.class, "Request is invalid: name must be specified, new name must be specified");
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.spaces
                .rename(RenameSpaceRequest.builder()
                    .build());
        }

    }

    public static final class RenameNoSpace extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_USERNAME);

        @Before
        public void setUp() throws Exception {
            requestOrganizationSpacesEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Space test-space-name does not exist");
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.spaces
                .rename(RenameSpaceRequest.builder()
                    .name("test-space-name")
                    .newName("test-new-space-name")
                    .build());
        }

    }

    public static final class SshAllowed extends AbstractOperationsApiTest<Boolean> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_USERNAME);

        @Before
        public void setUp() throws Exception {
            requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name", "test-space-spaceQuotaDefinitionId");
        }

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(true);
        }

        @Override
        protected Mono<Boolean> invoke() {
            return this.spaces
                .sshAllowed(SpaceSshAllowedRequest.builder()
                    .name("test-space-name")
                    .build());
        }

    }

    public static final class SshAllowedNoSpace extends AbstractOperationsApiTest<Boolean> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID), MISSING_USERNAME);

        @Before
        public void setUp() throws Exception {
            requestOrganizationSpacesEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name");
        }

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Space test-space-name does not exist");
        }

        @Override
        protected Mono<Boolean> invoke() {
            return this.spaces
                .sshAllowed(SpaceSshAllowedRequest.builder()
                    .name("test-space-name")
                    .build());
        }

    }

}

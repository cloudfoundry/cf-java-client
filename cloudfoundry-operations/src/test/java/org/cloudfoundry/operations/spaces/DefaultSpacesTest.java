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
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.job.GetJobRequest;
import org.cloudfoundry.client.v2.job.GetJobResponse;
import org.cloudfoundry.client.v2.job.JobEntity;
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupResource;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.spacequotadefinitions.GetSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.GetSpaceQuotaDefinitionResponse;
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
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.spaces.UpdateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.UpdateSpaceResponse;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.operations.spacequotas.SpaceQuota;
import org.cloudfoundry.util.Optional;
import org.cloudfoundry.util.RequestValidationException;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.fn.Supplier;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.cloudfoundry.util.test.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultSpacesTest {

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

    private static void requestOrganizationSpaces(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        when(cloudFoundryClient.organizations()
            .listSpaces(fillPage(ListOrganizationSpacesRequest.builder())
                .name(space)
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationSpacesResponse.builder())
                    .resource(fill(SpaceResource.builder(), "space-")
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

    public static final class Delete extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Before
        public void setUp() {
            requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name");
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

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Before
        public void setUp() {
            requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name");
            requestDeleteSpace(this.cloudFoundryClient, "test-space-id");
            requestJobSuccess(this.cloudFoundryClient, "test-id");
            requestJobFailure(this.cloudFoundryClient, "test-id");
        }


        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(CloudFoundryException.class);
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

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(RequestValidationException.class);
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .delete(DeleteSpaceRequest.builder()
                    .build());
        }

    }

    public static final class DeleteInvalidSpace extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Before
        public void setUp() {
            requestOrganizationSpacesEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class);
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

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, MISSING_ID);

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class);
        }

        @Override
        protected Mono<Void> invoke() {
            return this.spaces
                .delete(DeleteSpaceRequest.builder()
                    .name("test-space-name")
                    .build());
        }

    }

    public static final class Get extends AbstractOperationsApiTest<SpaceDetail> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Before
        public void setUp() throws Exception {
            requestOrganization(this.cloudFoundryClient, "test-space-organizationId");
            requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, TEST_SPACE_NAME);
            requestSpaceApplications(this.cloudFoundryClient, TEST_SPACE_ID);
            requestSpaceDomains(this.cloudFoundryClient, TEST_SPACE_ID);
            requestSpaceSecurityGroups(this.cloudFoundryClient, TEST_SPACE_ID);
            requestSpaceServices(this.cloudFoundryClient, TEST_SPACE_ID);
            requestSpaceQuotaDefinition(this.cloudFoundryClient, "test-space-spaceQuotaDefinitionId");
        }

        @Override
        protected void assertions(TestSubscriber<SpaceDetail> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(fill(SpaceDetail.builder())
                    .application("test-application-name")
                    .domain("test-domain-name")
                    .id(TEST_SPACE_ID)
                    .name(TEST_SPACE_NAME)
                    .organization("test-organization-name")
                    .securityGroup("test-security-group-name")
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

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, MISSING_ID);

        @Override
        protected void assertions(TestSubscriber<SpaceDetail> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class);
        }

        @Override
        protected Publisher<SpaceDetail> invoke() {
            return this.spaces
                .get(fill(GetSpaceRequest.builder())
                    .build());
        }

    }

    public static final class GetNoSpaceQuota extends AbstractOperationsApiTest<SpaceDetail> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Before
        public void setUp() throws Exception {
            requestOrganization(this.cloudFoundryClient, "test-space-organizationId");
            requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, TEST_SPACE_NAME);
            requestSpaceApplications(this.cloudFoundryClient, TEST_SPACE_ID);
            requestSpaceDomains(this.cloudFoundryClient, TEST_SPACE_ID);
            requestSpaceSecurityGroups(this.cloudFoundryClient, TEST_SPACE_ID);
            requestSpaceServices(this.cloudFoundryClient, TEST_SPACE_ID);
        }

        @Override
        protected void assertions(TestSubscriber<SpaceDetail> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(fill(SpaceDetail.builder())
                    .application("test-application-name")
                    .domain("test-domain-name")
                    .id(TEST_SPACE_ID)
                    .name(TEST_SPACE_NAME)
                    .organization("test-organization-name")
                    .securityGroup("test-security-group-name")
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

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

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

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, MISSING_ID);

        @Override
        protected void assertions(TestSubscriber<SpaceSummary> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class);
        }

        @Override
        protected Publisher<SpaceSummary> invoke() {
            return this.spaces
                .list();
        }

    }

    public static final class Rename extends AbstractOperationsApiTest<Void> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Before
        public void setUp() throws Exception {
            requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name");
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

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber.assertError(RequestValidationException.class);
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.spaces
                .rename(RenameSpaceRequest.builder()
                    .build());
        }

    }

    public static final class SshEnabled extends AbstractOperationsApiTest<Boolean> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Before
        public void setUp() throws Exception {
            requestOrganizationSpaces(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name");
        }

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber.assertEquals(true);
        }

        @Override
        protected Mono<Boolean> invoke() {
            return this.spaces
                .sshEnabled(SpaceSshEnabledRequest.builder()
                    .name("test-space-name")
                    .build());
        }

    }

    public static final class SshEnabledNoApp extends AbstractOperationsApiTest<Boolean> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Before
        public void setUp() throws Exception {
            requestOrganizationSpacesEmpty(this.cloudFoundryClient, TEST_ORGANIZATION_ID, "test-space-name");
        }

        @Override
        protected void assertions(TestSubscriber<Boolean> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Mono<Boolean> invoke() {
            return this.spaces
                .sshEnabled(SpaceSshEnabledRequest.builder()
                    .name("test-space-name")
                    .build());
        }

    }

}

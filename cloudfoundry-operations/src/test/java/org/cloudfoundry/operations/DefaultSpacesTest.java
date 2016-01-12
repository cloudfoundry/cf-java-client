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

package org.cloudfoundry.operations;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.Resource.Metadata;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.domains.DomainEntity;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupResource;
import org.cloudfoundry.client.v2.services.ServiceEntity;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.spacequotadefinitions.GetSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.GetSpaceQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionEntity;
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
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.Mono;

import static org.cloudfoundry.operations.AbstractOperationsTest.TEST_ORGANIZATION_ID;
import static org.cloudfoundry.operations.AbstractOperationsTest.TEST_SPACE_ID;
import static org.cloudfoundry.operations.AbstractOperationsTest.TEST_SPACE_NAME;
import static org.mockito.Mockito.when;

public final class DefaultSpacesTest {

    private static void setupExpectations(CloudFoundryClient cloudFoundryClient) {
        ListOrganizationSpacesRequest request1 = ListOrganizationSpacesRequest.builder()
                .organizationId(TEST_ORGANIZATION_ID)
                .name(TEST_SPACE_NAME)
                .page(1)
                .build();
        ListOrganizationSpacesResponse response1 = ListOrganizationSpacesResponse.builder()
                .resource(SpaceResource.builder()
                        .metadata(Metadata.builder()
                                .id(TEST_SPACE_ID)
                                .build())
                        .entity(SpaceEntity.builder()
                                .name(TEST_SPACE_NAME)
                                .organizationId(TEST_ORGANIZATION_ID)
                                .build())
                        .build())
                .totalPages(1)
                .build();
        when(cloudFoundryClient.organizations().listSpaces(request1)).thenReturn(Mono.just(response1));

        ListSpaceApplicationsRequest request2 = ListSpaceApplicationsRequest.builder()
                .id(TEST_SPACE_ID)
                .page(1)
                .build();
        ListSpaceApplicationsResponse response2 = ListSpaceApplicationsResponse.builder()
                .resource(ApplicationResource.builder()
                        .metadata(Metadata.builder()
                                .id("test-application-id")
                                .build())
                        .entity(ApplicationEntity.builder()
                                .name("test-application-name")
                                .build())
                        .build())
                .totalPages(1)
                .build();
        when(cloudFoundryClient.spaces().listApplications(request2)).thenReturn(Mono.just(response2));

        ListSpaceDomainsRequest request3 = ListSpaceDomainsRequest.builder()
                .id(TEST_SPACE_ID)
                .page(1)
                .build();
        ListSpaceDomainsResponse response3 = ListSpaceDomainsResponse.builder()
                .resource(DomainResource.builder()
                        .metadata(Metadata.builder()
                                .id("test-domain-id")
                                .build())
                        .entity(DomainEntity.builder()
                                .name("test-domain-name")
                                .build())
                        .build())
                .totalPages(1)
                .build();
        when(cloudFoundryClient.spaces().listDomains(request3)).thenReturn(Mono.just(response3));

        GetOrganizationRequest request4 = GetOrganizationRequest.builder()
                .id(TEST_ORGANIZATION_ID)
                .build();
        GetOrganizationResponse response4 = GetOrganizationResponse.builder()
                .metadata(Metadata.builder()
                        .id(TEST_ORGANIZATION_ID)
                        .build())
                .entity(OrganizationEntity.builder()
                        .name("test-organization-name")
                        .build())
                .build();
        when(cloudFoundryClient.organizations().get(request4)).thenReturn(Mono.just(response4));

        ListSpaceSecurityGroupsRequest request5 = ListSpaceSecurityGroupsRequest.builder()
                .id(TEST_SPACE_ID)
                .page(1)
                .build();
        ListSpaceSecurityGroupsResponse response5 = ListSpaceSecurityGroupsResponse.builder()
                .resource(SecurityGroupResource.builder()
                        .metadata(Metadata.builder()
                                .id("test-security-group-id")
                                .build())
                        .entity(SecurityGroupEntity.builder()
                                .name("test-security-group-name")
                                .build())
                        .build())
                .totalPages(1)
                .build();
        when(cloudFoundryClient.spaces().listSecurityGroups(request5)).thenReturn(Mono.just(response5));

        ListSpaceServicesRequest request6 = ListSpaceServicesRequest.builder()
                .id(TEST_SPACE_ID)
                .page(1)
                .build();
        ListSpaceServicesResponse response6 = ListSpaceServicesResponse.builder()
                .resource(ServiceResource.builder()
                        .metadata(Metadata.builder()
                                .id("test-service-id")
                                .build())
                        .entity(ServiceEntity.builder()
                                .label("test-service-name")
                                .build())
                        .build())
                .totalPages(1)
                .build();
        when(cloudFoundryClient.spaces().listServices(request6)).thenReturn(Mono.just(response6));
    }

    public static final class Get extends AbstractOperationsApiTest<SpaceDetail> {

        private final Spaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Before
        public void setUp() throws Exception {
            setupExpectations(this.cloudFoundryClient);

            GetSpaceQuotaDefinitionRequest request = GetSpaceQuotaDefinitionRequest.builder()
                    .build();
            GetSpaceQuotaDefinitionResponse response = GetSpaceQuotaDefinitionResponse.builder()
                    .metadata(Metadata.builder()
                            .id("test-quota-id")
                            .build())
                    .entity(SpaceQuotaDefinitionEntity.builder()
                            .instanceMemoryLimit(111)
                            .name("test-quota-name")
                            .organizationId(TEST_ORGANIZATION_ID)
                            .nonBasicServicesAllowed(true)
                            .memoryLimit(222)
                            .totalRoutes(333)
                            .totalServices(444)
                            .build())
                    .build();
            when(this.cloudFoundryClient.spaceQuotaDefinitions().get(request)).thenReturn(Mono.just(response));
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
                            .securityGroup("test-security-group-name")
                            .service("test-service-name")
                            .spaceQuota(Optional.of(SpaceQuota.builder()
                                    .id("test-quota-id")
                                    .instanceMemoryLimit(111)
                                    .name("test-quota-name")
                                    .organizationId(TEST_ORGANIZATION_ID)
                                    .paidServicePlans(true)
                                    .totalMemoryLimit(222)
                                    .totalRoutes(333)
                                    .totalServiceInstances(444)
                                    .build()))
                            .build());
        }

        @Override
        protected Mono<SpaceDetail> invoke() {
            GetSpaceRequest request = GetSpaceRequest.builder()
                    .name(TEST_SPACE_NAME)
                    .securityGroupRules(true)
                    .build();

            return this.spaces.get(request);
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
        protected Mono<SpaceDetail> invoke() {
            GetSpaceRequest request = GetSpaceRequest.builder()
                    .name(TEST_SPACE_NAME)
                    .securityGroupRules(true)
                    .build();

            return this.spaces.get(request);
        }

    }

    public static final class GetNoSpaceQuota extends AbstractOperationsApiTest<SpaceDetail> {

        private final Spaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Before
        public void setUp() throws Exception {
            setupExpectations(this.cloudFoundryClient);
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
                            .securityGroup("test-security-group-name")
                            .service("test-service-name")
                            .spaceQuota(Optional.<SpaceQuota>empty())
                            .build());
        }

        @Override
        protected Mono<SpaceDetail> invoke() {
            GetSpaceRequest request = GetSpaceRequest.builder()
                    .name(TEST_SPACE_NAME)
                    .securityGroupRules(false)
                    .build();

            return this.spaces.get(request);
        }

    }

    public static final class List extends AbstractOperationsApiTest<SpaceSummary> {

        private final Spaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Before
        public void setUp() throws Exception {
            ListSpacesRequest request1 = ListSpacesRequest.builder()
                    .organizationId("test-organization-id")
                    .page(1)
                    .build();
            ListSpacesResponse page1 = ListSpacesResponse.builder()
                    .resource(SpaceResource.builder()
                            .metadata(Metadata.builder()
                                    .id("test-id-1")
                                    .build())
                            .entity(SpaceEntity.builder()
                                    .name("test-name-1")
                                    .build())
                            .build())
                    .totalPages(2)
                    .build();
            when(this.cloudFoundryClient.spaces().list(request1)).thenReturn(Mono.just(page1));

            ListSpacesResponse page2 = ListSpacesResponse.builder()
                    .resource(SpaceResource.builder()
                            .metadata(Metadata.builder()
                                    .id("test-id-2")
                                    .build())
                            .entity(SpaceEntity.builder()
                                    .name("test-name-2")
                                    .build())
                            .build())
                    .totalPages(2)
                    .build();
            ListSpacesRequest request2 = ListSpacesRequest.builder()
                    .organizationId("test-organization-id")
                    .page(2)
                    .build();
            when(this.cloudFoundryClient.spaces().list(request2)).thenReturn(Mono.just(page2));
        }

        @Override
        protected void assertions(TestSubscriber<SpaceSummary> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(SpaceSummary.builder()
                            .id("test-id-1")
                            .name("test-name-1")
                            .build())
                    .assertEquals(SpaceSummary.builder()
                            .id("test-id-2")
                            .name("test-name-2")
                            .build());
        }

        @Override
        protected Publisher<SpaceSummary> invoke() {
            return this.spaces.list();
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
            return this.spaces.list();
        }

    }

}

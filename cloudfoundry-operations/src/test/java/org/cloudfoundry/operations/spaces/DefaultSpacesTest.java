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
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupResource;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.spacequotadefinitions.GetSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.GetSpaceQuotaDefinitionResponse;
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
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.operations.spacequotas.SpaceQuota;
import org.cloudfoundry.utils.Optional;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.utils.test.TestObjects.fill;
import static org.cloudfoundry.utils.test.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultSpacesTest {

    private static void requestOrganization(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.organizations()
            .get(GetOrganizationRequest.builder()
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(fill(GetOrganizationResponse.builder(), "organization-")
                    .build()));
    }

    private static void requestOrganizationSpace(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
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

    public static final class Get extends AbstractOperationsApiTest<SpaceDetail> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Before
        public void setUp() throws Exception {
            requestOrganization(this.cloudFoundryClient, "test-space-organizationId");
            requestOrganizationSpace(this.cloudFoundryClient, TEST_ORGANIZATION_ID, TEST_SPACE_NAME);
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
            requestOrganizationSpace(this.cloudFoundryClient, TEST_ORGANIZATION_ID, TEST_SPACE_NAME);
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

}

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
import org.cloudfoundry.client.v2.domains.DomainResource;
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
import org.cloudfoundry.client.v2.organizationquotadefinitions.GetOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.GetOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.operations.spacequotas.SpaceQuota;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.utils.test.TestObjects.fill;
import static org.cloudfoundry.utils.test.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultOrganizationsTest {

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

    public static final class Info extends AbstractOperationsApiTest<OrganizationDetail> {

        private final DefaultOrganizations organizations = new DefaultOrganizations(this.cloudFoundryClient);

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

        private final DefaultOrganizations organizations = new DefaultOrganizations(this.cloudFoundryClient);

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

}

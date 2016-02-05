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

import org.cloudfoundry.client.v2.Resource;
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
import org.cloudfoundry.client.v2.quotadefinitions.GetOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.quotadefinitions.GetOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.quotadefinitions.OrganizationQuotaDefinitionEntity;
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

    public static final class Info extends AbstractOperationsApiTest<OrganizationInfo> {

        private final DefaultOrganizations organizations = new DefaultOrganizations(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            when(cloudFoundryClient.organizations()
                .list(fillPage(ListOrganizationsRequest.builder())
                    .name("test-organization-name")
                    .page(1)
                    .build()))
                .thenReturn(Mono
                    .just(fillPage(ListOrganizationsResponse.builder())
                        .resource(fill(OrganizationResource.builder())
                            .entity(OrganizationEntity.builder()
                                .quotaDefinitionId("test-quota-definition-id")
                                .build())
                            .metadata(Resource.Metadata.builder()
                                .id("test-organization-id")
                                .build())
                            .build())
                        .totalPages(1)
                        .build()));

            when(cloudFoundryClient.organizations()
                .listDomains(fillPage(ListOrganizationDomainsRequest.builder())
                    .organizationId("test-organization-id")
                    .page(1)
                    .build()))
                .thenReturn(Mono
                    .just(fillPage(ListOrganizationDomainsResponse.builder())
                        .resource(fill(DomainResource.builder()).build())
                        .totalPages(1)
                        .build()));

            when(cloudFoundryClient.organizationQuotaDefinitions()
                .get(GetOrganizationQuotaDefinitionRequest.builder()
                    .quotaDefinitionId("test-quota-definition-id")
                    .build()))
                .thenReturn(Mono
                    .just(GetOrganizationQuotaDefinitionResponse.builder()
                        .entity(fill(OrganizationQuotaDefinitionEntity.builder())
                            .build())
                        .build()));

            when(cloudFoundryClient.organizations()
                .listSpaceQuotaDefinitions(fillPage(ListOrganizationSpaceQuotaDefinitionsRequest.builder())
                    .organizationId("test-organization-id")
                    .page(1)
                    .build()))
                .thenReturn(Mono
                    .just(fillPage(ListOrganizationSpaceQuotaDefinitionsResponse.builder())
                        .resource(fill(SpaceQuotaDefinitionResource.builder()).build())
                        .totalPages(1)
                        .build()));

            when(cloudFoundryClient.organizations()
                .listSpaces(fillPage(ListOrganizationSpacesRequest.builder())
                    .organizationId("test-organization-id")
                    .page(1)
                    .build()))
                .thenReturn(Mono
                    .just(fillPage(ListOrganizationSpacesResponse.builder())
                        .resource(fill(SpaceResource.builder()).build())
                        .totalPages(1)
                        .build()));
        }

        @Override
        protected void assertions(TestSubscriber<OrganizationInfo> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(OrganizationInfo.builder()
                    .domain("test-name")
                    .id("test-organization-id")
                    .name("test-organization-name")
                    .quota(OrganizationQuota.builder()
                        .instanceMemoryLimit(1)
                        .name("test-name")
                        .organizationId("test-organization-id")
                        .paidServicePlans(true)
                        .totalMemoryLimit(1)
                        .totalRoutes(1)
                        .totalServiceInstances(1)
                        .build())
                    .spacesQuota(SpaceQuota.builder()
                        .instanceMemoryLimit(1)
                        .name("test-name")
                        .organizationId("test-organizationId")
                        .paidServicePlans(true)
                        .totalMemoryLimit(1)
                        .totalRoutes(1)
                        .totalServiceInstances(1)
                        .build())
                    .space("test-name")
                    .build());
        }

        @Override
        protected Publisher<OrganizationInfo> invoke() {
            OrganizationInfoRequest request = OrganizationInfoRequest.builder()
                .name("test-organization-name")
                .build();
            return this.organizations.info(request);
        }

    }

    public static final class List extends AbstractOperationsApiTest<Organization> {

        private final DefaultOrganizations organizations = new DefaultOrganizations(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            when(cloudFoundryClient.organizations()
                .list(fillPage(ListOrganizationsRequest.builder())
                    .page(1)
                    .build()))
                .thenReturn(Mono
                    .just(fillPage(ListOrganizationsResponse.builder())
                        .resource(fill(OrganizationResource.builder(), "organization1-").build())
                        .totalPages(2)
                        .build()));

            when(cloudFoundryClient.organizations()
                .list(fillPage(ListOrganizationsRequest.builder())
                    .page(2)
                    .build()))
                .thenReturn(Mono
                    .just(fillPage(ListOrganizationsResponse.builder())
                        .resource(fill(OrganizationResource.builder(), "organization2-").build())
                        .totalPages(2)
                        .build()));
        }

        @Override
        protected void assertions(TestSubscriber<Organization> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(fill(Organization.builder(), "organization1-").build())
                .assertEquals(fill(Organization.builder(), "organization2-").build());
        }

        @Override
        protected Publisher<Organization> invoke() {
            return this.organizations.list();
        }

    }

}

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

package org.cloudfoundry.operations.organizationadmin;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitionResource;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.cloudfoundry.util.test.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultOrganizationAdminTest {

    private static void requestListOrganizationQuotas(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.organizationQuotaDefinitions()
            .list(fillPage(ListOrganizationQuotaDefinitionsRequest.builder())
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationQuotaDefinitionsResponse.builder())
                    .resource(fill(OrganizationQuotaDefinitionResource.builder(), "quota-")
                        .build())
                    .build()));
    }

    private static void requestListOrganizationQuotas(CloudFoundryClient cloudFoundryClient, String name) {
        when(cloudFoundryClient.organizationQuotaDefinitions()
            .list(fillPage(ListOrganizationQuotaDefinitionsRequest.builder().name(name))
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationQuotaDefinitionsResponse.builder())
                    .resource(fill(OrganizationQuotaDefinitionResource.builder(), "quota-")
                        .build())
                    .build()));
    }

    private static void requestListOrganizationQuotasEmpty(CloudFoundryClient cloudFoundryClient, String name) {
        when(cloudFoundryClient.organizationQuotaDefinitions()
            .list(fillPage(ListOrganizationQuotaDefinitionsRequest.builder().name(name))
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationQuotaDefinitionsResponse.builder())
                    .build()));
    }

    public static final class GetQuota extends AbstractOperationsApiTest<OrganizationQuota> {

        private final DefaultOrganizationAdmin organizationAdmin = new DefaultOrganizationAdmin(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestListOrganizationQuotas(this.cloudFoundryClient, "test-quota");
        }

        @Override
        protected void assertions(TestSubscriber<OrganizationQuota> testSubscriber) {
            testSubscriber
                .assertEquals(OrganizationQuota.builder()
                    .allowPaidServicePlans(true)
                    .applicationInstanceLimit(1)
                    .id("test-quota-id")
                    .instanceMemoryLimit(1)
                    .memoryLimit(1)
                    .name("test-quota-name")
                    .totalRoutes(1)
                    .totalServices(1)
                    .build());
        }

        @Override
        protected Publisher<OrganizationQuota> invoke() {
            return this.organizationAdmin.getQuota(GetQuotaRequest.builder()
                .name("test-quota")
                .build());
        }
    }

    public static final class GetQuotaNotFound extends AbstractOperationsApiTest<OrganizationQuota> {

        private final DefaultOrganizationAdmin organizationAdmin = new DefaultOrganizationAdmin(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestListOrganizationQuotasEmpty(this.cloudFoundryClient, "test-quota-not-found");
        }

        @Override
        protected void assertions(TestSubscriber<OrganizationQuota> testSubscriber) {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Quota test-quota-not-found does not exist");
        }

        @Override
        protected Mono<OrganizationQuota> invoke() {
            return this.organizationAdmin.getQuota(GetQuotaRequest.builder()
                .name("test-quota-not-found")
                .build());
        }

    }

    public static final class ListQuotas extends AbstractOperationsApiTest<OrganizationQuota> {

        private final DefaultOrganizationAdmin organizationAdmin = new DefaultOrganizationAdmin(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestListOrganizationQuotas(this.cloudFoundryClient);
        }

        @Override
        protected void assertions(TestSubscriber<OrganizationQuota> testSubscriber) {
            testSubscriber
                .assertEquals(OrganizationQuota.builder()
                    .allowPaidServicePlans(true)
                    .applicationInstanceLimit(1)
                    .id("test-quota-id")
                    .instanceMemoryLimit(1)
                    .memoryLimit(1)
                    .name("test-quota-name")
                    .totalRoutes(1)
                    .totalServices(1)
                    .build());
        }

        @Override
        protected Publisher<OrganizationQuota> invoke() {
            return this.organizationAdmin.listQuotas();
        }
    }

}

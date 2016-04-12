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

package org.cloudfoundry.operations.quotas;

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

public final class DefaultQuotasTest {

    private static void requestQuotas(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.organizationQuotaDefinitions()
            .list(fillPage(ListOrganizationQuotaDefinitionsRequest.builder())
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationQuotaDefinitionsResponse.builder())
                    .resource(fill(OrganizationQuotaDefinitionResource.builder(), "quotas-")
                        .build())
                    .build()));
    }

    public static final class List extends AbstractOperationsApiTest<Quota> {

        private final DefaultQuotas quotas = new DefaultQuotas(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestQuotas(this.cloudFoundryClient);
        }

        @Override
        protected void assertions(TestSubscriber<Quota> testSubscriber) {
            testSubscriber
                .assertEquals(Quota.builder()
                    .allowPaidServicePlans(true)
                    .applicationInstanceLimit(1)
                    .instanceMemoryLimit(1)
                    .memoryLimit(1)
                    .name("test-quotas-name")
                    .totalRoutes(1)
                    .totalServices(1)
                    .build());
        }

        @Override
        protected Publisher<Quota> invoke() {
            return this.quotas.list();
        }
    }

}

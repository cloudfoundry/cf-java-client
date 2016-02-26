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

package org.cloudfoundry.operations.spacequotas;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.cloudfoundry.util.test.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultSpaceQuotasTest {

    private static void requestSpaceQuotaDefinitions(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.organizations()
            .listSpaceQuotaDefinitions(fillPage(ListOrganizationSpaceQuotaDefinitionsRequest.builder())
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationSpaceQuotaDefinitionsResponse.builder())
                    .resource(fill(SpaceQuotaDefinitionResource.builder(), "space-quota-definition-")
                        .build())
                    .build()));
    }

    private static void requestSpaceQuotaDefinitionsEmpty(CloudFoundryClient cloudFoundryClient, String organizationId) {
        when(cloudFoundryClient.organizations()
            .listSpaceQuotaDefinitions(fillPage(ListOrganizationSpaceQuotaDefinitionsRequest.builder())
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationSpaceQuotaDefinitionsResponse.builder())
                    .build()));
    }

    public static final class Get extends AbstractOperationsApiTest<SpaceQuota> {

        private final DefaultSpaceQuotas spaceQuotas = new DefaultSpaceQuotas(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Before
        public void setUp() throws Exception {
            requestSpaceQuotaDefinitions(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        }

        @Override
        protected void assertions(TestSubscriber<SpaceQuota> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(fill(SpaceQuota.builder(), "space-quota-definition-")
                    .build());
        }

        @Override
        protected Mono<SpaceQuota> invoke() {
            return this.spaceQuotas
                .get(GetSpaceQuotaRequest.builder()
                    .name("test-space-quota-definition-name")
                    .build());
        }

    }

    public static final class GetNoOrganization extends AbstractOperationsApiTest<SpaceQuota> {

        private final DefaultSpaceQuotas spaceQuotas = new DefaultSpaceQuotas(this.cloudFoundryClient, MISSING_ORGANIZATION_ID);

        @Override
        protected void assertions(TestSubscriber<SpaceQuota> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_ORGANIZATION_ID");
        }

        @Override
        protected Mono<SpaceQuota> invoke() {
            return this.spaceQuotas
                .get(GetSpaceQuotaRequest.builder()
                    .name("test-space-quota-definition-name")
                    .build());
        }

    }

    public static final class GetNotFound extends AbstractOperationsApiTest<SpaceQuota> {

        private final DefaultSpaceQuotas spaceQuotas = new DefaultSpaceQuotas(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Before
        public void setUp() throws Exception {
            requestSpaceQuotaDefinitionsEmpty(cloudFoundryClient, TEST_ORGANIZATION_ID);
        }

        @Override
        protected void assertions(TestSubscriber<SpaceQuota> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Space Quota test-space-quota-definition-name does not exist");
        }

        @Override
        protected Mono<SpaceQuota> invoke() {
            return this.spaceQuotas
                .get(GetSpaceQuotaRequest.builder()
                    .name("test-space-quota-definition-name")
                    .build());
        }

    }

    public static final class List extends AbstractOperationsApiTest<SpaceQuota> {

        private final DefaultSpaceQuotas spaceQuotas = new DefaultSpaceQuotas(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Before
        public void setUp() throws Exception {
            requestSpaceQuotaDefinitions(this.cloudFoundryClient, TEST_ORGANIZATION_ID);
        }

        @Override
        protected void assertions(TestSubscriber<SpaceQuota> testSubscriber) throws Exception {
            testSubscriber
                .assertEquals(fill(SpaceQuota.builder(), "space-quota-definition-")
                    .build());
        }

        @Override
        protected Publisher<SpaceQuota> invoke() {
            return this.spaceQuotas
                .list();
        }

    }

    public static final class ListNoOrganization extends AbstractOperationsApiTest<SpaceQuota> {

        private final DefaultSpaceQuotas spaceQuotas = new DefaultSpaceQuotas(this.cloudFoundryClient, MISSING_ORGANIZATION_ID);

        @Override
        protected void assertions(TestSubscriber<SpaceQuota> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalStateException.class, "MISSING_ORGANIZATION_ID");
        }

        @Override
        protected Publisher<SpaceQuota> invoke() {
            return this.spaceQuotas
                .list();
        }

    }

}

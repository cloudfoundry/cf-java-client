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

import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.operations.RequestValidationException;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.Mono;

import static org.cloudfoundry.operations.util.v2.TestObjects.fill;
import static org.cloudfoundry.operations.util.v2.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultSpaceQuotasTest {

    public static final class Get extends AbstractOperationsApiTest<SpaceQuota> {

        private final DefaultSpaceQuotas spaceQuotas = new DefaultSpaceQuotas(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Before
        public void setUp() throws Exception {
            ListOrganizationSpaceQuotaDefinitionsRequest request = fillPage(ListOrganizationSpaceQuotaDefinitionsRequest.builder())
                    .id(TEST_ORGANIZATION_ID)
                    .build();
            ListOrganizationSpaceQuotaDefinitionsResponse response = fillPage(ListOrganizationSpaceQuotaDefinitionsResponse.builder())
                    .resource(fill(SpaceQuotaDefinitionResource.builder(), "spaceQuotaDefinition-").build())
                    .build();
            when(this.cloudFoundryClient.organizations()
                    .listSpaceQuotaDefinitions(request))
                    .thenReturn(Mono.just(response));
        }

        @Override
        protected void assertions(TestSubscriber<SpaceQuota> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(fill(SpaceQuota.builder(), "spaceQuotaDefinition-").build());
        }

        @Override
        protected Mono<SpaceQuota> invoke() {
            return this.spaceQuotas.get(fill(GetSpaceQuotaRequest.builder(), "spaceQuotaDefinition-").build());
        }

    }

    public static final class GetInvalid extends AbstractOperationsApiTest<SpaceQuota> {

        private final DefaultSpaceQuotas spaceQuotas = new DefaultSpaceQuotas(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Override
        protected void assertions(TestSubscriber<SpaceQuota> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(RequestValidationException.class);
        }

        @Override
        protected Mono<SpaceQuota> invoke() {
            return this.spaceQuotas.get(GetSpaceQuotaRequest.builder().build());
        }
    }

    public static final class GetNoOrganization extends AbstractOperationsApiTest<SpaceQuota> {

        private final DefaultSpaceQuotas spaceQuotas = new DefaultSpaceQuotas(this.cloudFoundryClient, MISSING_ID);

        @Override
        protected void assertions(TestSubscriber<SpaceQuota> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalStateException.class);
        }

        @Override
        protected Mono<SpaceQuota> invoke() {
            return this.spaceQuotas.get(fill(GetSpaceQuotaRequest.builder()).build());
        }

    }


    public static final class GetNotFound extends AbstractOperationsApiTest<SpaceQuota> {

        private final DefaultSpaceQuotas spaceQuotas = new DefaultSpaceQuotas(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Before
        public void setUp() throws Exception {
            when(this.cloudFoundryClient.organizations()
                    .listSpaceQuotaDefinitions(fillPage(ListOrganizationSpaceQuotaDefinitionsRequest.builder())
                            .id(TEST_ORGANIZATION_ID)
                            .build()))
                    .thenReturn(Mono.just(fillPage(ListOrganizationSpaceQuotaDefinitionsResponse.builder()).build()));
        }

        @Override
        protected void assertions(TestSubscriber<SpaceQuota> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Mono<SpaceQuota> invoke() {
            return this.spaceQuotas.get(fill(GetSpaceQuotaRequest.builder(), "does-not-exist").build());
        }

    }

    public static final class List extends AbstractOperationsApiTest<SpaceQuota> {

        private final DefaultSpaceQuotas spaceQuotas = new DefaultSpaceQuotas(this.cloudFoundryClient, Mono.just(TEST_ORGANIZATION_ID));

        @Before
        public void setUp() throws Exception {
            ListOrganizationSpaceQuotaDefinitionsRequest request1 = fillPage(ListOrganizationSpaceQuotaDefinitionsRequest.builder())
                    .id(TEST_ORGANIZATION_ID)
                    .build();
            ListOrganizationSpaceQuotaDefinitionsResponse response1 = fillPage(ListOrganizationSpaceQuotaDefinitionsResponse.builder())
                    .resource(fill(SpaceQuotaDefinitionResource.builder(), "spaceQuotaDefinition1-").build())
                    .totalPages(2)
                    .build();
            ListOrganizationSpaceQuotaDefinitionsRequest request2 = fillPage(ListOrganizationSpaceQuotaDefinitionsRequest.builder())
                    .id(TEST_ORGANIZATION_ID)
                    .page(2)
                    .build();
            ListOrganizationSpaceQuotaDefinitionsResponse response2 = fillPage(ListOrganizationSpaceQuotaDefinitionsResponse.builder())
                    .resource(fill(SpaceQuotaDefinitionResource.builder(), "spaceQuotaDefinition2-").build())
                    .totalPages(2)
                    .build();
            when(this.cloudFoundryClient.organizations()
                    .listSpaceQuotaDefinitions(request1))
                    .thenReturn(Mono.just(response1));
            when(this.cloudFoundryClient.organizations()
                    .listSpaceQuotaDefinitions(request2))
                    .thenReturn(Mono.just(response2));
        }

        @Override
        protected void assertions(TestSubscriber<SpaceQuota> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(fill(SpaceQuota.builder(), "spaceQuotaDefinition1-").build())
                    .assertEquals(fill(SpaceQuota.builder(), "spaceQuotaDefinition2-").build())
            ;
        }

        @Override
        protected Publisher<SpaceQuota> invoke() {
            return this.spaceQuotas.list();
        }

    }

    public static final class ListNoOrganization extends AbstractOperationsApiTest<SpaceQuota> {

        private final DefaultSpaceQuotas spaceQuotas = new DefaultSpaceQuotas(this.cloudFoundryClient, MISSING_ID);

        @Override
        protected void assertions(TestSubscriber<SpaceQuota> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalStateException.class);
        }

        @Override
        protected Publisher<SpaceQuota> invoke() {
            return this.spaceQuotas.list();
        }

    }

}

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
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.organizationquotadefinitions.CreateOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.CreateOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitionResource;
import org.cloudfoundry.client.v2.organizationquotadefinitions.UpdateOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.UpdateOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationResponse;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DefaultOrganizationAdminTest {

    private static void requestCreateOrganizationQuota(CloudFoundryClient cloudFoundryClient, Integer instanceMemoryLimit, Integer memoryLimit, String name, Boolean nonBasicServicesAllowed,
                                                       Integer totalRoutes, Integer totalServices, String quotaDefinitionId) {
        when(cloudFoundryClient.organizationQuotaDefinitions()
            .create(CreateOrganizationQuotaDefinitionRequest.builder()
                .instanceMemoryLimit(instanceMemoryLimit)
                .memoryLimit(memoryLimit)
                .nonBasicServicesAllowed(nonBasicServicesAllowed)
                .totalRoutes(totalRoutes)
                .totalServices(totalServices)
                .name(name)
                .build()))
            .thenReturn(Mono
                .just(CreateOrganizationQuotaDefinitionResponse.builder()
                    .metadata(fill(Metadata.builder())
                        .id(quotaDefinitionId)
                        .build())
                    .entity(OrganizationQuotaDefinitionEntity.builder()
                        .totalServices(totalServices)
                        .memoryLimit(memoryLimit)
                        .instanceMemoryLimit(instanceMemoryLimit)
                        .applicationInstanceLimit(-1)
                        .applicationTaskLimit(-1)
                        .name("test-quota")
                        .nonBasicServicesAllowed(nonBasicServicesAllowed)
                        .totalPrivateDomains(-1)
                        .totalRoutes(totalRoutes)
                        .build())
                    .build()));
    }

    private static void requestCreateOrganizationQuotaError(CloudFoundryClient cloudFoundryClient, String name) {
        when(cloudFoundryClient.organizationQuotaDefinitions()
            .create(CreateOrganizationQuotaDefinitionRequest.builder()
                .instanceMemoryLimit(-1)
                .memoryLimit(0)
                .nonBasicServicesAllowed(false)
                .totalRoutes(0)
                .totalServices(0)
                .name(name)
                .build()))
            .thenReturn(Mono
                .error(new CloudFoundryException(999, "test-exception-description", "test-exception-errorCode")));
    }

    private static void requestListOrganizationEmpty(CloudFoundryClient cloudFoundryClient, String name) {
        when(cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .name(name)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationsResponse.builder())
                    .build()));
    }

    private static void requestListOrganizationQuotas(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.organizationQuotaDefinitions()
            .list(ListOrganizationQuotaDefinitionsRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationQuotaDefinitionsResponse.builder())
                    .resource(fill(OrganizationQuotaDefinitionResource.builder(), "quota-")
                        .build())
                    .build()));
    }

    private static void requestListOrganizationQuotas(CloudFoundryClient cloudFoundryClient, String name) {
        when(cloudFoundryClient.organizationQuotaDefinitions()
            .list(ListOrganizationQuotaDefinitionsRequest.builder()
                .name(name)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationQuotaDefinitionsResponse.builder())
                    .resource(fill(OrganizationQuotaDefinitionResource.builder(), "quota-")
                        .build())
                    .build()));
    }

    private static void requestListOrganizationQuotasEmpty(CloudFoundryClient cloudFoundryClient, String name) {
        when(cloudFoundryClient.organizationQuotaDefinitions()
            .list(ListOrganizationQuotaDefinitionsRequest.builder()
                .name(name)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationQuotaDefinitionsResponse.builder())
                    .build()));
    }

    private static void requestListOrganizations(CloudFoundryClient cloudFoundryClient, String name) {
        when(cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .name(name)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationsResponse.builder())
                    .resource(fill(OrganizationResource.builder(), "organization-")
                        .build())
                    .build()));
    }

    private static void requestUpdateOrganization(CloudFoundryClient cloudFoundryClient, String organizationId, String quotaId) {
        when(cloudFoundryClient.organizations()
            .update(UpdateOrganizationRequest.builder()
                .organizationId(organizationId)
                .quotaDefinitionId(quotaId)
                .build()))
            .thenReturn(Mono
                .just(fill(UpdateOrganizationResponse.builder(), "organization-").build()));
    }

    private static void requestUpdateOrganizationQuota(CloudFoundryClient cloudFoundryClient, String organizationQuotaDefinitionId, Integer instanceMemoryLimit, Integer memoryLimit, String name,
                                                       Boolean nonBasicServicesAllowed, Integer totalRoutes, Integer totalServices) {
        when(cloudFoundryClient.organizationQuotaDefinitions()
            .update(UpdateOrganizationQuotaDefinitionRequest.builder()
                .instanceMemoryLimit(instanceMemoryLimit)
                .memoryLimit(memoryLimit)
                .name(name)
                .nonBasicServicesAllowed(nonBasicServicesAllowed)
                .organizationQuotaDefinitionId(organizationQuotaDefinitionId)
                .totalRoutes(totalRoutes)
                .totalServices(totalServices)
                .build()))
            .thenReturn(Mono
                .just(UpdateOrganizationQuotaDefinitionResponse.builder()
                    .metadata(fill(Metadata.builder()).id(organizationQuotaDefinitionId).build())
                    .entity(OrganizationQuotaDefinitionEntity.builder()
                        .applicationInstanceLimit(-1)
                        .applicationTaskLimit(-1)
                        .instanceMemoryLimit(instanceMemoryLimit)
                        .memoryLimit(memoryLimit)
                        .name(name)
                        .nonBasicServicesAllowed(nonBasicServicesAllowed)
                        .totalPrivateDomains(-1)
                        .totalRoutes(totalRoutes)
                        .totalServices(totalServices)
                        .build())
                    .build()));
    }

    public static final class CreateQuota extends AbstractOperationsApiTest<OrganizationQuota> {

        private final DefaultOrganizationAdmin organizationAdmin = new DefaultOrganizationAdmin(Mono.just(this.cloudFoundryClient));

        @Before
        public void setUp() throws Exception {
            requestCreateOrganizationQuota(this.cloudFoundryClient, 3, 4, "test-quota", true, 1, 2, "test-quota-id");
        }

        @Override
        protected void assertions(TestSubscriber<OrganizationQuota> testSubscriber) {
            testSubscriber
                .assertEquals(OrganizationQuota.builder()
                    .allowPaidServicePlans(true)
                    .applicationInstanceLimit(-1)
                    .id("test-quota-id")
                    .instanceMemoryLimit(3)
                    .memoryLimit(4)
                    .name("test-quota")
                    .totalRoutes(1)
                    .totalServices(2)
                    .build());
        }

        @Override
        protected Publisher<OrganizationQuota> invoke() {
            return this.organizationAdmin.createQuota(CreateQuotaRequest.builder()
                .name("test-quota")
                .allowPaidServicePlans(true)
                .totalRoutes(1)
                .totalServices(2)
                .instanceMemoryLimit(3)
                .memoryLimit(4)
                .build());
        }
    }

    public static final class CreateQuotaError extends AbstractOperationsApiTest<OrganizationQuota> {

        private final DefaultOrganizationAdmin organizationAdmin = new DefaultOrganizationAdmin(Mono.just(this.cloudFoundryClient));

        @Before
        public void setUp() throws Exception {
            requestCreateOrganizationQuotaError(this.cloudFoundryClient, "test-quota-error");
        }

        @Override
        protected void assertions(TestSubscriber<OrganizationQuota> testSubscriber) {
            testSubscriber
                .assertError(CloudFoundryException.class, "test-exception-errorCode(999): test-exception-description");
        }

        @Override
        protected Publisher<OrganizationQuota> invoke() {
            return this.organizationAdmin.createQuota(CreateQuotaRequest.builder()
                .name("test-quota-error")
                .build());
        }
    }

    public static final class GetQuota extends AbstractOperationsApiTest<OrganizationQuota> {

        private final DefaultOrganizationAdmin organizationAdmin = new DefaultOrganizationAdmin(Mono.just(this.cloudFoundryClient));

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

        private final DefaultOrganizationAdmin organizationAdmin = new DefaultOrganizationAdmin(Mono.just(this.cloudFoundryClient));

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

        private final DefaultOrganizationAdmin organizationAdmin = new DefaultOrganizationAdmin(Mono.just(this.cloudFoundryClient));

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

    public static final class SetQuota extends AbstractOperationsApiTest<Void> {

        private final DefaultOrganizationAdmin organizationAdmin = new DefaultOrganizationAdmin(Mono.just(this.cloudFoundryClient));

        @Before
        public void setUp() throws Exception {
            requestListOrganizationQuotas(this.cloudFoundryClient, "test-quota");
            requestListOrganizations(this.cloudFoundryClient, "test-organization");
            requestUpdateOrganization(this.cloudFoundryClient, "test-organization-id", "test-quota-id");

        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.organizationAdmin.setQuota(SetQuotaRequest.builder()
                .organizationName("test-organization")
                .quotaName("test-quota")
                .build());
        }
    }

    public static final class SetQuotaOrganizationNotFound extends AbstractOperationsApiTest<Void> {

        private final DefaultOrganizationAdmin organizationAdmin = new DefaultOrganizationAdmin(Mono.just(this.cloudFoundryClient));

        @Before
        public void setUp() throws Exception {
            requestListOrganizationQuotas(this.cloudFoundryClient, "test-quota");
            requestListOrganizationEmpty(this.cloudFoundryClient, "test-organization-not-found");

        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Organization test-organization-not-found does not exist");
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.organizationAdmin.setQuota(SetQuotaRequest.builder()
                .organizationName("test-organization-not-found")
                .quotaName("test-quota")
                .build());
        }
    }

    public static final class SetQuotaQuotaNotFound extends AbstractOperationsApiTest<Void> {

        private final DefaultOrganizationAdmin organizationAdmin = new DefaultOrganizationAdmin(Mono.just(this.cloudFoundryClient));

        @Before
        public void setUp() throws Exception {
            requestListOrganizationQuotasEmpty(this.cloudFoundryClient, "test-quota-not-found");
            requestListOrganizations(this.cloudFoundryClient, "test-organization");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Quota test-quota-not-found does not exist");
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.organizationAdmin.setQuota(SetQuotaRequest.builder()
                .organizationName("test-organization")
                .quotaName("test-quota-not-found")
                .build());
        }
    }

    public static final class UpdateQuota extends AbstractOperationsApiTest<OrganizationQuota> {

        private final DefaultOrganizationAdmin organizationAdmin = new DefaultOrganizationAdmin(Mono.just(this.cloudFoundryClient));

        @Before
        public void setUp() throws Exception {
            requestListOrganizationQuotas(this.cloudFoundryClient, "test-quota");
            requestUpdateOrganizationQuota(this.cloudFoundryClient, "test-quota-id", 3, 4, "new-test-quota", true, 1, 2);
        }

        @Override
        protected void assertions(TestSubscriber<OrganizationQuota> testSubscriber) {
            testSubscriber
                .assertEquals(OrganizationQuota.builder()
                    .allowPaidServicePlans(true)
                    .applicationInstanceLimit(-1)
                    .id("test-quota-id")
                    .instanceMemoryLimit(3)
                    .memoryLimit(4)
                    .name("new-test-quota")
                    .totalRoutes(1)
                    .totalServices(2)
                    .build());
        }

        @Override
        protected Publisher<OrganizationQuota> invoke() {
            return this.organizationAdmin.updateQuota(UpdateQuotaRequest.builder()
                .name("test-quota")
                .allowPaidServicePlans(true)
                .newName("new-test-quota")
                .totalRoutes(1)
                .totalServices(2)
                .instanceMemoryLimit(3)
                .memoryLimit(4)
                .build());
        }
    }

    public static final class UpdateQuotaNotFound extends AbstractOperationsApiTest<OrganizationQuota> {

        private final DefaultOrganizationAdmin organizationAdmin = new DefaultOrganizationAdmin(Mono.just(this.cloudFoundryClient));

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
        protected Publisher<OrganizationQuota> invoke() {
            return this.organizationAdmin.updateQuota(UpdateQuotaRequest.builder()
                .name("test-quota-not-found")
                .newName("new-test-quota")
                .build());
        }
    }

}

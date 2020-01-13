/*
 * Copyright 2013-2020 the original author or authors.
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
import org.cloudfoundry.client.v2.ClientV2Exception;
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
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DefaultOrganizationAdminTest extends AbstractOperationsTest {

    private final DefaultOrganizationAdmin organizationAdmin = new DefaultOrganizationAdmin(Mono.just(this.cloudFoundryClient));

    @Test
    public void createQuota() {
        requestCreateOrganizationQuota(this.cloudFoundryClient, 3, 4, "test-quota", true, 1, 2, "test-quota-id");

        this.organizationAdmin.createQuota(CreateQuotaRequest.builder()
            .name("test-quota")
            .allowPaidServicePlans(true)
            .totalRoutes(1)
            .totalServices(2)
            .instanceMemoryLimit(3)
            .memoryLimit(4)
            .build())
            .as(StepVerifier::create)
            .expectNext(OrganizationQuota.builder()
                .allowPaidServicePlans(true)
                .applicationInstanceLimit(-1)
                .id("test-quota-id")
                .instanceMemoryLimit(3)
                .memoryLimit(4)
                .name("test-quota")
                .totalReservedRoutePorts(0)
                .totalRoutes(1)
                .totalServices(2)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void createQuotaError() {
        requestCreateOrganizationQuotaError(this.cloudFoundryClient, "test-quota-error");

        this.organizationAdmin.createQuota(CreateQuotaRequest.builder()
            .name("test-quota-error")
            .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessage("test-exception-errorCode(999): test-exception-description"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getQuota() {
        requestListOrganizationQuotas(this.cloudFoundryClient, "test-quota");

        this.organizationAdmin.getQuota(GetQuotaRequest.builder()
            .name("test-quota")
            .build())
            .as(StepVerifier::create)
            .expectNext(OrganizationQuota.builder()
                .allowPaidServicePlans(true)
                .applicationInstanceLimit(1)
                .id("test-quota-id")
                .instanceMemoryLimit(1)
                .memoryLimit(1)
                .name("test-quota-name")
                .totalReservedRoutePorts(1)
                .totalRoutes(1)
                .totalServices(1)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getQuotaNotFound() {
        requestListOrganizationQuotasEmpty(this.cloudFoundryClient, "test-quota-not-found");

        this.organizationAdmin.getQuota(GetQuotaRequest.builder()
            .name("test-quota-not-found")
            .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Quota test-quota-not-found does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listQuotas() {
        requestListOrganizationQuotas(this.cloudFoundryClient);

        this.organizationAdmin.listQuotas()
            .as(StepVerifier::create)
            .expectNext(OrganizationQuota.builder()
                .allowPaidServicePlans(true)
                .applicationInstanceLimit(1)
                .id("test-quota-id")
                .instanceMemoryLimit(1)
                .memoryLimit(1)
                .name("test-quota-name")
                .totalReservedRoutePorts(1)
                .totalRoutes(1)
                .totalServices(1)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void setQuota() {
        requestListOrganizationQuotas(this.cloudFoundryClient, "test-quota");
        requestListOrganizations(this.cloudFoundryClient, "test-organization");
        requestUpdateOrganization(this.cloudFoundryClient, "test-organization-id", "test-quota-id");

        this.organizationAdmin.setQuota(SetQuotaRequest.builder()
            .organizationName("test-organization")
            .quotaName("test-quota")
            .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void setQuotaOrganizationNotFound() {
        requestListOrganizationQuotas(this.cloudFoundryClient, "test-quota");
        requestListOrganizationEmpty(this.cloudFoundryClient, "test-organization-not-found");

        this.organizationAdmin.setQuota(SetQuotaRequest.builder()
            .organizationName("test-organization-not-found")
            .quotaName("test-quota")
            .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Organization test-organization-not-found does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void setQuotaQuotaNotFound() {
        requestListOrganizationQuotasEmpty(this.cloudFoundryClient, "test-quota-not-found");
        requestListOrganizations(this.cloudFoundryClient, "test-organization");

        this.organizationAdmin.setQuota(SetQuotaRequest.builder()
            .organizationName("test-organization")
            .quotaName("test-quota-not-found")
            .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Quota test-quota-not-found does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateQuota() {
        requestListOrganizationQuotas(this.cloudFoundryClient, "test-quota");
        requestUpdateOrganizationQuota(this.cloudFoundryClient, "test-quota-id", 3, 4, "new-test-quota", true, 1, 2);

        this.organizationAdmin.updateQuota(UpdateQuotaRequest.builder()
            .name("test-quota")
            .allowPaidServicePlans(true)
            .newName("new-test-quota")
            .totalRoutes(1)
            .totalServices(2)
            .instanceMemoryLimit(3)
            .memoryLimit(4)
            .build())
            .as(StepVerifier::create)
            .expectNext(OrganizationQuota.builder()
                .allowPaidServicePlans(true)
                .applicationInstanceLimit(-1)
                .id("test-quota-id")
                .instanceMemoryLimit(3)
                .memoryLimit(4)
                .name("new-test-quota")
                .totalReservedRoutePorts(0)
                .totalRoutes(1)
                .totalServices(2)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateQuotaNotFound() {
        requestListOrganizationQuotasEmpty(this.cloudFoundryClient, "test-quota-not-found");

        this.organizationAdmin.updateQuota(UpdateQuotaRequest.builder()
            .name("test-quota-not-found")
            .newName("new-test-quota")
            .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Quota test-quota-not-found does not exist"))
            .verify(Duration.ofSeconds(5));
    }

    private static void requestCreateOrganizationQuota(CloudFoundryClient cloudFoundryClient, Integer instanceMemoryLimit, Integer memoryLimit, String name, Boolean nonBasicServicesAllowed,
                                                       Integer totalRoutes, Integer totalServices, String quotaDefinitionId) {
        when(cloudFoundryClient.organizationQuotaDefinitions()
            .create(CreateOrganizationQuotaDefinitionRequest.builder()
                .applicationInstanceLimit(-1)
                .instanceMemoryLimit(instanceMemoryLimit)
                .memoryLimit(memoryLimit)
                .nonBasicServicesAllowed(nonBasicServicesAllowed)
                .totalReservedRoutePorts(0)
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
                        .totalReservedRoutePorts(0)
                        .totalPrivateDomains(-1)
                        .totalRoutes(totalRoutes)
                        .build())
                    .build()));
    }

    private static void requestCreateOrganizationQuotaError(CloudFoundryClient cloudFoundryClient, String name) {
        when(cloudFoundryClient.organizationQuotaDefinitions()
            .create(CreateOrganizationQuotaDefinitionRequest.builder()
                .applicationInstanceLimit(-1)
                .instanceMemoryLimit(-1)
                .memoryLimit(0)
                .nonBasicServicesAllowed(false)
                .totalReservedRoutePorts(0)
                .totalRoutes(0)
                .totalServices(0)
                .name(name)
                .build()))
            .thenReturn(Mono
                .error(new ClientV2Exception(null, 999, "test-exception-description", "test-exception-errorCode")));
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
                .applicationInstanceLimit(-1)
                .instanceMemoryLimit(instanceMemoryLimit)
                .memoryLimit(memoryLimit)
                .name(name)
                .nonBasicServicesAllowed(nonBasicServicesAllowed)
                .organizationQuotaDefinitionId(organizationQuotaDefinitionId)
                .totalReservedRoutePorts(0)
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
                        .totalReservedRoutePorts(0)
                        .totalPrivateDomains(-1)
                        .totalRoutes(totalRoutes)
                        .totalServices(totalServices)
                        .build())
                    .build()));
    }

}

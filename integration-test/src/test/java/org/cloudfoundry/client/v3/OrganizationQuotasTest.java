/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.client.v3;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;
import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v3.organizations.Organization;
import org.cloudfoundry.client.v3.quotas.Apps;
import org.cloudfoundry.client.v3.quotas.Routes;
import org.cloudfoundry.client.v3.quotas.Services;
import org.cloudfoundry.client.v3.quotas.organizations.*;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_8)
public final class OrganizationQuotasTest extends AbstractIntegrationTest {

    @Autowired private CloudFoundryClient cloudFoundryClient;

    @Test
    public void create() {
        String organizationQuotaName = this.nameFactory.getQuotaDefinitionName();
        Apps organizationQuotaAppLimits =
                Apps.builder()
                        .perProcessMemoryInMb(1024)
                        .totalMemoryInMb(2048)
                        .logRateLimitInBytesPerSecond(0)
                        .build();
        Services organizationQuotaServiceLimits =
                Services.builder().isPaidServicesAllowed(false).totalServiceInstances(10).build();
        Routes organizationQuotaRouteLimits = Routes.builder().totalRoutes(10).build();
        this.cloudFoundryClient
                .organizationQuotasV3()
                .create(
                        CreateOrganizationQuotaRequest.builder()
                                .name(organizationQuotaName)
                                .apps(organizationQuotaAppLimits)
                                .services(organizationQuotaServiceLimits)
                                .routes(organizationQuotaRouteLimits)
                                .build())
                .thenMany(
                        requestListOrganizationQuotas(
                                this.cloudFoundryClient, organizationQuotaName))
                .single()
                .as(StepVerifier::create)
                .assertNext(
                        organizationQuotaResource -> {
                            assertThat(organizationQuotaResource).isNotNull();
                            assertThat(organizationQuotaResource.getId()).isNotNull();
                            assertThat(organizationQuotaResource.getName())
                                    .isEqualTo(organizationQuotaName);
                            assertThat(organizationQuotaResource.getApps())
                                    .isEqualTo(organizationQuotaAppLimits);
                            assertThat(organizationQuotaResource.getServices())
                                    .isEqualTo(organizationQuotaServiceLimits);
                            assertThat(organizationQuotaResource.getRoutes())
                                    .isEqualTo(organizationQuotaRouteLimits);
                        })
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String organizationQuotaName = this.nameFactory.getQuotaDefinitionName();

        createOrganizationQuotaId(this.cloudFoundryClient, organizationQuotaName)
                .flatMap(
                        organizationQuotaId ->
                                this.cloudFoundryClient
                                        .organizationQuotasV3()
                                        .delete(
                                                DeleteOrganizationQuotaRequest.builder()
                                                        .organizationQuotaId(organizationQuotaId)
                                                        .build())
                                        .flatMap(
                                                job ->
                                                        JobUtils.waitForCompletion(
                                                                this.cloudFoundryClient,
                                                                Duration.ofMinutes(5),
                                                                job)))
                .thenMany(
                        requestListOrganizationQuotas(
                                this.cloudFoundryClient, organizationQuotaName))
                .as(StepVerifier::create)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String organizationQuotaName = this.nameFactory.getQuotaDefinitionName();

        createOrganizationQuotaId(this.cloudFoundryClient, organizationQuotaName)
                .flatMap(
                        organizationQuotaId ->
                                this.cloudFoundryClient
                                        .organizationQuotasV3()
                                        .get(
                                                GetOrganizationQuotaRequest.builder()
                                                        .organizationQuotaId(organizationQuotaId)
                                                        .build()))
                .map(GetOrganizationQuotaResponse::getName)
                .as(StepVerifier::create)
                .expectNext(organizationQuotaName)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String organizationQuotaName = this.nameFactory.getQuotaDefinitionName();

        createOrganizationQuota(this.cloudFoundryClient, organizationQuotaName)
                .thenMany(
                        PaginationUtils.requestClientV3Resources(
                                page ->
                                        this.cloudFoundryClient
                                                .organizationQuotasV3()
                                                .list(
                                                        ListOrganizationQuotasRequest.builder()
                                                                .page(page)
                                                                .build())))
                .filter(resource -> organizationQuotaName.equals(resource.getName()))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String organizationQuotaName = this.nameFactory.getQuotaDefinitionName();
        int totalMemoryLimit = 64 * 1024; // 64 GB

        createOrganizationQuotaId(this.cloudFoundryClient, organizationQuotaName)
                .flatMap(
                        organizationQuotaId ->
                                this.cloudFoundryClient
                                        .organizationQuotasV3()
                                        .update(
                                                UpdateOrganizationQuotaRequest.builder()
                                                        .organizationQuotaId(organizationQuotaId)
                                                        .apps(
                                                                Apps.builder()
                                                                        .totalMemoryInMb(
                                                                                totalMemoryLimit)
                                                                        .build())
                                                        .routes(
                                                                Routes.builder()
                                                                        .totalRoutes(100)
                                                                        .build())
                                                        .services(
                                                                Services.builder()
                                                                        .isPaidServicesAllowed(true)
                                                                        .totalServiceInstances(100)
                                                                        .build())
                                                        .build()))
                .thenMany(
                        requestListOrganizationQuotas(
                                this.cloudFoundryClient, organizationQuotaName))
                .as(StepVerifier::create)
                .consumeNextWith(
                        organizationQuotaResource -> {
                            assertThat(organizationQuotaResource.getApps().getTotalMemoryInMb())
                                    .isEqualTo(totalMemoryLimit);
                            assertThat(organizationQuotaResource.getRoutes().getTotalRoutes())
                                    .isEqualTo(100);
                            assertThat(
                                            organizationQuotaResource
                                                    .getServices()
                                                    .getTotalServiceInstances())
                                    .isEqualTo(100);
                        })
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void apply() {
        String orgName = this.nameFactory.getOrganizationName();
        String organizationId = createOrganization(this.cloudFoundryClient, orgName).getId();
        Relationship organizationRelationship1 = Relationship.builder().id(organizationId).build();
        ToManyRelationship organizationRelationships =
                ToManyRelationship.builder().data(organizationRelationship1).build();

        String organizationQuotaName = this.nameFactory.getQuotaDefinitionName();

        createOrganizationQuotaId(this.cloudFoundryClient, organizationQuotaName)
                .flatMap(
                        organizationQuotaId -> {
                            ApplyOrganizationQuotaRequest applyOrganizationQuotaRequest =
                                    ApplyOrganizationQuotaRequest.builder()
                                            .organizationQuotaId(organizationQuotaId)
                                            .organizationRelationships(organizationRelationships)
                                            .build();
                            return this.cloudFoundryClient
                                    .organizationQuotasV3()
                                    .apply(applyOrganizationQuotaRequest);
                        })
                .as(StepVerifier::create)
                .consumeNextWith(
                        applyOrganizationQuotaResponse -> {
                            List<Relationship> organizationRelationshipsData =
                                    applyOrganizationQuotaResponse
                                            .organizationRelationships()
                                            .getData();
                            assertThat(organizationRelationshipsData.size()).isEqualTo(1);
                            assertThat(organizationRelationshipsData.get(0).getId())
                                    .isEqualTo(organizationId);
                        })
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createOrganizationQuotaId(
            CloudFoundryClient cloudFoundryClient, String organizationQuotaName) {
        return createOrganizationQuota(cloudFoundryClient, organizationQuotaName)
                .map(CreateOrganizationQuotaResponse::getId);
    }

    private static Mono<CreateOrganizationQuotaResponse> createOrganizationQuota(
            CloudFoundryClient cloudFoundryClient, String organizationQuotaName) {
        return cloudFoundryClient
                .organizationQuotasV3()
                .create(
                        CreateOrganizationQuotaRequest.builder()
                                .name(organizationQuotaName)
                                .build());
    }

    private static Flux<OrganizationQuotaResource> requestListOrganizationQuotas(
            CloudFoundryClient cloudFoundryClient, String organizationName) {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        cloudFoundryClient
                                .organizationQuotasV3()
                                .list(
                                        ListOrganizationQuotasRequest.builder()
                                                .name(organizationName)
                                                .page(page)
                                                .build()));
    }

    private static Organization createOrganization(
            CloudFoundryClient cloudFoundryClient, String orgName) {
        return cloudFoundryClient
                .organizationsV3()
                .create(CreateOrganizationRequest.builder().name(orgName).build())
                .block(Duration.ofMinutes(5));
    }
}

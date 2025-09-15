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
import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.organizationquotadefinitions.Apps;
import org.cloudfoundry.client.v3.organizationquotadefinitions.CreateOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v3.organizationquotadefinitions.CreateOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v3.organizationquotadefinitions.DeleteOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v3.organizationquotadefinitions.GetOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.client.v3.organizationquotadefinitions.GetOrganizationQuotaDefinitionResponse;
import org.cloudfoundry.client.v3.organizationquotadefinitions.ListOrganizationQuotaDefinitionsRequest;
import org.cloudfoundry.client.v3.organizationquotadefinitions.OrganizationQuotaDefinitionResource;
import org.cloudfoundry.client.v3.organizationquotadefinitions.Routes;
import org.cloudfoundry.client.v3.organizationquotadefinitions.Services;
import org.cloudfoundry.client.v3.organizationquotadefinitions.UpdateOrganizationQuotaDefinitionRequest;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_8)
public final class OrganizationQuotaDefinitionsTest extends AbstractIntegrationTest {

    @Autowired private CloudFoundryClient cloudFoundryClient;

    @Test
    public void create() {
        String organizationQuotaName = this.nameFactory.getQuotaDefinitionName();
        this.cloudFoundryClient
                .organizationQuotaDefinitionsV3()
                .create(
                        CreateOrganizationQuotaDefinitionRequest.builder()
                                .name(organizationQuotaName)
                                .build())
                .thenMany(
                        requestListOrganizationQuotas(
                                this.cloudFoundryClient, organizationQuotaName))
                .single()
                .as(StepVerifier::create)
                .expectNextCount(1)
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
                                        .organizationQuotaDefinitionsV3()
                                        .delete(
                                                DeleteOrganizationQuotaDefinitionRequest.builder()
                                                        .organizationQuotaDefinitionId(
                                                                organizationQuotaId)
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
                                        .organizationQuotaDefinitionsV3()
                                        .get(
                                                GetOrganizationQuotaDefinitionRequest.builder()
                                                        .organizationQuotaDefinitionId(
                                                                organizationQuotaId)
                                                        .build()))
                .map(GetOrganizationQuotaDefinitionResponse::getName)
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
                                                .organizationQuotaDefinitionsV3()
                                                .list(
                                                        ListOrganizationQuotaDefinitionsRequest
                                                                .builder()
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
                                        .organizationQuotaDefinitionsV3()
                                        .update(
                                                UpdateOrganizationQuotaDefinitionRequest.builder()
                                                        .organizationQuotaDefinitionId(
                                                                organizationQuotaId)
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
                        organizationQuotaDefinitionResource -> {
                            assertThat(
                                            organizationQuotaDefinitionResource
                                                    .getApps()
                                                    .getTotalMemoryInMb())
                                    .isEqualTo(totalMemoryLimit);
                            assertThat(
                                            organizationQuotaDefinitionResource
                                                    .getRoutes()
                                                    .getTotalRoutes())
                                    .isEqualTo(100);
                            assertThat(
                                            organizationQuotaDefinitionResource
                                                    .getServices()
                                                    .getTotalServiceInstances())
                                    .isEqualTo(100);
                        })
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createOrganizationQuotaId(
            CloudFoundryClient cloudFoundryClient, String organizationQuotaName) {
        return createOrganizationQuota(cloudFoundryClient, organizationQuotaName)
                .map(CreateOrganizationQuotaDefinitionResponse::getId);
    }

    private static Mono<CreateOrganizationQuotaDefinitionResponse> createOrganizationQuota(
            CloudFoundryClient cloudFoundryClient, String organizationQuotaName) {
        return cloudFoundryClient
                .organizationQuotaDefinitionsV3()
                .create(
                        CreateOrganizationQuotaDefinitionRequest.builder()
                                .name(organizationQuotaName)
                                .build());
    }

    private static Flux<OrganizationQuotaDefinitionResource> requestListOrganizationQuotas(
            CloudFoundryClient cloudFoundryClient, String organizationName) {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        cloudFoundryClient
                                .organizationQuotaDefinitionsV3()
                                .list(
                                        ListOrganizationQuotaDefinitionsRequest.builder()
                                                .name(organizationName)
                                                .page(page)
                                                .build()));
    }
}

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
import org.cloudfoundry.client.v3.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v3.organizations.Organization;
import org.cloudfoundry.client.v3.spacequotadefinitions.Apps;
import org.cloudfoundry.client.v3.spacequotadefinitions.CreateSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v3.spacequotadefinitions.CreateSpaceQuotaDefinitionResponse;
import org.cloudfoundry.client.v3.spacequotadefinitions.DeleteSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v3.spacequotadefinitions.GetSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v3.spacequotadefinitions.GetSpaceQuotaDefinitionResponse;
import org.cloudfoundry.client.v3.spacequotadefinitions.ListSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v3.spacequotadefinitions.Routes;
import org.cloudfoundry.client.v3.spacequotadefinitions.Services;
import org.cloudfoundry.client.v3.spacequotadefinitions.SpaceQuotaDefinitionRelationships;
import org.cloudfoundry.client.v3.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.client.v3.spacequotadefinitions.UpdateSpaceQuotaDefinitionRequest;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_8)
public final class SpaceQuotaDefinitionsTest extends AbstractIntegrationTest {

    @Autowired private CloudFoundryClient cloudFoundryClient;

    private String organizationId;

    @BeforeEach
    public void createOrganization() {
        String orgName = this.nameFactory.getOrganizationName();
        organizationId = createOrganization(this.cloudFoundryClient, orgName).getId();
    }

    @Test
    public void create() {
        String spaceQuotaName = this.nameFactory.getQuotaDefinitionName();
        SpaceQuotaDefinitionRelationships spaceQuotaDefinitionRelationships =
                createSpaceQuotaDefinitionRelationships(organizationId);

        this.cloudFoundryClient
                .spaceQuotaDefinitionsV3()
                .create(
                        CreateSpaceQuotaDefinitionRequest.builder()
                                .name(spaceQuotaName)
                                .relationships(spaceQuotaDefinitionRelationships)
                                .build())
                .thenMany(requestListSpaceQuotas(this.cloudFoundryClient, spaceQuotaName))
                .single()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String spaceQuotaName = this.nameFactory.getQuotaDefinitionName();

        createSpaceQuotaId(this.cloudFoundryClient, spaceQuotaName, organizationId)
                .flatMap(
                        spaceQuotaId ->
                                this.cloudFoundryClient
                                        .spaceQuotaDefinitionsV3()
                                        .get(
                                                GetSpaceQuotaDefinitionRequest.builder()
                                                        .spaceQuotaDefinitionId(spaceQuotaId)
                                                        .build()))
                .map(GetSpaceQuotaDefinitionResponse::getName)
                .as(StepVerifier::create)
                .expectNext(spaceQuotaName)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String spaceQuotaName = this.nameFactory.getQuotaDefinitionName();

        createSpaceQuota(this.cloudFoundryClient, spaceQuotaName, organizationId)
                .thenMany(
                        PaginationUtils.requestClientV3Resources(
                                page ->
                                        this.cloudFoundryClient
                                                .spaceQuotaDefinitionsV3()
                                                .list(
                                                        ListSpaceQuotaDefinitionsRequest.builder()
                                                                .page(page)
                                                                .build())))
                .filter(resource -> spaceQuotaName.equals(resource.getName()))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String spaceQuotaName = this.nameFactory.getQuotaDefinitionName();
        int totalMemoryLimit = 64 * 1024; // 64 GB

        createSpaceQuotaId(this.cloudFoundryClient, spaceQuotaName, organizationId)
                .flatMap(
                        spaceQuotaId ->
                                this.cloudFoundryClient
                                        .spaceQuotaDefinitionsV3()
                                        .update(
                                                UpdateSpaceQuotaDefinitionRequest.builder()
                                                        .spaceQuotaDefinitionId(spaceQuotaId)
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
                .thenMany(requestListSpaceQuotas(this.cloudFoundryClient, spaceQuotaName))
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

    @Test
    public void delete() {
        String spaceQuotaName = this.nameFactory.getQuotaDefinitionName();

        createSpaceQuotaId(this.cloudFoundryClient, spaceQuotaName, organizationId)
                .flatMap(
                        spaceQuotaId ->
                                this.cloudFoundryClient
                                        .spaceQuotaDefinitionsV3()
                                        .delete(
                                                DeleteSpaceQuotaDefinitionRequest.builder()
                                                        .spaceQuotaDefinitionId(spaceQuotaId)
                                                        .build())
                                        .flatMap(
                                                job ->
                                                        JobUtils.waitForCompletion(
                                                                this.cloudFoundryClient,
                                                                Duration.ofMinutes(5),
                                                                job)))
                .thenMany(requestListSpaceQuotas(this.cloudFoundryClient, spaceQuotaName))
                .as(StepVerifier::create)
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    private static Organization createOrganization(
            CloudFoundryClient cloudFoundryClient, String orgName) {
        return cloudFoundryClient
                .organizationsV3()
                .create(CreateOrganizationRequest.builder().name(orgName).build())
                .block(Duration.ofMinutes(5));
    }

    @NotNull
    private static SpaceQuotaDefinitionRelationships createSpaceQuotaDefinitionRelationships(
            String orgGuid) {
        ToOneRelationship organizationRelationship =
                ToOneRelationship.builder()
                        .data(Relationship.builder().id(orgGuid).build())
                        .build();
        return SpaceQuotaDefinitionRelationships.builder()
                .organization(organizationRelationship)
                .build();
    }

    private static Mono<String> createSpaceQuotaId(
            CloudFoundryClient cloudFoundryClient, String spaceQuotaName, String orgGuid) {
        return createSpaceQuota(cloudFoundryClient, spaceQuotaName, orgGuid)
                .map(CreateSpaceQuotaDefinitionResponse::getId);
    }

    private static Mono<CreateSpaceQuotaDefinitionResponse> createSpaceQuota(
            CloudFoundryClient cloudFoundryClient, String spaceQuotaName, String orgGuid) {
        SpaceQuotaDefinitionRelationships spaceQuotaDefinitionRelationships =
                createSpaceQuotaDefinitionRelationships(orgGuid);
        return cloudFoundryClient
                .spaceQuotaDefinitionsV3()
                .create(
                        CreateSpaceQuotaDefinitionRequest.builder()
                                .name(spaceQuotaName)
                                .relationships(spaceQuotaDefinitionRelationships)
                                .build());
    }

    private static Flux<SpaceQuotaDefinitionResource> requestListSpaceQuotas(
            CloudFoundryClient cloudFoundryClient, String spaceName) {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        cloudFoundryClient
                                .spaceQuotaDefinitionsV3()
                                .list(
                                        ListSpaceQuotaDefinitionsRequest.builder()
                                                .name(spaceName)
                                                .page(page)
                                                .build()));
    }
}

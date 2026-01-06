/*
 * Copyright 2013-2025 the original author or authors.
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v3.organizations.Organization;
import org.cloudfoundry.client.v3.quotas.Apps;
import org.cloudfoundry.client.v3.quotas.Routes;
import org.cloudfoundry.client.v3.quotas.Services;
import org.cloudfoundry.client.v3.quotas.spaces.*;
import org.cloudfoundry.client.v3.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v3.spaces.Space;
import org.cloudfoundry.client.v3.spaces.SpaceRelationships;
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
public final class SpaceQuotasTest extends AbstractIntegrationTest {

    @Autowired private CloudFoundryClient cloudFoundryClient;

    private String organizationId;
    private String spaceId;

    @BeforeEach
    public void createOrganization() {
        String orgName = this.nameFactory.getOrganizationName();
        String spaceName = this.nameFactory.getSpaceName();

        organizationId = createOrganization(this.cloudFoundryClient, orgName).getId();
        spaceId = createSpace(this.cloudFoundryClient, organizationId, spaceName).getId();
    }

    @Test
    public void create() {
        String spaceQuotaName = this.nameFactory.getQuotaDefinitionName();
        SpaceQuotaRelationships spaceQuotaRelationships =
                createSpaceQuotaRelationships(organizationId);

        Apps spaceQuotaAppLimits =
                Apps.builder()
                        .perProcessMemoryInMb(1024)
                        .totalMemoryInMb(2048)
                        .logRateLimitInBytesPerSecond(0)
                        .build();
        Services spaceQuotaServiceLimits =
                Services.builder().isPaidServicesAllowed(false).totalServiceInstances(10).build();
        Routes spaceQuotaRouteLimits = Routes.builder().totalRoutes(10).build();

        this.cloudFoundryClient
                .spaceQuotasV3()
                .create(
                        CreateSpaceQuotaRequest.builder()
                                .name(spaceQuotaName)
                                .apps(spaceQuotaAppLimits)
                                .services(spaceQuotaServiceLimits)
                                .routes(spaceQuotaRouteLimits)
                                .relationships(spaceQuotaRelationships)
                                .build())
                .thenMany(requestListSpaceQuotas(this.cloudFoundryClient, spaceQuotaName))
                .single()
                .as(StepVerifier::create)
                .assertNext(
                        spaceQuotaResource -> {
                            assertThat(spaceQuotaResource).isNotNull();
                            assertThat(spaceQuotaResource.getId()).isNotNull();
                            assertThat(spaceQuotaResource.getName()).isEqualTo(spaceQuotaName);
                            assertThat(spaceQuotaResource.getApps()).isEqualTo(spaceQuotaAppLimits);
                            assertThat(spaceQuotaResource.getServices())
                                    .isEqualTo(spaceQuotaServiceLimits);
                            assertThat(spaceQuotaResource.getRoutes())
                                    .isEqualTo(spaceQuotaRouteLimits);
                        })
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void createWithSpaceRelationship() {
        String spaceQuotaName = this.nameFactory.getQuotaDefinitionName();
        SpaceQuotaRelationships spaceQuotaRelationships =
                createSpaceQuotaRelationships(organizationId, Collections.singletonList(spaceId));

        Apps spaceQuotaAppLimits =
                Apps.builder()
                        .perProcessMemoryInMb(1024)
                        .totalMemoryInMb(2048)
                        .logRateLimitInBytesPerSecond(0)
                        .build();
        Services spaceQuotaServiceLimits =
                Services.builder().isPaidServicesAllowed(false).totalServiceInstances(10).build();
        Routes spaceQuotaRouteLimits = Routes.builder().totalRoutes(10).build();

        this.cloudFoundryClient
                .spaceQuotasV3()
                .create(
                        CreateSpaceQuotaRequest.builder()
                                .name(spaceQuotaName)
                                .apps(spaceQuotaAppLimits)
                                .services(spaceQuotaServiceLimits)
                                .routes(spaceQuotaRouteLimits)
                                .relationships(spaceQuotaRelationships)
                                .build())
                .thenMany(requestListSpaceQuotas(this.cloudFoundryClient, spaceQuotaName))
                .single()
                .as(StepVerifier::create)
                .assertNext(
                        spaceQuotaResource -> {
                            assertThat(spaceQuotaResource).isNotNull();
                            assertThat(spaceQuotaResource.getId()).isNotNull();
                            assertThat(spaceQuotaResource.getName()).isEqualTo(spaceQuotaName);
                            assertThat(spaceQuotaResource.getApps()).isEqualTo(spaceQuotaAppLimits);
                            assertThat(spaceQuotaResource.getServices())
                                    .isEqualTo(spaceQuotaServiceLimits);
                            assertThat(spaceQuotaResource.getRoutes())
                                    .isEqualTo(spaceQuotaRouteLimits);
                            assertThat(spaceQuotaResource.getRelationships()).isNotNull();
                            assertThat(spaceQuotaResource.getRelationships().getOrganization())
                                    .isNotNull();
                            assertThat(spaceQuotaResource.getRelationships().getSpaces())
                                    .isNotNull();
                            assertThat(
                                            spaceQuotaResource
                                                    .getRelationships()
                                                    .getOrganization()
                                                    .getData()
                                                    .getId())
                                    .isEqualTo(organizationId);
                            assertThat(
                                            spaceQuotaResource
                                                    .getRelationships()
                                                    .getSpaces()
                                                    .getData()
                                                    .get(0)
                                                    .getId())
                                    .isEqualTo(spaceId);
                        })
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
                                        .spaceQuotasV3()
                                        .get(
                                                GetSpaceQuotaRequest.builder()
                                                        .spaceQuotaId(spaceQuotaId)
                                                        .build()))
                .map(GetSpaceQuotaResponse::getName)
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
                                                .spaceQuotasV3()
                                                .list(
                                                        ListSpaceQuotasRequest.builder()
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
                                        .spaceQuotasV3()
                                        .update(
                                                UpdateSpaceQuotaRequest.builder()
                                                        .spaceQuotaId(spaceQuotaId)
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
    public void delete() {
        String spaceQuotaName = this.nameFactory.getQuotaDefinitionName();

        createSpaceQuotaId(this.cloudFoundryClient, spaceQuotaName, organizationId)
                .flatMap(
                        spaceQuotaId ->
                                this.cloudFoundryClient
                                        .spaceQuotasV3()
                                        .delete(
                                                DeleteSpaceQuotaRequest.builder()
                                                        .spaceQuotaId(spaceQuotaId)
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

    @Test
    public void apply() {
        String spaceQuotaName = this.nameFactory.getQuotaDefinitionName();

        Relationship spaceRelationship1 = Relationship.builder().id(spaceId).build();
        ToManyRelationship spaceRelationships =
                ToManyRelationship.builder().data(spaceRelationship1).build();

        createSpaceQuotaId(this.cloudFoundryClient, spaceQuotaName, organizationId)
                .flatMap(
                        spaceQuotaId -> {
                            ApplySpaceQuotaRequest applySpaceQuotaRequest =
                                    ApplySpaceQuotaRequest.builder()
                                            .spaceQuotaId(spaceQuotaId)
                                            .spaceRelationships(spaceRelationships)
                                            .build();
                            return this.cloudFoundryClient
                                    .spaceQuotasV3()
                                    .apply(applySpaceQuotaRequest);
                        })
                .as(StepVerifier::create)
                .consumeNextWith(
                        applySpaceQuotaResponse -> {
                            List<Relationship> spaceRelationshipsData =
                                    applySpaceQuotaResponse.spaceRelationships().getData();
                            assertThat(spaceRelationshipsData.size()).isEqualTo(1);
                            assertThat(spaceRelationshipsData.get(0).getId()).isEqualTo(spaceId);
                        })
                .expectComplete()
                .verify(Duration.ofMinutes(5));
    }

    @Test
    public void remove() {
        String spaceQuotaName = this.nameFactory.getQuotaDefinitionName();

        // create a space quota in org with "organizationId" that is pre-associated to the space
        // with "spaceId"
        createSpaceQuotaId(
                        this.cloudFoundryClient,
                        spaceQuotaName,
                        organizationId,
                        Collections.singletonList(spaceId))
                .flatMap(
                        spaceQuotaId -> {
                            RemoveSpaceQuotaRequest removeSpaceQuotaRequest =
                                    RemoveSpaceQuotaRequest.builder()
                                            .spaceQuotaId(spaceQuotaId)
                                            .spaceId(spaceId)
                                            .build();
                            return this.cloudFoundryClient
                                    .spaceQuotasV3()
                                    .remove(removeSpaceQuotaRequest);
                        })
                .thenMany(requestListSpaceQuotas(this.cloudFoundryClient, spaceQuotaName))
                .as(StepVerifier::create)
                .consumeNextWith(
                        spaceQuotaResource -> {
                            List<Relationship> spaceRelationshipsData =
                                    spaceQuotaResource.getRelationships().getSpaces().getData();
                            assertThat(spaceRelationshipsData.size()).isEqualTo(0);
                        })
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

    private static Space createSpace(
            CloudFoundryClient cloudFoundryClient, String orgGuid, String spaceName) {
        ToOneRelationship organizationRelationship =
                ToOneRelationship.builder()
                        .data(Relationship.builder().id(orgGuid).build())
                        .build();
        SpaceRelationships spaceRelationships =
                SpaceRelationships.builder().organization(organizationRelationship).build();
        return cloudFoundryClient
                .spacesV3()
                .create(
                        CreateSpaceRequest.builder()
                                .name(spaceName)
                                .relationships(spaceRelationships)
                                .build())
                .block(Duration.ofMinutes(5));
    }

    @NotNull
    private static SpaceQuotaRelationships createSpaceQuotaRelationships(String orgGuid) {
        ToOneRelationship organizationRelationship =
                ToOneRelationship.builder()
                        .data(Relationship.builder().id(orgGuid).build())
                        .build();
        return SpaceQuotaRelationships.builder().organization(organizationRelationship).build();
    }

    @NotNull
    private static SpaceQuotaRelationships createSpaceQuotaRelationships(
            String orgGuid, List<String> spaceGuids) {
        ToOneRelationship organizationRelationship =
                ToOneRelationship.builder()
                        .data(Relationship.builder().id(orgGuid).build())
                        .build();

        // iterate over spaceGuids to create Relationship objects
        List<Relationship> spaceRelationshipsData =
                spaceGuids.stream()
                        .map(spaceGuid -> Relationship.builder().id(spaceGuid).build())
                        .collect(Collectors.toList());

        ToManyRelationship spaceRelationships =
                ToManyRelationship.builder().data(spaceRelationshipsData).build();
        return SpaceQuotaRelationships.builder()
                .organization(organizationRelationship)
                .spaces(spaceRelationships)
                .build();
    }

    private static Mono<String> createSpaceQuotaId(
            CloudFoundryClient cloudFoundryClient, String spaceQuotaName, String orgGuid) {
        return createSpaceQuota(cloudFoundryClient, spaceQuotaName, orgGuid)
                .map(CreateSpaceQuotaResponse::getId);
    }

    private static Mono<CreateSpaceQuotaResponse> createSpaceQuota(
            CloudFoundryClient cloudFoundryClient, String spaceQuotaName, String orgGuid) {
        SpaceQuotaRelationships spaceQuotaRelationships = createSpaceQuotaRelationships(orgGuid);
        return cloudFoundryClient
                .spaceQuotasV3()
                .create(
                        CreateSpaceQuotaRequest.builder()
                                .name(spaceQuotaName)
                                .relationships(spaceQuotaRelationships)
                                .build());
    }

    private static Mono<String> createSpaceQuotaId(
            CloudFoundryClient cloudFoundryClient,
            String spaceQuotaName,
            String orgGuid,
            List<String> spaceGuids) {
        return createSpaceQuota(cloudFoundryClient, spaceQuotaName, orgGuid, spaceGuids)
                .map(CreateSpaceQuotaResponse::getId);
    }

    private static Mono<CreateSpaceQuotaResponse> createSpaceQuota(
            CloudFoundryClient cloudFoundryClient,
            String spaceQuotaName,
            String orgGuid,
            List<String> spaceGuids) {
        SpaceQuotaRelationships spaceQuotaRelationships =
                createSpaceQuotaRelationships(orgGuid, spaceGuids);
        return cloudFoundryClient
                .spaceQuotasV3()
                .create(
                        CreateSpaceQuotaRequest.builder()
                                .name(spaceQuotaName)
                                .relationships(spaceQuotaRelationships)
                                .build());
    }

    private static Flux<SpaceQuotaResource> requestListSpaceQuotas(
            CloudFoundryClient cloudFoundryClient, String spaceName) {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        cloudFoundryClient
                                .spaceQuotasV3()
                                .list(
                                        ListSpaceQuotasRequest.builder()
                                                .name(spaceName)
                                                .page(page)
                                                .build()));
    }
}

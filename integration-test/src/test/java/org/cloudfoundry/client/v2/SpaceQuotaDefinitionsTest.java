/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.AssociateSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.AssociateSpaceQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.CreateSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.CreateSpaceQuotaDefinitionResponse;
import org.cloudfoundry.client.v2.spacequotadefinitions.DeleteSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.GetSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.ListSpaceQuotaDefinitionSpacesRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.ListSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.RemoveSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionEntity;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitionResource;
import org.cloudfoundry.client.v2.spacequotadefinitions.UpdateSpaceQuotaDefinitionRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class SpaceQuotaDefinitionsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Test
    public void associateSpace() {
        String quotaName = this.nameFactory.getQuotaDefinitionName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName),
                createSpaceQuotaDefinitionId(this.cloudFoundryClient, organizationId, quotaName)
            ))
            .flatMap(function((spaceId, quotaId) -> this.cloudFoundryClient.spaceQuotaDefinitions()
                .associateSpace(AssociateSpaceQuotaDefinitionRequest.builder()
                    .spaceId(spaceId)
                    .spaceQuotaDefinitionId(quotaId)
                    .build())))
            .map(ResourceUtils::getEntity)
            .map(SpaceQuotaDefinitionEntity::getName)
            .as(StepVerifier::create)
            .expectNext(quotaName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() {
        String quotaName = this.nameFactory.getQuotaDefinitionName();

        this.organizationId
            .flatMap(organizationId -> this.cloudFoundryClient.spaceQuotaDefinitions()
                .create(CreateSpaceQuotaDefinitionRequest.builder()
                    .memoryLimit(512)
                    .name(quotaName)
                    .nonBasicServicesAllowed(false)
                    .organizationId(organizationId)
                    .totalRoutes(1)
                    .totalServices(1)
                    .build()))
            .map(ResourceUtils::getEntity)
            .map(SpaceQuotaDefinitionEntity::getName)
            .as(StepVerifier::create)
            .expectNext(quotaName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String quotaName = this.nameFactory.getQuotaDefinitionName();

        this.organizationId
            .flatMap(organizationId -> createSpaceQuotaDefinitionId(this.cloudFoundryClient, organizationId, quotaName))
            .flatMap(quotaId -> this.cloudFoundryClient.spaceQuotaDefinitions()
                .delete(DeleteSpaceQuotaDefinitionRequest.builder()
                    .async(true)
                    .spaceQuotaDefinitionId(quotaId)
                    .build())
                .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

    }

    @Test
    public void get() {
        String quotaName = this.nameFactory.getQuotaDefinitionName();

        this.organizationId
            .flatMap(organizationId -> createSpaceQuotaDefinitionId(this.cloudFoundryClient, organizationId, quotaName))
            .flatMap(quotaId -> this.cloudFoundryClient.spaceQuotaDefinitions()
                .get(GetSpaceQuotaDefinitionRequest.builder()
                    .spaceQuotaDefinitionId(quotaId)
                    .build()))
            .map(ResourceUtils::getEntity)
            .map(SpaceQuotaDefinitionEntity::getName)
            .as(StepVerifier::create)
            .expectNext(quotaName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws TimeoutException, InterruptedException {
        String quotaName = this.nameFactory.getQuotaDefinitionName();

        this.organizationId
            .flatMap(organizationId -> createSpaceQuotaDefinitionId(this.cloudFoundryClient, organizationId, quotaName))
            .thenMany(PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.spaceQuotaDefinitions()
                    .list(ListSpaceQuotaDefinitionsRequest.builder()
                        .page(page)
                        .build()))
                .map(ResourceUtils::getEntity))
            .filter(quota -> quotaName.equals(quota.getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpaces() {
        String quotaName = this.nameFactory.getQuotaDefinitionName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName),
                createSpaceQuotaDefinitionId(this.cloudFoundryClient, organizationId, quotaName)
            ))
            .flatMap(function((spaceId, quotaId) -> requestAssociateSpace(this.cloudFoundryClient, quotaId, spaceId)
                .then(Mono.just(quotaId))))
            .flatMapMany(quotaId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.spaceQuotaDefinitions()
                    .listSpaces(ListSpaceQuotaDefinitionSpacesRequest.builder()
                        .page(page)
                        .spaceQuotaDefinitionId(quotaId)
                        .build())))
            .map(ResourceUtils::getEntity)
            .map(SpaceEntity::getName)
            .as(StepVerifier::create)
            .expectNext(spaceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpacesFilterByApplicationId() {
        String applicationName = this.nameFactory.getApplicationName();
        String quotaName = this.nameFactory.getQuotaDefinitionName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                createSpaceQuotaDefinitionId(this.cloudFoundryClient, organizationId, quotaName),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((quotaId, spaceId) -> Mono.when(
                requestAssociateSpace(this.cloudFoundryClient, quotaId, spaceId)
                    .then(Mono.just(quotaId)),
                createApplicationId(this.cloudFoundryClient, spaceId, applicationName))
            ))
            .flatMapMany(function((quotaId, applicationId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.spaceQuotaDefinitions()
                    .listSpaces(ListSpaceQuotaDefinitionSpacesRequest.builder()
                        .applicationId(applicationId)
                        .page(page)
                        .spaceQuotaDefinitionId(quotaId)
                        .build()))))
            .map(ResourceUtils::getEntity)
            .map(SpaceEntity::getName)
            .as(StepVerifier::create)
            .expectNext(spaceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/643
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/643")
    @Test
    public void listSpacesFilterByDeveloperId() {

    }

    @Test
    public void listSpacesFilterByName() {
        String quotaName = this.nameFactory.getQuotaDefinitionName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName),
                createSpaceQuotaDefinitionId(this.cloudFoundryClient, organizationId, quotaName)
            ))
            .flatMap(function((spaceId, quotaId) -> requestAssociateSpace(this.cloudFoundryClient, quotaId, spaceId)
                .then(Mono.just(quotaId))))
            .flatMapMany(quotaId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.spaceQuotaDefinitions()
                    .listSpaces(ListSpaceQuotaDefinitionSpacesRequest.builder()
                        .name(spaceName)
                        .page(page)
                        .spaceQuotaDefinitionId(quotaId)
                        .build())))
            .map(ResourceUtils::getEntity)
            .map(SpaceEntity::getName)
            .as(StepVerifier::create)
            .expectNext(spaceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpacesFilterByOrganizationId() {
        String quotaName = this.nameFactory.getQuotaDefinitionName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName),
                createSpaceQuotaDefinitionId(this.cloudFoundryClient, organizationId, quotaName)
            ))
            .flatMap(function((organizationId, spaceId, quotaId) -> requestAssociateSpace(this.cloudFoundryClient, quotaId, spaceId)
                .then(Mono.just(Tuples.of(organizationId, quotaId)))))
            .flatMapMany(function((organizationId, quotaId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.spaceQuotaDefinitions()
                    .listSpaces(ListSpaceQuotaDefinitionSpacesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .spaceQuotaDefinitionId(quotaId)
                        .build()))))
            .map(ResourceUtils::getEntity)
            .map(SpaceEntity::getName)
            .as(StepVerifier::create)
            .expectNext(spaceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpacesNotFound() {
        String quotaName = this.nameFactory.getQuotaDefinitionName();

        this.organizationId
            .flatMap(organizationId -> createSpaceQuotaDefinitionId(this.cloudFoundryClient, organizationId, quotaName))
            .flatMapMany(quotaId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.spaceQuotaDefinitions()
                    .listSpaces(ListSpaceQuotaDefinitionSpacesRequest.builder()
                        .spaceQuotaDefinitionId(quotaId)
                        .build())))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpacesQueryBySpaceId() {
        String quotaName = this.nameFactory.getQuotaDefinitionName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName),
                createSpaceQuotaDefinitionId(this.cloudFoundryClient, organizationId, quotaName)
            ))
            .flatMap(function((spaceId, quotaId) -> requestAssociateSpace(this.cloudFoundryClient, quotaId, spaceId)
                .then(Mono.just(quotaId))))
            .flatMapMany(quotaId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.spaceQuotaDefinitions()
                    .listSpaces(ListSpaceQuotaDefinitionSpacesRequest.builder()
                        .page(page)
                        .spaceQuotaDefinitionId(quotaId)
                        .build())))
            .map(ResourceUtils::getEntity)
            .map(SpaceEntity::getName)
            .as(StepVerifier::create)
            .expectNext(spaceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeSpace() {
        String quotaName = this.nameFactory.getQuotaDefinitionName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName),
                createSpaceQuotaDefinitionId(this.cloudFoundryClient, organizationId, quotaName)
            ))
            .flatMap(function((spaceId, quotaId) -> requestAssociateSpace(this.cloudFoundryClient, quotaId, spaceId)
                .then(this.cloudFoundryClient.spaceQuotaDefinitions()
                    .removeSpace(RemoveSpaceQuotaDefinitionRequest.builder()
                        .spaceId(spaceId)
                        .spaceQuotaDefinitionId(quotaId)
                        .build()))
                .then()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String quotaName1 = this.nameFactory.getQuotaDefinitionName();
        String quotaName2 = this.nameFactory.getQuotaDefinitionName();

        this.organizationId
            .flatMap(organizationId -> createSpaceQuotaDefinitionId(this.cloudFoundryClient, organizationId, quotaName1))
            .flatMap(quotaId -> this.cloudFoundryClient.spaceQuotaDefinitions()
                .update(UpdateSpaceQuotaDefinitionRequest.builder()
                    .name(quotaName2)
                    .spaceQuotaDefinitionId(quotaId)
                    .build()))
            .thenMany(requestList(this.cloudFoundryClient)
                .map(ResourceUtils::getEntity))
            .filter(quota -> quotaName2.equals(quota.getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return cloudFoundryClient.spaces()
            .create(CreateSpaceRequest.builder()
                .organizationId(organizationId)
                .name(spaceName)
                .build())
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createSpaceQuotaDefinitionId(CloudFoundryClient cloudFoundryClient, String organizationId, String quotaName) {
        return requestCreateSpaceQuotaDefinition(cloudFoundryClient, organizationId, quotaName)
            .map(ResourceUtils::getId);
    }

    private static Mono<AssociateSpaceQuotaDefinitionResponse> requestAssociateSpace(CloudFoundryClient cloudFoundryClient, String quotaId, String spaceId) {
        return cloudFoundryClient.spaceQuotaDefinitions()
            .associateSpace(AssociateSpaceQuotaDefinitionRequest.builder()
                .spaceId(spaceId)
                .spaceQuotaDefinitionId(quotaId)
                .build());
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .buildpack("staticfile_buildpack")
                .diego(true)
                .diskQuota(512)
                .memory(64)
                .name(applicationName)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateSpaceQuotaDefinitionResponse> requestCreateSpaceQuotaDefinition(CloudFoundryClient cloudFoundryClient, String organizationId, String quotaName) {
        return cloudFoundryClient.spaceQuotaDefinitions()
            .create(CreateSpaceQuotaDefinitionRequest.builder()
                .memoryLimit(512)
                .name(quotaName)
                .nonBasicServicesAllowed(false)
                .organizationId(organizationId)
                .totalRoutes(1)
                .totalServices(1)
                .build());
    }

    private static Flux<SpaceQuotaDefinitionResource> requestList(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaceQuotaDefinitions()
                .list(ListSpaceQuotaDefinitionsRequest.builder()
                    .page(page)
                    .build()));
    }

}

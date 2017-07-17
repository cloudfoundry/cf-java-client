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
import org.cloudfoundry.client.v2.domains.CreateDomainRequest;
import org.cloudfoundry.client.v2.domains.CreateDomainResponse;
import org.cloudfoundry.client.v2.domains.DeleteDomainRequest;
import org.cloudfoundry.client.v2.domains.DeleteDomainResponse;
import org.cloudfoundry.client.v2.domains.DomainEntity;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.GetDomainResponse;
import org.cloudfoundry.client.v2.domains.ListDomainSpacesRequest;
import org.cloudfoundry.client.v2.domains.ListDomainsRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserResponse;
import org.cloudfoundry.client.v2.routes.AssociateRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.AssociateRouteApplicationResponse;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperRequest;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperResponse;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

@SuppressWarnings("deprecation")
public final class DomainsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private Mono<String> spaceId;

    @Autowired
    private Mono<String> userId;

    @Test
    public void create() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                createDomainEntity(this.cloudFoundryClient, organizationId, domainName),
                Mono.just(organizationId)
            ))
            .as(StepVerifier::create)
            .consumeNextWith(domainNameAndOrganizationIdEquality(domainName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> createDomainId(this.cloudFoundryClient, domainName, organizationId))
            .delayUntil(domainId -> requestDeleteDomain(this.cloudFoundryClient, domainId)
                .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job)))
            .flatMap(domainId -> requestGetDomain(this.cloudFoundryClient, domainId))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessageMatching("CF-DomainNotFound\\([0-9]+\\): The domain could not be found: .*"))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteNotAsync() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> createDomainId(this.cloudFoundryClient, domainName, organizationId))
            .delayUntil(domainId -> requestDeleteDomainAsyncFalse(this.cloudFoundryClient, domainId))
            .flatMap(domainId -> requestGetDomain(this.cloudFoundryClient, domainId))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessageMatching("CF-DomainNotFound\\([0-9]+\\): The domain could not be found: .*"))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                createDomainId(this.cloudFoundryClient, domainName, organizationId)
            ))
            .flatMap(function((organizationId, domainId) -> Mono.when(
                getDomainEntity(this.cloudFoundryClient, domainId),
                Mono.just(organizationId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(domainNameAndOrganizationIdEquality(domainName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                createDomainId(this.cloudFoundryClient, domainName, organizationId)
            ))
            .flatMap(function((organizationId, domainId) -> Mono.when(
                requestListDomains(this.cloudFoundryClient)
                    .filter(resource -> domainId.equals(ResourceUtils.getId(resource)))
                    .single()
                    .map(ResourceUtils::getEntity),
                Mono.just(organizationId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(domainNameAndOrganizationIdEquality(domainName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listDomainSpaces() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        Mono.when(this.organizationId, this.spaceId)
            .flatMap(function((organizationId, spaceId) -> Mono.when(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                Mono.just(spaceId)
            )))
            .flatMap(function((domainId, spaceId) -> Mono.when(
                requestListDomainSpaces(this.cloudFoundryClient, domainId)
                    .filter(resource -> spaceId.equals(ResourceUtils.getId(resource)))
                    .single()
                    .map(ResourceUtils::getId),
                Mono.just(spaceId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listDomainSpacesFilterByApplicationId() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> createDomainId(this.cloudFoundryClient, domainName, organizationId))
            .and(this.spaceId)
            .flatMap(function((domainId, spaceId) -> Mono.when(
                Mono.just(domainId),
                Mono.just(spaceId),
                getApplicationId(this.cloudFoundryClient, applicationName, spaceId),
                getRouteId(this.cloudFoundryClient, domainId, spaceId)
            )))
            .delayUntil(function((domainId, spaceId, applicationId, routeId) -> requestAssociateRouteApplication(this.cloudFoundryClient, applicationId, routeId)))
            .flatMap(function((domainId, spaceId, applicationId, routeId) -> Mono.when(
                requestListDomainSpacesByApplicationId(this.cloudFoundryClient, applicationId, domainId)
                    .filter(resource -> spaceId.equals(ResourceUtils.getId(resource)))
                    .single()
                    .map(ResourceUtils::getId),
                Mono.just(spaceId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listDomainSpacesFilterByDeveloperId() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        Mono.when(this.spaceId, this.organizationId, this.userId)
            .flatMap(function((spaceId, organizationId, userId) -> Mono.when(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                requestAssociateOrganizationUser(this.cloudFoundryClient, organizationId, userId),
                Mono.just(spaceId),
                Mono.just(userId)
            )))
            .delayUntil(function((domainId, response, spaceId, userId) -> requestAssociateSpaceDeveloper(this.cloudFoundryClient, spaceId, userId)))
            .flatMap(function((domainId, response, spaceId, userId) -> requestListSpaceDevelopers(this.cloudFoundryClient, domainId, userId)
                .filter(resource -> spaceId.equals(ResourceUtils.getId(resource)))
                .single()
                .map(ResourceUtils::getId)
                .and(Mono.just(spaceId))))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listDomainSpacesFilterByName() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        Mono.when(this.organizationId, this.spaceId)
            .flatMap(function((organizationId, spaceId) ->
                Mono.when(
                    Mono.just(spaceId),
                    getSpaceName(this.cloudFoundryClient, spaceId),
                    createDomainId(this.cloudFoundryClient, domainName, organizationId)
                )))
            .flatMap(function((spaceId, spaceName, domainId) -> Mono.when(
                requestListDomainSpacesBySpaceName(this.cloudFoundryClient, domainId, spaceName)
                    .filter(resource -> spaceId.equals(ResourceUtils.getId(resource)))
                    .single()
                    .map(ResourceUtils::getId),
                Mono.just(spaceId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listDomainSpacesFilterByOrganizationId() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        Mono
            .when(this.organizationId, this.spaceId)
            .flatMap(function((organizationId, spaceId) -> Mono.when(
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                Mono.just(organizationId),
                Mono.just(spaceId)
            )))
            .flatMap(function((domainId, organizationId, spaceId) -> Mono.when(
                requestListDomainSpacesByOrganizationId(this.cloudFoundryClient, domainId, organizationId)
                    .filter(resource -> spaceId.equals(ResourceUtils.getId(resource)))
                    .single()
                    .map(ResourceUtils::getId),
                Mono.just(spaceId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByName() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                createDomainId(this.cloudFoundryClient, domainName, organizationId)
            ))
            .flatMap(function((organizationId, domainId) -> Mono.when(
                requestListDomains(this.cloudFoundryClient, domainName)
                    .filter(resource -> domainId.equals(ResourceUtils.getId(resource)))
                    .single()
                    .map(ResourceUtils::getEntity),
                Mono.just(organizationId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(domainNameAndOrganizationIdEquality(domainName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByOwningOrganizationId() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                createDomainId(this.cloudFoundryClient, domainName, organizationId)
            ))
            .flatMap(function((organizationId, domainId) -> Mono.when(
                requestListDomainsByOwningOrganization(this.cloudFoundryClient, organizationId)
                    .filter(resource -> domainId.equals(ResourceUtils.getId(resource)))
                    .single()
                    .map(ResourceUtils::getEntity),
                Mono.just(organizationId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(domainNameAndOrganizationIdEquality(domainName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<DomainEntity> createDomainEntity(CloudFoundryClient cloudFoundryClient, String organizationId, String domainName) {
        return requestCreateDomain(cloudFoundryClient, organizationId, domainName)
            .map(ResourceUtils::getEntity);
    }

    private static Mono<String> createDomainId(CloudFoundryClient cloudFoundryClient, String domainName, String organizationId) {
        return requestCreateDomain(cloudFoundryClient, organizationId, domainName)
            .map(ResourceUtils::getId);
    }

    private static Consumer<Tuple2<DomainEntity, String>> domainNameAndOrganizationIdEquality(String domainName) {
        return consumer((entity, organizationId) -> {
            assertThat(entity.getName()).isEqualTo(domainName);
            assertThat(entity.getOwningOrganizationId()).isEqualTo(organizationId);
        });
    }

    private static Mono<String> getApplicationId(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName)
            .map(ResourceUtils::getId);
    }

    private static Mono<DomainEntity> getDomainEntity(CloudFoundryClient cloudFoundryClient, String domainId) {
        return requestGetDomain(cloudFoundryClient, domainId)
            .map(ResourceUtils::getEntity);
    }

    private static Mono<String> getRouteId(CloudFoundryClient cloudFoundryClient, String domainId, String spaceId) {
        return requestCreateRoute(cloudFoundryClient, domainId, spaceId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getSpaceName(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestSpace(cloudFoundryClient, spaceId)
            .map(ResourceUtils::getEntity)
            .map(SpaceEntity::getName);
    }

    private static Mono<AssociateOrganizationUserResponse> requestAssociateOrganizationUser(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateUser(AssociateOrganizationUserRequest.builder()
                .organizationId(organizationId)
                .userId(userId)
                .build());
    }

    private static Mono<AssociateRouteApplicationResponse> requestAssociateRouteApplication(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return cloudFoundryClient.routes()
            .associateApplication(AssociateRouteApplicationRequest.builder()
                .routeId(routeId)
                .applicationId(applicationId)
                .build());
    }

    private static Mono<AssociateSpaceDeveloperResponse> requestAssociateSpaceDeveloper(CloudFoundryClient cloudFoundryClient, String spaceId, String developerId) {
        return cloudFoundryClient.spaces()
            .associateDeveloper(AssociateSpaceDeveloperRequest.builder()
                .spaceId(spaceId)
                .developerId(developerId)
                .build());
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .name(applicationName)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateDomainResponse> requestCreateDomain(CloudFoundryClient cloudFoundryClient, String organizationId, String domainName) {
        return cloudFoundryClient.domains()
            .create(CreateDomainRequest.builder()
                .name(domainName)
                .owningOrganizationId(organizationId)
                .wildcard(true)
                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(CloudFoundryClient cloudFoundryClient, String domainId, String spaceId) {
        return cloudFoundryClient.routes()
            .create(CreateRouteRequest.builder()
                .domainId(domainId)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<DeleteDomainResponse> requestDeleteDomain(CloudFoundryClient cloudFoundryClient, String domainId) {
        return cloudFoundryClient.domains()
            .delete(DeleteDomainRequest.builder()
                .async(true)
                .domainId(domainId)
                .build());
    }

    private static Mono<DeleteDomainResponse> requestDeleteDomainAsyncFalse(CloudFoundryClient cloudFoundryClient, String domainId) {
        return cloudFoundryClient.domains()
            .delete(DeleteDomainRequest.builder()
                .async(false)
                .domainId(domainId)
                .build());
    }

    private static Mono<GetDomainResponse> requestGetDomain(CloudFoundryClient cloudFoundryClient, String domainId) {
        return cloudFoundryClient.domains()
            .get(GetDomainRequest.builder()
                .domainId(domainId)
                .build());
    }

    private static Flux<SpaceResource> requestListDomainSpaces(CloudFoundryClient cloudFoundryClient, String domainId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.domains()
                .listSpaces(ListDomainSpacesRequest.builder()
                    .domainId(domainId)
                    .page(page)
                    .build()));
    }

    private static Flux<SpaceResource> requestListDomainSpacesByApplicationId(CloudFoundryClient cloudFoundryClient, String applicationId, String domainId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.domains()
                .listSpaces(ListDomainSpacesRequest.builder()
                    .page(page)
                    .applicationId(applicationId)
                    .domainId(domainId)
                    .build())
            );
    }

    private static Flux<SpaceResource> requestListDomainSpacesByOrganizationId(CloudFoundryClient cloudFoundryClient, String domainId, String organizationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.domains()
                .listSpaces(ListDomainSpacesRequest.builder()
                    .domainId(domainId)
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Flux<SpaceResource> requestListDomainSpacesBySpaceName(CloudFoundryClient cloudFoundryClient, String domainId, String spaceName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.domains()
                .listSpaces(ListDomainSpacesRequest.builder()
                    .domainId(domainId)
                    .name(spaceName)
                    .page(page)
                    .build()));
    }

    private static Flux<DomainResource> requestListDomains(CloudFoundryClient cloudFoundryClient, String domainName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.domains()
                .list(ListDomainsRequest.builder()
                    .name(domainName)
                    .page(page)
                    .build()));
    }

    private static Flux<DomainResource> requestListDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.domains()
                .list(ListDomainsRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Flux<DomainResource> requestListDomainsByOwningOrganization(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.domains()
                .list(ListDomainsRequest.builder()
                    .owningOrganizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Flux<SpaceResource> requestListSpaceDevelopers(CloudFoundryClient cloudFoundryClient, String domainId, String userId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.domains()
                .listSpaces(ListDomainSpacesRequest.builder()
                    .page(page)
                    .developerId(userId)
                    .domainId(domainId)
                    .build())
            );
    }

    private static Mono<GetSpaceResponse> requestSpace(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return cloudFoundryClient.spaces()
            .get(GetSpaceRequest.builder()
                .spaceId(spaceId)
                .build());
    }

}


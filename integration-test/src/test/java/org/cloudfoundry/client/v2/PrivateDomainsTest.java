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
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationPrivateDomainRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationPrivateDomainResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserResponse;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v2.privatedomains.AbstractPrivateDomainResource;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.DeletePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.DeletePrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.GetPrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.GetPrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainSharedOrganizationsRequest;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsRequest;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
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

public final class PrivateDomainsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Autowired
    private Mono<String> userId;

    @Test
    public void create() throws TimeoutException, InterruptedException {
        String privateDomainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                requestCreatePrivateDomain(this.cloudFoundryClient, privateDomainName, organizationId),
                Mono.just(organizationId)
            ))
            .as(StepVerifier::create)
            .consumeNextWith(domainNameAndOrganizationIdEquality(privateDomainName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() throws TimeoutException, InterruptedException {
        String privateDomainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> requestCreatePrivateDomain(this.cloudFoundryClient, privateDomainName, organizationId))
            .flatMap(privateDomainResource -> requestDeletePrivateDomain(this.cloudFoundryClient, ResourceUtils.getId(privateDomainResource))
                .flatMap(jobResource -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), jobResource))
                .then(Mono.just(privateDomainResource)))
            .flatMap(privateDomainResource -> requestGetPrivateDomain(this.cloudFoundryClient, ResourceUtils.getId(privateDomainResource)))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessageMatching("CF-DomainNotFound\\([0-9]+\\): The domain could not be found: .*"))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws TimeoutException, InterruptedException {
        String privateDomainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                requestCreatePrivateDomain(this.cloudFoundryClient, privateDomainName, organizationId)
            ))
            .flatMap(function((organizationId, privateDomainResource) -> Mono.when(
                requestGetPrivateDomain(this.cloudFoundryClient, ResourceUtils.getId(privateDomainResource)),
                Mono.just(organizationId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(domainNameAndOrganizationIdEquality(privateDomainName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws TimeoutException, InterruptedException {
        String privateDomainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                requestCreatePrivateDomain(this.cloudFoundryClient, privateDomainName, organizationId)
            ))
            .flatMap(function((organizationId, privateDomainResource) -> Mono.when(
                listPrivateDomains(this.cloudFoundryClient)
                    .filter(resource -> ResourceUtils.getId(privateDomainResource).equals(ResourceUtils.getId(resource)))
                    .single(),
                Mono.just(organizationId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(domainNameAndOrganizationIdEquality(privateDomainName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByName() throws TimeoutException, InterruptedException {
        String privateDomainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                Mono.just(organizationId),
                requestCreatePrivateDomain(this.cloudFoundryClient, privateDomainName, organizationId)
            ))
            .flatMap(function((organizationId, privateDomainResource) -> Mono.when(
                listPrivateDomains(this.cloudFoundryClient, privateDomainName)
                    .filter(resource -> ResourceUtils.getId(privateDomainResource).equals(ResourceUtils.getId(resource)))
                    .single(),
                Mono.just(organizationId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(domainNameAndOrganizationIdEquality(privateDomainName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSharedOrganizations() throws TimeoutException, InterruptedException {
        String sharedOrganizationName = this.nameFactory.getOrganizationName();
        String privateDomainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono
                .when(
                    createPrivateDomainId(this.cloudFoundryClient, privateDomainName, organizationId),
                    createOrganizationId(this.cloudFoundryClient, sharedOrganizationName)
                ))
            .flatMap(function((domainId, organizationId) -> requestAssociateOrganizationPrivateDomain(this.cloudFoundryClient, domainId, organizationId)
                .then(Mono.just(domainId))))
            .flatMapMany(domainId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.privateDomains()
                    .listSharedOrganizations(ListPrivateDomainSharedOrganizationsRequest.builder()
                        .page(page)
                        .privateDomainId(domainId)
                        .build())))
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .filter(sharedOrganizationName::equals)
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSharedOrganizationsFilterByAuditorId() throws TimeoutException, InterruptedException {
        String sharedOrganizationName = this.nameFactory.getOrganizationName();
        String privateDomainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono
                .when(
                    createPrivateDomainId(this.cloudFoundryClient, privateDomainName, organizationId),
                    createOrganizationId(this.cloudFoundryClient, sharedOrganizationName),
                    this.userId
                ))
            .flatMap(function((domainId, organizationId, userId) -> Mono
                .when(
                    requestAssociateOrganizationPrivateDomain(this.cloudFoundryClient, domainId, organizationId)
                        .then(Mono.just(domainId)),
                    requestAssociateOrganizationAuditor(this.cloudFoundryClient, organizationId, userId)
                        .then(Mono.just(userId))
                )))
            .flatMapMany(function((domainId, userId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.privateDomains()
                    .listSharedOrganizations(ListPrivateDomainSharedOrganizationsRequest.builder()
                        .auditorId(userId)
                        .page(page)
                        .privateDomainId(domainId)
                        .build()))))
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .as(StepVerifier::create)
            .expectNext(sharedOrganizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSharedOrganizationsFilterByBillingManagerId() throws TimeoutException, InterruptedException {
        String sharedOrganizationName = this.nameFactory.getOrganizationName();
        String privateDomainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono
                .when(
                    createPrivateDomainId(this.cloudFoundryClient, privateDomainName, organizationId),
                    createOrganizationId(this.cloudFoundryClient, sharedOrganizationName),
                    this.userId
                ))
            .flatMap(function((domainId, organizationId, userId) -> Mono
                .when(
                    requestAssociateOrganizationPrivateDomain(this.cloudFoundryClient, domainId, organizationId)
                        .then(Mono.just(domainId)),
                    requestAssociateOrganizationBillingManager(this.cloudFoundryClient, organizationId, userId)
                        .then(Mono.just(userId))
                )))
            .flatMapMany(function((domainId, userId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.privateDomains()
                    .listSharedOrganizations(ListPrivateDomainSharedOrganizationsRequest.builder()
                        .billingManagerId(userId)
                        .page(page)
                        .privateDomainId(domainId)
                        .build()))))
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .as(StepVerifier::create)
            .expectNext(sharedOrganizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSharedOrganizationsFilterByManagerId() throws TimeoutException, InterruptedException {
        String sharedOrganizationName = this.nameFactory.getOrganizationName();
        String privateDomainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono
                .when(
                    createPrivateDomainId(this.cloudFoundryClient, privateDomainName, organizationId),
                    createOrganizationId(this.cloudFoundryClient, sharedOrganizationName),
                    this.userId
                ))
            .flatMap(function((domainId, organizationId, userId) -> Mono
                .when(
                    requestAssociateOrganizationPrivateDomain(this.cloudFoundryClient, domainId, organizationId)
                        .then(Mono.just(domainId)),
                    requestAssociateOrganizationManager(this.cloudFoundryClient, organizationId, userId)
                        .then(Mono.just(userId))
                )))
            .flatMapMany(function((domainId, userId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.privateDomains()
                    .listSharedOrganizations(ListPrivateDomainSharedOrganizationsRequest.builder()
                        .managerId(userId)
                        .page(page)
                        .privateDomainId(domainId)
                        .build()))))
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .as(StepVerifier::create)
            .expectNext(sharedOrganizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSharedOrganizationsFilterByName() throws TimeoutException, InterruptedException {
        String sharedOrganizationName = this.nameFactory.getOrganizationName();
        String privateDomainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono
                .when(
                    createPrivateDomainId(this.cloudFoundryClient, privateDomainName, organizationId),
                    createOrganizationId(this.cloudFoundryClient, sharedOrganizationName)
                ))
            .flatMap(function((domainId, organizationId) -> requestAssociateOrganizationPrivateDomain(this.cloudFoundryClient, domainId, organizationId)
                .then(Mono.just(domainId))))
            .flatMapMany(domainId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.privateDomains()
                    .listSharedOrganizations(ListPrivateDomainSharedOrganizationsRequest.builder()
                        .name(sharedOrganizationName)
                        .page(page)
                        .privateDomainId(domainId)
                        .build())))
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .as(StepVerifier::create)
            .expectNext(sharedOrganizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSharedOrganizationsFilterBySpaceId() throws TimeoutException, InterruptedException {
        String sharedOrganizationName = this.nameFactory.getOrganizationName();
        String privateDomainName = this.nameFactory.getDomainName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono
                .when(
                    createPrivateDomainId(this.cloudFoundryClient, privateDomainName, organizationId),
                    createOrganizationId(this.cloudFoundryClient, sharedOrganizationName)
                ))
            .flatMap(function((domainId, organizationId) -> Mono
                .when(
                    requestAssociateOrganizationPrivateDomain(this.cloudFoundryClient, domainId, organizationId)
                        .then(Mono.just(domainId)),
                    createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
                )))
            .flatMapMany(function((domainId, spaceId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.privateDomains()
                    .listSharedOrganizations(ListPrivateDomainSharedOrganizationsRequest.builder()
                        .page(page)
                        .privateDomainId(domainId)
                        .spaceId(spaceId)
                        .build()))))
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .as(StepVerifier::create)
            .expectNext(sharedOrganizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSharedOrganizationsFilterByStatus() throws TimeoutException, InterruptedException {
        String sharedOrganizationName = this.nameFactory.getOrganizationName();
        String privateDomainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono
                .when(
                    createPrivateDomainId(this.cloudFoundryClient, privateDomainName, organizationId),
                    createOrganizationId(this.cloudFoundryClient, sharedOrganizationName)
                ))
            .flatMap(function((domainId, organizationId) -> requestAssociateOrganizationPrivateDomain(this.cloudFoundryClient, domainId, organizationId)
                .then(Mono.just(domainId))))
            .flatMapMany(domainId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.privateDomains()
                    .listSharedOrganizations(ListPrivateDomainSharedOrganizationsRequest.builder()
                        .page(page)
                        .privateDomainId(domainId)
                        .status("active")
                        .build())))
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .as(StepVerifier::create)
            .expectNext(sharedOrganizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSharedOrganizationsFilterByUserId() throws TimeoutException, InterruptedException {
        String sharedOrganizationName = this.nameFactory.getOrganizationName();
        String privateDomainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono
                .when(
                    createPrivateDomainId(this.cloudFoundryClient, privateDomainName, organizationId),
                    createOrganizationId(this.cloudFoundryClient, sharedOrganizationName),
                    this.userId
                ))
            .flatMap(function((domainId, organizationId, userId) -> Mono
                .when(
                    requestAssociateOrganizationPrivateDomain(this.cloudFoundryClient, domainId, organizationId)
                        .then(Mono.just(domainId)),
                    requestAssociateOrganizationUser(this.cloudFoundryClient, organizationId, userId)
                        .then(Mono.just(userId))
                )))
            .flatMapMany(function((domainId, userId) -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.privateDomains()
                    .listSharedOrganizations(ListPrivateDomainSharedOrganizationsRequest.builder()
                        .page(page)
                        .privateDomainId(domainId)
                        .userId(userId)
                        .build()))))
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .as(StepVerifier::create)
            .expectNext(sharedOrganizationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createOrganizationId(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return requestCreateOrganization(cloudFoundryClient, organizationName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createPrivateDomainId(CloudFoundryClient cloudFoundryClient, String domainName, String organizationId) {
        return requestCreatePrivateDomain(cloudFoundryClient, domainName, organizationId)
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

    private static <R extends AbstractPrivateDomainResource> Consumer<Tuple2<R, String>> domainNameAndOrganizationIdEquality(String domainName) {
        return consumer((resource, organizationId) -> {
            assertThat(ResourceUtils.getEntity(resource).getName()).isEqualTo(domainName);
            assertThat(ResourceUtils.getEntity(resource).getOwningOrganizationId()).isEqualTo(organizationId);
        });
    }

    private static Flux<PrivateDomainResource> listPrivateDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.privateDomains()
                .list(ListPrivateDomainsRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Flux<PrivateDomainResource> listPrivateDomains(CloudFoundryClient cloudFoundryClient, String privateDomainName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.privateDomains()
                .list(ListPrivateDomainsRequest.builder()
                    .page(page)
                    .name(privateDomainName)
                    .build()));
    }

    private static Mono<AssociateOrganizationAuditorResponse> requestAssociateOrganizationAuditor(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateAuditor(AssociateOrganizationAuditorRequest.builder()
                .auditorId(userId)
                .organizationId(organizationId)
                .build());
    }

    private static Mono<AssociateOrganizationBillingManagerResponse> requestAssociateOrganizationBillingManager(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateBillingManager(AssociateOrganizationBillingManagerRequest.builder()
                .billingManagerId(userId)
                .organizationId(organizationId)
                .build());
    }

    private static Mono<AssociateOrganizationManagerResponse> requestAssociateOrganizationManager(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateManager(AssociateOrganizationManagerRequest.builder()
                .managerId(userId)
                .organizationId(organizationId)
                .build());
    }

    private static Mono<AssociateOrganizationPrivateDomainResponse> requestAssociateOrganizationPrivateDomain(CloudFoundryClient cloudFoundryClient, String domainId, String organizationId) {
        return cloudFoundryClient.organizations()
            .associatePrivateDomain(AssociateOrganizationPrivateDomainRequest.builder()
                .organizationId(organizationId)
                .privateDomainId(domainId)
                .build());
    }

    private static Mono<AssociateOrganizationUserResponse> requestAssociateOrganizationUser(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.organizations()
            .associateUser(AssociateOrganizationUserRequest.builder()
                .organizationId(organizationId)
                .userId(userId)
                .build());
    }

    private static Mono<CreateOrganizationResponse> requestCreateOrganization(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return cloudFoundryClient.organizations()
            .create(CreateOrganizationRequest.builder()
                .name(organizationName)
                .status("active")
                .build());
    }

    private static Mono<CreatePrivateDomainResponse> requestCreatePrivateDomain(CloudFoundryClient cloudFoundryClient, String domainName, String organizationId) {
        return cloudFoundryClient.privateDomains()
            .create(CreatePrivateDomainRequest.builder()
                .name(domainName)
                .owningOrganizationId(organizationId)
                .build());
    }

    private static Mono<DeletePrivateDomainResponse> requestDeletePrivateDomain(CloudFoundryClient cloudFoundryClient, String privateDomainId) {
        return cloudFoundryClient.privateDomains()
            .delete(DeletePrivateDomainRequest.builder()
                .privateDomainId(privateDomainId)
                .build());
    }

    private static Mono<GetPrivateDomainResponse> requestGetPrivateDomain(CloudFoundryClient cloudFoundryClient, String privateDomainId) {
        return cloudFoundryClient.privateDomains()
            .get(GetPrivateDomainRequest.builder()
                .privateDomainId(privateDomainId)
                .build());
    }

}


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

package org.cloudfoundry.client.v3;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.domains.CreateDomainRequest;
import org.cloudfoundry.client.v3.domains.CreateDomainResponse;
import org.cloudfoundry.client.v3.domains.DeleteDomainRequest;
import org.cloudfoundry.client.v3.domains.Domain;
import org.cloudfoundry.client.v3.domains.DomainRelationships;
import org.cloudfoundry.client.v3.domains.DomainResource;
import org.cloudfoundry.client.v3.domains.GetDomainRequest;
import org.cloudfoundry.client.v3.domains.GetDomainResponse;
import org.cloudfoundry.client.v3.domains.ListDomainsRequest;
import org.cloudfoundry.client.v3.domains.ShareDomainRequest;
import org.cloudfoundry.client.v3.domains.ShareDomainResponse;
import org.cloudfoundry.client.v3.domains.UnshareDomainRequest;
import org.cloudfoundry.client.v3.domains.UpdateDomainRequest;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v3.organizations.ListOrganizationDomainsRequest;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_1_12)
public final class DomainsTest extends AbstractIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainsTest.class);

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Test
    public void create() {
        String domainName = this.nameFactory.getDomainName();

        requestCreateDomain(this.cloudFoundryClient, domainName)
            .as(StepVerifier::create)
            .consumeNextWith(globalDomainNameEquality(domainName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void createForAnOrganization() {
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                requestCreateDomain(this.cloudFoundryClient, organizationId, domainName),
                Mono.just(organizationId)
            ))
            .as(StepVerifier::create)
            .consumeNextWith(consumer((response, organizationId) -> {
                assertThat(response.getName()).isEqualTo(domainName);
                assertThat(response.getRelationships().getOrganization().getData().getId()).isEqualTo(organizationId);
                assertThat(response.getRelationships().getSharedOrganizations().getData()).isEmpty();
            }))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String domainName = this.nameFactory.getDomainName();

        createDomainId(this.cloudFoundryClient, domainName)
            .delayUntil(domainId -> requestDeleteDomain(this.cloudFoundryClient, domainId))
            .flatMap(domainId -> requestGetDomain(this.cloudFoundryClient, domainId))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV3Exception.class).hasMessageMatching("CF-DomainNotFound\\([0-9]+\\): The domain could not be found: .*"))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String domainName = this.nameFactory.getDomainName();

        createDomainId(this.cloudFoundryClient, domainName)
            .flatMap(domainId -> requestGetDomain(this.cloudFoundryClient, domainId))
            .as(StepVerifier::create)
            .consumeNextWith(globalDomainNameEquality(domainName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String domainName = this.nameFactory.getDomainName();

        createDomainId(this.cloudFoundryClient, domainName)
            .flatMap(domainId -> requestListDomains(this.cloudFoundryClient)
                .filter(resource -> domainId.equals(resource.getId()))
                .single()
            )
            .as(StepVerifier::create)
            .consumeNextWith(globalDomainNameEquality(domainName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByName() {
        String domainName = this.nameFactory.getDomainName();

        createDomainId(this.cloudFoundryClient, domainName)
            .flatMap(domainId -> requestListDomains(this.cloudFoundryClient, domainName)
                .filter(resource -> domainId.equals(resource.getId()))
                .single()
            )
            .as(StepVerifier::create)
            .consumeNextWith(globalDomainNameEquality(domainName))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByOwningOrganizationId() {
        String domainName = this.nameFactory.getDomainName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                Mono.just(organizationId),
                createDomainId(this.cloudFoundryClient, domainName, organizationId)
            ))
            .flatMap(function((organizationId, domainId) -> Mono.zip(
                requestListDomainsByOwningOrganization(this.cloudFoundryClient, organizationId)
                    .filter(resource -> domainId.equals(resource.getId()))
                    .single(),
                Mono.just(organizationId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(consumer((response, organizationId) -> {
                assertThat(response.getName()).isEqualTo(domainName);
                assertThat(response.getRelationships().getOrganization().getData().getId()).isEqualTo(organizationId);
                assertThat(response.getRelationships().getSharedOrganizations().getData()).isEmpty();
            }))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void share() {
        String domainName = this.nameFactory.getDomainName();
        String organizationName = this.nameFactory.getOrganizationName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                Mono.just(organizationId),
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                createOrganizationId(this.cloudFoundryClient, organizationName)
            ))
            .delayUntil(function((organizationId, domainId, newOrganizationId) ->
                requestShareDomain(this.cloudFoundryClient, domainId, newOrganizationId)))
            .flatMap(function((organizationId, domainId, newOrganizationId) -> Mono.zip(
                requestListDomainsForOrganization(this.cloudFoundryClient, organizationId)
                    .filter(resource -> domainId.equals(resource.getId()))
                    .single(),
                Mono.just(organizationId),
                Mono.just(newOrganizationId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(consumer((response, organizationId, newOrganizationId) -> {
                assertThat(response.getName()).isEqualTo(domainName);
                assertThat(response.getRelationships().getOrganization().getData().getId()).isEqualTo(organizationId);
                assertThat(response.getRelationships().getSharedOrganizations().getData().get(0).getId()).isEqualTo(newOrganizationId);
            }))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void unshare() {
        String domainName = this.nameFactory.getDomainName();
        String organizationName = this.nameFactory.getOrganizationName();

        this.organizationId
            .flatMap(organizationId -> Mono.zip(
                Mono.just(organizationId),
                createDomainId(this.cloudFoundryClient, domainName, organizationId),
                createOrganizationId(this.cloudFoundryClient, organizationName)
            ))
            .doOnSuccess(T -> LOGGER.info("OrganizationId: {}, domainId: {}, newOrganizationId: {}", T.getT1(), T.getT2(), T.getT3()))
            .delayUntil(function((organizationId, domainId, newOrganizationId) ->
                requestShareDomain(this.cloudFoundryClient, domainId, newOrganizationId)))
            .delayUntil(function((organizationId, domainId, newOrganizationId) ->
                requestUnshareDomain(this.cloudFoundryClient, domainId, newOrganizationId)))
            .flatMapMany(function((organizationId, domainId, newOrganizationId) ->
                requestListDomainsForOrganization(this.cloudFoundryClient, newOrganizationId)
                    .filter(resource -> domainId.equals(resource.getId()))
            ))
            .as(StepVerifier::create)
            .expectNextCount(0)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String domainName = this.nameFactory.getDomainName();

        createDomainId(this.cloudFoundryClient, domainName)
            .flatMap(domainId -> this.cloudFoundryClient.domainsV3()
                .update(UpdateDomainRequest.builder()
                .domainId(domainId)
                .metadata(Metadata.builder()
                    .annotation("annotationKey", "annotationValue")
                    .label("labelKey", "labelValue")
                    .build())
                .build()))
            .thenMany(requestListDomains(this.cloudFoundryClient, domainName))
            .map(DomainResource::getMetadata)
            .as(StepVerifier::create)
            .consumeNextWith(metadata -> {
                assertThat(metadata.getAnnotations().get("annotationKey")).isEqualTo("annotationValue");
                assertThat(metadata.getLabels().get("labelKey")).isEqualTo("labelValue");
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createDomainId(CloudFoundryClient cloudFoundryClient, String domainName) {
        return requestCreateDomain(cloudFoundryClient, domainName)
            .map(CreateDomainResponse::getId);
    }

    private static Mono<String> createDomainId(CloudFoundryClient cloudFoundryClient, String domainName, String organizationId) {
        return requestCreateDomain(cloudFoundryClient, organizationId, domainName)
            .map(CreateDomainResponse::getId);
    }

    private static Mono<String> createOrganizationId(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return cloudFoundryClient.organizationsV3()
            .create(CreateOrganizationRequest.builder()
                .name(organizationName)
                .build())
            .map(CreateOrganizationResponse::getId);
    }

    private static Consumer<Domain> globalDomainNameEquality(String domainName) {
        return response -> {
            assertThat(response.getName()).isEqualTo(domainName);
            assertThat(response.getRelationships().getOrganization().getData()).isNull();
            assertThat(response.getRelationships().getSharedOrganizations().getData()).isEmpty();
        };
    }

    private static Mono<CreateDomainResponse> requestCreateDomain(CloudFoundryClient cloudFoundryClient, String domainName) {
        return cloudFoundryClient.domainsV3()
            .create(CreateDomainRequest.builder()
                .name(domainName)
                .internal(false)
                .build());
    }

    private static Mono<CreateDomainResponse> requestCreateDomain(CloudFoundryClient cloudFoundryClient, String organizationId, String domainName) {
        return cloudFoundryClient.domainsV3()
            .create(CreateDomainRequest.builder()
                .name(domainName)
                .relationships(DomainRelationships.builder()
                    .organization(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id(organizationId)
                            .build())
                        .build())
                    .build())
                .build());
    }

    private static Mono<String> requestDeleteDomain(CloudFoundryClient cloudFoundryClient, String domainId) {
        return cloudFoundryClient.domainsV3()
            .delete(DeleteDomainRequest.builder()
                .domainId(domainId)
                .build());
    }

    private static Mono<GetDomainResponse> requestGetDomain(CloudFoundryClient cloudFoundryClient, String domainId) {
        return cloudFoundryClient.domainsV3()
            .get(GetDomainRequest.builder()
                .domainId(domainId)
                .build());
    }

    private static Flux<DomainResource> requestListDomains(CloudFoundryClient cloudFoundryClient, String domainName) {
        return PaginationUtils
            .requestClientV3Resources(page -> cloudFoundryClient.domainsV3()
                .list(ListDomainsRequest.builder()
                    .name(domainName)
                    .page(page)
                    .build()));
    }

    private static Flux<DomainResource> requestListDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV3Resources(page -> cloudFoundryClient.domainsV3()
                .list(ListDomainsRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Flux<DomainResource> requestListDomainsByOwningOrganization(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils
            .requestClientV3Resources(page -> cloudFoundryClient.domainsV3()
                .list(ListDomainsRequest.builder()
                    .owningOrganizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Flux<DomainResource> requestListDomainsForOrganization(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils
            .requestClientV3Resources(page -> cloudFoundryClient.organizationsV3()
                .listDomains(ListOrganizationDomainsRequest.builder()
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Mono<ShareDomainResponse> requestShareDomain(CloudFoundryClient cloudFoundryClient, String domainId, String organizationId) {
        return cloudFoundryClient.domainsV3()
            .share(ShareDomainRequest.builder()
                .domainId(domainId)
                .data(Relationship.builder()
                    .id(organizationId)
                    .build())
                .build());
    }

    private static Mono<Void> requestUnshareDomain(CloudFoundryClient cloudFoundryClient, String domainId, String organizationId) {
        return cloudFoundryClient.domainsV3()
            .unshare(UnshareDomainRequest.builder()
                .domainId(domainId)
                .organizationId(organizationId)
                .build());
    }

}

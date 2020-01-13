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

package org.cloudfoundry.operations;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.v2.ClientV2Exception;
import org.cloudfoundry.operations.domains.CreateDomainRequest;
import org.cloudfoundry.operations.domains.CreateSharedDomainRequest;
import org.cloudfoundry.operations.domains.Domain;
import org.cloudfoundry.operations.domains.ShareDomainRequest;
import org.cloudfoundry.operations.domains.UnshareDomainRequest;
import org.cloudfoundry.operations.organizations.CreateOrganizationRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.operations.domains.Status.OWNED;
import static org.cloudfoundry.operations.domains.Status.SHARED;

public final class DomainsTest extends AbstractIntegrationTest {

    private static final String DEFAULT_ROUTER_GROUP = "default-tcp";

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Autowired
    private String organizationName;

    @Test
    public void createInvalidDomain() {
        this.cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain("invalid-domain")
                .organization(this.organizationName)
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessageMatching("CF-DomainInvalid\\([0-9]+\\): The domain is invalid.*"))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void createPrivate() {
        String domainName = this.nameFactory.getDomainName();

        this.cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain(domainName)
                .organization(this.organizationName)
                .build())
            .thenMany(requestListDomains(this.cloudFoundryOperations))
            .filter(domain -> domainName.equals(domain.getName()))
            .map(Domain::getStatus)
            .as(StepVerifier::create)
            .expectNext(OWNED)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void createShared() {
        String domainName = this.nameFactory.getDomainName();

        this.cloudFoundryOperations.domains()
            .createShared(CreateSharedDomainRequest.builder()
                .domain(domainName)
                .build())
            .thenMany(requestListDomains(this.cloudFoundryOperations))
            .filter(domain -> domainName.equals(domain.getName()))
            .map(Domain::getStatus)
            .as(StepVerifier::create)
            .expectNext(SHARED)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void createSharedTcp() {
        String domainName = this.nameFactory.getDomainName();

        this.cloudFoundryOperations.domains()
            .createShared(CreateSharedDomainRequest.builder()
                .domain(domainName)
                .routerGroup(DEFAULT_ROUTER_GROUP)
                .build())
            .thenMany(requestListDomains(this.cloudFoundryOperations))
            .filter(domain -> domainName.equals(domain.getName()))
            .map(Domain::getType)
            .as(StepVerifier::create)
            .expectNext("tcp")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String domainName = this.nameFactory.getDomainName();

        requestCreateDomain(this.cloudFoundryOperations, domainName, this.organizationName)
            .thenMany(this.cloudFoundryOperations.domains()
                .list()
                .filter(domain -> domainName.equals(domain.getName())))
            .map(Domain::getStatus)
            .as(StepVerifier::create)
            .expectNext(OWNED)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listRouterGroups() {
        this.cloudFoundryOperations.domains()
            .listRouterGroups()
            .filter(response -> DEFAULT_ROUTER_GROUP.equals(response.getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listTcp() {
        String domainName = this.nameFactory.getDomainName();

        requestCreateTcpDomain(this.cloudFoundryOperations, domainName)
            .thenMany(this.cloudFoundryOperations.domains()
                .list()
                .filter(domain -> domainName.equals(domain.getName())))
            .map(Domain::getType)
            .as(StepVerifier::create)
            .expectNext("tcp")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void share() {
        String domainName = this.nameFactory.getDomainName();
        String targetOrganizationName = this.nameFactory.getOrganizationName();

        requestCreateOrganization(this.cloudFoundryOperations, targetOrganizationName)
            .then(requestCreateDomain(this.cloudFoundryOperations, domainName, this.organizationName))
            .then(this.cloudFoundryOperations.domains()
                .share(ShareDomainRequest.builder()
                    .domain(domainName)
                    .organization(targetOrganizationName)
                    .build()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void unshare() {
        String domainName = this.nameFactory.getDomainName();
        String targetOrganizationName = this.nameFactory.getOrganizationName();

        requestCreateOrganization(this.cloudFoundryOperations, targetOrganizationName)
            .then(requestCreateDomain(this.cloudFoundryOperations, domainName, this.organizationName))
            .then(requestShareDomain(this.cloudFoundryOperations, targetOrganizationName, domainName))
            .then(this.cloudFoundryOperations.domains()
                .unshare(UnshareDomainRequest.builder()
                    .domain(domainName)
                    .organization(targetOrganizationName)
                    .build()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<Void> requestCreateDomain(CloudFoundryOperations cloudFoundryOperations, String domainName, String organizationName) {
        return cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain(domainName)
                .organization(organizationName)
                .build());
    }

    private static Mono<Void> requestCreateOrganization(CloudFoundryOperations cloudFoundryOperations, String name) {
        return cloudFoundryOperations.organizations()
            .create(CreateOrganizationRequest.builder()
                .organizationName(name)
                .build());
    }

    private static Mono<Void> requestCreateTcpDomain(CloudFoundryOperations cloudFoundryOperations, String domainName) {
        return cloudFoundryOperations.domains()
            .createShared(CreateSharedDomainRequest.builder()
                .domain(domainName)
                .routerGroup(DEFAULT_ROUTER_GROUP)
                .build());
    }

    private static Flux<Domain> requestListDomains(CloudFoundryOperations cloudFoundryOperations) {
        return cloudFoundryOperations.domains()
            .list();
    }

    private static Mono<Void> requestShareDomain(CloudFoundryOperations cloudFoundryOperations, String organizationName, String domainName) {
        return cloudFoundryOperations.domains()
            .share(ShareDomainRequest.builder()
                .domain(domainName)
                .organization(organizationName)
                .build());
    }

}

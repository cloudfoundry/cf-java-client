/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry.operations.domains;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationPrivateDomainRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationPrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsRequest;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsResponse;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainResponse;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsResponse;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class DefaultDomainsTest {

    private static void requestAssociateOrganizationPrivateDomain(CloudFoundryClient cloudFoundryClient, String domainId, String organizationId) {
        when(cloudFoundryClient.organizations()
            .associatePrivateDomain(AssociateOrganizationPrivateDomainRequest.builder()
                .privateDomainId(domainId)
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono.empty());
    }

    private static void requestCreatePrivateDomain(CloudFoundryClient cloudFoundryClient, String domain, String organizationId) {
        when(cloudFoundryClient.privateDomains()
            .create(CreatePrivateDomainRequest.builder()
                .name(domain)
                .owningOrganizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(fill(CreatePrivateDomainResponse.builder(), "private-domain-")
                    .build()));
    }

    private static void requestCreateSharedDomain(CloudFoundryClient cloudFoundryClient, String domain) {
        when(cloudFoundryClient.sharedDomains()
            .create(org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainRequest.builder()
                .name(domain)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateSharedDomainResponse.builder(), "shared-domain-")
                    .build()));
    }

    private static void requestListPrivateDomains(CloudFoundryClient cloudFoundryClient, String domain, String domainId) {
        when(cloudFoundryClient.privateDomains()
            .list(ListPrivateDomainsRequest.builder()
                .name(domain)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListPrivateDomainsResponse.builder())
                    .resource(PrivateDomainResource.builder()
                        .metadata(fill(Metadata.builder(), "private-domain-")
                            .id(domainId)
                            .build())
                        .build())
                    .totalPages(1)
                    .build()));
    }

    private static void requestListPrivateDomainsEmpty(CloudFoundryClient cloudFoundryClient, String domain) {
        when(cloudFoundryClient.privateDomains()
            .list(ListPrivateDomainsRequest.builder()
                .name(domain)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListPrivateDomainsResponse.builder())
                    .build()));
    }

    private static void requestOrganizations(CloudFoundryClient cloudFoundryClient, String organization) {
        when(cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .name(organization)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationsResponse.builder())
                    .resource(fill(OrganizationResource.builder(), "organization-")
                        .build())
                    .build()));
    }

    private static void requestOrganizationsEmpty(CloudFoundryClient cloudFoundryClient, String organization) {
        when(cloudFoundryClient.organizations()
            .list(ListOrganizationsRequest.builder()
                .name(organization)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListOrganizationsResponse.builder(), "organization-")
                    .build()));
    }

    private static void requestPrivateDomains(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.privateDomains()
            .list(ListPrivateDomainsRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListPrivateDomainsResponse.builder())
                    .resource(fill(PrivateDomainResource.builder(), "private-domain-")
                        .build())
                    .build()));
    }

    private static void requestPrivateDomainsEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.privateDomains()
            .list(ListPrivateDomainsRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListPrivateDomainsResponse.builder())
                    .build()));
    }

    private static void requestRemoveOrganizationPrivateDomain(CloudFoundryClient cloudFoundryClient, String domainId, String organizationId) {
        when(cloudFoundryClient.organizations()
            .removePrivateDomain(RemoveOrganizationPrivateDomainRequest.builder()
                .privateDomainId(domainId)
                .organizationId(organizationId)
                .build()))
            .thenReturn(Mono.empty());
    }

    private static void requestSharedDomains(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.sharedDomains()
            .list(ListSharedDomainsRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSharedDomainsResponse.builder())
                    .resource(fill(SharedDomainResource.builder(), "shared-domain-")
                        .build())
                    .build()));
    }

    private static void requestSharedDomainsEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.sharedDomains()
            .list(ListSharedDomainsRequest.builder()
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fill(ListSharedDomainsResponse.builder())
                    .build()));
    }

    public static final class CreateDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultDomains domains = new DefaultDomains(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestOrganizations(this.cloudFoundryClient, "test-organization");
            requestCreatePrivateDomain(this.cloudFoundryClient, "test-domain", "test-organization-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.domains
                .create(CreateDomainRequest.builder()
                    .domain("test-domain")
                    .organization("test-organization")
                    .build());
        }

    }

    public static final class CreateDomainInvalidOrganization extends AbstractOperationsApiTest<Void> {

        private final DefaultDomains domains = new DefaultDomains(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestOrganizationsEmpty(this.cloudFoundryClient, "test-organization");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Organization test-organization does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.domains
                .create(CreateDomainRequest.builder()
                    .domain("test-domain")
                    .organization("test-organization")
                    .build());
        }

    }

    public static final class CreateSharedDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultDomains domains = new DefaultDomains(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestCreateSharedDomain(this.cloudFoundryClient, "test-domain");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.domains
                .createShared(CreateSharedDomainRequest.builder()
                    .domain("test-domain")
                    .build());
        }

    }

    public static final class ListDomains extends AbstractOperationsApiTest<Domain> {

        private final DefaultDomains domains = new DefaultDomains(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestPrivateDomains(this.cloudFoundryClient);
            requestSharedDomains(this.cloudFoundryClient);
        }

        @Override
        protected void assertions(TestSubscriber<Domain> testSubscriber) {
            testSubscriber
                .assertEquals(Domain.builder()
                    .id("test-private-domain-id")
                    .name("test-private-domain-name")
                    .status(Status.OWNED)
                    .build());
            testSubscriber
                .assertEquals(Domain.builder()
                    .id("test-shared-domain-id")
                    .name("test-shared-domain-name")
                    .status(Status.SHARED)
                    .build());
        }

        @Override
        protected Publisher<Domain> invoke() {
            return this.domains
                .list();
        }

    }

    public static final class ListDomainsOnlyPrivate extends AbstractOperationsApiTest<Domain> {

        private final DefaultDomains domains = new DefaultDomains(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestPrivateDomains(this.cloudFoundryClient);
            requestSharedDomainsEmpty(this.cloudFoundryClient);
        }

        @Override
        protected void assertions(TestSubscriber<Domain> testSubscriber) {
            testSubscriber
                .assertEquals(Domain.builder()
                    .id("test-private-domain-id")
                    .name("test-private-domain-name")
                    .status(Status.OWNED)
                    .build());
        }

        @Override
        protected Publisher<Domain> invoke() {
            return this.domains
                .list();
        }

    }

    public static final class ListDomainsOnlyShared extends AbstractOperationsApiTest<Domain> {

        private final DefaultDomains domains = new DefaultDomains(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestSharedDomains(this.cloudFoundryClient);
            requestPrivateDomainsEmpty(this.cloudFoundryClient);
        }

        @Override
        protected void assertions(TestSubscriber<Domain> testSubscriber) {
            testSubscriber
                .assertEquals(Domain.builder()
                    .id("test-shared-domain-id")
                    .name("test-shared-domain-name")
                    .status(Status.SHARED)
                    .build());
        }

        @Override
        protected Publisher<Domain> invoke() {
            return this.domains
                .list();
        }

    }

    public static final class ShareDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultDomains domains = new DefaultDomains(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestListPrivateDomains(this.cloudFoundryClient, "test-domain", "test-domain-id");
            requestOrganizations(this.cloudFoundryClient, "test-organization");
            requestAssociateOrganizationPrivateDomain(this.cloudFoundryClient, "test-domain-id", "test-organization-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.domains
                .share(ShareDomainRequest.builder()
                    .domain("test-domain")
                    .organization("test-organization")
                    .build());
        }

    }

    public static final class ShareDomainSharedDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultDomains domains = new DefaultDomains(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestListPrivateDomainsEmpty(this.cloudFoundryClient, "test-domain");
            requestOrganizations(this.cloudFoundryClient, "test-organization");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber
                .assertError(IllegalArgumentException.class, "Private domain test-domain does not exist");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.domains
                .share(ShareDomainRequest.builder()
                    .domain("test-domain")
                    .organization("test-organization")
                    .build());
        }

    }

    public static final class UnshareDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultDomains domains = new DefaultDomains(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestListPrivateDomains(this.cloudFoundryClient, "test-domain", "test-domain-id");
            requestOrganizations(this.cloudFoundryClient, "test-organization");
            requestRemoveOrganizationPrivateDomain(this.cloudFoundryClient, "test-domain-id", "test-organization-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.domains
                .unshare(UnshareDomainRequest.builder()
                    .domain("test-domain")
                    .organization("test-organization")
                    .build());
        }

    }

}

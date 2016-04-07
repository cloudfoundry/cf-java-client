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
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
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
import org.cloudfoundry.util.RequestValidationException;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.cloudfoundry.util.test.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultDomainsTest {

    private static void requestCreateDomain(CloudFoundryClient cloudFoundryClient, String domain, String organizationId) {
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

    private static void requestDomains(CloudFoundryClient cloudFoundryClient) {
        requestPrivateDomains(cloudFoundryClient);
        requestSharedDomains(cloudFoundryClient);
    }

    private static void requestOrganizations(CloudFoundryClient cloudFoundryClient, String organization) {
        when(cloudFoundryClient.organizations()
            .list(fillPage(ListOrganizationsRequest.builder())
                .name(organization)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationsResponse.builder())
                    .resource(fill(OrganizationResource.builder(), "organization-")
                        .build())
                    .build()));
    }

    private static void requestOrganizationsEmpty(CloudFoundryClient cloudFoundryClient, String organization) {
        when(cloudFoundryClient.organizations()
            .list(fillPage(ListOrganizationsRequest.builder())
                .name(organization)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationsResponse.builder(), "organization-")
                    .build()));
    }

    private static void requestPrivateDomains(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.privateDomains()
            .list(fillPage(ListPrivateDomainsRequest.builder())
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListPrivateDomainsResponse.builder())
                    .resource(fill(PrivateDomainResource.builder(), "private-domain-")
                        .build())
                    .build()));
    }

    private static void requestPrivateDomainsEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.privateDomains()
            .list(fillPage(ListPrivateDomainsRequest.builder())
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListPrivateDomainsResponse.builder())
                    .build()));
    }

    private static void requestSharedDomains(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.sharedDomains()
            .list(fillPage(ListSharedDomainsRequest.builder())
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSharedDomainsResponse.builder())
                    .resource(fill(SharedDomainResource.builder(), "shared-domain-")
                        .build())
                    .build()));
    }

    private static void requestSharedDomainsEmpty(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.sharedDomains()
            .list(fillPage(ListSharedDomainsRequest.builder())
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListSharedDomainsResponse.builder())
                    .build()));
    }

    public static final class CreateDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultDomains domains = new DefaultDomains(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestOrganizations(this.cloudFoundryClient, "test-organization");
            requestCreateDomain(this.cloudFoundryClient, "test-domain", "test-organization-id");
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

    public static final class CreateSharedDomainInvalidRequest extends AbstractOperationsApiTest<Void> {

        private final DefaultDomains domains = new DefaultDomains(this.cloudFoundryClient);

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber
                .assertError(RequestValidationException.class, "Request is invalid: domain must be specified");
        }

        @Override
        protected Mono<Void> invoke() {
            return this.domains
                .createShared(CreateSharedDomainRequest.builder()
                    .build());
        }

    }

    public static final class ListDomains extends AbstractOperationsApiTest<Domain> {

        private final DefaultDomains domains = new DefaultDomains(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestDomains(this.cloudFoundryClient);
        }

        @Override
        protected void assertions(TestSubscriber<Domain> testSubscriber) {
            testSubscriber
                .assertEquals(Domain.builder()
                    .domainName("test-private-domain-name")
                    .domainId("test-private-domain-id")
                    .status("owned")
                    .build());
            testSubscriber
                .assertEquals(Domain.builder()
                    .domainName("test-shared-domain-name")
                    .domainId("test-shared-domain-id")
                    .status("shared")
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
                    .domainName("test-private-domain-name")
                    .domainId("test-private-domain-id")
                    .status("owned")
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
                    .domainName("test-shared-domain-name")
                    .domainId("test-shared-domain-id")
                    .status("shared")
                    .build());
        }

        @Override
        protected Publisher<Domain> invoke() {
            return this.domains
                .list();
        }

    }

}

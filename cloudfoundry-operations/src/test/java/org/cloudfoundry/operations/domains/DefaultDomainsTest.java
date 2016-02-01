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

import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainResponse;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.operations.util.v2.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultDomainsTest {

    public static final class CreateDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultDomains domains = new DefaultDomains(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            ListOrganizationsRequest request1 = fillPage(ListOrganizationsRequest.builder())
                    .name("test-organization")
                    .build();
            ListOrganizationsResponse response1 = fillPage(ListOrganizationsResponse.builder())
                    .resource(OrganizationResource.builder()
                            .metadata(Resource
                                    .Metadata.builder()
                                    .id("test-organization-id")
                                    .build())
                            .build())
                    .build();
            when(this.organizations.list(request1)).thenReturn(Mono.just(response1));

            CreatePrivateDomainRequest request2 = CreatePrivateDomainRequest.builder()
                    .name("test-domain-name")
                    .owningOrganizationId("test-organization-id")
                    .wildcard(true)
                    .build();

            CreatePrivateDomainResponse response2 = CreatePrivateDomainResponse.builder().build();
            when(this.privateDomains.create(request2)).thenReturn(Mono.just(response2));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            CreateDomainRequest request = CreateDomainRequest.builder()
                    .domain("test-domain-name")
                    .organization("test-organization")
                    .build();

            return this.domains.create(request);
        }

    }

    public static final class CreateDomainInvalidDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultDomains domains = new DefaultDomains(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            ListOrganizationsRequest request1 = fillPage(ListOrganizationsRequest.builder())
                    .name("test-organization")
                    .build();
            ListOrganizationsResponse response1 = fillPage(ListOrganizationsResponse.builder())
                    .resource(OrganizationResource.builder()
                            .metadata(Resource
                                    .Metadata.builder()
                                    .id("test-organization-id")
                                    .build())
                            .build())
                    .build();
            when(this.organizations.list(request1)).thenReturn(Mono.just(response1));

            CreatePrivateDomainRequest request2 = CreatePrivateDomainRequest.builder()
                    .name("test-domain-name")
                    .owningOrganizationId("test-organization-id")
                    .wildcard(true)
                    .build();

            when(this.privateDomains.create(request2)).thenThrow(new CloudFoundryException(130003, "The domain name is taken: local.micropcf.io", "CF-DomainNameTaken"));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(CloudFoundryException.class);
        }

        @Override
        protected Publisher<Void> invoke() {
            CreateDomainRequest request = CreateDomainRequest.builder()
                    .domain("test-domain-name")
                    .organization("test-organization")
                    .build();

            return this.domains.create(request);
        }

    }

    public static final class CreateDomainInvalidOrganization extends AbstractOperationsApiTest<Void> {

        private final DefaultDomains domains = new DefaultDomains(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            ListOrganizationsRequest request1 = fillPage(ListOrganizationsRequest.builder())
                    .name("test-organization")
                    .build();
            ListOrganizationsResponse response1 = fillPage(ListOrganizationsResponse.builder())
                    .build();
            when(this.organizations.list(request1)).thenReturn(Mono.just(response1));
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Publisher<Void> invoke() {
            CreateDomainRequest request = CreateDomainRequest.builder()
                    .domain("test-domain-name")
                    .organization("test-organization")
                    .build();

            return this.domains.create(request);
        }

    }

}

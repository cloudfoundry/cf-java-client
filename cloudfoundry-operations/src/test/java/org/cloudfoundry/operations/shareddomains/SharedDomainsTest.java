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

package org.cloudfoundry.operations.shareddomains;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainResponse;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Before;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class SharedDomainsTest {

    private static void requestCreateSharedDomain(CloudFoundryClient cloudFoundryClient, String domain, String routerGroupId) {
        when(cloudFoundryClient.sharedDomains()
                .create(CreateSharedDomainRequest.builder()
                        .name(domain)
                        .routerGroupId(routerGroupId)
                        .build()))
                .thenReturn(Mono
                        .just(fill(CreateSharedDomainResponse.builder(), "shared-domain-")
                                .build()));
    }



    public static final class CreateSharedDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultSharedDomains sharedDomains = new DefaultSharedDomains(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestCreateSharedDomain(this.cloudFoundryClient, "shared-domain", "random-guid");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Mono<Void> invoke() {
            return this.sharedDomains
                    .create(CreateSharedDomainRequest.builder()
                            .name("shared-domain")
                            .routerGroupId("random-guid")
                            .build());
        }

    }

}

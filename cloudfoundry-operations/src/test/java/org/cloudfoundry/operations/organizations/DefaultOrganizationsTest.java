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

package org.cloudfoundry.operations.organizations;

import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.Mono;

import static org.cloudfoundry.operations.util.v2.TestObjects.fill;
import static org.cloudfoundry.operations.util.v2.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultOrganizationsTest {

    public static final class List extends AbstractOperationsApiTest<Organization> {

        private final DefaultOrganizations organizations = new DefaultOrganizations(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            ListOrganizationsRequest request1 = fillPage(ListOrganizationsRequest.builder())
                    .page(1)
                    .build();
            ListOrganizationsResponse page1 = ListOrganizationsResponse.builder()
                    .totalPages(2)
                    .resource(fill(OrganizationResource.builder(), "org1-").build())
                    .build();
            when(this.cloudFoundryClient.organizations().list(request1)).thenReturn(Mono.just(page1));

            ListOrganizationsRequest request2 = fillPage(ListOrganizationsRequest.builder())
                    .page(2)
                    .build();
            ListOrganizationsResponse page2 = ListOrganizationsResponse.builder()
                    .totalPages(2)
                    .resource(fill(OrganizationResource.builder(), "org2-").build())
                    .build();
            when(this.cloudFoundryClient.organizations().list(request2)).thenReturn(Mono.just(page2));
        }

        @Override
        protected void assertions(TestSubscriber<Organization> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(fill(Organization.builder(), "org1-").build())
                    .assertEquals(fill(Organization.builder(), "org2-").build());
        }

        @Override
        protected Publisher<Organization> invoke() {
            return this.organizations.list();
        }

    }

}

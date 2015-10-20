/*
 * Copyright 2013-2015 the original author or authors.
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

import org.cloudfoundry.client.v2.Resource.Metadata;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse.ListOrganizationsResponseResource;
import org.junit.Test;
import reactor.Publishers;
import reactor.rx.Streams;

import java.util.Arrays;
import java.util.List;

import static org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse.ListOrganizationsResponseEntity;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public final class DefaultOrganizationsTest extends AbstractOperationsTest {

    private final DefaultOrganizations organizations = new DefaultOrganizations(this.cloudFoundryClient);

    @Test
    public void list() {
        ListOrganizationsResponse page1 = new ListOrganizationsResponse()
                .withResource(new ListOrganizationsResponseResource()
                        .withMetadata(new Metadata().withId("test-id-1"))
                        .withEntity(new ListOrganizationsResponseEntity().withName("test-name-1")))
                .withTotalPages(2);
        when(this.cloudFoundryClient.organizations().list(new ListOrganizationsRequest().withPage(1)))
                .thenReturn(Publishers.just(page1));

        ListOrganizationsResponse page2 = new ListOrganizationsResponse()
                .withResource(new ListOrganizationsResponseResource()
                        .withMetadata(new Metadata().withId("test-id-2"))
                        .withEntity(new ListOrganizationsResponseEntity().withName("test-name-2")))
                .withTotalPages(2);
        when(this.cloudFoundryClient.organizations().list(new ListOrganizationsRequest().withPage(2)))
                .thenReturn(Publishers.just(page2));

        List<Organization> expected = Arrays.asList(
                new Organization("test-id-1", "test-name-1"),
                new Organization("test-id-2", "test-name-2")
        );

        List<Organization> actual = Streams.wrap(this.organizations.list()).toList().poll();

        assertEquals(expected, actual);
    }
}

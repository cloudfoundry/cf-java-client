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
import org.junit.Test;
import reactor.Publishers;
import reactor.rx.Streams;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public final class DefaultOrganizationsTest extends AbstractOperationsTest {

    private final DefaultOrganizations organizations = new DefaultOrganizations(this.cloudFoundryClient);

    @Test
    public void list() {
        ListOrganizationsResponse page1 = ListOrganizationsResponse.builder()
                .resource(ListOrganizationsResponse.Resource.builder()
                        .metadata(Metadata.builder()
                                .id("test-id-1")
                                .build())
                        .entity(ListOrganizationsResponse.Resource.AuditorEntity.builder()
                                .name("test-name-1")
                                .build())
                        .build())
                .totalPages(2)
                .build();
        when(this.cloudFoundryClient.organizations().list(ListOrganizationsRequest.builder().page(1).build()))
                .thenReturn(Publishers.just(page1));

        ListOrganizationsResponse page2 = ListOrganizationsResponse.builder()
                .resource(ListOrganizationsResponse.Resource.builder()
                        .metadata(Metadata.builder()
                                .id("test-id-2")
                                .build())
                        .entity(ListOrganizationsResponse.Resource.AuditorEntity.builder()
                                .name("test-name-2")
                                .build())
                        .build())
                .totalPages(2)
                .build();
        when(this.cloudFoundryClient.organizations().list(ListOrganizationsRequest.builder().page(2).build()))
                .thenReturn(Publishers.just(page2));

        List<Organization> expected = Arrays.asList(
                Organization.builder().id("test-id-1").name("test-name-1").build(),
                Organization.builder().id("test-id-2").name("test-name-2").build()
        );

        List<Organization> actual = Streams.wrap(this.organizations.list()).toList().poll();

        assertEquals(expected, actual);
    }

}

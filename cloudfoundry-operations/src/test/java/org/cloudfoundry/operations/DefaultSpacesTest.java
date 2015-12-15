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
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.junit.Test;
import reactor.Publishers;
import reactor.rx.Streams;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public final class DefaultSpacesTest extends AbstractOperationsTest {

    private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, TEST_ORGANIZATION);

    private DefaultSpaces spacesNoOrganization = new DefaultSpaces(this.cloudFoundryClient, null);

    @Test
    public void list() {
        ListSpacesResponse page1 = ListSpacesResponse.builder()
                .resource(SpaceResource.builder()
                        .metadata(Metadata.builder()
                                .id("test-id-1")
                                .build())
                        .entity(SpaceEntity.builder()
                                .name("test-name-1")
                                .build())
                        .build())
                .totalPages(2)
                .build();
        when(this.cloudFoundryClient.spaces().list(
                ListSpacesRequest.builder().organizationId("test-organization-id").page(1).build()))
                .thenReturn(Publishers.just(page1));

        ListSpacesResponse page2 = ListSpacesResponse.builder()
                .resource(SpaceResource.builder()
                        .metadata(Metadata.builder()
                                .id("test-id-2")
                                .build())
                        .entity(SpaceEntity.builder()
                                .name("test-name-2")
                                .build())
                        .build())
                .totalPages(2)
                .build();
        when(this.cloudFoundryClient.spaces().list(
                ListSpacesRequest.builder().organizationId("test-organization-id").page(2).build()))
                .thenReturn(Publishers.just(page2));

        List<Space> expected = Arrays.asList(
                Space.builder().id("test-id-1").name("test-name-1").build(),
                Space.builder().id("test-id-2").name("test-name-2").build()
        );

        List<Space> actual = Streams.wrap(this.spaces.list()).toList().get();

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void listNoOrganization() {
        this.spacesNoOrganization.list();
    }

}

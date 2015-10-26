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
import org.cloudfoundry.client.v2.spaces.SpaceResource.SpaceEntity;
import org.junit.Test;
import reactor.Publishers;
import reactor.rx.Streams;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public final class DefaultSpacesTest extends AbstractOperationsTest {

    private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient,
            Optional.of("test-organization-id"));

    private DefaultSpaces spacesNoOrganization = new DefaultSpaces(this.cloudFoundryClient, Optional.empty());

    @Test
    public void list() {
        ListSpacesResponse page1 = new ListSpacesResponse()
                .withResource(new ListSpacesResponse.ListSpacesResponseResource()
                        .withMetadata(new Metadata().withId("test-id-1"))
                        .withEntity(new SpaceEntity().withName("test-name-1")))
                .withTotalPages(2);
        when(this.cloudFoundryClient.spaces().list(
                new ListSpacesRequest().withOrganizationId("test-organization-id").withPage(1)))
                .thenReturn(Publishers.just(page1));

        ListSpacesResponse page2 = new ListSpacesResponse()
                .withResource(new ListSpacesResponse.ListSpacesResponseResource()
                        .withMetadata(new Metadata().withId("test-id-2"))
                        .withEntity(new SpaceEntity().withName("test-name-2")))
                .withTotalPages(2);
        when(this.cloudFoundryClient.spaces().list(
                new ListSpacesRequest().withOrganizationId("test-organization-id").withPage(2)))
                .thenReturn(Publishers.just(page2));

        List<Space> expected = Arrays.asList(
                new Space("test-id-1", "test-name-1"),
                new Space("test-id-2", "test-name-2")
        );

        List<Space> actual = Streams.wrap(this.spaces.list()).toList().poll();

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void listNoOrganization() {
        this.spacesNoOrganization.list();
    }
}

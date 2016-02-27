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

package org.cloudfoundry.util;

import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.rx.Fluxion;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public final class PaginationUtilsTest {

    @Test
    public void pageStream() {
        List<SpaceResource> expected = Arrays.asList(testSpaceResource(1), testSpaceResource(2), testSpaceResource(3));

        List<SpaceResource> actual = PaginationUtils
            .requestPages(i -> testPaginatedResponsePublisher(i, 3))
            .flatMap(response -> Fluxion.fromIterable(response.getResources()))
            .toList().get();

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void pageStreamNoTotalPages() {
        PaginationUtils
            .requestPages(page -> Mono
                .just(ListSpacesResponse.builder()
                    .resource(testSpaceResource(0))
                    .build()))
            .toList()
            .get();
    }

    @Test
    public void resourceStream() {
        List<SpaceResource> expected = Arrays.asList(testSpaceResource(0), testSpaceResource(1), testSpaceResource(2));

        List<SpaceResource> actual = PaginationUtils
            .requestResources(i -> testPaginatedResponsePublisher(i - 1, 3)).toList().get();

        assertEquals(expected, actual);
    }

    private static Mono<ListSpacesResponse> testPaginatedResponsePublisher(int i, int totalNumber) {
        ListSpacesResponse response = ListSpacesResponse.builder()
            .totalPages(totalNumber)
            .resource(testSpaceResource(i))
            .build();

        return Mono.just(response);
    }

    private static SpaceResource testSpaceResource(int i) {
        return SpaceResource.builder()
            .metadata(SpaceResource.Metadata.builder()
                .id("test-id-" + i)
                .build())
            .entity(SpaceEntity.builder()
                .name("name-" + i)
                .build())
            .build();
    }

}

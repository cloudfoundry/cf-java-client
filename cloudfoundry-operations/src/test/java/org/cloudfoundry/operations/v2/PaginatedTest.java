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

package org.cloudfoundry.operations.v2;

import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.fn.Function;
import reactor.rx.Streams;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public final class PaginatedTest {

    @Test
    public void pageStream() {
        List<SpaceResource> expected = Arrays.asList(testSpaceResource(1), testSpaceResource(2), testSpaceResource(3));

        List<SpaceResource> actual = Paginated
                .requestPages(new Function<Integer, Publisher<ListSpacesResponse>>() {

                    @Override
                    public Publisher<ListSpacesResponse> apply(Integer i) {
                        return testPaginatedResponsePublisher(i, 3);
                    }

                })
                .flatMap(new Function<ListSpacesResponse, Publisher<? extends SpaceResource>>() {

                    @Override
                    public Publisher<? extends SpaceResource> apply(ListSpacesResponse response) {
                        return Streams.from(response.getResources());
                    }

                })
                .toList().get();

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void pageStreamNoTotalPages() {
        Paginated
                .requestPages(new Function<Integer, Publisher<ListSpacesResponse>>() {

                    @Override
                    public Publisher<ListSpacesResponse> apply(Integer page) {
                        ListSpacesResponse response = ListSpacesResponse.builder()
                                .resource(testSpaceResource(0))
                                .build();

                        return Streams.just(response);
                    }

                }).toList().get();
    }

    @Test
    public void resourceStream() {
        List<SpaceResource> expected = Arrays.asList(testSpaceResource(0), testSpaceResource(1), testSpaceResource(2));

        List<SpaceResource> actual = Paginated
                .requestResources(new Function<Integer, Publisher<ListSpacesResponse>>() {

                    @Override
                    public Publisher<ListSpacesResponse> apply(Integer i) {
                        return testPaginatedResponsePublisher(i - 1, 3);
                    }

                }).toList().get();

        assertEquals(expected, actual);
    }

    private static Publisher<ListSpacesResponse> testPaginatedResponsePublisher(int i, int totalNumber) {
        ListSpacesResponse response = ListSpacesResponse.builder()
                .totalPages(totalNumber)
                .resource(testSpaceResource(i))
                .build();

        return Streams.just(response);
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

/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.operations.v3.mapper;

import org.cloudfoundry.client.v3.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v3.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v3.spaces.SpaceResource;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.cloudfoundry.client.v3.Pagination;
import java.lang.IllegalStateException;
import org.cloudfoundry.client.v3.Link;

import reactor.core.publisher.Mono;
import java.time.Duration;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import reactor.test.StepVerifier;
import static org.cloudfoundry.operations.TestObjects.fill;
import static org.mockito.Mockito.when;

public final class SpaceIdByNameTest extends AbstractOperationsTest {

        @Before
        public void settup() {
                when(this.cloudFoundryClient.spacesV3().list(ListSpacesRequest.builder()
                        .organizationId(TEST_ORGANIZATION_ID)
                        .name(TEST_SPACE_NAME)
                        .page(1)
                        .build()))
                        .thenReturn(Mono.just(ListSpacesResponse.builder()
                                .pagination(   
                                        Pagination.builder()
                                        .totalResults(1)
                                        .totalPages(1)
                                        .first(Link.builder()
                                                .href("https://api.example.org/v3/spaces?page=1&per_page=50")
                                                .build())
                                        .last(Link.builder()
                                                .href("https://api.example.org/v3/spaces?page=1&per_page=50")
                                                .build())
                                        .build())
                                .resource(SpaceResource.builder()
                                .id(TEST_SPACE_ID)
                                .createdAt("2017-02-01T01:33:58Z")
                                .updatedAt("2017-02-01T01:33:58Z")
                                .name(TEST_SPACE_NAME)
                                .link("self", Link.builder()
                                        .href("https://api.example.org/v3/spaces/"+TEST_SPACE_ID)
                                        .build())
                                .build())
                                .build()));
        }

        @Test
        public void getSpaceIdByName() {
                MapperUtils.getSpaceIdByName(this.cloudFoundryClient, TEST_ORGANIZATION_ID, TEST_SPACE_NAME)
                                .as(StepVerifier::create)
                                .expectNext(TEST_SPACE_ID)
                                .expectComplete()
                                .verify(Duration.ofSeconds(5));
        }

}

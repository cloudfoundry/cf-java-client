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
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.Publishers;
import reactor.rx.Streams;

import static org.mockito.Mockito.when;

public final class DefaultSpacesTest {

    public static final class List extends AbstractOperationsApiTest<Space> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, Streams.just(TEST_ORGANIZATION));

        @Before
        public void setUp() throws Exception {
            ListSpacesRequest request1 = ListSpacesRequest.builder()
                    .organizationId("test-organization-id")
                    .page(1)
                    .build();
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
            when(this.cloudFoundryClient.spaces().list(request1)).thenReturn(Publishers.just(page1));

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
            ListSpacesRequest request2 = ListSpacesRequest.builder()
                    .organizationId("test-organization-id")
                    .page(2)
                    .build();
            when(this.cloudFoundryClient.spaces().list(request2)).thenReturn(Publishers.just(page2));
        }

        @Override
        protected void assertions(TestSubscriber<Space> testSubscriber) throws Exception {
            testSubscriber
                    .assertEquals(Space.builder()
                            .id("test-id-1")
                            .name("test-name-1")
                            .build())
                    .assertEquals(Space.builder()
                            .id("test-id-2")
                            .name("test-name-2")
                            .build());
        }

        @Override
        protected Publisher<Space> invoke() {
            return this.spaces.list();
        }
    }

    public static final class ListNoOrganization extends AbstractOperationsApiTest<Space> {

        private final DefaultSpaces spaces = new DefaultSpaces(this.cloudFoundryClient, MISSING_ID);

        @Override
        protected void assertions(TestSubscriber<Space> testSubscriber) throws Exception {
            testSubscriber
                    .assertError(IllegalStateException.class);
        }

        @Override
        protected Publisher<Space> invoke() {
            return this.spaces.list();
        }

    }

}

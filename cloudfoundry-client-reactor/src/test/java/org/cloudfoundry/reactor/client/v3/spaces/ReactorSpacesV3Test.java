/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.spaces;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v3.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v3.spaces.SpaceResource;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class ReactorSpacesV3Test extends AbstractClientApiTest {

    private final ReactorSpacesV3 spaces = new ReactorSpacesV3(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v3/spaces")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/spaces/GET_response.json")
                .build())
            .build());

        this.spaces
            .list(ListSpacesRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListSpacesResponse.builder()
                .pagination(Pagination.builder()
                    .first(Link.builder()
                        .href("/v3/spaces?page=1&per_page=50")
                        .build())
                    .last(Link.builder()
                        .href("/v3/spaces?page=1&per_page=50")
                        .build())
                    .totalPages(1)
                    .totalResults(2)
                    .build())
                .resource(SpaceResource.builder()
                    .createdAt("2017-02-01T01:33:58Z")
                    .id("885735b5-aea4-4cf5-8e44-961af0e41920")
                    .name("space1")
                    .updatedAt("2017-02-01T01:33:58Z")
                    .build())
                .resource(SpaceResource.builder()
                    .createdAt("2017-02-02T00:14:30Z")
                    .id("d4c91047-7b29-4fda-b7f9-04033e5c9c9f")
                    .name("space2")
                    .updatedAt("2017-02-02T00:14:30Z")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
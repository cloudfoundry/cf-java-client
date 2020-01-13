/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.reactor.client.v2.resourcematch;

import org.cloudfoundry.client.v2.resourcematch.ListMatchingResourcesRequest;
import org.cloudfoundry.client.v2.resourcematch.ListMatchingResourcesResponse;
import org.cloudfoundry.client.v2.resourcematch.Resource;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorResourceMatchTest extends AbstractClientApiTest {

    private final ReactorResourceMatch resourceMatch = new ReactorResourceMatch(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/resource_match")
                .payload("fixtures/client/v2/resource_match/PUT_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/resource_match/PUT_response.json")
                .build())
            .build());

        this.resourceMatch
            .list(ListMatchingResourcesRequest.builder()
                .resource(Resource.builder()
                    .hash("002d760bea1be268e27077412e11a320d0f164d3")
                    .size(36)
                    .build())
                .resource(Resource.builder()
                    .hash("a9993e364706816aba3e25717850c26c9cd0d89d")
                    .size(1)
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(ListMatchingResourcesResponse.builder()
                .resource(Resource.builder()
                    .hash("002d760bea1be268e27077412e11a320d0f164d3")
                    .size(36)
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}

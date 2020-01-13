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

package org.cloudfoundry.reactor.networking.v1.tags;

import org.cloudfoundry.networking.v1.tags.ListTagsRequest;
import org.cloudfoundry.networking.v1.tags.ListTagsResponse;
import org.cloudfoundry.networking.v1.tags.Tag;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.networking.AbstractNetworkingApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorTagsClientTest extends AbstractNetworkingApiTest {

    private final ReactorTags tags = new ReactorTags(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/tags")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/networking/tags/GET_response.json")
                .build())
            .build());

        this.tags
            .list(ListTagsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListTagsResponse.builder()
                .tag(Tag.builder()
                    .id("1081ceac-f5c4-47a8-95e8-88e1e302efb5")
                    .tag("0001")
                    .build())
                .tag(Tag.builder()
                    .id("308e7ef1-63f1-4a6c-978c-2e527cbb1c36")
                    .tag("0002")
                    .build())
                .tag(Tag.builder()
                    .id("38f08df0-19df-4439-b4e9-61096d4301ea")
                    .tag("0003")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}

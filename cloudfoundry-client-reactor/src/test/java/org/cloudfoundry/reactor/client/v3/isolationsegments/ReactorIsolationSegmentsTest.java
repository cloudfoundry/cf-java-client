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

package org.cloudfoundry.reactor.client.v3.isolationsegments;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.isolationsegments.CreateIsolationSegmentRequest;
import org.cloudfoundry.client.v3.isolationsegments.CreateIsolationSegmentResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;

import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;

public class ReactorIsolationSegmentsTest extends AbstractClientApiTest {

    private final ReactorIsolationSegments isolationSegments = new ReactorIsolationSegments(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/v3/isolation_segments")
                .payload("fixtures/client/v3/isolationsegments/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v3/isolationsegments/POST_response.json")
                .build())
            .build());

        this.isolationSegments
            .create(CreateIsolationSegmentRequest.builder()
                .name("my_segment")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateIsolationSegmentResponse.builder()
                .createdAt("2016-10-19T20:25:04Z")
                .id("b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                .link("self", Link.builder()
                    .href("/v3/isolation_segments/b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                    .build())
                .link("organizations", Link.builder()
                    .href("/v3/isolation_segments/b19f6525-cbd3-4155-b156-dc0c2a431b4c/organizations")
                    .build())
                .name("an_isolation_segment")
                .updatedAt("2016-11-08T16:41:26Z")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}

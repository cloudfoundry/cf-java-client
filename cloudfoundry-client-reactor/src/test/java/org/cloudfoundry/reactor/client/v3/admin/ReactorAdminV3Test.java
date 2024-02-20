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

package org.cloudfoundry.reactor.client.v3.admin;

import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;

import java.time.Duration;
import java.util.Collections;
import org.cloudfoundry.client.v3.admin.ClearBuildpackCacheRequest;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

final class ReactorAdminV3Test extends AbstractClientApiTest {

    private final ReactorAdminV3 admin =
            new ReactorAdminV3(
                    CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    void clearBuildpackCache() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(POST)
                                        .path("/admin/actions/clear_buildpack_cache")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(ACCEPTED)
                                        .header(
                                                "Location",
                                                "https://api.example.org/v3/jobs/[guid]")
                                        .build())
                        .build());

        this.admin
                .clearBuildpackCache(ClearBuildpackCacheRequest.builder().build())
                .as(StepVerifier::create)
                .expectNext("[guid]")
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }
}

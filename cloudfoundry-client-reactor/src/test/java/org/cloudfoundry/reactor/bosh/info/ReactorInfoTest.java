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

package org.cloudfoundry.reactor.bosh.info;

import org.cloudfoundry.bosh.info.GetInfoRequest;
import org.cloudfoundry.bosh.info.GetInfoResponse;
import org.cloudfoundry.bosh.info.Type;
import org.cloudfoundry.bosh.info.UserAuthentication;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.bosh.AbstractBoshApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorInfoTest extends AbstractBoshApiTest {

    private final ReactorInfo info = new ReactorInfo(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

    @Test
    public void info() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/info")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/bosh/info/GET_response.json")
                .build())
            .build());

        this.info
            .get(GetInfoRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(GetInfoResponse.builder()
                .name("Bosh Lite Director")
                .uuid("2daf673a-9755-4b4f-aa6d-3632fbed8012")
                .version("1.3126.0 (00000000)")
                .cpi("warden_cpi")
                .userAuthentication(UserAuthentication.builder()
                    .type(Type.UAA)
                    .option("url", "https://10.194.46.3:8443")
                    .option("urls", Collections.singletonList("https://10.194.46.3:8443"))
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }
}

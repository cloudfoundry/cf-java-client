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

package org.cloudfoundry.reactor.client.v2.info;

import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.client.v2.info.GetInfoResponse;
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

public final class ReactorInfoTest extends AbstractClientApiTest {

    private final ReactorInfo info = new ReactorInfo(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/info")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/info/GET_response.json")
                .build())
            .build());

        this.info
            .get(GetInfoRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(GetInfoResponse.builder()
                .name("vcap")
                .buildNumber("2222")
                .support("http://support.cloudfoundry.com")
                .version(2)
                .description("Cloud Foundry sponsored by Pivotal")
                .authorizationEndpoint("http://localhost:8080/uaa")
                .tokenEndpoint("http://localhost:8080/uaa")
                .apiVersion("2.44.0")
                .applicationSshEndpoint("ssh.system.domain.example.com:2222")
                .applicationSshHostKeyFingerprint("47:0d:d1:c8:c3:3d:0a:36:d1:49:2f:f2:90:27:31:d0")
                .routingEndpoint("http://localhost:3000")
                .loggingEndpoint("ws://loggregator.vcap.me:80")
                .dopplerLoggingEndpoint("ws://doppler.vcap.me:80")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}

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

package org.cloudfoundry.reactor.client.v3.info;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.time.Duration;
import java.util.Collections;
import org.cloudfoundry.client.v3.info.GetInfoRequestV3;
import org.cloudfoundry.client.v3.info.GetInfoResponseV3;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

final class ReactorInfoV3Test extends AbstractClientApiTest {

    private final ReactorInfoV3 info =
            new ReactorInfoV3(
                    CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    void get() {
        mockRequest(
                InteractionContext.builder()
                        .request(TestRequest.builder().method(GET).path("/info").build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload("fixtures/client/v3/info/GET_response.json")
                                        .build())
                        .build());

        this.info
                .get(GetInfoRequestV3.builder().build())
                .as(StepVerifier::create)
                .expectNext(
                        GetInfoResponseV3.builder()
                                .name("cf-deployment")
                                .buildNumber("v40.18.0")
                                .support("http://support.cloudfoundry.com")
                                .version(40)
                                .description("SAP BTP Cloud Foundry environment")
                                .minCliVersion("8.0.0")
                                .minRecommendedCliVersion("")
                                .self("https://api.cf.lod-cfcli3.cfrt-sof.sapcloud.io/v3/info")
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5000));
    }
}

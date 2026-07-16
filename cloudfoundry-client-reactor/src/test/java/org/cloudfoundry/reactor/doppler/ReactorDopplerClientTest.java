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

package org.cloudfoundry.reactor.doppler;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.time.Duration;
import java.util.Collections;
import org.cloudfoundry.doppler.ContainerMetric;
import org.cloudfoundry.doppler.ContainerMetricsRequest;
import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.doppler.EventType;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

final class ReactorDopplerClientTest extends AbstractDopplerApiTest {

    private final ReactorDopplerEndpoints dopplerEndpoints =
            new ReactorDopplerEndpoints(
                    CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    void containerMetrics() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(GET)
                                        .path("/apps/test-application-id/containermetrics")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .contentType(
                                                "multipart/x-protobuf;"
                                                    + " boundary=30662872b152b6fbeb87658af504679def2b6680145265ad354761ea7acf")
                                        .payload(
                                                "fixtures/doppler/apps/GET_{id}_containermetrics_response.bin")
                                        .build())
                        .build());

        this.dopplerEndpoints
                .containerMetrics(
                        ContainerMetricsRequest.builder()
                                .applicationId("test-application-id")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        Envelope.builder()
                                .containerMetric(
                                        ContainerMetric.builder()
                                                .applicationId(
                                                        "1a95eadc-95c6-4675-aa07-8c02f80ea8a4")
                                                .cpuPercentage(0.09530591690894699)
                                                .diskBytes(154005504L)
                                                .instanceIndex(2)
                                                .memoryBytes(385896448L)
                                                .build())
                                .deployment("cf-cfapps-io2-diego")
                                .eventType(EventType.CONTAINER_METRIC)
                                .index("17")
                                .ip("10.10.115.52")
                                .job("cell_z2")
                                .origin("rep")
                                .timestamp(1460991824620929073L)
                                .build(),
                        Envelope.builder()
                                .containerMetric(
                                        ContainerMetric.builder()
                                                .applicationId(
                                                        "1a95eadc-95c6-4675-aa07-8c02f80ea8a4")
                                                .cpuPercentage(0.070504789909887)
                                                .diskBytes(154005504L)
                                                .instanceIndex(0)
                                                .memoryBytes(371363840L)
                                                .build())
                                .deployment("cf-cfapps-io2-diego")
                                .eventType(EventType.CONTAINER_METRIC)
                                .index("55")
                                .ip("10.10.115.90")
                                .job("cell_z2")
                                .origin("rep")
                                .timestamp(1460991826249611682L)
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void containerMetricsLarge() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(GET)
                                        .path("/apps/test-application-id/containermetrics")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .contentType(
                                                "multipart/x-protobuf;"
                                                    + " boundary=d12911a0934bf75879de385a042c4037fa903841921ba84abb77cb73a444")
                                        .payload(
                                                "fixtures/doppler/apps/GET_{id}_containermetrics_response-large.bin")
                                        .build())
                        .build());

        this.dopplerEndpoints
                .containerMetrics(
                        ContainerMetricsRequest.builder()
                                .applicationId("test-application-id")
                                .build())
                .as(StepVerifier::create)
                .expectNextCount(3093)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }
}

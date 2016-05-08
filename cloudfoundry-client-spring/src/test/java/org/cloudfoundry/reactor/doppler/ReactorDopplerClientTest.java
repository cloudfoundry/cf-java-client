/*
 * Copyright 2013-2016 the original author or authors.
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

import org.cloudfoundry.doppler.ContainerMetric;
import org.cloudfoundry.doppler.ContainerMetricsRequest;
import org.cloudfoundry.doppler.LogMessage;
import org.cloudfoundry.doppler.LogMessage.MessageType;
import org.cloudfoundry.doppler.RecentLogsRequest;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorDopplerClientTest {

    public static final class ContainerMetrics extends AbstractDopplerApiTest<ContainerMetricsRequest, ContainerMetric> {

        private final ReactorDopplerClient dopplerClient = new ReactorDopplerClient(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/apps/test-application-id/containermetrics")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .contentType("multipart/x-protobuf; boundary=30662872b152b6fbeb87658af504679def2b6680145265ad354761ea7acf")
                    .payload("fixtures/doppler/apps/GET_{id}_containermetrics_response.bin")
                    .build())
                .build();
        }

        @Override
        protected ContainerMetricsRequest getInvalidRequest() {
            return ContainerMetricsRequest.builder()
                .build();
        }

        @Override
        protected ContainerMetric getResponse() {
            return null;
        }

        @Override
        protected Flux<ContainerMetric> getResponses() {
            return Flux.just(
                ContainerMetric.builder()
                    .applicationId("1a95eadc-95c6-4675-aa07-8c02f80ea8a4")
                    .cpuPercentage(0.09530591690894699)
                    .diskBytes(154005504L)
                    .instanceIndex(2)
                    .memoryBytes(385896448L)
                    .build(),
                ContainerMetric.builder()
                    .applicationId("1a95eadc-95c6-4675-aa07-8c02f80ea8a4")
                    .cpuPercentage(0.070504789909887)
                    .diskBytes(154005504L)
                    .instanceIndex(0)
                    .memoryBytes(371363840L)
                    .build());
        }

        @Override
        protected ContainerMetricsRequest getValidRequest() throws Exception {
            return ContainerMetricsRequest.builder()
                .applicationId("test-application-id")
                .build();
        }

        @Override
        protected Flux<ContainerMetric> invoke(ContainerMetricsRequest request) {
            return this.dopplerClient.containerMetrics(request);
        }

    }

    public static final class RecentLogs extends AbstractDopplerApiTest<RecentLogsRequest, LogMessage> {

        private final ReactorDopplerClient dopplerClient = new ReactorDopplerClient(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/apps/test-application-id/recentlogs")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .contentType("multipart/x-protobuf; boundary=92d42123ec83c0af6a27ba0de34528b702a53e2e67ba99636286b6a4cafb")
                    .payload("fixtures/doppler/apps/GET_{id}_recentlogs_response.bin")
                    .build())
                .build();
        }

        @Override
        protected RecentLogsRequest getInvalidRequest() {
            return RecentLogsRequest.builder()
                .build();
        }

        @Override
        protected LogMessage getResponse() {
            return null;
        }

        @Override
        protected Publisher<LogMessage> getResponses() {
            return Flux.just(
                LogMessage.builder()
                    .applicationId("1a95eadc-95c6-4675-aa07-8c02f80ea8a4")
                    .message("2016-04-21 22:36:28.035  INFO 24 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Located managed bean 'rabbitConnectionFactory': registering with JMX " +
                        "server as MBean [org.springframework.amqp.rabbit.connection:name=rabbitConnectionFactory,type=CachingConnectionFactory]")
                    .messageType(MessageType.OUT)
                    .sourceInstance("0")
                    .sourceType("APP")
                    .timestamp(1461278188035928339L)
                    .build(),
                LogMessage.builder()
                    .applicationId("1a95eadc-95c6-4675-aa07-8c02f80ea8a4")
                    .message("Container became healthy")
                    .messageType(MessageType.OUT)
                    .sourceInstance("0")
                    .sourceType("CELL")
                    .timestamp(1461278188715651492L)
                    .build());
        }

        @Override
        protected RecentLogsRequest getValidRequest() throws Exception {
            return RecentLogsRequest.builder()
                .applicationId("test-application-id")
                .build();
        }

        @Override
        protected Publisher<LogMessage> invoke(RecentLogsRequest request) {
            return this.dopplerClient.recentLogs(request);
        }

    }

}

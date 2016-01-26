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

package org.cloudfoundry.client.spring;

import org.cloudfoundry.client.loggregator.LoggregatorMessage;
import org.cloudfoundry.client.loggregator.RecentLogsRequest;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Ignore;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.WebSocketContainer;

import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringLoggregatorClientTest {

    @Ignore("TODO: re-instate when reactor-core is fixed.")
    public static final class Recent extends AbstractApiTest<RecentLogsRequest, LoggregatorMessage> {

        private static final MediaType MEDIA_TYPE = MediaType.parseMediaType("multipart/x-protobuf; boundary=90ad9060c87222ee30ddcffe751393a7c5734c48e070a623121abf82eb3c");

        private final ClientEndpointConfig clientEndpointConfig = mock(ClientEndpointConfig.class, RETURNS_SMART_NULLS);

        private final WebSocketContainer webSocketContainer = mock(WebSocketContainer.class, RETURNS_SMART_NULLS);

        private final SpringLoggregatorClient client = new SpringLoggregatorClient(this.clientEndpointConfig, this.webSocketContainer, this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected void assertions(TestSubscriber<LoggregatorMessage> testSubscriber, LoggregatorMessage expected) {
            testSubscriber
                    .assertCount(14);
        }

        @Override
        protected RecentLogsRequest getInvalidRequest() {
            return RecentLogsRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/recent?app=test-application-id")
                    .status(OK)
                    .contentType(MEDIA_TYPE).responsePayload("loggregator_response.bin");
        }

        @Override
        protected LoggregatorMessage getResponse() {
            return null;
        }

        @Override
        protected RecentLogsRequest getValidRequest() throws Exception {
            return RecentLogsRequest.builder()
                    .applicationId("test-application-id")
                    .build();
        }

        @Override
        protected Publisher<LoggregatorMessage> invoke(RecentLogsRequest request) {
            return this.client.recent(request);
        }

    }

}

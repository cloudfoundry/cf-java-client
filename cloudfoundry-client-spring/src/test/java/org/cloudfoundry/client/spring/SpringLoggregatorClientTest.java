/*
 * Copyright 2013-2015 the original author or authors.
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

import org.cloudfoundry.client.RequestValidationException;
import org.cloudfoundry.client.loggregator.RecentLogsRequest;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import reactor.rx.Streams;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.WebSocketContainer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

public final class SpringLoggregatorClientTest extends AbstractRestTest {

    private static final MediaType MEDIA_TYPE = MediaType.parseMediaType("multipart/x-protobuf; " +
            "boundary=90ad9060c87222ee30ddcffe751393a7c5734c48e070a623121abf82eb3c");

    private final ClientEndpointConfig clientEndpointConfig = mock(ClientEndpointConfig.class);

    private final WebSocketContainer webSocketContainer = mock(WebSocketContainer.class);

    private final SpringLoggregatorClient client = new SpringLoggregatorClient(this.clientEndpointConfig,
            this.webSocketContainer, this.restTemplate, this.root);

    @Test
    public void recent() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/recent?app=test-id"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("loggregator_response.bin"))
                        .contentType(MEDIA_TYPE));

        RecentLogsRequest request = new RecentLogsRequest()
                .withId("test-id");

        Long count = Streams.wrap(this.client.recent(request)).count().next().get();

        assertEquals(Long.valueOf(14), count);
        this.mockServer.verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void recentError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/recent?app=test-id"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        RecentLogsRequest request = new RecentLogsRequest()
                .withId("test-id");

        Streams.wrap(this.client.recent(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void recentInvalidRequest() {
        Streams.wrap(this.client.recent(new RecentLogsRequest())).next().get();
    }
}

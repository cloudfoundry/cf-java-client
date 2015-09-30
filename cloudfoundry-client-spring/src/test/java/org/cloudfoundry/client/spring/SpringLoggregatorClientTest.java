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

import org.cloudfoundry.client.loggregator.RecentLogsRequest;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
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
                        .contentType(APPLICATION_JSON));

        RecentLogsRequest request = new RecentLogsRequest()
                .withId("test-id");

        Streams.wrap(this.client.recent(request))
                .count()
                .consume(i -> {
                    assertEquals(Long.valueOf(14), i);
                    this.mockServer.verify();
                });
    }

    @Test
    public void recentError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/recent?app=test-id"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        RecentLogsRequest request = new RecentLogsRequest()
                .withId("test-id");

        this.client.recent(request).subscribe(new ExpectedExceptionSubscriber());
    }

    @Test
    public void recentInvalidRequest() {
        this.client.recent(new RecentLogsRequest()).subscribe(new ExpectedExceptionSubscriber());
    }
}

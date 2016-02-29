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

package org.cloudfoundry.spring.logging;

import org.cloudfoundry.logging.LogMessage;
import org.cloudfoundry.logging.RecentLogsRequest;
import org.cloudfoundry.spring.AbstractRestTest;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Test;
import org.springframework.web.client.RestOperations;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class SpringLoggingClientTest extends AbstractRestTest {

    private final ClientEndpointConfig clientEndpointConfig = mock(ClientEndpointConfig.class, RETURNS_SMART_NULLS);

    private final RestOperations restOperations = mock(RestOperations.class, RETURNS_SMART_NULLS);

    private final WebSocketContainer webSocketContainer = mock(WebSocketContainer.class, RETURNS_SMART_NULLS);

    private final SpringLoggingClient client = new SpringLoggingClient(this.restOperations, URI.create("https://recent.root"), URI.create("https://stream.root"), this.webSocketContainer, this
        .clientEndpointConfig, PROCESSOR_GROUP);

    @Test
    public void recent() throws InterruptedException {
        when(this.restOperations.getForObject(URI.create("https://recent.root/recent?app=test-application-id"), List.class)).thenReturn(Collections.singletonList(LogMessage.builder().build()));
        TestSubscriber<LogMessage> testSubscriber = new TestSubscriber<>();

        this.client
            .recent(RecentLogsRequest.builder()
                .applicationId("test-application-id")
                .build())
            .subscribe(testSubscriber
                .assertCount(1));

        testSubscriber.verify(5, SECONDS);
    }

}

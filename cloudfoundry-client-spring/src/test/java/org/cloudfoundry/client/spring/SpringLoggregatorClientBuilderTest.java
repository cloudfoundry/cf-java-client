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

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;

import javax.websocket.WebSocketContainer;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public final class SpringLoggregatorClientBuilderTest extends AbstractRestTest {

    private final SpringCloudFoundryClient cloudFoundryClient = new SpringCloudFoundryClient(
            this.restTemplate, this.root);

    private final WebSocketContainer webSocketContainer = mock(WebSocketContainer.class);

    private final SpringLoggregatorClientBuilder builder = new SpringLoggregatorClientBuilder(this.webSocketContainer);

    @Test
    public void test() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v2/info"))
                .andRespond(withSuccess(new ClassPathResource("v2/info/GET_response.json"),
                        MediaType.APPLICATION_JSON));

        this.builder
                .withCloudFoundryClient(this.cloudFoundryClient)
                .build();

        this.mockServer.verify();
    }

    @Test
    public void defaultConstructor() {
        new SpringLoggregatorClientBuilder();
    }
}

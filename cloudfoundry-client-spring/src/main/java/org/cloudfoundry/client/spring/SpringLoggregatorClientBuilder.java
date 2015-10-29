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

import org.cloudfoundry.client.LoggregatorClient;
import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.rx.Streams;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.net.URI;

/**
 * A builder API for creating a Spring-backed implementation of the {@link LoggregatorClient}
 */
public final class SpringLoggregatorClientBuilder {

    private final WebSocketContainer webSocketContainer;

    private volatile SpringCloudFoundryClient cloudFoundryClient;

    public SpringLoggregatorClientBuilder() {
        this(ContainerProvider.getWebSocketContainer());
    }

    SpringLoggregatorClientBuilder(WebSocketContainer webSocketContainer) {
        this.webSocketContainer = webSocketContainer;
    }

    public SpringLoggregatorClientBuilder cloudFoundryClient(SpringCloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
        return this;
    }

    /**
     * Builds a new instance of a Spring-backed implementation of {@link LoggregatorClient} using the information
     * provided
     *
     * @return a new instance of a Spring-backed implementation of the {@link LoggregatorClient}
     * @throws IllegalArgumentException if {@code cloudFoundryClient} has not been set
     */
    public SpringLoggregatorClient build() {
        Assert.notNull(this.cloudFoundryClient, "cloudFoundryClient must be set");

        URI root = Streams.wrap(this.cloudFoundryClient.info().get())
                .map(GetInfoResponse::getLoggingEndpoint)
                .map(loggingEndpoint -> UriComponentsBuilder.fromUriString(loggingEndpoint).build().toUri())
                .next().poll();

        RestOperations restOperations = this.cloudFoundryClient.getRestOperations();
        return new SpringLoggregatorClient(getClientEndpointConfig(), this.webSocketContainer, restOperations, root);
    }

    private ClientEndpointConfig getClientEndpointConfig() {
        ClientEndpointConfig.Configurator configurator = new AuthorizationConfigurator(this.cloudFoundryClient);
        return ClientEndpointConfig.Builder.create().configurator(configurator).build();
    }

}

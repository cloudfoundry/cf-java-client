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

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import org.cloudfoundry.client.LoggregatorClient;
import org.cloudfoundry.client.loggregator.LoggregatorMessage;
import org.cloudfoundry.client.loggregator.RecentLogsRequest;
import org.cloudfoundry.client.loggregator.StreamLogsRequest;
import org.cloudfoundry.client.spring.loggregator.SpringRecent;
import org.cloudfoundry.client.spring.loggregator.SpringStream;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.reactivestreams.Publisher;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SchedulerGroup;
import reactor.core.util.PlatformDependent;
import reactor.fn.Function;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.net.URI;

/**
 * The Spring-based implementation of {@link LoggregatorClient}
 */
@ToString(callSuper = true)
public final class SpringLoggregatorClient implements LoggregatorClient {

    private final SpringRecent recent;

    private final SpringStream stream;

    @Builder
    SpringLoggregatorClient(@NonNull SpringCloudFoundryClient cloudFoundryClient) {
        this(cloudFoundryClient, ContainerProvider.getWebSocketContainer());
    }

    SpringLoggregatorClient(SpringCloudFoundryClient cloudFoundryClient, WebSocketContainer webSocketContainer) {
        SchedulerGroup schedulerGroup = createSchedulerGroup();

        URI cloudFoundryRoot = getRoot(cloudFoundryClient);
        URI loggregatorRoot = UriComponentsBuilder.fromUri(cloudFoundryRoot).scheme("wss").build().toUri();

        this.recent = new SpringRecent(getRestOperations(cloudFoundryClient), cloudFoundryRoot, schedulerGroup);
        this.stream = new SpringStream(getClientEndpointConfig(cloudFoundryClient), loggregatorRoot, schedulerGroup, webSocketContainer);
    }

    SpringLoggregatorClient(ClientEndpointConfig clientEndpointConfig, WebSocketContainer webSocketContainer, RestOperations restOperations, URI root, SchedulerGroup schedulerGroup) {
        this.recent = new SpringRecent(restOperations, root, schedulerGroup);
        this.stream = new SpringStream(clientEndpointConfig, root, schedulerGroup, webSocketContainer);
    }

    @Override
    public Publisher<LoggregatorMessage> recent(RecentLogsRequest request) {
        return this.recent.recent(request);
    }

    @Override
    public Publisher<LoggregatorMessage> stream(StreamLogsRequest request) {
        return this.stream.stream(request);
    }

    private static SchedulerGroup createSchedulerGroup() {
        return SchedulerGroup.io("loggregator", PlatformDependent.MEDIUM_BUFFER_SIZE, SchedulerGroup.DEFAULT_POOL_SIZE, false);
    }

    private static ClientEndpointConfig getClientEndpointConfig(SpringCloudFoundryClient cloudFoundryClient) {
        ClientEndpointConfig.Configurator configurator = new AuthorizationConfigurator(cloudFoundryClient);
        return ClientEndpointConfig.Builder.create().configurator(configurator).build();
    }

    private static RestOperations getRestOperations(SpringCloudFoundryClient cloudFoundryClient) {
        return cloudFoundryClient.getRestOperations();
    }

    private static URI getRoot(SpringCloudFoundryClient cloudFoundryClient) {
        return requestInfo(cloudFoundryClient)
            .map(new Function<GetInfoResponse, String>() {

                @Override
                public String apply(GetInfoResponse getInfoResponse) {
                    return getInfoResponse.getLoggingEndpoint();
                }

            })
            .map(new Function<String, URI>() {

                @Override
                public URI apply(String loggingEndpoint) {
                    return UriComponentsBuilder.fromUriString(loggingEndpoint).scheme("https").build().toUri();
                }

            })
            .get();
    }

    private static Mono<GetInfoResponse> requestInfo(SpringCloudFoundryClient cloudFoundryClient) {
        return cloudFoundryClient.info()
            .get(GetInfoRequest.builder()
                .build());
    }

}

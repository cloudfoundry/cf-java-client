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

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.spring.client.SpringCloudFoundryClient;
import org.cloudfoundry.logging.LoggingClient;
import org.cloudfoundry.logging.LogMessage;
import org.cloudfoundry.logging.RecentLogsRequest;
import org.cloudfoundry.logging.StreamLogsRequest;
import org.cloudfoundry.spring.util.SchedulerGroupBuilder;
import org.cloudfoundry.spring.util.network.AuthorizationConfigurator;
import org.cloudfoundry.spring.util.network.ConnectionContext;
import org.cloudfoundry.spring.util.network.FallbackHttpMessageConverter;
import org.cloudfoundry.spring.util.network.OAuth2RestOperationsOAuth2TokenProvider;
import org.cloudfoundry.spring.util.network.OAuth2RestTemplateBuilder;
import org.cloudfoundry.spring.util.network.SslCertificateTruster;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.reactivestreams.Publisher;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SchedulerGroup;
import reactor.fn.Function;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.net.URI;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * The Spring-based implementation of {@link LoggingClient}
 */
@ToString
public final class SpringLoggingClient implements LoggingClient {

    private final SpringRecent recent;

    private final SpringStream stream;

    @Builder
    SpringLoggingClient(@NonNull SpringCloudFoundryClient cloudFoundryClient) {
        this(cloudFoundryClient.getConnectionContext(), getRestOperations(cloudFoundryClient.getConnectionContext()), ContainerProvider.getWebSocketContainer(), getSchedulerGroup());
    }

    SpringLoggingClient(RestOperations restOperations, URI recentRoot, URI streamRoot, WebSocketContainer webSocketContainer, ClientEndpointConfig clientEndpointConfig,
                        SchedulerGroup schedulerGroup) {

        this.recent = new SpringRecent(restOperations, recentRoot, schedulerGroup);
        this.stream = new SpringStream(webSocketContainer, clientEndpointConfig, streamRoot, schedulerGroup);
    }

    // Let's take a moment to reflect on the fact that this bridge constructor is needed to counter a useless compiler constraint
    private SpringLoggingClient(ConnectionContext connectionContext, OAuth2RestOperations restOperations, WebSocketContainer webSocketContainer, SchedulerGroup schedulerGroup) {
        this(connectionContext, restOperations, getLoggingEndpoint(connectionContext.getCloudFoundryClient()), webSocketContainer, schedulerGroup);
    }

    // Let's take a moment to reflect on the fact that this bridge constructor is needed to counter a useless compiler constraint
    private SpringLoggingClient(ConnectionContext connectionContext, OAuth2RestOperations restOperations, URI loggingEndpoint, WebSocketContainer webSocketContainer, SchedulerGroup schedulerGroup) {
        this(restOperations, getRecentRoot(loggingEndpoint, connectionContext.getSslCertificateTruster()),
            getStreamRoot(loggingEndpoint, connectionContext.getSslCertificateTruster()), webSocketContainer, getClientEndpointConfig(restOperations), schedulerGroup);
    }

    @Override
    public Publisher<LogMessage> recent(RecentLogsRequest request) {
        return this.recent.recent(request);
    }

    @Override
    public Publisher<LogMessage> stream(StreamLogsRequest request) {
        return this.stream.stream(request);
    }

    private static ClientEndpointConfig getClientEndpointConfig(OAuth2RestOperations restOperations) {
        return ClientEndpointConfig.Builder
            .create()
            .configurator(new AuthorizationConfigurator(new OAuth2RestOperationsOAuth2TokenProvider(restOperations)))
            .build();
    }

    private static URI getLoggingEndpoint(CloudFoundryClient cloudFoundryClient) {
        return requestInfo(cloudFoundryClient)
            .map(new Function<GetInfoResponse, String>() {

                @Override
                public String apply(GetInfoResponse response) {
                    return response.getLoggingEndpoint();
                }

            })
            .map(new Function<String, URI>() {

                @Override
                public URI apply(String uri) {
                    return URI.create(uri);
                }

            })
            .get(5, SECONDS);
    }

    private static URI getRecentRoot(URI loggingEndpoint, SslCertificateTruster sslCertificateTruster) {
        URI uri = UriComponentsBuilder.fromUri(loggingEndpoint)
            .scheme("https")
            .build().toUri();

        sslCertificateTruster.trust(uri.getHost(), uri.getPort(), 5, SECONDS);
        return uri;
    }

    private static OAuth2RestOperations getRestOperations(ConnectionContext connectionContext) {
        return new OAuth2RestTemplateBuilder()
            .clientContext(connectionContext.getClientContext())
            .protectedResourceDetails(connectionContext.getProtectedResourceDetails())
            .hostnameVerifier(connectionContext.getHostnameVerifier())
            .sslContext(connectionContext.getSslContext())
            .messageConverter(new LoggregatorMessageHttpMessageConverter())
            .messageConverter(new FallbackHttpMessageConverter())
            .build();
    }

    private static SchedulerGroup getSchedulerGroup() {
        return new SchedulerGroupBuilder()
            .name("logging")
            .autoShutdown(false)
            .build();
    }

    private static URI getStreamRoot(URI loggingEndpoint, SslCertificateTruster sslCertificateTruster) {
        sslCertificateTruster.trust(loggingEndpoint.getHost(), loggingEndpoint.getPort(), 5, SECONDS);
        return loggingEndpoint;
    }

    private static Mono<GetInfoResponse> requestInfo(CloudFoundryClient cloudFoundryClient) {
        return cloudFoundryClient.info()
            .get(GetInfoRequest.builder()
                .build());
    }

}

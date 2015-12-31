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

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import org.cloudfoundry.client.LoggregatorClient;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.loggregator.LoggregatorMessage;
import org.cloudfoundry.client.loggregator.RecentLogsRequest;
import org.cloudfoundry.client.loggregator.StreamLogsRequest;
import org.cloudfoundry.client.spring.loggregator.LoggregatorMessageHandler;
import org.cloudfoundry.client.spring.loggregator.ReactiveEndpoint;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.Validators;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.Publishers;
import reactor.core.processor.ProcessorGroup;
import reactor.core.subscriber.SubscriberWithContext;
import reactor.fn.BiConsumer;
import reactor.fn.Consumer;
import reactor.fn.Function;
import reactor.rx.Stream;
import reactor.rx.Streams;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The Spring-based implementation of {@link LoggregatorClient}
 */
@ToString(callSuper = true)
public final class SpringLoggregatorClient extends AbstractSpringOperations implements LoggregatorClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ClientEndpointConfig clientEndpointConfig;

    private final URI root;

    private final WebSocketContainer webSocketContainer;

    @Builder
    SpringLoggregatorClient(@NonNull SpringCloudFoundryClient cloudFoundryClient) {
        this(cloudFoundryClient, ContainerProvider.getWebSocketContainer());
    }

    SpringLoggregatorClient(SpringCloudFoundryClient cloudFoundryClient, WebSocketContainer webSocketContainer) {
        super(getRestOperations(cloudFoundryClient), getRoot(cloudFoundryClient), getProcessorGroup(cloudFoundryClient));

        this.clientEndpointConfig = getClientEndpointConfig(cloudFoundryClient);
        this.root = UriComponentsBuilder.fromUri(super.root).scheme("wss").build().toUri();
        this.webSocketContainer = webSocketContainer;
    }

    SpringLoggregatorClient(ClientEndpointConfig clientEndpointConfig, WebSocketContainer webSocketContainer, RestOperations restOperations, URI root, ProcessorGroup<?> processorGroup) {
        super(restOperations, root, processorGroup);
        this.clientEndpointConfig = clientEndpointConfig;
        this.root = root;
        this.webSocketContainer = webSocketContainer;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Publisher<LoggregatorMessage> recent(final RecentLogsRequest request) {
        return get(request, Stream.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("recent").queryParam("app", request.getId());
            }

        })
                .take(1)
                .flatMap(new Function<Stream, Publisher<? extends LoggregatorMessage>>() {

                    @Override
                    public Publisher<? extends LoggregatorMessage> apply(Stream stream) {
                        return stream;
                    }

                });
    }

    @Override
    public Publisher<LoggregatorMessage> stream(final StreamLogsRequest request) {
        return ws(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.path("tail/").queryParam("app", request.getId());
            }

        }, new Function<Subscriber<LoggregatorMessage>, MessageHandler>() {

            @Override
            public MessageHandler apply(Subscriber<LoggregatorMessage> subscriber) {
                return new LoggregatorMessageHandler(subscriber);
            }

        });
    }

    private static ProcessorGroup<?> getProcessorGroup(SpringCloudFoundryClient cloudFoundryClient) {
        return cloudFoundryClient.getProcessorGroup();
    }

    private static RestOperations getRestOperations(SpringCloudFoundryClient cloudFoundryClient) {
        return cloudFoundryClient.getRestOperations();
    }

    private static URI getRoot(SpringCloudFoundryClient cloudFoundryClient) {
        GetInfoRequest request = GetInfoRequest.builder()
                .build();

        return Streams
                .wrap(cloudFoundryClient.info().get(request))
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
                .next().poll();
    }

    @SuppressWarnings("unchecked")
    private <T, V extends Validatable> Stream<T> exchange(V request, final Consumer<Subscriber<T>> exchange) {
        return Validators
                .stream(request)
                .flatMap(new Function<V, Publisher<T>>() {

                    @Override
                    public Publisher<T> apply(V request) {
                        return Publishers
                                .createWithDemand(new BiConsumer<Long, SubscriberWithContext<T, Void>>() {

                                    @Override
                                    public void accept(Long n, SubscriberWithContext<T, Void> subscriber) {
                                        if (n != Long.MAX_VALUE) {
                                            subscriber.onError(new IllegalArgumentException("Publisher doesn't support back pressure"));
                                        }

                                        exchange.accept(subscriber);
                                    }

                                });
                    }

                });
    }

    private ClientEndpointConfig getClientEndpointConfig(SpringCloudFoundryClient cloudFoundryClient) {
        ClientEndpointConfig.Configurator configurator = new AuthorizationConfigurator(cloudFoundryClient);
        return ClientEndpointConfig.Builder.create().configurator(configurator).build();
    }

    private <T> Stream<T> ws(Validatable request, final Consumer<UriComponentsBuilder> builderCallback, final Function<Subscriber<T>, MessageHandler> messageHandlerCreator) {
        final AtomicReference<Session> session = new AtomicReference<>();

        Stream<T> exchange = exchange(request, new Consumer<Subscriber<T>>() {

            @Override
            public void accept(Subscriber<T> subscriber) {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUri(SpringLoggregatorClient.this.root);
                builderCallback.accept(builder);
                URI uri = builder.build().toUri();

                MessageHandler messageHandler = messageHandlerCreator.apply(subscriber);
                ReactiveEndpoint<T> endpoint = new ReactiveEndpoint<>(messageHandler, subscriber);

                try {
                    SpringLoggregatorClient.this.logger.debug("WS {}", uri);
                    session.set(SpringLoggregatorClient.this.webSocketContainer.connectToServer(endpoint, SpringLoggregatorClient.this.clientEndpointConfig, uri));
                } catch (DeploymentException | IOException e) {
                    subscriber.onError(e);
                }
            }

        });

        return exchange.observeCancel(new Consumer<Void>() {

            @Override
            public void accept(Void v) {
                if (session.get() != null) {
                    try {
                        session.get().close();
                    } catch (IOException e) {
                        SpringLoggregatorClient.this.logger.warn("Failure closing session", e);
                    }
                }
            }

        });
    }

}

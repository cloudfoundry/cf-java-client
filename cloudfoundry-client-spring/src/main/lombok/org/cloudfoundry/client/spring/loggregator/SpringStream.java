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

package org.cloudfoundry.client.spring.loggregator;

import lombok.ToString;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.loggregator.LoggregatorMessage;
import org.cloudfoundry.client.loggregator.StreamLogsRequest;
import org.cloudfoundry.utils.OperationUtils;
import org.cloudfoundry.utils.ValidationUtils;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.SchedulerGroup;
import reactor.core.subscriber.SubscriberWithContext;
import reactor.fn.BiConsumer;
import reactor.fn.Consumer;
import reactor.fn.Function;
import reactor.rx.Stream;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

@ToString
public final class SpringStream {

    private final Logger logger = LoggerFactory.getLogger("cloudfoundry-client.request");

    private final ClientEndpointConfig clientEndpointConfig;

    private final URI root;

    private final SchedulerGroup schedulerGroup;

    private final WebSocketContainer webSocketContainer;

    public SpringStream(ClientEndpointConfig clientEndpointConfig, URI root, SchedulerGroup schedulerGroup, WebSocketContainer webSocketContainer) {
        this.clientEndpointConfig = clientEndpointConfig;
        this.root = root;
        this.schedulerGroup = schedulerGroup;
        this.webSocketContainer = webSocketContainer;
    }

    public Stream<LoggregatorMessage> stream(final StreamLogsRequest request) {
        return ws(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.path("tail/").queryParam("app", request.getApplicationId());
            }

        }, new Function<Subscriber<LoggregatorMessage>, MessageHandler>() {

            @Override
            public MessageHandler apply(Subscriber<LoggregatorMessage> subscriber) {
                return new LoggregatorMessageHandler(subscriber);
            }

        });
    }

    private static String toString(String method, URI uri) {
        return String.format("%-6s %s", method, trimUri(uri));
    }

    private static URI trimUri(URI uri) {
        return UriComponentsBuilder.fromUri(uri)
            .scheme(null).host(null).port(null)
            .build().encode().toUri();
    }

    @SuppressWarnings("unchecked")
    private <T, V extends Validatable> Stream<T> exchange(V request, final Consumer<Subscriber<T>> exchange) {
        return ValidationUtils
            .validate(request)
            .flatMap(new Function<V, Stream<T>>() {

                @Override
                public Stream<T> apply(V request) {
                    return Stream
                        .createWith(new BiConsumer<Long, SubscriberWithContext<T, Void>>() {

                            @Override
                            public void accept(Long n, SubscriberWithContext<T, Void> subscriber) {
                                if (n != Long.MAX_VALUE) {
                                    subscriber.onError(new IllegalArgumentException("Publisher doesn't support back pressure"));
                                }

                                exchange.accept(subscriber);
                            }

                        });
                }

            })
            .as(OperationUtils.<T>stream())
            .publishOn(this.schedulerGroup)
            .onBackpressureBlock();
    }

    private <T> Stream<T> ws(Validatable request, final Consumer<UriComponentsBuilder> builderCallback, final Function<Subscriber<T>, MessageHandler> messageHandlerCreator) {
        final AtomicReference<Session> session = new AtomicReference<>();

        return exchange(request, new Consumer<Subscriber<T>>() {

            @Override
            public void accept(Subscriber<T> subscriber) {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUri(SpringStream.this.root);
                builderCallback.accept(builder);
                URI uri = builder.build().toUri();

                MessageHandler messageHandler = messageHandlerCreator.apply(subscriber);
                ReactiveEndpoint<T> endpoint = new ReactiveEndpoint<>(messageHandler, subscriber);

                try {
                    if (SpringStream.this.logger.isDebugEnabled()) {
                        SpringStream.this.logger.debug(SpringStream.toString("WS", uri));
                    }


                    session.set(SpringStream.this.webSocketContainer.connectToServer(endpoint, SpringStream.this.clientEndpointConfig, uri));
                } catch (DeploymentException | IOException e) {
                    subscriber.onError(e);
                }
            }

        }).doOnCancel(new Runnable() {

            @Override
            public void run() {
                if (session.get() != null) {
                    try {
                        session.get().close();
                    } catch (IOException e) {
                        SpringStream.this.logger.warn("Failure closing session", e);
                    }
                }
            }

        });
    }


}

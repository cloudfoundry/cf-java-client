/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.reactor.util;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.cloudfoundry.reactor.HttpClientResponseWithBody;
import org.cloudfoundry.reactor.HttpClientResponseWithConnection;
import org.reactivestreams.Publisher;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.Connection;
import reactor.netty.NettyOutbound;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientForm;
import reactor.netty.http.client.HttpClientRequest;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;
import reactor.util.retry.Retry;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class Operator extends OperatorContextAware {

    private final HttpClient httpClient;

    public Operator(OperatorContext context, HttpClient httpClient) {
        super(context);
        this.httpClient = httpClient;
    }

    public UriConfiguration delete() {
        return request(HttpMethod.DELETE);
    }

    public Operator followRedirects() {
        return new Operator(this.context, this.httpClient.followRedirect(true));
    }

    public UriConfiguration get() {
        return request(HttpMethod.GET);
    }

    public Operator headers(Consumer<HttpHeaders> headersTransformer) {
        return new Operator(this.context, this.httpClient.headers(headersTransformer));
    }

    public Operator headersWhen(Function<HttpHeaders, Mono<? extends HttpHeaders>> headersWhenTransformer) {
        return new Operator(this.context, this.httpClient.headersWhen(headersWhenTransformer));
    }

    public UriConfiguration patch() {
        return request(HttpMethod.PATCH);
    }

    public UriConfiguration post() {
        return request(HttpMethod.POST);
    }

    public UriConfiguration put() {
        return request(HttpMethod.PUT);
    }

    public UriConfiguration request(HttpMethod method) {
        return new UriConfiguration(this.context, attachRequestLogger(this.httpClient).request(method));
    }

    public WebsocketUriConfiguration websocket() {
        return new WebsocketUriConfiguration(this.context, this.httpClient.websocket());
    }

    public Operator withErrorPayloadMapper(ErrorPayloadMapper errorPayloadMapper) {
        return new Operator(this.context.withErrorPayloadMapper(errorPayloadMapper), this.httpClient);
    }

    private static HttpClient attachRequestLogger(HttpClient httpClient) {
        RequestLogger requestLogger = new RequestLogger();
        return httpClient.doAfterRequest((request, connection) -> requestLogger.request(request))
            .doAfterResponseSuccess((response, connection) -> requestLogger.response(response))
            .doOnResponseError((response, connection) -> requestLogger.response(response));
    }

    public static class PayloadConfiguration extends OperatorContextAware {

        private final HttpClient.RequestSender requestSender;

        PayloadConfiguration(OperatorContext context, HttpClient.RequestSender requestSender) {
            super(context);
            this.requestSender = requestSender;
        }

        public ResponseReceiver response() {
            return new ResponseReceiver(this.context, this.requestSender);
        }

        public ResponseReceiverConstructor send(Object payload) {
            return send(serialized(payload));
        }

        public ResponseReceiverConstructor send(BiFunction<HttpClientRequest, NettyOutbound, Publisher<Void>> requestTransformer) {
            HttpClient.ResponseReceiver<?> responseReceiver = this.requestSender.send(requestTransformer);
            return new ResponseReceiverConstructor(this.context, responseReceiver);
        }

        public ResponseReceiverConstructor sendForm(BiConsumer<HttpClientRequest, HttpClientForm> requestTransformer) {
            HttpClient.ResponseReceiver<?> responseReceiver = this.requestSender.sendForm(requestTransformer);
            return new ResponseReceiverConstructor(this.context, responseReceiver);
        }

        private BiFunction<HttpClientRequest, NettyOutbound, Publisher<Void>> serialized(Object payload) {
            return JsonCodec.encode(this.context.getConnectionContext().getObjectMapper(), payload);
        }

    }

    public static class ResponseReceiver extends OperatorContextAware {

        private final List<Function<HttpClientResponse, ChannelHandler>> channelHandlerBuilders = new ArrayList<>();

        private final HttpClient.ResponseReceiver<?> responseReceiver;

        ResponseReceiver(OperatorContext context, HttpClient.ResponseReceiver<?> responseReceiver) {
            super(context);
            this.responseReceiver = responseReceiver;
        }

        public ResponseReceiver addChannelHandler(Function<HttpClientResponse, ChannelHandler> channelHandlerBuilder) {
            this.channelHandlerBuilders.add(channelHandlerBuilder);
            return this;
        }

        public Mono<HttpClientResponse> get() {
            return this.responseReceiver.responseConnection((response, connection) -> Mono.just(HttpClientResponseWithConnection.of(connection, response)))
                .transform(this::processResponse)
                .map(HttpClientResponseWithConnection::getResponse)
                .singleOrEmpty();
        }

        public <T> Mono<T> parseBody(Class<T> bodyType) {
            addChannelHandler(response -> {
                if (HttpHeaderValues.APPLICATION_JSON.contentEquals(response.responseHeaders().get(HttpHeaderNames.CONTENT_TYPE))) {
                    return JsonCodec.createDecoder();
                }

                return null;
            });

            return parseBodyToMono(responseWithBody -> deserialized(responseWithBody.getBody(), bodyType));
        }

        public <T> Flux<T> parseBodyToFlux(Function<HttpClientResponseWithBody, Publisher<T>> responseTransformer) {
            return this.responseReceiver.responseConnection((response, connection) -> Mono.just(HttpClientResponseWithConnection.of(connection, response)))
                .transform(this::processResponse)
                .flatMap(httpClientResponseWithConnection -> {
                    Connection connection = httpClientResponseWithConnection.getConnection();
                    HttpClientResponse response = httpClientResponseWithConnection.getResponse();

                    attachChannelHandlers(response, connection);
                    ByteBufFlux body = ByteBufFlux.fromInbound(connection.inbound().receive()
                        .doFinally(signalType -> connection.dispose()));

                    return Mono.just(HttpClientResponseWithBody.of(body, response));
                })
                .flatMap(responseTransformer);
        }

        public <T> Mono<T> parseBodyToMono(Function<HttpClientResponseWithBody, Publisher<T>> responseTransformer) {
            return parseBodyToFlux(responseTransformer).singleOrEmpty();
        }

        public <T> Mono<T> parseBodyToToken(Function<HttpClientResponseWithBody, Publisher<T>> responseTransformer) {
            return this.responseReceiver.responseConnection((response, connection) -> Mono.just(HttpClientResponseWithConnection.of(connection, response)))
                .transform(this.context.getErrorPayloadMapper()
                    .orElse(ErrorPayloadMappers.fallback()))
                .flatMap(httpClientResponseWithConnection -> {
                    Connection connection = httpClientResponseWithConnection.getConnection();
                    HttpClientResponse response = httpClientResponseWithConnection.getResponse();

                    ByteBufFlux body = ByteBufFlux.fromInbound(connection.inbound().receive()
                        .doFinally(signalType -> connection.dispose()));

                    return Mono.just(HttpClientResponseWithBody.of(body, response));
                })
                .flatMap(responseTransformer).singleOrEmpty();
        }

        private static boolean isUnauthorized(HttpClientResponseWithConnection response) {
            return response.getResponse().status() == HttpResponseStatus.UNAUTHORIZED;
        }

        private void attachChannelHandlers(HttpClientResponse response, Connection connection) {
            for (Function<HttpClientResponse, ChannelHandler> handlerBuilder : this.channelHandlerBuilders) {
                ChannelHandler handler = handlerBuilder.apply(response);
                if (handler != null) {
                    connection.addHandler(handler);
                }
            }
        }

        private <T> Mono<T> deserialized(ByteBufFlux body, Class<T> bodyType) {
            return JsonCodec.decode(this.context.getConnectionContext().getObjectMapper(), body, bodyType);
        }

        private Flux<HttpClientResponseWithConnection> invalidateToken(Flux<HttpClientResponseWithConnection> inbound) {
            return inbound
                .doOnNext(response -> {
                    if (isUnauthorized(response)) {
                        this.context.getTokenProvider().ifPresent(tokenProvider -> tokenProvider.invalidate(this.context.getConnectionContext()));
                        throw new InvalidTokenException();
                    }
                });
        }

        private Flux<HttpClientResponseWithConnection> processResponse(Flux<HttpClientResponseWithConnection> inbound) {
            return inbound
                .transform(this::invalidateToken)
                .retryWhen(Retry.max(this.context.getConnectionContext().getInvalidTokenRetries()).filter(InvalidTokenException.class::isInstance))
                .transform(this.context.getErrorPayloadMapper()
                    .orElse(ErrorPayloadMappers.fallback()));
        }

        private static final class InvalidTokenException extends RuntimeException {

            private static final long serialVersionUID = -3114034909507471614L;

            private InvalidTokenException() {
            }

            @Override
            public synchronized Throwable fillInStackTrace() {
                return null;
            }
        }

    }

    public static class ResponseReceiverConstructor extends OperatorContextAware {

        private final HttpClient.ResponseReceiver<?> responseReceiver;

        ResponseReceiverConstructor(OperatorContext context, HttpClient.ResponseReceiver<?> responseReceiver) {
            super(context);
            this.responseReceiver = responseReceiver;
        }

        public ResponseReceiver response() {
            return new ResponseReceiver(this.context, this.responseReceiver);
        }

    }

    public static class UriConfiguration extends OperatorContextAware {

        private final HttpClient.RequestSender requestSender;

        private UriConfiguration(OperatorContext context, HttpClient.RequestSender requestSender) {
            super(context);
            this.requestSender = requestSender;
        }

        public PayloadConfiguration uri(Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
            String uri = transformRoot(uriTransformer);
            return new PayloadConfiguration(this.context, this.requestSender.uri(uri));
        }

    }

    public static class WebsocketResponseReceiver {

        private final HttpClient.WebsocketSender sender;

        WebsocketResponseReceiver(HttpClient.WebsocketSender sender) {
            this.sender = sender;
        }

        public Flux<InputStream> get() {
            return this.sender.handle(this::handleWebsocketCommunication);
        }

        private Publisher<InputStream> handleWebsocketCommunication(WebsocketInbound inbound, WebsocketOutbound outbound) {
            return inbound.aggregateFrames()
                .receive()
                .asInputStream()
                .doFinally(signalType -> outbound.sendClose());
        }

    }

    public static class WebsocketUriConfiguration extends OperatorContextAware {

        private final HttpClient.WebsocketSender sender;

        private WebsocketUriConfiguration(OperatorContext context, HttpClient.WebsocketSender sender) {
            super(context);
            this.sender = sender;
        }

        public WebsocketResponseReceiver uri(Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
            String uri = transformRoot(uriTransformer);
            logWebsocketRequest(uri);

            return new WebsocketResponseReceiver(this.sender.uri(uri));
        }

        private static void logWebsocketRequest(String uri) {
            new RequestLogger().websocketRequest(uri);
        }

    }

}

class OperatorContextAware {

    protected final OperatorContext context;

    OperatorContextAware(OperatorContext context) {
        this.context = context;
    }

    protected String transformRoot(Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(this.context.getRoot());
        return uriTransformer.apply(uriComponentsBuilder)
            .encode()
            .build()
            .toUriString();
    }

}

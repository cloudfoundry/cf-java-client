/*
 * Copyright 2013-2019 the original author or authors.
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.cloudfoundry.reactor.HttpClientResponseWithBody;
import org.reactivestreams.Publisher;
import org.springframework.web.util.UriComponentsBuilder;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
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

public class Operator extends OperatorContextAware {

    private final HttpClient httpClient;

    public Operator(OperatorContext context, HttpClient httpClient) {
        super(context);
        this.httpClient = httpClient;
    }

    public Operator followRedirects() {
        return new Operator(context, httpClient.followRedirect(true));
    }

    public Operator headers(Consumer<HttpHeaders> headersTransformer) {
        return new Operator(context, httpClient.headers(headersTransformer));
    }

    public Operator withErrorPayloadMapper(ErrorPayloadMapper errorPayloadMapper) {
        return new Operator(context.withErrorPayloadMapper(errorPayloadMapper), httpClient);
    }

    public UriConfiguration get() {
        return request(HttpMethod.GET);
    }

    public UriConfiguration put() {
        return request(HttpMethod.PUT);
    }

    public UriConfiguration post() {
        return request(HttpMethod.POST);
    }

    public UriConfiguration patch() {
        return request(HttpMethod.PATCH);
    }

    public UriConfiguration delete() {
        return request(HttpMethod.DELETE);
    }

    public UriConfiguration request(HttpMethod method) {
        return new UriConfiguration(context, attachRequestLogger(httpClient).request(method));
    }

    public WebsocketUriConfiguration websocket() {
        return new WebsocketUriConfiguration(context, httpClient.websocket());
    }

    private static HttpClient attachRequestLogger(HttpClient httpClient) {
        RequestLogger requestLogger = new RequestLogger();
        return httpClient.doAfterRequest((request, connection) -> requestLogger.request(request))
            .doAfterResponse((response, connection) -> requestLogger.response(response));
    }

    public static class UriConfiguration extends OperatorContextAware {

        private final HttpClient.RequestSender requestSender;

        public UriConfiguration(OperatorContext context, HttpClient.RequestSender requestSender) {
            super(context);
            this.requestSender = requestSender;
        }

        public PayloadConfiguration uri(Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
            String uri = transformRoot(uriTransformer);
            return new PayloadConfiguration(context, requestSender.uri(uri));
        }

    }

    public static class PayloadConfiguration extends OperatorContextAware {

        private final HttpClient.RequestSender requestSender;

        public PayloadConfiguration(OperatorContext context, HttpClient.RequestSender requestSender) {
            super(context);
            this.requestSender = requestSender;
        }

        public ResponseReceiver response() {
            return new ResponseReceiver(context, requestSender);
        }

        public ResponseReceiverConstructor send(Object payload) {
            return send(serialized(payload));
        }

        public ResponseReceiverConstructor send(BiFunction<HttpClientRequest, NettyOutbound, Publisher<Void>> requestTransformer) {
            HttpClient.ResponseReceiver<?> responseReceiver = requestSender.send(requestTransformer);
            return new ResponseReceiverConstructor(context, responseReceiver);
        }

        public ResponseReceiverConstructor sendForm(BiConsumer<HttpClientRequest, HttpClientForm> requestTransformer) {
            HttpClient.ResponseReceiver<?> responseReceiver = requestSender.sendForm(requestTransformer);
            return new ResponseReceiverConstructor(context, responseReceiver);
        }

        private BiFunction<HttpClientRequest, NettyOutbound, Publisher<Void>> serialized(Object payload) {
            return JsonCodec.encode(context.getConnectionContext()
                    .getObjectMapper(),
                payload);
        }

    }

    public static class ResponseReceiverConstructor extends OperatorContextAware {

        private final HttpClient.ResponseReceiver<?> responseReceiver;

        public ResponseReceiverConstructor(OperatorContext context, HttpClient.ResponseReceiver<?> responseReceiver) {
            super(context);
            this.responseReceiver = responseReceiver;
        }

        public ResponseReceiver response() {
            return new ResponseReceiver(context, responseReceiver);
        }

    }

    public static class ResponseReceiver extends OperatorContextAware {

        private final HttpClient.ResponseReceiver<?> responseReceiver;

        private final List<Function<HttpClientResponse, ChannelHandler>> channelHandlerBuilders = new ArrayList<>();

        public ResponseReceiver(OperatorContext context, HttpClient.ResponseReceiver<?> responseReceiver) {
            super(context);
            this.responseReceiver = responseReceiver;
        }

        public ResponseReceiver addChannelHandler(Function<HttpClientResponse, ChannelHandler> channelHandlerBuilder) {
            channelHandlerBuilders.add(channelHandlerBuilder);
            return this;
        }

        public Mono<HttpClientResponse> get() {
            return responseReceiver.response((response,
                                              body) -> processResponse(response, body).map(HttpClientResponseWithBody::getResponse))
                .singleOrEmpty();
        }

        public <T> Mono<T> parseBody(Class<T> bodyType) {
            addChannelHandler(JsonCodec::createDecoder);
            return parseBodyToMono(responseWithBody -> deserialized(responseWithBody.getBody(), bodyType));
        }

        private <T> Mono<T> deserialized(ByteBufFlux body, Class<T> bodyType) {
            return JsonCodec.decode(context.getConnectionContext()
                    .getObjectMapper(),
                body, bodyType);
        }

        public <T> Mono<T> parseBodyToMono(Function<HttpClientResponseWithBody, Publisher<T>> responseTransformer) {
            return parseBodyToFlux(responseTransformer).singleOrEmpty();
        }

        public <T> Flux<T> parseBodyToFlux(Function<HttpClientResponseWithBody, Publisher<T>> responseTransformer) {
            return responseReceiver.responseConnection((response, connection) -> {
                attachChannelHandlers(response, connection);
                ByteBufFlux body = connection.inbound()
                    .receive();
                return processResponse(response, body).flatMapMany(responseTransformer)
                    .doOnTerminate(connection::dispose);
            });
        }

        private void attachChannelHandlers(HttpClientResponse response, Connection connection) {
            for (Function<HttpClientResponse, ChannelHandler> handlerBuilder : channelHandlerBuilders) {
                connection.addHandler(handlerBuilder.apply(response));
            }
        }

        private Mono<HttpClientResponseWithBody> processResponse(HttpClientResponse response, ByteBufFlux body) {
            HttpClientResponseWithBody responseWithBody = HttpClientResponseWithBody.of(response, body);

            return Mono.just(responseWithBody)
                .map(this::invalidateToken)
                .transform(context.getErrorPayloadMapper()
                    .orElse(ErrorPayloadMappers.fallback()));
        }

        private HttpClientResponseWithBody invalidateToken(HttpClientResponseWithBody response) {
            if (isUnauthorized(response.getResponse())) {
                context.getTokenProvider()
                    .ifPresent(tokenProvider -> tokenProvider.invalidate(context.getConnectionContext()));
            }
            return response;
        }

        private static boolean isUnauthorized(HttpClientResponse response) {
            return response.status() == HttpResponseStatus.UNAUTHORIZED;
        }

    }

    public static class WebsocketUriConfiguration extends OperatorContextAware {

        private final HttpClient.WebsocketSender sender;

        public WebsocketUriConfiguration(OperatorContext context, HttpClient.WebsocketSender sender) {
            super(context);
            this.sender = sender;
        }

        public WebsocketResponseReceiver uri(Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
            String uri = transformRoot(uriTransformer);
            logWebsocketRequest(uri);
            return new WebsocketResponseReceiver(sender.uri(uri));
        }

        private static void logWebsocketRequest(String uri) {
            new RequestLogger().websocketRequest(uri);
        }

    }

    public static class WebsocketResponseReceiver {

        private final HttpClient.WebsocketSender sender;

        public WebsocketResponseReceiver(HttpClient.WebsocketSender sender) {
            this.sender = sender;
        }

        public Flux<InputStream> get() {
            return sender.handle(this::handleWebsocketCommunication);
        }

        private Publisher<InputStream> handleWebsocketCommunication(WebsocketInbound inbound, WebsocketOutbound outbound) {
            return inbound.aggregateFrames()
                .receive()
                .asInputStream()
                .doOnTerminate(outbound::sendClose);
        }

    }

}

class OperatorContextAware {

    protected final OperatorContext context;

    protected OperatorContextAware(OperatorContext context) {
        this.context = context;
    }

    protected String transformRoot(Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(context.getRoot());
        return uriTransformer.apply(uriComponentsBuilder)
            .build()
            .encode()
            .toUriString();
    }

}

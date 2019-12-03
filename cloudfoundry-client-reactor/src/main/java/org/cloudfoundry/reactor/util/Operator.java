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

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.cloudfoundry.reactor.HttpClientResponseWithBody;
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
            .doAfterResponse((response, connection) -> requestLogger.response(response));
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
            return this.responseReceiver.response((resp, body) -> Mono.just(HttpClientResponseWithBody.of(body, resp)))
                .transform(this::processResponse)
                .map(HttpClientResponseWithBody::getResponse)
                .singleOrEmpty();
        }

        public <T> Mono<T> parseBody(Class<T> bodyType) {
            addChannelHandler(ignore -> JsonCodec.createDecoder());
            return parseBodyToMono(responseWithBody -> deserialized(responseWithBody.getBody(), bodyType));
        }

        public <T> Flux<T> parseBodyToFlux(Function<HttpClientResponseWithBody, Publisher<T>> responseTransformer) {
            return this.responseReceiver.responseConnection((response, connection) -> {
                attachChannelHandlers(response, connection);
                ByteBufFlux body = connection.inbound().receive();

                return Mono.just(HttpClientResponseWithBody.of(body, response));
            })
                .transform(this::processResponse)
                .flatMap(responseTransformer);
        }

        public <T> Mono<T> parseBodyToMono(Function<HttpClientResponseWithBody, Publisher<T>> responseTransformer) {
            return parseBodyToFlux(responseTransformer).singleOrEmpty();
        }

        private static boolean isUnauthorized(HttpClientResponseWithBody response) {
            return response.getResponse().status() == HttpResponseStatus.UNAUTHORIZED;
        }

        private void attachChannelHandlers(HttpClientResponse response, Connection connection) {
            for (Function<HttpClientResponse, ChannelHandler> handlerBuilder : this.channelHandlerBuilders) {
                connection.addHandler(handlerBuilder.apply(response));
            }
        }

        private <T> Mono<T> deserialized(ByteBufFlux body, Class<T> bodyType) {
            return JsonCodec.decode(this.context.getConnectionContext().getObjectMapper(), body, bodyType);
        }

        private Flux<HttpClientResponseWithBody> invalidateToken(Flux<HttpClientResponseWithBody> inbound) {
            return inbound
                .doOnNext(response -> {
                    if (isUnauthorized(response)) {
                        this.context.getTokenProvider().ifPresent(tokenProvider -> tokenProvider.invalidate(this.context.getConnectionContext()));
                        throw new InvalidTokenException();
                    }
                });
        }

        private Flux<HttpClientResponseWithBody> processResponse(Flux<HttpClientResponseWithBody> inbound) {
            return inbound
                .transform(this::invalidateToken)
                .retry(t -> t instanceof InvalidTokenException)
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
            .build()
            .encode()
            .toUriString();
    }

}

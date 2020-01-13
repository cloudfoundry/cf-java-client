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

import io.netty.handler.codec.http.HttpHeaders;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION;

public abstract class AbstractReactorOperations {

    protected static final String APPLICATION_ZIP = "application/zip";

    protected final ConnectionContext connectionContext;

    protected final Map<String, String> requestTags;

    protected final Mono<String> root;

    protected final TokenProvider tokenProvider;

    protected AbstractReactorOperations(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        this.connectionContext = connectionContext;
        this.root = root;
        this.tokenProvider = tokenProvider;
        this.requestTags = requestTags;
    }

    protected Mono<Operator> createOperator() {
        HttpClient httpClient = this.connectionContext.getHttpClient();

        return this.root.map(this::buildOperatorContext)
            .map(context -> new Operator(context, httpClient))
            .map(operator -> operator.headers(this::addHeaders))
            .map(operator -> operator.headersWhen(this::addHeadersWhen));
    }

    private void addHeaders(HttpHeaders httpHeaders) {
        UserAgent.setUserAgent(httpHeaders);
        JsonCodec.setDecodeHeaders(httpHeaders);
        this.requestTags.forEach(httpHeaders::set);
    }

    private Mono<? extends HttpHeaders> addHeadersWhen(HttpHeaders httpHeaders) {
        return this.tokenProvider.getToken(this.connectionContext)
            .map(token -> httpHeaders.set(AUTHORIZATION, token));
    }

    private OperatorContext buildOperatorContext(String root) {
        return OperatorContext.builder()
            .connectionContext(this.connectionContext)
            .root(root)
            .tokenProvider(this.tokenProvider)
            .build();
    }

}

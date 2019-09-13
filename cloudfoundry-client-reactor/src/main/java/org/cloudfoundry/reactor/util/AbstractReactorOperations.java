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

import static io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;

import io.netty.util.AsciiString;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

public abstract class AbstractReactorOperations {

    protected static final AsciiString APPLICATION_ZIP = new AsciiString("application/zip");

    protected final ConnectionContext connectionContext;

    protected final Mono<String> root;

    protected final TokenProvider tokenProvider;

    protected AbstractReactorOperations(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        this.connectionContext = connectionContext;
        this.root = root;
        this.tokenProvider = tokenProvider;
    }

    protected Mono<Operator> createOperator() {
        HttpClient httpClient = connectionContext.getHttpClient();
        return root.map(this::buildOperatorContext)
            .map(context -> new Operator(context, httpClient))
            .flatMap(operator -> tokenProvider.getToken(connectionContext)
                .map(token -> setHeaders(operator, token)));
    }

    private OperatorContext buildOperatorContext(String root) {
        return OperatorContext.builder()
            .connectionContext(connectionContext)
            .root(root)
            .tokenProvider(tokenProvider)
            .build();
    }

    private Operator setHeaders(Operator operator, String token) {
        return operator.headers(httpHeaders -> {
            httpHeaders.set(AUTHORIZATION, token);
            UserAgent.setUserAgent(httpHeaders);
            JsonCodec.setDecodeHeaders(httpHeaders);
        });
    }

}

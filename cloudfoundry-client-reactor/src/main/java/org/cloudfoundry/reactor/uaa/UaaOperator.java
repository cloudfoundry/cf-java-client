/*
 * Copyright 2013-2025 the original author or authors.
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

package org.cloudfoundry.reactor.uaa;

import io.netty.handler.codec.http.HttpHeaders;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import org.cloudfoundry.reactor.uaa.UaaThrottler.Token;
import org.cloudfoundry.reactor.util.ErrorPayloadMapper;
import org.cloudfoundry.reactor.util.Operator;
import org.cloudfoundry.reactor.util.OperatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

public class UaaOperator extends Operator {

    private Token token = null;
    private static final Logger LOGGER = LoggerFactory.getLogger("cloudfoundry-client.test");

    public UaaOperator(OperatorContext context, HttpClient httpClient, Token value, String caller) {
        super(context, httpClient);
        token = Objects.requireNonNull(value, "value must not be null");
        if (token != UaaThrottler.NON_UAA_TOKEN) {
            LOGGER.trace("UaaOperator creating instance for " + value.id() + " caller " + caller);
        }
    }

    @Override
    public UaaOperator followRedirects() {
        return new UaaOperator(
                this.context, super.getHttpClient().followRedirect(true), this.token, "follow");
    }

    @Override
    public UaaOperator headers(Consumer<HttpHeaders> headersTransformer) {
        return new UaaOperator(
                this.context,
                super.getHttpClient().headers(headersTransformer),
                this.token,
                "headers");
    }

    @Override
    public UaaOperator headersWhen(
            Function<HttpHeaders, Mono<? extends HttpHeaders>> headersWhenTransformer) {
        return new UaaOperator(
                this.context,
                super.getHttpClient().headersWhen(headersWhenTransformer),
                this.token,
                "headersWhen");
    }

    @Override
    public UaaOperator withErrorPayloadMapper(ErrorPayloadMapper errorPayloadMapper) {
        return new UaaOperator(
                this.context.withErrorPayloadMapper(errorPayloadMapper),
                super.getHttpClient(),
                this.token,
                "errorPayload");
    }

    @Override
    protected HttpClient attachRequestLogger(HttpClient httpClient) {
        return super.attachRequestLogger(httpClient)
                .doAfterRequest((response, connection) -> token.activate());
    }
}

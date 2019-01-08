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

package org.cloudfoundry.reactor.doppler;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.util.AbstractReactorOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientResponse;

import java.util.function.Function;

abstract class AbstractDopplerOperations extends AbstractReactorOperations {

    AbstractDopplerOperations(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
    }

    final Mono<HttpClientResponse> get(Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return doGet(uriTransformer, outbound -> outbound, inbound -> inbound);
    }

    final Mono<HttpClientResponse> ws(Function<UriComponentsBuilder, UriComponentsBuilder> uriTransformer) {
        return doWs(uriTransformer, outbound -> outbound, inbound -> inbound);
    }

}

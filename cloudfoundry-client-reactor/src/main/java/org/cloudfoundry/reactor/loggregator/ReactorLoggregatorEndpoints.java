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

package org.cloudfoundry.reactor.loggregator;

import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.loggregator.v2.StreamRequest;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.doppler.MultipartCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactorLoggregatorEndpoints extends AbstractLoggregatorOperations {

    ReactorLoggregatorEndpoints(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
    }

    public Flux<Envelope> stream(StreamRequest request) {
        return get(request, uriComponentsBuilder -> uriComponentsBuilder.pathSegment("v2/read"), MultipartCodec::createSimpleDecoder, MultipartCodec::decodeAsEnvelope)
            .map(Envelope::from)
            .checkpoint();
    }
}

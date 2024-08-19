/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.reactor.logcache.v1;

import java.util.Map;
import org.cloudfoundry.logcache.v1.InfoRequest;
import org.cloudfoundry.logcache.v1.InfoResponse;
import org.cloudfoundry.logcache.v1.MetaRequest;
import org.cloudfoundry.logcache.v1.MetaResponse;
import org.cloudfoundry.logcache.v1.ReadRequest;
import org.cloudfoundry.logcache.v1.ReadResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import reactor.core.publisher.Mono;

final class ReactorLogCacheEndpoints extends AbstractLogCacheOperations {

    ReactorLogCacheEndpoints(
            ConnectionContext connectionContext,
            Mono<String> root,
            TokenProvider tokenProvider,
            Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    Mono<InfoResponse> info(InfoRequest request) {
        return get(request, InfoResponse.class, "info").checkpoint();
    }

    Mono<MetaResponse> meta(MetaRequest request) {
        return get(request, MetaResponse.class, "meta").checkpoint();
    }

    Mono<ReadResponse> read(ReadRequest request) {
        return get(request, ReadResponse.class, "read", request.getSourceId()).checkpoint();
    }

    Mono<ReadResponse> recentLogs(ReadRequest request) {
        return get(request, ReadResponse.class, "read", request.getSourceId()).checkpoint();
    }
}

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

package org.cloudfoundry.reactor.routing.v1.tcproutes;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.routing.v1.AbstractRoutingV1Operations;
import org.cloudfoundry.routing.v1.tcproutes.CreateTcpRoutesRequest;
import org.cloudfoundry.routing.v1.tcproutes.CreateTcpRoutesResponse;
import org.cloudfoundry.routing.v1.tcproutes.DeleteTcpRoutesRequest;
import org.cloudfoundry.routing.v1.tcproutes.EventType;
import org.cloudfoundry.routing.v1.tcproutes.EventsRequest;
import org.cloudfoundry.routing.v1.tcproutes.ListTcpRoutesRequest;
import org.cloudfoundry.routing.v1.tcproutes.ListTcpRoutesResponse;
import org.cloudfoundry.routing.v1.tcproutes.TcpRouteEvent;
import org.cloudfoundry.routing.v1.tcproutes.TcpRoutes;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;

/**
 * The Reactor-based implementation of {@link TcpRoutes}
 */
public class ReactorTcpRoutes extends AbstractRoutingV1Operations implements TcpRoutes {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorTcpRoutes(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateTcpRoutesResponse> create(CreateTcpRoutesRequest request) {
        return post(request, CreateTcpRoutesResponse.class, builder -> builder.pathSegment("v1", "tcp_routes", "create"))
            .checkpoint();
    }

    @Override
    public Mono<Void> delete(DeleteTcpRoutesRequest request) {
        return post(request, Void.class, builder -> builder.pathSegment("v1", "tcp_routes", "delete"))
            .checkpoint();
    }

    @Override
    public Flux<TcpRouteEvent> events(EventsRequest request) {
        return get(EventStreamCodec::createDecoder, builder -> builder.pathSegment("v1", "tcp_routes", "events"), EventStreamCodec::decode)
            .map(event -> {
                try {
                    return this.connectionContext.getObjectMapper()
                        .readValue(event.getData(), TcpRouteEvent.Builder.class)
                        .eventType(EventType.from(event.getEventType()))
                        .build();
                } catch (IOException e) {
                    throw Exceptions.propagate(e);
                }
            })
            .checkpoint();
    }

    @Override
    public Mono<ListTcpRoutesResponse> list(ListTcpRoutesRequest request) {
        return get(ListTcpRoutesResponse.class, builder -> builder.pathSegment("v1", "tcp_routes"))
            .checkpoint();
    }

}

/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry.reactor.uaa.clients;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.uaa.AbstractUaaOperations;
import org.cloudfoundry.uaa.clients.Clients;
import org.cloudfoundry.uaa.clients.CreateClientRequest;
import org.cloudfoundry.uaa.clients.CreateClientResponse;
import org.cloudfoundry.uaa.clients.DeleteClientRequest;
import org.cloudfoundry.uaa.clients.DeleteClientResponse;
import org.cloudfoundry.uaa.clients.GetClientRequest;
import org.cloudfoundry.uaa.clients.GetClientResponse;
import org.cloudfoundry.uaa.clients.ListClientsRequest;
import org.cloudfoundry.uaa.clients.ListClientsResponse;
import org.cloudfoundry.uaa.clients.UpdateClientRequest;
import org.cloudfoundry.uaa.clients.UpdateClientResponse;
import reactor.core.publisher.Mono;

/**
 * The Reactor-based implementation of {@link Clients}
 */
public final class ReactorClients extends AbstractUaaOperations implements Clients {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     */
    public ReactorClients(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
    }

    @Override
    public Mono<CreateClientResponse> create(CreateClientRequest request) {
        return post(request, CreateClientResponse.class, builder -> builder.pathSegment("oauth", "clients"));
    }

    @Override
    public Mono<DeleteClientResponse> delete(DeleteClientRequest request) {
        return delete(request, DeleteClientResponse.class, builder -> builder.pathSegment("oauth", "clients", request.getClientId()));
    }

    @Override
    public Mono<GetClientResponse> get(GetClientRequest request) {
        return get(request, GetClientResponse.class, builder -> builder.pathSegment("oauth", "clients", request.getClientId()));
    }

    @Override
    public Mono<ListClientsResponse> list(ListClientsRequest request) {
        return get(request, ListClientsResponse.class, builder -> builder.pathSegment("oauth", "clients"));
    }

    @Override
    public Mono<UpdateClientResponse> update(UpdateClientRequest request) {
        return put(request, UpdateClientResponse.class, builder -> builder.pathSegment("oauth", "clients", request.getClientId()));
    }

}

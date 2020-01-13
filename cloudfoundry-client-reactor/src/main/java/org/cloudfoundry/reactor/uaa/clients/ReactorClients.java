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

package org.cloudfoundry.reactor.uaa.clients;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.uaa.AbstractUaaOperations;
import org.cloudfoundry.uaa.clients.BatchChangeSecretRequest;
import org.cloudfoundry.uaa.clients.BatchChangeSecretResponse;
import org.cloudfoundry.uaa.clients.BatchCreateClientsRequest;
import org.cloudfoundry.uaa.clients.BatchCreateClientsResponse;
import org.cloudfoundry.uaa.clients.BatchDeleteClientsRequest;
import org.cloudfoundry.uaa.clients.BatchDeleteClientsResponse;
import org.cloudfoundry.uaa.clients.BatchUpdateClientsRequest;
import org.cloudfoundry.uaa.clients.BatchUpdateClientsResponse;
import org.cloudfoundry.uaa.clients.ChangeSecretRequest;
import org.cloudfoundry.uaa.clients.ChangeSecretResponse;
import org.cloudfoundry.uaa.clients.Clients;
import org.cloudfoundry.uaa.clients.CreateClientRequest;
import org.cloudfoundry.uaa.clients.CreateClientResponse;
import org.cloudfoundry.uaa.clients.DeleteClientRequest;
import org.cloudfoundry.uaa.clients.DeleteClientResponse;
import org.cloudfoundry.uaa.clients.GetClientRequest;
import org.cloudfoundry.uaa.clients.GetClientResponse;
import org.cloudfoundry.uaa.clients.GetMetadataRequest;
import org.cloudfoundry.uaa.clients.GetMetadataResponse;
import org.cloudfoundry.uaa.clients.ListClientsRequest;
import org.cloudfoundry.uaa.clients.ListClientsResponse;
import org.cloudfoundry.uaa.clients.ListMetadatasRequest;
import org.cloudfoundry.uaa.clients.ListMetadatasResponse;
import org.cloudfoundry.uaa.clients.MixedActionsRequest;
import org.cloudfoundry.uaa.clients.MixedActionsResponse;
import org.cloudfoundry.uaa.clients.UpdateClientRequest;
import org.cloudfoundry.uaa.clients.UpdateClientResponse;
import org.cloudfoundry.uaa.clients.UpdateMetadataRequest;
import org.cloudfoundry.uaa.clients.UpdateMetadataResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link Clients}
 */
public final class ReactorClients extends AbstractUaaOperations implements Clients {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://uaa.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorClients(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<BatchChangeSecretResponse> batchChangeSecret(BatchChangeSecretRequest request) {
        return post(request, BatchChangeSecretResponse.class, builder -> builder.pathSegment("oauth", "clients", "tx", "secret"))
            .checkpoint();
    }

    @Override
    public Mono<BatchCreateClientsResponse> batchCreate(BatchCreateClientsRequest request) {
        return post(request, BatchCreateClientsResponse.class, builder -> builder.pathSegment("oauth", "clients", "tx"))
            .checkpoint();
    }

    @Override
    public Mono<BatchDeleteClientsResponse> batchDelete(BatchDeleteClientsRequest request) {
        return post(request, BatchDeleteClientsResponse.class, builder -> builder.pathSegment("oauth", "clients", "tx", "delete"))
            .checkpoint();
    }

    @Override
    public Mono<BatchUpdateClientsResponse> batchUpdate(BatchUpdateClientsRequest request) {
        return put(request, BatchUpdateClientsResponse.class, builder -> builder.pathSegment("oauth", "clients", "tx"))
            .checkpoint();
    }

    @Override
    public Mono<ChangeSecretResponse> changeSecret(ChangeSecretRequest request) {
        return put(request, ChangeSecretResponse.class, builder -> builder.pathSegment("oauth", "clients", request.getClientId(), "secret"))
            .checkpoint();
    }

    @Override
    public Mono<CreateClientResponse> create(CreateClientRequest request) {
        return post(request, CreateClientResponse.class, builder -> builder.pathSegment("oauth", "clients"))
            .checkpoint();
    }

    @Override
    public Mono<DeleteClientResponse> delete(DeleteClientRequest request) {
        return delete(request, DeleteClientResponse.class, builder -> builder.pathSegment("oauth", "clients", request.getClientId()))
            .checkpoint();
    }

    @Override
    public Mono<GetClientResponse> get(GetClientRequest request) {
        return get(request, GetClientResponse.class, builder -> builder.pathSegment("oauth", "clients", request.getClientId()))
            .checkpoint();
    }

    @Override
    public Mono<GetMetadataResponse> getMetadata(GetMetadataRequest request) {
        return get(request, GetMetadataResponse.class, builder -> builder.pathSegment("oauth", "clients", request.getClientId(), "meta"))
            .checkpoint();
    }

    @Override
    public Mono<ListClientsResponse> list(ListClientsRequest request) {
        return get(request, ListClientsResponse.class, builder -> builder.pathSegment("oauth", "clients"))
            .checkpoint();
    }

    @Override
    public Mono<ListMetadatasResponse> listMetadatas(ListMetadatasRequest request) {
        return get(request, ListMetadatasResponse.class, builder -> builder.pathSegment("oauth", "clients", "meta"))
            .checkpoint();
    }

    @Override
    public Mono<MixedActionsResponse> mixedActions(MixedActionsRequest request) {
        return post(request, MixedActionsResponse.class, builder -> builder.pathSegment("oauth", "clients", "tx", "modify"))
            .checkpoint();
    }

    @Override
    public Mono<UpdateClientResponse> update(UpdateClientRequest request) {
        return put(request, UpdateClientResponse.class, builder -> builder.pathSegment("oauth", "clients", request.getClientId()))
            .checkpoint();
    }

    @Override
    public Mono<UpdateMetadataResponse> updateMetadata(UpdateMetadataRequest request) {
        return put(request, UpdateMetadataResponse.class, builder -> builder.pathSegment("oauth", "clients", request.getClientId(), "meta"))
            .checkpoint();
    }
}

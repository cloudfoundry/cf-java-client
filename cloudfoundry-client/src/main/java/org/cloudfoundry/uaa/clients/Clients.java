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

package org.cloudfoundry.uaa.clients;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the UAA Clients API
 */
public interface Clients {

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#batch-secret-change">Batch Change Secret</a> request
     *
     * @param request the Batch Change Secret request
     * @return the response to the Batch Change Secret request
     */
    Mono<BatchChangeSecretResponse> batchChangeSecret(BatchChangeSecretRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#batch-create">Batch Create Client</a> request
     *
     * @param request the Batch Create Client request
     * @return the response to the Batch Create Client request
     */
    Mono<BatchCreateClientsResponse> batchCreate(BatchCreateClientsRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#batch-delete">Batch Delete Clients</a> request
     *
     * @param request the Batch Delete Clients request
     * @return the response to the Batch Delete Clients request
     */
    Mono<BatchDeleteClientsResponse> batchDelete(BatchDeleteClientsRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#batch-update">Batch Update Clients</a> request
     *
     * @param request the Batch Update Clients request
     * @return the response to the Batch Update Clients request
     */
    Mono<BatchUpdateClientsResponse> batchUpdate(BatchUpdateClientsRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#change-secret">Change Secret</a> request
     *
     * @param request the Change Secret request
     * @return the response to the Change Secret request
     */
    Mono<ChangeSecretResponse> changeSecret(ChangeSecretRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#create94">Create Client</a> request
     *
     * @param request the Create Client request
     * @return the response to the Create Client request
     */
    Mono<CreateClientResponse> create(CreateClientRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#delete97">Delete Client</a> request
     *
     * @param request the Delete Client request
     * @return the response to the Delete Client request
     */
    Mono<DeleteClientResponse> delete(DeleteClientRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#retrieve95">Retrieve Client</a> request
     *
     * @param request the Get Client request
     * @return the response to the Get Client request
     */
    Mono<GetClientResponse> get(GetClientRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#retrieve106">Retrieve Metadata</a> request
     *
     * @param request the Get Metadata request
     * @return the response to the Get Metadata request
     */
    Mono<GetMetadataResponse> getMetadata(GetMetadataRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#list99">List Clients</a> request
     *
     * @param request the List Clients request
     * @return the response to the List Clients request
     */
    Mono<ListClientsResponse> list(ListClientsRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#list107">List Metadatas</a> request
     *
     * @param request the List Metadatas request
     * @return the response to the List Metadatas request
     */
    Mono<ListMetadatasResponse> listMetadatas(ListMetadatasRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#mixed-actions">Mixed Actions</a> request
     *
     * @param request the Mixed Actions request
     * @return the response to the Mixed Actions request
     */
    Mono<MixedActionsResponse> mixedActions(MixedActionsRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#update96">Update Client</a> request
     *
     * @param request the Update Client request
     * @return the response to the Update Client request
     */
    Mono<UpdateClientResponse> update(UpdateClientRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#update108">Update Metadata</a> request
     *
     * @param request the Update Metadata request
     * @return the response to the Update Metadata request
     */
    Mono<UpdateMetadataResponse> updateMetadata(UpdateMetadataRequest request);

}

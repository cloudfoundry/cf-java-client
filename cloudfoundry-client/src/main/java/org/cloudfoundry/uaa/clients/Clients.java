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

package org.cloudfoundry.uaa.clients;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the UAA Clients API
 */
public interface Clients {

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#batch-create">Batch Create Client</a> request
     *
     * @param request the Batch Create Client request
     * @return the Response to the Batch Create Client Request
     */
    Mono<BatchCreateClientsResponse> batchCreate(BatchCreateClientsRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#batch-delete">Batch Delete Clients</a> request
     *
     * @param request the Batch Delete Clients request
     * @return the Response to the Batch Delete Clients Request
     */
    Mono<BatchDeleteClientsResponse> batchDelete(BatchDeleteClientsRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#batch-update">Batch Update Clients</a> request
     *
     * @param request the Batch Update Clients request
     * @return the Response to the Batch Update Clients Request
     */
    Mono<BatchUpdateClientsResponse> batchUpdate(BatchUpdateClientsRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#change-secret">Change Secret</a> request
     *
     * @param request the Change Secret request
     * @return the Response to the Change Secret Request
     */
    Mono<ChangeSecretResponse> changeSecret(ChangeSecretRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#create76">Create Client</a> request
     *
     * @param request the Create Client request
     * @return the Response to the Create Client Request
     */
    Mono<CreateClientResponse> create(CreateClientRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#delete79">Delete Client</a> request
     *
     * @param request the Delete Client request
     * @return the Response to the Delete Client Request
     */
    Mono<DeleteClientResponse> delete(DeleteClientRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#retrieve77">Retrieve Client</a> request
     *
     * @param request the Get Client request
     * @return the Response to the Get Client Request
     */
    Mono<GetClientResponse> get(GetClientRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#retrieve88">Retrieve Metadata</a> request
     *
     * @param request the Get Metadata request
     * @return the Response to the Get Metadata Request
     */
    Mono<GetMetadataResponse> getMetadata(GetMetadataRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#list81">List Clients</a> request
     *
     * @param request the List Clients request
     * @return the Response to the List Clients Request
     */
    Mono<ListClientsResponse> list(ListClientsRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#list89">List Metadatas</a> request
     *
     * @param request the List Metadatas request
     * @return the Response to the List Metadatas Request
     */
    Mono<ListMetadatasResponse> listMetadatas(ListMetadatasRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#update78">Update Client</a> request
     *
     * @param request the Update Client request
     * @return the Response to the Update Client Request
     */
    Mono<UpdateClientResponse> update(UpdateClientRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#update90">Update Metadata</a> request
     *
     * @param request the Update Metadata request
     * @return the Response to the Update Metadata Request
     */
    Mono<UpdateMetadataResponse> updateMetadata(UpdateMetadataRequest request);

}

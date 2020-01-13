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

package org.cloudfoundry.client.v2.servicekeys;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Service Keys Client API
 */
public interface ServiceKeys {

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/service_keys/create_a_service_key.html">Create Service Key</a> request
     *
     * @param request the Create Service Key request
     * @return the response from the Create Service Key request
     */
    Mono<CreateServiceKeyResponse> create(CreateServiceKeyRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/service_keys/delete_a_particular_service_key.html">Delete the Service Key</a> request
     *
     * @param request the Delete Service Key request
     * @return the response from the Delete Service Key request
     */
    Mono<Void> delete(DeleteServiceKeyRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/service_keys/retrieve_a_particular_service_key.html">Retrieve a Particular Service Key</a> request
     *
     * @param request the Get Service Key request
     * @return the response from the Get Service Key request
     */
    Mono<GetServiceKeyResponse> get(GetServiceKeyRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/service_keys/list_all_service_keys.html">List Service Keys</a> request
     *
     * @param request the List Service Keys request
     * @return the response from the List Service Keys request
     */
    Mono<ListServiceKeysResponse> list(ListServiceKeysRequest request);

}

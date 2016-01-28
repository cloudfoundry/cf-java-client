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

package org.cloudfoundry.client.v2.servicekeys;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Service Keys Client API
 */
public interface ServiceKeys {

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/service_keys/create_a_service_key.html">Create Service Key</a> request
     *
     * @param request the Create Service Key request
     * @return the response from the Create Service Key request
     */
    Mono<CreateServiceKeyResponse> create(CreateServiceKeyRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/214/service_keys/delete_a_particular_service_key.html">Delete the Service Key</a> request
     *
     * @param request the Delete Service Key request
     * @return the response from the Delete Service Key request
     */
    Mono<Void> delete(DeleteServiceKeyRequest request);

}

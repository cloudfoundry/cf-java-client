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

package org.cloudfoundry.client.v2.shareddomains;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Shared Domains Client API
 */
public interface SharedDomains {

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/shared_domains/create_a_shared_domain.html">Create a Shared Domain</a> request
     *
     * @param request the Create a Shared Domain request
     * @return the response from the Create a Shared Domain request
     */
    Mono<CreateSharedDomainResponse> create(CreateSharedDomainRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/shared_domains/delete_a_particular_shared_domain.html">Delete a Shared Domain</a> request
     *
     * @param request the Delete a Shared Domain request
     * @return the response from the Delete a Shared Domain request
     */
    Mono<DeleteSharedDomainResponse> delete(DeleteSharedDomainRequest request);


    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/shared_domains/retrieve_a_particular_shared_domain.html">Get a Shared Domain</a> request
     *
     * @param request the Get a Shared Domain request
     * @return the response from the Get a Shared Domain request
     */
    Mono<GetSharedDomainResponse> get(GetSharedDomainRequest request);


    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/shared_domains/list_all_shared_domains.html">List all Shared Domains</a> request
     *
     * @param request the List all Shared Domains request
     * @return the response from the List all Shared Domains request
     */
    Mono<ListSharedDomainsResponse> list(ListSharedDomainsRequest request);

}

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

package org.cloudfoundry.client.v3.domains;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Domains V3 Client API
 */
public interface DomainsV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.86.0/index.html#check-reserved-routes-for-a-domain">Check Reserved Routes</a> request
     *
     * @param request the Check Reserved Routes request
     * @return the response from the Check Reserved Routes request
     */
    Mono<CheckReservedRoutesResponse> checkReservedRoutes(CheckReservedRoutesRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#create-a-domain">Create a domain</a> request
     *
     * @param request the Create a Domain request
     * @return the response from the Create a Domain request
     */
    Mono<CreateDomainResponse> create(CreateDomainRequest request);

    /**
     * Makes <a href="http://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#delete-a-domain">Delete a Particular Domain</a> request
     *
     * @param request the Delete a Particular Domain request
     * @return the response from the Delete a Particular Domain request
     */
    Mono<String> delete(DeleteDomainRequest request);

    /**
     * Makes <a href="http://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#get-a-domain">Get Domain</a> request
     *
     * @param request The Get Domain request
     * @return the response from the Get Domain request
     */
    Mono<GetDomainResponse> get(GetDomainRequest request);

    /**
     * Makes <a href="http://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#list-domains">List all Domains</a> request
     *
     * @param request the List all Domains request
     * @return the response from the List all Domains request
     */
    Mono<ListDomainsResponse> list(ListDomainsRequest request);

    /**
     * Makes <a href="http://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#share-a-domain">Share a Domain</a> request
     *
     * @param request The Share a Domain request
     * @return the response from the Share a Domain request
     */
    Mono<ShareDomainResponse> share(ShareDomainRequest request);

    /**
     * Makes <a href="http://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#unshare-a-domain">Unshare a Domain</a> request
     *
     * @param request The Unshare a Domain request
     * @return the response from the Unshare a Domain request
     */
    Mono<Void> unshare(UnshareDomainRequest request);

    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/3.77.0/index.html#update-a-domain">Create a domain</a> request
     *
     * @param request the Update a Domain request
     * @return the response from the Update a Domain request
     */
    Mono<UpdateDomainResponse> update(UpdateDomainRequest request);

}

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

package org.cloudfoundry.uaa.identityproviders;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the UAA Identity Provider Management Client API
 */
public interface IdentityProviders {

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#create">Create Identity Provider</a> request
     *
     * @param request the Create Identity Provider request
     * @return the response from the Create Identity Provider request
     */
    Mono<CreateIdentityProviderResponse> create(CreateIdentityProviderRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#delete">Delete Identity Provider</a> request
     *
     * @param request the Delete Identity Provider request
     * @return the response from the Delete Identity Provider request
     */
    Mono<DeleteIdentityProviderResponse> delete(DeleteIdentityProviderRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#retrieve">Get Identity Provider</a> request
     *
     * @param request the Get Identity Provider request
     * @return the response from the Get Identity Provider request
     */
    Mono<GetIdentityProviderResponse> get(GetIdentityProviderRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#retrieve-all">List Identity Providers</a> request
     *
     * @param request the List Identity Providers request
     * @return the response from the List Identitys Provider request
     */
    Mono<ListIdentityProvidersResponse> list(ListIdentityProvidersRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#update">Update Identity Provider</a> request
     *
     * @param request the Update Identity Provider request
     * @return the response from the Update Identity Provider request
     */
    Mono<UpdateIdentityProviderResponse> update(UpdateIdentityProviderRequest request);

}

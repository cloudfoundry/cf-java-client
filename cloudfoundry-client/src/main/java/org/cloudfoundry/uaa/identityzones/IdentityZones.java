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

package org.cloudfoundry.uaa.identityzones;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the UAA Identity Zone Management Client API
 */
public interface IdentityZones {

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#creating-an-identity-zone">Create Identity Zone</a> request
     *
     * @param request the Create Identity Zone request
     * @return the response from the Create Identity Zone request
     */
    Mono<CreateIdentityZoneResponse> create(CreateIdentityZoneRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#deleting-an-identity-zone">Delete the Identity Zone</a> request
     *
     * @param request the Delete Identity Zone request
     * @return the response from the Delete Identity Zone request
     */
    Mono<DeleteIdentityZoneResponse> delete(DeleteIdentityZoneRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#retrieving-an-identity-zone">Get Identity Zone</a> request
     *
     * @param request the Get Identity Zone request
     * @return the response from the Get Identity Zone request
     */
    Mono<GetIdentityZoneResponse> get(GetIdentityZoneRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#retrieving-all-identity-zones">List Identity Zones</a> request
     *
     * @param request the List Identity Zones request
     * @return the response from the List Identity Zones request
     */
    Mono<ListIdentityZonesResponse> list(ListIdentityZonesRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#updating-an-identity-zone">Update Identity Zone</a> request
     *
     * @param request the Update Identity Zone request
     * @return the response from the Update Identity Zone request
     */
    Mono<UpdateIdentityZoneResponse> update(UpdateIdentityZoneRequest request);

}

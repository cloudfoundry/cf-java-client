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

package org.cloudfoundry.uaa.identityzones;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the UAA Identity Zones Client API
 */
public interface IdentityZones {

    /**
     * Makes the <a href="https://github.com/cloudfoundry/uaa/blob/master/docs/UAA-APIs.rst#create-or-update-identity-zones-post-or-put-identity-zones">Create Identity Zone</a> request
     *
     * @param request the Create Identity Zone request
     * @return the response from the Create Identity Zone request
     */
    Mono<CreateIdentityZoneResponse> create(CreateIdentityZoneRequest request);

    /**
     * Makes the <a href="https://github.com/cloudfoundry/uaa/blob/master/docs/UAA-APIs.rst#identity-zone-clients-api-identity-zones-clients">Create Identity Zone Client</a> request
     *
     * @param request the Create Identity Zone Client request
     * @return the response from the Create Identity Zone Client request
     */
    Mono<Void> createClient(CreateIdentityZoneClientRequest request);

    /**
     * Makes the <a href="https://github.com/cloudfoundry/uaa/blob/master/docs/UAA-APIs.rst#delete-single-identity-zone-delete-identity-zones-identityzoneid">Delete the Identity Zone</a> request
     *
     * @param request the Delete Identity Zone request
     * @return the response from the Delete Identity Zone request
     */
    Mono<DeleteIdentityZoneResponse> delete(DeleteIdentityZoneRequest request);

    /**
     * Makes the <a href="https://github.com/cloudfoundry/uaa/blob/master/docs/UAA-APIs.rst#get-single-identity-zone-get-identity-zones-identityzoneid">Get Identity Zone</a> request
     *
     * @param request the Get Identity Zone request
     * @return the response from the Get Identity Zone request
     */
    Mono<GetIdentityZoneResponse> get(GetIdentityZoneRequest request);

    /**
     * Makes the <a href="https://github.com/cloudfoundry/uaa/blob/master/docs/UAA-APIs.rst#list-identity-zones-get-identity-zones">List Identity Zones</a> request
     *
     * @param request the List Identity Zones request
     * @return the response from the List Identity Zones request
     */
    Mono<ListIdentityZoneResponse> list(ListIdentityZoneRequest request);

    /**
     * Makes the <a href="https://github.com/cloudfoundry/uaa/blob/master/docs/UAA-APIs.rst#create-or-update-identity-zones-post-or-put-identity-zones">Update Identity Zone</a> request
     *
     * @param request the Update Identity Zone request
     * @return the response from the Update Identity Zone request
     */
    Mono<UpdateIdentityZoneResponse> update(UpdateIdentityZoneRequest request);

}

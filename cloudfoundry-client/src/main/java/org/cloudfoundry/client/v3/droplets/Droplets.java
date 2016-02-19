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

package org.cloudfoundry.client.v3.droplets;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Droplets Client API
 */
public interface Droplets {

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/droplets_(experimental)/delete_a_droplet.html">Delete Droplet</a> request
     *
     * @param request the Delete Droplet request
     * @return the response from the Delete Droplet request
     */
    Mono<Void> delete(DeleteDropletRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/droplets_(experimental)/get_a_droplet.html">Get Droplet</a> request
     *
     * @param request the Get Droplet request
     * @return the response from the Get Droplet request
     */
    Mono<GetDropletResponse> get(GetDropletRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/droplets_%28experimental%29/list_all_droplets.html">List Droplets</a> request
     *
     * @param request the List Droplets request
     * @return the response from the List Droplets request
     */
    Mono<ListDropletsResponse> list(ListDropletsRequest request);

}

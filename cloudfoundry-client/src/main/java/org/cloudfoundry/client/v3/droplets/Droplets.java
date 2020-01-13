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

package org.cloudfoundry.client.v3.droplets;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Droplets Client API
 */
public interface Droplets {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/#copy-a-droplet">Copy Droplet</a> request
     *
     * @param request the Copy Droplet request
     */
    Mono<CopyDropletResponse> copy(CopyDropletRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#delete-a-droplet">Delete Droplet</a> request
     *
     * @param request the Delete Droplet request
     * @return the response from the Delete Droplet request
     */
    Mono<String> delete(DeleteDropletRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/#get-a-droplet">Get Droplet</a> request
     *
     * @param request the Get Droplet request
     * @return the response from the Get Droplet request
     */
    Mono<GetDropletResponse> get(GetDropletRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.27.0/index.html#list-droplets">List Droplets</a> request
     *
     * @param request the List Droplets request
     * @return the response from the List Droplets request
     */
    Mono<ListDropletsResponse> list(ListDropletsRequest request);

}

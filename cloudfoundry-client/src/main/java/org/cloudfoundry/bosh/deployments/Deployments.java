/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.bosh.deployments;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the BOSH Deployments API
 */
public interface Deployments {

    /**
     * Makes the <a href="https://bosh.io/docs/director-api-v1.html#post-deployment">Create Deployment</a> request
     *
     * @param request the Create Deployment Request
     * @return the response from the Create Deployment request
     */
    Mono<CreateDeploymentResponse> create(CreateDeploymentRequest request);

    /**
     * Makes the <a href="https://bosh.io/docs/director-api-v1.html#list-deployments">List Deployments</a> request
     *
     * @param request the List Deployments Request
     * @return the response from the List Deployments request
     */
    Mono<ListDeploymentsResponse> list(ListDeploymentsRequest request);

}

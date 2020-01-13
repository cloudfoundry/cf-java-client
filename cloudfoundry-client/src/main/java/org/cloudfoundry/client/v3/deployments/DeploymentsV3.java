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

package org.cloudfoundry.client.v3.deployments;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Deployments V3 Client API
 */
public interface DeploymentsV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.63.0/index.html#cancel-a-deployment">Cancel a deployment</a> request
     *
     * @param request the Cancel a deployment request
     * @return the response from the Cancel a deployment request
     */
    Mono<CancelDeploymentResponse> cancel(CancelDeploymentRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.63.0/index.html#create-a-deployment">Get Deployment</a> request
     *
     * @param request the Create Deployment request
     * @return the response from the Create Deployment request
     */
    Mono<CreateDeploymentResponse> create(CreateDeploymentRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.63.0/index.html#get-a-deployment">Get Deployment</a> request
     *
     * @param request the Get Deployment request
     * @return the response from the Get Deployment request
     */
    Mono<GetDeploymentResponse> get(GetDeploymentRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.63.0/index.html#list-deployments">List Deployments</a> request
     *
     * @param request the List Deployments request
     * @return the response from the List Deployments request
     */
    Mono<ListDeploymentsResponse> list(ListDeploymentsRequest request);

}

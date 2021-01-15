/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.client.v3.serviceplans;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Service Plans Client API
 */
public interface ServicePlansV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.88.0/index.html#delete-a-service-plan">Delete Service Plan</a> request
     *
     * @param request the Delete Service Plan request
     * @return the response from the Delete Service Plan request
     */
    Mono<Void> delete(DeleteServicePlanRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.88.0/index.html#get-a-service-plan">Get Service Plan</a> request
     *
     * @param request the Get Service Plan request
     * @return the response from the Get Service Plan request
     */
    Mono<GetServicePlanResponse> get(GetServicePlanRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.88.0/index.html#list-service-plans">List Service Plan</a> request
     *
     * @param request the List Service Plans request
     * @return the response from the List Service Plans request
     */
    Mono<ListServicePlansResponse> list(ListServicePlansRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.88.0/index.html#update-a-service-plan"> Upload Service Plan</a> request
     *
     * @param request the Update Service Plan request
     * @return the response from the Update Service Plan request
     */
    Mono<UpdateServicePlanResponse> update(UpdateServicePlanRequest request);

}

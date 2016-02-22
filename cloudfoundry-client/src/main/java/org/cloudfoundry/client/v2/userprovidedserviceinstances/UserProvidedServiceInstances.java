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

package org.cloudfoundry.client.v2.userprovidedserviceinstances;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry User Provided Service Instances Client API
 */
public interface UserProvidedServiceInstances {

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/user_provided_service_instances/creating_a_user_provided_service_instance.html">Create User Provided Service Instance</a>
     * request
     *
     * @param request the Create User Provided Service Instance request
     * @return the response from the Create User Provided Service Instance request
     */
    Mono<CreateUserProvidedServiceInstanceResponse> create(CreateUserProvidedServiceInstanceRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/user_provided_service_instances/delete_a_particular_user_provided_service_instance.html">Delete the User Provided Service
     * Instance</a> request
     *
     * @param request the Delete User Provided Service Instance request
     * @return the response from the Delete User Provided Service Instance request
     */
    Mono<Void> delete(DeleteUserProvidedServiceInstanceRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/user_provided_service_instances/list_all_user_provided_service_instances.html">List User Provided Service Instances</a>
     * request
     *
     * @param request the List User Provided Service Instances request
     * @return the response from the List User Provided Service Instances request
     */
    Mono<ListUserProvidedServiceInstancesResponse> list(ListUserProvidedServiceInstancesRequest request);

}

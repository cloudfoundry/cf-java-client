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

package org.cloudfoundry.client.v2.userprovidedserviceinstances;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry User Provided Service Instances Client API
 */
public interface UserProvidedServiceInstances {

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/user_provided_service_instances/associate_route_with_the_user_provided_service_instance.html">Associate Route with the User
     * Provided Service Instance</a> request
     *
     * @param request the Associate Route With User Provided Service Instance request
     * @return the response from the Associate Route With User Provided Service Instance request
     */
    Mono<AssociateUserProvidedServiceInstanceRouteResponse> associateRoute(AssociateUserProvidedServiceInstanceRouteRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/user_provided_service_instances/creating_a_user_provided_service_instance.html">Create User Provided Service Instance</a>
     * request
     *
     * @param request the Create User Provided Service Instance request
     * @return the response from the Create User Provided Service Instance request
     */
    Mono<CreateUserProvidedServiceInstanceResponse> create(CreateUserProvidedServiceInstanceRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/user_provided_service_instances/delete_a_particular_user_provided_service_instance.html">Delete the User Provided Service
     * Instance</a> request
     *
     * @param request the Delete User Provided Service Instance request
     * @return the response from the Delete User Provided Service Instance request
     */
    Mono<Void> delete(DeleteUserProvidedServiceInstanceRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/user_provided_service_instances/retrieve_a_particular_user_provided_service_instance.html">Retrieve a Particular User Provided
     * Service Instance</a> request
     *
     * @param request the Get User Provided Service Instance request
     * @return the response from the Get User Provided Service Instance request
     */
    Mono<GetUserProvidedServiceInstanceResponse> get(GetUserProvidedServiceInstanceRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/user_provided_service_instances/list_all_user_provided_service_instances.html">List User Provided Service Instances</a>
     * request
     *
     * @param request the List User Provided Service Instances request
     * @return the response from the List User Provided Service Instances request
     */
    Mono<ListUserProvidedServiceInstancesResponse> list(ListUserProvidedServiceInstancesRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/user_provided_service_instances/list_all_routes_for_the_user_provided_service_instance.html">List all Routes for the User
     * Provided Service Instance</a> request
     *
     * @param request the List User Provided Service Instance Routes request
     * @return the response from the List User Provided Service Instance Routes request
     */
    Mono<ListUserProvidedServiceInstanceRoutesResponse> listRoutes(ListUserProvidedServiceInstanceRoutesRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/user_provided_service_instances/list_all_service_bindings_for_the_user_provided_service_instance.html">List all Service
     * Bindings for the User Provided Service Instance</a> request
     *
     * @param request the List Service Bindings request
     * @return the response from the List Service Bindings request
     */
    Mono<ListUserProvidedServiceInstanceServiceBindingsResponse> listServiceBindings(ListUserProvidedServiceInstanceServiceBindingsRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/user_provided_service_instances/remove_route_from_the_user_provided_service_instance.html">Remove Route from the User Provided
     * Service Instance</a> request
     *
     * @param request the Remove Route from the User Provided Service Instance request
     * @return the response from the Remove Route from the User Provided Service Instance request
     */
    Mono<Void> removeRoute(RemoveUserProvidedServiceInstanceRouteRequest request);

    /**
     * Makes the <a href="https://apidocs.cloudfoundry.org/latest-release/user_provided_service_instances/updating_a_user_provided_service_instance.html">Update User Provided Service Instance</a>
     * request
     *
     * @param request the Update User Provided Service Instance request
     * @return the response from the Update User Provided Service Instance request
     */
    Mono<UpdateUserProvidedServiceInstanceResponse> update(UpdateUserProvidedServiceInstanceRequest request);

}

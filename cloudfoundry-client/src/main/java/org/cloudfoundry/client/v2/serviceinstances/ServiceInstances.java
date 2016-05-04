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

package org.cloudfoundry.client.v2.serviceinstances;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Service Instances Client API
 */
public interface ServiceInstances {

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/service_instances/binding_a_service_instance_to_a_route.html">Bind Service Instance To a Route</a> request
     *
     * @param request the Bind Service Instance To Route request
     * @return the response from the Bind Service Instance To Route request
     */
    Mono<BindServiceInstanceToRouteResponse> bindToRoute(BindServiceInstanceToRouteRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/service_instances/creating_a_service_instance.html">Create Service Instance</a> request
     *
     * @param request the Create Service Instance request
     * @return the response from the Create Service Instance request
     */
    Mono<CreateServiceInstanceResponse> create(CreateServiceInstanceRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/service_instances/delete_a_service_instance.html">Delete the Service Instance</a> request
     *
     * @param request the Delete Service Instance request
     * @return the response from the Delete Service Instance request
     */
    Mono<DeleteServiceInstanceResponse> delete(DeleteServiceInstanceRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/service_instances/retrieve_a_particular_service_instance.html">Retrieve a Particular Service Instance</a> request
     *
     * @param request the Get Service Instance request
     * @return the response from the Get Service Instance request
     */
    Mono<GetServiceInstanceResponse> get(GetServiceInstanceRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/service_instances/retrieving_permissions_on_a_service_instance.html">Retrieving permissions on a Service Instance</a> request
     *
     * @param request the Get Permissions request
     * @return the response from the Get Permissions request
     */
    Mono<GetServiceInstancePermissionsResponse> getPermissions(GetServiceInstancePermissionsRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/service_instances/list_all_service_instances.html">List Service Instances</a> request
     *
     * @param request the List Service Instances request
     * @return the response from the List Service Instances request
     */
    Mono<ListServiceInstancesResponse> list(ListServiceInstancesRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/service_instances/list_all_service_bindings_for_the_service_instance.html">List all Service Bindings for the Service
     * Instance</a> request
     *
     * @param request the List Service Bindings request
     * @return the response from the List Service Bindings request
     */
    Mono<ListServiceInstanceServiceBindingsResponse> listServiceBindings(ListServiceInstanceServiceBindingsRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/service_instances/list_all_service_keys_for_the_service_instance.html">List all Service keys for the Service
     * Instance</a> request
     *
     * @param request the List Service Keys request
     * @return the response from the List Service Keys request
     */
    Mono<ListServiceInstanceServiceKeysResponse> listServiceKeys(ListServiceInstanceServiceKeysRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/service_instances/update_a_service_instance.html">Update Service Instance</a> request
     *
     * @param request the Update Service Instance request
     * @return the response from the Update Service Instance request
     */
    Mono<UpdateServiceInstanceResponse> update(UpdateServiceInstanceRequest request);

}

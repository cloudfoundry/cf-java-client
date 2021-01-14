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

package org.cloudfoundry.client.v3.roles;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Roles V3 Client API
 */
public interface RolesV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.92.0/index.html#create-a-role">Create Role</a> request
     *
     * @param request the Create Role request
     * @return the response from the Create Role request
     */
    Mono<CreateRoleResponse> create(CreateRoleRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.92.0/index.html#delete-a-role">Delete Role</a> request
     *
     * @param request the Delete Role request
     * @return the response from the Delete Role request
     */
    Mono<String> delete(DeleteRoleRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.92.0/index.html#get-a-role">Get Role</a> request
     *
     * @param request the Get Role request
     * @return the response from the Get Role request
     */
    Mono<GetRoleResponse> get(GetRoleRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.92.0/index.html#list-roles">List Roles</a> request
     *
     * @param request the List Roles request
     * @return the response from the List Roles request
     */
    Mono<ListRolesResponse> list(ListRolesRequest request);

}

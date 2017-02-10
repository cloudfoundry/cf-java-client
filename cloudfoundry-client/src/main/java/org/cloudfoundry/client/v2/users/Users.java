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

package org.cloudfoundry.client.v2.users;

import reactor.core.publisher.Mono;

public interface Users {

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/users/creating_a_user.html">Creating a User</a> request
     *
     * @param request the Creating a User request
     * @return the response from the Creating a User request
     */
    Mono<CreateUserResponse> create(CreateUserRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/users/delete_a_particular_user.html">Delete a Particular User</a> request
     *
     * @param request the Delete a Particular User request
     * @return the response from the Delete a Particular User request
     */
    Mono<DeleteUserResponse> delete(DeleteUserRequest request);

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/latest-release/users/list_all_users.html">List all Users</a> request
     *
     * @param request the List all Users request
     * @return the response from the List all Users request
     */
    Mono<ListUsersResponse> list(ListUsersRequest request);

}

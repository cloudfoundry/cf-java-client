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

package org.cloudfoundry.uaa.users;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the UAA Users Client API
 */
public interface Users {

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#create48">Create Users</a> request
     *
     * @param request the Create Users request
     * @return the response from the Create Users request
     */
    Mono<CreateUserResponse> create(CreateUserRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#list">List Users</a> request
     *
     * @param request the List Users request
     * @return the response from the List Users request
     */
    Mono<ListUsersResponse> list(ListUsersRequest request);

}

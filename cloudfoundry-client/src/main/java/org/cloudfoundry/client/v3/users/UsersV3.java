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
package org.cloudfoundry.client.v3.users;


import reactor.core.publisher.Mono;


/**
 * Main entry point to the Cloud Foundry Users V3 Client API
 */
public interface UsersV3 {

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.197.0/index.html#create-a-user">Create a User</a> request
     *
     * @param request the Create User request
     * @return the response from the Create User request
     */
    Mono<CreateUserResponse> create(CreateUserRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.197.0/index.html#update-a-user">Get a User</a> request
     *
     * @param request the Get User request
     * @return the response from the Get User request
     */
    Mono<GetUserResponse> get(GetUserRequest request);

    /**
     * Makes the <a href="v3-apidocs.cloudfoundry.org/version/3.197.0/index.html#update-a-user">Update a Stack</a> request
     *
     * @param request the Update User request
     * @return the response from the Update User request
     */
    Mono<UpdateUserResponse> update(UpdateUserRequest request);

    /**
     * Makes the <a href="https://v3-apidocs.cloudfoundry.org/version/3.197.0/index.html#delete-a-user">Delete a User</a> request
     *
     * @param request the Delete User request
     * @return void
     */
    Mono<Void> delete(DeleteUserRequest request);
}
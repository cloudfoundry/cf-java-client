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

package org.cloudfoundry.operations.useradmin;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry User Admin Operations API
 */
public interface UserAdmin {

    /**
     * Create a user
     *
     * @param request the create user request
     * @return completion indicator
     */
    Mono<Void> create(CreateUserRequest request);

    /**
     * Delete a user
     *
     * @param request the delete user request
     * @return completion indicator
     */
    Mono<Void> delete(DeleteUserRequest request);

    /**
     * List organization users
     *
     * @param request the list organization users request
     * @return the Organization Users
     */
    Mono<OrganizationUsers> listOrganizationUsers(ListOrganizationUsersRequest request);

    /**
     * List space users
     *
     * @param request the list space users request
     * @return the Space Users
     */
    Mono<SpaceUsers> listSpaceUsers(ListSpaceUsersRequest request);

    /**
     * Assign an organization role to a user
     *
     * @param request the set organization user request
     * @return completion indicator
     */
    Mono<Void> setOrganizationRole(SetOrganizationRoleRequest request);

    /**
     * Assign a space role to a user
     *
     * @param request the set space user request
     * @return completion indicator
     */
    Mono<Void> setSpaceRole(SetSpaceRoleRequest request);

    /**
     * Remove an organization role from a user
     *
     * @param request the unset organization user request
     * @return completion indicator
     */
    Mono<Void> unsetOrganizationRole(UnsetOrganizationRoleRequest request);

    /**
     * Remove a space role from a user
     *
     * @param request the unset space user request
     * @return completion indicator
     */
    Mono<Void> unsetSpaceRole(UnsetSpaceRoleRequest request);

}

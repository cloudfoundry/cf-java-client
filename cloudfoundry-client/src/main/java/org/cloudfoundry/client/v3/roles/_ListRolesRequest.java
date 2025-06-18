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

package org.cloudfoundry.client.v3.roles;

import org.cloudfoundry.client.v3.FilterParameter;
import org.cloudfoundry.client.v3.PaginatedRequest;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the List Roles operation
 */
@Value.Immutable
abstract class _ListRolesRequest extends PaginatedRequest {

    /**
     * The role ids filter
     */
    @FilterParameter("guids")
    abstract List<String> getRoleIds();

    /**
     * The organization ids filter
     */
    @FilterParameter("organization_guids")
    abstract List<String> getOrganizationIds();

    /**
     * The space ids filter
     */
    @FilterParameter("space_guids")
    abstract List<String> getSpaceIds();

    /**
     * The types filter
     */
    @FilterParameter("types")
    abstract List<RoleType> getTypes();

    /**
     * The user ids filter
     */
    @FilterParameter("user_guids")
    abstract List<String> getUserIds();

    /**
     * The include parameter
     */
    @FilterParameter("include")
    abstract List<String> getInclude();

}

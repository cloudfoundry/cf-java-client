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

package org.cloudfoundry.client.v3.packages;

import org.cloudfoundry.client.v3.FilterParameter;
import org.cloudfoundry.client.v3.PaginatedRequest;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the List Packages operation
 */
@Value.Immutable
abstract class _ListPackagesRequest extends PaginatedRequest {

    /**
     * List of application ids to filter by
     */
    @FilterParameter("app_guids")
    abstract List<String> getApplicationIds();

    /**
     * List of organization ids to filter by
     */
    @FilterParameter("organization_guids")
    abstract List<String> getOrganizationIds();

    /**
     * List of package ids to filter by
     */
    @FilterParameter("guids")
    abstract List<String> getPackageIds();

    /**
     * List of space ids to filter by
     */
    @FilterParameter("space_guids")
    abstract List<String> getSpaceIds();

    /**
     * List of package states to filter by
     */
    @FilterParameter("states")
    abstract List<PackageState> getStates();

    /**
     * List of package types to filter by
     */
    @FilterParameter("types")
    abstract List<PackageType> getTypes();

}

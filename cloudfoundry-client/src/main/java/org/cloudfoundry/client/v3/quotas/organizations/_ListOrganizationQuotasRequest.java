/*
 * Copyright 2013-2025 the original author or authors.
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

package org.cloudfoundry.client.v3.quotas.organizations;

import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.FilterParameter;
import org.cloudfoundry.client.v3.PaginatedRequest;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the List all Organization Quota
 */
@Value.Immutable
abstract class _ListOrganizationQuotasRequest extends PaginatedRequest {

    /**
     * list of organization quota guids to filter by
     */
    @FilterParameter("guids")
    @Nullable
    abstract List<String> getGuids();

    /**
     * list of organization quota names to filter by
     */
    @FilterParameter("names")
    @Nullable
    abstract List<String> getNames();

    /**
     *  list of organization guids to filter by
     */
    @FilterParameter("organization_guids")
    @Nullable
    abstract List<String> getOrganizationGuids();

}

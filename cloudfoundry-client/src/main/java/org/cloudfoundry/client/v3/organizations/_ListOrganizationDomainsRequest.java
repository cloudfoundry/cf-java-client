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

package org.cloudfoundry.client.v3.organizations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.FilterParameter;
import org.cloudfoundry.client.v3.PaginatedRequest;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the List Organization Domains operation
 */
@Value.Immutable
abstract class _ListOrganizationDomainsRequest extends PaginatedRequest {

    /**
     * The domain ids to filter by
     */
    @FilterParameter("guids")
    @Nullable
    abstract List<String> getDomainIds();

    /**
     * The metadata query
     */
    @FilterParameter("label_selector")
    @Nullable
    abstract String getLabelSelector();

    /**
     * The names
     */
    @FilterParameter("names")
    @Nullable
    abstract List<String> getNames();

    /**
     * The organization id
     */
    @JsonIgnore
    abstract String getOrganizationId();

    /**
     * The owning organization ids
     */
    @FilterParameter("organization_guids")
    @Nullable
    abstract List<String> getOwningOrganizationIds();

}

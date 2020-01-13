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

package org.cloudfoundry.client.v2.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v2.FilterParameter;
import org.cloudfoundry.client.v2.PaginatedRequest;
import org.immutables.value.Value;

/**
 * The request payload for the List all Audited Spaces for the User operation
 */
@Value.Immutable
abstract class _ListUserAuditedSpacesRequest extends PaginatedRequest {

    /**
     * The application id
     */
    @FilterParameter("app_guid")
    @Nullable
    abstract String getApplicationId();

    /**
     * The developer id
     */
    @FilterParameter("developer_guid")
    @Nullable
    abstract String getDeveloperId();

    /**
     * The name
     */
    @FilterParameter("name")
    @Nullable
    abstract String getName();

    /**
     * The organization id
     */
    @FilterParameter("organization_guid")
    @Nullable
    abstract String getOrganizationId();

    /**
     * The id of the user
     */
    @JsonIgnore
    abstract String getUserId();

}

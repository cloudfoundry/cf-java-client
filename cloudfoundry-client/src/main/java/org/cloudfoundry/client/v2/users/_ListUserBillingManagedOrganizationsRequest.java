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
 * The request payload for the List all Billing Managed Organizations for the User operation
 */
@Value.Immutable
abstract class _ListUserBillingManagedOrganizationsRequest extends PaginatedRequest {

    /**
     * The auditor id
     */
    @FilterParameter("auditor_guid")
    @Nullable
    abstract String getAuditorId();

    /**
     * The billing manager id
     */
    @FilterParameter("billing_manager_guid")
    @Nullable
    abstract String getBillingManagerId();

    /**
     * The manager id
     */
    @FilterParameter("manager_guid")
    @Nullable
    abstract String getManagerId();

    /**
     * The name
     */
    @FilterParameter("name")
    @Nullable
    abstract String getName();

    /**
     * The space id
     */
    @FilterParameter("space_guid")
    @Nullable
    abstract String getSpaceId();

    /**
     * The status
     */
    @FilterParameter("status")
    @Nullable
    abstract String getStatus();

    /**
     * The id of the user
     */
    @JsonIgnore
    abstract String getUserId();

}

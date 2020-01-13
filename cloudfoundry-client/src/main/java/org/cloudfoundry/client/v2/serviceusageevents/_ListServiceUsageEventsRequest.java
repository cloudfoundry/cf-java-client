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

package org.cloudfoundry.client.v2.serviceusageevents;

import org.cloudfoundry.Nullable;
import org.cloudfoundry.QueryParameter;
import org.cloudfoundry.client.v2.FilterParameter;
import org.cloudfoundry.client.v2.PaginatedRequest;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for the List Service Usage Events operation
 */
@Value.Immutable
abstract class _ListServiceUsageEventsRequest extends PaginatedRequest {

    /**
     * The after service usage event id: Restrict results to Service Usage Events after the one with the given id
     */
    @Nullable
    @QueryParameter("after_guid")
    abstract String getAfterServiceUsageEventId();

    /**
     * The service ids
     */
    @FilterParameter("service_guid")
    @Nullable
    abstract List<String> getServiceIds();

    /**
     * The service instance types
     */
    @FilterParameter("service_instance_type")
    @Nullable
    abstract List<String> getServiceInstanceTypes();

}

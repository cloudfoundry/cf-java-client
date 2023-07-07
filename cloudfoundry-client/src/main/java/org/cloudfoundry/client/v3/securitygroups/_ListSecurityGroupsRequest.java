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

package org.cloudfoundry.client.v3.securitygroups;

import org.cloudfoundry.client.v2.PaginatedRequest;
import org.immutables.value.Value;
import org.cloudfoundry.client.v3.FilterParameter;
import org.cloudfoundry.Nullable;
import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;;

/**
 * The request payload for the List Security Group operation
 */
@JsonSerialize
@Value.Immutable
abstract class _ListSecurityGroupsRequest extends PaginatedRequest {

    /**
     * The security group ids filter
     */
    @FilterParameter("guids")
    abstract List<String> getSecurityGroupIds();

    /**
     * The security group names filter
     */
    @FilterParameter("names")
    abstract List<String> getNames();

    /**
     * the security group globally enabled running filter
     */
    @FilterParameter("globally_enabled_running")
    @Nullable
    abstract Boolean getGloballyEnabledRunning();

    /**
     * the security group globally enabled staging filter
     */
    @FilterParameter("globally_enabled_staging")
    @Nullable
    abstract Boolean getGloballyEnabledStagingBoolean();

    /**
     * the security group running_space_guids filter
     */
    @FilterParameter("running_space_guids")
    abstract List<String> getRunningSpaceIds();

    /**
     * the security group staging_space_guids filter
     */
    @FilterParameter("staging_space_guids")
    abstract List<String> getStagingSpaceIds();
}

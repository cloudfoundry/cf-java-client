/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cloudfoundry.client.v3.securitygroups;

import java.util.List;

import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v2.PaginatedRequest;
import org.cloudfoundry.client.v3.FilterParameter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractListSecurityGroupRequest extends PaginatedRequest {

    /**
     * The Space id
     */
    @JsonIgnore
    abstract String getSpaceId();

    /**
     * The security group ids filter
     */
    @FilterParameter("guids")
    @Nullable
    abstract List<String> getSecurityGroupIds();

    /**
     * The security group names filter
     */
    @FilterParameter("names")
    @Nullable
    abstract List<String> getNames();

}

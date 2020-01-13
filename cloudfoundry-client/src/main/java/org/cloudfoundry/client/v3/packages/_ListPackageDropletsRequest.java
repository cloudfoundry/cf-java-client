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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cloudfoundry.client.v3.FilterParameter;
import org.cloudfoundry.client.v3.PaginatedRequest;
import org.cloudfoundry.client.v3.droplets.DropletState;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
abstract class _ListPackageDropletsRequest extends PaginatedRequest {

    /**
     * The droplet ids to filter by
     */
    @FilterParameter("guids")
    abstract List<String> getDropletIds();

    /**
     * The package id
     */
    @JsonIgnore
    abstract String getPackageId();

    /**
     * The droplet states to filter by
     */
    @FilterParameter("states")
    abstract List<DropletState> getStates();

}

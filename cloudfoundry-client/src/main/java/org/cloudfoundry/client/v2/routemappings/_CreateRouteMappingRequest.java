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

package org.cloudfoundry.client.v2.routemappings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The request payload for the Create a Route Mapping operation
 */
@JsonSerialize
@Value.Immutable
abstract class _CreateRouteMappingRequest {

    /**
     * The application id
     */
    @JsonProperty("app_guid")
    abstract String getApplicationId();

    /**
     * The application port on which the application should listen, and to which requests for the mapped route will be routed.
     */
    @JsonProperty("app_port")
    @Nullable
    abstract Integer getApplicationPort();

    /**
     * The route id
     */
    @JsonProperty("route_guid")
    abstract String getRouteId();

}

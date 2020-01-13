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

package org.cloudfoundry.routing.v1.routergroups;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The payload for Router Group responses
 */
abstract class AbstractRouterGroup {

    /**
     * Name of the Router Group
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * Comma delimited list of reservable port or port ranges
     */
    @JsonProperty("reservable_ports")
    abstract String getReservablePorts();

    /**
     * ID of the Router Group
     */
    @JsonProperty("guid")
    abstract String getRouterGroupId();

    /**
     * Type of the router group, e.g. tcp
     */
    @JsonProperty("type")
    abstract String getType();

}

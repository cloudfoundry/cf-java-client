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

package org.cloudfoundry.routing.v1.tcproutes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

/**
 * The payload for TCP Route responses
 */
@JsonDeserialize
@Value.Immutable
abstract class _TcpRouteConfiguration {

    /**
     * IP address of backend.
     */
    @JsonProperty("backend_ip")
    abstract String getBackendIp();

    /**
     * Backend port.
     */
    @JsonProperty("backend_port")
    abstract Integer getBackendPort();

    /**
     * External facing port for the TCP route.
     */
    @JsonProperty("port")
    abstract Integer getPort();

    /**
     * ID of the router group associated with this route.
     */
    @JsonProperty("router_group_guid")
    abstract String getRouterGroupId();

    /**
     * Time to live, in seconds.
     */
    @JsonProperty("ttl")
    abstract Integer getTtl();

}

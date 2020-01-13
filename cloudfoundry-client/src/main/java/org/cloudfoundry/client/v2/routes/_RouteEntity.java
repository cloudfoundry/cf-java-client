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

package org.cloudfoundry.client.v2.routes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The entity response payload for the Route resource
 */
@JsonDeserialize
@Value.Immutable
abstract class _RouteEntity {

    /**
     * The applications url
     */
    @JsonProperty("apps_url")
    @Nullable
    abstract String getApplicationsUrl();

    /**
     * The domain id
     */
    @JsonProperty("domain_guid")
    @Nullable
    abstract String getDomainId();

    /**
     * The domain url
     */
    @JsonProperty("domain_url")
    @Nullable
    abstract String getDomainUrl();

    /**
     * The host
     */
    @JsonProperty("host")
    @Nullable
    abstract String getHost();

    /**
     * The path
     */
    @JsonProperty("path")
    @Nullable
    abstract String getPath();

    /**
     * The port
     */
    @JsonProperty("port")
    @Nullable
    abstract Integer getPort();

    /**
     * The route mappings url
     */
    @JsonProperty("route_mappings_url")
    @Nullable
    abstract String getRouteMappingsUrl();

    /**
     * The service instance id
     */
    @JsonProperty("service_instance_guid")
    @Nullable
    abstract String getServiceInstanceId();

    /**
     * The service instance url
     */
    @JsonProperty("service_instance_url")
    @Nullable
    abstract String getServiceInstanceUrl();

    /**
     * The space id
     */
    @JsonProperty("space_guid")
    @Nullable
    abstract String getSpaceId();

    /**
     * The space url
     */
    @JsonProperty("space_url")
    @Nullable
    abstract String getSpaceUrl();

}

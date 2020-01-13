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

package org.cloudfoundry.client.v2.applications;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.List;

/**
 * The entity response payload for the Application resource
 */
@JsonDeserialize
@Value.Immutable
abstract class _ApplicationEntity extends AbstractApplicationEntity {

    /**
     * The detected buildpack
     */
    @JsonProperty("detected_buildpack")
    @Nullable
    abstract String getDetectedBuildpack();

    /**
     * The detected buildpack id
     */
    @JsonProperty("detected_buildpack_guid")
    @Nullable
    abstract String getDetectedBuildpackId();

    /**
     * Whether SSH is enabled
     */
    @JsonProperty("enable_ssh")
    @Nullable
    abstract Boolean getEnableSsh();

    /**
     * The events url
     */
    @JsonProperty("events_url")
    @Nullable
    abstract String getEventsUrl();

    /**
     * The package state
     */
    @JsonProperty("package_state")
    @Nullable
    abstract String getPackageState();

    /**
     * When the package was update
     */
    @JsonProperty("package_updated_at")
    @Nullable
    abstract String getPackageUpdatedAt();

    /**
     * The ports
     */
    @JsonProperty("ports")
    @Nullable
    abstract List<Integer> getPorts();

    /**
     * The route mappings url
     */
    @JsonProperty("route_mappings_url")
    @Nullable
    abstract String getRouteMappingsUrl();

    /**
     * The routes url
     */
    @JsonProperty("routes_url")
    @Nullable
    abstract String getRoutesUrl();

    /**
     * The service bindings url
     */
    @JsonProperty("service_bindings_url")
    @Nullable
    abstract String getServiceBindingsUrl();

    /**
     * The space url
     */
    @JsonProperty("space_url")
    @Nullable
    abstract String getSpaceUrl();

    /**
     * The stack url
     */
    @JsonProperty("stack_url")
    @Nullable
    abstract String getStackUrl();

    /**
     * The version
     */
    @JsonProperty("version")
    @Nullable
    abstract String getVersion();

}

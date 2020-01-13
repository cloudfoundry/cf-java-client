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
import org.cloudfoundry.client.v2.domains.Domain;
import org.cloudfoundry.client.v2.routes.Route;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstance;
import org.immutables.value.Value;

import java.util.List;

/**
 * The response payload for the Get Application Summary operation
 */
@JsonDeserialize
@Value.Immutable
abstract class _SummaryApplicationResponse extends AbstractApplicationEntity {

    /**
     * The available domains
     */
    @JsonProperty("available_domains")
    @Nullable
    abstract List<Domain> getAvailableDomains();

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
     * The id
     */
    @JsonProperty("guid")
    @Nullable
    abstract String getId();

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
     * The routes
     */
    @JsonProperty("routes")
    @Nullable
    abstract List<Route> getRoutes();

    /**
     * The running instances
     */
    @JsonProperty("running_instances")
    @Nullable
    abstract Integer getRunningInstances();

    /**
     * The services
     */
    @JsonProperty("services")
    @Nullable
    abstract List<ServiceInstance> getServices();

    /**
     * The version
     */
    @JsonProperty("version")
    @Nullable
    abstract String getVersion();

}

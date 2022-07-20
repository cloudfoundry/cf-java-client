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

package org.cloudfoundry.client.v3.serviceinstances;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

/**
 * The request payload for the Create Service operation
 */
@JsonSerialize
@Value.Immutable
abstract class _CreateServiceInstanceRequest {

    /**
     * The type of the service instance
     */
    @JsonProperty("type")
    abstract ServiceInstanceType getType();

    /**
     * The name
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * The relationships
     */
    @JsonProperty("relationships")
    @Nullable
    abstract ServiceInstanceRelationships getRelationships();

    /**
     * A list of tags for the service instance
     */
    @JsonProperty("tags")
    @Nullable
    abstract List<String> getTags();

    /**
     * The service creation parameters
     */
    @JsonProperty("parameters")
    @Nullable
    abstract Map<String, Object> getParameters();

    /**
     * The metadata
     */
    @JsonProperty("metadata")
    @Nullable
    abstract Metadata getMetadata();

    /**
     * The user provided service credentials
     */
    @JsonProperty("credentials")
    @Nullable
    abstract Map<String, Object> getCredentials();

    /**
     * The syslog drain url
     */
    @JsonProperty("syslog_drain_url")
    @Nullable
    abstract String getSyslogDrainUrl();

    /**
     * The route service url
     */
    @JsonProperty("route_service_url")
    @Nullable
    abstract String getRouteServiceUrl();

}

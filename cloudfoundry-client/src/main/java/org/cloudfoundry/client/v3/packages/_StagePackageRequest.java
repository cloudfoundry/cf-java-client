/*
 * Copyright 2013-2016 the original author or authors.
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
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Lifecycle;
import org.immutables.value.Value;

import java.util.Map;

/**
 * The request payload for the Stage Package operation
 */
@Value.Immutable
abstract class _StagePackageRequest {

    /**
     * The environment variables
     */
    @JsonProperty("environment_variables")
    @Nullable
    abstract Map<String, Object> getEnvironmentVariables();

    /**
     * The lifecycle
     */
    @JsonProperty("lifecycle")
    @Nullable
    abstract Lifecycle getLifecycle();

    /**
     * The package id
     */
    @JsonIgnore
    abstract String getPackageId();

    /**
     * The staging disk in MB
     */
    @JsonProperty("staging_disk_in_mb")
    @Nullable
    abstract Integer getStagingDiskInMb();

    /**
     * The staging memory in MB
     */
    @JsonProperty("staging_memory_in_mb")
    @Nullable
    abstract Integer getStagingMemoryInMb();

}

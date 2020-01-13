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

package org.cloudfoundry.client.v3.droplets;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Checksum;
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Resource;

import java.util.List;
import java.util.Map;

/**
 * Base class for responses that are droplets
 */
public abstract class Droplet extends Resource {

    /**
     * The buildpacks
     */
    @JsonProperty("buildpacks")
    @Nullable
    public abstract List<Buildpack> getBuildpacks();

    /**
     * The checksum
     */
    @JsonProperty("checksum")
    @Nullable
    public abstract Checksum getChecksum();

    /**
     * The error
     */
    @JsonProperty("error")
    @Nullable
    public abstract String getError();

    /**
     * Serialized JSON data resulting from staging for use when executing a droplet
     */
    @JsonProperty("execution_metadata")
    public abstract String getExecutionMetadata();

    /**
     * The docker image
     */
    @JsonProperty("image")
    @Nullable
    public abstract String getImage();

    /**
     * The lifecycle
     */
    @JsonProperty("lifecycle")
    public abstract Lifecycle getLifecycle();

    /**
     * The metadata
     */
    @JsonProperty("metadata")
    @Nullable
    public abstract Metadata getMetadata();

    /**
     * The process types and associated start commands
     */
    @JsonProperty("process_types")
    @Nullable
    public abstract Map<String, String> getProcessTypes();

    /**
     * The relationships
     */
    @JsonProperty("relationships")
    @Nullable
    public abstract DropletRelationships getRelationships();

    /**
     * The stack
     */
    @JsonProperty("stack")
    @Nullable
    public abstract String getStack();

    /**
     * The state
     */
    @JsonProperty("state")
    public abstract DropletState getState();

}

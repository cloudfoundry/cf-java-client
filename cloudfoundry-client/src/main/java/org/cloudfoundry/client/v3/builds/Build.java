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

package org.cloudfoundry.client.v3.builds;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.Resource;

/**
 * Base class for responses that are builds
 */
public abstract class Build extends Resource {

    /**
     * The user that created the build
     */
    @JsonProperty("created_by")
    public abstract CreatedBy getCreatedBy();

    /**
     * A resulting droplet from the staging process
     */
    @JsonProperty("droplet")
    @Nullable
    public abstract Droplet getDroplet();

    /**
     * Describes errors during the build process
     */
    @JsonProperty("error")
    @Nullable
    public abstract String getError();

    /**
     * The package that is the input to the staging process
     */
    @JsonProperty("package")
    public abstract Relationship getInputPackage();

    /**
     * The lifecycle that was configured or discovered from the application
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
     * The relationships
     */
    @JsonProperty("relationships")
    @Nullable
    public abstract BuildRelationships getRelationships();

    /**
     * State of the build
     */
    @JsonProperty("state")
    public abstract BuildState getState();

    /**
     * Memory used for the build (MB)
     */
    @JsonProperty("staging_memory_in_mb")
    @Nullable
    public abstract Integer getStagingMemory();

    /**
     * Disk space used for the build (MB)
     */
    @JsonProperty("staging_disk_in_mb")
    @Nullable
    public abstract Integer getStagingDisk();
}

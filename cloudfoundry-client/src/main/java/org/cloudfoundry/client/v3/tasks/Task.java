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

package org.cloudfoundry.client.v3.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Resource;

/**
 * Base class for responses that are tasks
 */
public abstract class Task extends Resource {

    /**
     * The command that will be executed
     */
    @JsonProperty("command")
    @Nullable
    public abstract String getCommand();

    /**
     * The amount of disk to allocate for the task in MB
     */
    @JsonProperty("disk_in_mb")
    public abstract Integer getDiskInMb();

    /**
     * The id of the droplet that will be used to run the command
     */
    @JsonProperty("droplet_guid")
    public abstract String getDropletId();

    /**
     * The amount of memory to allocate for the task in MB
     */
    @JsonProperty("memory_in_mb")
    public abstract Integer getMemoryInMb();

    /**
     * The metadata
     */
    @JsonProperty("metadata")
    @Nullable
    public abstract Metadata getMetadata();

    /**
     * The task's name
     */
    @JsonProperty("name")
    public abstract String getName();

    /**
     * The task result
     */
    @JsonProperty("result")
    @Nullable
    public abstract Result getResult();

    /**
     * The user-facing id of the task
     */
    @JsonProperty("sequence_id")
    public abstract Integer getSequenceId();

    /**
     * The state of the task
     */
    @JsonProperty("state")
    public abstract TaskState getState();

    /**
     * The task relationships
     */
    @JsonProperty("relationships")
    @Nullable
    public abstract TaskRelationships getTaskRelationships();

}

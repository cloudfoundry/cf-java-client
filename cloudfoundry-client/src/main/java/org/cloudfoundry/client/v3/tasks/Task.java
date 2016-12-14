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

package org.cloudfoundry.client.v3.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.client.v3.Link;

import java.util.Map;

/**
 * Base class for responses that are tasks
 */
public abstract class Task {

    /**
     * The command
     */
    @JsonProperty("command")
    @Nullable
    public abstract String getCommand();

    /**
     * The created at
     */
    @JsonProperty("created_at")
    @Nullable
    public abstract String getCreatedAt();

    /**
     * The environment variables
     */
    @AllowNulls
    @JsonProperty("environment_variables")
    @Nullable
    public abstract Map<String, String> getEnvironmentVariables();

    /**
     * The id
     */
    @JsonProperty("guid")
    @Nullable
    public abstract String getId();

    /**
     * The links
     */
    @AllowNulls
    @JsonProperty("links")
    @Nullable
    public abstract Map<String, Link> getLinks();

    /**
     * The memory in megabytes=
     */
    @JsonProperty("memory_in_mb")
    @Nullable
    public abstract Integer getMemoryInMb();

    /**
     * The tasks name
     */
    @JsonProperty("name")
    @Nullable
    public abstract String getName();

    /**
     * The task result
     */
    @JsonProperty("result")
    @Nullable
    public abstract Result getResult();

    /**
     * The task state
     */
    @JsonProperty("state")
    @Nullable
    public abstract State getState();

    /**
     * The updated at
     */
    @JsonProperty("updated_at")
    @Nullable
    public abstract String getUpdatedAt();

}

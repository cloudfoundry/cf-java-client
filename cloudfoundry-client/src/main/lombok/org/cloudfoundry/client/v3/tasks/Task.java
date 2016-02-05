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
import lombok.Data;
import lombok.Singular;
import org.cloudfoundry.client.v3.Link;

import java.util.Map;

/**
 * Base class for responses that are tasks
 */
@Data
public abstract class Task {

    public static String FAILED_STATE = "FAILED";

    public static String RUNNING_STATE = "RUNNING";

    public static String SUCCEEDED_STATE = "SUCCEEDED";

    /**
     * The command
     *
     * @param command the command
     * @return the command
     */
    private final String command;

    /**
     * The created at
     *
     * @param createdAt the created at
     * @return the created at
     */
    private final String createdAt;
    
    /**
     * The environment variables
     *
     * @param environmentVariables the environment variables
     * @return the environment variables
     */
    private final Map<String, String> environmentVariables;
    
    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    private final String id;

    /**
     * The links
     *
     * @param links the links
     * @return the links
     */
    private final Map<String, Link> links;

    /**
     * The memory in megabytes
     *
     * @param memoryInMb the memory in megabytes
     * @return the memory in megabytes
     */
    private final Integer memoryInMb;

    /**
     * The tasks name
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The task result
     *
     * @param result the task result
     * @return the task result
     */
    private final Map<String, Object> results;

    /**
     * The tasks state, one of RUNNING, FAILED or SUCCEEDED
     *
     * @param state the state
     * @return the state
     */
    private final String state;

    /**
     * The updated at
     *
     * @param updatedAt the updated at
     * @return the updated at
     */
    private final String updatedAt;

    protected Task(@JsonProperty("command") String command,
                   @JsonProperty("created_at") String createdAt,
                   @JsonProperty("result") @Singular Map<String, Object> results,
                   @JsonProperty("environment_variables") @Singular Map<String, String> environmentVariables,
                   @JsonProperty("guid") String id,
                   @JsonProperty("name") String name,
                   @JsonProperty("links") @Singular Map<String, Link> links,
                   @JsonProperty("memory_in_mb") Integer memoryInMb,
                   @JsonProperty("state") String state,
                   @JsonProperty("updated_at") String updatedAt) {
        this.command = command;
        this.createdAt = createdAt;
        this.results = results;
        this.environmentVariables = environmentVariables;
        this.id = id;
        this.name = name;
        this.links = links;
        this.memoryInMb = memoryInMb;
        this.state = state;
        this.updatedAt = updatedAt;
    }

}

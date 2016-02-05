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

package org.cloudfoundry.client.v3.droplets;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Singular;
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.Link;

import java.util.Map;

/**
 * Base class for responses that are droplets
 */
@Data
public abstract class Droplet {

    /**
     * The created at
     *
     * @param createdAt the created at
     * @return the created at
     */
    private final String createdAt;

    /**
     * The disk limit
     *
     * @param diskLimit the disk limit
     * @return the disk limit
     */
    private final Integer diskLimit;

    /**
     * The environment variables
     *
     * @param environmentVariables the environment variables
     * @return the environment variables
     */
    private final Map<String, Object> environmentVariables;

    /**
     * The error
     *
     * @param error the error
     * @return the error
     */
    private final String error;

    /**
     * The id
     *
     * @param id the id
     * @return id
     */
    private final String id;

    /**
     * The lifecycle
     *
     * @param lifecycle lifecycle
     * @return lifecycle
     */
    private final Lifecycle lifecycle;

    /**
     * The links
     *
     * @param links the links
     * @return links
     */
    private final Map<String, Link> links;

    /**
     * The memory limit
     *
     * @param diskLimit the memory limit
     * @return the memory limit
     */
    private final Integer memoryLimit;

    /**
     * The results
     *
     * @param results the results
     * @return the results
     */
    private final Map<String, Object> results;

    /**
     * The state
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

    protected Droplet(@JsonProperty("created_at") String createdAt,
                      @JsonProperty("disk_limit") Integer diskLimit,
                      @JsonProperty("environment_variables") @Singular Map<String, Object> environmentVariables,
                      @JsonProperty("error") String error,
                      @JsonProperty("lifecycle") Lifecycle lifecycle,
                      @JsonProperty("guid") String id,
                      @JsonProperty("links") @Singular Map<String, Link> links,
                      @JsonProperty("memory_limit") Integer memoryLimit,
                      @JsonProperty("result") @Singular Map<String, Object> results,
                      @JsonProperty("state") String state,
                      @JsonProperty("updated_at") String updatedAt) {
        this.createdAt = createdAt;
        this.diskLimit = diskLimit;
        this.environmentVariables = environmentVariables;
        this.error = error;
        this.lifecycle = lifecycle;
        this.id = id;
        this.links = links;
        this.memoryLimit = memoryLimit;
        this.results = results;
        this.state = state;
        this.updatedAt = updatedAt;
    }

}

/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.v3.processes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Singular;
import org.cloudfoundry.client.v3.Link;

import java.util.Map;

/**
 * Base class for responses that are processes
 */
@Data
public abstract class Process {

    /**
     * The created at
     *
     * @param createdAt the created at
     * @return the created at
     */
    private final String createdAt;

    /**
     * The command
     *
     * @param command the command
     * @return the command
     */
    private final String command;

    /**
     * The disk in megabytes
     *
     * @param diskInMb the disk in megabytes
     * @return the disk in megabytes
     */
    private final Integer diskInMb;

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
     * The instances
     *
     * @param instances the instances
     * @return the instances
     */
    private final Integer instances;

    /**
     * The memory in megabytes
     *
     * @param memoryInMb the memory in megabytes
     * @return the memory in megabytes
     */
    private final Integer memoryInMb;

    /**
     * The type
     *
     * @param type the type
     * @return the type
     */
    private final String type;

    /**
     * The updated at
     *
     * @param updatedAt the updated at
     * @return the updated at
     */
    private final String updatedAt;

    protected Process(@JsonProperty("created_at") String createdAt,
                      @JsonProperty("command") String command,
                      @JsonProperty("disk_in_mb") Integer diskInMb,
                      @JsonProperty("guid") String id,
                      @JsonProperty("_links") @Singular Map<String, Link> links,
                      @JsonProperty("instances") Integer instances,
                      @JsonProperty("memory_in_mb") Integer memoryInMb,
                      @JsonProperty("type") String type,
                      @JsonProperty("updated_at") String updatedAt) {
        this.createdAt = createdAt;
        this.command = command;
        this.diskInMb = diskInMb;
        this.id = id;
        this.links = links;
        this.instances = instances;
        this.memoryInMb = memoryInMb;
        this.type = type;
        this.updatedAt = updatedAt;
    }

}

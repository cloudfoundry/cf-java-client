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

package org.cloudfoundry.client.v3.applications;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.LinkBased;

import java.util.HashMap;
import java.util.Map;

/**
 * The response payload for the Scale Application operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class ScaleApplicationResponse implements LinkBased {

    private volatile String createdAt;

    private volatile String command;

    private volatile Integer diskInMb;

    private volatile String id;

    private final Map<String, Link> links = new HashMap<>();

    private volatile Integer instances;

    private volatile Integer memoryInMb;

    private volatile String type;

    private volatile String updatedAt;

    /**
     * Returns the created at
     *
     * @return the created at
     */
    public final String getCreatedAt() {
        return this.createdAt;
    }

    /**
     * Configure the created at
     *
     * @param createdAt the created at
     * @return {@code this}
     */
    @JsonProperty("created_at")
    public final ScaleApplicationResponse withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    /**
     * Returns the command
     *
     * @return the command
     */
    public final String getCommand() {
        return this.command;
    }

    /**
     * Configure the command
     *
     * @param command the command
     * @return {@code this}
     */
    @JsonProperty("command")
    public final ScaleApplicationResponse withCommand(String command) {
        this.command = command;
        return this;
    }

    /**
     * Returns the disk space in Mb for the application
     *
     * @return the disk space in Mb
     */
    public final Integer getDiskInMb() {
        return this.diskInMb;
    }

    /**
     * Configure the disk space in Mb for the application
     *
     * @param diskInMb the disk space in Mb
     * @return {@code this}
     */
    @JsonProperty("disk_in_mb")
    public final ScaleApplicationResponse withDiskInMb(Integer diskInMb) {
        this.diskInMb = diskInMb;
        return this;
    }

    /**
     * Returns the id
     *
     * @return the id
     */
    public final String getId() {
        return this.id;
    }

    /**
     * Configure the id
     *
     * @param id the id
     * @return {@code this}
     */
    @JsonProperty("guid")
    public final ScaleApplicationResponse withId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public final Link getLink(String rel) {
        return this.links.get(rel);
    }

    /**
     * Returns the links
     *
     * @return the links
     */
    @Override
    public final Map<String, Link> getLinks() {
        return this.links;
    }

    /**
     * Add a link
     *
     * @param rel  the rel
     * @param link the link
     * @return {@code this}
     */
    public final ScaleApplicationResponse withLink(String rel, Link link) {
        this.links.put(rel, link);
        return this;
    }

    /**
     * Add links
     *
     * @param links the links
     * @return {@code this}
     */
    @JsonProperty("_links")
    public final ScaleApplicationResponse withLinks(Map<String, Link> links) {
        this.links.putAll(links);
        return this;
    }

    /**
     * Returns the number instances
     *
     * @return the number instances
     */
    public final Integer getInstances() {
        return this.instances;
    }

    /**
     * Configure the number of instances
     *
     * @param instances the number of instances
     * @return {@code this}
     */
    @JsonProperty("instances")
    public final ScaleApplicationResponse withInstances(Integer instances) {
        this.instances = instances;
        return this;
    }

    /**
     * Returns the memory in Mb for the application
     *
     * @return the memory in Mb
     */
    public final Integer getMemoryInMb() {
        return this.memoryInMb;
    }

    /**
     * Configure the memory in Mb for the application
     *
     * @param memoryInMb the memory in Mb
     * @return {@code this}
     */
    @JsonProperty("memory_in_mb")
    public final ScaleApplicationResponse withMemoryInMb(Integer memoryInMb) {
        this.memoryInMb = memoryInMb;
        return this;
    }

    /**
     * Returns the type
     *
     * @return the type
     */
    public final String getType() {
        return this.type;
    }

    /**
     * Configure the type
     *
     * @param type the type
     * @return {@code this}
     */
    @JsonProperty("type")
    public final ScaleApplicationResponse withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Returns the updated at
     *
     * @return the updated at
     */
    public final String getUpdatedAt() {
        return this.updatedAt;
    }

    /**
     * Configure the updated at
     *
     * @param updatedAt the updated at
     * @return {@code this}
     */
    @JsonProperty("updated_at")
    public final ScaleApplicationResponse withUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
}

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
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.LinkBased;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for responses that are processes
 *
 * <p><b>This class is NOT threadsafe.</b>
 *
 * @param <T> the "self" type.  Used to ensure the appropriate type is returned from builder APIs.
 */
public abstract class Process<T extends Process<T>> implements LinkBased {

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
    @SuppressWarnings("unchecked")
    public final T withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return (T) this;
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
    @SuppressWarnings("unchecked")
    public final T withCommand(String command) {
        this.command = command;
        return (T) this;
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
    @SuppressWarnings("unchecked")
    public final T withDiskInMb(Integer diskInMb) {
        this.diskInMb = diskInMb;
        return (T) this;
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
    @SuppressWarnings("unchecked")
    public final T withId(String id) {
        this.id = id;
        return (T) this;
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
    @SuppressWarnings("unchecked")
    public final T withLink(String rel, Link link) {
        this.links.put(rel, link);
        return (T) this;
    }

    /**
     * Add links
     *
     * @param links the links
     * @return {@code this}
     */
    @JsonProperty("_links")
    @SuppressWarnings("unchecked")
    public final T withLinks(Map<String, Link> links) {
        this.links.putAll(links);
        return (T) this;
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
    @SuppressWarnings("unchecked")
    public final T withInstances(Integer instances) {
        this.instances = instances;
        return (T) this;
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
    @SuppressWarnings("unchecked")
    public final T withMemoryInMb(Integer memoryInMb) {
        this.memoryInMb = memoryInMb;
        return (T) this;
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
    @SuppressWarnings("unchecked")
    public final T withType(String type) {
        this.type = type;
        return (T) this;
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
    @SuppressWarnings("unchecked")
    public final T withUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        return (T) this;
    }
}

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

package org.cloudfoundry.client.v3.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.LinkBased;

import java.util.HashMap;
import java.util.Map;

abstract class Application<T extends Application<T>> implements LinkBased {

    private volatile String buildpack;

    private volatile String createdAt;

    private volatile String desiredState;

    private final Map<String, String> environmentVariables = new HashMap<>();

    private volatile String id;

    private final Map<String, Link> links = new HashMap<>();

    private volatile String name;

    private volatile Integer totalDesiredInstances;

    private volatile String updatedAt;

    /**
     * Returns the buildpack
     *
     * @return the buildpack
     */
    public final String getBuildpack() {
        return this.buildpack;
    }

    /**
     * Configure the buildpack
     *
     * @param buildpack the buildpack
     * @return {@code this}
     */
    @JsonProperty("buildpack")
    @SuppressWarnings("unchecked")
    public final T withBuildpack(String buildpack) {
        this.buildpack = buildpack;
        return (T) this;
    }

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
     * Returns the desired state
     *
     * @return the desired state
     */
    public final String getDesiredState() {
        return this.desiredState;
    }

    /**
     * Configure the desired state
     *
     * @param desiredState the desired state
     * @return {@code this}
     */
    @JsonProperty("desired_state")
    @SuppressWarnings("unchecked")
    public final T withDesiredState(String desiredState) {
        this.desiredState = desiredState;
        return (T) this;
    }

    /**
     * Returns the environment variables
     *
     * @return the environment variables
     */
    public final Map<String, String> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    /**
     * Add an environment variable
     *
     * @param key   the environment variable key
     * @param value the environment variable value
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    public final T withEnvironmentVariable(String key, String value) {
        this.environmentVariables.put(key, value);
        return (T) this;
    }

    /**
     * Add environment variables
     *
     * @param environmentVariables the environment variables
     * @return {@code this}
     */
    @JsonProperty("environment_variables")
    @SuppressWarnings("unchecked")
    public final T withEnvironmentVariables(Map<String, String> environmentVariables) {
        this.environmentVariables.putAll(environmentVariables);
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
     * Returns the name
     *
     * @return the name
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Configure the name
     *
     * @param name the name
     * @return {@code this}
     */
    @JsonProperty("name")
    @SuppressWarnings("unchecked")
    public final T withName(String name) {
        this.name = name;
        return (T) this;
    }

    /**
     * Returns the total desired instances
     *
     * @return the total desired instances
     */
    public final Integer getTotalDesiredInstances() {
        return this.totalDesiredInstances;
    }

    /**
     * Configure the total desired instances
     *
     * @param totalDesiredInstances the total desired instances
     * @return {@code this}
     */
    @JsonProperty("total_desired_instances")
    @SuppressWarnings("unchecked")
    public final T withTotalDesiredInstances(Integer totalDesiredInstances) {
        this.totalDesiredInstances = totalDesiredInstances;
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

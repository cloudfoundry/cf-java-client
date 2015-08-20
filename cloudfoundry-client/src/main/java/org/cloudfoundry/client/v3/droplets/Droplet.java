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

package org.cloudfoundry.client.v3.droplets;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.client.v3.Hash;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.LinkBased;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for responses that are droplets
 *
 * <p><b>This class is NOT threadsafe.</b>
 *
 * @param <T> the "self" type.  Used to ensure the appropriate type is returned from builder APIs.
 */
public abstract class Droplet<T extends Droplet<T>> implements LinkBased {

    private volatile String buildpack;

    private volatile String createdAt;

    private final Map<String, Object> environmentVariables = new HashMap<>();

    private volatile String error;

    private volatile Hash hash;

    private volatile String id;

    private final Map<String, Link> links = new HashMap<>();

    private volatile String procfile;

    private volatile String state;

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
     * Returns the environment variables
     *
     * @return the environment variables
     */
    public final Map<String, Object> getEnvironmentVariables() {
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
    public final T withEnvironmentVariable(String key, Object value) {
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
    public final T withEnvironmentVariables(Map<String, Object> environmentVariables) {
        this.environmentVariables.putAll(environmentVariables);
        return (T) this;
    }

    /**
     * Returns the error
     *
     * @return the error
     */
    public final String getError() {
        return this.error;
    }

    /**
     * Configure the error
     *
     * @param error the error
     * @return {@code this}
     */
    @JsonProperty("error")
    @SuppressWarnings("unchecked")
    public final T withError(String error) {
        this.error = error;
        return (T) this;
    }

    /**
     * Returns the hash
     *
     * @return the hash
     */
    public final Hash getHash() {
        return this.hash;
    }

    /**
     * Configure the hash
     *
     * @param hash the hash
     * @return {@code this}
     */
    @JsonProperty("hash")
    @SuppressWarnings("unchecked")
    public final T withHash(Hash hash) {
        this.hash = hash;
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
     * Returns the procfile
     *
     * @return the procfile
     */
    public final String getProcfile() {
        return this.procfile;
    }

    /**
     * Configure the procfile
     *
     * @param procfile the procfile
     * @return {@code this}
     */
    @JsonProperty("procfile")
    @SuppressWarnings("unchecked")
    public final T withProcfile(String procfile) {
        this.procfile = procfile;
        return (T) this;
    }

    /**
     * Returns the state
     *
     * @return the state
     */
    public final String getState() {
        return this.state;
    }

    /**
     * Configure the state
     *
     * @param state the state
     * @return {@code this}
     */
    @JsonProperty("state")
    @SuppressWarnings("unchecked")
    public final T withState(String state) {
        this.state = state;
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

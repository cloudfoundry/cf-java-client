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

package org.cloudfoundry.v3.client.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.v3.client.Link;
import org.cloudfoundry.v3.client.LinkBased;

import java.util.HashMap;
import java.util.Map;

/**
 * The response payload for the Create Application operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class CreateApplicationResponse implements LinkBased {

    private volatile String buildpack;

    private volatile String createdAt;

    private volatile String desiredState;

    private volatile Map<String, String> environmentVariables = new HashMap<>();

    private volatile String id;

    private volatile Map<String, Link> links = new HashMap<>();

    private volatile String name;

    private volatile Integer totalDesiredInstances;

    private volatile String updatedAt;

    public String getBuildpack() {
        return this.buildpack;
    }

    @JsonProperty("buildpack")
    public CreateApplicationResponse withBuildpack(String buildpack) {
        this.buildpack = buildpack;
        return this;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    @JsonProperty("created_at")
    public CreateApplicationResponse withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getDesiredState() {
        return this.desiredState;
    }

    @JsonProperty("desired_state")
    public CreateApplicationResponse withDesiredState(String desiredState) {
        this.desiredState = desiredState;
        return this;
    }

    public Map<String, String> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    /**
     * Add an environment variable
     *
     * @param key   the environment variable key
     * @param value the environment variable value
     * @return {@code this}
     */
    public CreateApplicationResponse withEnvironmentVariable(String key, String value) {
        this.environmentVariables.put(key, value);
        return this;
    }

    /**
     * Add environment variables
     *
     * @param environmentVariables the environment variables
     * @return {@code this}
     */
    @JsonProperty("environment_variables")
    public CreateApplicationResponse withEnvironmentVariables(Map<String, String> environmentVariables) {
        this.environmentVariables.putAll(environmentVariables);
        return this;
    }

    public String getId() {
        return this.id;
    }

    @JsonProperty("guid")
    public CreateApplicationResponse withId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public Link getLink(String rel) {
        return this.links.get(rel);
    }

    @Override
    public Map<String, Link> getLinks() {
        return this.links;
    }

    public CreateApplicationResponse withLink(String rel, Link link) {
        this.links.put(rel, link);
        return this;
    }

    @JsonProperty("_links")
    public CreateApplicationResponse withLinks(Map<String, Link> links) {
        this.links.putAll(links);
        return this;
    }

    public String getName() {
        return this.name;
    }

    @JsonProperty("name")
    public CreateApplicationResponse withName(String name) {
        this.name = name;
        return this;
    }

    public Integer getTotalDesiredInstances() {
        return this.totalDesiredInstances;
    }

    @JsonProperty("total_desired_instances")
    public CreateApplicationResponse withTotalDesiredInstances(Integer totalDesiredInstances) {
        this.totalDesiredInstances = totalDesiredInstances;
        return this;
    }

    public String getUpdatedAt() {
        return this.updatedAt;
    }

    @JsonProperty("updated_at")
    public CreateApplicationResponse withUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

}

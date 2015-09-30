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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

import java.util.HashMap;
import java.util.Map;

/**
 * The request payload for the Update Application operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class UpdateApplicationRequest implements Validatable {

    private volatile String buildpack;

    private final Map<String, String> environmentVariables = new HashMap<>();

    private volatile String id;

    private volatile String name;

    /**
     * Returns the buildpack
     *
     * @return the buildpack
     */
    @JsonProperty("buildpack")
    public String getBuildpack() {
        return this.buildpack;
    }

    /**
     * Configure the buildpack
     *
     * @param buildpack the buildpack
     * @return {@code this}
     */
    public UpdateApplicationRequest withBuildpack(String buildpack) {
        this.buildpack = buildpack;
        return this;
    }

    /**
     * Returns the environment variables
     *
     * @return the environment variables
     */
    @JsonProperty("environment_variables")
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
    public UpdateApplicationRequest withEnvironmentVariable(String key, String value) {
        this.environmentVariables.put(key, value);
        return this;
    }

    /**
     * Add environment variables
     *
     * @param environmentVariables the environment variables
     * @return {@code this}
     */
    public UpdateApplicationRequest withEnvironmentVariables(Map<String, String> environmentVariables) {
        this.environmentVariables.putAll(environmentVariables);
        return this;
    }

    /**
     * Returns the id
     *
     * @return the id
     */
    @JsonIgnore
    public String getId() {
        return this.id;
    }

    /**
     * Configure the id
     *
     * @param id the id
     * @return {@code this}
     */
    public UpdateApplicationRequest withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns the name
     *
     * @return the name
     */
    @JsonProperty("name")
    public String getName() {
        return this.name;
    }

    /**
     * Configure the name
     *
     * @param name the name
     * @return {@code this}
     */
    public UpdateApplicationRequest withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult result = new ValidationResult();

        if (this.id == null) {
            result.invalid("id must be specified");
        }

        return result;
    }

}

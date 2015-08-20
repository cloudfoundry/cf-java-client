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

package org.cloudfoundry.client.v3.packages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

import java.util.HashMap;
import java.util.Map;

/**
 * The request payload for the Stage Package operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class StagePackageRequest implements Validatable {

    private volatile String buildpack;

    private volatile Integer diskLimit;

    private final Map<String, Object> environmentVariables = new HashMap<>();

    private volatile String id;

    private volatile Integer memoryLimit;

    private volatile String stack;

    /**
     * Returns the buildpack
     *
     * @return the buildpack
     */
    @JsonProperty("buildpack")
    public String getBuildpack() {
        return buildpack;
    }

    /**
     * Configure the buildpack
     *
     * @param buildpack the buildpack
     * @return {@code this}
     */
    public StagePackageRequest withBuildpack(String buildpack) {
        this.buildpack = buildpack;
        return this;
    }

    /**
     * Returns the disk limit
     *
     * @return the disk limit
     */
    @JsonProperty("disk_limit")
    public Integer getDiskLimit() {
        return diskLimit;
    }

    /**
     * Returns the disk limit
     *
     * @param diskLimit the disk limit
     * @return {@code this}
     */
    public StagePackageRequest withDiskLimit(Integer diskLimit) {
        this.diskLimit = diskLimit;
        return this;
    }

    /**
     * Returns the environment variables
     *
     * @return the environment variables
     */
    @JsonProperty("environment_variables")
    public Map<String, Object> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    /**
     * Add an environment variable
     *
     * @param key   the environment variable key
     * @param value the environment variable value
     * @return {@code this}
     */
    public StagePackageRequest withEnvironmentVariable(String key, Object value) {
        this.environmentVariables.put(key, value);
        return this;
    }

    /**
     * Add environment variables
     *
     * @param environmentVariables the environment variables
     * @return {@code this}
     */
    public StagePackageRequest withEnvironmentVariables(Map<String, Object> environmentVariables) {
        this.environmentVariables.putAll(environmentVariables);
        return this;
    }

    /**
     * Returns the memory limit
     *
     * @return the memory limit
     */
    @JsonProperty("memory_limit")
    public Integer getMemoryLimit() {
        return memoryLimit;
    }

    /**
     * Configure the memory limit
     *
     * @param memoryLimit the memory limit
     * @return {@code this}
     */
    public StagePackageRequest withMemoryLimit(Integer memoryLimit) {
        this.memoryLimit = memoryLimit;
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
    public StagePackageRequest withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns the stack
     *
     * @return the stack
     */
    @JsonProperty("stack")
    public String getStack() {
        return stack;
    }

    /**
     * Configure the stack
     *
     * @param stack the stack
     * @return {@code this}
     */
    public StagePackageRequest withStack(String stack) {
        this.stack = stack;
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

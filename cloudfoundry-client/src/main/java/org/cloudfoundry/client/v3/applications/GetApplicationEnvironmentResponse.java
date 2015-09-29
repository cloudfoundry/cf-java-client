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

import java.util.HashMap;
import java.util.Map;

/**
 * The response payload for the Get Application Environment operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class GetApplicationEnvironmentResponse {

    private final Map<String, Object> applicationEnvironmentVariables = new HashMap<>();

    private final Map<String, Object> environmentVariables = new HashMap<>();

    private final Map<String, Object> runningEnvironmentVariables = new HashMap<>();

    private final Map<String, Object> stagingEnvironmentVariables = new HashMap<>();

    /**
     * Returns the application environment variables
     *
     * @return the application environment variables
     */
    public Map<String, Object> getApplicationEnvironmentVariables() {
        return this.applicationEnvironmentVariables;
    }

    /**
     * Configure an application environment variable
     *
     * @param key   the key
     * @param value the value
     * @return {@code this}
     */
    public GetApplicationEnvironmentResponse withApplicationEnvironmentVariable(String key, Object value) {
        this.applicationEnvironmentVariables.put(key, value);
        return this;
    }

    /**
     * Configure the application environment variables
     *
     * @param applicationEnvironmentVariables the application environment variables
     * @return {@code this}
     */
    @JsonProperty("application_env_json")
    public GetApplicationEnvironmentResponse withApplicationEnvironmentVariables(
            Map<String, Object> applicationEnvironmentVariables) {
        this.applicationEnvironmentVariables.putAll(applicationEnvironmentVariables);
        return this;
    }

    /**
     * Returns the environment variables
     *
     * @return the environment variables
     */
    public Map<String, Object> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    /**
     * Configure an environment variable
     *
     * @param key   the key
     * @param value the value
     * @return {@code this}
     */
    public GetApplicationEnvironmentResponse withEnvironmentVariable(String key, Object value) {
        this.environmentVariables.put(key, value);
        return this;
    }

    /**
     * Configure the environment variables
     *
     * @param environmentVariables the environment variables
     * @return {@code this}
     */
    @JsonProperty("environment_variables")
    public GetApplicationEnvironmentResponse withEnvironmentVariables(Map<String, Object> environmentVariables) {
        this.environmentVariables.putAll(environmentVariables);
        return this;
    }

    /**
     * Returns the running environment variables
     *
     * @return the running environment variables
     */
    public Map<String, Object> getRunningEnvironmentVariables() {
        return this.runningEnvironmentVariables;
    }

    /**
     * Configure a running environment variable
     *
     * @param key   the key
     * @param value the value
     * @return {@code this}
     */
    public GetApplicationEnvironmentResponse withRunningEnvironmentVariable(String key, Object value) {
        this.runningEnvironmentVariables.put(key, value);
        return this;
    }

    /**
     * Configure the running environment variables
     *
     * @param runningEnvironmentVariables the running environment variables
     * @return {@code this}
     */
    @JsonProperty("running_env_json")
    public GetApplicationEnvironmentResponse withRunningEnvironmentVariables(
            Map<String, Object> runningEnvironmentVariables) {
        this.runningEnvironmentVariables.putAll(runningEnvironmentVariables);
        return this;
    }

    /**
     * Returns the staging environment variables
     *
     * @return the staging environment variables
     */
    public Map<String, Object> getStagingEnvironmentVariables() {
        return this.stagingEnvironmentVariables;
    }

    /**
     * Configure a staging environment variable
     *
     * @param key   the key
     * @param value the value
     * @return {@code this}
     */
    public GetApplicationEnvironmentResponse withStagingEnvironmentVariable(String key, Object value) {
        this.stagingEnvironmentVariables.put(key, value);
        return this;
    }

    /**
     * Configure the staging environment variables
     *
     * @param stagingEnvironmentVariables the staging environment variables
     * @return {@code this}
     */
    @JsonProperty("staging_env_json")
    public GetApplicationEnvironmentResponse withStagingEnvironmentVariables(
            Map<String, Object> stagingEnvironmentVariables) {
        this.stagingEnvironmentVariables.putAll(stagingEnvironmentVariables);
        return this;
    }

}

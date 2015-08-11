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

package org.cloudfoundry.v3.client;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The response payload for the Info operation
 *
 * <p><b>This class is NOT threadsafe.</b>
 */
public final class GetInfoResponse {

    private volatile String apiVersion;

    private volatile String appSshEndpoint;

    private volatile String appSshHostKeyFingerprint;

    private volatile String authorizationEndpoint;

    private volatile String build;

    private volatile String description;

    private volatile String dopplerLoggingEndpoint;

    private volatile String loggingEndpoint;

    private volatile String minCliVersion;

    private volatile String minRecommendedCliVersion;

    private volatile String name;

    private volatile String support;

    private volatile String tokenEndpoint;

    private volatile Integer version;

    /**
     * Returns the API version
     *
     * @return the API version
     */
    public String getApiVersion() {
        return this.apiVersion;
    }

    /**
     * Configure the API version
     *
     * @param apiVersion the API version
     * @return {@code this}
     */
    @JsonProperty("api_version")
    public GetInfoResponse withApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    /**
     * Returns the app SSH endpoint
     *
     * @return the app SSH endpoint
     */
    public String getAppSshEndpoint() {
        return this.appSshEndpoint;
    }

    /**
     * Configure the app SSH endpoint
     *
     * @param appSshEndpoint the app SSH endpoint
     * @return {@code this}
     */
    @JsonProperty("app_ssh_endpoint")
    public GetInfoResponse withAppSshEndpoint(String appSshEndpoint) {
        this.appSshEndpoint = appSshEndpoint;
        return this;
    }

    /**
     * Returns the app SSH host key fingerprint
     *
     * @return the app SSH host key fingerprint
     */
    public String getAppSshHostKeyFingerprint() {
        return this.appSshHostKeyFingerprint;
    }

    /**
     * Configure the app SSH host key fingerprint
     *
     * @param appSshHostKeyFingerprint the app SSH host key fingerprint
     * @return {@code this}
     */
    @JsonProperty("app_ssh_host_key_fingerprint")
    public GetInfoResponse withAppSshHostKeyFingerprint(String appSshHostKeyFingerprint) {
        this.appSshHostKeyFingerprint = appSshHostKeyFingerprint;
        return this;
    }

    /**
     * Returns the authorization endpoint
     *
     * @return the authorization endpoint
     */
    public String getAuthorizationEndpoint() {
        return this.authorizationEndpoint;
    }

    /**
     * Configure the authorization endpoint
     *
     * @param authorizationEndpoint the authorization endpoint
     * @return {@code this}
     */
    @JsonProperty("authorization_endpoint")
    public GetInfoResponse withAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
        return this;
    }

    /**
     * Returns the build
     *
     * @return the build
     */
    public String getBuild() {
        return this.build;
    }

    /**
     * Configure the build
     *
     * @param build the build
     * @return {@code this}
     */
    @JsonProperty("build")
    public GetInfoResponse withBuild(String build) {
        this.build = build;
        return this;
    }

    /**
     * Returns the description
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Configure the description
     *
     * @param description the description
     * @return {@code this}
     */
    @JsonProperty("description")
    public GetInfoResponse withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Returns the Doppler logging endpoint
     *
     * @return the Doppler logging endpoint
     */
    public String getDopplerLoggingEndpoint() {
        return this.dopplerLoggingEndpoint;
    }

    /**
     * Configure the Doppler logging endpoint
     *
     * @param dopplerLoggingEndpoint the Doppler logging endpoint
     * @return {@code this}
     */
    @JsonProperty("doppler_logging_endpoint")
    public GetInfoResponse withDopplerLoggingEndpoint(String dopplerLoggingEndpoint) {
        this.dopplerLoggingEndpoint = dopplerLoggingEndpoint;
        return this;
    }

    /**
     * Returns the logging endpoint
     *
     * @return the logging endpoint
     */
    public String getLoggingEndpoint() {
        return this.loggingEndpoint;
    }

    /**
     * Configure the logging endpoint
     *
     * @param loggingEndpoint the logging endpoint
     * @return {@code this}
     */
    @JsonProperty("logging_endpoint")
    public GetInfoResponse withLoggingEndpoint(String loggingEndpoint) {
        this.loggingEndpoint = loggingEndpoint;
        return this;
    }

    /**
     * Returns the minimum CLI version
     *
     * @return the minimum CLI version
     */
    public String getMinCliVersion() {
        return this.minCliVersion;
    }

    /**
     * Configure the minimum CLI version
     *
     * @param minCliVersion the minimum CLI version
     * @return {@code this}
     */
    @JsonProperty("min_cli_version")
    public GetInfoResponse withMinCliVersion(String minCliVersion) {
        this.minCliVersion = minCliVersion;
        return this;
    }

    /**
     * Returns the minimum recommended CLI version
     *
     * @return the minimum recommended CLI version
     */
    public String getMinRecommendedCliVersion() {
        return this.minRecommendedCliVersion;
    }

    /**
     * Configure the minimum recommended CLI version
     *
     * @param minRecommendedCliVersion the minimum recommended CLI version
     * @return {@code this}
     */
    @JsonProperty("min_recommended_cli_version")
    public GetInfoResponse withMinRecommendedCliVersion(String minRecommendedCliVersion) {
        this.minRecommendedCliVersion = minRecommendedCliVersion;
        return this;
    }

    /**
     * Returns the name
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Configure the name
     *
     * @param name the name
     * @return {@code this}
     */
    @JsonProperty("name")
    public GetInfoResponse withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Returns the support
     *
     * @return the support
     */
    public String getSupport() {
        return this.support;
    }

    /**
     * Configure the support
     *
     * @param support the support
     * @return {@code this}
     */
    @JsonProperty("support")
    public GetInfoResponse withSupport(String support) {
        this.support = support;
        return this;
    }

    /**
     * Returns the token endpoint
     *
     * @return the token endpoint
     */
    public String getTokenEndpoint() {
        return this.tokenEndpoint;
    }

    /**
     * Configure the token endpoint
     *
     * @param tokenEndpoint the token endpoint
     * @return {@code this}
     */
    @JsonProperty("token_endpoint")
    public GetInfoResponse withTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
        return this;
    }

    /**
     * Returns the version
     *
     * @return the version
     */
    public Integer getVersion() {
        return this.version;
    }

    /**
     * Configure the version
     *
     * @param version the version
     * @return {@code this}
     */
    @JsonProperty("version")
    public GetInfoResponse withVersion(Integer version) {
        this.version = version;
        return this;
    }

}

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

package org.cloudfoundry.client.v2.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * The response payload for the Info operation
 */
@Data
public final class GetInfoResponse {

    /**
     * The API version
     *
     * @param apiVersion the API version
     * @return the API version
     */
    private final String apiVersion;

    /**
     * The application SSH endpoint
     *
     * @param appSshEndpoint the application SSH endpoint
     * @return the application SSH endpoint
     */
    private final String appSshEndpoint;

    /**
     * The application SSH host key fingerprint
     *
     * @param appSshHostKeyFingerprint the application SSH host key fingerprint
     * @return the application SSH host key fingerprint
     */
    private final String appSshHostKeyFingerprint;

    /**
     * The authorization endpoint
     *
     * @param authorizationEndpoint the authorization endpoint
     * @return the authorization endpoint
     */
    private final String authorizationEndpoint;

    /**
     * The build number
     *
     * @param buildNumber the build number
     * @return the build number
     */
    private final String buildNumber;

    /**
     * The description
     *
     * @param description the description
     * @return the description
     */
    private final String description;

    /**
     * The doppler logging endpoint
     *
     * @param dopplerLoggingEndpoint the doppler logging endpoint
     * @return the doppler logging endpoint
     */
    private final String dopplerLoggingEndpoint;

    /**
     * The logging endpoint
     *
     * @param loggingEndpoint the logging endpoint
     * @return the logging endpoint
     */
    private final String loggingEndpoint;

    /**
     * The minimum CLI version
     *
     * @param minCliVersion the minimum CLI version
     * @return the minimum CLI version
     */
    private final String minCliVersion;

    /**
     * The minimum recommended CLI version
     *
     * @param minRecommendedCliVersion the minimum recommended CLI version
     * @return the minimum recommended CLI version
     */
    private final String minRecommendedCliVersion;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The support url
     *
     * @param support the support url
     * @return the support url
     */
    private final String support;

    /**
     * The token endpoint
     *
     * @param tokenEndpoint the token endpoint
     * @return the token endpoint
     */
    private final String tokenEndpoint;

    /**
     * The user
     *
     * @param user the user
     * @return the user
     */
    private final String user;

    /**
     * The version
     *
     * @param version the
     * @return the version
     */
    private final Integer version;

    @Builder
    GetInfoResponse(@JsonProperty("api_version") String apiVersion,
                    @JsonProperty("app_ssh_endpoint") String appSshEndpoint,
                    @JsonProperty("app_ssh_host_key_fingerprint") String appSshHostKeyFingerprint,
                    @JsonProperty("authorization_endpoint") String authorizationEndpoint,
                    @JsonProperty("build") String buildNumber,
                    @JsonProperty("description") String description,
                    @JsonProperty("doppler_logging_endpoint") String dopplerLoggingEndpoint,
                    @JsonProperty("logging_endpoint") String loggingEndpoint,
                    @JsonProperty("min_cli_version") String minCliVersion,
                    @JsonProperty("min_recommended_cli_version") String minRecommendedCliVersion,
                    @JsonProperty("name") String name,
                    @JsonProperty("support") String support,
                    @JsonProperty("token_endpoint") String tokenEndpoint,
                    @JsonProperty("user") String user,
                    @JsonProperty("version") Integer version) {
        this.apiVersion = apiVersion;
        this.appSshEndpoint = appSshEndpoint;
        this.appSshHostKeyFingerprint = appSshHostKeyFingerprint;
        this.authorizationEndpoint = authorizationEndpoint;
        this.buildNumber = buildNumber;
        this.description = description;
        this.dopplerLoggingEndpoint = dopplerLoggingEndpoint;
        this.loggingEndpoint = loggingEndpoint;
        this.minCliVersion = minCliVersion;
        this.minRecommendedCliVersion = minRecommendedCliVersion;
        this.name = name;
        this.support = support;
        this.tokenEndpoint = tokenEndpoint;
        this.user = user;
        this.version = version;
    }

}

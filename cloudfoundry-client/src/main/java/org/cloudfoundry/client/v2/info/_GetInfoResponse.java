/*
 * Copyright 2013-2020 the original author or authors.
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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The response payload for the Info operation
 */
@JsonDeserialize
@Value.Immutable
abstract class _GetInfoResponse {

    /**
     * The API version
     */
    @JsonProperty("api_version")
    @Nullable
    abstract String getApiVersion();

    /**
     * The application SSH endpoint
     */
    @JsonProperty("app_ssh_endpoint")
    @Nullable
    abstract String getApplicationSshEndpoint();

    /**
     * The application SSH host key fingerprint
     */
    @JsonProperty("app_ssh_host_key_fingerprint")
    @Nullable
    abstract String getApplicationSshHostKeyFingerprint();

    /**
     * The application SSH OAuth client
     */
    @JsonProperty("app_ssh_oauth_client")
    @Nullable
    abstract String getApplicationSshOAuthClient();

    /**
     * The authorization endpoint
     */
    @JsonProperty("authorization_endpoint")
    @Nullable
    abstract String getAuthorizationEndpoint();

    /**
     * The build number
     */
    @JsonProperty("build")
    @Nullable
    abstract String getBuildNumber();

    /**
     * The description
     */
    @JsonProperty("description")
    @Nullable
    abstract String getDescription();

    /**
     * The doppler logging endpoint
     */
    @JsonProperty("doppler_logging_endpoint")
    @Nullable
    abstract String getDopplerLoggingEndpoint();

    /**
     * The logging endpoint
     */
    @JsonProperty("logging_endpoint")
    @Nullable
    abstract String getLoggingEndpoint();

    /**
     * The minimum CLI version
     */
    @JsonProperty("min_cli_version")
    @Nullable
    abstract String getMinCliVersion();

    /**
     * The minimum recommended CLI version
     */
    @JsonProperty("min_recommended_cli_version")
    @Nullable
    abstract String getMinRecommendedCliVersion();

    /**
     * The name
     */
    @JsonProperty("name")
    @Nullable
    abstract String getName();

    /**
     * The version of the supported Open Service Broker API
     */
    @JsonProperty("osbapi_version")
    @Nullable
    abstract String getOsbapiVersion();

    /**
     * The routing endpoint
     */
    @JsonProperty("routing_endpoint")
    @Nullable
    abstract String getRoutingEndpoint();

    /**
     * The support url
     */
    @JsonProperty("support")
    @Nullable
    abstract String getSupport();

    /**
     * The token endpoint
     */
    @JsonProperty("token_endpoint")
    @Nullable
    abstract String getTokenEndpoint();

    /**
     * The user
     */
    @JsonProperty("user")
    @Nullable
    abstract String getUser();

    /**
     * The version
     */
    @JsonProperty("version")
    @Nullable
    abstract Integer getVersion();

}

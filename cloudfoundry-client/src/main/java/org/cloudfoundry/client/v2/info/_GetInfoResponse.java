/*
 * Copyright 2013-2021 the original author or authors.
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

import java.util.Arrays;
import java.util.LinkedList;

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The response payload for the Info operation
 */
@JsonDeserialize
@Value.Immutable
abstract class _GetInfoResponse{

    /**
     * The API version
     * @deprecated: use connectionContext.getRootProvider().getRootKey(new LinkedList<String>(Arrays.asList("links","cloud_controller_v3","meta","version")), connectionContext)
     * or connectionContext.getRootProvider().getRootKey(new LinkedList<String>(Arrays.asList("links","cloud_controller_v2","meta","version")), connectionContext)
     */
    @JsonProperty("api_version")
    @Nullable
    abstract String getApiVersion();

    /**
     * The application SSH endpoint
     * @deprecated: use connectionContext.getRootProvider().getRoot("app_ssh", connectionContext)
     */
    @JsonProperty("app_ssh_endpoint")
    @Nullable
    abstract String getApplicationSshEndpoint();

    /**
     * The application SSH host key fingerprint
     * @deprecated: use connectionContext.getRootProvider().getRootKey("app_ssh.meta.host_key_fingerprint", connectionContext)
     */
    @JsonProperty("app_ssh_host_key_fingerprint")
    @Nullable
    abstract String getApplicationSshHostKeyFingerprint();

    /**
     * The application SSH OAuth client
     * @deprecated: use connectionContext.getRootProvider().getRootKey("app_ssh.meta.oauth_client", connectionContext)
     */
    @JsonProperty("app_ssh_oauth_client")
    @Nullable
    abstract String getApplicationSshOAuthClient();

    /**
     * The authorization endpoint
     * @deprecated: use connectionContext.getRootProvider().getRoot("login", connectionContext)
     */
    @JsonProperty("authorization_endpoint")
    @Nullable
    abstract String getAuthorizationEndpoint();

    /**
     * The build number
     * @deprecated: use corresponding method in V3 api.
     */
    @JsonProperty("build")
    @Nullable
    abstract String getBuildNumber();

    /**
     * The description
     * @deprecated: use corresponding method in V3 api.
     */
    @JsonProperty("description")
    @Nullable
    abstract String getDescription();

    /**
     * The doppler logging endpoint
     * @deprecated: use connectionContext.getRootProvider().getRoot("logging", connectionContext)
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
     * @deprecated: use corresponding method in V3 api.
     */
    @JsonProperty("min_cli_version")
    @Nullable
    abstract String getMinCliVersion();

    /**
     * The minimum recommended CLI version
     * @deprecated: use corresponding method in V3 api.
     */
    @JsonProperty("min_recommended_cli_version")
    @Nullable
    abstract String getMinRecommendedCliVersion();

    /**
     * The name
     * @deprecated: use corresponding method in V3 api.
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
     * @deprecated: use connectionContext.getRootProvider().getRoot("routing", connectionContext)
     */
    @JsonProperty("routing_endpoint")
    @Nullable
    abstract String getRoutingEndpoint();

    /**
     * The support url
     * @deprecated: use corresponding method in V3 api.
     */
    @JsonProperty("support")
    @Nullable
    abstract String getSupport();

    /**
     * The token endpoint
     * @deprecated: use connectionContext.getRootProvider().getRoot("uaa", connectionContext)
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
     * @deprecated: use corresponding method in V3 api.
     */
    @JsonProperty("version")
    @Nullable
    abstract Integer getVersion();

}

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

package org.cloudfoundry.uaa.clients;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.uaa.tokens.GrantType;
import org.immutables.value.Value;

/**
 * Client in Create request
 */
abstract class AbstractCreateClient {

    @Value.Check
    void checkAuthorizedGrantTypes() {
        if (this.getAuthorizedGrantTypes() == null) {
            throw new IllegalStateException(
                    "Cannot build CreateClientRequest, required attribute authorizedGrantTypes is"
                            + " not set");
        }
    }

    /**
     * The access token validity
     */
    @JsonProperty("access_token_validity")
    @Nullable
    abstract Long getAccessTokenValidity();

    /**
     * A list of origin keys (alias) for identity providers the client is limited to. Null implies any identity provider is allowed.
     */
    @JsonProperty("allowedproviders")
    @Nullable
    abstract List<String> getAllowedProviders();

    /**
     * Were the approvals deleted for the client, and an audit event sent
     */
    @JsonProperty("approvals_deleted")
    @Nullable
    abstract Boolean getApprovalsDeleted();

    /**
     * Scopes that the client is able to grant when creating a client
     */
    @JsonProperty("authorities")
    @Nullable
    abstract List<String> getAuthorities();

    /**
     * List of grant types that can be used to obtain a token with this client. Can include authorization_code, password, implicit, and/or client_credentials.
     */
    @JsonProperty("authorized_grant_types")
    @Nullable
    abstract List<GrantType> getAuthorizedGrantTypes();

    /**
     * Scopes that do not require user approval
     */
    @JsonProperty("autoapprove")
    @Nullable
    abstract List<String> getAutoApproves();

    /**
     * Client identifier, unique within identity zone
     */
    @JsonProperty("client_id")
    abstract String getClientId();

    /**
     * A secret string used for authenticating as this client
     */
    @JsonProperty("client_secret")
    @Nullable
    abstract String getClientSecret();

    /**
     * What scope the bearer token had when client was created
     */
    @JsonProperty("createdwith")
    @Nullable
    abstract String getCreatedWith();

    /**
     * A human readable name for the client
     */
    @JsonProperty("name")
    @Nullable
    abstract String getName();

    /**
     * Allowed URI pattern for redirect during authorization
     */
    @JsonProperty("redirect_uri")
    @Nullable
    abstract List<String> getRedirectUriPatterns();

    /**
     * The refresh token validity
     */
    @JsonProperty("refresh_token_validity")
    @Nullable
    abstract Long getRefreshTokenValidity();

    /**
     * Resources the client is allowed access to
     */
    @JsonProperty("resource_ids")
    @Nullable
    abstract List<String> getResourceIds();

    /**
     * Scopes allowed for the client
     */
    @JsonProperty("scope")
    @Nullable
    abstract List<String> getScopes();

    /**
     * A random string used to generate the client’s revokation key. Change this value to revoke all active tokens for the client
     */
    @JsonProperty("token_salt")
    @Nullable
    abstract String getTokenSalt();
}

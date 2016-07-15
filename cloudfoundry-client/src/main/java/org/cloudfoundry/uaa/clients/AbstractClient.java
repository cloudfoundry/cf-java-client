/*
 * Copyright 2013-2016 the original author or authors.
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
import org.cloudfoundry.Nullable;

import java.util.List;

/**
 * The entity response payload for Client
 */
abstract class AbstractClient {

    /**
     * A list of origin keys (alias) for identity providers the client is limited to. Null implies any identity provider is allowed.
     */
    @JsonProperty("allowedproviders")
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
    abstract List<String> getAuthorities();

    /**
     * List of grant types that can be used to obtain a token with this client. Can include authorization_code, password, implicit, and/or client_credentials.
     */
    @JsonProperty("authorized_grant_types")
    abstract List<String> getAuthorizedGrantTypes();

    /**
     * Scopes that do not require user approval
     */
    @JsonProperty("autoapprove")
    abstract List<String> getAutoApproves();

    /**
     * Client identifier, unique within identity zone
     */
    @JsonProperty("client_id")
    abstract String getClientId();

    /**
     * What scope the bearer token had when client was created
     */
    @JsonProperty("createdwith")
    @Nullable
    abstract String getCreatedWith();

    /**
     * Epoch of the moment the client information was last altered
     */
    @JsonProperty("lastModified")
    abstract Long getLastModified();

    /**
     * A human readable name for the client
     */
    @JsonProperty("name")
    abstract String getName();

    /**
     * Allowed URI pattern for redirect during authorization
     */
    @JsonProperty("redirect_uri")
    abstract List<String> getRedirectUriPatterns();

    /**
     * Resources the client is allowed access to
     */
    @JsonProperty("resource_ids")
    abstract List<String> getResourceIds();

    /**
     * Scopes allowed for the client
     */
    @JsonProperty("scope")
    abstract List<String> getScopes();

    /**
     * A random string used to generate the client’s revokation key. Change this value to revoke all active tokens for the client
     */
    @JsonProperty("token_salt")
    abstract String getTokenSalt();

}

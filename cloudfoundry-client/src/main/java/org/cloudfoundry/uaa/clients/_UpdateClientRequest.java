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
import org.cloudfoundry.uaa.IdentityZoned;
import org.immutables.value.Value;

import java.util.List;

/**
 * The request payload for Update Client
 */
@Value.Immutable
abstract class _UpdateClientRequest implements IdentityZoned {

    /**
     * A list of origin keys (alias) for identity providers the client is limited to. Null implies any identity provider is allowed.
     */
    @Nullable
    @JsonProperty("allowedproviders")
    abstract List<String> getAllowedProviders();

    /**
     * Were the approvals deleted for the client, and an audit event sent
     */
    @Nullable
    @JsonProperty("approvals_deleted")
    abstract Boolean getApprovalsDeleted();

    /**
     * Scopes that the client is able to grant when creating a client
     */
    @Nullable
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
    @Nullable
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
    @Nullable
    @JsonProperty("createdwith")
    abstract String getCreatedWith();

    /**
     * A human readable name for the client
     */
    @Nullable
    @JsonProperty("name")
    abstract String getName();

    /**
     * Allowed URI pattern for redirect during authorization
     */
    @Nullable
    @JsonProperty("redirect_uri")
    abstract List<String> getRedirectUriPatterns();

    /**
     * Resources the client is allowed access to
     */
    @Nullable
    @JsonProperty("resource_ids")
    abstract List<String> getResourceIds();

    /**
     * Scopes allowed for the client
     */
    @Nullable
    @JsonProperty("scope")
    abstract List<String> getScopes();

    /**
     * A random string used to generate the client’s revokation key. Change this value to revoke all active tokens for the client
     */
    @Nullable
    @JsonProperty("token_salt")
    abstract String getTokenSalt();

}

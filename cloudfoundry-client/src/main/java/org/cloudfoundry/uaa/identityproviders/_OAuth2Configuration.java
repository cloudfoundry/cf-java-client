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

package org.cloudfoundry.uaa.identityproviders;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The payload for the OAuth2 identity provider configuration
 */
@JsonDeserialize
@Value.Immutable
abstract class _OAuth2Configuration extends AbstractAuthenticationConfiguration {

    /**
     * The OAuth check token endpoint URL. Reserved for future OAuth use.
     */
    @JsonProperty("checkTokenUrl")
    @Nullable
    abstract String getCheckTokenUrl();

    /**
     * Send the client credentials in the token retrieval call as body parameters instead of a Basic Authorization header.
     */
    @JsonProperty("clientAuthInBody")
    @Nullable
    abstract Boolean getClientAuthInBody();

    /**
     * The OAuth 2.0 token issuer.
     */
    @JsonProperty("issuer")
    @Nullable
    abstract String getIssuer();

    /**
     * The OAuth 2.0 response type.
     */
    @JsonProperty("responseType")
    @Nullable
    abstract String getResponseType();

    /**
     * A URL for fetching user info attributes when queried with the obtained token authorization.
     */
    @JsonProperty("userInfoUrl")
    @Nullable
    abstract String getUserInfoUrl();

    /**
     * Name of the request parameter that is used to pass a known username when redirecting to this identity provider from the account chooser
     */
    @JsonProperty("userPropagationParameter")
    @Nullable
    abstract String getUserPropagationParameter();

    /**
     * A flag controlling whether PKCE (RFC 7636) is active in authorization code flow when requesting tokens from the external provider.
     */
    @JsonProperty("cacheJwks")
    @Nullable
    abstract Boolean getCacheJwks();

    /**
     * Option to enable caching for the JWKS (verification key for validating token signatures)
     */
    @JsonProperty("pkce")
    @Nullable
    abstract Boolean getPkce();

    /**
     * OAuth 2.0 logout endpoint.
     */
    @JsonProperty("logoutUrl")
    @Nullable
    abstract String getLogoutUrl();

    /**
     * A flag controlling whether to log out of the external provider after a successful UAA logout
     */
    @JsonProperty("performRpInitiatedLogout")
    @Nullable
    abstract Boolean getPerformRpInitiatedLogout();
}

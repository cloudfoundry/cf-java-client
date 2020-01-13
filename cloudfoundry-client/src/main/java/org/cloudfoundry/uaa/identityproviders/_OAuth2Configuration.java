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

}

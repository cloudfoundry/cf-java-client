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

package org.cloudfoundry.uaa.tokens;

import com.fasterxml.jackson.annotation.JsonProperty;

abstract class AbstractToken {

    /**
     * The access token
     */
    @JsonProperty("access_token")
    abstract String getAccessToken();

    /**
     * The number of seconds until token expiry
     */
    @JsonProperty("expires_in")
    abstract Integer getExpiresInSeconds();

    /**
     * The space-delimited list of scopes authorized by the user for this client
     */
    @JsonProperty("scope")
    abstract String getScopes();

    /**
     * The identifier for this token
     */
    @JsonProperty("jti")
    abstract String getTokenId();

    /**
     * The type of the access token issued
     */
    @JsonProperty("token_type")
    abstract String getTokenType();

}

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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;

/**
 * The response from the token key request
 */
@JsonDeserialize
@Value.Immutable
abstract class AbstractCheckTokenResponse {

    /**
     * The audience claim
     */
    @JsonProperty("aud")
    abstract List<String> getAudiences();

    /**
     * The auth time
     */
    @JsonProperty("auth_time")
    abstract Long getAuthorizationTime();

    /**
     * The authorities
     */
    @JsonProperty("authorities")
    abstract List<String> getAuthorities();

    /**
     * The authorized party
     */
    @JsonProperty("azp")
    abstract String getAuthorizedParty();

    /**
     * The cid
     */
    @JsonProperty("cid")
    abstract String getCid();

    /**
     * The client id
     */
    @JsonProperty("client_id")
    abstract String getClientId();

    /**
     * The email
     */
    @JsonProperty("email")
    abstract String getEmail();

    /**
     * The expiration time claim
     */
    @JsonProperty("exp")
    abstract Long getExpirationTime();

    /**
     * The grant type
     */
    @JsonProperty("grant_type")
    abstract String getGrantType();

    /**
     * The issued at claim
     */
    @JsonProperty("iat")
    abstract Long getIssuedAt();

    /**
     * The issuer claim
     */
    @JsonProperty("iss")
    abstract String getIssuer();

    /**
     * The jwt id claim
     */
    @JsonProperty("jti")
    abstract String getJwtId();

    /**
     * The origin
     */
    @JsonProperty("origin")
    abstract String getOrigin();

    /**
     * Whether token is revocable
     */
    @JsonProperty("revocable")
    abstract Boolean getRevocable();

    /**
     * The revocation signature
     */
    @JsonProperty("rev_sig")
    abstract String getRevocationSignature();

    /**
     * The scopes authorized by the user for this client
     */
    @JsonProperty("scope")
    abstract List<String> getScopes();

    /**
     * The subject claim
     */
    @JsonProperty("sub")
    abstract String getSubject();

    /**
     * The user id
     */
    @JsonProperty("user_id")
    abstract String getUserId();

    /**
     * The user name
     */
    @JsonProperty("user_name")
    abstract String getUserName();

    /**
     * The zone id
     */
    @JsonProperty("zid")
    abstract String getZoneId();

}

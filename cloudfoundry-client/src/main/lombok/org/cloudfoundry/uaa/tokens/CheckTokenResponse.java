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
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The response from the token key request
 */
@Data
public final class CheckTokenResponse {

    /**
     * The audience claim
     *
     * @param audiences the audience claim
     * @return the audience claim
     */
    private final List<String> audiences;

    /**
     * The auth time
     *
     * @param authTime the auth time
     * @return the auth time
     */
    private final Long authTime;

    /**
     * The authorities
     *
     * @param authorities the authorities
     * @return the authorities
     */
    private final List<String> authorities;

    /**
     * The authorized party
     *
     * @param authorizedParty the authorized party
     * @return the authorized party
     */
    private final String authorizedParty;

    /**
     * The cid
     *
     * @param cid the cid
     * @return the cid
     */
    private final String cid;

    /**
     * The client id
     *
     * @param clientId the client id
     * @return the client id
     */
    private final String clientId;

    /**
     * The email
     *
     * @param email the email
     * @return the email
     */
    private final String email;

    /**
     * The expiration time claim
     *
     * @param expirationTime the expiration time claim
     * @return the expiration time claim
     */
    private final Long expirationTime;

    /**
     * The grant type
     *
     * @param grantType the grant type
     * @return the grant type
     */
    private final String grantType;

    /**
     * The issued at claim
     *
     * @param issuedAt the issued at claim
     * @return the issued at claim
     */
    private final Long issuedAt;

    /**
     * The issuer claim
     *
     * @param issuer the issuer claim
     * @return the issuer claim
     */
    private final String issuer;

    /**
     * The jwt id claim
     *
     * @param jwtId the jwt id claim
     * @return the jwt id claim
     */
    private final String jwtId;

    /**
     * The origin
     *
     * @param origin the origin
     * @return the origin
     */
    private final String origin;

    /**
     * Whether token is revocable
     *
     * @param revocable whether token is revocable
     * @return whether token is revocable
     */
    private final Boolean revocable;

    /**
     * The revocation signature
     *
     * @param revocationSignature the revocation signature
     * @return the revocation signature
     */
    private final String revocationSignature;

    /**
     * The scopes authorized by the user for this client
     *
     * @param scopes the scopes authorized by the user for this client
     * @return the scopes authorized by the user for this client
     */
    private final List<String> scopes;

    /**
     * The subject claim
     *
     * @param subject the subject claim
     * @return the subject claim
     */
    private final String subject;

    /**
     * The user id
     *
     * @param userId the user id
     * @return the user id
     */
    private final String userId;

    /**
     * The user name
     *
     * @param userName the user name
     * @return the user name
     */
    private final String userName;

    /**
     * The zone id
     *
     * @param zoneId the zone id
     * @return the zone id
     */
    private final String zoneId;

    @Builder
    CheckTokenResponse(@JsonProperty("aud") @Singular List<String> audiences,
                       @JsonProperty("auth_time") Long authTime,
                       @JsonProperty("authorities") @Singular List<String> authorities,
                       @JsonProperty("azp") String authorizedParty,
                       @JsonProperty("cid") String cid,
                       @JsonProperty("client_id") String clientId,
                       @JsonProperty("email") String email,
                       @JsonProperty("exp") Long expirationTime,
                       @JsonProperty("grant_type") String grantType,
                       @JsonProperty("iat") Long issuedAt,
                       @JsonProperty("iss") String issuer,
                       @JsonProperty("jti") String jwtId,
                       @JsonProperty("origin") String origin,
                       @JsonProperty("revocable") Boolean revocable,
                       @JsonProperty("rev_sig") String revocationSignature,
                       @JsonProperty("scope") @Singular List<String> scopes,
                       @JsonProperty("sub") String subject,
                       @JsonProperty("user_id") String userId,
                       @JsonProperty("user_name") String userName,
                       @JsonProperty("zid") String zoneId) {
        this.audiences = audiences;
        this.authTime = authTime;
        this.authorities = Optional.ofNullable(authorities).orElse(Collections.emptyList());;
        this.authorizedParty = authorizedParty;
        this.cid = cid;
        this.clientId = clientId;
        this.email = email;
        this.expirationTime = expirationTime;
        this.grantType = grantType;
        this.issuedAt = issuedAt;
        this.issuer = issuer;
        this.jwtId = jwtId;
        this.origin = origin;
        this.revocable = revocable;
        this.revocationSignature = revocationSignature;
        this.scopes = scopes;
        this.subject = subject;
        this.userId = userId;
        this.userName = userName;
        this.zoneId = zoneId;
    }

}
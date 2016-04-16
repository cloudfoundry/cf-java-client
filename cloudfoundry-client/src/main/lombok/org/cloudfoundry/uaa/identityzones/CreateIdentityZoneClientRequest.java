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

package org.cloudfoundry.uaa.identityzones;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Singular;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * The request payload for the create identity zone client operation
 */
@Data
public final class CreateIdentityZoneClientRequest implements Validatable {

    /**
     * The access token validity.
     *
     * @param accessTokenValidity the access token validity
     * @return the access token validity
     */
    @Getter(onMethod = @__(@JsonProperty("access_token_validity")))
    private final Integer accessTokenValidity;

    /**
     * The allowed providers.
     *
     * @param allowedProviders the allowed providers
     * @return the allowed providers
     */
    @Getter(onMethod = @__(@JsonProperty("allowedproviders")))
    private final List<String> allowedProviders;

    /**
     * The authorities.
     *
     * @param authorities the authorities
     * @return the authorities
     */
    @Getter(onMethod = @__(@JsonProperty("authorities")))
    private final List<String> authorities;

    /**
     * The authorized grant types.
     *
     * @param authorizedGrantTypes the authorized grant types
     * @return the authorized grant types
     */
    @Getter(onMethod = @__(@JsonProperty("authorized_grant_types")))
    private final List<String> authorizedGrantTypes;

    /**
     * The auto approves
     *
     * @param autoApproves the auto approves
     * @return the auto approves
     */
    @Getter(onMethod = @__({@JsonProperty("autoapprove"), @JsonInclude(NON_EMPTY)}))
    private final List<String> autoApproves;

    /**
     * The client id.
     *
     * @param clientId the client id
     * @return the client id
     */
    @Getter(onMethod = @__(@JsonProperty("client_id")))
    private final String clientId;

    /**
     * The client secret.
     *
     * @param clientSecret the client secret
     * @return the client secret
     */
    @Getter(onMethod = @__(@JsonProperty("client_secret")))
    private final String clientSecret;

    /**
     * The identity zone id.
     *
     * @param identityZoneId the identity zone id
     * @return the identity zone id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String identityZoneId;

    /**
     * The redirect uri.
     *
     * @param redirectUri the redirect uri
     * @return the redirect uri
     */
    @Getter(onMethod = @__(@JsonProperty("redirect_uri")))
    private final String redirectUri;

    /**
     * The refresh token validity.
     *
     * @param refreshTokenValidity the refresh token validity
     * @return the refresh token validity
     */
    @Getter(onMethod = @__(@JsonProperty("refresh_token_validity")))
    private final Integer refreshTokenValidity;

    /**
     * The scopes.
     *
     * @param scopes the scopes
     * @return the scopes
     */
    @Getter(onMethod = @__(@JsonProperty("scope")))
    private final List<String> scopes;

    @Builder
    CreateIdentityZoneClientRequest(Integer accessTokenValidity,
                                    @Singular List<String> allowedProviders,
                                    @Singular List<String> authorities,
                                    @Singular List<String> authorizedGrantTypes,
                                    @Singular List<String> autoApproves,
                                    String clientId,
                                    String clientSecret,
                                    String identityZoneId,
                                    String redirectUri,
                                    Integer refreshTokenValidity,
                                    @Singular List<String> scopes) {
        this.accessTokenValidity = accessTokenValidity;
        this.allowedProviders = allowedProviders;
        this.authorities = authorities;
        this.authorizedGrantTypes = authorizedGrantTypes;
        this.autoApproves = autoApproves;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.identityZoneId = identityZoneId;
        this.redirectUri = redirectUri;
        this.refreshTokenValidity = refreshTokenValidity;
        this.scopes = scopes;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.identityZoneId == null) {
            builder.message("identity zone id must be specified");
        }

        if (this.clientId == null) {
            builder.message("client id must be specified");
        }

        if (this.clientSecret == null) {
            builder.message("client secret must be specified");
        }

        if (this.authorizedGrantTypes == null || this.authorizedGrantTypes.isEmpty()) {
            builder.message("authorized grant types must be specified");
        }

        if (this.scopes == null || this.scopes.isEmpty()) {
            builder.message("scopes must be specified");
        }

        if (this.authorities == null || this.authorities.isEmpty()) {
            builder.message("authorities must be specified");
        }

        if (this.allowedProviders == null || this.allowedProviders.isEmpty()) {
            builder.message("allowed providers must be specified");
        }

        return builder.build();
    }

}


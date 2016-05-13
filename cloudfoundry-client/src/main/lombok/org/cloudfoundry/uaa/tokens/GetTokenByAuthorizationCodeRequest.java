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

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.QueryParameter;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

/**
 * The request payload for the get token by authorization code operation
 */
@Data
public final class GetTokenByAuthorizationCodeRequest implements Validatable {

    /**
     * The authorization code
     *
     * @param authorizationCode the authorization code
     * @return the authorization code
     */
    @Getter(onMethod = @__(@QueryParameter("code")))
    private final String authorizationCode;

    /**
     * The client identifier
     *
     * @param clientId the client identifier
     * @return the client identifier
     */
    @Getter(onMethod = @__(@QueryParameter("client_id")))
    private final String clientId;

    /**
     * The client's secret passphrase
     *
     * @param clientSecret the client secret
     * @return the client secret
     */
    @Getter(onMethod = @__(@QueryParameter("client_secret")))
    private final String clientSecret;

    /**
     * The redirection URI
     *
     * @param redirectUri the redirection URI
     * @return the redirection URI
     */
    @Getter(onMethod = @__(@QueryParameter("redirect_uri")))
    private final String redirectUri;

    /**
     * The token format
     *
     * @param tokenFormat the token format
     * @return the token format
     */
    @Getter(onMethod = @__(@QueryParameter("token_format")))
    private final TokenFormat tokenFormat;

    @Builder
    GetTokenByAuthorizationCodeRequest(String authorizationCode, String clientId, String clientSecret, String redirectUri, TokenFormat tokenFormat) {
        this.authorizationCode = authorizationCode;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.tokenFormat = tokenFormat;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.clientId == null) {
            builder.message("client id must be specified");
        }
        if (this.clientSecret == null) {
            builder.message("client secret must be specified");
        }
        if (this.authorizationCode == null) {
            builder.message("authorization code must be specified");
        }

        return builder.build();
    }

}

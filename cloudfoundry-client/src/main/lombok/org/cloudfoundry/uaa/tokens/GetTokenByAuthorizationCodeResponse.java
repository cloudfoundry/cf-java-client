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
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * The response from the get token by authorization code request
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class GetTokenByAuthorizationCodeResponse extends AbstractToken {

    private final String refreshToken;

    @Builder
    GetTokenByAuthorizationCodeResponse(@JsonProperty("access_token") String accessToken,
                                        @JsonProperty("expires_in") Integer expiresInSeconds,
                                        @JsonProperty("refresh_token") String refreshToken,
                                        @JsonProperty("scope") String scopes,
                                        @JsonProperty("jti") String tokenId,
                                        @JsonProperty("token_type") String tokenType) {

        super(accessToken, expiresInSeconds, convertScopes(scopes), tokenId, tokenType);

        this.refreshToken = refreshToken;
    }

    private static List<String> convertScopes(String scopes) {
        return Optional.ofNullable(scopes)
            .map(ss -> ss.split(" "))
            .map(Arrays::asList)
            .orElse(null);
    }
}

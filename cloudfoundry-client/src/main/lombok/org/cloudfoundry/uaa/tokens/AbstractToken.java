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

import lombok.Data;

import java.util.List;

@Data
public abstract class AbstractToken {

    /**
     * The access token
     *
     * @param accessToken the access token
     * @return the access token
     */
    private final String accessToken;

    /**
     * The number of seconds until token expiry
     *
     * @param expiresInSeconds the number of seconds until token expiry
     * @return the number of seconds until token expiry
     */
    private final Integer expiresInSeconds;

    /**
     * The space-delimited list of scopes authorized by the user for this client
     *
     * @param scopes the space-delimited list of scopes authorized by the user for this client
     * @return the space-delimited list of scopes authorized by the user for this client
     */
    private final List<String> scopes;

    /**
     * The identifier for this token
     *
     * @param tokenId the identifier for this token
     * @return the identifier for this token
     */
    private final String tokenId;

    /**
     * The type of the access token issued
     *
     * @param tokenType the type of the access token issued
     * @return the type of the access token issued
     */
    private final String tokenType;

    AbstractToken(String accessToken,
                  Integer expiresInSeconds,
                  List<String> scopes,
                  String tokenId,
                  String tokenType) {

        this.accessToken = accessToken;
        this.expiresInSeconds = expiresInSeconds;
        this.scopes = scopes;
        this.tokenId = tokenId;
        this.tokenType = tokenType;
    }

}

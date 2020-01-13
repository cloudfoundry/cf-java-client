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

package org.cloudfoundry.uaa.tokens;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the UAA Access Token Administration Client API
 */
public interface Tokens {

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#check-token">Check Token</a> request
     *
     * @param request the Check Token request
     * @return the Check Token response
     */
    Mono<CheckTokenResponse> check(CheckTokenRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#authorization-code-grant13">Authorization Code Grant</a> request
     *
     * @param request the Authorization Code request
     * @return the response from the Authorization Code request
     */
    Mono<GetTokenByAuthorizationCodeResponse> getByAuthorizationCode(GetTokenByAuthorizationCodeRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#client-credentials-grant">Client Credentials Grant</a> request
     *
     * @param request the Client Credentials request
     * @return the response from the Client Credentials request
     */
    Mono<GetTokenByClientCredentialsResponse> getByClientCredentials(GetTokenByClientCredentialsRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#one-time-passcode">One-time Passcode</a> request
     *
     * @param request the One Time Passcode token request
     * @return the response from the One Time Passcode token request
     */
    Mono<GetTokenByOneTimePasscodeResponse> getByOneTimePasscode(GetTokenByOneTimePasscodeRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#openid-connect">OpenID Connect</a> request
     *
     * @param request the OpenId request
     * @return the response from the OpenId request
     */
    Mono<GetTokenByOpenIdResponse> getByOpenId(GetTokenByOpenIdRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#password-grant">Password Grant</a> request
     *
     * @param request the Password token request
     * @return the response from the Password token request
     */
    Mono<GetTokenByPasswordResponse> getByPassword(GetTokenByPasswordRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#token-key">Token Key</a> request
     *
     * @param request the Token Key request
     * @return the response from the Token Key request
     */
    Mono<GetTokenKeyResponse> getKey(GetTokenKeyRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#token-keys">Token Keys</a> request
     *
     * @param request the Token Keys request
     * @return the Token Keys response
     */
    Mono<ListTokenKeysResponse> listKeys(ListTokenKeysRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#refresh-token">Refresh Token</a> request
     *
     * @param request the refresh token request
     * @return the response from the refresh token request
     */
    Mono<RefreshTokenResponse> refresh(RefreshTokenRequest request);

}

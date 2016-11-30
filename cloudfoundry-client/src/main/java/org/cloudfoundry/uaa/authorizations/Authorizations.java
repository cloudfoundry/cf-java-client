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

package org.cloudfoundry.uaa.authorizations;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the UAA Authorization Client API
 */
public interface Authorizations {

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#api-flow">Authorize By Authorization Code Grant (API)</a> request
     *
     * @param request Authorize By Authorization Code Grant (API) request
     * @return the authorization code
     */
    Mono<String> authorizationCodeGrantApi(AuthorizeByAuthorizationCodeGrantApiRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#browser-flow">Authorize By Authorization Code Grant (Browser)</a> request
     *
     * @param request Authorize By Authorization Code Grant (Browser) request
     * @return the authentication redirect URI
     */
    Mono<String> authorizationCodeGrantBrowser(AuthorizeByAuthorizationCodeGrantBrowserRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#hybrid-flow">Authorize By Authorization Code Grant (Hybrid)</a> request
     *
     * @param request Authorize By Authorization Code Grant (Hybrid) request
     * @return the authentication redirect URI
     */
    Mono<String> authorizationCodeGrantHybrid(AuthorizeByAuthorizationCodeGrantHybridRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/#implicit-grant">Authorize By Implicit Grant (Browser)</a> request
     *
     * @param request Authorize By Implicit Grant (Browser) request
     * @return the authentication redirect URI
     */
    Mono<String> implicitGrantBrowser(AuthorizeByImplicitGrantBrowserRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#with-authorization-code-grant">Authorize By Open ID with Authorization Code Grant</a> request
     *
     * @param request Authorize By Open ID with an Authorization Code Grant request
     * @return the authentication location URI, including Access Token
     */
    Mono<String> openIdWithAuthorizationCodeAndIdToken(AuthorizeByOpenIdWithAuthorizationCodeGrantRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#id-token">Authorize By Open ID with an ID Token</a> request
     *
     * @param request Authorize By Open ID with an ID Token request
     * @return the authentication location URI, including Access Token
     */
    Mono<String> openIdWithIdToken(AuthorizeByOpenIdWithIdTokenRequest request);

    /**
     * Makes the <a href="http://docs.cloudfoundry.com/uaa/#with-implicit-grant">Authorize By Open ID with Implicit Grant</a> request
     *
     * @param request Authorize By Open ID with Implicit Grant request
     * @return the authentication location URI, including Access Token
     */
    Mono<String> openIdWithTokenAndIdToken(AuthorizeByOpenIdWithImplicitGrantRequest request);

}

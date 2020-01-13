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

package org.cloudfoundry.uaa.serverinformation;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the UAA Server Information Client API
 */
public interface ServerInformation {

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/version/4.10.0/index.html#get-authentication-code">Get Auto Login Authentication Code</a> request
     *
     * @param request the Get Auto Login Authentication Code request
     * @return the response from the Get Auto Login Authentication Code request
     */
    Mono<GetAutoLoginAuthenticationCodeResponse> getAuthenticationCode(GetAutoLoginAuthenticationCodeRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/version/4.10.0/index.html#server-information-2">Server Information</a> request
     *
     * @param request the Get UAA Server Information request
     * @return the response from the Get UAA Server Information request
     */
    Mono<GetInfoResponse> getInfo(GetInfoRequest request);

    /**
     * Makes the <a href="https://docs.cloudfoundry.org/api/uaa/version/4.10.0/index.html#perform-login">Perform Auto Login</a> request
     *
     * @param request the Perform Auto Login request
     * @return the response from the Perform Auto Login request
     */
    Mono<Void> autoLogin(AutoLoginRequest request);

}

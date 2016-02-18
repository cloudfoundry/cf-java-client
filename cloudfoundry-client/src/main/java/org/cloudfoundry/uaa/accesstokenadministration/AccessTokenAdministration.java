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

package org.cloudfoundry.uaa.accesstokenadministration;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the UAA Access Token Administration Client API
 */
public interface AccessTokenAdministration {

    /**
     * Makes the <a href="https://github.com/cloudfoundry/uaa/blob/master/docs/UAA-APIs.rst#get-the-token-signing-key-get-token-key">Token Key</a> request
     *
     * @param request the Token Key request
     * @return the response from the Token Key request
     */
    Mono<GetTokenKeyResponse> getTokenKey(GetTokenKeyRequest request);

}

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

package org.cloudfoundry.spring.util.network;

import org.springframework.security.oauth2.client.OAuth2RestOperations;
import reactor.core.publisher.Mono;

public final class OAuth2RestOperationsOAuth2TokenProvider implements OAuth2TokenProvider {

    private final OAuth2RestOperations restOperations;

    public OAuth2RestOperationsOAuth2TokenProvider(OAuth2RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    @Override
    public Mono<String> getToken() {
        return Mono.defer(() -> Mono.just(this.restOperations.getAccessToken().getValue()));
    }

}

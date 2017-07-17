/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.operations.advanced;

import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.authorizations.AuthorizeByAuthorizationCodeGrantApiRequest;
import reactor.core.publisher.Mono;

public final class DefaultAdvanced implements Advanced {

    private final Mono<UaaClient> uaaClient;

    public DefaultAdvanced(Mono<UaaClient> uaaClient) {
        this.uaaClient = uaaClient;
    }

    @Override
    public Mono<String> sshCode() {
        return this.uaaClient
            .flatMap(DefaultAdvanced::requestAuthorizeByAuthorizationCodeGrantApi)
            .transform(OperationsLogging.log("Get SSH Code"))
            .checkpoint();
    }

    private static Mono<String> requestAuthorizeByAuthorizationCodeGrantApi(UaaClient uaaClient) {
        return uaaClient.authorizations()
            .authorizationCodeGrantApi(AuthorizeByAuthorizationCodeGrantApiRequest.builder()
                .clientId("ssh-proxy")
                .build());
    }

}

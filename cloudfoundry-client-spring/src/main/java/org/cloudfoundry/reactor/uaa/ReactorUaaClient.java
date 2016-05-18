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

package org.cloudfoundry.reactor.uaa;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import org.cloudfoundry.reactor.uaa.identityzones.ReactorIdentityZones;
import org.cloudfoundry.reactor.uaa.tokens.ReactorTokens;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import org.cloudfoundry.reactor.util.ConnectionContextSupplier;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.identityzones.IdentityZones;
import org.cloudfoundry.uaa.tokens.Tokens;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

/**
 * The Reactor-based implementation of {@link UaaClient}
 */
public final class ReactorUaaClient implements UaaClient {

    private final IdentityZones identityZones;

    private final Tokens tokens;

    @Builder
    ReactorUaaClient(ConnectionContextSupplier cloudFoundryClient) {
        this(cloudFoundryClient.getConnectionContext().getAuthorizationProvider(), cloudFoundryClient.getConnectionContext().getClientId(), cloudFoundryClient.getConnectionContext().getClientSecret(),
            cloudFoundryClient.getConnectionContext().getHttpClient(), cloudFoundryClient.getConnectionContext().getObjectMapper(),
            cloudFoundryClient.getConnectionContext().getRoot("token_endpoint"));
    }

    ReactorUaaClient(AuthorizationProvider authorizationProvider, String clientId, String clientSecret, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        this.identityZones = new ReactorIdentityZones(authorizationProvider, httpClient, objectMapper, root);
        this.tokens = new ReactorTokens(authorizationProvider, clientId, clientSecret, httpClient, objectMapper, root);
    }

    @Override
    public IdentityZones identityZones() {
        return this.identityZones;
    }

    @Override
    public Tokens tokens() {
        return this.tokens;
    }

}

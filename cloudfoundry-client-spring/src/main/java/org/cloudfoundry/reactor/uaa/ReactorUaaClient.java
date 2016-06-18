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
import org.cloudfoundry.reactor.uaa.authorizations.ReactorAuthorizations;
import org.cloudfoundry.reactor.uaa.groups.ReactorGroups;
import org.cloudfoundry.reactor.uaa.identityproviders.ReactorIdentityProviders;
import org.cloudfoundry.reactor.uaa.identityzones.ReactorIdentityZones;
import org.cloudfoundry.reactor.uaa.tokens.ReactorTokens;
import org.cloudfoundry.reactor.uaa.users.ReactorUsers;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import org.cloudfoundry.reactor.util.ConnectionContextSupplier;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.authorizations.Authorizations;
import org.cloudfoundry.uaa.groups.Groups;
import org.cloudfoundry.uaa.identityproviders.IdentityProviders;
import org.cloudfoundry.uaa.identityzones.IdentityZones;
import org.cloudfoundry.uaa.tokens.Tokens;
import org.cloudfoundry.uaa.users.Users;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

/**
 * The Reactor-based implementation of {@link UaaClient}
 */
public final class ReactorUaaClient implements UaaClient {

    private final Authorizations authorizations;

    private final Groups groups;

    private final IdentityProviders identityProviders;

    private final IdentityZones identityZones;

    private final Tokens tokens;

    private final Users users;

    @Builder
    ReactorUaaClient(ConnectionContextSupplier cloudFoundryClient) {
        this(cloudFoundryClient.getConnectionContext().getAuthorizationProvider(), cloudFoundryClient.getConnectionContext().getHttpClient(),
            cloudFoundryClient.getConnectionContext().getObjectMapper(), cloudFoundryClient.getConnectionContext().getRoot("token_endpoint"));
    }

    ReactorUaaClient(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        this.authorizations = new ReactorAuthorizations(authorizationProvider, httpClient, objectMapper, root);
        this.groups = new ReactorGroups(authorizationProvider, httpClient, objectMapper, root);
        this.identityProviders = new ReactorIdentityProviders(authorizationProvider, httpClient, objectMapper, root);
        this.identityZones = new ReactorIdentityZones(authorizationProvider, httpClient, objectMapper, root);
        this.tokens = new ReactorTokens(authorizationProvider, httpClient, objectMapper, root);
        this.users = new ReactorUsers(authorizationProvider, httpClient, objectMapper, root);
    }

    @Override
    public Authorizations authorizations() {
        return this.authorizations;
    }

    @Override
    public Groups groups() {
        return this.groups;
    }

    @Override
    public IdentityProviders identityProviders() {
        return this.identityProviders;
    }

    @Override
    public IdentityZones identityZones() {
        return this.identityZones;
    }

    @Override
    public Tokens tokens() {
        return this.tokens;
    }

    @Override
    public Users users() {
        return this.users;
    }

}

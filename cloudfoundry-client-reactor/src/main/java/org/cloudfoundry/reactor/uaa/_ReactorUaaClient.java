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
import org.cloudfoundry.Nullable;
import org.cloudfoundry.reactor.uaa.authorizations.ReactorAuthorizations;
import org.cloudfoundry.reactor.uaa.groups.ReactorGroups;
import org.cloudfoundry.reactor.uaa.identityproviders.ReactorIdentityProviders;
import org.cloudfoundry.reactor.uaa.identityzones.ReactorIdentityZones;
import org.cloudfoundry.reactor.uaa.tokens.ReactorTokens;
import org.cloudfoundry.reactor.uaa.users.ReactorUsers;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.authorizations.Authorizations;
import org.cloudfoundry.uaa.groups.Groups;
import org.cloudfoundry.uaa.identityproviders.IdentityProviders;
import org.cloudfoundry.uaa.identityzones.IdentityZones;
import org.cloudfoundry.uaa.tokens.Tokens;
import org.cloudfoundry.uaa.users.Users;
import org.immutables.value.Value;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

/**
 * The Reactor-based implementation of {@link UaaClient}
 */
@Value.Immutable
abstract class _ReactorUaaClient implements UaaClient {

    @Override
    @Value.Derived
    public Authorizations authorizations() {
        return new ReactorAuthorizations(getConnectionContext(), getRoot(), getTokenProvider());
    }

    @Override
    @Value.Derived
    public Mono<String> getUsername() {
        return getUsernameProvider().get();
    }

    @Override
    @Value.Derived
    public Groups groups() {
        return new ReactorGroups(getConnectionContext(), getRoot(), getTokenProvider());
    }

    @Override
    @Value.Derived
    public IdentityProviders identityProviders() {
        return new ReactorIdentityProviders(getConnectionContext(), getRoot(), getTokenProvider());
    }

    @Override
    @Value.Derived
    public IdentityZones identityZones() {
        return new ReactorIdentityZones(getConnectionContext(), getRoot(), getTokenProvider());
    }

    @Override
    @Value.Derived
    public Tokens tokens() {
        return new ReactorTokens(getConnectionContext(), getRoot(), getTokenProvider());
    }

    @Override
    @Value.Derived
    public Users users() {
        return new ReactorUsers(getConnectionContext(), getRoot(), getTokenProvider());
    }

    @Nullable
    abstract ConnectionContext getConnectionContext();

    @Value.Default
    HttpClient getHttpClient() {
        return getConnectionContext().getHttpClient();
    }

    @Value.Default
    ObjectMapper getObjectMapper() {
        return getConnectionContext().getObjectMapper();
    }

    @Value.Default
    Mono<String> getRoot() {
        return getConnectionContext().getRoot("token_endpoint");
    }

    abstract TokenProvider getTokenProvider();

    @Value.Default
    UsernameProvider getUsernameProvider() {
        return new UsernameProvider(getConnectionContext(), getTokenProvider(), tokens());
    }

}

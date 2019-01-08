/*
 * Copyright 2013-2019 the original author or authors.
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

import org.cloudfoundry.Nullable;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.uaa.authorizations.ReactorAuthorizations;
import org.cloudfoundry.reactor.uaa.clients.ReactorClients;
import org.cloudfoundry.reactor.uaa.groups.ReactorGroups;
import org.cloudfoundry.reactor.uaa.identityproviders.ReactorIdentityProviders;
import org.cloudfoundry.reactor.uaa.identityzones.ReactorIdentityZones;
import org.cloudfoundry.reactor.uaa.serverinformation.ReactorServerInformation;
import org.cloudfoundry.reactor.uaa.tokens.ReactorTokens;
import org.cloudfoundry.reactor.uaa.users.ReactorUsers;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.authorizations.Authorizations;
import org.cloudfoundry.uaa.clients.Clients;
import org.cloudfoundry.uaa.groups.Groups;
import org.cloudfoundry.uaa.identityproviders.IdentityProviders;
import org.cloudfoundry.uaa.identityzones.IdentityZones;
import org.cloudfoundry.uaa.serverinformation.ServerInformation;
import org.cloudfoundry.uaa.tokens.Tokens;
import org.cloudfoundry.uaa.users.Users;
import org.immutables.value.Value;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.function.Function;

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
    public Clients clients() {
        return new ReactorClients(getConnectionContext(), getRoot(), getTokenProvider());
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
    public ServerInformation serverInformation() {
        return new ReactorServerInformation(getConnectionContext(), getRoot(), getTokenProvider());
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

    /**
     * The connection context
     */
    abstract ConnectionContext getConnectionContext();

    /**
     * The identity zone subdomain
     */
    @Nullable
    abstract String getIdentityZoneSubdomain();

    @Value.Default
    Mono<String> getRoot() {
        return getConnectionContext().getRootProvider().getRoot("uaa", getConnectionContext())
            .map(getIdentityZoneEndpoint(getIdentityZoneSubdomain()))
            .cache();
    }

    /**
     * The token provider
     */
    abstract TokenProvider getTokenProvider();

    @Value.Default
    UsernameProvider getUsernameProvider() {
        return new UsernameProvider(getConnectionContext(), getTokenProvider(), tokens());
    }

    private static Function<String, String> getIdentityZoneEndpoint(String identityZoneId) {
        return raw -> {
            if (identityZoneId == null) {
                return raw;
            }

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(raw);
            builder.host(String.format("%s.%s", identityZoneId, builder.build().getHost()));
            return builder.build().encode().toUriString();
        };
    }

}

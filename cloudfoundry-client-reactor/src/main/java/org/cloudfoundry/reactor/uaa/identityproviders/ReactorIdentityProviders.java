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

package org.cloudfoundry.reactor.uaa.identityproviders;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.uaa.AbstractUaaOperations;
import org.cloudfoundry.uaa.identityproviders.CreateIdentityProviderRequest;
import org.cloudfoundry.uaa.identityproviders.CreateIdentityProviderResponse;
import org.cloudfoundry.uaa.identityproviders.DeleteIdentityProviderRequest;
import org.cloudfoundry.uaa.identityproviders.DeleteIdentityProviderResponse;
import org.cloudfoundry.uaa.identityproviders.GetIdentityProviderRequest;
import org.cloudfoundry.uaa.identityproviders.GetIdentityProviderResponse;
import org.cloudfoundry.uaa.identityproviders.IdentityProviders;
import org.cloudfoundry.uaa.identityproviders.ListIdentityProvidersRequest;
import org.cloudfoundry.uaa.identityproviders.ListIdentityProvidersResponse;
import org.cloudfoundry.uaa.identityproviders.UpdateIdentityProviderRequest;
import org.cloudfoundry.uaa.identityproviders.UpdateIdentityProviderResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link IdentityProviders}
 */
public final class ReactorIdentityProviders extends AbstractUaaOperations implements IdentityProviders {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://uaa.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorIdentityProviders(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateIdentityProviderResponse> create(CreateIdentityProviderRequest request) {
        return post(request, CreateIdentityProviderResponse.class, builder -> builder.pathSegment("identity-providers").queryParam("rawConfig", true))
            .checkpoint();
    }

    @Override
    public Mono<DeleteIdentityProviderResponse> delete(DeleteIdentityProviderRequest request) {
        return delete(request, DeleteIdentityProviderResponse.class, builder -> builder.pathSegment("identity-providers", request.getIdentityProviderId()).queryParam("rawConfig", true))
            .checkpoint();
    }

    @Override
    public Mono<GetIdentityProviderResponse> get(GetIdentityProviderRequest request) {
        return get(request, GetIdentityProviderResponse.class, builder -> builder.pathSegment("identity-providers", request.getIdentityProviderId()).queryParam("rawConfig", true))
            .checkpoint();
    }

    @Override
    public Mono<ListIdentityProvidersResponse> list(ListIdentityProvidersRequest request) {
        return get(request, ListIdentityProvidersResponse.class, builder -> builder.pathSegment("identity-providers").queryParam("rawConfig", true))
            .checkpoint();
    }

    @Override
    public Mono<UpdateIdentityProviderResponse> update(UpdateIdentityProviderRequest request) {
        return put(request, UpdateIdentityProviderResponse.class, builder -> builder.pathSegment("identity-providers", request.getIdentityProviderId()).queryParam("rawConfig", true))
            .checkpoint();
    }

}

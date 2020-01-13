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

package org.cloudfoundry.reactor.client.v2.shareddomains;

import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainResponse;
import org.cloudfoundry.client.v2.shareddomains.DeleteSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.DeleteSharedDomainResponse;
import org.cloudfoundry.client.v2.shareddomains.GetSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.GetSharedDomainResponse;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsResponse;
import org.cloudfoundry.client.v2.shareddomains.SharedDomains;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link SharedDomains}
 */
public final class ReactorSharedDomains extends AbstractClientV2Operations implements SharedDomains {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorSharedDomains(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateSharedDomainResponse> create(CreateSharedDomainRequest request) {
        return post(request, CreateSharedDomainResponse.class, builder -> builder.pathSegment("shared_domains"))
            .checkpoint();
    }

    @Override
    public Mono<DeleteSharedDomainResponse> delete(DeleteSharedDomainRequest request) {
        return delete(request, DeleteSharedDomainResponse.class, builder -> builder.pathSegment("shared_domains", request.getSharedDomainId()))
            .checkpoint();
    }

    @Override
    public Mono<GetSharedDomainResponse> get(GetSharedDomainRequest request) {
        return get(request, GetSharedDomainResponse.class, builder -> builder.pathSegment("shared_domains", request.getSharedDomainId()))
            .checkpoint();

    }

    @Override
    public Mono<ListSharedDomainsResponse> list(ListSharedDomainsRequest request) {
        return get(request, ListSharedDomainsResponse.class, builder -> builder.pathSegment("shared_domains"))
            .checkpoint();
    }

}

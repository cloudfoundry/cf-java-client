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

package org.cloudfoundry.reactor.client.v2.privatedomains;

import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.DeletePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.DeletePrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.GetPrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.GetPrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainSharedOrganizationsRequest;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainSharedOrganizationsResponse;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsRequest;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsResponse;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomains;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link PrivateDomains}
 */
public final class ReactorPrivateDomains extends AbstractClientV2Operations implements PrivateDomains {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorPrivateDomains(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreatePrivateDomainResponse> create(CreatePrivateDomainRequest request) {
        return post(request, CreatePrivateDomainResponse.class, builder -> builder.pathSegment("private_domains"))
            .checkpoint();
    }

    @Override
    public Mono<DeletePrivateDomainResponse> delete(DeletePrivateDomainRequest request) {
        return delete(request, DeletePrivateDomainResponse.class, builder -> builder.pathSegment("private_domains", request.getPrivateDomainId()))
            .checkpoint();
    }

    @Override
    public Mono<GetPrivateDomainResponse> get(GetPrivateDomainRequest request) {
        return get(request, GetPrivateDomainResponse.class, builder -> builder.pathSegment("private_domains", request.getPrivateDomainId()))
            .checkpoint();
    }

    @Override
    public Mono<ListPrivateDomainsResponse> list(ListPrivateDomainsRequest request) {
        return get(request, ListPrivateDomainsResponse.class, builder -> builder.pathSegment("private_domains"))
            .checkpoint();
    }

    @Override
    public Mono<ListPrivateDomainSharedOrganizationsResponse> listSharedOrganizations(ListPrivateDomainSharedOrganizationsRequest request) {
        return get(request, ListPrivateDomainSharedOrganizationsResponse.class, builder -> builder.pathSegment("private_domains", request.getPrivateDomainId(), "shared_organizations"))
            .checkpoint();
    }

}

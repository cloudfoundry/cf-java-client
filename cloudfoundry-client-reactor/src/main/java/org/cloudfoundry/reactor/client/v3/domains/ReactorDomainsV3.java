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

package org.cloudfoundry.reactor.client.v3.domains;

import org.cloudfoundry.client.v3.domains.CheckReservedRoutesRequest;
import org.cloudfoundry.client.v3.domains.CheckReservedRoutesResponse;
import org.cloudfoundry.client.v3.domains.CreateDomainRequest;
import org.cloudfoundry.client.v3.domains.CreateDomainResponse;
import org.cloudfoundry.client.v3.domains.DeleteDomainRequest;
import org.cloudfoundry.client.v3.domains.DomainsV3;
import org.cloudfoundry.client.v3.domains.GetDomainRequest;
import org.cloudfoundry.client.v3.domains.GetDomainResponse;
import org.cloudfoundry.client.v3.domains.ListDomainsRequest;
import org.cloudfoundry.client.v3.domains.ListDomainsResponse;
import org.cloudfoundry.client.v3.domains.ShareDomainRequest;
import org.cloudfoundry.client.v3.domains.ShareDomainResponse;
import org.cloudfoundry.client.v3.domains.UnshareDomainRequest;
import org.cloudfoundry.client.v3.domains.UpdateDomainRequest;
import org.cloudfoundry.client.v3.domains.UpdateDomainResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link DomainsV3}
 */
public final class ReactorDomainsV3 extends AbstractClientV3Operations implements DomainsV3 {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorDomainsV3(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CheckReservedRoutesResponse> checkReservedRoutes(CheckReservedRoutesRequest request) {
        return get(request, CheckReservedRoutesResponse.class, builder -> builder.pathSegment("domains", request.getDomainId(), "route_reservations"))
            .checkpoint();
    }

    @Override
    public Mono<CreateDomainResponse> create(CreateDomainRequest request) {
        return post(request, CreateDomainResponse.class, builder -> builder.pathSegment("domains"))
            .checkpoint();
    }

    @Override
    public Mono<String> delete(DeleteDomainRequest request) {
        return delete(request, builder -> builder.pathSegment("domains", request.getDomainId()))
            .checkpoint();
    }

    @Override
    public Mono<GetDomainResponse> get(GetDomainRequest request) {
        return get(request, GetDomainResponse.class, builder -> builder.pathSegment("domains", request.getDomainId()))
            .checkpoint();
    }

    @Override
    public Mono<ListDomainsResponse> list(ListDomainsRequest request) {
        return get(request, ListDomainsResponse.class, builder -> builder.pathSegment("domains"))
            .checkpoint();
    }

    @Override
    public Mono<ShareDomainResponse> share(ShareDomainRequest request) {
        return post(request, ShareDomainResponse.class, builder -> builder.pathSegment("domains", request.getDomainId(), "relationships", "shared_organizations"))
            .checkpoint();
    }

    @Override
    public Mono<Void> unshare(UnshareDomainRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("domains", request.getDomainId(), "relationships", "shared_organizations", request.getOrganizationId()))
            .checkpoint();
    }

    @Override
    public Mono<UpdateDomainResponse> update(UpdateDomainRequest request) {
        return patch(request, UpdateDomainResponse.class, builder -> builder.pathSegment("domains", request.getDomainId()))
            .checkpoint();
    }
}

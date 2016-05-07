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

package org.cloudfoundry.reactor.client.v2.privatedomains;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.DeletePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.DeletePrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.GetPrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.GetPrivateDomainResponse;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsRequest;
import org.cloudfoundry.client.v2.privatedomains.ListPrivateDomainsResponse;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomains;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

/**
 * The Reactor-based implementation of {@link PrivateDomains}
 */
public final class ReactorPrivateDomains extends AbstractClientV2Operations implements PrivateDomains {

    /**
     * Creates an instance
     *
     * @param authorizationProvider the {@link AuthorizationProvider} to use when communicating with the server
     * @param httpClient            the {@link HttpClient} to use when communicating with the server
     * @param objectMapper          the {@link ObjectMapper} to use when communicating with the server
     * @param root                  the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     */
    public ReactorPrivateDomains(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
    }

    @Override
    public Mono<CreatePrivateDomainResponse> create(CreatePrivateDomainRequest request) {
        return post(request, CreatePrivateDomainResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "private_domains")));
    }

    @Override
    public Mono<DeletePrivateDomainResponse> delete(DeletePrivateDomainRequest request) {
        return delete(request, DeletePrivateDomainResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "private_domains", validRequest.getPrivateDomainId())));
    }

    @Override
    public Mono<GetPrivateDomainResponse> get(GetPrivateDomainRequest request) {
        return get(request, GetPrivateDomainResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "private_domains", validRequest.getPrivateDomainId())));
    }

    @Override
    public Mono<ListPrivateDomainsResponse> list(ListPrivateDomainsRequest request) {
        return get(request, ListPrivateDomainsResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "private_domains")));
    }

}

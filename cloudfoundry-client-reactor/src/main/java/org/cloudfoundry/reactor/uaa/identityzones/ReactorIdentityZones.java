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

package org.cloudfoundry.reactor.uaa.identityzones;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.uaa.AbstractUaaOperations;
import org.cloudfoundry.uaa.identityzones.CreateIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.CreateIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzones.DeleteIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.DeleteIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzones.GetIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.GetIdentityZoneResponse;
import org.cloudfoundry.uaa.identityzones.IdentityZones;
import org.cloudfoundry.uaa.identityzones.ListIdentityZonesRequest;
import org.cloudfoundry.uaa.identityzones.ListIdentityZonesResponse;
import org.cloudfoundry.uaa.identityzones.UpdateIdentityZoneRequest;
import org.cloudfoundry.uaa.identityzones.UpdateIdentityZoneResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link IdentityZones}
 */
public final class ReactorIdentityZones extends AbstractUaaOperations implements IdentityZones {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://uaa.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorIdentityZones(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateIdentityZoneResponse> create(CreateIdentityZoneRequest request) {
        return post(request, CreateIdentityZoneResponse.class, builder -> builder.pathSegment("identity-zones"))
            .checkpoint();
    }

    @Override
    public Mono<DeleteIdentityZoneResponse> delete(DeleteIdentityZoneRequest request) {
        return delete(request, DeleteIdentityZoneResponse.class, builder -> builder.pathSegment("identity-zones", request.getIdentityZoneId()))
            .checkpoint();
    }

    @Override
    public Mono<GetIdentityZoneResponse> get(GetIdentityZoneRequest request) {
        return get(request, GetIdentityZoneResponse.class, builder -> builder.pathSegment("identity-zones", request.getIdentityZoneId()))
            .checkpoint();
    }

    @Override
    public Mono<ListIdentityZonesResponse> list(ListIdentityZonesRequest request) {
        return get(request, ListIdentityZonesResponse.class, builder -> builder.pathSegment("identity-zones"))
            .checkpoint();
    }

    @Override
    public Mono<UpdateIdentityZoneResponse> update(UpdateIdentityZoneRequest request) {
        return put(request, UpdateIdentityZoneResponse.class, builder -> builder.pathSegment("identity-zones", request.getIdentityZoneId()))
            .checkpoint();
    }

}

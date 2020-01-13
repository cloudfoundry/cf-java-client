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

package org.cloudfoundry.reactor.client.v2.servicebindings;

import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.GetServiceBindingParametersRequest;
import org.cloudfoundry.client.v2.servicebindings.GetServiceBindingParametersResponse;
import org.cloudfoundry.client.v2.servicebindings.GetServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.GetServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsRequest;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsResponse;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingsV2;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link ServiceBindingsV2}
 */
public final class ReactorServiceBindingsV2 extends AbstractClientV2Operations implements ServiceBindingsV2 {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorServiceBindingsV2(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateServiceBindingResponse> create(CreateServiceBindingRequest request) {
        return post(request, CreateServiceBindingResponse.class, builder -> builder.pathSegment("service_bindings"))
            .checkpoint();
    }

    @Override
    public Mono<DeleteServiceBindingResponse> delete(DeleteServiceBindingRequest request) {
        return delete(request, DeleteServiceBindingResponse.class, builder -> builder.pathSegment("service_bindings", request.getServiceBindingId()))
            .checkpoint();
    }

    @Override
    public Mono<GetServiceBindingResponse> get(GetServiceBindingRequest request) {
        return get(request, GetServiceBindingResponse.class, builder -> builder.pathSegment("service_bindings", request.getServiceBindingId()))
            .checkpoint();
    }

    @Override
    public Mono<GetServiceBindingParametersResponse> getParameters(GetServiceBindingParametersRequest request) {
        return get(request, GetServiceBindingParametersResponse.class, builder -> builder.pathSegment("service_bindings", request.getServiceBindingId(), "parameters"))
            .checkpoint();
    }

    @Override
    public Mono<ListServiceBindingsResponse> list(ListServiceBindingsRequest request) {
        return get(request, ListServiceBindingsResponse.class, builder -> builder.pathSegment("service_bindings"))
            .checkpoint();
    }

}

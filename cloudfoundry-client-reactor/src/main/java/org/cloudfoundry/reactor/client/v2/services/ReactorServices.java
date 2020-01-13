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

package org.cloudfoundry.reactor.client.v2.services;

import org.cloudfoundry.client.v2.services.DeleteServiceRequest;
import org.cloudfoundry.client.v2.services.DeleteServiceResponse;
import org.cloudfoundry.client.v2.services.GetServiceRequest;
import org.cloudfoundry.client.v2.services.GetServiceResponse;
import org.cloudfoundry.client.v2.services.ListServiceServicePlansRequest;
import org.cloudfoundry.client.v2.services.ListServiceServicePlansResponse;
import org.cloudfoundry.client.v2.services.ListServicesRequest;
import org.cloudfoundry.client.v2.services.ListServicesResponse;
import org.cloudfoundry.client.v2.services.Services;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link Services}
 */
public final class ReactorServices extends AbstractClientV2Operations implements Services {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorServices(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<DeleteServiceResponse> delete(DeleteServiceRequest request) {
        return delete(request, DeleteServiceResponse.class, builder -> builder.pathSegment("services", request.getServiceId()))
            .checkpoint();
    }

    @Override
    public Mono<GetServiceResponse> get(GetServiceRequest request) {
        return get(request, GetServiceResponse.class, builder -> builder.pathSegment("services", request.getServiceId()))
            .checkpoint();
    }

    @Override
    public Mono<ListServicesResponse> list(ListServicesRequest request) {
        return get(request, ListServicesResponse.class, builder -> builder.pathSegment("services"))
            .checkpoint();
    }

    @Override
    public Mono<ListServiceServicePlansResponse> listServicePlans(ListServiceServicePlansRequest request) {
        return get(request, ListServiceServicePlansResponse.class, builder -> builder.pathSegment("services", request.getServiceId(), "service_plans"))
            .checkpoint();
    }

}

/*
 * Copyright 2013-2026 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.serviceroutebindings;

import java.util.Map;
import java.util.Optional;
import org.cloudfoundry.client.v3.serviceroutebindings.CreateServiceRouteBindingRequest;
import org.cloudfoundry.client.v3.serviceroutebindings.CreateServiceRouteBindingResponse;
import org.cloudfoundry.client.v3.serviceroutebindings.DeleteServiceRouteBindingRequest;
import org.cloudfoundry.client.v3.serviceroutebindings.DeleteServiceRouteBindingResponse;
import org.cloudfoundry.client.v3.serviceroutebindings.GetServiceRouteBindingParametersRequest;
import org.cloudfoundry.client.v3.serviceroutebindings.GetServiceRouteBindingParametersResponse;
import org.cloudfoundry.client.v3.serviceroutebindings.GetServiceRouteBindingRequest;
import org.cloudfoundry.client.v3.serviceroutebindings.GetServiceRouteBindingResponse;
import org.cloudfoundry.client.v3.serviceroutebindings.ListServiceRouteBindingsRequest;
import org.cloudfoundry.client.v3.serviceroutebindings.ListServiceRouteBindingsResponse;
import org.cloudfoundry.client.v3.serviceroutebindings.ServiceRouteBindingResource;
import org.cloudfoundry.client.v3.serviceroutebindings.ServiceRouteBindingsV3;
import org.cloudfoundry.client.v3.serviceroutebindings.UpdateServiceRouteBindingRequest;
import org.cloudfoundry.client.v3.serviceroutebindings.UpdateServiceRouteBindingResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

/**
 * The Reactor-based implementation of {@link ServiceRouteBindingsV3}
 */
public final class ReactorServiceRouteBindingsV3 extends AbstractClientV3Operations
        implements ServiceRouteBindingsV3 {

    public ReactorServiceRouteBindingsV3(
            ConnectionContext connectionContext,
            Mono<String> root,
            TokenProvider tokenProvider,
            Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateServiceRouteBindingResponse> create(
            CreateServiceRouteBindingRequest request) {
        return postWithResponse(
                        request,
                        ServiceRouteBindingResource.class,
                        builder -> builder.pathSegment("service_route_bindings"))
                .map(
                        responseTuple ->
                                CreateServiceRouteBindingResponse.builder()
                                        .serviceRouteBinding(responseTuple.getBody())
                                        .jobId(
                                                Optional.ofNullable(
                                                        extractJobId(responseTuple.getResponse())))
                                        .build())
                .checkpoint();
    }

    @Override
    public Mono<DeleteServiceRouteBindingResponse> delete(
            DeleteServiceRouteBindingRequest request) {
        return createOperator()
                .flatMap(
                        operator ->
                                operator.delete()
                                        .uri(
                                                builder ->
                                                        builder.pathSegment(
                                                                "service_route_bindings",
                                                                request.getServiceRouteBindingId()))
                                        .send(request)
                                        .response()
                                        .get())
                .map(
                        response ->
                                DeleteServiceRouteBindingResponse.builder()
                                        .jobId(Optional.ofNullable(extractJobId(response)))
                                        .build())
                .checkpoint();
    }

    @Override
    public Mono<GetServiceRouteBindingResponse> get(GetServiceRouteBindingRequest request) {
        return get(
                        request,
                        GetServiceRouteBindingResponse.class,
                        builder ->
                                builder.pathSegment(
                                        "service_route_bindings",
                                        request.getServiceRouteBindingId()))
                .checkpoint();
    }

    @Override
    public Mono<GetServiceRouteBindingParametersResponse> getParameters(
            GetServiceRouteBindingParametersRequest request) {
        return get(
                        request,
                        GetServiceRouteBindingParametersResponse.class,
                        builder ->
                                builder.pathSegment(
                                        "service_route_bindings",
                                        request.getServiceRouteBindingId(),
                                        "parameters"))
                .checkpoint();
    }

    @Override
    public Mono<ListServiceRouteBindingsResponse> list(ListServiceRouteBindingsRequest request) {
        return get(
                        request,
                        ListServiceRouteBindingsResponse.class,
                        builder -> builder.pathSegment("service_route_bindings"))
                .checkpoint();
    }

    @Override
    public Mono<UpdateServiceRouteBindingResponse> update(
            UpdateServiceRouteBindingRequest request) {
        return patch(
                        request,
                        UpdateServiceRouteBindingResponse.class,
                        builder ->
                                builder.pathSegment(
                                        "service_route_bindings",
                                        request.getServiceRouteBindingId()))
                .checkpoint();
    }
}

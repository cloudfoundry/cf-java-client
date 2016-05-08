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

package org.cloudfoundry.reactor.client.v2.serviceplanvisibilities;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.client.v2.serviceplanvisibilities.CreateServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.CreateServicePlanVisibilityResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.DeleteServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.DeleteServicePlanVisibilityResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.GetServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.GetServicePlanVisibilityResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ListServicePlanVisibilitiesRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ListServicePlanVisibilitiesResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilities;
import org.cloudfoundry.client.v2.serviceplanvisibilities.UpdateServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.UpdateServicePlanVisibilityResponse;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

/**
 * The Reactor-based implementation of {@link ServicePlanVisibilities}
 */
public final class ReactorServicePlanVisibilities extends AbstractClientV2Operations implements ServicePlanVisibilities {

    /**
     * Creates an instance
     *
     * @param authorizationProvider the {@link AuthorizationProvider} to use when communicating with the server
     * @param httpClient            the {@link HttpClient} to use when communicating with the server
     * @param objectMapper          the {@link ObjectMapper} to use when communicating with the server
     * @param root                  the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     */
    public ReactorServicePlanVisibilities(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
    }

    @Override
    public Mono<CreateServicePlanVisibilityResponse> create(CreateServicePlanVisibilityRequest request) {
        return post(request, CreateServicePlanVisibilityResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "service_plan_visibilities")));
    }

    @Override
    public Mono<DeleteServicePlanVisibilityResponse> delete(DeleteServicePlanVisibilityRequest request) {
        return delete(request, DeleteServicePlanVisibilityResponse.class,
            function((builder, validRequest) -> builder.pathSegment("v2", "service_plan_visibilities", validRequest.getServicePlanVisibilityId())));
    }

    @Override
    public Mono<GetServicePlanVisibilityResponse> get(GetServicePlanVisibilityRequest request) {
        return get(request, GetServicePlanVisibilityResponse.class,
            function((builder, validRequest) -> builder.pathSegment("v2", "service_plan_visibilities", validRequest.getServicePlanVisibilityId())));
    }

    @Override
    public Mono<ListServicePlanVisibilitiesResponse> list(ListServicePlanVisibilitiesRequest request) {
        return get(request, ListServicePlanVisibilitiesResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "service_plan_visibilities")));
    }

    @Override
    public Mono<UpdateServicePlanVisibilityResponse> update(UpdateServicePlanVisibilityRequest request) {
        return put(request, UpdateServicePlanVisibilityResponse.class,
            function((builder, validRequest) -> builder.pathSegment("v2", "service_plan_visibilities", validRequest.getServicePlanVisibilityId())));
    }

}

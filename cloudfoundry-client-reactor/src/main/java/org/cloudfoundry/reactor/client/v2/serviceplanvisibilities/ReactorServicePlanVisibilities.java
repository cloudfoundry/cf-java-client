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

package org.cloudfoundry.reactor.client.v2.serviceplanvisibilities;

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
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link ServicePlanVisibilities}
 */
public final class ReactorServicePlanVisibilities extends AbstractClientV2Operations implements ServicePlanVisibilities {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorServicePlanVisibilities(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateServicePlanVisibilityResponse> create(CreateServicePlanVisibilityRequest request) {
        return post(request, CreateServicePlanVisibilityResponse.class, builder -> builder.pathSegment("service_plan_visibilities"))
            .checkpoint();
    }

    @Override
    public Mono<DeleteServicePlanVisibilityResponse> delete(DeleteServicePlanVisibilityRequest request) {
        return delete(request, DeleteServicePlanVisibilityResponse.class, builder -> builder.pathSegment("service_plan_visibilities", request.getServicePlanVisibilityId()))
            .checkpoint();
    }

    @Override
    public Mono<GetServicePlanVisibilityResponse> get(GetServicePlanVisibilityRequest request) {
        return get(request, GetServicePlanVisibilityResponse.class, builder -> builder.pathSegment("service_plan_visibilities", request.getServicePlanVisibilityId()))
            .checkpoint();
    }

    @Override
    public Mono<ListServicePlanVisibilitiesResponse> list(ListServicePlanVisibilitiesRequest request) {
        return get(request, ListServicePlanVisibilitiesResponse.class, builder -> builder.pathSegment("service_plan_visibilities"))
            .checkpoint();
    }

    @Override
    public Mono<UpdateServicePlanVisibilityResponse> update(UpdateServicePlanVisibilityRequest request) {
        return put(request, UpdateServicePlanVisibilityResponse.class, builder -> builder.pathSegment("service_plan_visibilities", request.getServicePlanVisibilityId()))
            .checkpoint();
    }

}

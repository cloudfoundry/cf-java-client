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

package org.cloudfoundry.reactor.client.v2.serviceplans;

import org.cloudfoundry.client.v2.serviceplans.DeleteServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.DeleteServicePlanResponse;
import org.cloudfoundry.client.v2.serviceplans.GetServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.GetServicePlanResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlanServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlanServiceInstancesResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansResponse;
import org.cloudfoundry.client.v2.serviceplans.ServicePlans;
import org.cloudfoundry.client.v2.serviceplans.UpdateServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.UpdateServicePlanResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link ServicePlans}
 */
public final class ReactorServicePlans extends AbstractClientV2Operations implements ServicePlans {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorServicePlans(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<DeleteServicePlanResponse> delete(DeleteServicePlanRequest request) {
        return delete(request, DeleteServicePlanResponse.class, builder -> builder.pathSegment("service_plans", request.getServicePlanId()))
            .checkpoint();
    }

    @Override
    public Mono<GetServicePlanResponse> get(GetServicePlanRequest request) {
        return get(request, GetServicePlanResponse.class, builder -> builder.pathSegment("service_plans", request.getServicePlanId()))
            .checkpoint();
    }

    @Override
    public Mono<ListServicePlansResponse> list(ListServicePlansRequest request) {
        return get(request, ListServicePlansResponse.class, builder -> builder.pathSegment("service_plans"))
            .checkpoint();
    }

    @Override
    public Mono<ListServicePlanServiceInstancesResponse> listServiceInstances(ListServicePlanServiceInstancesRequest request) {
        return get(request, ListServicePlanServiceInstancesResponse.class,
            builder -> builder.pathSegment("service_plans", request.getServicePlanId(), "service_instances"))
            .checkpoint();
    }

    @Override
    public Mono<UpdateServicePlanResponse> update(UpdateServicePlanRequest request) {
        return put(request, UpdateServicePlanResponse.class, builder -> builder.pathSegment("service_plans", request.getServicePlanId()))
            .checkpoint();
    }

}

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

package org.cloudfoundry.reactor.client.v2.serviceplans;


import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

/**
 * The Reactor-based implementation of {@link ServicePlans}
 */
public final class ReactorServicePlans extends AbstractClientV2Operations implements ServicePlans {

    /**
     * Creates an instance
     *
     * @param authorizationProvider the {@link AuthorizationProvider} to use when communicating with the server
     * @param httpClient            the {@link HttpClient} to use when communicating with the server
     * @param objectMapper          the {@link ObjectMapper} to use when communicating with the server
     * @param root                  the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     */
    public ReactorServicePlans(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
    }

    @Override
    public Mono<DeleteServicePlanResponse> delete(DeleteServicePlanRequest request) {
        return delete(request, DeleteServicePlanResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "service_plans", validRequest.getServicePlanId())));
    }

    @Override
    public Mono<GetServicePlanResponse> get(GetServicePlanRequest request) {
        return get(request, GetServicePlanResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "service_plans", validRequest.getServicePlanId())));
    }

    @Override
    public Mono<ListServicePlansResponse> list(ListServicePlansRequest request) {
        return get(request, ListServicePlansResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "service_plans")));
    }

    @Override
    public Mono<ListServicePlanServiceInstancesResponse> listServiceInstances(ListServicePlanServiceInstancesRequest request) {
        return get(request, ListServicePlanServiceInstancesResponse.class,
            function((builder, validRequest) -> builder.pathSegment("v2", "service_plans", validRequest.getServicePlanId(), "service_instances")));
    }

    @Override
    public Mono<UpdateServicePlanResponse> update(UpdateServicePlanRequest request) {
        return put(request, UpdateServicePlanResponse.class, function((builder, validRequest) -> builder.pathSegment("v2", "service_plans", validRequest.getServicePlanId())));
    }

}

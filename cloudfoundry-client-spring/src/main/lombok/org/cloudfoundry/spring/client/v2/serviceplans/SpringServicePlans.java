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

package org.cloudfoundry.spring.client.v2.serviceplans;


import lombok.ToString;
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
import org.cloudfoundry.spring.client.v2.FilterBuilder;
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.cloudfoundry.spring.util.QueryBuilder;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.net.URI;

/**
 * The Spring-based implementation of {@link ServicePlans}
 */
@ToString(callSuper = true)
public final class SpringServicePlans extends AbstractSpringOperations implements ServicePlans {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations } to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringServicePlans(RestOperations restOperations, URI root, Scheduler schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<DeleteServicePlanResponse> delete(DeleteServicePlanRequest request) {
        return delete(request, DeleteServicePlanResponse.class, builder -> {
            builder.pathSegment("v2", "service_plans", request.getServicePlanId());
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<GetServicePlanResponse> get(GetServicePlanRequest request) {
        return get(request, GetServicePlanResponse.class, builder -> builder.pathSegment("v2", "service_plans", request.getServicePlanId()));
    }

    @Override
    public Mono<ListServicePlansResponse> list(ListServicePlansRequest request) {
        return get(request, ListServicePlansResponse.class, builder -> {
            builder.pathSegment("v2", "service_plans");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<ListServicePlanServiceInstancesResponse> listServiceInstances(ListServicePlanServiceInstancesRequest request) {
        return get(request, ListServicePlanServiceInstancesResponse.class, builder -> {
            builder.pathSegment("v2", "service_plans", request.getServicePlanId(), "service_instances");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<UpdateServicePlanResponse> update(UpdateServicePlanRequest request) {
        return put(request, UpdateServicePlanResponse.class, builder -> builder.pathSegment("v2", "service_plans", request.getServicePlanId()));
    }

}

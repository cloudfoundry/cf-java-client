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

package org.cloudfoundry.spring.client.v2.serviceplanvisibilities;


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
import org.cloudfoundry.spring.client.v2.FilterBuilder;
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.cloudfoundry.spring.util.QueryBuilder;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SchedulerGroup;

import java.net.URI;

public final class SpringServicePlanVisibilities extends AbstractSpringOperations implements ServicePlanVisibilities {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringServicePlanVisibilities(RestOperations restOperations, URI root, SchedulerGroup schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<CreateServicePlanVisibilityResponse> create(CreateServicePlanVisibilityRequest request) {
        return post(request, CreateServicePlanVisibilityResponse.class, builder -> builder.pathSegment("v2", "service_plan_visibilities"));
    }

    @Override
    public Mono<DeleteServicePlanVisibilityResponse> delete(DeleteServicePlanVisibilityRequest request) {
        return delete(request, DeleteServicePlanVisibilityResponse.class, builder -> {
            builder.pathSegment("v2", "service_plan_visibilities", request.getServicePlanVisibilityId());
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<GetServicePlanVisibilityResponse> get(GetServicePlanVisibilityRequest request) {
        return get(request, GetServicePlanVisibilityResponse.class, builder -> builder.pathSegment("v2", "service_plan_visibilities", request.getServicePlanVisibilityId()));
    }

    @Override
    public Mono<ListServicePlanVisibilitiesResponse> list(ListServicePlanVisibilitiesRequest request) {
        return get(request, ListServicePlanVisibilitiesResponse.class, builder -> {
            builder.pathSegment("v2", "service_plan_visibilities");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<UpdateServicePlanVisibilityResponse> update(UpdateServicePlanVisibilityRequest request) {
        return put(request, UpdateServicePlanVisibilityResponse.class, builder -> builder.pathSegment("v2", "service_plan_visibilities", request.getServicePlanVisibilityId()));
    }

}

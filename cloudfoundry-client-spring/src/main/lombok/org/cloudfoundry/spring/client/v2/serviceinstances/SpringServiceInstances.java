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

package org.cloudfoundry.spring.client.v2.serviceinstances;

import lombok.ToString;
import org.cloudfoundry.client.v2.serviceinstances.BindServiceInstanceToRouteRequest;
import org.cloudfoundry.client.v2.serviceinstances.BindServiceInstanceToRouteResponse;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstancePermissionsRequest;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstancePermissionsResponse;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceBindingsRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceBindingsResponse;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesResponse;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstances;
import org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceResponse;
import org.cloudfoundry.spring.client.v2.FilterBuilder;
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.cloudfoundry.spring.util.QueryBuilder;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SchedulerGroup;

import java.net.URI;

/**
 * The Spring-based implementation of {@link ServiceInstances}
 */
@ToString(callSuper = true)
public final class SpringServiceInstances extends AbstractSpringOperations implements ServiceInstances {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringServiceInstances(RestOperations restOperations, URI root, SchedulerGroup schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<BindServiceInstanceToRouteResponse> bindToRoute(BindServiceInstanceToRouteRequest request) {
        return put(request, BindServiceInstanceToRouteResponse.class, builder -> builder.pathSegment("v2", "service_instances", request.getServiceInstanceId(), "routes", request.getRouteId()));
    }

    @Override
    public Mono<CreateServiceInstanceResponse> create(CreateServiceInstanceRequest request) {
        return post(request, CreateServiceInstanceResponse.class, builder -> {
            builder.pathSegment("v2", "service_instances");
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<DeleteServiceInstanceResponse> delete(DeleteServiceInstanceRequest request) {
        return delete(request, DeleteServiceInstanceResponse.class, builder -> {
            builder.pathSegment("v2", "service_instances", request.getServiceInstanceId());
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<GetServiceInstanceResponse> get(GetServiceInstanceRequest request) {
        return get(request, GetServiceInstanceResponse.class, builder -> builder.pathSegment("v2", "service_instances", request.getServiceInstanceId()));
    }

    @Override
    public Mono<GetServiceInstancePermissionsResponse> getPermissions(GetServiceInstancePermissionsRequest request) {
        return get(request, GetServiceInstancePermissionsResponse.class, builder -> builder.pathSegment("v2", "service_instances", request.getServiceInstanceId(), "permissions"));
    }

    @Override
    public Mono<ListServiceInstancesResponse> list(ListServiceInstancesRequest request) {
        return get(request, ListServiceInstancesResponse.class, builder -> {
            builder.pathSegment("v2", "service_instances");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<ListServiceInstanceServiceBindingsResponse> listServiceBindings(ListServiceInstanceServiceBindingsRequest request) {
        return get(request, ListServiceInstanceServiceBindingsResponse.class, builder -> {
            builder.pathSegment("v2", "service_instances", request.getServiceInstanceId(), "service_bindings");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<UpdateServiceInstanceResponse> update(UpdateServiceInstanceRequest request) {
        return put(request, UpdateServiceInstanceResponse.class, builder -> {
            builder.pathSegment("v2", "service_instances", request.getServiceInstanceId());
            QueryBuilder.augment(builder, request);
        });
    }

}

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

package org.cloudfoundry.spring.client.v2.routemappings;

import lombok.ToString;
import org.cloudfoundry.client.v2.routemappings.CreateRouteMappingRequest;
import org.cloudfoundry.client.v2.routemappings.CreateRouteMappingResponse;
import org.cloudfoundry.client.v2.routemappings.DeleteRouteMappingRequest;
import org.cloudfoundry.client.v2.routemappings.DeleteRouteMappingResponse;
import org.cloudfoundry.client.v2.routemappings.GetRouteMappingRequest;
import org.cloudfoundry.client.v2.routemappings.GetRouteMappingResponse;
import org.cloudfoundry.client.v2.routemappings.ListRouteMappingsRequest;
import org.cloudfoundry.client.v2.routemappings.ListRouteMappingsResponse;
import org.cloudfoundry.client.v2.routemappings.RouteMappings;
import org.cloudfoundry.spring.client.v2.FilterBuilder;
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.cloudfoundry.spring.util.QueryBuilder;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SchedulerGroup;

import java.net.URI;

/**
 * The Spring-based implementation of {@link RouteMappings}
 */
@ToString(callSuper = true)
public final class SpringRouteMappings extends AbstractSpringOperations implements RouteMappings {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringRouteMappings(RestOperations restOperations, URI root, SchedulerGroup schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<CreateRouteMappingResponse> create(CreateRouteMappingRequest request) {
        return post(request, CreateRouteMappingResponse.class, builder -> builder.pathSegment("v2", "route_mappings"));
    }

    @Override
    public Mono<DeleteRouteMappingResponse> delete(DeleteRouteMappingRequest request) {
        return delete(request, DeleteRouteMappingResponse.class, builder -> {
            builder.pathSegment("v2", "route_mappings", request.getRouteMappingId());
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<GetRouteMappingResponse> get(GetRouteMappingRequest request) {
        return get(request, GetRouteMappingResponse.class, builder -> builder.pathSegment("v2", "route_mappings", request.getRouteMappingId()));
    }

    @Override
    public Mono<ListRouteMappingsResponse> list(ListRouteMappingsRequest request) {
        return get(request, ListRouteMappingsResponse.class, builder -> {
            builder.pathSegment("v2", "route_mappings");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

}

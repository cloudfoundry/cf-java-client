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

package org.cloudfoundry.spring.client.v2.routes;

import lombok.ToString;
import org.cloudfoundry.client.v2.routes.AssociateRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.AssociateRouteApplicationResponse;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.routes.DeleteRouteResponse;
import org.cloudfoundry.client.v2.routes.GetRouteRequest;
import org.cloudfoundry.client.v2.routes.GetRouteResponse;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsResponse;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.client.v2.routes.ListRoutesResponse;
import org.cloudfoundry.client.v2.routes.RemoveRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.RouteExistsRequest;
import org.cloudfoundry.client.v2.routes.Routes;
import org.cloudfoundry.client.v2.routes.UpdateRouteRequest;
import org.cloudfoundry.client.v2.routes.UpdateRouteResponse;
import org.cloudfoundry.spring.client.v2.FilterBuilder;
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.cloudfoundry.spring.util.QueryBuilder;
import org.cloudfoundry.util.ExceptionUtils;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Routes}
 */
@ToString(callSuper = true)
public final class SpringRoutes extends AbstractSpringOperations implements Routes {

    private static final int CF_NOT_FOUND = 10000;

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringRoutes(RestOperations restOperations, URI root, Scheduler schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<AssociateRouteApplicationResponse> associateApplication(AssociateRouteApplicationRequest request) {
        return put(request, AssociateRouteApplicationResponse.class, builder -> builder.pathSegment("v2", "routes", request.getRouteId(), "apps", request.getApplicationId()));
    }

    @Override
    public Mono<CreateRouteResponse> create(CreateRouteRequest request) {
        return post(request, CreateRouteResponse.class, builder -> {
            builder.pathSegment("v2", "routes");
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<DeleteRouteResponse> delete(DeleteRouteRequest request) {
        return delete(request, DeleteRouteResponse.class, builder -> {
            builder.pathSegment("v2", "routes", request.getRouteId());
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<Boolean> exists(RouteExistsRequest request) {
        return get(request, Boolean.class, builder -> {
            builder.pathSegment("v2", "routes", "reserved", "domain", request.getDomainId(), "host", request.getHost());
            QueryBuilder.augment(builder, request);
        })
            .defaultIfEmpty(true)
            .otherwise(ExceptionUtils.replace(CF_NOT_FOUND, () -> Mono.just(false)));
    }

    @Override
    public Mono<GetRouteResponse> get(GetRouteRequest request) {
        return get(request, GetRouteResponse.class, builder -> builder.pathSegment("v2", "routes", request.getRouteId()));
    }

    @Override
    public Mono<ListRoutesResponse> list(ListRoutesRequest request) {
        return get(request, ListRoutesResponse.class, builder -> {
            builder.pathSegment("v2", "routes");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<ListRouteApplicationsResponse> listApplications(ListRouteApplicationsRequest request) {
        return get(request, ListRouteApplicationsResponse.class, builder -> {
            builder.pathSegment("v2", "routes", request.getRouteId(), "apps");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Mono<Void> removeApplication(RemoveRouteApplicationRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("v2", "routes", request.getRouteId(), "apps", request.getApplicationId()));
    }

    @Override
    public Mono<UpdateRouteResponse> update(UpdateRouteRequest request) {
        return put(request, UpdateRouteResponse.class, builder -> builder.pathSegment("v2", "routes", request.getRouteId()));
    }

}

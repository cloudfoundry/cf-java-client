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

package org.cloudfoundry.client.spring.v2.routes;

import lombok.ToString;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.spring.v2.FilterBuilder;
import org.cloudfoundry.client.v2.CloudFoundryException;
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
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SchedulerGroup;
import reactor.fn.Consumer;
import reactor.fn.Function;

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
     * @param processorGroup The group to use when making requests
     */
    public SpringRoutes(RestOperations restOperations, URI root, SchedulerGroup processorGroup) {
        super(restOperations, root, processorGroup);
    }

    @Override
    public Mono<AssociateRouteApplicationResponse> associateApplication(final AssociateRouteApplicationRequest request) {
        return put(request, AssociateRouteApplicationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "routes", request.getRouteId(), "apps", request.getApplicationId());
            }

        });
    }

    @Override
    public Mono<CreateRouteResponse> create(final CreateRouteRequest request) {
        return post(request, CreateRouteResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "routes");
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<DeleteRouteResponse> delete(final DeleteRouteRequest request) {
        return delete(request, DeleteRouteResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "routes", request.getRouteId());
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<Boolean> exists(final RouteExistsRequest request) {
        return get(request, Boolean.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "routes", "reserved", "domain", request.getDomainId(), "host", request.getHost());
                QueryBuilder.augment(builder, request);
            }

        })
            .defaultIfEmpty(true)
            .otherwise(new Function<Throwable, Mono<? extends Boolean>>() {

                @Override
                public Mono<? extends Boolean> apply(Throwable throwable) {
                    if (throwable instanceof CloudFoundryException && ((CloudFoundryException) throwable).getCode() == CF_NOT_FOUND) {
                        return Mono.just(false);
                    } else {
                        return Mono.error(throwable);
                    }
                }

            });
    }

    @Override
    public Mono<GetRouteResponse> get(final GetRouteRequest request) {
        return get(request, GetRouteResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "routes", request.getRouteId());
            }

        });
    }

    @Override
    public Mono<ListRoutesResponse> list(final ListRoutesRequest request) {
        return get(request, ListRoutesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "routes");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<ListRouteApplicationsResponse> listApplications(final ListRouteApplicationsRequest request) {
        return get(request, ListRouteApplicationsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "routes", request.getRouteId(), "apps");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<Void> removeApplication(final RemoveRouteApplicationRequest request) {
        return delete(request, Void.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "routes", request.getRouteId(), "apps", request.getApplicationId());
            }

        });
    }

    @Override
    public Mono<UpdateRouteResponse> update(final UpdateRouteRequest request) {
        return put(request, UpdateRouteResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "routes", request.getRouteId());
            }

        });
    }

}

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

package org.cloudfoundry.client.spring.v3.applications;

import lombok.ToString;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.spring.v3.FilterBuilder;
import org.cloudfoundry.client.v3.applications.ApplicationsV3;
import org.cloudfoundry.client.v3.applications.AssignApplicationDropletRequest;
import org.cloudfoundry.client.v3.applications.AssignApplicationDropletResponse;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v3.applications.DeleteApplicationInstanceRequest;
import org.cloudfoundry.client.v3.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationDropletsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationDropletsResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationPackagesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationPackagesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationProcessesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationProcessesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationRoutesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationRoutesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v3.applications.MapApplicationRouteRequest;
import org.cloudfoundry.client.v3.applications.ScaleApplicationRequest;
import org.cloudfoundry.client.v3.applications.ScaleApplicationResponse;
import org.cloudfoundry.client.v3.applications.StartApplicationRequest;
import org.cloudfoundry.client.v3.applications.StartApplicationResponse;
import org.cloudfoundry.client.v3.applications.StopApplicationRequest;
import org.cloudfoundry.client.v3.applications.StopApplicationResponse;
import org.cloudfoundry.client.v3.applications.UnmapApplicationRouteRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationResponse;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ProcessorGroup;
import reactor.fn.Consumer;

import java.net.URI;

/**
 * The Spring-based implementation of {@link ApplicationsV3}
 */
@ToString(callSuper = true)
public final class SpringApplicationsV3 extends AbstractSpringOperations implements ApplicationsV3 {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param processorGroup The group to use when making requests
     */
    public SpringApplicationsV3(RestOperations restOperations, URI root, ProcessorGroup processorGroup) {
        super(restOperations, root, processorGroup);
    }

    @Override
    public Mono<AssignApplicationDropletResponse> assignDroplet(final AssignApplicationDropletRequest request) {
        return put(request, AssignApplicationDropletResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps", request.getApplicationId(), "current_droplet");
            }

        });
    }

    @Override
    public Mono<CreateApplicationResponse> create(CreateApplicationRequest request) {
        return post(request, CreateApplicationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps");
            }

        });
    }

    @Override
    public Mono<Void> delete(final DeleteApplicationRequest request) {
        return delete(request, Void.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps", request.getApplicationId());
            }

        });
    }

    @Override
    public Mono<Void> deleteInstance(final DeleteApplicationInstanceRequest request) {
        return delete(request, Void.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps", request.getApplicationId(), "processes", request.getType(), "instances",
                        request.getIndex());
            }

        });
    }

    @Override
    public Mono<GetApplicationResponse> get(final GetApplicationRequest request) {
        return get(request, GetApplicationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps", request.getApplicationId());
            }

        });
    }

    @Override
    public Mono<GetApplicationEnvironmentResponse> getEnvironment(final GetApplicationEnvironmentRequest request) {
        return get(request, GetApplicationEnvironmentResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps", request.getApplicationId(), "env");
            }

        });
    }

    @Override
    public Mono<GetApplicationProcessResponse> getProcess(final GetApplicationProcessRequest request) {
        return get(request, GetApplicationProcessResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps", request.getApplicationId(), "processes", request.getType());
            }

        });
    }

    @Override
    public Mono<ListApplicationsResponse> list(final ListApplicationsRequest request) {
        return get(request, ListApplicationsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<ListApplicationDropletsResponse> listDroplets(final ListApplicationDropletsRequest request) {
        return get(request, ListApplicationDropletsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps", request.getApplicationId(), "droplets");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<ListApplicationPackagesResponse> listPackages(final ListApplicationPackagesRequest request) {
        return get(request, ListApplicationPackagesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps", request.getApplicationId(), "packages");
            }

        });
    }

    @Override
    public Mono<ListApplicationProcessesResponse> listProcesses(final ListApplicationProcessesRequest request) {
        return get(request, ListApplicationProcessesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps", request.getApplicationId(), "processes");
            }

        });
    }

    @Override
    public Mono<ListApplicationRoutesResponse> listRoutes(final ListApplicationRoutesRequest request) {
        return get(request, ListApplicationRoutesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps", request.getApplicationId(), "routes");
            }

        });
    }

    @Override
    public Mono<Void> mapRoute(final MapApplicationRouteRequest request) {
        return put(request, Void.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps", request.getApplicationId(), "routes");
            }

        });
    }

    @Override
    public Mono<ScaleApplicationResponse> scale(final ScaleApplicationRequest request) {
        return put(request, ScaleApplicationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps", request.getApplicationId(), "processes", request.getType(), "scale");
            }

        });
    }

    @Override
    public Mono<StartApplicationResponse> start(final StartApplicationRequest request) {
        return put(request, StartApplicationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps", request.getApplicationId(), "start");
            }

        });
    }

    @Override
    public Mono<StopApplicationResponse> stop(final StopApplicationRequest request) {
        return put(request, StopApplicationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps", request.getApplicationId(), "stop");
            }

        });
    }

    @Override
    public Mono<Void> unmapRoute(final UnmapApplicationRouteRequest request) {
        return delete(request, Void.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps", request.getApplicationId(), "routes");
            }

        });
    }

    @Override
    public Mono<UpdateApplicationResponse> update(final UpdateApplicationRequest request) {
        return patch(request, UpdateApplicationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "apps", request.getApplicationId());
            }

        });
    }

}

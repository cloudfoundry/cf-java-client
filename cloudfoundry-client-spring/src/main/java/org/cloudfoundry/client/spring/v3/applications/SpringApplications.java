/*
 * Copyright 2013-2015 the original author or authors.
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

import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.spring.v3.FilterBuilder;
import org.cloudfoundry.client.v3.applications.Applications;
import org.cloudfoundry.client.v3.applications.AssignApplicationDropletRequest;
import org.cloudfoundry.client.v3.applications.AssignApplicationDropletResponse;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v3.applications.DeleteApplicationProcessRequest;
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
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v3.applications.MapApplicationRouteRequest;
import org.cloudfoundry.client.v3.applications.ScaleApplicationRequest;
import org.cloudfoundry.client.v3.applications.ScaleApplicationResponse;
import org.cloudfoundry.client.v3.applications.StartApplicationRequest;
import org.cloudfoundry.client.v3.applications.StartApplicationResponse;
import org.cloudfoundry.client.v3.applications.StopApplicationRequest;
import org.cloudfoundry.client.v3.applications.StopApplicationResponse;
import org.cloudfoundry.client.v3.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationResponse;
import org.reactivestreams.Publisher;
import org.springframework.web.client.RestOperations;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Applications}
 */
public final class SpringApplications extends AbstractSpringOperations implements Applications {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     */
    public SpringApplications(RestOperations restOperations, URI root) {
        super(restOperations, root);
    }

    @Override
    public Publisher<AssignApplicationDropletResponse> assignDroplet(AssignApplicationDropletRequest request) {
        return put(request, AssignApplicationDropletResponse.class,
                builder -> builder.pathSegment("v3", "apps", request.getId(), "current_droplet"));
    }

    @Override
    public Publisher<CreateApplicationResponse> create(CreateApplicationRequest request) {
        return post(request, CreateApplicationResponse.class, builder -> builder.pathSegment("v3", "apps"));
    }

    @Override
    public Publisher<Void> delete(DeleteApplicationRequest request) {
        return delete(request, builder -> builder.pathSegment("v3", "apps", request.getId()));
    }

    @Override
    public Publisher<Void> deleteProcess(DeleteApplicationProcessRequest request) {
        return delete(request, builder -> builder.pathSegment("v3", "apps", request.getId(), "processes",
                request.getType(), "instances", request.getIndex()));
    }

    @Override
    public Publisher<GetApplicationResponse> get(GetApplicationRequest request) {
        return get(request, GetApplicationResponse.class,
                builder -> builder.pathSegment("v3", "apps", request.getId()));
    }

    @Override
    public Publisher<GetApplicationEnvironmentResponse> getEnvironment(GetApplicationEnvironmentRequest request) {
        return get(request, GetApplicationEnvironmentResponse.class,
                builder -> builder.pathSegment("v3", "apps", request.getId(), "env"));
    }

    @Override
    public Publisher<GetApplicationProcessResponse> getProcess(GetApplicationProcessRequest request) {
        return get(request, GetApplicationProcessResponse.class,
                builder -> builder.pathSegment("v3", "apps", request.getId(), "processes", request.getType()));
    }

    @Override
    public Publisher<ListApplicationsResponse> list(ListApplicationsRequest request) {
        return get(request, ListApplicationsResponse.class, builder -> {
            builder.pathSegment("v3", "apps");
            FilterBuilder.augment(builder, request);
            QueryBuilder.augment(builder, request);
        });
    }

    @Override
    public Publisher<ListApplicationDropletsResponse> listDroplets(ListApplicationDropletsRequest request) {
        return get(request, ListApplicationDropletsResponse.class,
                builder -> builder.pathSegment("v3", "apps", request.getId(), "droplets"));
    }

    @Override
    public Publisher<ListApplicationPackagesResponse> listPackages(ListApplicationPackagesRequest request) {
        return get(request, ListApplicationPackagesResponse.class,
                builder -> builder.pathSegment("v3", "apps", request.getId(), "packages"));
    }

    @Override
    public Publisher<ListApplicationProcessesResponse> listProcesses(ListApplicationProcessesRequest request) {
        return get(request, ListApplicationProcessesResponse.class,
                builder -> builder.pathSegment("v3", "apps", request.getId(), "processes"));
    }

    @Override
    public Publisher<Void> mapRoute(MapApplicationRouteRequest request) {
        return put(request, Void.class, builder -> builder.pathSegment("v3", "apps", request.getId(), "routes"));
    }

    @Override
    public Publisher<ScaleApplicationResponse> scale(ScaleApplicationRequest request) {
        return put(request, ScaleApplicationResponse.class,
                builder -> builder.pathSegment("v3", "apps", request.getId(), "processes", request.getType(), "scale"));
    }

    @Override
    public Publisher<StartApplicationResponse> start(StartApplicationRequest request) {
        return put(request, StartApplicationResponse.class,
                builder -> builder.pathSegment("v3", "apps", request.getId(), "start"));
    }

    @Override
    public Publisher<StopApplicationResponse> stop(StopApplicationRequest request) {
        return put(request, StopApplicationResponse.class,
                builder -> builder.pathSegment("v3", "apps", request.getId(), "stop"));
    }

    @Override
    public Publisher<UpdateApplicationResponse> update(UpdateApplicationRequest request) {
        return patch(request, UpdateApplicationResponse.class,
                builder -> builder.pathSegment("v3", "apps", request.getId()));
    }

}

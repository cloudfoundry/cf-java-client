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

package org.cloudfoundry.reactor.client.v3.applications;

import org.cloudfoundry.client.v3.applications.ApplicationsV3;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v3.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationCurrentDropletRelationshipRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationCurrentDropletRelationshipResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationCurrentDropletRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationCurrentDropletResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentVariablesRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentVariablesResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessStatisticsRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessStatisticsResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationBuildsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationBuildsResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationDropletsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationDropletsResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationPackagesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationPackagesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationProcessesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationProcessesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationRoutesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationRoutesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationTasksRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationTasksResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v3.applications.ScaleApplicationRequest;
import org.cloudfoundry.client.v3.applications.ScaleApplicationResponse;
import org.cloudfoundry.client.v3.applications.SetApplicationCurrentDropletRequest;
import org.cloudfoundry.client.v3.applications.SetApplicationCurrentDropletResponse;
import org.cloudfoundry.client.v3.applications.StartApplicationRequest;
import org.cloudfoundry.client.v3.applications.StartApplicationResponse;
import org.cloudfoundry.client.v3.applications.StopApplicationRequest;
import org.cloudfoundry.client.v3.applications.StopApplicationResponse;
import org.cloudfoundry.client.v3.applications.TerminateApplicationInstanceRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationEnvironmentVariablesRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationEnvironmentVariablesResponse;
import org.cloudfoundry.client.v3.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link ApplicationsV3}
 */
public final class ReactorApplicationsV3 extends AbstractClientV3Operations implements ApplicationsV3 {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorApplicationsV3(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CreateApplicationResponse> create(CreateApplicationRequest request) {
        return post(request, CreateApplicationResponse.class, builder -> builder.pathSegment("apps"))
            .checkpoint();
    }

    @Override
    public Mono<String> delete(DeleteApplicationRequest request) {
        return delete(request, builder -> builder.pathSegment("apps", request.getApplicationId()))
            .checkpoint();
    }

    @Override
    public Mono<GetApplicationResponse> get(GetApplicationRequest request) {
        return get(request, GetApplicationResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId()))
            .checkpoint();
    }

    @Override
    public Mono<GetApplicationCurrentDropletResponse> getCurrentDroplet(GetApplicationCurrentDropletRequest request) {
        return get(request, GetApplicationCurrentDropletResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "droplets", "current"))
            .checkpoint();
    }

    @Override
    public Mono<GetApplicationCurrentDropletRelationshipResponse> getCurrentDropletRelationship(GetApplicationCurrentDropletRelationshipRequest request) {
        return get(request, GetApplicationCurrentDropletRelationshipResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "relationships", "current_droplet"))
            .checkpoint();
    }

    @Override
    public Mono<GetApplicationEnvironmentResponse> getEnvironment(GetApplicationEnvironmentRequest request) {
        return get(request, GetApplicationEnvironmentResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "env"))
            .checkpoint();
    }

    @Override
    public Mono<GetApplicationEnvironmentVariablesResponse> getEnvironmentVariables(GetApplicationEnvironmentVariablesRequest request) {
        return get(request, GetApplicationEnvironmentVariablesResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "environment_variables"))
            .checkpoint();
    }

    @Override
    public Mono<GetApplicationProcessResponse> getProcess(GetApplicationProcessRequest request) {
        return get(request, GetApplicationProcessResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "processes", request.getType()))
            .checkpoint();
    }

    @Override
    public Mono<GetApplicationProcessStatisticsResponse> getProcessStatistics(GetApplicationProcessStatisticsRequest request) {
        return get(request, GetApplicationProcessStatisticsResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "processes", request.getType(), "stats"))
            .checkpoint();
    }

    @Override
    public Mono<ListApplicationsResponse> list(ListApplicationsRequest request) {
        return get(request, ListApplicationsResponse.class, builder -> builder.pathSegment("apps"))
            .checkpoint();
    }

    @Override
    public Mono<ListApplicationBuildsResponse> listBuilds(ListApplicationBuildsRequest request) {
        return get(request, ListApplicationBuildsResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "builds"))
            .checkpoint();
    }

    @Override
    public Mono<ListApplicationDropletsResponse> listDroplets(ListApplicationDropletsRequest request) {
        return get(request, ListApplicationDropletsResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "droplets"))
            .checkpoint();
    }

    @Override
    public Mono<ListApplicationPackagesResponse> listPackages(ListApplicationPackagesRequest request) {
        return get(request, ListApplicationPackagesResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "packages"))
            .checkpoint();
    }

    @Override
    public Mono<ListApplicationProcessesResponse> listProcesses(ListApplicationProcessesRequest request) {
        return get(request, ListApplicationProcessesResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "processes"))
            .checkpoint();
    }

    @Override
    public Mono<ListApplicationRoutesResponse> listRoutes(ListApplicationRoutesRequest request) {
        return get(request, ListApplicationRoutesResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "routes"))
            .checkpoint();
    }

    @Override
    public Mono<ListApplicationTasksResponse> listTasks(ListApplicationTasksRequest request) {
        return get(request, ListApplicationTasksResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "tasks"))
            .checkpoint();
    }

    @Override
    public Mono<ScaleApplicationResponse> scale(ScaleApplicationRequest request) {
        return put(request, ScaleApplicationResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "processes", request.getType(), "actions", "scale"))
            .checkpoint();
    }

    @Override
    public Mono<SetApplicationCurrentDropletResponse> setCurrentDroplet(SetApplicationCurrentDropletRequest request) {
        return patch(request, SetApplicationCurrentDropletResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "relationships", "current_droplet"))
            .checkpoint();
    }

    @Override
    public Mono<StartApplicationResponse> start(StartApplicationRequest request) {
        return post(request, StartApplicationResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "actions", "start"))
            .checkpoint();
    }

    @Override
    public Mono<StopApplicationResponse> stop(StopApplicationRequest request) {
        return post(request, StopApplicationResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "actions", "stop"))
            .checkpoint();
    }

    @Override
    public Mono<Void> terminateInstance(TerminateApplicationInstanceRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "processes", request.getType(), "instances", request.getIndex()))
            .checkpoint();
    }

    @Override
    public Mono<UpdateApplicationResponse> update(UpdateApplicationRequest request) {
        return patch(request, UpdateApplicationResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId()))
            .checkpoint();
    }

    @Override
    public Mono<UpdateApplicationEnvironmentVariablesResponse> updateEnvironmentVariables(UpdateApplicationEnvironmentVariablesRequest request) {
        return patch(request, UpdateApplicationEnvironmentVariablesResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "environment_variables"))
            .checkpoint();
    }

}

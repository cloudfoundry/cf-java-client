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

package org.cloudfoundry.reactor.client.v3.applications;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.client.v3.applications.ApplicationsV3;
import org.cloudfoundry.client.v3.applications.AssignApplicationDropletRequest;
import org.cloudfoundry.client.v3.applications.AssignApplicationDropletResponse;
import org.cloudfoundry.client.v3.applications.CancelApplicationTaskRequest;
import org.cloudfoundry.client.v3.applications.CancelApplicationTaskResponse;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v3.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessDetailedStatisticsRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessDetailedStatisticsResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationTaskRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationTaskResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationDropletsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationDropletsResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationPackagesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationPackagesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationProcessesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationProcessesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationTasksRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationTasksResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v3.applications.ScaleApplicationRequest;
import org.cloudfoundry.client.v3.applications.ScaleApplicationResponse;
import org.cloudfoundry.client.v3.applications.StartApplicationRequest;
import org.cloudfoundry.client.v3.applications.StartApplicationResponse;
import org.cloudfoundry.client.v3.applications.StopApplicationRequest;
import org.cloudfoundry.client.v3.applications.StopApplicationResponse;
import org.cloudfoundry.client.v3.applications.TerminateApplicationInstanceRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationResponse;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

/**
 * The Reactor-based implementation of {@link ApplicationsV3}
 */
public final class ReactorApplicationsV3 extends AbstractClientV3Operations implements ApplicationsV3 {

    /**
     * Creates an instance
     *
     * @param authorizationProvider the {@link AuthorizationProvider} to use when communicating with the server
     * @param httpClient            the {@link HttpClient} to use when communicating with the server
     * @param objectMapper          the {@link ObjectMapper} to use when communicating with the server
     * @param root                  the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     */
    public ReactorApplicationsV3(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
    }

    @Override
    public Mono<AssignApplicationDropletResponse> assignDroplet(AssignApplicationDropletRequest request) {
        return put(request, AssignApplicationDropletResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId(), "current_droplet")));
    }

    @Override
    public Mono<CancelApplicationTaskResponse> cancelTask(CancelApplicationTaskRequest request) {
        return put(request, CancelApplicationTaskResponse.class,
            function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId(), "tasks", validRequest.getTaskId(), "cancel")));
    }

    @Override
    public Mono<CreateApplicationResponse> create(CreateApplicationRequest request) {
        return post(request, CreateApplicationResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "apps")));
    }

    @Override
    public Mono<Void> delete(DeleteApplicationRequest request) {
        return delete(request, Void.class, function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId())));
    }

    @Override
    public Mono<GetApplicationResponse> get(GetApplicationRequest request) {
        return get(request, GetApplicationResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId())));
    }

    @Override
    public Mono<GetApplicationEnvironmentResponse> getEnvironment(GetApplicationEnvironmentRequest request) {
        return get(request, GetApplicationEnvironmentResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId(), "env")));
    }

    @Override
    public Mono<GetApplicationProcessResponse> getProcess(GetApplicationProcessRequest request) {
        return get(request, GetApplicationProcessResponse.class,
            function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId(), "processes", validRequest.getType())));
    }

    @Override
    public Mono<GetApplicationProcessDetailedStatisticsResponse> getProcessDetailedStatistics(GetApplicationProcessDetailedStatisticsRequest request) {
        return get(request, GetApplicationProcessDetailedStatisticsResponse.class,
            function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId(), "processes", validRequest.getType(), "stats")));
    }

    @Override
    public Mono<GetApplicationTaskResponse> getTask(GetApplicationTaskRequest request) {
        return get(request, GetApplicationTaskResponse.class,
            function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId(), "tasks", validRequest.getTaskId())));
    }

    @Override
    public Mono<ListApplicationsResponse> list(ListApplicationsRequest request) {
        return get(request, ListApplicationsResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "apps")));
    }

    @Override
    public Mono<ListApplicationDropletsResponse> listDroplets(ListApplicationDropletsRequest request) {
        return get(request, ListApplicationDropletsResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId(), "droplets")));
    }

    @Override
    public Mono<ListApplicationPackagesResponse> listPackages(ListApplicationPackagesRequest request) {
        return get(request, ListApplicationPackagesResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId(), "packages")));
    }

    @Override
    public Mono<ListApplicationProcessesResponse> listProcesses(ListApplicationProcessesRequest request) {
        return get(request, ListApplicationProcessesResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId(), "processes")));
    }

    @Override
    public Mono<ListApplicationTasksResponse> listTasks(ListApplicationTasksRequest request) {
        return get(request, ListApplicationTasksResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId(), "tasks")));
    }

    @Override
    public Mono<ScaleApplicationResponse> scale(ScaleApplicationRequest request) {
        return put(request, ScaleApplicationResponse.class,
            function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId(), "processes", validRequest.getType(), "scale")));
    }

    @Override
    public Mono<StartApplicationResponse> start(StartApplicationRequest request) {
        return put(request, StartApplicationResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId(), "start")));
    }

    @Override
    public Mono<StopApplicationResponse> stop(StopApplicationRequest request) {
        return put(request, StopApplicationResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId(), "stop")));
    }

    @Override
    public Mono<Void> terminateInstance(TerminateApplicationInstanceRequest request) {
        return delete(request, Void.class,
            function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId(), "processes", validRequest.getType(), "instances", validRequest.getIndex())));
    }

    @Override
    public Mono<UpdateApplicationResponse> update(UpdateApplicationRequest request) {
        return patch(request, UpdateApplicationResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "apps", validRequest.getApplicationId())));
    }

}

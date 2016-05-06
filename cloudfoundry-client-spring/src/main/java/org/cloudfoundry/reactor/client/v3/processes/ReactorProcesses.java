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

package org.cloudfoundry.reactor.client.v3.processes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.client.v3.processes.GetProcessDetailedStatisticsRequest;
import org.cloudfoundry.client.v3.processes.GetProcessDetailedStatisticsResponse;
import org.cloudfoundry.client.v3.processes.GetProcessRequest;
import org.cloudfoundry.client.v3.processes.GetProcessResponse;
import org.cloudfoundry.client.v3.processes.ListProcessesRequest;
import org.cloudfoundry.client.v3.processes.ListProcessesResponse;
import org.cloudfoundry.client.v3.processes.Processes;
import org.cloudfoundry.client.v3.processes.ScaleProcessRequest;
import org.cloudfoundry.client.v3.processes.ScaleProcessResponse;
import org.cloudfoundry.client.v3.processes.TerminateProcessInstanceRequest;
import org.cloudfoundry.client.v3.processes.UpdateProcessRequest;
import org.cloudfoundry.client.v3.processes.UpdateProcessResponse;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

/**
 * The Reactor-based implementation of {@link Processes}
 */
public final class ReactorProcesses extends AbstractClientV3Operations implements Processes {

    /**
     * Creates an instance
     *
     * @param authorizationProvider the {@link AuthorizationProvider} to use when communicating with the server
     * @param httpClient            the {@link HttpClient} to use when communicating with the server
     * @param objectMapper          the {@link ObjectMapper} to use when communicating with the server
     * @param root                  the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     */
    public ReactorProcesses(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
    }

    @Override
    public Mono<GetProcessResponse> get(GetProcessRequest request) {
        return get(request, GetProcessResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "processes", validRequest.getProcessId())));
    }

    @Override
    public Mono<GetProcessDetailedStatisticsResponse> getDetailedStatistics(GetProcessDetailedStatisticsRequest request) {
        return get(request, GetProcessDetailedStatisticsResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "processes", validRequest.getProcessId(), "stats")));
    }

    @Override
    public Mono<ListProcessesResponse> list(ListProcessesRequest request) {
        return get(request, ListProcessesResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "processes")));
    }

    @Override
    public Mono<ScaleProcessResponse> scale(ScaleProcessRequest request) {
        return put(request, ScaleProcessResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "processes", validRequest.getProcessId(), "scale")));
    }

    @Override
    public Mono<Void> terminateInstance(TerminateProcessInstanceRequest request) {
        return delete(request, Void.class, function((builder, validRequest) -> builder.pathSegment("v3", "processes", validRequest.getProcessId(), "instances", validRequest.getIndex())));
    }

    @Override
    public Mono<UpdateProcessResponse> update(UpdateProcessRequest request) {
        return patch(request, UpdateProcessResponse.class, function((builder, validRequest) -> builder.pathSegment("v3", "processes", validRequest.getProcessId())));
    }

}

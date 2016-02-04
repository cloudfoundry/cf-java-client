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

package org.cloudfoundry.client.spring.v3.processes;

import lombok.ToString;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.QueryBuilder;
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
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SchedulerGroup;
import reactor.fn.Consumer;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Processes}
 */
@ToString(callSuper = true)
public final class SpringProcesses extends AbstractSpringOperations implements Processes {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringProcesses(RestOperations restOperations, URI root, SchedulerGroup schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<GetProcessResponse> get(final GetProcessRequest request) {
        return get(request, GetProcessResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "processes", request.getProcessId());
            }

        });
    }

    @Override
    public Mono<ListProcessesResponse> list(final ListProcessesRequest request) {
        return get(request, ListProcessesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "processes");
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<ScaleProcessResponse> scale(final ScaleProcessRequest request) {
        return put(request, ScaleProcessResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "processes", request.getProcessId(), "scale");
            }

        });
    }

    @Override
    public Mono<Void> terminateInstance(final TerminateProcessInstanceRequest request) {
        return delete(request, Void.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "processes", request.getProcessId(), "instances", request.getIndex());
            }

        });
    }

    @Override
    public Mono<UpdateProcessResponse> update(final UpdateProcessRequest request) {
        return patch(request, UpdateProcessResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v3", "processes", request.getProcessId());
            }

        });
    }

}

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

package org.cloudfoundry.client.spring.v3.processes;

import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.v3.processes.DeleteInstanceRequest;
import org.cloudfoundry.client.v3.processes.GetProcessRequest;
import org.cloudfoundry.client.v3.processes.GetProcessResponse;
import org.cloudfoundry.client.v3.processes.ListProcessesRequest;
import org.cloudfoundry.client.v3.processes.ListProcessesResponse;
import org.cloudfoundry.client.v3.processes.Processes;
import org.cloudfoundry.client.v3.processes.ScaleProcessRequest;
import org.cloudfoundry.client.v3.processes.ScaleProcessResponse;
import org.reactivestreams.Publisher;
import org.springframework.web.client.RestOperations;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Processes}
 */
public final class SpringProcesses extends AbstractSpringOperations implements Processes {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     */
    public SpringProcesses(RestOperations restOperations, URI root) {
        super(restOperations, root);
    }

    @Override
    public Publisher<Void> deleteInstance(DeleteInstanceRequest request) {
        return delete(request, builder -> builder.pathSegment("v3", "processes", request.getId(),
                "instances", request.getIndex()));
    }

    @Override
    public Publisher<GetProcessResponse> get(GetProcessRequest request) {
        return get(request, GetProcessResponse.class,
                builder -> builder.pathSegment("v3", "processes", request.getId()));
    }

    @Override
    public Publisher<ListProcessesResponse> list(ListProcessesRequest request) {
        return get(request, ListProcessesResponse.class, builder -> builder.pathSegment("v3", "processes"));
    }

    public Publisher<ScaleProcessResponse> scale(ScaleProcessRequest request) {
        return put(request, ScaleProcessResponse.class,
                builder -> builder.pathSegment("v3", "processes", request.getId(), "scale"));
    }

}

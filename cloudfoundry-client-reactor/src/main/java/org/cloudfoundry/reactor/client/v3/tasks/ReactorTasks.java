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

package org.cloudfoundry.reactor.client.v3.tasks;

import org.cloudfoundry.client.v3.tasks.CancelTaskRequest;
import org.cloudfoundry.client.v3.tasks.CancelTaskResponse;
import org.cloudfoundry.client.v3.tasks.CreateTaskRequest;
import org.cloudfoundry.client.v3.tasks.CreateTaskResponse;
import org.cloudfoundry.client.v3.tasks.GetTaskRequest;
import org.cloudfoundry.client.v3.tasks.GetTaskResponse;
import org.cloudfoundry.client.v3.tasks.ListTasksRequest;
import org.cloudfoundry.client.v3.tasks.ListTasksResponse;
import org.cloudfoundry.client.v3.tasks.Tasks;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The Reactor-based implementation of {@link Tasks}
 */
public final class ReactorTasks extends AbstractClientV3Operations implements Tasks {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorTasks(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<CancelTaskResponse> cancel(CancelTaskRequest request) {  // TODO: Modify once support has aged out https://v3-apidocs.cloudfoundry.org/version/3.27.0/#cancel-a-task
        return put(request, CancelTaskResponse.class, builder -> builder.pathSegment("tasks", request.getTaskId(), "cancel"))
            .checkpoint();
    }

    @Override
    public Mono<CreateTaskResponse> create(CreateTaskRequest request) {
        return post(request, CreateTaskResponse.class, builder -> builder.pathSegment("apps", request.getApplicationId(), "tasks"))
            .checkpoint();
    }

    @Override
    public Mono<GetTaskResponse> get(GetTaskRequest request) {
        return get(request, GetTaskResponse.class, builder -> builder.pathSegment("tasks", request.getTaskId()))
            .checkpoint();
    }

    @Override
    public Mono<ListTasksResponse> list(ListTasksRequest request) {
        return get(request, ListTasksResponse.class, builder -> builder.pathSegment("tasks"))
            .checkpoint();
    }

}

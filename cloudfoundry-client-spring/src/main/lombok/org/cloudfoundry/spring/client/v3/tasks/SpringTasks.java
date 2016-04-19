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

package org.cloudfoundry.spring.client.v3.tasks;

import lombok.ToString;
import org.cloudfoundry.client.v3.tasks.CancelTaskRequest;
import org.cloudfoundry.client.v3.tasks.CancelTaskResponse;
import org.cloudfoundry.client.v3.tasks.CreateTaskRequest;
import org.cloudfoundry.client.v3.tasks.CreateTaskResponse;
import org.cloudfoundry.client.v3.tasks.GetTaskRequest;
import org.cloudfoundry.client.v3.tasks.GetTaskResponse;
import org.cloudfoundry.client.v3.tasks.ListTasksRequest;
import org.cloudfoundry.client.v3.tasks.ListTasksResponse;
import org.cloudfoundry.client.v3.tasks.Tasks;
import org.cloudfoundry.spring.util.AbstractSpringOperations;
import org.cloudfoundry.spring.util.QueryBuilder;
import org.springframework.web.client.RestOperations;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Tasks}
 */
@ToString(callSuper = true)
public final class SpringTasks extends AbstractSpringOperations implements Tasks {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param schedulerGroup The group to use when making requests
     */
    public SpringTasks(RestOperations restOperations, URI root, Scheduler schedulerGroup) {
        super(restOperations, root, schedulerGroup);
    }

    @Override
    public Mono<CancelTaskResponse> cancel(CancelTaskRequest request) {
        return put(request, CancelTaskResponse.class, builder -> builder.pathSegment("v3", "tasks", request.getTaskId(), "cancel"));
    }

    @Override
    public Mono<CreateTaskResponse> create(CreateTaskRequest request) {
        return post(request, CreateTaskResponse.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId(), "tasks"));
    }

    @Override
    public Mono<GetTaskResponse> get(GetTaskRequest request) {
        return get(request, GetTaskResponse.class, builder -> builder.pathSegment("v3", "tasks", request.getTaskId()));
    }

    @Override
    public Mono<ListTasksResponse> list(ListTasksRequest request) {
        return get(request, ListTasksResponse.class, builder -> {
            builder.pathSegment("v3", "tasks");
            QueryBuilder.augment(builder, request);
        });
    }

}

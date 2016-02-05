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

package org.cloudfoundry.client.spring.v3.tasks;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.tasks.CreateTaskRequest;
import org.cloudfoundry.client.v3.tasks.CreateTaskResponse;
import org.cloudfoundry.client.v3.tasks.GetTaskRequest;
import org.cloudfoundry.client.v3.tasks.GetTaskResponse;
import org.cloudfoundry.client.v3.tasks.Task;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

public final class SpringTasksTest {

    public static final class Create extends AbstractApiTest<CreateTaskRequest, CreateTaskResponse> {

        private final SpringTasks tasks = new SpringTasks(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreateTaskRequest getInvalidRequest() {
            return CreateTaskRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(POST).path("/v3/apps/test-application-id/tasks")
                .requestPayload("v3/tasks/POST_apps_{id}_tasks_request.json")
                .status(OK)
                .responsePayload("v3/tasks/POST_apps_{id}_tasks_response.json");
        }

        @Override
        protected CreateTaskResponse getResponse() {
            return CreateTaskResponse.builder()
                .id("d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                .name("migrate")
                .command("rake db:migrate")
                .state(Task.RUNNING_STATE)
                .memoryInMb(256)
                .results(Collections.singletonMap("failure_reason", null))
                .link("self", Link.builder()
                    .href("/v3/tasks/d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                    .build())
                .link("droplet", Link.builder()
                    .href("/v3/droplets/740ebd2b-162b-469a-bd72-3edb96fabd9a")
                    .build())
                .build();
        }

        @Override
        protected CreateTaskRequest getValidRequest() {
            return CreateTaskRequest.builder()
                .applicationId("test-application-id")
                .command("echo 'hello world'")
                .memoryInMb(512)
                .name("my-task")
                .build();
        }

        @Override
        protected Mono<CreateTaskResponse> invoke(CreateTaskRequest request) {
            return this.tasks.create(request);
        }

    }

    public static final class Get extends AbstractApiTest<GetTaskRequest, GetTaskResponse> {

        private final SpringTasks tasks = new SpringTasks(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetTaskRequest getInvalidRequest() {
            return GetTaskRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v3/tasks/test-id")
                .status(OK)
                .responsePayload("v3/tasks/GET_{id}_response.json");
        }

        @Override
        protected GetTaskResponse getResponse() {
            return GetTaskResponse.builder()
                .id("d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                .name("migrate")
                .command("rake db:migrate")
                .state(Task.RUNNING_STATE)
                .memoryInMb(256)
                .results(Collections.singletonMap("failure_reason", null))
                .link("self", Link.builder()
                    .href("/v3/tasks/d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                    .build())
                .link("droplet", Link.builder()
                    .href("/v3/droplets/740ebd2b-162b-469a-bd72-3edb96fabd9a")
                    .build())
                .build();
        }

        @Override
        protected GetTaskRequest getValidRequest() {
            return GetTaskRequest.builder()
                .id("test-id")
                .build();
        }

        @Override
        protected Mono<GetTaskResponse> invoke(GetTaskRequest request) {
            return this.tasks.get(request);
        }

    }

}

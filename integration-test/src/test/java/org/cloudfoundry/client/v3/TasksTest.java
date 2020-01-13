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

package org.cloudfoundry.client.v3;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.applications.ApplicationResource;
import org.cloudfoundry.client.v3.applications.GetApplicationCurrentDropletRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationCurrentDropletResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.tasks.CancelTaskRequest;
import org.cloudfoundry.client.v3.tasks.CancelTaskResponse;
import org.cloudfoundry.client.v3.tasks.CreateTaskRequest;
import org.cloudfoundry.client.v3.tasks.CreateTaskResponse;
import org.cloudfoundry.client.v3.tasks.GetTaskRequest;
import org.cloudfoundry.client.v3.tasks.GetTaskResponse;
import org.cloudfoundry.client.v3.tasks.ListTasksRequest;
import org.cloudfoundry.client.v3.tasks.TaskResource;
import org.cloudfoundry.client.v3.tasks.TaskState;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationHealthCheck;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;

import static org.cloudfoundry.client.v3.tasks.TaskState.RUNNING;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_1_12)
public final class TasksTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Test
    public void cancel() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, applicationName)
            .then(getApplicationId(this.cloudFoundryClient, applicationName))
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                getDropletId(this.cloudFoundryClient, applicationId)))
            .flatMap(function((applicationId, dropletId) -> createTaskId(this.cloudFoundryClient, applicationId)))
            .flatMap(taskId -> this.cloudFoundryClient.tasks()
                .cancel(CancelTaskRequest.builder()
                    .taskId(taskId)
                    .build())
                .map(CancelTaskResponse::getState))
            .as(StepVerifier::create)
            .expectNext(TaskState.CANCELING)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, applicationName)
            .then(getApplicationId(this.cloudFoundryClient, applicationName))
            .delayUntil(applicationId -> getDropletId(this.cloudFoundryClient, applicationId))
            .flatMap(applicationId -> this.cloudFoundryClient.tasks()
                .create(CreateTaskRequest.builder()
                    .applicationId(applicationId)
                    .command("ls")
                    .diskInMb(129)
                    .memoryInMb(129)
                    .build()))
            .thenMany(requestListTasks(this.cloudFoundryClient)
                .filter(task -> 129 == task.getMemoryInMb())
                .map(TaskResource::getDiskInMb))
            .as(StepVerifier::create)
            .expectNext(129)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, applicationName)
            .then(getApplicationId(this.cloudFoundryClient, applicationName))
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                getDropletId(this.cloudFoundryClient, applicationId)))
            .flatMap(function((applicationId, dropletId) -> createTaskId(this.cloudFoundryClient, applicationId)))
            .flatMap(taskId -> this.cloudFoundryClient.tasks()
                .get(GetTaskRequest.builder()
                    .taskId(taskId)
                    .build())
                .map(GetTaskResponse::getCommand))
            .as(StepVerifier::create)
            .expectNext("ls")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, applicationName)
            .then(getApplicationId(this.cloudFoundryClient, applicationName))
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                getDropletId(this.cloudFoundryClient, applicationId)))
            .flatMap(function((applicationId, dropletId) -> requestCreateTask(this.cloudFoundryClient, applicationId)
                .thenReturn(dropletId)))
            .flatMapMany(dropletId -> PaginationUtils.
                requestClientV3Resources(page -> this.cloudFoundryClient.tasks()
                    .list(ListTasksRequest.builder()
                        .page(page)
                        .build()))
                .filter(task -> dropletId.equals(task.getDropletId())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByApplication() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, applicationName)
            .then(getApplicationId(this.cloudFoundryClient, applicationName))
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                getDropletId(this.cloudFoundryClient, applicationId)))
            .delayUntil(function((applicationId, dropletId) -> requestCreateTask(this.cloudFoundryClient, applicationId)))
            .flatMapMany(function((applicationId, dropletId) -> PaginationUtils.
                requestClientV3Resources(page -> this.cloudFoundryClient.tasks()
                    .list(ListTasksRequest.builder()
                        .applicationId(applicationId)
                        .page(page)
                        .build()))
                .filter(task -> dropletId.equals(task.getDropletId()))))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByName() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, applicationName)
            .then(getApplicationId(this.cloudFoundryClient, applicationName))
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                getDropletId(this.cloudFoundryClient, applicationId)))
            .flatMap(function((applicationId, dropletId) -> createTaskId(this.cloudFoundryClient, applicationId)))
            .flatMap(taskId -> getTaskName(this.cloudFoundryClient, taskId))
            .flatMapMany(taskName -> PaginationUtils.
                requestClientV3Resources(page -> this.cloudFoundryClient.tasks()
                    .list(ListTasksRequest.builder()
                        .name(taskName)
                        .page(page)
                        .build())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByState() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, applicationName)
            .then(getApplicationId(this.cloudFoundryClient, applicationName))
            .flatMap(applicationId -> Mono.zip(
                Mono.just(applicationId),
                getDropletId(this.cloudFoundryClient, applicationId)))
            .flatMap(function((applicationId, dropletId) -> requestCreateTask(this.cloudFoundryClient, applicationId)
                .thenReturn(dropletId)))
            .flatMapMany(dropletId -> PaginationUtils.
                requestClientV3Resources(page -> this.cloudFoundryClient.tasks()
                    .list(ListTasksRequest.builder()
                        .state(RUNNING)
                        .page(page)
                        .build()))
                .filter(task -> dropletId.equals(task.getDropletId())))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<Void> createApplication(CloudFoundryOperations cloudFoundryOperations, String name) throws IOException {
        return cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .path(new ClassPathResource("test-application.zip").getFile().toPath())
                .buildpack("staticfile_buildpack")
                .diskQuota(256)
                .healthCheckType(ApplicationHealthCheck.PORT)
                .memory(64)
                .name(name)
                .noStart(false)
                .build());
    }

    private static Mono<String> createTaskId(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestCreateTask(cloudFoundryClient, applicationId)
            .map(CreateTaskResponse::getId);
    }

    private static Mono<String> getApplicationId(CloudFoundryClient cloudFoundryClient, String applicationName) {
        return PaginationUtils.requestClientV3Resources(page -> cloudFoundryClient.applicationsV3()
            .list(ListApplicationsRequest.builder()
                .name(applicationName)
                .build()))
            .single()
            .map(ApplicationResource::getId);
    }

    private static Mono<String> getDropletId(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV3()
            .getCurrentDroplet(GetApplicationCurrentDropletRequest.builder()
                .applicationId(applicationId)
                .build())
            .map(GetApplicationCurrentDropletResponse::getId);
    }

    private static Mono<String> getTaskName(CloudFoundryClient cloudFoundryClient, String taskId) {
        return cloudFoundryClient.tasks()
            .get(GetTaskRequest.builder()
                .taskId(taskId)
                .build())
            .map(GetTaskResponse::getName);
    }

    private static Mono<CreateTaskResponse> requestCreateTask(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.tasks()
            .create(CreateTaskRequest.builder()
                .applicationId(applicationId)
                .command("ls")
                .diskInMb(64)
                .memoryInMb(67)
                .build());
    }

    private static Flux<TaskResource> requestListTasks(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.
            requestClientV3Resources(page -> cloudFoundryClient.tasks()
                .list(ListTasksRequest.builder()
                    .page(page)
                    .build()));
    }

}

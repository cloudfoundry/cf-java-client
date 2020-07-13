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
import org.cloudfoundry.client.v3.applications.GetApplicationProcessRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessResponse;
import org.cloudfoundry.client.v3.processes.GetProcessRequest;
import org.cloudfoundry.client.v3.processes.GetProcessResponse;
import org.cloudfoundry.client.v3.processes.GetProcessStatisticsRequest;
import org.cloudfoundry.client.v3.processes.GetProcessStatisticsResponse;
import org.cloudfoundry.client.v3.processes.HealthCheck;
import org.cloudfoundry.client.v3.processes.HealthCheckType;
import org.cloudfoundry.client.v3.processes.ListProcessesRequest;
import org.cloudfoundry.client.v3.processes.Process;
import org.cloudfoundry.client.v3.processes.ProcessResource;
import org.cloudfoundry.client.v3.processes.ProcessStatisticsResource;
import org.cloudfoundry.client.v3.processes.ScaleProcessRequest;
import org.cloudfoundry.client.v3.processes.TerminateProcessInstanceRequest;
import org.cloudfoundry.client.v3.processes.UpdateProcessRequest;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.cloudfoundry.operations.applications.ApplicationHealthCheck;
import org.cloudfoundry.operations.applications.GetApplicationRequest;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_0)
public final class ProcessesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Test
    public void get() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        Path path =  new ClassPathResource("test-application.zip").getFile().toPath();

        createApplication(this.cloudFoundryOperations, applicationName, path)
            .then(getApplicationId(this.cloudFoundryOperations, applicationName))
            .flatMap(applicationId -> getProcessId(this.cloudFoundryClient, applicationId))
            .flatMap(processId -> this.cloudFoundryClient.processes()
                .get(GetProcessRequest.builder()
                    .processId(processId)
                    .build()))
            .map(GetProcessResponse::getDiskInMb)
            .as(StepVerifier::create)
            .expectNext(258)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getStatistics() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        Path path =  new ClassPathResource("test-application.zip").getFile().toPath();

        createApplication(this.cloudFoundryOperations, applicationName, path)
            .then(getApplicationId(this.cloudFoundryOperations, applicationName))
            .flatMap(applicationId -> getProcessId(this.cloudFoundryClient, applicationId))
            .flatMap(processId -> this.cloudFoundryClient.processes()
                .getStatistics(GetProcessStatisticsRequest.builder()
                    .processId(processId)
                    .build()))
            .flatMapIterable(GetProcessStatisticsResponse::getResources)
            .map(ProcessStatisticsResource::getType)
            .as(StepVerifier::create)
            .expectNext("web")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        Path path =  new ClassPathResource("test-application.zip").getFile().toPath();

        createApplication(this.cloudFoundryOperations, applicationName, path)
            .then(getApplicationId(this.cloudFoundryOperations, applicationName))
            .flatMap(applicationId -> getProcessId(this.cloudFoundryClient, applicationId))
            .flatMapMany(processId -> PaginationUtils.requestClientV3Resources(page -> this.cloudFoundryClient.processes()
                .list(ListProcessesRequest.builder()
                    .page(page)
                    .processId(processId)
                    .build())))
            .map(ProcessResource::getDiskInMb)
            .as(StepVerifier::create)
            .expectNext(258)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void scale() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        Path path =  new ClassPathResource("test-application.zip").getFile().toPath();

        createApplication(this.cloudFoundryOperations, applicationName, path)
            .then(getApplicationId(this.cloudFoundryOperations, applicationName))
            .flatMap(applicationId -> getProcessId(this.cloudFoundryClient, applicationId))
            .flatMap(processId -> this.cloudFoundryClient.processes()
                .scale(ScaleProcessRequest.builder()
                    .diskInMb(259)
                    .processId(processId)
                    .build())
                .then(Mono.just(processId)))
            .flatMap(processId -> requestGetProcess(this.cloudFoundryClient, processId))
            .map(GetProcessResponse::getDiskInMb)
            .as(StepVerifier::create)
            .expectNext(259)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void terminateInstance() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        Path path =  new ClassPathResource("test-application.zip").getFile().toPath();

        createApplication(this.cloudFoundryOperations, applicationName, path)
            .then(getApplicationId(this.cloudFoundryOperations, applicationName))
            .flatMap(applicationId -> getProcessId(this.cloudFoundryClient, applicationId))
            .flatMap(processId -> this.cloudFoundryClient.processes()
                .terminateInstance(TerminateProcessInstanceRequest.builder()
                    .index("1")
                    .processId(processId)
                    .build())
                .then(Mono.just(processId)))
            .flatMap(processId -> requestGetProcess(this.cloudFoundryClient, processId))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV3Exception.class).hasMessageMatching("CF-ResourceNotFound\\([0-9]+\\): Instance not found"))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        Path path =  new ClassPathResource("test-application.zip").getFile().toPath();

        createApplication(this.cloudFoundryOperations, applicationName, path)
            .then(getApplicationId(this.cloudFoundryOperations, applicationName))
            .flatMap(applicationId -> getProcessId(this.cloudFoundryClient, applicationId))
            .flatMap(processId -> this.cloudFoundryClient.processes()
                .update(UpdateProcessRequest.builder()
                    .healthCheck(HealthCheck.builder()
                        .type(HealthCheckType.PROCESS)
                        .build())
                    .processId(processId)
                    .build())
                .then(Mono.just(processId)))
            .flatMap(processId -> requestGetProcess(this.cloudFoundryClient, processId))
            .map(GetProcessResponse::getHealthCheck)
            .map(HealthCheck::getType)
            .as(StepVerifier::create)
            .expectNext(HealthCheckType.PROCESS)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<Void> createApplication(CloudFoundryOperations cloudFoundryOperations, String name, Path path) throws IOException {
        return cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .buildpack("staticfile_buildpack")
                .diskQuota(258)
                .healthCheckType(ApplicationHealthCheck.PORT)
                .memory(64)
                .name(name)
                .path(path)
                .noStart(false)
                .build());
    }

    private static Mono<String> getApplicationId(CloudFoundryOperations cloudFoundryOperations, String applicationName) {
        return requestGetApplication(cloudFoundryOperations, applicationName)
            .map(ApplicationDetail::getId);
    }

    private static Mono<String> getProcessId(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestGetApplicationProcess(cloudFoundryClient, applicationId)
            .map(Process::getId);
    }

    private static Mono<ApplicationDetail> requestGetApplication(CloudFoundryOperations cloudFoundryOperations, String applicationName) {
        return cloudFoundryOperations.applications()
            .get(GetApplicationRequest.builder()
                .name(applicationName)
                .build());
    }

    private static Mono<GetApplicationProcessResponse> requestGetApplicationProcess(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV3()
            .getProcess(GetApplicationProcessRequest.builder()
                .applicationId(applicationId)
                .type("web")
                .build());
    }

    private static Mono<GetProcessResponse> requestGetProcess(CloudFoundryClient cloudFoundryClient, String processId) {
        return cloudFoundryClient.processes()
            .get(GetProcessRequest.builder()
                .processId(processId)
                .build());
    }

}

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

package org.cloudfoundry.operations;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.doppler.LogMessage;
import org.cloudfoundry.doppler.MessageType;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.cloudfoundry.operations.applications.ApplicationEnvironments;
import org.cloudfoundry.operations.applications.ApplicationEvent;
import org.cloudfoundry.operations.applications.ApplicationHealthCheck;
import org.cloudfoundry.operations.applications.ApplicationManifest;
import org.cloudfoundry.operations.applications.ApplicationSshEnabledRequest;
import org.cloudfoundry.operations.applications.ApplicationSummary;
import org.cloudfoundry.operations.applications.CopySourceApplicationRequest;
import org.cloudfoundry.operations.applications.DeleteApplicationRequest;
import org.cloudfoundry.operations.applications.DisableApplicationSshRequest;
import org.cloudfoundry.operations.applications.EnableApplicationSshRequest;
import org.cloudfoundry.operations.applications.GetApplicationEnvironmentsRequest;
import org.cloudfoundry.operations.applications.GetApplicationEventsRequest;
import org.cloudfoundry.operations.applications.GetApplicationHealthCheckRequest;
import org.cloudfoundry.operations.applications.GetApplicationManifestRequest;
import org.cloudfoundry.operations.applications.GetApplicationRequest;
import org.cloudfoundry.operations.applications.ListApplicationTasksRequest;
import org.cloudfoundry.operations.applications.LogsRequest;
import org.cloudfoundry.operations.applications.PushApplicationManifestRequest;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.operations.applications.RenameApplicationRequest;
import org.cloudfoundry.operations.applications.RestageApplicationRequest;
import org.cloudfoundry.operations.applications.RestartApplicationInstanceRequest;
import org.cloudfoundry.operations.applications.RestartApplicationRequest;
import org.cloudfoundry.operations.applications.Route;
import org.cloudfoundry.operations.applications.RunApplicationTaskRequest;
import org.cloudfoundry.operations.applications.ScaleApplicationRequest;
import org.cloudfoundry.operations.applications.SetApplicationHealthCheckRequest;
import org.cloudfoundry.operations.applications.SetEnvironmentVariableApplicationRequest;
import org.cloudfoundry.operations.applications.StartApplicationRequest;
import org.cloudfoundry.operations.applications.StopApplicationRequest;
import org.cloudfoundry.operations.applications.Task;
import org.cloudfoundry.operations.applications.TaskState;
import org.cloudfoundry.operations.applications.TerminateApplicationTaskRequest;
import org.cloudfoundry.operations.applications.UnsetEnvironmentVariableApplicationRequest;
import org.cloudfoundry.operations.domains.CreateDomainRequest;
import org.cloudfoundry.operations.domains.CreateSharedDomainRequest;
import org.cloudfoundry.operations.routes.ListRoutesRequest;
import org.cloudfoundry.operations.services.BindServiceInstanceRequest;
import org.cloudfoundry.operations.services.CreateServiceInstanceRequest;
import org.cloudfoundry.operations.services.CreateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.operations.services.GetServiceInstanceRequest;
import org.cloudfoundry.operations.services.ServiceInstance;
import org.cloudfoundry.util.FluentMap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public final class ApplicationsTest extends AbstractIntegrationTest {

    private static final String DEFAULT_ROUTER_GROUP = "default-tcp";

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Autowired
    private String organizationName;

    @Autowired
    private String planName;

    @Autowired
    private String serviceName;

    @Test
    public void copySource() throws IOException {
        String sourceName = this.nameFactory.getApplicationName();
        String targetName = this.nameFactory.getApplicationName();

        Mono.when(
            createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), sourceName, false),
            createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), targetName, true)
        )
            .then(this.cloudFoundryOperations.applications()
                .copySource(CopySourceApplicationRequest.builder()
                    .name(sourceName)
                    .restart(true)
                    .targetName(targetName)
                    .build()))
            .then(requestGetApplication(this.cloudFoundryOperations, targetName))
            .map(ApplicationDetail::getRequestedState)
            .as(StepVerifier::create)
            .expectNext("STARTED")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteApplication() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .delete(DeleteApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .thenMany(requestListApplications(this.cloudFoundryOperations))
            .filter(response -> applicationName.equals(response.getName()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteApplicationAndRoutes() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .delete(DeleteApplicationRequest.builder()
                    .name(applicationName)
                    .deleteRoutes(true)
                    .build()))
            .thenMany(requestListRoutes(this.cloudFoundryOperations))
            .map(org.cloudfoundry.operations.routes.Route::getApplications)
            .filter(applicationName::equals)
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteApplicationWithServiceBindings() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(bindServiceToApplication(this.cloudFoundryOperations, applicationName, serviceInstanceName))
            .then(this.cloudFoundryOperations.applications()
                .delete(DeleteApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void disableSsh() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .disableSsh(DisableApplicationSshRequest.builder()
                    .name(applicationName)
                    .build()))
            .then(requestSshEnabled(this.cloudFoundryOperations, applicationName))
            .as(StepVerifier::create)
            .expectNext(false)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void enableSsh() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .enableSsh(EnableApplicationSshRequest.builder()
                    .name(applicationName)
                    .build()))
            .then(requestSshEnabled(this.cloudFoundryOperations, applicationName))
            .as(StepVerifier::create)
            .expectNext(true)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .get(GetApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .map(ApplicationDetail::getName)
            .as(StepVerifier::create)
            .expectNext(applicationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getEvents() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, true)
            .thenMany(this.cloudFoundryOperations.applications()
                .getEvents(GetApplicationEventsRequest.builder()
                    .name(applicationName)
                    .build())
                .next())
            .map(ApplicationEvent::getEvent)
            .as(StepVerifier::create)
            .expectNext("audit.app.update")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getHealthCheck() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, true)
            .then(this.cloudFoundryOperations.applications()
                .getHealthCheck(GetApplicationHealthCheckRequest.builder()
                    .name(applicationName)
                    .build()))
            .as(StepVerifier::create)
            .expectNext(ApplicationHealthCheck.NONE)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getManifest() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, true)
            .then(this.cloudFoundryOperations.applications()
                .getApplicationManifest(GetApplicationManifestRequest.builder()
                    .name(applicationName)
                    .build()))
            .map(ApplicationManifest::getName)
            .as(StepVerifier::create)
            .expectNext(applicationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getManifestForTcpRoute() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, true)
            .then(this.cloudFoundryOperations.applications()
                .getApplicationManifest(GetApplicationManifestRequest.builder()
                    .name(applicationName)
                    .build()))
            .map(ApplicationManifest::getName)
            .as(StepVerifier::create)
            .expectNext(applicationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getMultipleBuildpacks() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplicationPhp(this.cloudFoundryOperations, new ClassPathResource("test-php.zip").getFile().toPath(), applicationName, true)
            .then(this.cloudFoundryOperations.applications()
                .get(GetApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .map(ApplicationDetail::getBuildpacks)
            .as(StepVerifier::create)
            .expectNext(Arrays.asList("staticfile_buildpack", "php_buildpack"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getMultipleBuildpacksManifest() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplicationPhp(this.cloudFoundryOperations, new ClassPathResource("test-php.zip").getFile().toPath(), applicationName, true)
            .then(this.cloudFoundryOperations.applications()
                .getApplicationManifest(GetApplicationManifestRequest.builder()
                    .name(applicationName)
                    .build()))
            .map(ApplicationManifest::getBuildpacks)
            .as(StepVerifier::create)
            .expectNext(Arrays.asList("staticfile_buildpack", "php_buildpack"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getStopped() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, true)
            .then(this.cloudFoundryOperations.applications()
                .get(GetApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .map(ApplicationDetail::getName)
            .as(StepVerifier::create)
            .expectNext(applicationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getTcp() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        requestCreateTcpDomain(this.cloudFoundryOperations, domainName, DEFAULT_ROUTER_GROUP)
            .then(createApplicationTcp(this.cloudFoundryOperations, applicationName, domainName))
            .thenMany(this.cloudFoundryOperations.applications()
                .get(GetApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .map(applicationDetail -> applicationDetail.getUrls().get(0))
            .as(StepVerifier::create)
            .consumeNextWith(route -> assertThat(route).matches(domainName + "+?:\\d+$"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .thenMany(this.cloudFoundryOperations.applications()
                .list())
            .filter(response -> applicationName.equals(response.getName()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_1_11)
    @Test
    public void listTasks() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String taskName = this.nameFactory.getTaskName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(requestCreateTask(this.cloudFoundryOperations, applicationName, taskName))
            .thenMany(this.cloudFoundryOperations.applications()
                .listTasks(ListApplicationTasksRequest.builder()
                    .name(applicationName)
                    .build()))
            .map(Task::getName)
            .as(StepVerifier::create)
            .expectNext(taskName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void logs() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .thenMany(this.cloudFoundryOperations.applications()
                .logs(LogsRequest.builder()
                    .name(applicationName)
                    .recent(true)
                    .build()))
            .map(LogMessage::getMessageType)
            .next()
            .as(StepVerifier::create)
            .expectNext(MessageType.OUT)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushBindServices() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        createServiceInstance(this.cloudFoundryOperations, this.planName, serviceInstanceName, this.serviceName)
            .then(this.cloudFoundryOperations.applications()
                .pushManifest(PushApplicationManifestRequest.builder()
                    .manifest(ApplicationManifest.builder()
                        .path(new ClassPathResource("test-application.zip").getFile().toPath())
                        .buildpack("staticfile_buildpack")
                        .disk(512)
                        .healthCheckType(ApplicationHealthCheck.PORT)
                        .memory(64)
                        .name(applicationName)
                        .service(serviceInstanceName)
                        .build())
                    .noStart(false)
                    .build()))
            .then(getServiceInstance(this.cloudFoundryOperations, serviceInstanceName)
                .flatMapIterable(ServiceInstance::getApplications)
                .filter(applicationName::equals)
                .single())
            .as(StepVerifier::create)
            .expectNext(applicationName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushDirectory() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application").getFile().toPath(), applicationName, false)
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushDomainHostPathRoute() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String routePath = this.nameFactory.getPath();

        createDomain(this.cloudFoundryOperations, domainName, this.organizationName)
            .then(this.cloudFoundryOperations.applications()
                .push(PushApplicationRequest.builder()
                    .path(new ClassPathResource("test-application.zip").getFile().toPath())
                    .buildpack("staticfile_buildpack")
                    .diskQuota(512)
                    .domain(domainName)
                    .healthCheckType(ApplicationHealthCheck.PORT)
                    .host("test-host")
                    .memory(64)
                    .name(applicationName)
                    .noStart(false)
                    .routePath(routePath)
                    .build()))
            .thenMany(this.cloudFoundryOperations.routes()
                .list(ListRoutesRequest.builder()
                    .build()))
            .filter(response -> domainName.equals(response.getDomain()))
            .map(org.cloudfoundry.operations.routes.Route::getPath)
            .as(StepVerifier::create)
            .expectNext(routePath)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushDomainNotFound() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        this.cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .path(new ClassPathResource("test-application.zip").getFile().toPath())
                .buildpack("staticfile_buildpack")
                .domain(domainName)
                .diskQuota(512)
                .memory(64)
                .name(applicationName)
                .build())
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(IllegalArgumentException.class).hasMessage("Domain %s not found", domainName))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushExisting() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .push(PushApplicationRequest.builder()
                    .path(new ClassPathResource("test-application.zip").getFile().toPath())
                    .buildpack("staticfile_buildpack")
                    .diskQuota(512)
                    .memory(64)
                    .name(applicationName)
                    .build()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushManifestMultipleBuildpacks() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        List<String> buildpacks = Arrays.asList("staticfile_buildpack", "php_buildpack");

        this.cloudFoundryOperations.applications()
            .pushManifest(PushApplicationManifestRequest.builder()
                .manifest(ApplicationManifest.builder()
                    .buildpacks(buildpacks)
                    .disk(512)
                    .healthCheckType(ApplicationHealthCheck.PORT)
                    .memory(64)
                    .name(applicationName)
                    .path(new ClassPathResource("test-php.zip").getFile().toPath())
                    .build())
                .noStart(true)
                .build())
            .then(requestGetManifest(this.cloudFoundryOperations, applicationName))
            .map(ApplicationManifest::getBuildpacks)
            .as(StepVerifier::create)
            .expectNext(buildpacks)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushMultipleBuildpacks() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        List<String> buildpacks = Arrays.asList("staticfile_buildpack", "php_buildpack");

        this.cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .buildpacks("staticfile_buildpack", "php_buildpack")
                .diskQuota(512)
                .healthCheckType(ApplicationHealthCheck.NONE)
                .memory(64)
                .name(applicationName)
                .noStart(true)
                .path(new ClassPathResource("test-php.zip").getFile().toPath())
                .build())
            .then(requestGetManifest(this.cloudFoundryOperations, applicationName))
            .map(ApplicationManifest::getBuildpacks)
            .as(StepVerifier::create)
            .expectNext(buildpacks)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushMultipleRoutes() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        requestCreateDomain(this.cloudFoundryOperations, domainName, this.organizationName)
            .then(this.cloudFoundryOperations.applications()
                .pushManifest(PushApplicationManifestRequest.builder()
                    .manifest(ApplicationManifest.builder()
                        .path(new ClassPathResource("test-application.zip").getFile().toPath())
                        .buildpack("staticfile_buildpack")
                        .disk(512)
                        .healthCheckType(ApplicationHealthCheck.PORT)
                        .memory(64)
                        .name(applicationName)
                        .route(Route.builder()
                            .route(String.format("test1.%s", domainName))
                            .build())
                        .route(Route.builder()
                            .route(String.format("test2.%s", domainName))
                            .build())
                        .build())
                    .noStart(false)
                    .build()))
            .thenMany(this.cloudFoundryOperations.routes()
                .list(ListRoutesRequest.builder()
                    .build()))
            .filter(response -> domainName.equals(response.getDomain()))
            .map(org.cloudfoundry.operations.routes.Route::getApplications)
            .as(StepVerifier::create)
            .expectNextCount(2)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushNew() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushNewDocker() {
        String applicationName = this.nameFactory.getApplicationName();

        createDockerApplication(this.cloudFoundryOperations, applicationName, false)
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushNoHostName() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        requestCreateDomain(this.cloudFoundryOperations, domainName, this.organizationName)
            .then(this.cloudFoundryOperations.applications()
                .push(PushApplicationRequest.builder()
                    .path(new ClassPathResource("test-application.zip").getFile().toPath())
                    .buildpack("staticfile_buildpack")
                    .diskQuota(512)
                    .domain(domainName)
                    .healthCheckType(ApplicationHealthCheck.PORT)
                    .memory(64)
                    .name(applicationName)
                    .noHostname(true)
                    .noStart(false)
                    .build()))
            .thenMany(this.cloudFoundryOperations.routes()
                .list(ListRoutesRequest.builder()
                    .build()))
            .filter(response -> domainName.equals(response.getDomain()))
            .map(org.cloudfoundry.operations.routes.Route::getHost)
            .as(StepVerifier::create)
            .expectNext("")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushNoRoute() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        createDomain(this.cloudFoundryOperations, domainName, this.organizationName)
            .then(this.cloudFoundryOperations.applications()
                .push(PushApplicationRequest.builder()
                    .path(new ClassPathResource("test-application.zip").getFile().toPath())
                    .buildpack("staticfile_buildpack")
                    .diskQuota(512)
                    .domain(domainName)
                    .memory(64)
                    .name(applicationName)
                    .noStart(true)
                    .build()))
            .then(this.cloudFoundryOperations.applications()
                .push(PushApplicationRequest.builder()
                    .path(new ClassPathResource("test-application.zip").getFile().toPath())
                    .buildpack("staticfile_buildpack")
                    .diskQuota(512)
                    .memory(64)
                    .name(applicationName)
                    .noRoute(true)
                    .noStart(true)
                    .build()))
            .thenMany(requestListRoutes(this.cloudFoundryOperations))
            .flatMapIterable(org.cloudfoundry.operations.routes.Route::getApplications)
            .filter(applicationName::equals)
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushPrivateDomain() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        createDomain(this.cloudFoundryOperations, domainName, this.organizationName)
            .then(this.cloudFoundryOperations.applications()
                .push(PushApplicationRequest.builder()
                    .path(new ClassPathResource("test-application.zip").getFile().toPath())
                    .buildpack("staticfile_buildpack")
                    .diskQuota(512)
                    .domain(domainName)
                    .memory(64)
                    .name(applicationName)
                    .build()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushRouteAndRoutePath() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String routePath1 = this.nameFactory.getPath();
        String routePath2 = this.nameFactory.getPath();

        requestCreateDomain(this.cloudFoundryOperations, domainName, this.organizationName)
            .then(this.cloudFoundryOperations.applications()
                .pushManifest(PushApplicationManifestRequest.builder()
                    .manifest(ApplicationManifest.builder()
                        .path(new ClassPathResource("test-application.zip").getFile().toPath())
                        .buildpack("staticfile_buildpack")
                        .disk(512)
                        .healthCheckType(ApplicationHealthCheck.PORT)
                        .memory(64)
                        .name(applicationName)
                        .route(Route.builder()
                            .route(String.format("test.%s%s", domainName, routePath1))
                            .build())
                        .routePath(routePath2)
                        .build())
                    .noStart(false)
                    .build()))
            .thenMany(requestListRoutes(this.cloudFoundryOperations))
            .map(org.cloudfoundry.operations.routes.Route::getPath)
            .filter(routePath2::equals)
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushRoutePath() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String routePath = this.nameFactory.getPath();

        this.cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .path(new ClassPathResource("test-application.zip").getFile().toPath())
                .buildpack("staticfile_buildpack")
                .diskQuota(512)
                .healthCheckHttpEndpoint("/health")
                .healthCheckType(ApplicationHealthCheck.PORT)
                .memory(64)
                .name(applicationName)
                .noStart(false)
                .routePath(routePath)
                .build())
            .thenMany(requestListRoutes(this.cloudFoundryOperations))
            .filter(route -> routePath.equals(route.getPath()))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushTcpRoute() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        requestCreateTcpDomain(this.cloudFoundryOperations, domainName, DEFAULT_ROUTER_GROUP)
            .then(this.cloudFoundryOperations.applications()
                .pushManifest(PushApplicationManifestRequest.builder()
                    .manifest(ApplicationManifest.builder()
                        .path(new ClassPathResource("test-application.zip").getFile().toPath())
                        .buildpack("staticfile_buildpack")
                        .disk(512)
                        .healthCheckType(ApplicationHealthCheck.PROCESS)
                        .memory(64)
                        .name(applicationName)
                        .randomRoute(true)
                        .route(Route.builder()
                            .route(domainName)
                            .build())
                        .build())
                    .noStart(true)
                    .build()))
            .then(requestGetManifest(this.cloudFoundryOperations, applicationName))
            .map(manifest -> manifest.getRoutes().get(0).getRoute())
            .as(StepVerifier::create)
            .consumeNextWith(route -> assertThat(route).matches(domainName + "+?:\\d+$"))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushUpdateRoute() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();
        String originalHostName = this.nameFactory.getHostName();
        String newHostName = this.nameFactory.getHostName();

        requestCreateDomain(this.cloudFoundryOperations, domainName, this.organizationName)
            .then(this.cloudFoundryOperations.applications()
                .pushManifest(PushApplicationManifestRequest.builder()
                    .manifest(ApplicationManifest.builder()
                        .path(new ClassPathResource("test-application.zip").getFile().toPath())
                        .buildpack("staticfile_buildpack")
                        .disk(512)
                        .healthCheckType(ApplicationHealthCheck.PROCESS)
                        .memory(64)
                        .name(applicationName)
                        .route(Route.builder()
                            .route(String.format("%s.%s", originalHostName, domainName))
                            .build())
                        .build())
                    .noStart(true)
                    .build()))
            .then(this.cloudFoundryOperations.applications()
                .pushManifest(PushApplicationManifestRequest.builder()
                    .manifest(ApplicationManifest.builder()
                        .path(new ClassPathResource("test-application.zip").getFile().toPath())
                        .buildpack("staticfile_buildpack")
                        .disk(512)
                        .healthCheckType(ApplicationHealthCheck.PROCESS)
                        .memory(64)
                        .name(applicationName)
                        .route(Route.builder()
                            .route(String.format("%s.%s", newHostName, domainName))
                            .build())
                        .build())
                    .noStart(true)
                    .build()))
            .thenMany(this.cloudFoundryOperations.routes()
                .list(ListRoutesRequest.builder()
                    .build()))
            .flatMapIterable(org.cloudfoundry.operations.routes.Route::getApplications)
            .filter(applicationName::equals)
            .as(StepVerifier::create)
            .expectNextCount(2)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushUpdateTcpRoute() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        requestCreateTcpDomain(this.cloudFoundryOperations, domainName, DEFAULT_ROUTER_GROUP)
            .then(this.cloudFoundryOperations.applications()
                .pushManifest(PushApplicationManifestRequest.builder()
                    .manifest(ApplicationManifest.builder()
                        .path(new ClassPathResource("test-application.zip").getFile().toPath())
                        .buildpack("staticfile_buildpack")
                        .disk(512)
                        .healthCheckType(ApplicationHealthCheck.PROCESS)
                        .memory(64)
                        .name(applicationName)
                        .randomRoute(true)
                        .route(Route.builder()
                            .route(domainName)
                            .build())
                        .build())
                    .noStart(true)
                    .build()))
            .then(this.cloudFoundryOperations.applications()
                .pushManifest(PushApplicationManifestRequest.builder()
                    .manifest(ApplicationManifest.builder()
                        .path(new ClassPathResource("test-application.zip").getFile().toPath())
                        .buildpack("staticfile_buildpack")
                        .disk(512)
                        .healthCheckType(ApplicationHealthCheck.PROCESS)
                        .memory(64)
                        .name(applicationName)
                        .randomRoute(true)
                        .route(Route.builder()
                            .route(domainName)
                            .build())
                        .build())
                    .noStart(true)
                    .build()))
            .then(requestGetApplication(this.cloudFoundryOperations, applicationName))
            .map(ApplicationDetail::getUrls)
            .as(StepVerifier::create)
            .consumeNextWith(routes -> {
                assertThat(routes.get(0).matches(domainName + "+?:\\d+$"));
                assertThat(routes.get(1).matches(domainName + "+?:\\d+$"));
                assertThat(!routes.get(0).matches(routes.get(1)));
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushWithHost() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String host = this.nameFactory.getHostName();

        this.cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .path(new ClassPathResource("test-application.zip").getFile().toPath())
                .buildpack("staticfile_buildpack")
                .diskQuota(512)
                .healthCheckType(ApplicationHealthCheck.PORT)
                .host(host)
                .memory(64)
                .name(applicationName)
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void rename() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String newName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .rename(RenameApplicationRequest.builder()
                    .name(applicationName)
                    .newName(newName)
                    .build()))
            .then(requestGetApplication(this.cloudFoundryOperations, newName))
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void restage() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .restage(RestageApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void restartInstance() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .restartInstance(RestartApplicationInstanceRequest.builder()
                    .instanceIndex(0)
                    .name(applicationName)
                    .build()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void restartNotStarted() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, true)
            .then(this.cloudFoundryOperations.applications()
                .restart(RestartApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void restartStarted() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .restart(RestartApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_1_11)
    @Test
    public void runTask() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String taskName = this.nameFactory.getTaskName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .runTask(RunApplicationTaskRequest.builder()
                    .applicationName(applicationName)
                    .command("ls")
                    .disk(64)
                    .memory(64)
                    .taskName(taskName)
                    .build()))
            .map(Task::getName)
            .as(StepVerifier::create)
            .expectNext(taskName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void scale() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .scale(ScaleApplicationRequest.builder()
                    .instances(2)
                    .memoryLimit(65)
                    .name(applicationName)
                    .build()))
            .then(requestGetApplication(this.cloudFoundryOperations, applicationName))
            .map(ApplicationDetail::getRunningInstances)
            .as(StepVerifier::create)
            .expectNext(2)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void setEnvironmentVariable() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String variableName1 = this.nameFactory.getVariableName();
        String variableName2 = this.nameFactory.getVariableName();
        String variableValue1 = this.nameFactory.getVariableValue();
        String variableValue2 = this.nameFactory.getVariableValue();

        Map<String, Object> expected = FluentMap.<String, Object>builder()
            .entry(variableName1, variableValue1)
            .entry(variableName2, variableValue2)
            .build();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .setEnvironmentVariable(SetEnvironmentVariableApplicationRequest.builder()
                    .name(applicationName)
                    .variableName(variableName1)
                    .variableValue(variableValue1)
                    .build()))
            .then(this.cloudFoundryOperations.applications()
                .setEnvironmentVariable(SetEnvironmentVariableApplicationRequest.builder()
                    .name(applicationName)
                    .variableName(variableName2)
                    .variableValue(variableValue2)
                    .build()))
            .then(this.cloudFoundryOperations.applications()
                .getEnvironments(GetApplicationEnvironmentsRequest.builder()
                    .name(applicationName)
                    .build()))
            .map(ApplicationEnvironments::getUserProvided)
            .as(StepVerifier::create)
            .expectNext(expected)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void setHealthCheck() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .setHealthCheck(SetApplicationHealthCheckRequest.builder()
                    .name(applicationName)
                    .type(ApplicationHealthCheck.PROCESS)
                    .build()))
            .then(requestGetHealthCheck(this.cloudFoundryOperations, applicationName))
            .as(StepVerifier::create)
            .expectNext(ApplicationHealthCheck.PROCESS)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void sshEnabled() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .sshEnabled(ApplicationSshEnabledRequest.builder()
                    .name(applicationName)
                    .build()))
            .as(StepVerifier::create)
            .expectNext(true)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void startNotStarted() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, true)
            .then(this.cloudFoundryOperations.applications()
                .start(StartApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void startStarted() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .start(StartApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void stop() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .stop(StopApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .then(requestGetApplication(this.cloudFoundryOperations, applicationName))
            .map(ApplicationDetail::getRequestedState)
            .as(StepVerifier::create)
            .expectNext("STOPPED")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_1_11)
    @Test
    public void terminateTask() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String taskName = this.nameFactory.getTaskName();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(getLongLivedTaskId(this.cloudFoundryOperations, applicationName, taskName))
            .flatMap(sequenceId -> this.cloudFoundryOperations.applications()
                .terminateTask(TerminateApplicationTaskRequest.builder()
                    .applicationName(applicationName)
                    .sequenceId(sequenceId)
                    .build())
                .thenReturn(sequenceId))
            .flatMapMany(sequenceId -> requestListTasks(this.cloudFoundryOperations, applicationName)
                .filter(task -> sequenceId.equals(task.getSequenceId())))
            .map(Task::getState)
            .as(StepVerifier::create)
            .expectNext(TaskState.FAILED)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void unsetEnvironmentVariableComplete() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String variableName1 = this.nameFactory.getVariableName();
        String variableName2 = this.nameFactory.getVariableName();
        String variableValue1 = this.nameFactory.getVariableValue();
        String variableValue2 = this.nameFactory.getVariableValue();

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .setEnvironmentVariable(SetEnvironmentVariableApplicationRequest.builder()
                    .name(applicationName)
                    .variableName(variableName1)
                    .variableValue(variableValue1)
                    .build()))
            .then(this.cloudFoundryOperations.applications()
                .setEnvironmentVariable(SetEnvironmentVariableApplicationRequest.builder()
                    .name(applicationName)
                    .variableName(variableName2)
                    .variableValue(variableValue2)
                    .build()))
            .then(this.cloudFoundryOperations.applications()
                .unsetEnvironmentVariable(UnsetEnvironmentVariableApplicationRequest.builder()
                    .name(applicationName)
                    .variableName(variableName1)
                    .build()))
            .then(this.cloudFoundryOperations.applications()
                .unsetEnvironmentVariable(UnsetEnvironmentVariableApplicationRequest.builder()
                    .name(applicationName)
                    .variableName(variableName2)
                    .build()))
            .then(this.cloudFoundryOperations.applications()
                .getEnvironments(GetApplicationEnvironmentsRequest.builder()
                    .name(applicationName)
                    .build()))
            .map(ApplicationEnvironments::getUserProvided)
            .as(StepVerifier::create)
            .expectNext(Collections.emptyMap())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void unsetEnvironmentVariablePartial() throws IOException {
        String applicationName = this.nameFactory.getApplicationName();
        String variableName1 = this.nameFactory.getVariableName();
        String variableName2 = this.nameFactory.getVariableName();
        String variableValue1 = this.nameFactory.getVariableValue();
        String variableValue2 = this.nameFactory.getVariableValue();

        Map<String, Object> expected = Collections.singletonMap(variableName2, variableValue2);

        createApplication(this.cloudFoundryOperations, new ClassPathResource("test-application.zip").getFile().toPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .setEnvironmentVariable(SetEnvironmentVariableApplicationRequest.builder()
                    .name(applicationName)
                    .variableName(variableName1)
                    .variableValue(variableValue1)
                    .build()))
            .then(this.cloudFoundryOperations.applications()
                .setEnvironmentVariable(SetEnvironmentVariableApplicationRequest.builder()
                    .name(applicationName)
                    .variableName(variableName2)
                    .variableValue(variableValue2)
                    .build()))
            .then(this.cloudFoundryOperations.applications()
                .unsetEnvironmentVariable(UnsetEnvironmentVariableApplicationRequest.builder()
                    .name(applicationName)
                    .variableName(variableName1)
                    .build()))
            .then(this.cloudFoundryOperations.applications()
                .getEnvironments(GetApplicationEnvironmentsRequest.builder()
                    .name(applicationName)
                    .build()))
            .map(ApplicationEnvironments::getUserProvided)
            .as(StepVerifier::create)
            .expectNext(expected)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<Void> bindServiceToApplication(CloudFoundryOperations cloudFoundryOperations, String applicationName, String serviceInstanceName) {
        return cloudFoundryOperations.services()
            .createUserProvidedInstance(CreateUserProvidedServiceInstanceRequest.builder()
                .name(serviceInstanceName)
                .build())
            .then(cloudFoundryOperations.services()
                .bind(BindServiceInstanceRequest.builder()
                    .serviceInstanceName(serviceInstanceName)
                    .applicationName(applicationName)
                    .build()));
    }

    private static Mono<Void> createApplication(CloudFoundryOperations cloudFoundryOperations, Path application, String name, Boolean noStart) {
        return cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .buildpack("staticfile_buildpack")
                .diskQuota(512)
                .healthCheckType(ApplicationHealthCheck.NONE)
                .memory(64)
                .name(name)
                .noStart(noStart)
                .path(application)
                .build());
    }

    private static Mono<Void> createApplicationPhp(CloudFoundryOperations cloudFoundryOperations, Path application, String name, Boolean noStart) {
        return cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .buildpacks("staticfile_buildpack", "php_buildpack")
                .diskQuota(512)
                .healthCheckType(ApplicationHealthCheck.NONE)
                .memory(64)
                .name(name)
                .noStart(noStart)
                .path(application)
                .build());
    }

    private static Mono<Void> createApplicationTcp(CloudFoundryOperations cloudFoundryOperations, String applicationName, String domainName) throws IOException {
        return cloudFoundryOperations.applications()
            .pushManifest(PushApplicationManifestRequest.builder()
                .manifest(ApplicationManifest.builder()
                    .buildpack("staticfile_buildpack")
                    .disk(512)
                    .healthCheckType(ApplicationHealthCheck.PROCESS)
                    .memory(64)
                    .name(applicationName)
                    .path(new ClassPathResource("test-application.zip").getFile().toPath())
                    .randomRoute(true)
                    .route(Route.builder()
                        .route(domainName)
                        .build())
                    .build())
                .noStart(true)
                .build());
    }

    private static Mono<Void> createDockerApplication(CloudFoundryOperations cloudFoundryOperations, String name, Boolean noStart) {
        return cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .diskQuota(512)
                .dockerImage("cloudfoundry/lattice-app")
                .healthCheckType(ApplicationHealthCheck.PORT)
                .memory(64)
                .name(name)
                .noStart(noStart)
                .build());
    }

    private static Mono<Void> createDomain(CloudFoundryOperations cloudFoundryOperations, String domainName, String organizationName) {
        return cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain(domainName)
                .organization(organizationName)
                .build());
    }

    private static Mono<Void> createServiceInstance(CloudFoundryOperations cloudFoundryOperations, String planName, String serviceInstanceName, String serviceName) {
        return cloudFoundryOperations.services()
            .createInstance(CreateServiceInstanceRequest.builder()
                .planName(planName)
                .serviceInstanceName(serviceInstanceName)
                .serviceName(serviceName)
                .build());
    }

    private static Mono<Integer> getLongLivedTaskId(CloudFoundryOperations cloudFoundryOperations, String applicationName, String taskName) {
        return requestCreateLongLivedTask(cloudFoundryOperations, applicationName, taskName)
            .map(Task::getSequenceId);
    }

    private static Mono<ServiceInstance> getServiceInstance(CloudFoundryOperations cloudFoundryOperations, String serviceInstanceName) {
        return cloudFoundryOperations.services()
            .getInstance(GetServiceInstanceRequest.builder()
                .name(serviceInstanceName)
                .build());
    }

    private static Mono<Void> requestCreateDomain(CloudFoundryOperations cloudFoundryOperations, String domainName, String organizationName) {
        return cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain(domainName)
                .organization(organizationName)
                .build());
    }

    private static Mono<Task> requestCreateLongLivedTask(CloudFoundryOperations cloudFoundryOperations, String applicationName, String taskName) {
        return cloudFoundryOperations.applications()
            .runTask(RunApplicationTaskRequest.builder()
                .applicationName(applicationName)
                .command("sleep 99")
                .disk(64)
                .memory(64)
                .taskName(taskName)
                .build());
    }

    private static Mono<Task> requestCreateTask(CloudFoundryOperations cloudFoundryOperations, String applicationName, String taskName) {
        return cloudFoundryOperations.applications()
            .runTask(RunApplicationTaskRequest.builder()
                .applicationName(applicationName)
                .command("ls")
                .disk(64)
                .memory(64)
                .taskName(taskName)
                .build());
    }

    private static Mono<Void> requestCreateTcpDomain(CloudFoundryOperations cloudFoundryOperations, String domainName, String routerGroup) {
        return cloudFoundryOperations.domains()
            .createShared(CreateSharedDomainRequest.builder()
                .domain(domainName)
                .routerGroup(routerGroup)
                .build());
    }

    private static Mono<ApplicationDetail> requestGetApplication(CloudFoundryOperations cloudFoundryOperations, String applicationName) {
        return cloudFoundryOperations.applications()
            .get(GetApplicationRequest.builder()
                .name(applicationName)
                .build());
    }

    private static Mono<ApplicationHealthCheck> requestGetHealthCheck(CloudFoundryOperations cloudFoundryOperations, String applicationName) {
        return cloudFoundryOperations.applications()
            .getHealthCheck(GetApplicationHealthCheckRequest.builder()
                .name(applicationName)
                .build());
    }

    private static Mono<ApplicationManifest> requestGetManifest(CloudFoundryOperations cloudFoundryOperations, String applicationName) {
        return cloudFoundryOperations.applications()
            .getApplicationManifest(GetApplicationManifestRequest.builder()
                .name(applicationName)
                .build());
    }

    private static Flux<ApplicationSummary> requestListApplications(CloudFoundryOperations cloudFoundryOperations) {
        return cloudFoundryOperations.applications()
            .list();
    }

    private static Flux<org.cloudfoundry.operations.routes.Route> requestListRoutes(CloudFoundryOperations cloudFoundryOperations) {
        return cloudFoundryOperations.routes()
            .list(ListRoutesRequest.builder()
                .build());
    }

    private static Flux<Task> requestListTasks(CloudFoundryOperations cloudFoundryOperations, String applicationName) {
        return cloudFoundryOperations.applications()
            .listTasks(ListApplicationTasksRequest.builder()
                .name(applicationName)
                .build());
    }

    private static Mono<Boolean> requestSshEnabled(CloudFoundryOperations cloudFoundryOperations, String applicationName) {
        return cloudFoundryOperations.applications()
            .sshEnabled(ApplicationSshEnabledRequest.builder()
                .name(applicationName)
                .build());
    }

}

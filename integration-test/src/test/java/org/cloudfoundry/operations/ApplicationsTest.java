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

package org.cloudfoundry.operations;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.cloudfoundry.operations.applications.ApplicationEnvironments;
import org.cloudfoundry.operations.applications.ApplicationHealthCheck;
import org.cloudfoundry.operations.applications.DeleteApplicationRequest;
import org.cloudfoundry.operations.applications.GetApplicationEnvironmentsRequest;
import org.cloudfoundry.operations.applications.GetApplicationHealthCheckRequest;
import org.cloudfoundry.operations.applications.GetApplicationRequest;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.operations.applications.RestartApplicationRequest;
import org.cloudfoundry.operations.applications.SetEnvironmentVariableApplicationRequest;
import org.cloudfoundry.operations.applications.StartApplicationRequest;
import org.cloudfoundry.operations.applications.UnsetEnvironmentVariableApplicationRequest;
import org.cloudfoundry.operations.domains.CreateDomainRequest;
import org.cloudfoundry.operations.services.BindServiceInstanceRequest;
import org.cloudfoundry.operations.services.CreateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.util.FluentMap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.test.subscriber.ScriptedSubscriber;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public final class ApplicationsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Autowired
    private String organizationName;

    @Test
    public void deleteApplication() throws IOException, TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Void> subscriber = ScriptedSubscriber.<Void>create()
            .expectComplete();

        createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .delete(DeleteApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteApplicationAndRoutes() throws IOException, TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Void> subscriber = ScriptedSubscriber.<Void>create()
            .expectComplete();

        createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .delete(DeleteApplicationRequest.builder()
                    .name(applicationName)
                    .deleteRoutes(true)
                    .build()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteApplicationWithServiceBindings() throws IOException, TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String serviceInstanceName = this.nameFactory.getServiceInstanceName();

        ScriptedSubscriber<Void> subscriber = ScriptedSubscriber.<Void>create()
            .expectComplete();

        Mono.empty()
            .then(createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, false))
            .then(bindServiceToApplication(this.cloudFoundryOperations, applicationName, serviceInstanceName))
            .then(this.cloudFoundryOperations.applications()
                .delete(DeleteApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<String> subscriber = ScriptedSubscriber.<String>create()
            .expectValue(applicationName)
            .expectComplete();

        createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .get(GetApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .map(ApplicationDetail::getName)
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void getHealthCheck() throws IOException, TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<ApplicationHealthCheck> subscriber = ScriptedSubscriber.<ApplicationHealthCheck>create()
            .expectValue(ApplicationHealthCheck.PORT)
            .expectComplete();

        createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, true)
            .then(this.cloudFoundryOperations.applications()
                .getHealthCheck(GetApplicationHealthCheckRequest.builder()
                    .name(applicationName)
                    .build()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void getStopped() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<String> subscriber = ScriptedSubscriber.<String>create()
            .expectValue(applicationName)
            .expectComplete();

        createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, true)
            .then(this.cloudFoundryOperations.applications()
                .get(GetApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .map(ApplicationDetail::getName)
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushDomainNotFound() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<Void> subscriber = errorExpectation(IllegalArgumentException.class, "Domain %s not found", domainName);

        this.cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .application(getApplicationPath())
                .buildpack("staticfile_buildpack")
                .domain(domainName)
                .diskQuota(512)
                .memory(64)
                .name(applicationName)
                .build())
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushExisting() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Void> subscriber = ScriptedSubscriber.<Void>create()
            .expectComplete();

        createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .push(PushApplicationRequest.builder()
                    .application(getApplicationPath())
                    .buildpack("staticfile_buildpack")
                    .diskQuota(512)
                    .memory(64)
                    .name(applicationName)
                    .build()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushNew() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Void> subscriber = ScriptedSubscriber.<Void>create()
            .expectComplete();

        createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, false)
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushNewDocker() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Void> subscriber = ScriptedSubscriber.<Void>create()
            .expectComplete();

        createDockerApplication(this.cloudFoundryOperations, applicationName, false)
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushPrivateDomain() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String domainName = this.nameFactory.getDomainName();

        ScriptedSubscriber<Void> subscriber = ScriptedSubscriber.<Void>create()
            .expectComplete();

        this.cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain(domainName)
                .organization(this.organizationName)
                .build())
            .then(this.cloudFoundryOperations.applications()
                .push(PushApplicationRequest.builder()
                    .application(getApplicationPath())
                    .buildpack("staticfile_buildpack")
                    .diskQuota(512)
                    .domain(domainName)
                    .memory(64)
                    .name(applicationName)
                    .build()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void pushWithHost() throws TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String host = this.nameFactory.getHostName();

        ScriptedSubscriber<Void> subscriber = ScriptedSubscriber.<Void>create()
            .expectComplete();

        this.cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .application(getApplicationPath())
                .buildpack("staticfile_buildpack")
                .diskQuota(512)
                .healthCheckType(ApplicationHealthCheck.PORT)
                .host(host)
                .memory(64)
                .name(applicationName)
                .build())
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void restartNotStarted() throws IOException, TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Void> subscriber = ScriptedSubscriber.<Void>create()
            .expectComplete();

        createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, true)
            .then(this.cloudFoundryOperations.applications()
                .restart(RestartApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void restartStarted() throws IOException, TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Void> subscriber = ScriptedSubscriber.<Void>create()
            .expectComplete();

        createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .restart(RestartApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void setEnvironmentVariable() throws IOException, TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String variableName1 = this.nameFactory.getVariableName();
        String variableName2 = this.nameFactory.getVariableName();
        String variableValue1 = this.nameFactory.getVariableValue();
        String variableValue2 = this.nameFactory.getVariableValue();

        Map<String, Object> expected = FluentMap.<String, Object>builder()
            .entry(variableName1, variableValue1)
            .entry(variableName2, variableValue2)
            .build();

        ScriptedSubscriber<Map<String, Object>> subscriber = ScriptedSubscriber.<Map<String, Object>>create()
            .expectValue(expected)
            .expectComplete();

        createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, false)
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
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void startNotStarted() throws IOException, TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Void> subscriber = ScriptedSubscriber.<Void>create()
            .expectComplete();

        createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, true)
            .then(this.cloudFoundryOperations.applications()
                .start(StartApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void startStarted() throws IOException, TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();

        ScriptedSubscriber<Void> subscriber = ScriptedSubscriber.<Void>create()
            .expectComplete();

        createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, false)
            .then(this.cloudFoundryOperations.applications()
                .start(StartApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void unsetEnvironmentVariableComplete() throws IOException, TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String variableName1 = this.nameFactory.getVariableName();
        String variableName2 = this.nameFactory.getVariableName();
        String variableValue1 = this.nameFactory.getVariableValue();
        String variableValue2 = this.nameFactory.getVariableValue();

        Map<String, Object> expected = Collections.emptyMap();

        ScriptedSubscriber<Map<String, Object>> subscriber = ScriptedSubscriber.<Map<String, Object>>create()
            .expectValue(expected)
            .expectComplete();

        createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, false)
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
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void unsetEnvironmentVariablePartial() throws IOException, TimeoutException, InterruptedException {
        String applicationName = this.nameFactory.getApplicationName();
        String variableName1 = this.nameFactory.getVariableName();
        String variableName2 = this.nameFactory.getVariableName();
        String variableValue1 = this.nameFactory.getVariableValue();
        String variableValue2 = this.nameFactory.getVariableValue();

        Map<String, Object> expected = Collections.singletonMap(variableName2, variableValue2);

        ScriptedSubscriber<Map<String, Object>> subscriber = ScriptedSubscriber.<Map<String, Object>>create()
            .expectValue(expected)
            .expectComplete();

        createApplication(this.cloudFoundryOperations, getApplicationPath(), applicationName, false)
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
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
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
                .application(application)
                .buildpack("staticfile_buildpack")
                .diskQuota(512)
                .healthCheckType(ApplicationHealthCheck.PORT)
                .memory(64)
                .name(name)
                .noStart(noStart)
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

    private static Path getApplicationPath() {
        try {
            return new ClassPathResource("test-application.zip").getFile().toPath();
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

}

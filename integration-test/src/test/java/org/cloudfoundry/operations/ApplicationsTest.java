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
import org.cloudfoundry.util.StringMap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;

public final class ApplicationsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Autowired
    private String organizationName;

    @Test
    public void deleteApplication() throws IOException {
        String applicationName = getApplicationName();

        createApplication(this.cloudFoundryOperations, getApplicationBits(), applicationName, false)
            .after(this.cloudFoundryOperations.applications()
                .delete(DeleteApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void deleteApplicationAndRoutes() throws IOException {
        String applicationName = getApplicationName();

        createApplication(this.cloudFoundryOperations, getApplicationBits(), applicationName, false)
            .after(this.cloudFoundryOperations.applications()
                .delete(DeleteApplicationRequest.builder()
                    .name(applicationName)
                    .deleteRoutes(true)
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void deleteApplicationWithServiceBindings() throws IOException {
        String applicationName = getApplicationName();
        String serviceInstanceName = getServiceInstanceName();

        Mono.empty()
            .after(() -> createApplication(this.cloudFoundryOperations, getApplicationBits(), applicationName, false))
            .after(() -> bindServiceToApplication(this.cloudFoundryOperations, applicationName, serviceInstanceName))
            .after(() -> this.cloudFoundryOperations.applications()
                .delete(DeleteApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void get() {
        String applicationName = getApplicationName();

        createApplication(this.cloudFoundryOperations, getApplicationBits(), applicationName, false)
            .after(this.cloudFoundryOperations.applications()
                .get(GetApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .map(response -> response.getName())
            .subscribe(testSubscriber()
                .assertEquals(applicationName));
    }

    @Test
    public void getHealthCheck() throws IOException {
        String applicationName = getApplicationName();

        createApplication(this.cloudFoundryOperations, getApplicationBits(), applicationName, true)
            .after(this.cloudFoundryOperations.applications()
                .getHealthCheck(GetApplicationHealthCheckRequest.builder()
                    .name(applicationName)
                    .build()))
            .map(ApplicationHealthCheck::getType)
            .subscribe(testSubscriber()
                .assertEquals("port"));
    }

    @Test
    public void getStopped() {
        String applicationName = getApplicationName();

        createApplication(this.cloudFoundryOperations, getApplicationBits(), applicationName, true)
            .after(this.cloudFoundryOperations.applications()
                .get(GetApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .map(response -> response.getName())
            .subscribe(testSubscriber()
                .assertEquals(applicationName));
    }

    @Test
    public void pushDomainNotFound() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();

        this.cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .application(getApplicationBits())
                .buildpack("staticfile_buildpack")
                .domain(domainName)
                .diskQuota(512)
                .memory(64)
                .name(applicationName)
                .build())
            .subscribe(testSubscriber()
                .assertError(IllegalStateException.class, "Domain %s not found", domainName));
    }

    @Test
    public void pushExisting() {
        String applicationName = getApplicationName();

        createApplication(this.cloudFoundryOperations, getApplicationBits(), applicationName, false)
            .after(this.cloudFoundryOperations.applications()
                .push(PushApplicationRequest.builder()
                    .application(getApplicationBits())
                    .buildpack("staticfile_buildpack")
                    .diskQuota(512)
                    .memory(64)
                    .name(applicationName)
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void pushNew() {
        String applicationName = getApplicationName();

        createApplication(this.cloudFoundryOperations, getApplicationBits(), applicationName, false)
            .subscribe(testSubscriber());
    }

    @Test
    public void pushPrivateDomain() {
        String applicationName = getApplicationName();
        String domainName = getDomainName();

        this.cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain(domainName)
                .organization(this.organizationName)
                .build())
            .after(this.cloudFoundryOperations.applications()
                .push(PushApplicationRequest.builder()
                    .application(getApplicationBits())
                    .buildpack("staticfile_buildpack")
                    .diskQuota(512)
                    .domain(domainName)
                    .memory(64)
                    .name(applicationName)
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void pushWithHost() {
        String applicationName = getApplicationName();
        String host = getHostName();

        this.cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .application(getApplicationBits())
                .buildpack("staticfile_buildpack")
                .diskQuota(512)
                .healthCheckType("port")
                .host(host)
                .memory(64)
                .name(applicationName)
                .build())
            .subscribe(testSubscriber());
    }

    @Test
    public void restartNotStarted() throws IOException {
        String applicationName = getApplicationName();

        createApplication(this.cloudFoundryOperations, getApplicationBits(), applicationName, false)
            .after(this.cloudFoundryOperations.applications()
                .restart(RestartApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void restartStarted() throws IOException {
        String applicationName = getApplicationName();

        createApplication(this.cloudFoundryOperations, getApplicationBits(), applicationName, false)
            .after(this.cloudFoundryOperations.applications()
                .restart(RestartApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void setAndUnsetEnvironment() throws IOException {
        String applicationName = getApplicationName();

        createApplication(this.cloudFoundryOperations, getApplicationBits(), applicationName, false)
            .after(this.cloudFoundryOperations.applications()
                .setEnvironmentVariable(SetEnvironmentVariableApplicationRequest.builder()
                    .name(applicationName)
                    .variableName("test-var-name")
                    .variableValue("test-var-value")
                    .build()))
            .after(this.cloudFoundryOperations.applications()
                .setEnvironmentVariable(SetEnvironmentVariableApplicationRequest.builder()
                    .name(applicationName)
                    .variableName("test-var2-name")
                    .variableValue("test-var2-value")
                    .build()))
            .after(this.cloudFoundryOperations.applications()
                .unsetEnvironmentVariable(UnsetEnvironmentVariableApplicationRequest.builder()
                    .name(applicationName)
                    .variableName("test-var-name")
                    .build()))
            .after(this.cloudFoundryOperations.applications()
                .getEnvironments(GetApplicationEnvironmentsRequest.builder()
                    .name(applicationName)
                    .build()))
            .map(ApplicationEnvironments::getUserProvided)
            .subscribe(testSubscriber()
                .assertEquals(StringMap.builder()
                    .entry("test-var2-name", "test-var2-value")
                    .build()));
    }

    @Test
    public void startNotStarted() throws IOException {
        String applicationName = getApplicationName();

        createApplication(this.cloudFoundryOperations, getApplicationBits(), applicationName, false)
            .after(this.cloudFoundryOperations.applications()
                .start(StartApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void startStarted() throws IOException {
        String applicationName = getApplicationName();

        createApplication(this.cloudFoundryOperations, getApplicationBits(), applicationName, false)
            .after(this.cloudFoundryOperations.applications()
                .start(StartApplicationRequest.builder()
                    .name(applicationName)
                    .build()))
            .subscribe(testSubscriber());
    }

    private static Mono<Void> bindServiceToApplication(CloudFoundryOperations cloudFoundryOperations, String applicationName, String serviceInstanceName) {
        return cloudFoundryOperations.services()
            .createUserProvidedInstance(CreateUserProvidedServiceInstanceRequest.builder()
                .name(serviceInstanceName)
                .build())
            .after(cloudFoundryOperations.services()
                .bind(BindServiceInstanceRequest.builder()
                    .serviceInstanceName(serviceInstanceName)
                    .applicationName(applicationName)
                    .build()));
    }

    private static Mono<Void> createApplication(CloudFoundryOperations cloudFoundryOperations, InputStream applicationBits, String name, Boolean noStart) {
        return cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .application(applicationBits)
                .buildpack("staticfile_buildpack")
                .diskQuota(512)
                .memory(64)
                .name(name)
                .noStart(noStart)
                .build());
    }

    private static InputStream getApplicationBits() {
        try {
            return new ClassPathResource("test-application.zip").getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}

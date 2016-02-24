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
import org.cloudfoundry.operations.applications.DeleteApplicationRequest;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.operations.applications.RestartApplicationRequest;
import org.cloudfoundry.operations.applications.StartApplicationRequest;
import org.cloudfoundry.operations.domains.CreateDomainRequest;
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
    public void delete() throws IOException {
        String name = "test-deleteNotStarted";

        createApplication(this.cloudFoundryOperations, getApplicationBits(), name, false)
            .after(() -> this.cloudFoundryOperations.applications()
                .delete(DeleteApplicationRequest.builder()
                    .name(name)
                    .deleteRoutes(false)
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void deleteWithRoutes() throws IOException {
        String name = "test-deleteWithRoutes";

        createApplication(this.cloudFoundryOperations, getApplicationBits(), name, false)
            .after(() -> this.cloudFoundryOperations.applications()
                .delete(DeleteApplicationRequest.builder()
                    .name(name)
                    .deleteRoutes(true)
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void pushDomainNotFound() {
        String name = "test-pushDomainNotFound";

        this.cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .application(getApplicationBits())
                .name(name)
                .domain("unknown-domain")
                .build())
            .subscribe(testSubscriber()
                .assertError(IllegalStateException.class, "Domain unknown-domain not found"));
    }

    @Test
    public void pushExisting() throws IOException {
        String name = "test-pushExisting";

        createApplication(this.cloudFoundryOperations, getApplicationBits(), name, false)
            .after(() -> this.cloudFoundryOperations.applications()
                .push(PushApplicationRequest.builder()
                    .application(getApplicationBits())
                    .name(name)
                    .diskQuota(257)
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void pushNew() throws IOException {
        String name = "test-pushNew";

        createApplication(this.cloudFoundryOperations, getApplicationBits(), name, false)
            .subscribe(testSubscriber());
    }

    @Test
    public void pushPrivateDomain() {
        String name = "test-pushPrivateDomain";
        String domain = "private.domain";

        this.cloudFoundryOperations.domains()
            .create(CreateDomainRequest.builder()
                .domain(domain)
                .organization(this.organizationName)
                .build())
            .after(() -> this.cloudFoundryOperations.applications()
                .push(PushApplicationRequest.builder()
                    .application(getApplicationBits())
                    .buildpack("staticfile_buildpack")
                    .domain(domain)
                    .name(name)
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void pushWithHost() {
        String name = "test-pushWithHost";

        this.cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .application(getApplicationBits())
                .buildpack("staticfile_buildpack")
                .host("test-host")
                .name(name)
                .build())
            .subscribe(testSubscriber());
    }

    @Test
    public void restartNotStarted() throws IOException {
        String name = "test-restartNotStarted";

        createApplication(this.cloudFoundryOperations, getApplicationBits(), name, false)
            .after(() -> this.cloudFoundryOperations.applications()
                .restart(RestartApplicationRequest.builder()
                    .name(name)
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void restartStarted() throws IOException {
        String name = "test-restartStarted";

        createApplication(this.cloudFoundryOperations, getApplicationBits(), name, false)
            .after(() -> this.cloudFoundryOperations.applications()
                .restart(RestartApplicationRequest.builder()
                    .name(name)
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void startNotStarted() throws IOException {
        String name = "test-startNotStarted";

        createApplication(this.cloudFoundryOperations, getApplicationBits(), name, false)
            .after(() -> this.cloudFoundryOperations.applications()
                .start(StartApplicationRequest.builder()
                    .name(name)
                    .build()))
            .subscribe(testSubscriber());
    }

    @Test
    public void startStarted() throws IOException {
        String name = "test-startStarted";

        createApplication(this.cloudFoundryOperations, getApplicationBits(), name, false)
            .after(() -> this.cloudFoundryOperations.applications()
                .start(StartApplicationRequest.builder()
                    .name(name)
                    .build()))
            .subscribe(testSubscriber());
    }

    private static Mono<Void> createApplication(CloudFoundryOperations cloudFoundryOperations, InputStream applicationBits, String name, Boolean noStart) throws IOException {
        return cloudFoundryOperations.applications()
            .push(PushApplicationRequest.builder()
                .application(applicationBits)
                .buildpack("staticfile_buildpack")
                .diskQuota(32)
                .memory(32)
                .name(name)
                .noStart(noStart)
                .build());
    }

    private static InputStream getApplicationBits() {
        try {
            return new ClassPathResource("testApplication.zip").getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}

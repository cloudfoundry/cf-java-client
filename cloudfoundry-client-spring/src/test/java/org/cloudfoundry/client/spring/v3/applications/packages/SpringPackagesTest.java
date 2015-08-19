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

package org.cloudfoundry.client.spring.v3.applications.packages;

import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.spring.ExpectedExceptionSubscriber;
import org.cloudfoundry.client.spring.v3.StubLinkBased;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.applications.packages.CreatePackageRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;

import static org.cloudfoundry.client.spring.ContentMatchers.jsonPayload;
import static org.cloudfoundry.client.v3.applications.packages.CreatePackageRequest.PackageType.DOCKER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

public final class SpringPackagesTest extends AbstractRestTest {

    private final SpringPackages packages = new SpringPackages(this.restTemplate, this.root);

    @Test
    public void createApplicationId() {
        this.mockServer
                .expect(method(HttpMethod.POST))
                .andExpect(requestTo("https://api.run.pivotal.io/v3/apps/test-application-id/packages"))
                .andExpect(jsonPayload(new ClassPathResource("v3/apps/packages/POST_request.json")))
                .andRespond(withStatus(CREATED)
                        .body(new ClassPathResource("v3/apps/packages/POST_response.json"))
                        .contentType(APPLICATION_JSON));

        CreatePackageRequest request = new CreatePackageRequest()
                .withApplicationId("test-application-id")
                .withType(DOCKER)
                .withUrl("docker://cloudfoundry/runtime-ci");

        this.packages.create(request).subscribe(response -> {
            assertEquals("2015-08-06T00:36:55Z", response.getCreatedAt());
            assertNull(response.getError());

            assertEquals("sha1", response.getHash().getType());
            assertNull(response.getHash().getValue());

            assertEquals("126e54c4-811d-4f7a-9a34-804130a75ab2", response.getId());

            assertEquals(2, response.getLinks().size());
            assertNotNull(response.getLink("self"));
            assertNotNull(response.getLink("app"));

            assertEquals("READY", response.getState());
            assertEquals("docker", response.getType());
            assertNull(response.getUpdatedAt());
            assertEquals("docker://cloudfoundry/runtime-ci", response.getUrl());
            this.mockServer.verify();
        });
    }

    @Test
    public void createLink() {
        this.mockServer
                .expect(method(HttpMethod.POST))
                .andExpect(requestTo("https://api.run.pivotal.io/test-link"))
                .andExpect(jsonPayload(new ClassPathResource("v3/apps/packages/POST_request.json")))
                .andRespond(withStatus(CREATED)
                        .body(new ClassPathResource("v3/apps/packages/POST_response.json"))
                        .contentType(APPLICATION_JSON));

        CreatePackageRequest request = new CreatePackageRequest()
                .withLink(new StubLinkBased("packages", new Link().withHref("test-link")))
                .withType(DOCKER)
                .withUrl("docker://cloudfoundry/runtime-ci");

        this.packages.create(request).subscribe(response -> {
            assertEquals("2015-08-06T00:36:55Z", response.getCreatedAt());
            assertNull(response.getError());

            assertEquals("sha1", response.getHash().getType());
            assertNull(response.getHash().getValue());

            assertEquals("126e54c4-811d-4f7a-9a34-804130a75ab2", response.getId());

            assertEquals(2, response.getLinks().size());
            assertNotNull(response.getLink("self"));
            assertNotNull(response.getLink("app"));

            assertEquals("READY", response.getState());
            assertEquals("docker", response.getType());
            assertNull(response.getUpdatedAt());
            assertEquals("docker://cloudfoundry/runtime-ci", response.getUrl());
            this.mockServer.verify();
        });
    }

    @Test
    public void createError() {
        this.mockServer
                .expect(method(HttpMethod.POST))
                .andExpect(requestTo("https://api.run.pivotal.io/test-link"))
                .andExpect(jsonPayload(new ClassPathResource("v3/apps/packages/POST_request.json")))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        CreatePackageRequest request = new CreatePackageRequest()
                .withLink(new StubLinkBased("packages", new Link().withHref("test-link")))
                .withType(DOCKER)
                .withUrl("docker://cloudfoundry/runtime-ci");

        this.packages.create(request).subscribe(new ExpectedExceptionSubscriber());
    }

    @Test
    public void createInvalidRequest() {
        this.packages.create(new CreatePackageRequest()).subscribe(new ExpectedExceptionSubscriber());
    }

}

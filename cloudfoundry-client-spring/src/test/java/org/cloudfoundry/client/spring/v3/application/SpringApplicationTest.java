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

package org.cloudfoundry.client.spring.v3.application;

import org.cloudfoundry.client.RequestValidationException;
import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v3.application.CreateApplicationRequest;
import org.cloudfoundry.client.v3.application.CreateApplicationResponse;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.Collections;

import static org.cloudfoundry.client.spring.ContentMatchers.jsonPayload;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

public final class SpringApplicationTest extends AbstractRestTest {

    private final SpringApplication application = new SpringApplication(this.restTemplate, this.root);

    @Test
    public void create() throws IOException {
        this.mockServer
                .expect(method(HttpMethod.POST))
                .andExpect(requestTo("https://api.run.pivotal.io/v3/apps"))
                .andExpect(jsonPayload(new ClassPathResource("v3/apps/POST_request.json")))
                .andRespond(withStatus(CREATED)
                        .body(new ClassPathResource("v3/apps/POST_response.json"))
                        .contentType(APPLICATION_JSON));

        CreateApplicationRequest request = new CreateApplicationRequest()
                .withName("my_app")
                .withSpaceId("31627bdc-5bc4-4c4d-a883-c7b2f53db249")
                .withEnvironmentVariable("open", "source")
                .withBuildpack("name-410");

        CreateApplicationResponse response = this.application.create(request).toBlocking().single();

        assertEquals("name-410", response.getBuildpack());
        assertEquals("2015-07-27T22:43:15Z", response.getCreatedAt());
        assertEquals("STOPPED", response.getDesiredState());
        assertEquals(Collections.singletonMap("open", "source"), response.getEnvironmentVariables());
        assertEquals("8b51db6f-7bae-47ca-bc75-74bc957ed460", response.getId());

        assertEquals(7, response.getLinks().size());
        assertNotNull(response.getLink("self"));
        assertNotNull(response.getLink("processes"));
        assertNotNull(response.getLink("packages"));
        assertNotNull(response.getLink("space"));
        assertNotNull(response.getLink("start"));
        assertNotNull(response.getLink("stop"));
        assertNotNull(response.getLink("assign_current_droplet"));

        assertEquals("my_app", response.getName());
        assertEquals(Integer.valueOf(0), response.getTotalDesiredInstances());
        assertNull(response.getUpdatedAt());
        this.mockServer.verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void createError() throws IOException {
        this.mockServer
                .expect(method(HttpMethod.POST))
                .andExpect(requestTo("https://api.run.pivotal.io/v3/apps"))
                .andExpect(jsonPayload(new ClassPathResource("v3/apps/POST_request.json")))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        CreateApplicationRequest request = new CreateApplicationRequest()
                .withName("my_app")
                .withSpaceId("31627bdc-5bc4-4c4d-a883-c7b2f53db249")
                .withEnvironmentVariable("open", "source")
                .withBuildpack("name-410");

        this.application.create(request).toBlocking().single();
    }

    @Test(expected = RequestValidationException.class)
    public void createInvalidRequest() throws Throwable {
        this.application.create(new CreateApplicationRequest()).toBlocking().single();
    }
}

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

package org.cloudfoundry.v3.client.spring.application;

import org.cloudfoundry.v3.client.RequestValidationException;
import org.cloudfoundry.v3.client.application.CreateApplicationRequest;
import org.cloudfoundry.v3.client.application.CreateApplicationResponse;
import org.cloudfoundry.v3.client.spring.AbstractRestTest;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.io.IOException;
import java.util.Collections;

import static org.cloudfoundry.v3.client.spring.ContentMatchers.jsonPayload;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

public final class SpringApplicationTest extends AbstractRestTest {

    private final SpringApplication application = new SpringApplication(this.restTemplate, this.root);

    @Test
    public void create() throws IOException {
        this.mockServer
                .expect(method(HttpMethod.POST))
                .andExpect(requestTo("https://api.run.pivotal.io/v3/apps"))
                .andExpect(jsonPayload(new ClassPathResource("v3/apps/POST_request.json")))
                .andRespond(MockRestResponseCreators.withStatus(CREATED)
                        .body(new ClassPathResource("v3/apps/POST_response.json"))
                        .contentType(MediaType.APPLICATION_JSON));

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

    @Test(expected = RequestValidationException.class)
    public void createInvalidRequest() throws Throwable {
        try {
            this.application.create(new CreateApplicationRequest()).toBlocking().single();
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }
}

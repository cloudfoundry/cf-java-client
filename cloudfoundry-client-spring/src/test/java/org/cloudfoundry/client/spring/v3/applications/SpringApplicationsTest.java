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

package org.cloudfoundry.client.spring.v3.applications;

import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.spring.ExpectedExceptionSubscriber;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsResponse.Resource;
import org.cloudfoundry.client.v3.applications.StartApplicationRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationRequest;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.rx.Streams;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.cloudfoundry.client.spring.ContentMatchers.jsonPayload;
import static org.cloudfoundry.client.v3.PaginatedAndSortedRequest.OrderBy.CREATED_AT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

public final class SpringApplicationsTest extends AbstractRestTest {

    private final SpringApplications applications = new SpringApplications(this.restTemplate, this.root);

    @Test
    public void create() throws IOException {
        this.mockServer
                .expect(method(POST))
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

        Streams.wrap(this.applications.create(request)).consume(response -> {
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
        });
    }

    @Test
    public void createError() throws IOException {
        this.mockServer
                .expect(method(POST))
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

        this.applications.create(request).subscribe(new ExpectedExceptionSubscriber());
    }

    @Test
    public void createInvalidRequest() throws Throwable {
        this.applications.create(new CreateApplicationRequest()).subscribe(new ExpectedExceptionSubscriber());
    }

    @Test
    public void get() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v3/apps/test-id"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v3/apps/GET_{id}_response.json"))
                        .contentType(APPLICATION_JSON));

        GetApplicationRequest request = new GetApplicationRequest()
                .withId("test-id");

        Streams.wrap(this.applications.get(request)).consume(response -> {
            assertEquals("name-2068", response.getBuildpack());
            assertEquals("2015-08-06T00:36:52Z", response.getCreatedAt());
            assertEquals("STOPPED", response.getDesiredState());
            assertEquals(Collections.singletonMap("unicorn", "horn"), response.getEnvironmentVariables());
            assertEquals("guid-a2ea0b27-971f-4f59-a9e4-d299e96c3f20", response.getId());

            assertEquals(10, response.getLinks().size());
            assertNotNull(response.getLink("self"));
            assertNotNull(response.getLink("processes"));
            assertNotNull(response.getLink("routes"));
            assertNotNull(response.getLink("packages"));
            assertNotNull(response.getLink("droplet"));
            assertNotNull(response.getLink("droplets"));
            assertNotNull(response.getLink("space"));
            assertNotNull(response.getLink("start"));
            assertNotNull(response.getLink("stop"));
            assertNotNull(response.getLink("assign_current_droplet"));

            assertEquals("my_app", response.getName());
            assertEquals(Integer.valueOf(3), response.getTotalDesiredInstances());
            assertNull(response.getUpdatedAt());
            this.mockServer.verify();
        });
    }

    @Test
    public void getError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v3/apps/test-id"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        GetApplicationRequest request = new GetApplicationRequest()
                .withId("test-id");

        this.applications.get(request).subscribe(new ExpectedExceptionSubscriber());
    }

    @Test
    public void getInvalidRequest() {
        this.applications.get(new GetApplicationRequest()).subscribe(new ExpectedExceptionSubscriber());
    }

    @Test
    public void getEnvironment() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v3/apps/test-id/env"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v3/apps/GET_{id}_env_response.json"))
                        .contentType(APPLICATION_JSON));

        GetApplicationEnvironmentRequest request = new GetApplicationEnvironmentRequest()
                .withId("test-id");

        Streams.wrap(this.applications.getEnvironment(request)).consume(response -> {
            Map<String, Object> vcapApplication = new HashMap<>();
            vcapApplication.put("limits", Collections.singletonMap("fds", 16384));
            vcapApplication.put("application_name", "app_name");
            vcapApplication.put("application_uris", Collections.emptyList());
            vcapApplication.put("name", "app_name");
            vcapApplication.put("space_name", "some_space");
            vcapApplication.put("space_id", "c595c2ee-df01-4769-a61f-df5bd5e4cbc1");
            vcapApplication.put("uris", Collections.emptyList());
            vcapApplication.put("users", null);

            Map<String, Object> applicationEnvironmentVariables = Collections.singletonMap("VCAP_APPLICATION",
                    vcapApplication);
            assertEquals(applicationEnvironmentVariables, response.getApplicationEnvironmentVariables());

            Map<String, Object> environmentVariables = Collections.singletonMap("SOME_KEY", "some_val");
            assertEquals(environmentVariables, response.getEnvironmentVariables());

            Map<String, Object> runningEnvironmentVariables = Collections.singletonMap("RUNNING_ENV", "running_value");
            assertEquals(runningEnvironmentVariables, response.getRunningEnvironmentVariables());

            Map<String, Object> stagingEnvironmentVariables = Collections.singletonMap("STAGING_ENV", "staging_value");
            assertEquals(stagingEnvironmentVariables, response.getStagingEnvironmentVariables());

            this.mockServer.verify();
        });
    }

    @Test
    public void getEnvironmentError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v3/apps/test-id/env"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        GetApplicationEnvironmentRequest request = new GetApplicationEnvironmentRequest()
                .withId("test-id");

        this.applications.getEnvironment(request).subscribe(new ExpectedExceptionSubscriber());
    }

    @Test
    public void getEnvironmentInvalidRequest() {
        this.applications.getEnvironment(new GetApplicationEnvironmentRequest()).subscribe(new
                ExpectedExceptionSubscriber());
    }

    @Test
    public void delete() {
        this.mockServer
                .expect(method(DELETE))
                .andExpect(requestTo("https://api.run.pivotal.io/v3/apps/test-id"))
                .andRespond(withStatus(OK));

        DeleteApplicationRequest request = new DeleteApplicationRequest()
                .withId("test-id");

        Streams.wrap(this.applications.delete(request)).consume(response -> {
            this.mockServer.verify();
        });
    }

    @Test
    public void deleteError() {
        this.mockServer
                .expect(method(DELETE))
                .andExpect(requestTo("https://api.run.pivotal.io/v3/apps/test-id"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        DeleteApplicationRequest request = new DeleteApplicationRequest()
                .withId("test-id");

        this.applications.delete(request).subscribe(new ExpectedExceptionSubscriber());
    }

    @Test
    public void deleteInvalidRequest() {
        this.applications.delete(new DeleteApplicationRequest()).subscribe(new ExpectedExceptionSubscriber());
    }

    @Test
    public void list() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v3/apps?names[]=test-name&order_by=created_at&page=1"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v3/apps/GET_response.json"))
                        .contentType(APPLICATION_JSON));

        ListApplicationsRequest request = new ListApplicationsRequest()
                .withPage(1)
                .withOrderBy(CREATED_AT)
                .withName("test-name");

        Streams.wrap(this.applications.list(request)).consume(response -> {
            Resource resource = response.getResources().get(0);

            assertEquals("name-383", resource.getBuildpack());
            assertEquals("1970-01-01T00:00:03Z", resource.getCreatedAt());
            assertEquals("STOPPED", resource.getDesiredState());
            assertEquals(Collections.singletonMap("magic", "beautiful"), resource.getEnvironmentVariables());
            assertEquals("guid-acfbae75-7d3a-45b1-b730-ca3cc4263045", resource.getId());
            assertEquals("my_app3", resource.getName());
            assertEquals(Integer.valueOf(0), resource.getTotalDesiredInstances());
            assertNull(resource.getUpdatedAt());

            assertNotNull(resource.getLink("self"));
            assertNotNull(resource.getLink("processes"));
            assertNotNull(resource.getLink("packages"));
            assertNotNull(resource.getLink("space"));
            assertNotNull(resource.getLink("start"));
            assertNotNull(resource.getLink("stop"));
            assertNotNull(resource.getLink("assign_current_droplet"));

            this.mockServer.verify();
        });
    }

    @Test
    public void listError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v3/apps?names[]=test-name&order_by=created_at&page=1"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        ListApplicationsRequest request = new ListApplicationsRequest()
                .withPage(1)
                .withOrderBy(CREATED_AT)
                .withName("test-name");

        this.applications.list(request).subscribe(new ExpectedExceptionSubscriber());
    }

    @Test
    public void listInvalidRequest() {
        this.applications.list(new ListApplicationsRequest().withPage(-1)).subscribe(new ExpectedExceptionSubscriber());
    }

    @Test
    public void start() {
        this.mockServer
                .expect(method(PUT))
                .andExpect(requestTo("https://api.run.pivotal.io/v3/apps/test-id/start"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v3/apps/PUT_{id}_start_response.json"))
                        .contentType(APPLICATION_JSON));

        StartApplicationRequest request = new StartApplicationRequest()
                .withId("test-id");

        Streams.wrap(this.applications.start(request)).consume(response -> {
            assertNull(response.getBuildpack());
            assertEquals("2015-07-27T22:43:15Z", response.getCreatedAt());
            assertEquals("STARTED", response.getDesiredState());
            assertEquals(Collections.emptyMap(), response.getEnvironmentVariables());
            assertEquals("guid-40460094-d035-4663-b58c-cdf4c802a2c6", response.getId());

            assertEquals(8, response.getLinks().size());
            assertNotNull(response.getLink("self"));
            assertNotNull(response.getLink("processes"));
            assertNotNull(response.getLink("packages"));
            assertNotNull(response.getLink("space"));
            assertNotNull(response.getLink("droplet"));
            assertNotNull(response.getLink("start"));
            assertNotNull(response.getLink("stop"));
            assertNotNull(response.getLink("assign_current_droplet"));

            assertEquals("original_name", response.getName());
            assertEquals(Integer.valueOf(0), response.getTotalDesiredInstances());
            assertEquals("2015-07-27T22:43:15Z", response.getUpdatedAt());
            this.mockServer.verify();
        });
    }

    @Test
    public void startError() {
        this.mockServer
                .expect(method(PUT))
                .andExpect(requestTo("https://api.run.pivotal.io/v3/apps/test-id/start"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        StartApplicationRequest request = new StartApplicationRequest()
                .withId("test-id");

        this.applications.start(request).subscribe(new ExpectedExceptionSubscriber());
    }

    @Test
    public void startInvalidRequest() {
        this.applications.start(new StartApplicationRequest()).subscribe(new ExpectedExceptionSubscriber());
    }

    @Test
    public void update() throws IOException {
        this.mockServer
                .expect(method(PATCH))
                .andExpect(requestTo("https://api.run.pivotal.io/v3/apps/test-id"))
                .andExpect(jsonPayload(new ClassPathResource("v3/apps/PATCH_{id}_request.json")))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v3/apps/PATCH_{id}_response.json"))
                        .contentType(APPLICATION_JSON));

        Map<String, String> environment_variables = new HashMap<>();
        environment_variables.put("MY_ENV_VAR", "foobar");
        environment_variables.put("FOOBAR", "MY_ENV_VAR");

        UpdateApplicationRequest request = new UpdateApplicationRequest()
                .withName("new_name")
                .withEnvironmentVariables(environment_variables)
                .withBuildpack("http://gitwheel.org/my-app")
                .withId("test-id");

        Streams.wrap(this.applications.update(request)).consume(response -> {
            assertEquals("http://gitwheel.org/my-app", response.getBuildpack());
            assertEquals("2015-07-27T22:43:14Z", response.getCreatedAt());
            assertEquals("2015-07-27T22:43:14Z", response.getUpdatedAt());
            assertEquals("STOPPED", response.getDesiredState());
            assertEquals(environment_variables, response.getEnvironmentVariables());
            assertEquals("guid-a7b667e9-2358-4f51-9b1d-92a74beaa30a", response.getId());

            assertEquals(7, response.getLinks().size());
            assertNotNull(response.getLink("self"));
            assertNotNull(response.getLink("processes"));
            assertNotNull(response.getLink("packages"));
            assertNotNull(response.getLink("space"));
            assertNotNull(response.getLink("start"));
            assertNotNull(response.getLink("stop"));
            assertNotNull(response.getLink("assign_current_droplet"));

            assertEquals("new_name", response.getName());
            assertEquals(Integer.valueOf(0), response.getTotalDesiredInstances());
            this.mockServer.verify();
        });
    }

    @Test
    public void updateError() throws IOException {
        this.mockServer
                .expect(method(PATCH))
                .andExpect(requestTo("https://api.run.pivotal.io/v3/apps"))
                .andExpect(jsonPayload(new ClassPathResource("v3/apps/PATCH_{id}_request.json")))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        Map<String, String> environment_variables = new HashMap<>();
        environment_variables.put("MY_ENV_VAR", "foobar");
        environment_variables.put("FOOBAR", "MY_ENV_VAR");

        UpdateApplicationRequest request = new UpdateApplicationRequest()
                .withName("new_name")
                .withEnvironmentVariables(environment_variables)
                .withBuildpack("http://gitwheel.org/my-app")
                .withId("test-id");

        this.applications.update(request).subscribe(new ExpectedExceptionSubscriber());
    }

    @Test
    public void updateInvalidRequest() throws Throwable {
        this.applications.update(new UpdateApplicationRequest()).subscribe(new ExpectedExceptionSubscriber());
    }

}

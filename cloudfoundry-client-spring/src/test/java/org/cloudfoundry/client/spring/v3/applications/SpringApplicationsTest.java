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

import org.cloudfoundry.client.RequestValidationException;
import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v3.applications.AssignApplicationDropletRequest;
import org.cloudfoundry.client.v3.applications.AssignApplicationDropletResponse;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v3.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v3.applications.DeleteApplicationResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationPackagesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationPackagesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationProcessesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationProcessesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v3.applications.ScaleApplicationRequest;
import org.cloudfoundry.client.v3.applications.ScaleApplicationResponse;
import org.cloudfoundry.client.v3.applications.StartApplicationRequest;
import org.cloudfoundry.client.v3.applications.StartApplicationResponse;
import org.cloudfoundry.client.v3.applications.StopApplicationRequest;
import org.cloudfoundry.client.v3.applications.StopApplicationResponse;
import org.cloudfoundry.client.v3.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationResponse;
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
    public void assignDroplet() {
        this.mockServer
                .expect(method(PUT))
                .andExpect(requestTo("https://api.run.pivotal.io/v3/apps/test-id/current_droplet"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v3/apps/PUT_{id}_current_droplet_response.json"))
                        .contentType(APPLICATION_JSON));

        AssignApplicationDropletRequest request = new AssignApplicationDropletRequest()
                .withDropletId("guid-3b5793e7-f6c8-40cb-a8d8-07080280da83")
                .withId("test-id");

        AssignApplicationDropletResponse response = Streams.wrap(this.applications.assignDroplet(request)).next().get();

        assertNull(response.getBuildpack());
        assertEquals("2015-07-27T22:43:15Z", response.getCreatedAt());
        assertEquals("STOPPED", response.getDesiredState());
        assertEquals(Collections.emptyMap(), response.getEnvironmentVariables());
        assertEquals("guid-9f33c9e4-4b31-4dda-b188-adf197dbea0a", response.getId());

        assertEquals(8, response.getLinks().size());
        assertNotNull(response.getLink("self"));
        assertNotNull(response.getLink("processes"));
        assertNotNull(response.getLink("packages"));
        assertNotNull(response.getLink("space"));
        assertNotNull(response.getLink("droplet"));
        assertNotNull(response.getLink("start"));
        assertNotNull(response.getLink("stop"));
        assertNotNull(response.getLink("assign_current_droplet"));

        assertEquals("name1", response.getName());
        assertEquals(Integer.valueOf(1), response.getTotalDesiredInstances());
        assertEquals("2015-07-27T22:43:15Z", response.getUpdatedAt());
        this.mockServer.verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void assignDropletError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v3/apps/test-id/current_droplet"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        AssignApplicationDropletRequest request = new AssignApplicationDropletRequest()
                .withDropletId("guid-3b5793e7-f6c8-40cb-a8d8-07080280da83")
                .withId("test-id");

        Streams.wrap(this.applications.assignDroplet(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void assignDropletInvalidRequest() {
        Streams.wrap(this.applications.assignDroplet(new AssignApplicationDropletRequest())).next().get();
    }

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

        CreateApplicationResponse response = Streams.wrap(this.applications.create(request)).next().get();

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

        Streams.wrap(this.applications.create(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void createInvalidRequest() throws Throwable {
        Streams.wrap(this.applications.create(new CreateApplicationRequest())).next().get();
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

        GetApplicationResponse response = Streams.wrap(this.applications.get(request)).next().get();

        assertEquals("name-371", response.getBuildpack());
        assertEquals("2015-07-27T22:43:15Z", response.getCreatedAt());
        assertEquals("STOPPED", response.getDesiredState());
        assertEquals(Collections.singletonMap("unicorn", "horn"), response.getEnvironmentVariables());
        assertEquals("guid-e23c9834-9c4a-4397-be7d-e0fb686cb646", response.getId());

        assertEquals(8, response.getLinks().size());
        assertNotNull(response.getLink("self"));
        assertNotNull(response.getLink("processes"));
        assertNotNull(response.getLink("packages"));
        assertNotNull(response.getLink("droplet"));
        assertNotNull(response.getLink("space"));
        assertNotNull(response.getLink("start"));
        assertNotNull(response.getLink("stop"));
        assertNotNull(response.getLink("assign_current_droplet"));

        assertEquals("my_app", response.getName());
        assertEquals(Integer.valueOf(3), response.getTotalDesiredInstances());
        assertNull(response.getUpdatedAt());
        this.mockServer.verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void getError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v3/apps/test-id"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        GetApplicationRequest request = new GetApplicationRequest()
                .withId("test-id");

        Streams.wrap(this.applications.get(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void getInvalidRequest() {
        Streams.wrap(this.applications.get(new GetApplicationRequest())).next().get();
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

        GetApplicationEnvironmentResponse response = Streams.wrap(this.applications.getEnvironment(request))
                .next().get();

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
    }

    @Test(expected = CloudFoundryException.class)
    public void getEnvironmentError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v3/apps/test-id/env"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        GetApplicationEnvironmentRequest request = new GetApplicationEnvironmentRequest()
                .withId("test-id");

        Streams.wrap(this.applications.getEnvironment(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void getEnvironmentInvalidRequest() {
        Streams.wrap(this.applications.getEnvironment(new GetApplicationEnvironmentRequest())).next().get();
    }

    @Test
    public void getProcess() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v3/apps/test-id/processes/web"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v3/apps/GET_{id}_processes_{type}_response.json"))
                        .contentType(APPLICATION_JSON));

        GetApplicationProcessRequest request = new GetApplicationProcessRequest()
                .withId("test-id")
                .withType("web");

        GetApplicationProcessResponse response = Streams.wrap(this.applications.getProcess(request))
                .next().get();

        assertEquals("2015-07-27T22:43:29Z", response.getCreatedAt());
        assertNull(response.getCommand());
        assertEquals(Integer.valueOf(1024), response.getDiskInMb());
        assertEquals("32f64d22-ab45-4a9b-ba93-2b3b160f3750", response.getId());
        assertEquals(Integer.valueOf(1), response.getInstances());

        assertEquals(4, response.getLinks().size());
        assertNotNull(response.getLink("self"));
        assertNotNull(response.getLink("scale"));
        assertNotNull(response.getLink("app"));
        assertNotNull(response.getLink("space"));

        assertEquals(Integer.valueOf(1024), response.getMemoryInMb());
        assertEquals("web", response.getType());
        assertEquals("2015-07-27T22:43:29Z", response.getUpdatedAt());
        this.mockServer.verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void getProcessError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v3/apps/test-id/processes/web"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        GetApplicationProcessRequest request = new GetApplicationProcessRequest()
                .withId("test-id")
                .withType("web");

        Streams.wrap(this.applications.getProcess(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void getProcessInvalidRequest() {
        Streams.wrap(this.applications.getProcess(new GetApplicationProcessRequest())).next().get();
    }

    @Test
    public void delete() {
        this.mockServer
                .expect(method(DELETE))
                .andExpect(requestTo("https://api.run.pivotal.io/v3/apps/test-id"))
                .andRespond(withStatus(OK));

        DeleteApplicationRequest request = new DeleteApplicationRequest()
                .withId("test-id");

        DeleteApplicationResponse response = Streams.wrap(this.applications.delete(request)).next().get();

        this.mockServer.verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void deleteError() {
        this.mockServer
                .expect(method(DELETE))
                .andExpect(requestTo("https://api.run.pivotal.io/v3/apps/test-id"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        DeleteApplicationRequest request = new DeleteApplicationRequest()
                .withId("test-id");

        Streams.wrap(this.applications.delete(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void deleteInvalidRequest() {
        Streams.wrap(this.applications.delete(new DeleteApplicationRequest())).next().get();
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

        ListApplicationsResponse response = Streams.wrap(this.applications.list(request)).next().get();

        ListApplicationsResponse.Resource resource = response.getResources().get(0);

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
    }

    @Test(expected = CloudFoundryException.class)
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

        Streams.wrap(this.applications.list(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listInvalidRequest() {
        Streams.wrap(this.applications.list(new ListApplicationsRequest().withPage(-1))).next().get();
    }

    @Test
    public void listPackages() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v3/apps/test-id/packages"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v3/apps/GET_{id}_packages_response.json"))
                        .contentType(APPLICATION_JSON));

        ListApplicationPackagesRequest request = new ListApplicationPackagesRequest()
                .withPage(1)
                .withId("test-id");

        ListApplicationPackagesResponse response = Streams.wrap(this.applications.listPackages(request)).next().get();

        ListApplicationPackagesResponse.Resource resource = response.getResources().get(0);

        assertEquals("2015-07-27T22:43:34Z", resource.getCreatedAt());
        assertNull(resource.getError());
        assertEquals("sha1", resource.getHash().getType());
        assertNull(resource.getHash().getValue());
        assertEquals("guid-3d792a08-e415-4f9e-912b-2a8485db781a", resource.getId());

        assertEquals(5, resource.getLinks().size());
        assertNotNull(resource.getLink("self"));
        assertNotNull(resource.getLink("upload"));
        assertNotNull(resource.getLink("download"));
        assertNotNull(resource.getLink("stage"));
        assertNotNull(resource.getLink("app"));

        assertEquals("AWAITING_UPLOAD", resource.getState());
        assertEquals("bits", resource.getType());
        assertNull(resource.getUpdatedAt());
        assertNull(resource.getUrl());
        this.mockServer.verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listPackagesError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v3/apps/test-id/packages"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        ListApplicationPackagesRequest request = new ListApplicationPackagesRequest()
                .withPage(1)
                .withId("test-id");
        Streams.wrap(this.applications.listPackages(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listPackagesInvalidRequest() {
        Streams.wrap(this.applications.listPackages(new ListApplicationPackagesRequest())).next().get();
    }

    @Test
    public void listProcesses() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v3/apps/test-id/processes"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v3/apps/GET_{id}_processes_response.json"))
                        .contentType(APPLICATION_JSON));

        ListApplicationProcessesRequest request = new ListApplicationProcessesRequest()
                .withPage(1)
                .withId("test-id");

        ListApplicationProcessesResponse response = Streams.wrap(this.applications.listProcesses(request)).next().get();

        ListApplicationProcessesResponse.Resource resource = response.getResources().get(0);

        assertEquals("2015-07-27T22:43:29Z", resource.getCreatedAt());
        assertNull(resource.getCommand());
        assertEquals(Integer.valueOf(1024), resource.getDiskInMb());
        assertEquals("38fcaafa-5356-4f74-af10-dc70da151993", resource.getId());
        assertEquals(Integer.valueOf(1), resource.getInstances());

        assertEquals(4, resource.getLinks().size());
        assertNotNull(resource.getLink("self"));
        assertNotNull(resource.getLink("scale"));
        assertNotNull(resource.getLink("app"));
        assertNotNull(resource.getLink("space"));

        assertEquals(Integer.valueOf(1024), resource.getMemoryInMb());
        assertEquals("web", resource.getType());
        assertEquals("2015-07-27T22:43:29Z", resource.getUpdatedAt());
        this.mockServer.verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listProcessesError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v3/apps/test-id/processes"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        ListApplicationProcessesRequest request = new ListApplicationProcessesRequest()
                .withPage(1)
                .withId("test-id");
        Streams.wrap(this.applications.listProcesses(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listProcessesInvalidRequest() {
        Streams.wrap(this.applications.listProcesses(new ListApplicationProcessesRequest())).next().get();
    }

    @Test
    public void scale() {
        this.mockServer
                .expect(method(PUT))
                .andExpect(requestTo("https://api.run.pivotal.io/v3/apps/test-id/processes/web/scale"))
                .andExpect(jsonPayload(new ClassPathResource("v3/apps/PUT_{id}_processes_{type}_scale_request.json")))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v3/apps/PUT_{id}_processes_{type}_scale_response.json"))
                        .contentType(APPLICATION_JSON));

        ScaleApplicationRequest request = new ScaleApplicationRequest()
                .withDiskInMb(100)
                .withId("test-id")
                .withInstances(3)
                .withMemoryInMb(100)
                .withType("web");

        ScaleApplicationResponse response = Streams.wrap(this.applications.scale(request)).next().get();

        assertEquals("2015-07-27T22:43:29Z", response.getCreatedAt());
        assertEquals(Integer.valueOf(100), response.getDiskInMb());
        assertEquals("edc2dffe-9f0d-416f-a712-890d56de8bae", response.getId());
        assertEquals(Integer.valueOf(3), response.getInstances());

        assertEquals(4, response.getLinks().size());
        assertNotNull(response.getLink("self"));
        assertNotNull(response.getLink("scale"));
        assertNotNull(response.getLink("app"));
        assertNotNull(response.getLink("space"));

        assertEquals(Integer.valueOf(100), response.getMemoryInMb());
        assertEquals("web", response.getType());
        assertEquals("2015-07-27T22:43:29Z", response.getUpdatedAt());
        this.mockServer.verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void scaleError() {
        this.mockServer
                .expect(requestTo("https://api.run.pivotal.io/v3/apps/test-id/processes/web/scale"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        ScaleApplicationRequest request = new ScaleApplicationRequest()
                .withDiskInMb(100)
                .withId("test-id")
                .withInstances(3)
                .withMemoryInMb(100)
                .withType("web");

        Streams.wrap(this.applications.scale(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void scaleInvalidRequest() {
        Streams.wrap(this.applications.scale(new ScaleApplicationRequest())).next().get();
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

        StartApplicationResponse response = Streams.wrap(this.applications.start(request)).next().get();

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
    }

    @Test(expected = CloudFoundryException.class)
    public void startError() {
        this.mockServer
                .expect(method(PUT))
                .andExpect(requestTo("https://api.run.pivotal.io/v3/apps/test-id/start"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        StartApplicationRequest request = new StartApplicationRequest()
                .withId("test-id");

        Streams.wrap(this.applications.start(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void startInvalidRequest() {
        Streams.wrap(this.applications.start(new StartApplicationRequest())).next().get();
    }

    @Test
    public void stop() {
        this.mockServer
                .expect(method(PUT))
                .andExpect(requestTo("https://api.run.pivotal.io/v3/apps/test-id/stop"))
                .andRespond(withStatus(OK)
                        .body(new ClassPathResource("v3/apps/PUT_{id}_stop_response.json"))
                        .contentType(APPLICATION_JSON));

        StopApplicationRequest request = new StopApplicationRequest()
                .withId("test-id");

        StopApplicationResponse response = Streams.wrap(this.applications.stop(request)).next().get();

        assertNull(response.getBuildpack());
        assertEquals("2015-07-27T22:43:15Z", response.getCreatedAt());
        assertEquals("STOPPED", response.getDesiredState());
        assertEquals(Collections.emptyMap(), response.getEnvironmentVariables());
        assertEquals("guid-be4e4357-5a9d-48fc-ae37-821f48c1ace0", response.getId());

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
    }

    @Test(expected = CloudFoundryException.class)
    public void stopError() {
        this.mockServer
                .expect(method(PUT))
                .andExpect(requestTo("https://api.run.pivotal.io/v3/apps/test-id/stop"))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY)
                        .body(new ClassPathResource("v2/error_response.json"))
                        .contentType(APPLICATION_JSON));

        StopApplicationRequest request = new StopApplicationRequest()
                .withId("test-id");

        Streams.wrap(this.applications.stop(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void stopInvalidRequest() {
        Streams.wrap(this.applications.stop(new StopApplicationRequest())).next().get();
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

        UpdateApplicationResponse response = Streams.wrap(this.applications.update(request)).next().get();

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
    }

    @Test(expected = CloudFoundryException.class)
    public void updateError() throws IOException {
        this.mockServer
                .expect(method(PATCH))
                .andExpect(requestTo("https://api.run.pivotal.io/v3/apps/test-id"))
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

        Streams.wrap(this.applications.update(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void updateInvalidRequest() throws Throwable {
        Streams.wrap(this.applications.update(new UpdateApplicationRequest())).next().get();
    }

}

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

package org.cloudfoundry.client.spring.v3.processes;

import org.cloudfoundry.client.RequestValidationException;
import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v3.processes.DeleteProcessInstanceRequest;
import org.cloudfoundry.client.v3.processes.GetProcessRequest;
import org.cloudfoundry.client.v3.processes.GetProcessResponse;
import org.cloudfoundry.client.v3.processes.ListProcessesRequest;
import org.cloudfoundry.client.v3.processes.ListProcessesResponse;
import org.cloudfoundry.client.v3.processes.ScaleProcessRequest;
import org.cloudfoundry.client.v3.processes.ScaleProcessResponse;
import org.cloudfoundry.client.v3.processes.UpdateProcessRequest;
import org.cloudfoundry.client.v3.processes.UpdateProcessResponse;
import org.junit.Test;
import reactor.rx.Streams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

public final class SpringProcessesTest extends AbstractRestTest {

    private final SpringProcesses processes = new SpringProcesses(this.restTemplate, this.root);

    @Test
    public void deleteInstance() {
        mockRequest(DELETE, "https://api.run.pivotal.io/v3/processes/test-id/instances/test-index",
                NO_CONTENT);

        DeleteProcessInstanceRequest request = new DeleteProcessInstanceRequest()
                .withId("test-id")
                .withIndex("test-index");

        Streams.wrap(this.processes.deleteInstance(request)).next().get();

        verifyMockServer();
    }

    @Test(expected = CloudFoundryException.class)
    public void deleteInstanceError() {
        mockRequest(DELETE, "https://api.run.pivotal.io/v3/processes/test-id/instances/test-index",
                UNPROCESSABLE_ENTITY, "v2/error_response.json");

        DeleteProcessInstanceRequest request = new DeleteProcessInstanceRequest()
                .withId("test-id")
                .withIndex("test-index");

        Streams.wrap(this.processes.deleteInstance(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void deleteInstanceInvalidRequest() {
        Streams.wrap(this.processes.deleteInstance(new DeleteProcessInstanceRequest())).next().get();
    }

    @Test
    public void get() {
        mockRequest(GET, "https://api.run.pivotal.io/v3/processes/test-id",
                OK, "v3/processes/GET_{id}_response.json");

        GetProcessRequest request = new GetProcessRequest()
                .withId("test-id");

        GetProcessResponse response = Streams.wrap(this.processes.get(request))
                .next().get();

        assertEquals("2015-07-27T22:43:31Z", response.getCreatedAt());
        assertNull(response.getCommand());
        assertEquals(Integer.valueOf(1024), response.getDiskInMb());
        assertEquals("07063514-b0ca-4e58-adbb-8e8bd7eebd64", response.getId());
        assertEquals(Integer.valueOf(1), response.getInstances());

        assertEquals(4, response.getLinks().size());
        assertNotNull(response.getLink("self"));
        assertNotNull(response.getLink("scale"));
        assertNotNull(response.getLink("app"));
        assertNotNull(response.getLink("space"));

        assertEquals(Integer.valueOf(1024), response.getMemoryInMb());
        assertEquals("web", response.getType());
        assertEquals("2015-07-27T22:43:31Z", response.getUpdatedAt());
        verifyMockServer();
    }

    @Test(expected = CloudFoundryException.class)
    public void getError() {
        mockRequest(GET, "https://api.run.pivotal.io/v3/processes/test-id",
                UNPROCESSABLE_ENTITY, "v2/error_response.json");

        GetProcessRequest request = new GetProcessRequest()
                .withId("test-id");

        Streams.wrap(this.processes.get(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void getInvalidRequest() {
        Streams.wrap(this.processes.get(new GetProcessRequest())).next().get();
    }

    @Test
    public void list() {
        mockRequest(GET, "https://api.run.pivotal.io/v3/processes?page=1&per_page=2",
                OK, "v3/processes/GET_response.json");

        ListProcessesRequest request = new ListProcessesRequest()
                .withPage(1)
                .withPerPage(2);

        ListProcessesResponse response = Streams.wrap(this.processes.list(request)).next().get();

        ListProcessesResponse.Resource resource = response.getResources().get(0);

        assertEquals("2015-07-27T22:43:31Z", resource.getCreatedAt());
        assertNull(resource.getCommand());
        assertEquals(Integer.valueOf(1024), resource.getDiskInMb());
        assertEquals("fdfa71c4-5e0e-4f68-adb6-82fc250cd233", resource.getId());
        assertEquals(Integer.valueOf(1), resource.getInstances());

        assertEquals(4, resource.getLinks().size());
        assertNotNull(resource.getLink("self"));
        assertNotNull(resource.getLink("scale"));
        assertNotNull(resource.getLink("app"));
        assertNotNull(resource.getLink("space"));

        assertEquals(Integer.valueOf(1024), resource.getMemoryInMb());
        assertEquals("web", resource.getType());
        assertEquals("2015-07-27T22:43:31Z", resource.getUpdatedAt());
        verifyMockServer();
    }

    @Test(expected = CloudFoundryException.class)
    public void listError() {
        mockRequest(GET, "https://api.run.pivotal.io/v3/processes?page=1&per_page=2",
                UNPROCESSABLE_ENTITY, "v2/error_response.json");

        ListProcessesRequest request = new ListProcessesRequest()
                .withPage(1)
                .withPerPage(2);

        Streams.wrap(this.processes.list(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listInvalidRequest() {
        Streams.wrap(this.processes.list(new ListProcessesRequest().withPage(0))).next().get();
    }

    @Test
    public void scale() {
        mockRequest(PUT, "https://api.run.pivotal.io/v3/processes/test-id/scale",
                "v3/processes/PUT_{id}_scale_request.json",
                OK, "v3/processes/PUT_{id}_scale_response.json");

        ScaleProcessRequest request = new ScaleProcessRequest()
                .withDiskInMb(100)
                .withId("test-id")
                .withInstances(3)
                .withMemoryInMb(100);

        ScaleProcessResponse response = Streams.wrap(this.processes.scale(request)).next().get();

        assertEquals("2015-07-27T22:43:31Z", response.getCreatedAt());
        assertEquals(Integer.valueOf(100), response.getDiskInMb());
        assertEquals("1dbdf1dc-ec61-4ade-96bf-4e148092b2e8", response.getId());
        assertEquals(Integer.valueOf(3), response.getInstances());

        assertEquals(4, response.getLinks().size());
        assertNotNull(response.getLink("self"));
        assertNotNull(response.getLink("scale"));
        assertNotNull(response.getLink("app"));
        assertNotNull(response.getLink("space"));

        assertEquals(Integer.valueOf(100), response.getMemoryInMb());
        assertEquals("web", response.getType());
        assertEquals("2015-07-27T22:43:32Z", response.getUpdatedAt());
        verifyMockServer();
    }

    @Test(expected = CloudFoundryException.class)
    public void scaleError() {
        mockRequest(PUT, "https://api.run.pivotal.io/v3/processes/test-id/scale",
                "v3/processes/PUT_{id}_scale_request.json",
                UNPROCESSABLE_ENTITY, "v2/error_response.json");

        ScaleProcessRequest request = new ScaleProcessRequest()
                .withDiskInMb(100)
                .withId("test-id")
                .withInstances(3)
                .withMemoryInMb(100);

        Streams.wrap(this.processes.scale(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void scaleInvalidRequest() {
        Streams.wrap(this.processes.scale(new ScaleProcessRequest())).next().get();
    }

    @Test
    public void update() {
        mockRequest(PATCH, "https://api.run.pivotal.io/v3/processes/test-id", "v3/processes/PATCH_{id}_request.json",
                OK, "v3/processes/PATCH_{id}_response.json");

        UpdateProcessRequest request = new UpdateProcessRequest()
                .withId("test-id")
                .withCommand("test-command");

        UpdateProcessResponse response = Streams.wrap(this.processes.update(request))
                .next().get();

        assertEquals("2015-07-27T22:43:32Z", response.getCreatedAt());
        assertEquals("X", response.getCommand());
        assertEquals(Integer.valueOf(1024), response.getDiskInMb());
        assertEquals("92d5b770-32ed-4f5c-8def-b1f2348447fb", response.getId());
        assertEquals(Integer.valueOf(1), response.getInstances());

        assertEquals(4, response.getLinks().size());
        assertNotNull(response.getLink("self"));
        assertNotNull(response.getLink("scale"));
        assertNotNull(response.getLink("app"));
        assertNotNull(response.getLink("space"));

        assertEquals(Integer.valueOf(1024), response.getMemoryInMb());
        assertEquals("web", response.getType());
        assertEquals("2015-07-27T22:43:32Z", response.getUpdatedAt());
        verifyMockServer();
    }

    @Test(expected = CloudFoundryException.class)
    public void updateError() {
        mockRequest(PATCH, "https://api.run.pivotal.io/v3/processes/test-id", "v3/processes/PATCH_{id}_request.json",
                UNPROCESSABLE_ENTITY, "v2/error_response.json");

        UpdateProcessRequest request = new UpdateProcessRequest()
                .withId("test-id")
                .withCommand("test-command");

        Streams.wrap(this.processes.update(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void updateInvalidRequest() {
        Streams.wrap(this.processes.update(new UpdateProcessRequest())).next().get();
    }

}

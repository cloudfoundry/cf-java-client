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

package org.cloudfoundry.client.spring.v3.droplets;

import org.cloudfoundry.client.RequestValidationException;
import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v3.droplets.DeleteDropletRequest;
import org.cloudfoundry.client.v3.droplets.GetDropletRequest;
import org.cloudfoundry.client.v3.droplets.GetDropletResponse;
import org.cloudfoundry.client.v3.droplets.ListDropletsRequest;
import org.cloudfoundry.client.v3.droplets.ListDropletsResponse;
import org.junit.Test;
import reactor.rx.Streams;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public final class SpringDropletsTest extends AbstractRestTest {

    private final SpringDroplets droplets = new SpringDroplets(this.restTemplate, this.root);

    @Test
    public void delete() {
        mockRequest(new RequestContext()
                .method(DELETE).path("/v3/droplets/test-id")
                .status(NO_CONTENT));

        DeleteDropletRequest request = new DeleteDropletRequest()
                .withId("test-id");

        Streams.wrap(this.droplets.delete(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void deleteError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("/v3/droplets/test-id")
                .errorResponse());

        DeleteDropletRequest request = new DeleteDropletRequest()
                .withId("test-id");

        Streams.wrap(this.droplets.delete(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void deleteInvalidRequest() {
        Streams.wrap(this.droplets.delete(new DeleteDropletRequest())).next().get();
    }

    @Test
    public void get() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/droplets/test-id")
                .status(OK)
                .responsePayload("v3/droplets/GET_{id}_response.json"));

        GetDropletRequest request = new GetDropletRequest()
                .withId("test-id");

        GetDropletResponse response = Streams.wrap(this.droplets.get(request)).next().get();

        assertEquals("http://github.com/myorg/awesome-buildpack", response.getBuildpack());
        assertEquals("2015-07-27T22:43:16Z", response.getCreatedAt());
        assertEquals(getExpectedEnvironmentVariables(), response.getEnvironmentVariables());
        assertNull(response.getError());
        assertEquals("sha1", response.getHash().getType());
        assertNull(response.getHash().getValue());
        assertEquals("whatuuid", response.getId());
        assertNull(response.getProcfile());
        assertEquals("PENDING", response.getState());
        assertNull(response.getUpdatedAt());
        validateLinks(response, "self", "package", "app");
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void getError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/droplets/test-id")
                .errorResponse());

        GetDropletRequest request = new GetDropletRequest()
                .withId("test-id");

        Streams.wrap(this.droplets.get(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void getInvalidRequest() {
        Streams.wrap(this.droplets.get(new GetDropletRequest())).next().get();
    }

    @Test
    public void list() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/droplets")
                .status(OK)
                .responsePayload("v3/droplets/GET_response.json"));

        ListDropletsRequest request = new ListDropletsRequest();
        ListDropletsResponse response = Streams.wrap(this.droplets.list(request)).next().get();

        Map<String, Object> environmentVariables = new HashMap<>();
        environmentVariables.put("yuu", "huuu");

        ListDropletsResponse.Resource resource = response.getResources().get(0);

        assertEquals("name-2141", resource.getBuildpack());
        assertEquals("2015-07-27T22:43:30Z", resource.getCreatedAt());
        assertEquals(environmentVariables, resource.getEnvironmentVariables());
        assertNull(resource.getError());
        assertEquals("sha1", resource.getHash().getType());
        assertNull(resource.getHash().getValue());
        assertEquals("guid-5be1225e-5f49-499a-87db-bcdff646eed6", resource.getId());
        assertNull(resource.getProcfile());
        assertEquals("STAGING", resource.getState());
        assertNull(resource.getUpdatedAt());
        validateLinks(resource, "self", "package", "app", "buildpack");
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/droplets")
                .errorResponse());

        ListDropletsRequest request = new ListDropletsRequest();

        Streams.wrap(this.droplets.list(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listInvalidRequest() {
        Streams.wrap(this.droplets.list(new ListDropletsRequest().withPage(0))).next().get();
    }

    public Map<String, Object> getExpectedEnvironmentVariables() {
        Map<String, Object> environmentVariables = new HashMap<>();

        environmentVariables.put("CUSTOM_ENV_VAR", "hello");

        Map<String, Object> limits = new HashMap<>();
        limits.put("mem", 1024);
        limits.put("disk", 4096);
        limits.put("fds", 16384);

        Map<String, Object> vcapApplication = new HashMap<>();
        vcapApplication.put("limits", limits);
        vcapApplication.put("application_id", "guid-a174c559-deb6-4db7-b3ef-2a5d778d8866");
        vcapApplication.put("application_version", "whatuuid");
        vcapApplication.put("application_name", "name-454");
        vcapApplication.put("application_uris", Collections.emptyList());
        vcapApplication.put("version", "whatuuid");
        vcapApplication.put("name", "name-454");
        vcapApplication.put("space_name", "name-451");
        vcapApplication.put("space_id", "a9573106-2d65-45bb-9a93-55bfe029be33");
        vcapApplication.put("uris", Collections.emptyList());
        vcapApplication.put("users", null);

        environmentVariables.put("VCAP_APPLICATION", vcapApplication);
        environmentVariables.put("CF_STACK", "cflinuxfs2");

        return environmentVariables;
    }

}

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
import org.cloudfoundry.client.v3.Hash;
import org.cloudfoundry.client.v3.Link;
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

import static org.cloudfoundry.client.v3.PaginatedResponse.Pagination;
import static org.cloudfoundry.client.v3.droplets.ListDropletsResponse.Resource;
import static org.cloudfoundry.client.v3.droplets.ListDropletsResponse.builder;
import static org.junit.Assert.assertEquals;
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

        DeleteDropletRequest request = DeleteDropletRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.droplets.delete(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void deleteError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("/v3/droplets/test-id")
                .errorResponse());

        DeleteDropletRequest request = DeleteDropletRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.droplets.delete(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void deleteInvalidRequest() {
        DeleteDropletRequest request = DeleteDropletRequest.builder()
                .build();

        Streams.wrap(this.droplets.delete(request)).next().get();
    }

    @Test
    public void get() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/droplets/test-id")
                .status(OK)
                .responsePayload("v3/droplets/GET_{id}_response.json"));

        GetDropletRequest request = GetDropletRequest.builder()
                .id("test-id")
                .build();

        GetDropletResponse expected = GetDropletResponse.builder()
                .id("whatuuid")
                .state("PENDING")
                .hash(Hash.builder()
                        .type("sha1")
                        .build())
                .buildpack("http://github.com/myorg/awesome-buildpack")
                .environmentVariable("CUSTOM_ENV_VAR", "hello")
                .environmentVariable("VCAP_APPLICATION", vcapApplication())
                .environmentVariable("CF_STACK", "cflinuxfs2")
                .createdAt("2015-07-27T22:43:16Z")
                .link("self", Link.builder()
                        .href("/v3/droplets/whatuuid")
                        .build())
                .link("package", Link.builder()
                        .href("/v3/packages/guid-c89ed121-a2f1-4f78-9d98-e3b607a07d09")
                        .build())
                .link("app", Link.builder()
                        .href("/v3/apps/guid-a174c559-deb6-4db7-b3ef-2a5d778d8866")
                        .build())
                .build();

        GetDropletResponse actual = Streams.wrap(this.droplets.get(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void getError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/droplets/test-id")
                .errorResponse());

        GetDropletRequest request = GetDropletRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.droplets.get(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void getInvalidRequest() {
        GetDropletRequest request = GetDropletRequest.builder()
                .build();

        Streams.wrap(this.droplets.get(request)).next().get();
    }

    @Test
    public void list() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/droplets")
                .status(OK)
                .responsePayload("v3/droplets/GET_response.json"));

        ListDropletsRequest request = ListDropletsRequest.builder()
                .build();

        ListDropletsResponse expected = builder()
                .pagination(Pagination.builder()
                        .totalResults(2)
                        .first(Link.builder()
                                .href("/v3/droplets?page=1&per_page=2")
                                .build())
                        .last(Link.builder()
                                .href("/v3/droplets?page=1&per_page=2")
                                .build())
                        .build())
                .resource(Resource.builder()
                        .id("guid-5be1225e-5f49-499a-87db-bcdff646eed6")
                        .state("STAGING")
                        .hash(Hash.builder()
                                .type("sha1")
                                .build())
                        .buildpack("name-2141")
                        .environmentVariable("yuu", "huuu")
                        .createdAt("2015-07-27T22:43:30Z")
                        .link("self", Link.builder()
                                .href("/v3/droplets/guid-5be1225e-5f49-499a-87db-bcdff646eed6")
                                .build())
                        .link("package", Link.builder()
                                .href("/v3/packages/guid-09037508-293d-4923-9552-12fe9cda5f98")
                                .build())
                        .link("app", Link.builder()
                                .href("/v3/apps/guid-d686e53a-9a5b-4bad-b1f5-0fe264b2b0c0")
                                .build())
                        .link("buildpack", Link.builder()
                                .href("/v2/buildpacks/b0179650-8a4f-4b3a-b485-255118b0c619")
                                .build())
                        .build())
                .resource(Resource.builder()
                        .id("guid-74a54cf4-99a5-40b1-8f81-74377c36240d")
                        .state("STAGED")
                        .hash(Hash.builder()
                                .type("sha1")
                                .value("my-hash")
                                .build())
                        .buildpack("https://github.com/cloudfoundry/my-buildpack.git")
                        .createdAt("2015-07-27T22:43:30Z")
                        .link("self", Link.builder()
                                .href("/v3/droplets/guid-74a54cf4-99a5-40b1-8f81-74377c36240d")
                                .build())
                        .link("package", Link.builder()
                                .href("/v3/packages/guid-09037508-293d-4923-9552-12fe9cda5f98")
                                .build())
                        .link("app", Link.builder()
                                .href("/v3/apps/guid-d686e53a-9a5b-4bad-b1f5-0fe264b2b0c0")
                                .build())
                        .link("assign_current_droplet", Link.builder()
                                .href("/v3/apps/guid-d686e53a-9a5b-4bad-b1f5-0fe264b2b0c0/current_droplet")
                                .method("PUT")
                                .build())
                        .build())
                .build();

        ListDropletsResponse actual = Streams.wrap(this.droplets.list(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/droplets")
                .errorResponse());

        ListDropletsRequest request = ListDropletsRequest.builder()
                .build();

        Streams.wrap(this.droplets.list(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listInvalidRequest() {
        ListDropletsRequest request = ListDropletsRequest.builder().page(0)
                .build();

        Streams.wrap(this.droplets.list(request)).next().get();
    }

    private Map<String, Object> vcapApplication() {
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

        return vcapApplication;
    }

}

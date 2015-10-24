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
import org.cloudfoundry.client.v3.Hash;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.applications.AssignApplicationDropletRequest;
import org.cloudfoundry.client.v3.applications.AssignApplicationDropletResponse;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v3.applications.DeleteApplicationInstanceRequest;
import org.cloudfoundry.client.v3.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationDropletsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationDropletsResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationPackagesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationPackagesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationProcessesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationProcessesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationRoutesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationRoutesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v3.applications.MapApplicationRouteRequest;
import org.cloudfoundry.client.v3.applications.ScaleApplicationRequest;
import org.cloudfoundry.client.v3.applications.ScaleApplicationResponse;
import org.cloudfoundry.client.v3.applications.StartApplicationRequest;
import org.cloudfoundry.client.v3.applications.StartApplicationResponse;
import org.cloudfoundry.client.v3.applications.StopApplicationRequest;
import org.cloudfoundry.client.v3.applications.StopApplicationResponse;
import org.cloudfoundry.client.v3.applications.UnmapApplicationRouteRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationResponse;
import org.junit.Test;
import reactor.rx.Streams;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.cloudfoundry.client.v3.PaginatedAndSortedRequest.OrderBy.CREATED_AT;
import static org.cloudfoundry.client.v3.PaginatedAndSortedRequest.OrderDirection.ASC;
import static org.cloudfoundry.client.v3.PaginatedResponse.Pagination;
import static org.cloudfoundry.client.v3.applications.ListApplicationPackagesResponse.Resource;
import static org.cloudfoundry.client.v3.applications.ListApplicationPackagesResponse.builder;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public final class SpringApplicationsTest extends AbstractRestTest {

    private final SpringApplications applications = new SpringApplications(this.restTemplate, this.root);

    @Test
    public void assignDroplet() {
        mockRequest(new RequestContext()
                .method(PUT).path("v3/apps/test-id/current_droplet")
                .requestPayload("v3/apps/PUT_{id}_current_droplet_request.json")
                .status(OK)
                .responsePayload("v3/apps/PUT_{id}_current_droplet_response.json"));

        AssignApplicationDropletRequest request = AssignApplicationDropletRequest.builder()
                .dropletId("guid-3b5793e7-f6c8-40cb-a8d8-07080280da83")
                .id("test-id")
                .build();

        AssignApplicationDropletResponse expected = AssignApplicationDropletResponse.builder()
                .id("guid-9f33c9e4-4b31-4dda-b188-adf197dbea0a")
                .name("name1")
                .desiredState("STOPPED")
                .totalDesiredInstances(1)
                .createdAt("2015-07-27T22:43:15Z")
                .updatedAt("2015-07-27T22:43:15Z")
                .link("self", Link.builder()
                        .href("/v3/apps/guid-9f33c9e4-4b31-4dda-b188-adf197dbea0a")
                        .build())
                .link("processes", Link.builder()
                        .href("/v3/apps/guid-9f33c9e4-4b31-4dda-b188-adf197dbea0a/processes")
                        .build())
                .link("packages", Link.builder()
                        .href("/v3/apps/guid-9f33c9e4-4b31-4dda-b188-adf197dbea0a/packages")
                        .build())
                .link("space", Link.builder()
                        .href("/v2/spaces/7f5329b4-ab5b-404f-a0dd-6dbdedb6f742")
                        .build())
                .link("droplet", Link.builder()
                        .href("/v3/droplets/guid-3b5793e7-f6c8-40cb-a8d8-07080280da83")
                        .build())
                .link("start", Link.builder()
                        .href("/v3/apps/guid-9f33c9e4-4b31-4dda-b188-adf197dbea0a/start")
                        .method("PUT")
                        .build())
                .link("stop", Link.builder()
                        .href("/v3/apps/guid-9f33c9e4-4b31-4dda-b188-adf197dbea0a/stop")
                        .method("PUT")
                        .build())
                .link("assign_current_droplet", Link.builder()
                        .href("/v3/apps/guid-9f33c9e4-4b31-4dda-b188-adf197dbea0a/current_droplet")
                        .method("PUT")
                        .build())
                .build();

        AssignApplicationDropletResponse actual = Streams.wrap(this.applications.assignDroplet(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void assignDropletError() {
        mockRequest(new RequestContext()
                        .method(PUT).path("v3/apps/test-id/current_droplet")
                        .requestPayload("v3/apps/PUT_{id}_current_droplet_request.json")
                        .errorResponse()
        );

        AssignApplicationDropletRequest request = AssignApplicationDropletRequest.builder()
                .dropletId("guid-3b5793e7-f6c8-40cb-a8d8-07080280da83")
                .id("test-id")
                .build();

        Streams.wrap(this.applications.assignDroplet(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void assignDropletInvalidRequest() {
        AssignApplicationDropletRequest request = AssignApplicationDropletRequest.builder()
                .build();

        Streams.wrap(this.applications.assignDroplet(request)).next().get();
    }

    @Test
    public void create() throws IOException {
        mockRequest(new RequestContext()
                .method(POST).path("/v3/apps")
                .requestPayload("v3/apps/POST_request.json")
                .status(CREATED)
                .responsePayload("v3/apps/POST_response.json"));

        CreateApplicationRequest request = CreateApplicationRequest.builder()
                .name("my_app")
                .spaceId("31627bdc-5bc4-4c4d-a883-c7b2f53db249")
                .environmentVariable("open", "source")
                .buildpack("name-410")
                .build();

        CreateApplicationResponse expected = CreateApplicationResponse.builder()
                .id("8b51db6f-7bae-47ca-bc75-74bc957ed460")
                .name("my_app")
                .desiredState("STOPPED")
                .totalDesiredInstances(0)
                .buildpack("name-410")
                .createdAt("2015-07-27T22:43:15Z")
                .environmentVariable("open", "source")
                .link("self", Link.builder()
                        .href("/v3/apps/8b51db6f-7bae-47ca-bc75-74bc957ed460")
                        .build())
                .link("processes", Link.builder()
                        .href("/v3/apps/8b51db6f-7bae-47ca-bc75-74bc957ed460/processes")
                        .build())
                .link("packages", Link.builder()
                        .href("/v3/apps/8b51db6f-7bae-47ca-bc75-74bc957ed460/packages")
                        .build())
                .link("space", Link.builder()
                        .href("/v2/spaces/31627bdc-5bc4-4c4d-a883-c7b2f53db249")
                        .build())
                .link("start", Link.builder()
                        .href("/v3/apps/8b51db6f-7bae-47ca-bc75-74bc957ed460/start")
                        .method("PUT")
                        .build())
                .link("stop", Link.builder()
                        .href("/v3/apps/8b51db6f-7bae-47ca-bc75-74bc957ed460/stop")
                        .method("PUT")
                        .build())
                .link("assign_current_droplet", Link.builder()
                        .href("/v3/apps/8b51db6f-7bae-47ca-bc75-74bc957ed460/current_droplet")
                        .method("PUT")
                        .build())
                .build();

        CreateApplicationResponse actual = Streams.wrap(this.applications.create(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void createError() throws IOException {
        mockRequest(new RequestContext()
                .method(POST).path("/v3/apps")
                .requestPayload("v3/apps/POST_request.json")
                .errorResponse());

        CreateApplicationRequest request = CreateApplicationRequest.builder()
                .name("my_app")
                .spaceId("31627bdc-5bc4-4c4d-a883-c7b2f53db249")
                .environmentVariable("open", "source")
                .buildpack("name-410")
                .build();

        Streams.wrap(this.applications.create(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void createInvalidRequest() throws Throwable {
        CreateApplicationRequest request = CreateApplicationRequest
                .builder().build();

        Streams.wrap(this.applications.create(request)).next().get();
    }

    @Test
    public void delete() {
        mockRequest(new RequestContext()
                .method(DELETE).path("/v3/apps/test-id")
                .status(NO_CONTENT));

        DeleteApplicationRequest request = DeleteApplicationRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.applications.delete(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void deleteError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("/v3/apps/test-id")
                .errorResponse());

        DeleteApplicationRequest request = DeleteApplicationRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.applications.delete(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void deleteInvalidRequest() {
        DeleteApplicationRequest request = DeleteApplicationRequest.builder()
                .build();

        Streams.wrap(this.applications.delete(request)).next().get();
    }

    @Test
    public void deleteProcess() {
        mockRequest(new RequestContext()
                .method(DELETE).path("/v3/apps/test-id/processes/test-type/instances/test-index")
                .status(NO_CONTENT));

        DeleteApplicationInstanceRequest request = DeleteApplicationInstanceRequest.builder()
                .id("test-id")
                .index("test-index")
                .type("test-type")
                .build();

        Streams.wrap(this.applications.deleteInstance(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void deleteProcessError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("/v3/apps/test-id/processes/test-type/instances/test-index")
                .errorResponse());

        DeleteApplicationInstanceRequest request = DeleteApplicationInstanceRequest.builder()
                .id("test-id")
                .index("test-index")
                .type("test-type")
                .build();

        Streams.wrap(this.applications.deleteInstance(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void deleteProcessInvalidRequest() {
        DeleteApplicationInstanceRequest request = DeleteApplicationInstanceRequest.builder()
                .build();

        Streams.wrap(this.applications.deleteInstance(request)).next().get();
    }

    @Test
    public void get() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/apps/test-id")
                .status(OK)
                .responsePayload("v3/apps/GET_{id}_response.json"));

        GetApplicationRequest request = GetApplicationRequest.builder()
                .id("test-id")
                .build();

        GetApplicationResponse expected = GetApplicationResponse.builder()
                .id("guid-e23c9834-9c4a-4397-be7d-e0fb686cb646")
                .name("my_app")
                .desiredState("STOPPED")
                .totalDesiredInstances(3)
                .buildpack("name-371")
                .createdAt("2015-07-27T22:43:15Z")
                .environmentVariable("unicorn", "horn")
                .link("self", Link.builder()
                        .href("/v3/apps/guid-e23c9834-9c4a-4397-be7d-e0fb686cb646")
                        .build())
                .link("processes", Link.builder()
                        .href("/v3/apps/guid-e23c9834-9c4a-4397-be7d-e0fb686cb646/processes")
                        .build())
                .link("packages", Link.builder()
                        .href("/v3/apps/guid-e23c9834-9c4a-4397-be7d-e0fb686cb646/packages")
                        .build())
                .link("space", Link.builder()
                        .href("/v2/spaces/6776a2d8-ef86-4760-a51e-f2b24b27d019")
                        .build())
                .link("droplet", Link.builder()
                        .href("/v3/droplets/a-droplet-guid")
                        .build())
                .link("start", Link.builder()
                        .href("/v3/apps/guid-e23c9834-9c4a-4397-be7d-e0fb686cb646/start")
                        .method("PUT")
                        .build())
                .link("stop", Link.builder()
                        .href("/v3/apps/guid-e23c9834-9c4a-4397-be7d-e0fb686cb646/stop")
                        .method("PUT")
                        .build())
                .link("assign_current_droplet", Link.builder()
                        .href("/v3/apps/guid-e23c9834-9c4a-4397-be7d-e0fb686cb646/current_droplet")
                        .method("PUT")
                        .build())
                .build();

        GetApplicationResponse actual = Streams.wrap(this.applications.get(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void getError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/apps/test-id")
                .errorResponse());

        GetApplicationRequest request = GetApplicationRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.applications.get(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void getInvalidRequest() {
        GetApplicationRequest request = GetApplicationRequest.builder()
                .build();

        Streams.wrap(this.applications.get(request)).next().get();
    }

    @Test
    public void getEnvironment() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/apps/test-id/env")
                .status(OK)
                .responsePayload("v3/apps/GET_{id}_env_response.json"));

        GetApplicationEnvironmentRequest request = GetApplicationEnvironmentRequest.builder()
                .id("test-id")
                .build();

        GetApplicationEnvironmentResponse expected = GetApplicationEnvironmentResponse.builder()
                .environmentVariable("SOME_KEY", "some_val")
                .stagingEnvironmentVariable("STAGING_ENV", "staging_value")
                .runningEnvironmentVariable("RUNNING_ENV", "running_value")
                .applicationEnvironmentVariable("VCAP_APPLICATION", vcapApplication())
                .build();

        GetApplicationEnvironmentResponse actual = Streams.wrap(this.applications.getEnvironment(request))
                .next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void getEnvironmentError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/apps/test-id/env")
                .errorResponse());

        GetApplicationEnvironmentRequest request = GetApplicationEnvironmentRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.applications.getEnvironment(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void getEnvironmentInvalidRequest() {
        GetApplicationEnvironmentRequest request = GetApplicationEnvironmentRequest.builder()
                .build();

        Streams.wrap(this.applications.getEnvironment(request)).next().get();
    }

    @Test
    public void getProcess() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/apps/test-id/processes/web")
                .status(OK)
                .responsePayload("v3/apps/GET_{id}_processes_{type}_response.json"));

        GetApplicationProcessRequest request = GetApplicationProcessRequest.builder()
                .id("test-id")
                .type("web")
                .build();

        GetApplicationProcessResponse expected = GetApplicationProcessResponse.builder()
                .id("32f64d22-ab45-4a9b-ba93-2b3b160f3750")
                .type("web")
                .instances(1)
                .memoryInMb(1024)
                .diskInMb(1024)
                .createdAt("2015-07-27T22:43:29Z")
                .updatedAt("2015-07-27T22:43:29Z")
                .link("self", Link.builder()
                        .href("/v3/processes/32f64d22-ab45-4a9b-ba93-2b3b160f3750")
                        .build())
                .link("scale", Link.builder()
                        .href("/v3/processes/32f64d22-ab45-4a9b-ba93-2b3b160f3750/scale")
                        .method("PUT")
                        .build())
                .link("app", Link.builder()
                        .href("/v3/apps/guid-03125b96-e771-4346-8003-026ecfab6a75")
                        .build())
                .link("space", Link.builder()
                        .href("/v2/spaces/55f4b2be-0ff7-470f-add0-3b2b6e19930c")
                        .build())
                .build();

        GetApplicationProcessResponse actual = Streams.wrap(this.applications.getProcess(request))
                .next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void getProcessError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/apps/test-id/processes/web")
                .errorResponse());

        GetApplicationProcessRequest request = GetApplicationProcessRequest.builder()
                .id("test-id")
                .type("web")
                .build();

        Streams.wrap(this.applications.getProcess(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void getProcessInvalidRequest() {
        GetApplicationProcessRequest request = GetApplicationProcessRequest.builder()
                .build();

        Streams.wrap(this.applications.getProcess(request)).next().get();
    }

    @Test
    public void list() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/apps?names[]=test-name&order_by=created_at&page=1")
                .status(OK)
                .responsePayload("v3/apps/GET_response.json"));

        ListApplicationsRequest request = ListApplicationsRequest.builder()
                .page(1)
                .orderBy(CREATED_AT)
                .name("test-name")
                .build();

        ListApplicationsResponse expected = ListApplicationsResponse.builder()
                .pagination(Pagination.builder()
                        .totalResults(3)
                        .first(Link.builder()
                                .href("/v3/apps?order_by=created_at&order_direction=desc&page=1&per_page=2")
                                .build())
                        .last(Link.builder()
                                .href("/v3/apps?order_by=created_at&order_direction=desc&page=2&per_page=2")
                                .build())
                        .next(Link.builder()
                                .href("/v3/apps?order_by=created_at&order_direction=desc&page=2&per_page=2")
                                .build())
                        .build())
                .resource(ListApplicationsResponse.Resource.builder()
                        .id("guid-acfbae75-7d3a-45b1-b730-ca3cc4263045")
                        .name("my_app3")
                        .desiredState("STOPPED")
                        .totalDesiredInstances(0)
                        .buildpack("name-383")
                        .createdAt("1970-01-01T00:00:03Z")
                        .environmentVariable("magic", "beautiful")
                        .link("self", Link.builder()
                                .href("/v3/apps/guid-acfbae75-7d3a-45b1-b730-ca3cc4263045")
                                .build())
                        .link("processes", Link.builder()
                                .href("/v3/apps/guid-acfbae75-7d3a-45b1-b730-ca3cc4263045/processes")
                                .build())
                        .link("packages", Link.builder()
                                .href("/v3/apps/guid-acfbae75-7d3a-45b1-b730-ca3cc4263045/packages")
                                .build())
                        .link("space", Link.builder()
                                .href("/v2/spaces/a6a823be-49c0-4591-bfdc-5bf0998a9d62")
                                .build())
                        .link("start", Link.builder()
                                .href("/v3/apps/guid-acfbae75-7d3a-45b1-b730-ca3cc4263045/start")
                                .method("PUT")
                                .build())
                        .link("stop", Link.builder()
                                .href("/v3/apps/guid-acfbae75-7d3a-45b1-b730-ca3cc4263045/stop")
                                .method("PUT")
                                .build())
                        .link("assign_current_droplet", Link.builder()
                                .href("/v3/apps/guid-acfbae75-7d3a-45b1-b730-ca3cc4263045/current_droplet")
                                .method("PUT")
                                .build())
                        .build())
                .resource(ListApplicationsResponse.Resource.builder()
                        .id("guid-f2e97073-86d2-473e-8d03-76742f16bf6e")
                        .name("my_app2")
                        .desiredState("STOPPED")
                        .totalDesiredInstances(0)
                        .createdAt("1970-01-01T00:00:02Z")
                        .link("self", Link.builder()
                                .href("/v3/apps/guid-f2e97073-86d2-473e-8d03-76742f16bf6e")
                                .build())
                        .link("processes", Link.builder()
                                .href("/v3/apps/guid-f2e97073-86d2-473e-8d03-76742f16bf6e/processes")
                                .build())
                        .link("packages", Link.builder()
                                .href("/v3/apps/guid-f2e97073-86d2-473e-8d03-76742f16bf6e/packages")
                                .build())
                        .link("space", Link.builder()
                                .href("/v2/spaces/a6a823be-49c0-4591-bfdc-5bf0998a9d62")
                                .build())
                        .link("start", Link.builder()
                                .href("/v3/apps/guid-f2e97073-86d2-473e-8d03-76742f16bf6e/start")
                                .method("PUT")
                                .build())
                        .link("stop", Link.builder()
                                .href("/v3/apps/guid-f2e97073-86d2-473e-8d03-76742f16bf6e/stop")
                                .method("PUT")
                                .build())
                        .link("assign_current_droplet", Link.builder()
                                .href("/v3/apps/guid-f2e97073-86d2-473e-8d03-76742f16bf6e/current_droplet")
                                .method("PUT")
                                .build())
                        .build())
                .build();

        ListApplicationsResponse actual = Streams.wrap(this.applications.list(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/apps?names[]=test-name&order_by=created_at&page=1")
                .errorResponse());

        ListApplicationsRequest request = ListApplicationsRequest.builder()
                .page(1)
                .orderBy(CREATED_AT)
                .name("test-name")
                .build();

        Streams.wrap(this.applications.list(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listInvalidRequest() {
        ListApplicationsRequest request = ListApplicationsRequest.builder()
                .page(-1)
                .build();

        Streams.wrap(this.applications.list(request)).next().get();
    }

    @Test
    public void listPackages() {
        mockRequest(new RequestContext()
                        .method(GET).path("/v3/apps/test-id/packages")
                        .status(OK)
                        .responsePayload("v3/apps/GET_{id}_packages_response.json")
        );

        ListApplicationPackagesRequest request = ListApplicationPackagesRequest.builder()
                .page(1)
                .id("test-id")
                .build();

        ListApplicationPackagesResponse expected = builder()
                .pagination(Pagination.builder()
                        .totalResults(1)
                        .first(Link.builder()
                                .href("/v3/apps/guid-e6ee32d9-013f-4184-84c4-f6528c3ce7e8/packages?page=1&per_page=50")
                                .build())
                        .last(Link.builder()
                                .href("/v3/apps/guid-e6ee32d9-013f-4184-84c4-f6528c3ce7e8/packages?page=1&per_page=50")
                                .build())
                        .build())
                .resource(Resource.builder()
                        .id("guid-3d792a08-e415-4f9e-912b-2a8485db781a")
                        .type("bits")
                        .hash(Hash.builder()
                                .type("sha1")
                                .build())
                        .state("AWAITING_UPLOAD")
                        .createdAt("2015-07-27T22:43:34Z")
                        .link("self", Link.builder()
                                .href("/v3/packages/guid-3d792a08-e415-4f9e-912b-2a8485db781a")
                                .build())
                        .link("upload", Link.builder()
                                .href("/v3/packages/guid-3d792a08-e415-4f9e-912b-2a8485db781a/upload")
                                .method("POST")
                                .build())
                        .link("download", Link.builder()
                                .href("/v3/packages/guid-3d792a08-e415-4f9e-912b-2a8485db781a/download")
                                .method("GET")
                                .build())
                        .link("stage", Link.builder()
                                .href("/v3/packages/guid-3d792a08-e415-4f9e-912b-2a8485db781a/droplets")
                                .method("POST")
                                .build())
                        .link("app", Link.builder()
                                .href("/v3/apps/guid-e6ee32d9-013f-4184-84c4-f6528c3ce7e8")
                                .build())
                        .build())
                .build();

        ListApplicationPackagesResponse actual = Streams.wrap(this.applications.listPackages(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listPackagesError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/apps/test-id/packages")
                .errorResponse());

        ListApplicationPackagesRequest request = ListApplicationPackagesRequest.builder()
                .page(1)
                .id("test-id")
                .build();

        Streams.wrap(this.applications.listPackages(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listPackagesInvalidRequest() {
        ListApplicationPackagesRequest request = ListApplicationPackagesRequest.builder()
                .build();

        Streams.wrap(this.applications.listPackages(request)).next().get();
    }

    @Test
    public void listDroplets() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/apps/test-id/droplets?order_by=created_at&order_direction=asc&page=1&per_page=2")
                .status(OK)
                .responsePayload("v3/apps/GET_{id}_droplets_response.json"));

        ListApplicationDropletsRequest request = ListApplicationDropletsRequest.builder()
                .page(1)
                .perPage(2)
                .orderBy(CREATED_AT)
                .orderDirection(ASC)
                .id("test-id")
                .build();

        ListApplicationDropletsResponse expected = ListApplicationDropletsResponse.builder()
                .pagination(Pagination.builder()
                        .totalResults(2)
                        .first(Link.builder()
                                .href("/v3/droplets?order_by=created_at&order_direction=asc&page=1&per_page=2")
                                .build())
                        .last(Link.builder()
                                .href("/v3/droplets?order_by=created_at&order_direction=asc&page=1&per_page=2")
                                .build())
                        .build())
                .resource(ListApplicationDropletsResponse.Resource.builder()
                        .id("guid-5df0a4bb-4fcb-4393-acdd-868524ad761e")
                        .state("STAGING")
                        .hash(Hash.builder()
                                .type("sha1")
                                .build())
                        .buildpack("name-2089")
                        .environmentVariable("yuu", "huuu")
                        .createdAt("1970-01-01T00:00:01Z")
                        .link("self", Link.builder()
                                .href("/v3/droplets/guid-5df0a4bb-4fcb-4393-acdd-868524ad761e")
                                .build())
                        .link("package", Link.builder()
                                .href("/v3/packages/guid-f80c4f3c-b625-474d-b43b-2c8b223b84e3")
                                .build())
                        .link("app", Link.builder()
                                .href("/v3/apps/guid-2ec6e3b5-fd07-437f-882e-cb0cb298eff1")
                                .build())
                        .link("assign_current_droplet", Link.builder()
                                .href("/v3/apps/guid-2ec6e3b5-fd07-437f-882e-cb0cb298eff1/current_droplet")
                                .method("PUT")
                                .build())
                        .link("buildpack", Link.builder()
                                .href("/v2/buildpacks/9058357a-a54f-4a6c-a18d-b63b079305b3")
                                .build())
                        .build())
                .resource(ListApplicationDropletsResponse.Resource.builder()
                        .id("guid-dd5fc1f8-fa34-4d97-9edd-a8fc82624fa8")
                        .state("STAGED")
                        .hash(Hash.builder()
                                .type("sha1")
                                .value("my-hash")
                                .build())
                        .buildpack("https://github.com/cloudfoundry/my-buildpack.git")
                        .createdAt("1970-01-01T00:00:02Z")
                        .link("self", Link.builder()
                                .href("/v3/droplets/guid-dd5fc1f8-fa34-4d97-9edd-a8fc82624fa8")
                                .build())
                        .link("package", Link.builder()
                                .href("/v3/packages/guid-f80c4f3c-b625-474d-b43b-2c8b223b84e3")
                                .build())
                        .link("app", Link.builder()
                                .href("/v3/apps/guid-2ec6e3b5-fd07-437f-882e-cb0cb298eff1")
                                .build())
                        .link("assign_current_droplet", Link.builder()
                                .href("/v3/apps/guid-2ec6e3b5-fd07-437f-882e-cb0cb298eff1/current_droplet")
                                .method("PUT")
                                .build())
                        .build())
                .build();

        ListApplicationDropletsResponse actual = Streams.wrap(this.applications.listDroplets(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listDropletsError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/apps/test-id/droplets?order_by=created_at&order_direction=asc&page=1&per_page=2")
                .errorResponse());

        ListApplicationDropletsRequest request = ListApplicationDropletsRequest.builder()
                .page(1)
                .perPage(2)
                .orderBy(CREATED_AT)
                .orderDirection(ASC)
                .id("test-id")
                .build();

        Streams.wrap(this.applications.listDroplets(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listDropletsInvalidRequest() {
        ListApplicationDropletsRequest request = ListApplicationDropletsRequest.builder()
                .build();

        Streams.wrap(this.applications.listDroplets(request)).next().get();
    }

    @Test
    public void listProcesses() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/apps/test-id/processes")
                .status(OK)
                .responsePayload("v3/apps/GET_{id}_processes_response.json"));

        ListApplicationProcessesRequest request = ListApplicationProcessesRequest.builder()
                .page(1)
                .id("test-id")
                .build();

        ListApplicationProcessesResponse expected = ListApplicationProcessesResponse.builder()
                .pagination(Pagination.builder()
                        .totalResults(1)
                        .first(Link.builder()
                                .href("/v3/apps/guid-ad6388b3-c798-4ee7-8bab-6714864eb389/processes?page=1&per_page=50")
                                .build())
                        .last(Link.builder()
                                .href("/v3/apps/guid-ad6388b3-c798-4ee7-8bab-6714864eb389/processes?page=1&per_page=50")
                                .build())
                        .build())
                .resource(ListApplicationProcessesResponse.Resource.builder()
                        .id("38fcaafa-5356-4f74-af10-dc70da151993")
                        .type("web")
                        .instances(1)
                        .memoryInMb(1024)
                        .diskInMb(1024)
                        .createdAt("2015-07-27T22:43:29Z")
                        .updatedAt("2015-07-27T22:43:29Z")
                        .link("self", Link.builder()
                                .href("/v3/processes/38fcaafa-5356-4f74-af10-dc70da151993")
                                .build())
                        .link("scale", Link.builder()
                                .href("/v3/processes/38fcaafa-5356-4f74-af10-dc70da151993/scale")
                                .method("PUT")
                                .build())
                        .link("app", Link.builder()
                                .href("/v3/apps/guid-ad6388b3-c798-4ee7-8bab-6714864eb389")
                                .build())
                        .link("space", Link.builder()
                                .href("/v2/spaces/b35fe3b3-7ec3-4fc0-acd0-249eb61a27f3")
                                .build())
                        .build())
                .build();

        ListApplicationProcessesResponse actual = Streams.wrap(this.applications.listProcesses(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listProcessesError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/apps/test-id/processes")
                .errorResponse());

        ListApplicationProcessesRequest request = ListApplicationProcessesRequest.builder()
                .page(1)
                .id("test-id")
                .build();

        Streams.wrap(this.applications.listProcesses(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listProcessesInvalidRequest() {
        ListApplicationProcessesRequest request = ListApplicationProcessesRequest.builder()
                .build();

        Streams.wrap(this.applications.listProcesses(request)).next().get();
    }

    @Test
    public void listRoutes() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/apps/test-id/routes")
                .status(OK)
                .responsePayload("v3/apps/GET_{id}_routes_response.json"));

        ListApplicationRoutesRequest request = ListApplicationRoutesRequest.builder()
                .id("test-id")
                .build();

        ListApplicationRoutesResponse expected = ListApplicationRoutesResponse.builder()
                .pagination(Pagination.builder()
                        .totalResults(2)
                        .first(Link.builder()
                                .href("/v3/apps/guid-7cc42bf5-2b0b-4c8b-84eb-a733c5a26762/routes?page=1&per_page=50")
                                .build())
                        .last(Link.builder()
                                .href("/v3/apps/guid-7cc42bf5-2b0b-4c8b-84eb-a733c5a26762/routes?page=1&per_page=50")
                                .build())
                        .build())
                .resource(ListApplicationRoutesResponse.Resource.builder()
                        .id("cad6fe1d-d6de-4698-9b8e-caf9506ecf8d")
                        .host("host-20")
                        .path("")
                        .createdAt("2015-07-27T22:43:32Z")
                        .link("space", Link.builder()
                                .href("/v2/spaces/5835dcab-415d-4758-b8cc-dc4246f930ce")
                                .build())
                        .link("domain", Link.builder()
                                .href("/v2/domains/74797591-b1cc-40a6-8cd3-d8bfa277a306")
                                .build())
                        .build())
                .resource(ListApplicationRoutesResponse.Resource.builder()
                        .id("74f9b772-ace2-4932-aa7b-328434e712a1")
                        .host("host-21")
                        .path("/foo/bar")
                        .createdAt("2015-07-27T22:43:32Z")
                        .link("space", Link.builder()
                                .href("/v2/spaces/5835dcab-415d-4758-b8cc-dc4246f930ce")
                                .build())
                        .link("domain", Link.builder()
                                .href("/v2/domains/9274a306-f253-4bd8-9cd1-d4af023bcf90")
                                .build())
                        .build())
                .build();

        ListApplicationRoutesResponse actual = Streams.wrap(this.applications.listRoutes(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listRoutesError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/apps/test-id/routes")
                .errorResponse());

        ListApplicationRoutesRequest request = ListApplicationRoutesRequest.builder()
                .id("test-id")
                .build();
        Streams.wrap(this.applications.listRoutes(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listRoutesInvalidRequest() {
        ListApplicationRoutesRequest request = ListApplicationRoutesRequest.builder()
                .build();

        Streams.wrap(this.applications.listRoutes(request)).next().get();
    }

    @Test
    public void mapRoute() {
        mockRequest(new RequestContext()
                .method(PUT).path("/v3/apps/test-id/routes")
                .requestPayload("v3/apps/PUT_{id}_routes_request.json")
                .status(NO_CONTENT));

        MapApplicationRouteRequest request = MapApplicationRouteRequest.builder()
                .id("test-id")
                .routeId("9cf0271a-420f-4ae4-b227-16683db93573")
                .build();

        Streams.wrap(this.applications.mapRoute(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void mapRouteError() {
        mockRequest(new RequestContext()
                .method(PUT).path("/v3/apps/test-id/routes")
                .requestPayload("v3/apps/PUT_{id}_routes_request.json")
                .errorResponse());

        MapApplicationRouteRequest request = MapApplicationRouteRequest.builder()
                .id("test-id")
                .routeId("9cf0271a-420f-4ae4-b227-16683db93573")
                .build();

        Streams.wrap(this.applications.mapRoute(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void mapRouteInvalidRequest() {
        MapApplicationRouteRequest request = MapApplicationRouteRequest.builder()
                .build();

        Streams.wrap(this.applications.mapRoute(request)).next().get();
    }

    @Test
    public void scale() {
        mockRequest(new RequestContext()
                .method(PUT).path("/v3/apps/test-id/processes/web/scale")
                .requestPayload("v3/apps/PUT_{id}_processes_{type}_scale_request.json")
                .status(OK)
                .responsePayload("v3/apps/PUT_{id}_processes_{type}_scale_response.json"));

        ScaleApplicationRequest request = ScaleApplicationRequest.builder()
                .diskInMb(100)
                .id("test-id")
                .instances(3)
                .memoryInMb(100)
                .type("web")
                .build();

        ScaleApplicationResponse expected = ScaleApplicationResponse.builder()
                .id("edc2dffe-9f0d-416f-a712-890d56de8bae")
                .type("web")
                .instances(3)
                .memoryInMb(100)
                .diskInMb(100)
                .createdAt("2015-07-27T22:43:29Z")
                .updatedAt("2015-07-27T22:43:29Z")
                .link("self", Link.builder()
                        .href("/v3/processes/edc2dffe-9f0d-416f-a712-890d56de8bae")
                        .build())
                .link("scale", Link.builder()
                        .href("/v3/processes/edc2dffe-9f0d-416f-a712-890d56de8bae/scale")
                        .method("PUT")
                        .build())
                .link("app", Link.builder()
                        .href("/v3/apps/guid-4b2de865-95a6-47a4-86af-0169ae6b9003")
                        .build())
                .link("space", Link.builder()
                        .href("/v2/spaces/baac2863-4fa7-4662-8c07-5645271640b9")
                        .build())
                .build();

        ScaleApplicationResponse actual = Streams.wrap(this.applications.scale(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void scaleError() {
        mockRequest(new RequestContext()
                .method(PUT).path("/v3/apps/test-id/processes/web/scale")
                .requestPayload("v3/apps/PUT_{id}_processes_{type}_scale_request.json")
                .errorResponse());

        ScaleApplicationRequest request = ScaleApplicationRequest.builder()
                .diskInMb(100)
                .id("test-id")
                .instances(3)
                .memoryInMb(100)
                .type("web")
                .build();

        Streams.wrap(this.applications.scale(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void scaleInvalidRequest() {
        ScaleApplicationRequest request = ScaleApplicationRequest.builder()
                .build();

        Streams.wrap(this.applications.scale(request)).next().get();
    }

    @Test
    public void start() {
        mockRequest(new RequestContext()
                .method(PUT).path("/v3/apps/test-id/start")
                .status(OK)
                .responsePayload("v3/apps/PUT_{id}_start_response.json"));

        StartApplicationRequest request = StartApplicationRequest.builder()
                .id("test-id")
                .build();

        StartApplicationResponse expected = StartApplicationResponse.builder()
                .id("guid-40460094-d035-4663-b58c-cdf4c802a2c6")
                .name("original_name")
                .desiredState("STARTED")
                .totalDesiredInstances(0)
                .createdAt("2015-07-27T22:43:15Z")
                .updatedAt("2015-07-27T22:43:15Z")
                .link("self", Link.builder()
                        .href("/v3/apps/guid-40460094-d035-4663-b58c-cdf4c802a2c6")
                        .build())
                .link("processes", Link.builder()
                        .href("/v3/apps/guid-40460094-d035-4663-b58c-cdf4c802a2c6/processes")
                        .build())
                .link("packages", Link.builder()
                        .href("/v3/apps/guid-40460094-d035-4663-b58c-cdf4c802a2c6/packages")
                        .build())
                .link("space", Link.builder()
                        .href("/v2/spaces/24f4776a-0cfd-49d3-922b-64951eb73e49")
                        .build())
                .link("droplet", Link.builder()
                        .href("/v3/droplets/guid-9b8fb4ff-e212-4024-bf2b-bed0d0803727")
                        .build())
                .link("start", Link.builder()
                        .href("/v3/apps/guid-40460094-d035-4663-b58c-cdf4c802a2c6/start")
                        .method("PUT")
                        .build())
                .link("stop", Link.builder()
                        .href("/v3/apps/guid-40460094-d035-4663-b58c-cdf4c802a2c6/stop")
                        .method("PUT")
                        .build())
                .link("assign_current_droplet", Link.builder()
                        .href("/v3/apps/guid-40460094-d035-4663-b58c-cdf4c802a2c6/current_droplet")
                        .method("PUT")
                        .build())
                .build();

        StartApplicationResponse actual = Streams.wrap(this.applications.start(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void startError() {
        mockRequest(new RequestContext()
                .method(PUT).path("/v3/apps/test-id/start")
                .errorResponse());

        StartApplicationRequest request = StartApplicationRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.applications.start(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void startInvalidRequest() {
        StartApplicationRequest request = StartApplicationRequest.builder()
                .build();

        Streams.wrap(this.applications.start(request)).next().get();
    }

    @Test
    public void stop() {
        mockRequest(new RequestContext()
                .method(PUT).path("/v3/apps/test-id/stop")
                .status(OK)
                .responsePayload("v3/apps/PUT_{id}_stop_response.json"));

        StopApplicationRequest request = StopApplicationRequest.builder()
                .id("test-id")
                .build();

        StopApplicationResponse expected = StopApplicationResponse.builder()
                .id("guid-be4e4357-5a9d-48fc-ae37-821f48c1ace0")
                .name("original_name")
                .desiredState("STOPPED")
                .totalDesiredInstances(0)
                .createdAt("2015-07-27T22:43:15Z")
                .updatedAt("2015-07-27T22:43:15Z")
                .link("self", Link.builder()
                        .href("/v3/apps/guid-be4e4357-5a9d-48fc-ae37-821f48c1ace0")
                        .build())
                .link("processes", Link.builder()
                        .href("/v3/apps/guid-be4e4357-5a9d-48fc-ae37-821f48c1ace0/processes")
                        .build())
                .link("packages", Link.builder()
                        .href("/v3/apps/guid-be4e4357-5a9d-48fc-ae37-821f48c1ace0/packages")
                        .build())
                .link("space", Link.builder()
                        .href("/v2/spaces/7550ea49-3bce-4655-b44c-30b5ca622402")
                        .build())
                .link("droplet", Link.builder()
                        .href("/v3/droplets/guid-978d2287-36ff-4074-bbdf-b613b8211397")
                        .build())
                .link("start", Link.builder()
                        .href("/v3/apps/guid-be4e4357-5a9d-48fc-ae37-821f48c1ace0/start")
                        .method("PUT")
                        .build())
                .link("stop", Link.builder()
                        .href("/v3/apps/guid-be4e4357-5a9d-48fc-ae37-821f48c1ace0/stop")
                        .method("PUT")
                        .build())
                .link("assign_current_droplet", Link.builder()
                        .href("/v3/apps/guid-be4e4357-5a9d-48fc-ae37-821f48c1ace0/current_droplet")
                        .method("PUT")
                        .build())
                .build();

        StopApplicationResponse actual = Streams.wrap(this.applications.stop(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void stopError() {
        mockRequest(new RequestContext()
                .method(PUT).path("/v3/apps/test-id/stop")
                .errorResponse());

        StopApplicationRequest request = StopApplicationRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.applications.stop(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void stopInvalidRequest() {
        StopApplicationRequest request = StopApplicationRequest.builder()
                .build();

        Streams.wrap(this.applications.stop(request)).next().get();
    }

    @Test
    public void unmapRoute() {
        mockRequest(new RequestContext()
                .method(DELETE).path("/v3/apps/test-id/routes")
                .requestPayload("v3/apps/DELETE_{id}_routes_request.json")
                .status(NO_CONTENT));

        UnmapApplicationRouteRequest request = UnmapApplicationRouteRequest.builder()
                .id("test-id")
                .routeId("3f0121a8-54e1-45c0-8daf-44d0f8ba1091")
                .build();

        Streams.wrap(this.applications.unmapRoute(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void unmapRouteError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("/v3/apps/test-id/routes")
                .requestPayload("v3/apps/DELETE_{id}_routes_request.json")
                .errorResponse());

        UnmapApplicationRouteRequest request = UnmapApplicationRouteRequest.builder()
                .id("test-id")
                .routeId("3f0121a8-54e1-45c0-8daf-44d0f8ba1091")
                .build();

        Streams.wrap(this.applications.unmapRoute(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void unmapRouteInvalidRequest() {
        UnmapApplicationRouteRequest request = UnmapApplicationRouteRequest.builder()
                .build();

        Streams.wrap(this.applications.unmapRoute(request)).next().get();
    }

    @Test
    public void update() throws IOException {
        mockRequest(new RequestContext()
                .method(PATCH).path("/v3/apps/test-id")
                .requestPayload("v3/apps/PATCH_{id}_request.json")
                .status(OK)
                .responsePayload("v3/apps/PATCH_{id}_response.json"));

        UpdateApplicationRequest request = UpdateApplicationRequest.builder()
                .name("new_name")
                .environmentVariable("MY_ENV_VAR", "foobar")
                .environmentVariable("FOOBAR", "MY_ENV_VAR")
                .buildpack("http://gitwheel.org/my-app")
                .id("test-id")
                .build();

        UpdateApplicationResponse expected = UpdateApplicationResponse.builder()
                .id("guid-a7b667e9-2358-4f51-9b1d-92a74beaa30a")
                .name("new_name")
                .desiredState("STOPPED")
                .totalDesiredInstances(0)
                .buildpack("http://gitwheel.org/my-app")
                .createdAt("2015-07-27T22:43:14Z")
                .updatedAt("2015-07-27T22:43:14Z")
                .environmentVariable("MY_ENV_VAR", "foobar")
                .environmentVariable("FOOBAR", "MY_ENV_VAR")
                .link("self", Link.builder()
                        .href("/v3/apps/guid-a7b667e9-2358-4f51-9b1d-92a74beaa30a")
                        .build())
                .link("processes", Link.builder()
                        .href("/v3/apps/guid-a7b667e9-2358-4f51-9b1d-92a74beaa30a/processes")
                        .build())
                .link("packages", Link.builder()
                        .href("/v3/apps/guid-a7b667e9-2358-4f51-9b1d-92a74beaa30a/packages")
                        .build())
                .link("space", Link.builder()
                        .href("/v2/spaces/68040a7b-e84d-4461-960d-36bf8c9873d8")
                        .build())
                .link("start", Link.builder()
                        .href("/v3/apps/guid-a7b667e9-2358-4f51-9b1d-92a74beaa30a/start")
                        .method("PUT")
                        .build())
                .link("stop", Link.builder()
                        .href("/v3/apps/guid-a7b667e9-2358-4f51-9b1d-92a74beaa30a/stop")
                        .method("PUT")
                        .build())
                .link("assign_current_droplet", Link.builder()
                        .href("/v3/apps/guid-a7b667e9-2358-4f51-9b1d-92a74beaa30a/current_droplet")
                        .method("PUT")
                        .build())
                .build();

        UpdateApplicationResponse actual = Streams.wrap(this.applications.update(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void updateError() throws IOException {
        mockRequest(new RequestContext()
                .method(PATCH).path("/v3/apps/test-id")
                .requestPayload("v3/apps/PATCH_{id}_request.json")
                .errorResponse());

        UpdateApplicationRequest request = UpdateApplicationRequest.builder()
                .name("new_name")
                .environmentVariable("MY_ENV_VAR", "foobar")
                .environmentVariable("FOOBAR", "MY_ENV_VAR")
                .buildpack("http://gitwheel.org/my-app")
                .id("test-id")
                .build();

        Streams.wrap(this.applications.update(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void updateInvalidRequest() throws Throwable {
        UpdateApplicationRequest request = UpdateApplicationRequest.builder()
                .build();

        Streams.wrap(this.applications.update(request)).next().get();
    }

    private Map<String, Object> vcapApplication() {
        Map<String, Object> vcapApplication = new HashMap<>();
        vcapApplication.put("limits", Collections.singletonMap("fds", 16384));
        vcapApplication.put("application_name", "app_name");
        vcapApplication.put("application_uris", Collections.emptyList());
        vcapApplication.put("name", "app_name");
        vcapApplication.put("space_name", "some_space");
        vcapApplication.put("space_id", "c595c2ee-df01-4769-a61f-df5bd5e4cbc1");
        vcapApplication.put("uris", Collections.emptyList());
        vcapApplication.put("users", null);

        return vcapApplication;
    }
}

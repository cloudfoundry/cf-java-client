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

package org.cloudfoundry.client.spring.v3.applications;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.spring.util.StringMap;
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
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.cloudfoundry.client.v3.PaginatedAndSortedRequest.OrderBy.CREATED_AT;
import static org.cloudfoundry.client.v3.PaginatedAndSortedRequest.OrderDirection.ASC;
import static org.cloudfoundry.client.v3.PaginatedResponse.Pagination;
import static org.cloudfoundry.client.v3.applications.ListApplicationPackagesResponse.Resource;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public final class SpringApplicationsV3Test {

    public static final class AssignDroplet extends AbstractApiTest<AssignApplicationDropletRequest, AssignApplicationDropletResponse> {

        private final SpringApplicationsV3 applications = new SpringApplicationsV3(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssignApplicationDropletRequest getInvalidRequest() {
            return AssignApplicationDropletRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(PUT).path("v3/apps/test-application-id/current_droplet")
                    .requestPayload("v3/apps/PUT_{id}_current_droplet_request.json")
                    .status(OK)
                    .responsePayload("v3/apps/PUT_{id}_current_droplet_response.json");
        }

        @Override
        protected AssignApplicationDropletResponse getResponse() {
            return AssignApplicationDropletResponse.builder()
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
        }

        @Override
        protected AssignApplicationDropletRequest getValidRequest() throws Exception {
            return AssignApplicationDropletRequest.builder()
                    .dropletId("guid-3b5793e7-f6c8-40cb-a8d8-07080280da83")
                    .applicationId("test-application-id")
                    .build();
        }

        @Override
        protected Mono<AssignApplicationDropletResponse> invoke(AssignApplicationDropletRequest request) {
            return this.applications.assignDroplet(request);
        }

    }

    public static final class Create extends AbstractApiTest<CreateApplicationRequest, CreateApplicationResponse> {

        private final SpringApplicationsV3 applications = new SpringApplicationsV3(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreateApplicationRequest getInvalidRequest() {
            return CreateApplicationRequest
                    .builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(POST).path("/v3/apps")
                    .requestPayload("v3/apps/POST_request.json")
                    .status(CREATED)
                    .responsePayload("v3/apps/POST_response.json");
        }

        @Override
        protected CreateApplicationResponse getResponse() {
            return CreateApplicationResponse.builder()
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
        }

        @Override
        protected CreateApplicationRequest getValidRequest() throws Exception {
            return CreateApplicationRequest.builder()
                    .name("my_app")
                    .spaceId("31627bdc-5bc4-4c4d-a883-c7b2f53db249")
                    .environmentVariable("open", "source")
                    .buildpack("name-410")
                    .build();
        }

        @Override
        protected Mono<CreateApplicationResponse> invoke(CreateApplicationRequest request) {
            return this.applications.create(request);
        }

    }

    public static final class Delete extends AbstractApiTest<DeleteApplicationRequest, Void> {

        private final SpringApplicationsV3 applications = new SpringApplicationsV3(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteApplicationRequest getInvalidRequest() {
            return DeleteApplicationRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(DELETE).path("/v3/apps/test-application-id")
                    .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected DeleteApplicationRequest getValidRequest() throws Exception {
            return DeleteApplicationRequest.builder()
                    .applicationId("test-application-id")
                    .build();
        }

        @Override
        protected Mono<Void> invoke(DeleteApplicationRequest request) {
            return this.applications.delete(request);
        }

    }

    public static final class DeleteProcess extends AbstractApiTest<DeleteApplicationInstanceRequest, Void> {

        private final SpringApplicationsV3 applications = new SpringApplicationsV3(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteApplicationInstanceRequest getInvalidRequest() {
            return DeleteApplicationInstanceRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(DELETE).path("/v3/apps/test-application-id/processes/test-type/instances/test-index")
                    .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected DeleteApplicationInstanceRequest getValidRequest() throws Exception {
            return DeleteApplicationInstanceRequest.builder()
                    .applicationId("test-application-id")
                    .index("test-index")
                    .type("test-type")
                    .build();
        }

        @Override
        protected Mono<Void> invoke(DeleteApplicationInstanceRequest request) {
            return this.applications.deleteInstance(request);
        }

    }

    public static final class Get extends AbstractApiTest<GetApplicationRequest, GetApplicationResponse> {

        private final SpringApplicationsV3 applications = new SpringApplicationsV3(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetApplicationRequest getInvalidRequest() {
            return GetApplicationRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v3/apps/test-application-id")
                    .status(OK)
                    .responsePayload("v3/apps/GET_{id}_response.json");
        }

        @Override
        protected GetApplicationResponse getResponse() {
            return GetApplicationResponse.builder()
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
        }

        @Override
        protected GetApplicationRequest getValidRequest() throws Exception {
            return GetApplicationRequest.builder()
                    .applicationId("test-application-id")
                    .build();
        }

        @Override
        protected Mono<GetApplicationResponse> invoke(GetApplicationRequest request) {
            return this.applications.get(request);
        }

    }

    public static final class GetEnvironment extends AbstractApiTest<GetApplicationEnvironmentRequest, GetApplicationEnvironmentResponse> {

        private final SpringApplicationsV3 applications = new SpringApplicationsV3(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetApplicationEnvironmentRequest getInvalidRequest() {
            return GetApplicationEnvironmentRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v3/apps/test-application-id/env")
                    .status(OK)
                    .responsePayload("v3/apps/GET_{id}_env_response.json");
        }

        @Override
        protected GetApplicationEnvironmentResponse getResponse() {
            return GetApplicationEnvironmentResponse.builder()
                    .environmentVariable("SOME_KEY", "some_val")
                    .stagingEnvironmentVariable("STAGING_ENV", "staging_value")
                    .runningEnvironmentVariable("RUNNING_ENV", "running_value")
                    .applicationEnvironmentVariable("VCAP_APPLICATION", StringMap.builder()
                            .entry("limits", StringMap.builder()
                                    .entry("fds", 16384)
                                    .build())
                            .entry("application_name", "app_name")
                            .entry("application_uris", Collections.emptyList())
                            .entry("name", "app_name")
                            .entry("space_name", "some_space")
                            .entry("space_id", "c595c2ee-df01-4769-a61f-df5bd5e4cbc1")
                            .entry("uris", Collections.emptyList())
                            .entry("users", null)
                            .build())
                    .build();
        }

        @Override
        protected GetApplicationEnvironmentRequest getValidRequest() throws Exception {
            return GetApplicationEnvironmentRequest.builder()
                    .applicationId("test-application-id")
                    .build();
        }

        @Override
        protected Mono<GetApplicationEnvironmentResponse> invoke(GetApplicationEnvironmentRequest request) {
            return this.applications.getEnvironment(request);
        }

    }

    public static final class GetProcess extends AbstractApiTest<GetApplicationProcessRequest, GetApplicationProcessResponse> {

        private final SpringApplicationsV3 applications = new SpringApplicationsV3(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetApplicationProcessRequest getInvalidRequest() {
            return GetApplicationProcessRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v3/apps/test-application-id/processes/web")
                    .status(OK)
                    .responsePayload("v3/apps/GET_{id}_processes_{type}_response.json");
        }

        @Override
        protected GetApplicationProcessResponse getResponse() {
            return GetApplicationProcessResponse.builder()
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
        }

        @Override
        protected GetApplicationProcessRequest getValidRequest() throws Exception {
            return GetApplicationProcessRequest.builder()
                    .applicationId("test-application-id")
                    .type("web")
                    .build();
        }

        @Override
        protected Mono<GetApplicationProcessResponse> invoke(GetApplicationProcessRequest request) {
            return this.applications.getProcess(request);
        }

    }

    public static final class List extends AbstractApiTest<ListApplicationsRequest, ListApplicationsResponse> {

        private final SpringApplicationsV3 applications = new SpringApplicationsV3(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListApplicationsRequest getInvalidRequest() {
            return ListApplicationsRequest.builder()
                    .page(-1)
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v3/apps?names%5B%5D=test-name&order_by=created_at&page=1")
                    .status(OK)
                    .responsePayload("v3/apps/GET_response.json");
        }

        @Override
        protected ListApplicationsResponse getResponse() {
            return ListApplicationsResponse.builder()
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
        }

        @Override
        protected ListApplicationsRequest getValidRequest() throws Exception {
            return ListApplicationsRequest.builder()
                    .page(1)
                    .orderBy(CREATED_AT)
                    .name("test-name")
                    .build();
        }

        @Override
        protected Mono<ListApplicationsResponse> invoke(ListApplicationsRequest request) {
            return this.applications.list(request);
        }

    }

    public static final class ListDroplets extends AbstractApiTest<ListApplicationDropletsRequest, ListApplicationDropletsResponse> {

        private final SpringApplicationsV3 applications = new SpringApplicationsV3(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListApplicationDropletsRequest getInvalidRequest() {
            return ListApplicationDropletsRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v3/apps/test-application-id/droplets?order_by=created_at&order_direction=asc&page=1&per_page=2")
                    .status(OK)
                    .responsePayload("v3/apps/GET_{id}_droplets_response.json");
        }

        @Override
        protected ListApplicationDropletsResponse getResponse() {
            return ListApplicationDropletsResponse.builder()
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
        }

        @Override
        protected ListApplicationDropletsRequest getValidRequest() throws Exception {
            return ListApplicationDropletsRequest.builder()
                    .page(1)
                    .perPage(2)
                    .orderBy(CREATED_AT)
                    .orderDirection(ASC)
                    .applicationId("test-application-id")
                    .build();
        }

        @Override
        protected Mono<ListApplicationDropletsResponse> invoke(ListApplicationDropletsRequest request) {
            return this.applications.listDroplets(request);
        }

    }

    public static final class ListPackages extends AbstractApiTest<ListApplicationPackagesRequest, ListApplicationPackagesResponse> {

        private final SpringApplicationsV3 applications = new SpringApplicationsV3(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListApplicationPackagesRequest getInvalidRequest() {
            return ListApplicationPackagesRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v3/apps/test-application-id/packages")
                    .status(OK)
                    .responsePayload("v3/apps/GET_{id}_packages_response.json");
        }

        @Override
        protected ListApplicationPackagesResponse getResponse() {
            return ListApplicationPackagesResponse.builder()
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
        }

        @Override
        protected ListApplicationPackagesRequest getValidRequest() throws Exception {
            return ListApplicationPackagesRequest.builder()
                    .page(1)
                    .applicationId("test-application-id")
                    .build();
        }

        @Override
        protected Mono<ListApplicationPackagesResponse> invoke(ListApplicationPackagesRequest request) {
            return this.applications.listPackages(request);
        }

    }

    public static final class ListProcesses extends AbstractApiTest<ListApplicationProcessesRequest, ListApplicationProcessesResponse> {

        private final SpringApplicationsV3 applications = new SpringApplicationsV3(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListApplicationProcessesRequest getInvalidRequest() {
            return ListApplicationProcessesRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v3/apps/test-application-id/processes")
                    .status(OK)
                    .responsePayload("v3/apps/GET_{id}_processes_response.json");
        }

        @Override
        protected ListApplicationProcessesResponse getResponse() {
            return ListApplicationProcessesResponse.builder()
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
        }

        @Override
        protected ListApplicationProcessesRequest getValidRequest() throws Exception {
            return ListApplicationProcessesRequest.builder()
                    .page(1)
                    .applicationId("test-application-id")
                    .build();
        }

        @Override
        protected Mono<ListApplicationProcessesResponse> invoke(ListApplicationProcessesRequest request) {
            return this.applications.listProcesses(request);
        }

    }

    public static final class ListRoutes extends AbstractApiTest<ListApplicationRoutesRequest, ListApplicationRoutesResponse> {

        private final SpringApplicationsV3 applications = new SpringApplicationsV3(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListApplicationRoutesRequest getInvalidRequest() {
            return ListApplicationRoutesRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v3/apps/test-application-id/routes")
                    .status(OK)
                    .responsePayload("v3/apps/GET_{id}_routes_response.json");
        }

        @Override
        protected ListApplicationRoutesResponse getResponse() {
            return ListApplicationRoutesResponse.builder()
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
        }

        @Override
        protected ListApplicationRoutesRequest getValidRequest() throws Exception {
            return ListApplicationRoutesRequest.builder()
                    .applicationId("test-application-id")
                    .build();
        }

        @Override
        protected Mono<ListApplicationRoutesResponse> invoke(ListApplicationRoutesRequest request) {
            return this.applications.listRoutes(request);
        }

    }

    public static final class MapRoute extends AbstractApiTest<MapApplicationRouteRequest, Void> {

        private final SpringApplicationsV3 applications = new SpringApplicationsV3(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected MapApplicationRouteRequest getInvalidRequest() {
            return MapApplicationRouteRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(PUT).path("/v3/apps/test-application-id/routes")
                    .requestPayload("v3/apps/PUT_{id}_routes_request.json")
                    .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected MapApplicationRouteRequest getValidRequest() throws Exception {
            return MapApplicationRouteRequest.builder()
                    .applicationId("test-application-id")
                    .routeId("9cf0271a-420f-4ae4-b227-16683db93573")
                    .build();
        }

        @Override
        protected Mono<Void> invoke(MapApplicationRouteRequest request) {
            return this.applications.mapRoute(request);
        }

    }

    public static final class Scale extends AbstractApiTest<ScaleApplicationRequest, ScaleApplicationResponse> {

        private final SpringApplicationsV3 applications = new SpringApplicationsV3(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ScaleApplicationRequest getInvalidRequest() {
            return ScaleApplicationRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(PUT).path("/v3/apps/test-application-id/processes/web/scale")
                    .requestPayload("v3/apps/PUT_{id}_processes_{type}_scale_request.json")
                    .status(OK)
                    .responsePayload("v3/apps/PUT_{id}_processes_{type}_scale_response.json");
        }

        @Override
        protected ScaleApplicationResponse getResponse() {
            return ScaleApplicationResponse.builder()
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
        }

        @Override
        protected ScaleApplicationRequest getValidRequest() throws Exception {
            return ScaleApplicationRequest.builder()
                    .diskInMb(100)
                    .applicationId("test-application-id")
                    .instances(3)
                    .memoryInMb(100)
                    .type("web")
                    .build();
        }

        @Override
        protected Mono<ScaleApplicationResponse> invoke(ScaleApplicationRequest request) {
            return this.applications.scale(request);
        }

    }

    public static final class Start extends AbstractApiTest<StartApplicationRequest, StartApplicationResponse> {

        private final SpringApplicationsV3 applications = new SpringApplicationsV3(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected StartApplicationRequest getInvalidRequest() {
            return StartApplicationRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(PUT).path("/v3/apps/test-application-id/start")
                    .status(OK)
                    .responsePayload("v3/apps/PUT_{id}_start_response.json");
        }

        @Override
        protected StartApplicationResponse getResponse() {
            return StartApplicationResponse.builder()
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
        }

        @Override
        protected StartApplicationRequest getValidRequest() throws Exception {
            return StartApplicationRequest.builder()
                    .applicationId("test-application-id")
                    .build();
        }

        @Override
        protected Mono<StartApplicationResponse> invoke(StartApplicationRequest request) {
            return this.applications.start(request);
        }

    }

    public static final class Stop extends AbstractApiTest<StopApplicationRequest, StopApplicationResponse> {

        private final SpringApplicationsV3 applications = new SpringApplicationsV3(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected StopApplicationRequest getInvalidRequest() {
            return StopApplicationRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(PUT).path("/v3/apps/test-application-id/stop")
                    .status(OK)
                    .responsePayload("v3/apps/PUT_{id}_stop_response.json");
        }

        @Override
        protected StopApplicationResponse getResponse() {
            return StopApplicationResponse.builder()
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
        }

        @Override
        protected StopApplicationRequest getValidRequest() throws Exception {
            return StopApplicationRequest.builder()
                    .applicationId("test-application-id")
                    .build();
        }

        @Override
        protected Mono<StopApplicationResponse> invoke(StopApplicationRequest request) {
            return this.applications.stop(request);
        }

    }

    public static final class UnmapRoute extends AbstractApiTest<UnmapApplicationRouteRequest, Void> {

        private final SpringApplicationsV3 applications = new SpringApplicationsV3(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected UnmapApplicationRouteRequest getInvalidRequest() {
            return UnmapApplicationRouteRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(DELETE).path("/v3/apps/test-application-id/routes")
                    .requestPayload("v3/apps/DELETE_{id}_routes_request.json")
                    .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected UnmapApplicationRouteRequest getValidRequest() throws Exception {
            return UnmapApplicationRouteRequest.builder()
                    .applicationId("test-application-id")
                    .routeId("3f0121a8-54e1-45c0-8daf-44d0f8ba1091")
                    .build();
        }

        @Override
        protected Mono<Void> invoke(UnmapApplicationRouteRequest request) {
            return this.applications.unmapRoute(request);
        }

    }

    public static final class Update extends AbstractApiTest<UpdateApplicationRequest, UpdateApplicationResponse> {

        private final SpringApplicationsV3 applications = new SpringApplicationsV3(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected UpdateApplicationRequest getInvalidRequest() {
            return UpdateApplicationRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(PATCH).path("/v3/apps/test-application-id")
                    .requestPayload("v3/apps/PATCH_{id}_request.json")
                    .status(OK)
                    .responsePayload("v3/apps/PATCH_{id}_response.json");
        }

        @Override
        protected UpdateApplicationResponse getResponse() {
            return UpdateApplicationResponse.builder()
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
        }

        @Override
        protected UpdateApplicationRequest getValidRequest() throws Exception {
            return UpdateApplicationRequest.builder()
                    .name("new_name")
                    .environmentVariable("MY_ENV_VAR", "foobar")
                    .environmentVariable("FOOBAR", "MY_ENV_VAR")
                    .buildpack("http://gitwheel.org/my-app")
                    .applicationId("test-application-id")
                    .build();
        }

        @Override
        protected Mono<UpdateApplicationResponse> invoke(UpdateApplicationRequest request) {
            return this.applications.update(request);
        }

    }

}

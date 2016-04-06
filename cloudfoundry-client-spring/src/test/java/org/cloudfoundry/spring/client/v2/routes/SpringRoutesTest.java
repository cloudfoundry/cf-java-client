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

package org.cloudfoundry.spring.client.v2.routes;

import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.routes.AssociateRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.AssociateRouteApplicationResponse;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.routes.DeleteRouteResponse;
import org.cloudfoundry.client.v2.routes.GetRouteRequest;
import org.cloudfoundry.client.v2.routes.GetRouteResponse;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsResponse;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.client.v2.routes.ListRoutesResponse;
import org.cloudfoundry.client.v2.routes.RemoveRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.routes.RouteExistsRequest;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.routes.UpdateRouteRequest;
import org.cloudfoundry.client.v2.routes.UpdateRouteResponse;
import org.cloudfoundry.spring.AbstractApiTest;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public final class SpringRoutesTest {

    public static final class AssociateApplication extends AbstractApiTest<AssociateRouteApplicationRequest, AssociateRouteApplicationResponse> {

        private final SpringRoutes routes = new SpringRoutes(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected AssociateRouteApplicationRequest getInvalidRequest() {
            return AssociateRouteApplicationRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v2/routes/test-route-id/apps/test-app-id")
                .status(OK)
                .responsePayload("fixtures/client/v2/routes/PUT_{id}_apps_{app-id}_response.json");
        }

        @Override
        protected AssociateRouteApplicationResponse getResponse() {
            return AssociateRouteApplicationResponse.builder()
                .metadata(Resource.Metadata.builder()
                    .id("a05c94a3-e4b3-456e-b044-475270919ea7")
                    .url("/v2/routes/a05c94a3-e4b3-456e-b044-475270919ea7")
                    .createdAt("2016-03-17T21:41:19Z")
                    .build())
                .entity(RouteEntity.builder()
                    .host("host-20")
                    .path("")
                    .port(0)
                    .domainId("f5804a26-df58-412e-95ed-fa2f2a699c18")
                    .spaceId("4886c9ec-4b1a-4a4c-8c8f-acfdd3d97d22")
                    .domainUrl("/v2/domains/f5804a26-df58-412e-95ed-fa2f2a699c18")
                    .spaceUrl("/v2/spaces/4886c9ec-4b1a-4a4c-8c8f-acfdd3d97d22")
                    .applicationsUrl("/v2/routes/a05c94a3-e4b3-456e-b044-475270919ea7/apps")
                    .routeMappingsUrl("/v2/routes/a05c94a3-e4b3-456e-b044-475270919ea7/route_mappings")
                    .build())
                .build();
        }

        @Override
        protected AssociateRouteApplicationRequest getValidRequest() throws Exception {
            return AssociateRouteApplicationRequest.builder()
                .applicationId("test-app-id")
                .routeId("test-route-id")
                .build();
        }

        @Override
        protected Mono<AssociateRouteApplicationResponse> invoke(AssociateRouteApplicationRequest request) {
            return this.routes.associateApplication(request);
        }

    }

    public static final class Create extends AbstractApiTest<CreateRouteRequest, CreateRouteResponse> {

        private final SpringRoutes routes = new SpringRoutes(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreateRouteRequest getInvalidRequest() {
            return CreateRouteRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(POST).path("/v2/routes")
                .requestPayload("fixtures/client/v2/routes/POST_request.json")
                .status(OK)
                .responsePayload("fixtures/client/v2/routes/POST_response.json");
        }

        @Override
        protected CreateRouteResponse getResponse() {
            return CreateRouteResponse.builder()
                .metadata(Resource.Metadata.builder()
                    .id("ad307f5b-efec-4034-8cf1-1f86515ee093")
                    .url("/v2/routes/ad307f5b-efec-4034-8cf1-1f86515ee093")
                    .createdAt("2016-03-17T21:41:19Z")
                    .build())
                .entity(RouteEntity.builder()
                    .host("")
                    .path("")
                    .domainId("c94d8b7e-081e-4293-8f24-964a2fe2b16c")
                    .spaceId("759cb2e0-239b-4202-ab83-1e6fd66becee")
                    .port(10000)
                    .domainUrl("/v2/domains/c94d8b7e-081e-4293-8f24-964a2fe2b16c")
                    .spaceUrl("/v2/spaces/759cb2e0-239b-4202-ab83-1e6fd66becee")
                    .applicationsUrl("/v2/routes/ad307f5b-efec-4034-8cf1-1f86515ee093/apps")
                    .routeMappingsUrl("/v2/routes/ad307f5b-efec-4034-8cf1-1f86515ee093/route_mappings")
                    .build())
                .build();
        }

        @Override
        protected CreateRouteRequest getValidRequest() throws Exception {
            return CreateRouteRequest.builder()
                .domainId("4d9e6314-58ca-4f09-a736-d8bcc903b95e")
                .port(10000)
                .spaceId("2f093daf-c030-4b57-99c2-9b8858b200e4")
                .build();
        }

        @Override
        protected Mono<CreateRouteResponse> invoke(CreateRouteRequest request) {
            return this.routes.create(request);
        }

    }

    public static final class Delete extends AbstractApiTest<DeleteRouteRequest, DeleteRouteResponse> {

        private final SpringRoutes routes = new SpringRoutes(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteRouteRequest getInvalidRequest() {
            return DeleteRouteRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/routes/test-route-id")
                .status(NO_CONTENT);
        }

        @Override
        protected DeleteRouteResponse getResponse() {
            return null;
        }

        @Override
        protected DeleteRouteRequest getValidRequest() throws Exception {
            return DeleteRouteRequest.builder()
                .routeId("test-route-id")
                .build();
        }

        @Override
        protected Mono<DeleteRouteResponse> invoke(DeleteRouteRequest request) {
            return this.routes.delete(request);
        }

    }

    public static final class DeleteAsync extends AbstractApiTest<DeleteRouteRequest, DeleteRouteResponse> {

        private final SpringRoutes routes = new SpringRoutes(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteRouteRequest getInvalidRequest() {
            return DeleteRouteRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/routes/test-route-id?async=true")
                .status(ACCEPTED)
                .responsePayload("fixtures/client/v2/routes/DELETE_{id}_async_response.json");
        }

        @Override
        protected DeleteRouteResponse getResponse() {
            return DeleteRouteResponse.builder()
                .metadata(Resource.Metadata.builder()
                    .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .createdAt("2016-02-02T17:16:31Z")
                    .url("/v2/jobs/2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .build())
                .entity(JobEntity.builder()
                    .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .status("queued")
                    .build())
                .build();
        }

        @Override
        protected DeleteRouteRequest getValidRequest() throws Exception {
            return DeleteRouteRequest.builder()
                .async(true)
                .routeId("test-route-id")
                .build();
        }

        @Override
        protected Mono<DeleteRouteResponse> invoke(DeleteRouteRequest request) {
            return this.routes.delete(request);
        }

    }

    public static final class Exists extends AbstractApiTest<RouteExistsRequest, Boolean> {

        private final SpringRoutes routes = new SpringRoutes(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RouteExistsRequest getInvalidRequest() {
            return RouteExistsRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/routes/reserved/domain/test-domain-id/host/test-host?path=test-path")
                .status(NO_CONTENT);
        }

        @Override
        protected Boolean getResponse() {
            return true;
        }

        @Override
        protected RouteExistsRequest getValidRequest() throws Exception {
            return RouteExistsRequest.builder()
                .domainId("test-domain-id")
                .host("test-host")
                .path("test-path")
                .build();
        }

        @Override
        protected Mono<Boolean> invoke(RouteExistsRequest request) {
            return this.routes.exists(request);
        }
    }

    public static final class Get extends AbstractApiTest<GetRouteRequest, GetRouteResponse> {

        private final SpringRoutes routes = new SpringRoutes(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetRouteRequest getInvalidRequest() {
            return GetRouteRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/routes/test-route-id")
                .status(OK)
                .responsePayload("fixtures/client/v2/routes/GET_{id}_response.json");
        }

        @Override
        protected GetRouteResponse getResponse() {
            return GetRouteResponse.builder()
                .metadata(Resource.Metadata.builder()
                    .id("e6c3ae35-9083-4816-9432-ee8ad700fd03")
                    .url("/v2/routes/e6c3ae35-9083-4816-9432-ee8ad700fd03")
                    .createdAt("2016-03-17T21:41:19Z")
                    .build())
                .entity(RouteEntity.builder()
                    .host("host-24")
                    .path("")
                    .domainId("f268feb8-2dec-4709-8b2d-db2e6c764093")
                    .spaceId("dd4e9e11-1b61-44a9-a4c5-6a5eb393c2a0")
                    .serviceInstanceId("492eb6e7-820e-40dc-847c-6b30a6fc7b64")
                    .port(0)
                    .domainUrl("/v2/domains/f268feb8-2dec-4709-8b2d-db2e6c764093")
                    .spaceUrl("/v2/spaces/dd4e9e11-1b61-44a9-a4c5-6a5eb393c2a0")
                    .serviceInstanceUrl("/v2/service_instances/492eb6e7-820e-40dc-847c-6b30a6fc7b64")
                    .applicationsUrl("/v2/routes/e6c3ae35-9083-4816-9432-ee8ad700fd03/apps")
                    .routeMappingsUrl("/v2/routes/e6c3ae35-9083-4816-9432-ee8ad700fd03/route_mappings")
                    .build())
                .build();
        }

        @Override
        protected GetRouteRequest getValidRequest() throws Exception {
            return GetRouteRequest.builder()
                .routeId("test-route-id")
                .build();
        }

        @Override
        protected Mono<GetRouteResponse> invoke(GetRouteRequest request) {
            return this.routes.get(request);
        }

    }

    public static final class List extends AbstractApiTest<ListRoutesRequest, ListRoutesResponse> {

        private final SpringRoutes routes = new SpringRoutes(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListRoutesRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/routes?page=-1")
                .status(OK)
                .responsePayload("fixtures/client/v2/routes/GET_response.json");
        }

        @Override
        protected ListRoutesResponse getResponse() {
            return ListRoutesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(RouteResource.builder()
                    .metadata(Resource.Metadata.builder()
                        .id("8fd7433e-e9c7-4897-809f-9a9696f72986")
                        .url("/v2/routes/8fd7433e-e9c7-4897-809f-9a9696f72986")
                        .createdAt("2016-03-17T21:41:19Z")
                        .build())
                    .entity(RouteEntity.builder()
                        .host("host-25")
                        .path("")
                        .domainId("76d083f5-a5cc-4179-81b8-530a134cccf6")
                        .spaceId("34453e18-fe59-4208-b29c-ae9f7b46985c")
                        .serviceInstanceId("8479be64-245d-4385-a553-593ffcc6b886")
                        .port(0)
                        .domainUrl("/v2/domains/76d083f5-a5cc-4179-81b8-530a134cccf6")
                        .spaceUrl("/v2/spaces/34453e18-fe59-4208-b29c-ae9f7b46985c")
                        .serviceInstanceUrl("/v2/service_instances/8479be64-245d-4385-a553-593ffcc6b886")
                        .applicationsUrl("/v2/routes/8fd7433e-e9c7-4897-809f-9a9696f72986/apps")
                        .routeMappingsUrl("/v2/routes/8fd7433e-e9c7-4897-809f-9a9696f72986/route_mappings")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListRoutesRequest getValidRequest() {
            return ListRoutesRequest.builder()
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListRoutesResponse> invoke(ListRoutesRequest request) {
            return this.routes.list(request);
        }

    }

    public static final class ListApplications extends AbstractApiTest<ListRouteApplicationsRequest, ListRouteApplicationsResponse> {

        private final SpringRoutes routes = new SpringRoutes(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListRouteApplicationsRequest getInvalidRequest() {
            return ListRouteApplicationsRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/routes/test-route-id/apps?page=-1")
                .status(OK)
                .responsePayload("fixtures/client/v2/routes/GET_{id}_apps_response.json");
        }

        @Override
        protected ListRouteApplicationsResponse getResponse() {
            return ListRouteApplicationsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ApplicationResource.builder()
                    .metadata(Resource.Metadata.builder()
                        .id("f1243da8-e613-490a-8a0e-21ef1bcce952")
                        .url("/v2/apps/f1243da8-e613-490a-8a0e-21ef1bcce952")
                        .createdAt("2015-11-30T23:38:56Z")
                        .updatedAt("2015-11-30T23:38:56Z")
                        .build())
                    .entity(ApplicationEntity.builder()
                        .name("name-2404")
                        .production(false)
                        .spaceId("55f1c5ea-12a5-4128-8f20-606af2a3bce1")
                        .stackId("0ef84d2a-4fdd-43ba-afbc-074a5e19ea66")
                        .memory(1024)
                        .instances(1)
                        .diskQuota(1024)
                        .state("STOPPED")
                        .version("5c7c81b2-941b-48a6-b718-c57c02a5f802")
                        .console(false)
                        .packageState("PENDING")
                        .healthCheckType("port")
                        .diego(false)
                        .packageUpdatedAt("2015-11-30T23:38:56Z")
                        .detectedStartCommand("")
                        .enableSsh(true)
                        .dockerCredentialsJson("redacted_message", "[PRIVATE DATA HIDDEN]")
                        .spaceUrl("/v2/spaces/55f1c5ea-12a5-4128-8f20-606af2a3bce1")
                        .stackUrl("/v2/stacks/0ef84d2a-4fdd-43ba-afbc-074a5e19ea66")
                        .eventsUrl("/v2/apps/f1243da8-e613-490a-8a0e-21ef1bcce952/events")
                        .serviceBindingsUrl("/v2/apps/f1243da8-e613-490a-8a0e-21ef1bcce952/service_bindings")
                        .routesUrl("/v2/apps/f1243da8-e613-490a-8a0e-21ef1bcce952/routes")
                        .routeMappingsUrl("/v2/apps/f1243da8-e613-490a-8a0e-21ef1bcce952/route_mappings")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListRouteApplicationsRequest getValidRequest() {
            return ListRouteApplicationsRequest.builder()
                .routeId("test-route-id")
                .page(-1)
                .build();
        }

        @Override
        protected Mono<ListRouteApplicationsResponse> invoke(ListRouteApplicationsRequest request) {
            return this.routes.listApplications(request);
        }

    }

    public static final class RemoveApplication extends AbstractApiTest<RemoveRouteApplicationRequest, Void> {

        private final SpringRoutes routes = new SpringRoutes(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected RemoveRouteApplicationRequest getInvalidRequest() {
            return RemoveRouteApplicationRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/routes/test-route-id/apps/test-app-id")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected RemoveRouteApplicationRequest getValidRequest() throws Exception {
            return RemoveRouteApplicationRequest.builder()
                .applicationId("test-app-id")
                .routeId("test-route-id")
                .build();
        }

        @Override
        protected Mono<Void> invoke(RemoveRouteApplicationRequest request) {
            return this.routes.removeApplication(request);
        }

    }

    public static final class Update extends AbstractApiTest<UpdateRouteRequest, UpdateRouteResponse> {

        private final SpringRoutes routes = new SpringRoutes(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected UpdateRouteRequest getInvalidRequest() {
            return UpdateRouteRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(PUT).path("/v2/routes/test-route-id")
                .requestPayload("fixtures/client/v2/routes/PUT_{id}_request.json")
                .status(CREATED)
                .responsePayload("fixtures/client/v2/routes/PUT_{id}_response.json");
        }

        @Override
        protected UpdateRouteResponse getResponse() {
            return UpdateRouteResponse.builder()
                .metadata(Resource.Metadata.builder()
                    .id("fc72a0ae-374b-4f06-b96d-38a3864ee91b")
                    .url("/v2/routes/fc72a0ae-374b-4f06-b96d-38a3864ee91b")
                    .createdAt("2016-03-17T21:41:19Z")
                    .updatedAt("2016-03-17T21:41:19Z")
                    .build())
                .entity(RouteEntity.builder()
                    .host("host-23")
                    .path("")
                    .domainId("6077a91b-66f9-4c52-be1f-f7b4b17a8c0d")
                    .spaceId("afed22fe-2b38-4976-9a7d-b81356c82531")
                    .port(10000)
                    .domainUrl("/v2/domains/6077a91b-66f9-4c52-be1f-f7b4b17a8c0d")
                    .spaceUrl("/v2/spaces/afed22fe-2b38-4976-9a7d-b81356c82531")
                    .applicationsUrl("/v2/routes/fc72a0ae-374b-4f06-b96d-38a3864ee91b/apps")
                    .routeMappingsUrl("/v2/routes/fc72a0ae-374b-4f06-b96d-38a3864ee91b/route_mappings")
                    .build())
                .build();
        }

        @Override
        protected UpdateRouteRequest getValidRequest() throws Exception {
            return UpdateRouteRequest.builder()
                .routeId("test-route-id")
                .port(10000)
                .build();
        }

        @Override
        protected Mono<UpdateRouteResponse> invoke(UpdateRouteRequest request) {
            return this.routes.update(request);
        }

    }

}

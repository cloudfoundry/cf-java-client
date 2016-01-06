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

package org.cloudfoundry.client.spring.v2.routes;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.routes.AssociateRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.AssociateRouteApplicationResponse;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
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
import reactor.Mono;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
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
                    .method(PUT).path("v2/routes/test-id/apps/test-app-id")
                    .status(OK)
                    .responsePayload("v2/routes/PUT_{id}_apps_{app-id}_response.json");
        }

        @Override
        protected AssociateRouteApplicationResponse getResponse() {
            return AssociateRouteApplicationResponse.builder()
                    .metadata(Resource.Metadata.builder()
                            .id("b1b30135-ac98-446e-aee8-48bb5cda0bf1")
                            .url("/v2/routes/b1b30135-ac98-446e-aee8-48bb5cda0bf1")
                            .createdAt("2015-11-30T23:38:56Z")
                            .build())
                    .entity(RouteEntity.builder()
                            .host("host-21")
                            .path("")
                            .port(0)
                            .domainId("d6833723-9bee-4890-b599-e1c3e50a85c3")
                            .spaceId("3e12f626-026f-4a07-aef7-bb4b5cd35cca")
                            .domainUrl("/v2/domains/d6833723-9bee-4890-b599-e1c3e50a85c3")
                            .spaceUrl("/v2/spaces/3e12f626-026f-4a07-aef7-bb4b5cd35cca")
                            .applicationsUrl("/v2/routes/b1b30135-ac98-446e-aee8-48bb5cda0bf1/apps")
                            .build())
                    .build();
        }

        @Override
        protected AssociateRouteApplicationRequest getValidRequest() throws Exception {
            return AssociateRouteApplicationRequest.builder()
                    .applicationId("test-app-id")
                    .id("test-id")
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
                    .method(POST).path("v2/routes")
                    .requestPayload("v2/routes/POST_request.json")
                    .status(OK)
                    .responsePayload("v2/routes/POST_response.json");
        }

        @Override
        protected CreateRouteResponse getResponse() {
            return CreateRouteResponse.builder()
                    .metadata(Resource.Metadata.builder()
                            .id("e689dab1-c4e7-4499-b708-7c649949e86d")
                            .url("/v2/routes/e689dab1-c4e7-4499-b708-7c649949e86d")
                            .createdAt("2015-11-30T23:38:55Z")
                            .build())
                    .entity(RouteEntity.builder()
                            .host("")
                            .path("")
                            .port(10000)
                            .domainId("4d9e6314-58ca-4f09-a736-d8bcc903b95e")
                            .spaceId("2f093daf-c030-4b57-99c2-9b8858b200e4")
                            .domainUrl("/v2/domains/4d9e6314-58ca-4f09-a736-d8bcc903b95e")
                            .spaceUrl("/v2/spaces/2f093daf-c030-4b57-99c2-9b8858b200e4")
                            .applicationsUrl("/v2/routes/e689dab1-c4e7-4499-b708-7c649949e86d/apps")
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

    public static final class Delete extends AbstractApiTest<DeleteRouteRequest, Void> {

        private final SpringRoutes routes = new SpringRoutes(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteRouteRequest getInvalidRequest() {
            return DeleteRouteRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(DELETE).path("v2/routes/test-id")
                    .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected DeleteRouteRequest getValidRequest() throws Exception {
            return DeleteRouteRequest.builder()
                    .id("test-id")
                    .build();
        }

        @Override
        protected Mono<Void> invoke(DeleteRouteRequest request) {
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
                    .method(GET).path("v2/routes/reserved/domain/test-domain-id/host/test-host?path=test-path")
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
                    .method(GET).path("v2/routes/test-id")
                    .status(OK)
                    .responsePayload("v2/routes/GET_{id}_response.json");
        }

        @Override
        protected GetRouteResponse getResponse() {
            return GetRouteResponse.builder()
                    .metadata(Resource.Metadata.builder()
                            .id("75c16cfe-9b8a-4faf-bb65-02c713c7956f")
                            .url("/v2/routes/75c16cfe-9b8a-4faf-bb65-02c713c7956f")
                            .createdAt("2015-11-30T23:38:56Z")
                            .build())
                    .entity(RouteEntity.builder()
                            .host("host-18")
                            .path("")
                            .port(0)
                            .domainId("a284da28-3a0b-4e46-8c2f-a4b28f76a09b")
                            .spaceId("b3f94ab9-1520-478b-a6d6-eb467c179ada")
                            .domainUrl("/v2/domains/a284da28-3a0b-4e46-8c2f-a4b28f76a09b")
                            .spaceUrl("/v2/spaces/b3f94ab9-1520-478b-a6d6-eb467c179ada")
                            .serviceInstanceId("e3db4ea8-ab0c-4c47-adf8-a70a8e990ee4")
                            .serviceInstanceUrl("/v2/service_instances/e3db4ea8-ab0c-4c47-adf8-a70a8e990ee4")
                            .applicationsUrl("/v2/routes/75c16cfe-9b8a-4faf-bb65-02c713c7956f/apps")
                            .build())
                    .build();
        }

        @Override
        protected GetRouteRequest getValidRequest() throws Exception {
            return GetRouteRequest.builder()
                    .id("test-id")
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
                    .method(GET).path("v2/routes?page=-1")
                    .status(OK)
                    .responsePayload("v2/routes/GET_response.json");
        }

        @Override
        protected ListRoutesResponse getResponse() {
            return ListRoutesResponse.builder()
                    .totalResults(1)
                    .totalPages(1)
                    .resource(RouteResource.builder()
                            .metadata(Resource.Metadata.builder()
                                    .id("13f2cca3-ea79-4b46-9ffd-99a490c84e2b")
                                    .url("/v2/routes/13f2cca3-ea79-4b46-9ffd-99a490c84e2b")
                                    .createdAt("2015-07-27T22:43:11Z")
                                    .build())
                            .entity(RouteEntity.builder()
                                    .host("host-6")
                                    .path("")
                                    .domainId("c3153192-e35d-4d1e-a518-ddaddf39241c")
                                    .spaceId("d56f6482-363c-4de9-9520-d16b3754c389")
                                    .domainUrl("/v2/domains/c3153192-e35d-4d1e-a518-ddaddf39241c")
                                    .spaceUrl("/v2/spaces/d56f6482-363c-4de9-9520-d16b3754c389")
                                    .applicationsUrl("/v2/routes/13f2cca3-ea79-4b46-9ffd-99a490c84e2b/apps")
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
                    .method(GET).path("v2/routes/test-id/apps?page=-1")
                    .status(OK)
                    .responsePayload("v2/routes/GET_{id}_apps_response.json");
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
                                    .build())
                            .build())
                    .build();
        }

        @Override
        protected ListRouteApplicationsRequest getValidRequest() {
            return ListRouteApplicationsRequest.builder()
                    .id("test-id")
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
                    .method(DELETE).path("v2/routes/test-id/apps/test-app-id")
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
                    .id("test-id")
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
                    .method(PUT).path("v2/routes/test-id")
                    .requestPayload("v2/routes/PUT_{id}_request.json")
                    .status(CREATED)
                    .responsePayload("v2/routes/PUT_{id}_response.json");
        }

        @Override
        protected UpdateRouteResponse getResponse() {
            return UpdateRouteResponse.builder()
                    .metadata(Resource.Metadata.builder()
                            .id("1df59bcc-a47c-4762-b793-61f1b2e6e5f3")
                            .url("/v2/routes/1df59bcc-a47c-4762-b793-61f1b2e6e5f3")
                            .createdAt("2015-11-30T23:38:55Z")
                            .updatedAt("2015-11-30T23:38:55Z")
                            .build())
                    .entity(RouteEntity.builder()
                            .host("host-15")
                            .path("")
                            .port(10000)
                            .domainId("fef11ae6-30cd-4d0e-88b7-ef7737d0c6f6")
                            .spaceId("8a27c503-19a8-4704-939c-aac293ac3add")
                            .domainUrl("/v2/domains/fef11ae6-30cd-4d0e-88b7-ef7737d0c6f6")
                            .spaceUrl("/v2/spaces/8a27c503-19a8-4704-939c-aac293ac3add")
                            .applicationsUrl("/v2/routes/1df59bcc-a47c-4762-b793-61f1b2e6e5f3/apps")
                            .build())
                    .build();
        }

        @Override
        protected UpdateRouteRequest getValidRequest() throws Exception {
            return UpdateRouteRequest.builder()
                    .id("test-id")
                    .port(10000)
                    .build();
        }

        @Override
        protected Mono<UpdateRouteResponse> invoke(UpdateRouteRequest request) {
            return this.routes.update(request);
        }

    }

}

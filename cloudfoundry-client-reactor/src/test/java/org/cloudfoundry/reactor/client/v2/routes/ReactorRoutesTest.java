/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.reactor.client.v2.routes;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.DockerCredentials;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.routemappings.RouteMappingEntity;
import org.cloudfoundry.client.v2.routemappings.RouteMappingResource;
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
import org.cloudfoundry.client.v2.routes.ListRouteMappingsRequest;
import org.cloudfoundry.client.v2.routes.ListRouteMappingsResponse;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.client.v2.routes.ListRoutesResponse;
import org.cloudfoundry.client.v2.routes.RemoveRouteApplicationRequest;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.routes.RouteExistsRequest;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.routes.UpdateRouteRequest;
import org.cloudfoundry.client.v2.routes.UpdateRouteResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorRoutesTest extends AbstractClientApiTest {

    private final ReactorRoutes routes = new ReactorRoutes(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void associateApplication() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/routes/test-route-id/apps/test-app-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/routes/PUT_{id}_apps_{app-id}_response.json")
                .build())
            .build());

        this.routes
            .associateApplication(AssociateRouteApplicationRequest.builder()
                .applicationId("test-app-id")
                .routeId("test-route-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(AssociateRouteApplicationResponse.builder()
                .metadata(Metadata.builder()
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
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/routes")
                .payload("fixtures/client/v2/routes/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/routes/POST_response.json")
                .build())
            .build());

        this.routes
            .create(CreateRouteRequest.builder()
                .domainId("4d9e6314-58ca-4f09-a736-d8bcc903b95e")
                .port(10000)
                .spaceId("2f093daf-c030-4b57-99c2-9b8858b200e4")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateRouteResponse.builder()
                .metadata(Metadata.builder()
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
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/routes/test-route-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.routes
            .delete(DeleteRouteRequest.builder()
                .routeId("test-route-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteAsync() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/routes/test-route-id?async=true")
                .build())
            .response(TestResponse.builder()
                .status(ACCEPTED)
                .payload("fixtures/client/v2/routes/DELETE_{id}_async_response.json")
                .build())
            .build());

        this.routes
            .delete(DeleteRouteRequest.builder()
                .async(true)
                .routeId("test-route-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(DeleteRouteResponse.builder()
                .metadata(Metadata.builder()
                    .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .createdAt("2016-02-02T17:16:31Z")
                    .url("/v2/jobs/2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .build())
                .entity(JobEntity.builder()
                    .id("2d9707ba-6f0b-4aef-a3de-fe9bdcf0c9d1")
                    .status("queued")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void exists() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/routes/reserved/domain/test-domain-id/host/test-host?path=test-path")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.routes
            .exists(RouteExistsRequest.builder()
                .domainId("test-domain-id")
                .host("test-host")
                .path("test-path")
                .build())
            .as(StepVerifier::create)
            .expectNext(true)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/routes/test-route-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/routes/GET_{id}_response.json")
                .build())
            .build());

        this.routes
            .get(GetRouteRequest.builder()
                .routeId("test-route-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetRouteResponse.builder()
                .metadata(Metadata.builder()
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
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/routes?page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/routes/GET_response.json")
                .build())
            .build());

        this.routes
            .list(ListRoutesRequest.builder()
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListRoutesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(RouteResource.builder()
                    .metadata(Metadata.builder()
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
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void listApplications() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/routes/81464707-0f48-4ab9-87dc-667ef15489fb/apps?app_guid=6e62b293-f4c8-405a-be2b-b719e2848984")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/routes/GET_{id}_apps_response.json")
                .build())
            .build());

        this.routes
            .listApplications(ListRouteApplicationsRequest.builder()
                .routeId("81464707-0f48-4ab9-87dc-667ef15489fb")
                .applicationId("6e62b293-f4c8-405a-be2b-b719e2848984")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListRouteApplicationsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ApplicationResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2016-06-08T16:41:28Z")
                        .id("6141e57e-7636-480b-8f17-78c6049813f6")
                        .updatedAt("2016-06-08T16:41:28Z")
                        .url("/v2/apps/6141e57e-7636-480b-8f17-78c6049813f6")
                        .build())
                    .entity(ApplicationEntity.builder()
                        .console(false)
                        .detectedStartCommand("")
                        .diego(false)
                        .diskQuota(1024)
                        .dockerCredentials(DockerCredentials.builder().build())
                        .enableSsh(true)
                        .eventsUrl("/v2/apps/6141e57e-7636-480b-8f17-78c6049813f6/events")
                        .memory(1024)
                        .healthCheckType("port")
                        .instances(1)
                        .name("name-1412")
                        .packageState("PENDING")
                        .packageUpdatedAt("2016-06-08T16:41:28Z")
                        .production(false)
                        .routeMappingsUrl("/v2/apps/6141e57e-7636-480b-8f17-78c6049813f6/route_mappings")
                        .routesUrl("/v2/apps/6141e57e-7636-480b-8f17-78c6049813f6/routes")
                        .serviceBindingsUrl("/v2/apps/6141e57e-7636-480b-8f17-78c6049813f6/service_bindings")
                        .spaceId("93e43758-13fe-4751-8edc-caf225e27647")
                        .spaceUrl("/v2/spaces/93e43758-13fe-4751-8edc-caf225e27647")
                        .stackId("0459956d-e777-412d-af7e-d45f8d172edc")
                        .stackUrl("/v2/stacks/0459956d-e777-412d-af7e-d45f8d172edc")
                        .state("STOPPED")
                        .version("51207851-f39e-428e-9a16-1372f4d6d4f6")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listMappings() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/routes/521c375d-a7e2-4f87-9527-7fd1db1b2010/route_mappings")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/routes/GET_{id}_route_mappings_response.json")
                .build())
            .build());

        this.routes
            .listMappings(ListRouteMappingsRequest.builder()
                .routeId("521c375d-a7e2-4f87-9527-7fd1db1b2010")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListRouteMappingsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(RouteMappingResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2016-06-08T16:41:28Z")
                        .id("9feb9667-9249-44b7-9e4e-92157a2aaefb")
                        .updatedAt("2016-06-08T16:41:26Z")
                        .url("/v2/route_mappings/9feb9667-9249-44b7-9e4e-92157a2aaefb")
                        .build())
                    .entity(RouteMappingEntity.builder()
                        .applicationId("bf65b03d-5416-4603-9de2-ef74491d29b6")
                        .applicationUrl("/v2/apps/bf65b03d-5416-4603-9de2-ef74491d29b6")
                        .routeId("521c375d-a7e2-4f87-9527-7fd1db1b2010")
                        .routeUrl("/v2/routes/521c375d-a7e2-4f87-9527-7fd1db1b2010")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void removeApplication() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/routes/test-route-id/apps/test-app-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.routes
            .removeApplication(RemoveRouteApplicationRequest.builder()
                .applicationId("test-app-id")
                .routeId("test-route-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/routes/test-route-id")
                .payload("fixtures/client/v2/routes/PUT_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/routes/PUT_{id}_response.json")
                .build())
            .build());

        this.routes
            .update(UpdateRouteRequest.builder()
                .routeId("test-route-id")
                .port(10000)
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateRouteResponse.builder()
                .metadata(Metadata.builder()
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
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}

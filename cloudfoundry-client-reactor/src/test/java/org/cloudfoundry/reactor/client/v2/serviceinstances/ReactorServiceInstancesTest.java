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

package org.cloudfoundry.reactor.client.v2.serviceinstances;

import org.cloudfoundry.client.v2.MaintenanceInfo;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingEntity;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.serviceinstances.BindServiceInstanceRouteRequest;
import org.cloudfoundry.client.v2.serviceinstances.BindServiceInstanceRouteResponse;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceParametersRequest;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceParametersResponse;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstancePermissionsRequest;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstancePermissionsResponse;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.GetServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.LastOperation;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceRoutesRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceRoutesResponse;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceBindingsRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceBindingsResponse;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceKeysRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceKeysResponse;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesResponse;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceinstances.UnbindServiceInstanceRouteRequest;
import org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.UpdateServiceInstanceResponse;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeyEntity;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeyResource;
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

public final class ReactorServiceInstancesTest extends AbstractClientApiTest {

    private final ReactorServiceInstances serviceInstances = new ReactorServiceInstances(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void bindRoute() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT)
                .path("/service_instances/test-service-instance-id/routes/route-id")
                .payload("fixtures/client/v2/service_instances/PUT_{id}_routes_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/service_instances/PUT_{id}_routes_response.json")
                .build())
            .build());

        this.serviceInstances
            .bindRoute(BindServiceInstanceRouteRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .routeId("route-id")
                .parameter("the_service_broker", "wants this object")
                .build())
            .as(StepVerifier::create)
            .expectNext(BindServiceInstanceRouteResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2015-12-22T18:27:58Z")
                    .id("e7e5b08e-c530-4c1c-b420-fa0b09b3770d")
                    .url("/v2/service_instances/e7e5b08e-c530-4c1c-b420-fa0b09b3770d")
                    .build())
                .entity(ServiceInstanceEntity.builder()
                    .name("name-160")
                    .credential("creds-key-89", "creds-val-89")
                    .servicePlanId("957307f5-6811-4eba-8667-ffee5a704a4a")
                    .spaceId("36b01ada-ef02-4ff5-9f78-cd9e704211d2")
                    .type("managed_service_instance")
                    .tags(Collections.emptyList())
                    .spaceUrl("/v2/spaces/36b01ada-ef02-4ff5-9f78-cd9e704211d2")
                    .servicePlanUrl("/v2/service_plans/957307f5-6811-4eba-8667-ffee5a704a4a")
                    .serviceBindingsUrl("/v2/service_instances/e7e5b08e-c530-4c1c-b420-fa0b09b3770d/service_bindings")
                    .serviceKeysUrl("/v2/service_instances/e7e5b08e-c530-4c1c-b420-fa0b09b3770d/service_keys")
                    .routesUrl("/v2/service_instances/e7e5b08e-c530-4c1c-b420-fa0b09b3770d/routes")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST)
                .path("/service_instances?accepts_incomplete=true")
                .payload("fixtures/client/v2/service_instances/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/service_instances/POST_response.json")
                .build())
            .build());

        this.serviceInstances
            .create(CreateServiceInstanceRequest.builder()
                .acceptsIncomplete(true)
                .name("my-service-instance")
                .servicePlanId("2048a369-d2d3-48cf-bcfd-eaf9032fa0ab")
                .spaceId("86b29f7e-721d-4eb8-b34f-3b1d1eccdf23")
                .parameter("the_service_broker", "wants this object")
                .tag("accounting")
                .tag("mongodb")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateServiceInstanceResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2015-07-27T22:43:08Z")
                    .id("8b2b3c5e-c1ba-41d0-ac87-08c776cfc25a")
                    .url("/v2/service_instances/8b2b3c5e-c1ba-41d0-ac87-08c776cfc25a")
                    .build())
                .entity(ServiceInstanceEntity.builder()
                    .name("my-service-instance")
                    .credential("creds-key-356", "creds-val-356")
                    .servicePlanId("2048a369-d2d3-48cf-bcfd-eaf9032fa0ab")
                    .spaceId("86b29f7e-721d-4eb8-b34f-3b1d1eccdf23")
                    .type("managed_service_instance")
                    .lastOperation(LastOperation.builder()
                        .createdAt("2015-07-27T22:43:08Z")
                        .updatedAt("2015-07-27T22:43:08Z")
                        .description("")
                        .state("in progress")
                        .type("create")
                        .build())
                    .tag("accounting")
                    .tag("mongodb")
                    .spaceUrl("/v2/spaces/86b29f7e-721d-4eb8-b34f-3b1d1eccdf23")
                    .servicePlanUrl("/v2/service_plans/2048a369-d2d3-48cf-bcfd-eaf9032fa0ab")
                    .serviceBindingsUrl("/v2/service_instances/8b2b3c5e-c1ba-41d0-ac87-08c776cfc25a/service_bindings")
                    .serviceKeysUrl("/v2/service_instances/8b2b3c5e-c1ba-41d0-ac87-08c776cfc25a/service_keys")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE)
                .path("/service_instances/test-service-instance-id?accepts_incomplete=true&purge=true")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.serviceInstances
            .delete(DeleteServiceInstanceRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .acceptsIncomplete(true)
                .purge(true)
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteAcceptsIncomplete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE)
                .path("/service_instances/test-service-instance-id?accepts_incomplete=true")
                .build())
            .response(TestResponse.builder()
                .status(ACCEPTED)
                .payload("fixtures/client/v2/service_instances/DELETE_{id}_accepts_incomplete_response.json")
                .build())
            .build());

        this.serviceInstances
            .delete(DeleteServiceInstanceRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .acceptsIncomplete(true)
                .build())
            .as(StepVerifier::create)
            .expectNext(DeleteServiceInstanceResponse.builder()
                .metadata(Metadata.builder()
                    .id("2e20eccf-6828-4c56-81cb-28c0e295ce19")
                    .url("/v2/service_instances/2e20eccf-6828-4c56-81cb-28c0e295ce19")
                    .createdAt("2017-02-27T12:30:29Z")
                    .updatedAt("2017-02-27T12:30:29Z")
                    .build())
                .entity(ServiceInstanceEntity.builder()
                    .name("test-service")
                    .servicePlanId("07c64d77-4df5-4974-a4b2-3bc58cafcf0d")
                    .spaceId("840d3266-8547-40fe-986e-ffc20eaba235")
                    .dashboardUrl("http://test-dashboard-host/2e20eccf-6828-4c56-81cb-28c0e295ce19")
                    .type("managed_service_instance")
                    .lastOperation(LastOperation.builder()
                        .type("delete")
                        .state("in progress")
                        .description("")
                        .updatedAt("2017-02-27T12:30:59Z")
                        .createdAt("2017-02-27T12:30:59Z")
                        .build())
                    .spaceUrl("/v2/spaces/840d3266-8547-40fe-986e-ffc20eaba235")
                    .servicePlanUrl("/v2/service_plans/07c64d77-4df5-4974-a4b2-3bc58cafcf0d")
                    .serviceBindingsUrl("/v2/service_instances/2e20eccf-6828-4c56-81cb-28c0e295ce19/service_bindings")
                    .serviceKeysUrl("/v2/service_instances/2e20eccf-6828-4c56-81cb-28c0e295ce19/service_keys")
                    .routesUrl("/v2/service_instances/2e20eccf-6828-4c56-81cb-28c0e295ce19/routes")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteAsync() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE)
                .path("/service_instances/test-service-instance-id?async=true&purge=true")
                .build())
            .response(TestResponse.builder()
                .status(ACCEPTED)
                .payload("fixtures/client/v2/service_instances/DELETE_{id}_async_response.json")
                .build())
            .build());

        this.serviceInstances
            .delete(DeleteServiceInstanceRequest.builder()
                .async(true)
                .serviceInstanceId("test-service-instance-id")
                .purge(true)
                .build())
            .as(StepVerifier::create)
            .expectNext(DeleteServiceInstanceResponse.builder()
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
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/service_instances/test-service-instance-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_instances/GET_{id}_response.json")
                .build())
            .build());

        this.serviceInstances
            .get(GetServiceInstanceRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetServiceInstanceResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:29Z")
                    .id("0d632575-bb06-4ea5-bb19-a451a9644d92")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .url("/v2/service_instances/0d632575-bb06-4ea5-bb19-a451a9644d92")
                    .build())
                .entity(ServiceInstanceEntity.builder()
                    .credential("creds-key-38", "creds-val-38")
                    .lastOperation(LastOperation.builder()
                        .createdAt("2016-06-08T16:41:29Z")
                        .description("service broker-provided description")
                        .state("succeeded")
                        .type("create")
                        .updatedAt("2016-06-08T16:41:29Z")
                        .build())
                    .name("name-1508")
                    .routesUrl("/v2/service_instances/0d632575-bb06-4ea5-bb19-a451a9644d92/routes")
                    .serviceBindingsUrl("/v2/service_instances/0d632575-bb06-4ea5-bb19-a451a9644d92/service_bindings")
                    .serviceId("a14baddf-1ccc-5299-0152-ab9s49de4422")
                    .serviceInstanceParametersUrl("/v2/service_instances/0d632575-bb06-4ea5-bb19-a451a9644d92/parameters")
                    .serviceKeysUrl("/v2/service_instances/0d632575-bb06-4ea5-bb19-a451a9644d92/service_keys")
                    .servicePlanId("779d2df0-9cdd-48e8-9781-ea05301cedb1")
                    .servicePlanUrl("/v2/service_plans/779d2df0-9cdd-48e8-9781-ea05301cedb1")
                    .serviceUrl("/v2/services/a14baddf-1ccc-5299-0152-ab9s49de4422")
                    .sharedFromUrl("/v2/service_instances/0d632575-bb06-4ea5-bb19-a451a9644d92/shared_from")
                    .sharedToUrl("/v2/service_instances/0d632575-bb06-4ea5-bb19-a451a9644d92/shared_to")
                    .spaceId("38511660-89d9-4a6e-a889-c32c7e94f139")
                    .spaceUrl("/v2/spaces/38511660-89d9-4a6e-a889-c32c7e94f139")
                    .tag("accounting")
                    .tag("mongodb")
                    .type("managed_service_instance")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getParameters() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/service_instances/test-service-instance-id/parameters")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_instances/GET_{id}_parameters_response.json")
                .build())
            .build());

        this.serviceInstances
            .getParameters(GetServiceInstanceParametersRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetServiceInstanceParametersResponse.builder()
                .parameter("test-param-key-1", "test-param-value-1")
                .parameter("test-param-key-2", 12345)
                .parameter("test-param-key-3", false)
                .parameter("test-param-key-4", 3.141)
                .parameter("test-param-key-5", null)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getPermissions() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/service_instances/test-service-instance-id/permissions")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_instances/GET_{id}_permissions_response.json")
                .build())
            .build());

        this.serviceInstances
            .getPermissions(GetServiceInstancePermissionsRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetServiceInstancePermissionsResponse.builder()
                .manage(true)
                .read(true)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/service_instances?q=name%3Atest-name&page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_instances/GET_response.json")
                .build())
            .build());

        this.serviceInstances
            .list(ListServiceInstancesRequest.builder()
                .name("test-name")
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListServiceInstancesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ServiceInstanceResource.builder()
                    .metadata(Metadata.builder()
                        .id("24ec15f9-f6c7-434a-8893-51baab8408d8")
                        .url("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8")
                        .createdAt("2015-07-27T22:43:08Z")
                        .build())
                    .entity(ServiceInstanceEntity.builder()
                        .name("name-133")
                        .credential("creds-key-72", "creds-val-72")
                        .servicePlanId("2b53255a-8b40-4671-803d-21d3f5d4183a")
                        .spaceId("83b3e705-49fd-4c40-8adf-f5e34f622a19")
                        .type("managed_service_instance")
                        .lastOperation(LastOperation.builder()
                            .type("create")
                            .state("succeeded")
                            .description("service broker-provided description")
                            .updatedAt("2015-07-27T22:43:08Z")
                            .createdAt("2015-07-27T22:43:08Z")
                            .build())
                        .tag("accounting")
                        .tag("mongodb")
                        .spaceUrl("/v2/spaces/83b3e705-49fd-4c40-8adf-f5e34f622a19")
                        .servicePlanUrl("/v2/service_plans/2b53255a-8b40-4671-803d-21d3f5d4183a")
                        .serviceBindingsUrl("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8/service_bindings")
                        .serviceKeysUrl("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8/service_keys")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listRoutes() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/service_instances/26fae4d0-df82-42f3-ac67-da5873e3a277/routes")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_instances/GET_{id}_routes_response.json")
                .build())
            .build());

        this.serviceInstances
            .listRoutes(ListServiceInstanceRoutesRequest.builder()
                .serviceInstanceId("26fae4d0-df82-42f3-ac67-da5873e3a277")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListServiceInstanceRoutesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(RouteResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2016-06-08T16:41:30Z")
                        .id("674b6eac-4a22-4a9d-bee2-b61299a57bf4")
                        .updatedAt("2016-06-08T16:41:26Z")
                        .url("/v2/routes/674b6eac-4a22-4a9d-bee2-b61299a57bf4")
                        .build())
                    .entity(RouteEntity.builder()
                        .applicationsUrl("/v2/routes/674b6eac-4a22-4a9d-bee2-b61299a57bf4/apps")
                        .domainId("8580604f-60e0-4903-a73f-f2e5e6660a68")
                        .domainUrl("/v2/private_domains/8580604f-60e0-4903-a73f-f2e5e6660a68")
                        .host("host-17")
                        .path("")
                        .routeMappingsUrl("/v2/routes/674b6eac-4a22-4a9d-bee2-b61299a57bf4/route_mappings")
                        .serviceInstanceId("26fae4d0-df82-42f3-ac67-da5873e3a277")
                        .serviceInstanceUrl("/v2/service_instances/26fae4d0-df82-42f3-ac67-da5873e3a277")
                        .spaceId("276011c4-0550-4a01-82d5-7e9c95feb9ae")
                        .spaceUrl("/v2/spaces/276011c4-0550-4a01-82d5-7e9c95feb9ae")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceBindings() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/service_instances/test-service-instance-id/service_bindings?q=app_guid%3Atest-application-id&page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_instances/GET_{id}_service_bindings_response.json")
                .build())
            .build());

        this.serviceInstances
            .listServiceBindings(ListServiceInstanceServiceBindingsRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .applicationId("test-application-id")
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListServiceInstanceServiceBindingsResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ServiceBindingResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:09Z")
                        .id("05f3ec3c-8d97-4bd8-bf86-e44cc835a154")
                        .url("/v2/service_bindings/05f3ec3c-8d97-4bd8-bf86-e44cc835a154")
                        .build())
                    .entity(ServiceBindingEntity.builder()
                        .applicationId("8a50163b-a39d-4f44-aece-dc5a956da848")
                        .serviceInstanceId("a5a0567e-edbf-4da9-ae90-dce24af308a1")
                        .bindingOptions(Collections.emptyMap())
                        .credential("creds-key-85", "creds-val-85")
                        .gatewayName("")
                        .applicationUrl("/v2/apps/8a50163b-a39d-4f44-aece-dc5a956da848")
                        .serviceInstanceUrl("/v2/service_instances/a5a0567e-edbf-4da9-ae90-dce24af308a1")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceKeys() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET)
                .path("/service_instances/test-service-instance-id/service_keys?page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_instances/GET_{id}_service_keys_response.json")
                .build())
            .build());

        this.serviceInstances
            .listServiceKeys(ListServiceInstanceServiceKeysRequest.builder()
                .serviceInstanceId("test-service-instance-id")
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListServiceInstanceServiceKeysResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ServiceKeyResource.builder()
                    .metadata(Metadata.builder()
                        .id("03ddc0ba-f792-4762-b4e4-dc08b307dc4f")
                        .url("/v2/service_keys/03ddc0ba-f792-4762-b4e4-dc08b307dc4f")
                        .createdAt("2016-05-04T04:49:09Z")
                        .build())
                    .entity(ServiceKeyEntity.builder()
                        .name("a-service-key")
                        .serviceInstanceId("28120eae-4a44-42da-a3db-2a34aea8dcaa")
                        .credential("creds-key-68", "creds-val-68")
                        .serviceInstanceUrl("/v2/service_instances/28120eae-4a44-42da-a3db-2a34aea8dcaa")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void unbindRoute() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE)
                .path("/service_instances/8fe97ac9-d53a-4858-b6a4-53c20f1fe409/routes/3bbd74b5-516d-409e-a107-19eaf9b2da18")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.serviceInstances
            .unbindRoute(UnbindServiceInstanceRouteRequest.builder()
                .routeId("3bbd74b5-516d-409e-a107-19eaf9b2da18")
                .serviceInstanceId("8fe97ac9-d53a-4858-b6a4-53c20f1fe409")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT)
                .path("/service_instances/test-service-instance-id?accepts_incomplete=true")
                .payload("fixtures/client/v2/service_instances/PUT_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/service_instances/PUT_{id}_response.json")
                .build())
            .build());

        this.serviceInstances
            .update(UpdateServiceInstanceRequest.builder()
                .acceptsIncomplete(true)
                .serviceInstanceId("test-service-instance-id")
                .servicePlanId("5b5e984f-bbf6-477b-9d3a-b6d5df941b50")
                .parameter("the_service_broker", "wants this object")
                .tags(Collections.emptyList())
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateServiceInstanceResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:30Z")
                    .id("a34f1423-4b84-4727-ab49-3f1522c4cb16")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .url("/v2/service_instances/a34f1423-4b84-4727-ab49-3f1522c4cb16")
                    .build())
                .entity(ServiceInstanceEntity.builder()
                    .name("name-1529")
                    .credential("creds-key-41", "creds-val-41")
                    .servicePlanId("4ec73bf4-9f3a-44c7-bbac-61ee9cb5a511")
                    .spaceId("da37b4b7-2439-4b30-9eb3-bded0dbf690f")
                    .type("managed_service_instance")
                    .tags(Collections.emptyList())
                    .lastOperation(LastOperation.builder()
                        .createdAt("2016-06-08T16:41:30Z")
                        .updatedAt("2016-06-08T16:41:30Z")
                        .description("")
                        .state("in progress")
                        .type("update")
                        .build())
                    .maintenanceInfo(MaintenanceInfo.builder()
                        .description("OS image update.\nExpect downtime.")
                        .version("2.1.0")
                        .build())
                    .routesUrl("/v2/service_instances/a34f1423-4b84-4727-ab49-3f1522c4cb16/routes")
                    .spaceUrl("/v2/spaces/da37b4b7-2439-4b30-9eb3-bded0dbf690f")
                    .servicePlanUrl("/v2/service_plans/4ec73bf4-9f3a-44c7-bbac-61ee9cb5a511")
                    .serviceBindingsUrl("/v2/service_instances/a34f1423-4b84-4727-ab49-3f1522c4cb16/service_bindings")
                    .serviceKeysUrl("/v2/service_instances/a34f1423-4b84-4727-ab49-3f1522c4cb16/service_keys")
                    .sharedFromUrl("/v2/service_instances/0d632575-bb06-4ea5-bb19-a451a9644d92/shared_from")
                    .sharedToUrl("/v2/service_instances/0d632575-bb06-4ea5-bb19-a451a9644d92/shared_to")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}

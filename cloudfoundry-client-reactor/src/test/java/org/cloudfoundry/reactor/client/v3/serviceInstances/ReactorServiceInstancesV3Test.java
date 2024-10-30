/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.serviceinstances;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.cloudfoundry.client.v3.LastOperation;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.MaintenanceInfo;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v3.serviceinstances.DeleteServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceinstances.GetManagedServiceParametersRequest;
import org.cloudfoundry.client.v3.serviceinstances.GetManagedServiceParametersResponse;
import org.cloudfoundry.client.v3.serviceinstances.GetServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceinstances.GetServiceInstanceResponse;
import org.cloudfoundry.client.v3.serviceinstances.GetUserProvidedCredentialsRequest;
import org.cloudfoundry.client.v3.serviceinstances.GetUserProvidedCredentialsResponse;
import org.cloudfoundry.client.v3.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v3.serviceinstances.ListServiceInstancesResponse;
import org.cloudfoundry.client.v3.serviceinstances.ListSharedSpacesRelationshipRequest;
import org.cloudfoundry.client.v3.serviceinstances.ListSharedSpacesRelationshipResponse;
import org.cloudfoundry.client.v3.serviceinstances.ServiceInstanceRelationships;
import org.cloudfoundry.client.v3.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v3.serviceinstances.ServiceInstanceType;
import org.cloudfoundry.client.v3.serviceinstances.ShareServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceinstances.ShareServiceInstanceResponse;
import org.cloudfoundry.client.v3.serviceinstances.UnshareServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceinstances.UpdateServiceInstanceRequest;
import org.cloudfoundry.client.v3.serviceinstances.UpdateServiceInstanceResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.cloudfoundry.reactor.client.v3.serviceinstances.ReactorServiceInstancesV3;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

final class ReactorServiceInstancesV3Test extends AbstractClientApiTest {

    private final ReactorServiceInstancesV3 serviceInstances =
            new ReactorServiceInstancesV3(
                    CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    void getManagedServiceParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("key_1", "value_1");
        parameters.put("key_2", "value_2");

        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(GET)
                                        .path(
                                                "/service_instances/test-service-instance-id/parameters")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/serviceinstances/GET_{id}_parameters.json")
                                        .build())
                        .build());

        this.serviceInstances
                .getManagedServiceParameters(
                        GetManagedServiceParametersRequest.builder()
                                .serviceInstanceId("test-service-instance-id")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        GetManagedServiceParametersResponse.builder()
                                .parameters(parameters)
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void getUserProvidedCredentials() {
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("username", "my-username");
        credentials.put("password", "super-secret");
        credentials.put("other", "credential");

        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(GET)
                                        .path(
                                                "/service_instances/test-service-instance-id/credentials")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/serviceinstances/GET_{id}_credentials.json")
                                        .build())
                        .build());

        this.serviceInstances
                .getUserProvidedCredentials(
                        GetUserProvidedCredentialsRequest.builder()
                                .serviceInstanceId("test-service-instance-id")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        GetUserProvidedCredentialsResponse.builder()
                                .credentials(credentials)
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void get() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(GET)
                                        .path(
                                                "/service_instances/c89b3280-fe8d-4aa0-a42e-44465bb1c61c")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(CREATED)
                                        .payload(
                                                "fixtures/client/v3/serviceinstances/GET_{id}_response.json")
                                        .build())
                        .build());

        this.serviceInstances
                .get(
                        GetServiceInstanceRequest.builder()
                                .serviceInstanceId("c89b3280-fe8d-4aa0-a42e-44465bb1c61c")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        GetServiceInstanceResponse.builder()
                                .id("c89b3280-fe8d-4aa0-a42e-44465bb1c61c")
                                .createdAt("2020-03-10T15:49:29Z")
                                .updatedAt("2020-03-10T15:49:29Z")
                                .name("my-managed-instance")
                                .tags("foo", "bar")
                                .type(ServiceInstanceType.MANAGED)
                                .maintenanceInfo(MaintenanceInfo.builder().version("1.0.0").build())
                                .updateAvailable(false)
                                .dashboardUrl("https://service-broker.example.org/dashboard")
                                .lastOperation(
                                        LastOperation.builder()
                                                .type("create")
                                                .state("succeeded")
                                                .description("Operation succeeded")
                                                .updatedAt("2020-03-10T15:49:32Z")
                                                .createdAt("2020-03-10T15:49:29Z")
                                                .build())
                                .relationships(
                                        ServiceInstanceRelationships.builder()
                                                .servicePlan(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(
                                                                                        "5358d122-638e-11ea-afca-bf6e756684ac")
                                                                                .build())
                                                                .build())
                                                .space(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(
                                                                                        "5a84d315-9513-4d74-95e5-f6a5501eeef7")
                                                                                .build())
                                                                .build())
                                                .build())
                                .link(
                                        "self",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/service_instances/c89b3280-fe8d-4aa0-a42e-44465bb1c61c")
                                                .build())
                                .link(
                                        "service_plan",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/service_plans/5358d122-638e-11ea-afca-bf6e756684ac")
                                                .build())
                                .link(
                                        "space",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/spaces/5a84d315-9513-4d74-95e5-f6a5501eeef7")
                                                .build())
                                .link(
                                        "parameters",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/service_instances/c89b3280-fe8d-4aa0-a42e-44465bb1c61c/parameters")
                                                .build())
                                .link(
                                        "shared_spaces",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/service_instances/c89b3280-fe8d-4aa0-a42e-44465bb1c61c/relationships/shared_spaces")
                                                .build())
                                .link(
                                        "service_credential_bindings",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/service_credential_bindings?service_instance_guids=c89b3280-fe8d-4aa0-a42e-44465bb1c61c")
                                                .build())
                                .link(
                                        "service_route_bindings",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/service_route_bindings?service_instance_guids=c89b3280-fe8d-4aa0-a42e-44465bb1c61c")
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void list() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(GET)
                                        .path(
                                                "/service_instances?names=test-service-instance-name&space_guids=test-space-id&page=1")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/serviceinstances/GET_response.json")
                                        .build())
                        .build());

        this.serviceInstances
                .list(
                        ListServiceInstancesRequest.builder()
                                .page(1)
                                .serviceInstanceName("test-service-instance-name")
                                .spaceId("test-space-id")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        ListServiceInstancesResponse.builder()
                                .pagination(
                                        Pagination.builder()
                                                .totalResults(3)
                                                .first(
                                                        Link.builder()
                                                                .href(
                                                                        "/v3/service_instances?page=1&per_page=2")
                                                                .build())
                                                .last(
                                                        Link.builder()
                                                                .href(
                                                                        "/v3/service_instances?page=2&per_page=2")
                                                                .build())
                                                .next(
                                                        Link.builder()
                                                                .href(
                                                                        "/v3/service_instances?page=2&per_page=2")
                                                                .build())
                                                .build())
                                .resource(
                                        ServiceInstanceResource.builder()
                                                .id("85ccdcad-d725-4109-bca4-fd6ba062b5c8")
                                                .createdAt("2017-11-17T13:54:21Z")
                                                .name("my_service_instance1")
                                                .relationships(
                                                        ServiceInstanceRelationships.builder()
                                                                .space(
                                                                        ToOneRelationship.builder()
                                                                                .data(
                                                                                        Relationship
                                                                                                .builder()
                                                                                                .id(
                                                                                                        "ae0031f9-dd49-461c-a945-df40e77c39cb")
                                                                                                .build())
                                                                                .build())
                                                                .build())
                                                .link(
                                                        "space",
                                                        Link.builder()
                                                                .href(
                                                                        "/v3/spaces/ae0031f9-dd49-461c-a945-df40e77c39cb")
                                                                .build())
                                                .build())
                                .resource(
                                        ServiceInstanceResource.builder()
                                                .id("85ccdcad-d725-4109-bca4-fd6ba062b5c7")
                                                .createdAt("2017-11-17T13:54:21Z")
                                                .name("my_service_instance2")
                                                .relationships(
                                                        ServiceInstanceRelationships.builder()
                                                                .space(
                                                                        ToOneRelationship.builder()
                                                                                .data(
                                                                                        Relationship
                                                                                                .builder()
                                                                                                .id(
                                                                                                        "ae0031f9-dd49-461c-a945-df40e77c39ce")
                                                                                                .build())
                                                                                .build())
                                                                .build())
                                                .link(
                                                        "space",
                                                        Link.builder()
                                                                .href(
                                                                        "/v3/spaces/ae0031f9-dd49-461c-a945-df40e77c39ce")
                                                                .build())
                                                .build())
                                .resource(
                                        ServiceInstanceResource.builder()
                                                .id("85ccdcad-d725-4109-bca4-fd6ba062b5c6")
                                                .createdAt("2017-11-17T13:54:21Z")
                                                .name("my_service_instance3")
                                                .relationships(
                                                        ServiceInstanceRelationships.builder()
                                                                .space(
                                                                        ToOneRelationship.builder()
                                                                                .data(
                                                                                        Relationship
                                                                                                .builder()
                                                                                                .id(
                                                                                                        "ae0031f9-dd49-461c-a945-df40e77c39cf")
                                                                                                .build())
                                                                                .build())
                                                                .build())
                                                .link(
                                                        "space",
                                                        Link.builder()
                                                                .href(
                                                                        "/v3/spaces/ae0031f9-dd49-461c-a945-df40e77c39cf")
                                                                .build())
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void listSharedSpaces() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(GET)
                                        .path(
                                                "/service_instances/test-service-instance-id/relationships/shared_spaces")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/serviceinstances/GET_{id}_relationships_spaces_shared_response.json")
                                        .build())
                        .build());

        this.serviceInstances
                .listSharedSpacesRelationship(
                        ListSharedSpacesRelationshipRequest.builder()
                                .serviceInstanceId("test-service-instance-id")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        ListSharedSpacesRelationshipResponse.builder()
                                .data(
                                        Relationship.builder()
                                                .id("68d54d31-9b3a-463b-ba94-e8e4c32edbac")
                                                .build())
                                .data(
                                        Relationship.builder()
                                                .id("b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                                                .build())
                                .link(
                                        "self",
                                        Link.builder()
                                                .href(
                                                        "/v3/service_instances/bdeg4371-cbd3-4155-b156-dc0c2a431b4c/relationships/shared_spaces")
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void share() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(POST)
                                        .path(
                                                "/service_instances/test-service-instance-id/relationships/shared_spaces")
                                        .payload(
                                                "fixtures/client/v3/serviceinstances/POST_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(CREATED)
                                        .payload(
                                                "fixtures/client/v3/serviceinstances/POST_response.json")
                                        .build())
                        .build());

        this.serviceInstances
                .share(
                        ShareServiceInstanceRequest.builder()
                                .serviceInstanceId("test-service-instance-id")
                                .data(Relationship.builder().id("space-guid-1").build())
                                .data(Relationship.builder().id("space-guid-2").build())
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        ShareServiceInstanceResponse.builder()
                                .data(
                                        Relationship.builder()
                                                .id("68d54d31-9b3a-463b-ba94-e8e4c32edbac")
                                                .build())
                                .data(
                                        Relationship.builder()
                                                .id("b19f6525-cbd3-4155-b156-dc0c2a431b4c")
                                                .build())
                                .link(
                                        "self",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/service_instances/bdeg4371-cbd3-4155-b156-dc0c2a431b4c/relationships/shared_spaces")
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void unshare() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(DELETE)
                                        .path(
                                                "/service_instances/test-service-instance-id/relationships/shared_spaces/test-space-id")
                                        .build())
                        .response(TestResponse.builder().status(NO_CONTENT).build())
                        .build());

        this.serviceInstances
                .unshare(
                        UnshareServiceInstanceRequest.builder()
                                .serviceInstanceId("test-service-instance-id")
                                .spaceId("test-space-id")
                                .build())
                .as(StepVerifier::create)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void createManagedServiceInstance() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(POST)
                                        .path("/service_instances")
                                        .payload(
                                                "fixtures/client/v3/serviceinstances/POST_request_create_managed_service_instance.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(ACCEPTED)
                                        .header("Location", "e1e4417c-74ee-11ea-a604-48bf6bec2d79")
                                        .build())
                        .build());

        this.serviceInstances
                .create(
                        CreateServiceInstanceRequest.builder()
                                .type(ServiceInstanceType.MANAGED)
                                .name("my_service_instance")
                                .relationships(
                                        ServiceInstanceRelationships.builder()
                                                .servicePlan(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(
                                                                                        "e0e4417c-74ee-11ea-a604-48bf6bec2d78")
                                                                                .build())
                                                                .build())
                                                .space(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(
                                                                                        "7304bc3c-7010-11ea-8840-48bf6bec2d78")
                                                                                .build())
                                                                .build())
                                                .build())
                                .metadata(
                                        Metadata.builder()
                                                .annotation("foo", "bar")
                                                .label("baz", "qux")
                                                .build())
                                .tags("foo", "bar", "baz")
                                .parameter("foo", "bar")
                                .parameter("baz", "qux")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        CreateServiceInstanceResponse.builder()
                                .jobId("e1e4417c-74ee-11ea-a604-48bf6bec2d79")
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void createUserProvidedService() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(POST)
                                        .path("/service_instances")
                                        .payload(
                                                "fixtures/client/v3/serviceinstances/POST_request_create_user_provided_service_instance.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(CREATED)
                                        .payload(
                                                "fixtures/client/v3/serviceinstances/POST_response_create_user_provided_service_instance.json")
                                        .build())
                        .build());

        this.serviceInstances
                .create(
                        CreateServiceInstanceRequest.builder()
                                .type(ServiceInstanceType.USER_PROVIDED)
                                .name("my-user-provided-instance")
                                .relationships(
                                        ServiceInstanceRelationships.builder()
                                                .space(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(
                                                                                        "7304bc3c-7010-11ea-8840-48bf6bec2d78")
                                                                                .build())
                                                                .build())
                                                .build())
                                .metadata(
                                        Metadata.builder()
                                                .annotation("foo", "bar")
                                                .label("baz", "qux")
                                                .build())
                                .tag("sql")
                                .credential("foo", "bar")
                                .credential("baz", "qux")
                                .syslogDrainUrl("https://syslog.com/drain")
                                .routeServiceUrl("https://route.com/service")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        CreateServiceInstanceResponse.builder()
                                .serviceInstance(
                                        ServiceInstanceResource.builder()
                                                .id("88ce23e5-27c3-4381-a2df-32a28ec43133")
                                                .createdAt("2020-03-10T15:56:08Z")
                                                .updatedAt("2020-03-10T15:56:08Z")
                                                .lastOperation(
                                                        LastOperation.builder()
                                                                .type("create")
                                                                .state("succeeded")
                                                                .description("Operation succeeded")
                                                                .updatedAt("2020-03-10T15:49:32Z")
                                                                .createdAt("2020-03-10T15:49:29Z")
                                                                .build())
                                                .name("my-user-provided-instance")
                                                .tag("sql")
                                                .type(ServiceInstanceType.USER_PROVIDED)
                                                .syslogDrainUrl("https://syslog.com/drain")
                                                .routeServiceUrl("https://route.com/service")
                                                .relationships(
                                                        ServiceInstanceRelationships.builder()
                                                                .space(
                                                                        ToOneRelationship.builder()
                                                                                .data(
                                                                                        Relationship
                                                                                                .builder()
                                                                                                .id(
                                                                                                        "7304bc3c-7010-11ea-8840-48bf6bec2d78")
                                                                                                .build())
                                                                                .build())
                                                                .build())
                                                .metadata(
                                                        Metadata.builder()
                                                                .annotation("foo", "bar")
                                                                .label("baz", "qux")
                                                                .build())
                                                .link(
                                                        "self",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_instances/88ce23e5-27c3-4381-a2df-32a28ec43133")
                                                                .build())
                                                .link(
                                                        "space",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/spaces/5a84d315-9513-4d74-95e5-f6a5501eeef7")
                                                                .build())
                                                .link(
                                                        "credentials",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_instances/88ce23e5-27c3-4381-a2df-32a28ec43133/credentials")
                                                                .build())
                                                .link(
                                                        "service_credential_bindings",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_credential_bindings?service_instance_guids=88ce23e5-27c3-4381-a2df-32a28ec43133")
                                                                .build())
                                                .link(
                                                        "service_route_bindings",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_route_bindings?service_instance_guids=88ce23e5-27c3-4381-a2df-32a28ec43133")
                                                                .build())
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void updateManagedServiceInstance() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(PATCH)
                                        .path(
                                                "/service_instances/c89b3280-fe8d-4aa0-a42e-44465bb1c61c")
                                        .payload(
                                                "fixtures/client/v3/serviceinstances/PATCH_request_update_managed_service_instance.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(ACCEPTED)
                                        .payload(
                                                "fixtures/client/v3/serviceinstances/PATCH_response_update_managed_service_instance.json")
                                        .build())
                        .build());

        this.serviceInstances
                .update(
                        UpdateServiceInstanceRequest.builder()
                                .serviceInstanceId("c89b3280-fe8d-4aa0-a42e-44465bb1c61c")
                                .name("my_service_instance")
                                .parameter("foo", "bar")
                                .parameter("baz", "qux")
                                .maintenanceInfo(MaintenanceInfo.builder().version("1.0.0").build())
                                .tags("foo", "bar", "baz")
                                .relationships(
                                        ServiceInstanceRelationships.builder()
                                                .servicePlan(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(
                                                                                        "f2b6ba9c-a4d2-11ea-8ae6-48bf6bec2d78")
                                                                                .build())
                                                                .build())
                                                .build())
                                .metadata(
                                        Metadata.builder()
                                                .annotation("note", "detailed information")
                                                .label("key", "value")
                                                .build())
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        UpdateServiceInstanceResponse.builder()
                                .serviceInstance(
                                        ServiceInstanceResource.builder()
                                                .id("c89b3280-fe8d-4aa0-a42e-44465bb1c61c")
                                                .createdAt("2020-03-10T15:49:29Z")
                                                .updatedAt("2020-03-10T15:49:29Z")
                                                .name("my_service_instance")
                                                .maintenanceInfo(
                                                        MaintenanceInfo.builder()
                                                                .version("1.0.0")
                                                                .build())
                                                .updateAvailable(false)
                                                .type(ServiceInstanceType.MANAGED)
                                                .dashboardUrl(
                                                        "https://service-broker.example.org/dashboard")
                                                .tags("foo", "bar", "baz")
                                                .lastOperation(
                                                        LastOperation.builder()
                                                                .type("update")
                                                                .description("Operation succeeded")
                                                                .state("succeeded")
                                                                .updatedAt("2020-03-10T15:49:32Z")
                                                                .createdAt("2020-03-10T15:49:29Z")
                                                                .build())
                                                .relationships(
                                                        ServiceInstanceRelationships.builder()
                                                                .servicePlan(
                                                                        ToOneRelationship.builder()
                                                                                .data(
                                                                                        Relationship
                                                                                                .builder()
                                                                                                .id(
                                                                                                        "5358d122-638e-11ea-afca-bf6e756684ac")
                                                                                                .build())
                                                                                .build())
                                                                .space(
                                                                        ToOneRelationship.builder()
                                                                                .data(
                                                                                        Relationship
                                                                                                .builder()
                                                                                                .id(
                                                                                                        "5a84d315-9513-4d74-95e5-f6a5501eeef7")
                                                                                                .build())
                                                                                .build())
                                                                .build())
                                                .metadata(
                                                        Metadata.builder()
                                                                .label("key", "value")
                                                                .annotation(
                                                                        "note",
                                                                        "detailed information")
                                                                .build())
                                                .link(
                                                        "self",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_instances/c89b3280-fe8d-4aa0-a42e-44465bb1c61c")
                                                                .build())
                                                .link(
                                                        "service_plan",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_plans/5358d122-638e-11ea-afca-bf6e756684ac")
                                                                .build())
                                                .link(
                                                        "space",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/spaces/5a84d315-9513-4d74-95e5-f6a5501eeef7")
                                                                .build())
                                                .link(
                                                        "parameters",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_instances/c89b3280-fe8d-4aa0-a42e-44465bb1c61c/parameters")
                                                                .build())
                                                .link(
                                                        "shared_spaces",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_instances/c89b3280-fe8d-4aa0-a42e-44465bb1c61c/relationships/shared_spaces")
                                                                .build())
                                                .link(
                                                        "service_credential_bindings",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_credential_bindings?service_instance_guids=c89b3280-fe8d-4aa0-a42e-44465bb1c61c")
                                                                .build())
                                                .link(
                                                        "service_route_bindings",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_route_bindings?service_instance_guids=c89b3280-fe8d-4aa0-a42e-44465bb1c61c")
                                                                .build())
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void updateManagedServiceInstanceAsync() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(PATCH)
                                        .path(
                                                "/service_instances/c89b3280-fe8d-4aa0-a42e-44465bb1c61c")
                                        .payload(
                                                "fixtures/client/v3/serviceinstances/PATCH_request_update_managed_service_instance.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(ACCEPTED)
                                        .header("Location", "e1e4417c-74ee-11ea-a604-48bf6bec2d79")
                                        .build())
                        .build());

        this.serviceInstances
                .update(
                        UpdateServiceInstanceRequest.builder()
                                .serviceInstanceId("c89b3280-fe8d-4aa0-a42e-44465bb1c61c")
                                .name("my_service_instance")
                                .parameter("foo", "bar")
                                .parameter("baz", "qux")
                                .maintenanceInfo(MaintenanceInfo.builder().version("1.0.0").build())
                                .tags("foo", "bar", "baz")
                                .relationships(
                                        ServiceInstanceRelationships.builder()
                                                .servicePlan(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(
                                                                                        "f2b6ba9c-a4d2-11ea-8ae6-48bf6bec2d78")
                                                                                .build())
                                                                .build())
                                                .build())
                                .metadata(
                                        Metadata.builder()
                                                .annotation("note", "detailed information")
                                                .label("key", "value")
                                                .build())
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        UpdateServiceInstanceResponse.builder()
                                .jobId("e1e4417c-74ee-11ea-a604-48bf6bec2d79")
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void updateUserProvidedServiceInstance() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(PATCH)
                                        .path(
                                                "/service_instances/88ce23e5-27c3-4381-a2df-32a28ec43133")
                                        .payload(
                                                "fixtures/client/v3/serviceinstances/PATCH_request_update_user_provided_service_instance.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/serviceinstances/PATCH_response_update_user_provided_service_instance.json")
                                        .build())
                        .build());

        this.serviceInstances
                .update(
                        UpdateServiceInstanceRequest.builder()
                                .serviceInstanceId("88ce23e5-27c3-4381-a2df-32a28ec43133")
                                .name("my-user-provided-instance")
                                .credential("foo", "bar")
                                .credential("baz", "qux")
                                .tags("foo", "bar", "baz")
                                .syslogDrainUrl("https://syslog.com/drain")
                                .routeServiceUrl("https://route.com/service")
                                .metadata(
                                        Metadata.builder()
                                                .annotation("foo", "bar")
                                                .label("baz", "qux")
                                                .build())
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        UpdateServiceInstanceResponse.builder()
                                .serviceInstance(
                                        ServiceInstanceResource.builder()
                                                .id("88ce23e5-27c3-4381-a2df-32a28ec43133")
                                                .createdAt("2020-03-10T15:56:08Z")
                                                .updatedAt("2020-03-10T15:56:08Z")
                                                .lastOperation(
                                                        LastOperation.builder()
                                                                .type("create")
                                                                .state("succeeded")
                                                                .description("Operation succeeded")
                                                                .updatedAt("2020-03-10T15:49:32Z")
                                                                .createdAt("2020-03-10T15:49:29Z")
                                                                .build())
                                                .name("my-user-provided-instance")
                                                .tags("foo", "bar", "baz")
                                                .type(ServiceInstanceType.USER_PROVIDED)
                                                .syslogDrainUrl("https://syslog.com/drain")
                                                .routeServiceUrl("https://route.com/service")
                                                .relationships(
                                                        ServiceInstanceRelationships.builder()
                                                                .space(
                                                                        ToOneRelationship.builder()
                                                                                .data(
                                                                                        Relationship
                                                                                                .builder()
                                                                                                .id(
                                                                                                        "5a84d315-9513-4d74-95e5-f6a5501eeef7")
                                                                                                .build())
                                                                                .build())
                                                                .build())
                                                .metadata(
                                                        Metadata.builder()
                                                                .annotation("foo", "bar")
                                                                .label("baz", "qux")
                                                                .build())
                                                .link(
                                                        "self",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_instances/88ce23e5-27c3-4381-a2df-32a28ec43133")
                                                                .build())
                                                .link(
                                                        "space",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/spaces/5a84d315-9513-4d74-95e5-f6a5501eeef7")
                                                                .build())
                                                .link(
                                                        "credentials",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_instances/88ce23e5-27c3-4381-a2df-32a28ec43133/credentials")
                                                                .build())
                                                .link(
                                                        "service_credential_bindings",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_credential_bindings?service_instance_guids=88ce23e5-27c3-4381-a2df-32a28ec43133")
                                                                .build())
                                                .link(
                                                        "service_route_bindings",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_route_bindings?service_instance_guids=88ce23e5-27c3-4381-a2df-32a28ec43133")
                                                                .build())
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void deleteManagedServiceInstance() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(DELETE)
                                        .path(
                                                "/service_instances/88ce23e5-27c3-4381-a2df-32a28ec43133")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(ACCEPTED)
                                        .header("Location", "e1e4417c-74ee-11ea-a604-48bf6bec2d79")
                                        .build())
                        .build());

        this.serviceInstances
                .delete(
                        DeleteServiceInstanceRequest.builder()
                                .serviceInstanceId("88ce23e5-27c3-4381-a2df-32a28ec43133")
                                .build())
                .as(StepVerifier::create)
                .expectNext(Optional.of("e1e4417c-74ee-11ea-a604-48bf6bec2d79"))
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void deleteUserProvidedServiceInstance() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(DELETE)
                                        .path(
                                                "/service_instances/88ce23e5-27c3-4381-a2df-32a28ec43133")
                                        .build())
                        .response(TestResponse.builder().status(NO_CONTENT).build())
                        .build());

        this.serviceInstances
                .delete(
                        DeleteServiceInstanceRequest.builder()
                                .serviceInstanceId("88ce23e5-27c3-4381-a2df-32a28ec43133")
                                .build())
                .as(StepVerifier::create)
                .expectNext(Optional.empty())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }
}

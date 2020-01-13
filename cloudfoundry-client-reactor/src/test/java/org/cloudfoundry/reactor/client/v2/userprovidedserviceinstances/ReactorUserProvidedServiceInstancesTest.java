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

package org.cloudfoundry.reactor.client.v2.userprovidedserviceinstances;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingEntity;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.AssociateUserProvidedServiceInstanceRouteRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.AssociateUserProvidedServiceInstanceRouteResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.CreateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.DeleteUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.GetUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.GetUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstanceRoutesRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstanceRoutesResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstanceServiceBindingsRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstanceServiceBindingsResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstancesRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.ListUserProvidedServiceInstancesResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.RemoveUserProvidedServiceInstanceRouteRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UpdateUserProvidedServiceInstanceResponse;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstanceEntity;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstanceResource;
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

public final class ReactorUserProvidedServiceInstancesTest extends AbstractClientApiTest {

    private final ReactorUserProvidedServiceInstances userProvidedServiceInstances = new ReactorUserProvidedServiceInstances(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void associateRoute() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/user_provided_service_instances/5badd282-6e07-4fc6-a8c4-78be99040774/routes/237d9236-7997-4b1a-be8d-2aaf2d85421a")
                .payload("fixtures/client/v2/user_provided_service_instances/PUT_{id}_route_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/user_provided_service_instances/PUT_{id}_route_response.json")
                .build())
            .build());

        this.userProvidedServiceInstances
            .associateRoute(AssociateUserProvidedServiceInstanceRouteRequest.builder()
                .routeId("237d9236-7997-4b1a-be8d-2aaf2d85421a")
                .userProvidedServiceInstanceId("5badd282-6e07-4fc6-a8c4-78be99040774")
                .build())
            .as(StepVerifier::create)
            .expectNext(AssociateUserProvidedServiceInstanceRouteResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:33Z")
                    .id("5badd282-6e07-4fc6-a8c4-78be99040774")
                    .url("/v2/user_provided_service_instances/5badd282-6e07-4fc6-a8c4-78be99040774")
                    .build())
                .entity(UserProvidedServiceInstanceEntity.builder()
                    .credential("creds-key-52", "creds-val-52")
                    .name("name-1676")
                    .routeServiceUrl("https://foo.com/url-92")
                    .routesUrl("/v2/user_provided_service_instances/5badd282-6e07-4fc6-a8c4-78be99040774/routes")
                    .serviceBindingsUrl("/v2/user_provided_service_instances/5badd282-6e07-4fc6-a8c4-78be99040774/service_bindings")
                    .spaceId("91b53184-6430-4891-8d4b-fabbe96a84f6")
                    .spaceUrl("/v2/spaces/91b53184-6430-4891-8d4b-fabbe96a84f6")
                    .syslogDrainUrl("https://foo.com/url-93")
                    .type("user_provided_service_instance")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/user_provided_service_instances")
                .payload("fixtures/client/v2/user_provided_service_instances/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/user_provided_service_instances/POST_response.json")
                .build())
            .build());

        this.userProvidedServiceInstances
            .create(CreateUserProvidedServiceInstanceRequest.builder()
                .spaceId("0d45d43f-7d50-43c6-9981-b32ce8d5a373")
                .name("my-user-provided-instance")
                .credential("somekey", "somevalue")
                .routeServiceUrl("https://logger.example.com")
                .syslogDrainUrl("syslog://example.com")
                .tags("tag1", "tag2")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateUserProvidedServiceInstanceResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2015-07-27T22:43:35Z")
                    .id("34d5500e-712d-49ef-8bbe-c9ac349532da")
                    .url("/v2/user_provided_service_instances/34d5500e-712d-49ef-8bbe-c9ac349532da")
                    .build())
                .entity(UserProvidedServiceInstanceEntity.builder()
                    .name("my-user-provided-instance")
                    .credential("somekey", "somevalue")
                    .spaceId("0d45d43f-7d50-43c6-9981-b32ce8d5a373")
                    .type("user_provided_service_instance")
                    .syslogDrainUrl("syslog://example.com")
                    .routeServiceUrl("https://logger.example.com")
                    .tags("tag1", "tag2")
                    .spaceUrl("/v2/spaces/0d45d43f-7d50-43c6-9981-b32ce8d5a373")
                    .serviceBindingsUrl("/v2/user_provided_service_instances/34d5500e-712d-49ef-8bbe-c9ac349532da/service_bindings")
                    .routesUrl("/v2/user_provided_service_instances/34d5500e-712d-49ef-8bbe-c9ac349532da/routes")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/user_provided_service_instances/5b6b45c8-89be-48d2-affd-f64346ad4d93")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.userProvidedServiceInstances
            .delete(DeleteUserProvidedServiceInstanceRequest.builder()
                .userProvidedServiceInstanceId("5b6b45c8-89be-48d2-affd-f64346ad4d93")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/user_provided_service_instances/8c12fd06-6639-4844-b5e7-a6831cadbbcc")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/user_provided_service_instances/GET_{id}_response.json")
                .build())
            .build());

        this.userProvidedServiceInstances
            .get(GetUserProvidedServiceInstanceRequest.builder()
                .userProvidedServiceInstanceId("8c12fd06-6639-4844-b5e7-a6831cadbbcc")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetUserProvidedServiceInstanceResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-06-08T16:41:33Z")
                    .id("e9358711-0ad9-4f2a-b3dc-289d47c17c87")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .url("/v2/user_provided_service_instances/e9358711-0ad9-4f2a-b3dc-289d47c17c87")
                    .build())
                .entity(UserProvidedServiceInstanceEntity.builder()
                    .credential("creds-key-58", "creds-val-58")
                    .name("name-1700")
                    .routesUrl("/v2/user_provided_service_instances/e9358711-0ad9-4f2a-b3dc-289d47c17c87/routes")
                    .serviceBindingsUrl("/v2/user_provided_service_instances/e9358711-0ad9-4f2a-b3dc-289d47c17c87/service_bindings")
                    .spaceId("22236d1a-d9c7-44b7-bdad-2bb079a6c4a1")
                    .spaceUrl("/v2/spaces/22236d1a-d9c7-44b7-bdad-2bb079a6c4a1")
                    .syslogDrainUrl("https://foo.com/url-104")
                    .tags("accounting", "mongodb")
                    .type("user_provided_service_instance")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/user_provided_service_instances?page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/user_provided_service_instances/GET_response.json")
                .build())
            .build());

        this.userProvidedServiceInstances
            .list(ListUserProvidedServiceInstancesRequest.builder()
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListUserProvidedServiceInstancesResponse.builder()
                .totalPages(1)
                .totalResults(1)
                .resource(UserProvidedServiceInstanceResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:34Z")
                        .id("8db6d37b-1ca8-4d0a-b1d3-2a6aaceae866")
                        .url("/v2/user_provided_service_instances/8db6d37b-1ca8-4d0a-b1d3-2a6aaceae866")
                        .build())
                    .entity(UserProvidedServiceInstanceEntity.builder()
                        .name("name-2365")
                        .credential("creds-key-665", "creds-val-665")
                        .spaceId("2fff6e71-d329-4991-9c89-7fa8abca70df")
                        .type("user_provided_service_instance")
                        .syslogDrainUrl("https://foo.com/url-90")
                        .spaceUrl("/v2/spaces/2fff6e71-d329-4991-9c89-7fa8abca70df")
                        .serviceBindingsUrl("/v2/user_provided_service_instances/8db6d37b-1ca8-4d0a-b1d3-2a6aaceae866/service_bindings")
                        .routesUrl("/v2/user_provided_service_instances/8db6d37b-1ca8-4d0a-b1d3-2a6aaceae866/routes")
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
                .method(GET).path("/user_provided_service_instances/500e64c6-7f70-4e3b-ab7b-940a6303d79b/routes")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/user_provided_service_instances/GET_{id}_routes_response.json")
                .build())
            .build());

        this.userProvidedServiceInstances
            .listRoutes(ListUserProvidedServiceInstanceRoutesRequest.builder()
                .userProvidedServiceInstanceId("500e64c6-7f70-4e3b-ab7b-940a6303d79b")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListUserProvidedServiceInstanceRoutesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(RouteResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2016-06-08T16:41:33Z")
                        .id("d6e18af9-9d84-4a53-a301-ab9bef03a7b0")
                        .updatedAt("2016-06-08T16:41:33Z")
                        .url("/v2/routes/d6e18af9-9d84-4a53-a301-ab9bef03a7b0")
                        .build())
                    .entity(RouteEntity.builder()
                        .applicationsUrl("/v2/routes/d6e18af9-9d84-4a53-a301-ab9bef03a7b0/apps")
                        .domainId("428b9275-47a6-481b-97e3-d93ae18611ee")
                        .domainUrl("/v2/private_domains/428b9275-47a6-481b-97e3-d93ae18611ee")
                        .host("host-24")
                        .path("")
                        .routeMappingsUrl("/v2/routes/d6e18af9-9d84-4a53-a301-ab9bef03a7b0/route_mappings")
                        .serviceInstanceId("500e64c6-7f70-4e3b-ab7b-940a6303d79b")
                        .serviceInstanceUrl("/v2/user_provided_service_instances/500e64c6-7f70-4e3b-ab7b-940a6303d79b")
                        .spaceId("dc7dd379-1ffb-4168-b2b4-773fe141dd2e")
                        .spaceUrl("/v2/spaces/dc7dd379-1ffb-4168-b2b4-773fe141dd2e")
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
                .method(GET).path("/user_provided_service_instances/test-user-provided-service-instance-id/service_bindings?page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/user_provided_service_instances/GET_{id}_service_bindings_response.json")
                .build())
            .build());

        this.userProvidedServiceInstances
            .listServiceBindings(ListUserProvidedServiceInstanceServiceBindingsRequest.builder()
                .userProvidedServiceInstanceId("test-user-provided-service-instance-id")
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListUserProvidedServiceInstanceServiceBindingsResponse.builder()
                .totalPages(1)
                .totalResults(1)
                .resource(ServiceBindingResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2016-01-26T22:20:16Z")
                        .id("e6b8d548-e009-47d4-ab79-675e3da6bb52")
                        .url("/v2/service_bindings/e6b8d548-e009-47d4-ab79-675e3da6bb52")
                        .build()
                    )
                    .entity(ServiceBindingEntity.builder()
                        .applicationId("a9bbd896-7500-45be-a75a-25e3d254f67c")
                        .serviceInstanceId("16c81612-6a63-4faa-8cd5-acc80771b562")
                        .credential("creds-key-29", "creds-val-29")
                        .bindingOptions(Collections.emptyMap())
                        .gatewayName("")
                        .applicationUrl("/v2/apps/a9bbd896-7500-45be-a75a-25e3d254f67c")
                        .serviceInstanceUrl("/v2/user_provided_service_instances/16c81612-6a63-4faa-8cd5-acc80771b562")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void removeRoute() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/user_provided_service_instances/fd195229-117c-4bbe-9418-c5df97131eae/routes/c3bc74b0-9465-413d-b5e6-3b305fb439cc")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.userProvidedServiceInstances
            .removeRoute(RemoveUserProvidedServiceInstanceRouteRequest.builder()
                .routeId("c3bc74b0-9465-413d-b5e6-3b305fb439cc")
                .userProvidedServiceInstanceId("fd195229-117c-4bbe-9418-c5df97131eae")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/user_provided_service_instances/e2c198b1-fa15-414e-a9a4-31537996b39d")
                .payload("fixtures/client/v2/user_provided_service_instances/PUT_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(ACCEPTED)
                .payload("fixtures/client/v2/user_provided_service_instances/PUT_{id}_response.json")
                .build())
            .build());

        this.userProvidedServiceInstances
            .update(UpdateUserProvidedServiceInstanceRequest.builder()
                .credential("somekey", "somenewvalue")
                .tag("tag1")
                .userProvidedServiceInstanceId("e2c198b1-fa15-414e-a9a4-31537996b39d")
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateUserProvidedServiceInstanceResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-02-19T02:04:06Z")
                    .id("e2c198b1-fa15-414e-a9a4-31537996b39d")
                    .updatedAt("2016-02-19T02:04:06Z")
                    .url("/v2/user_provided_service_instances/e2c198b1-fa15-414e-a9a4-31537996b39d")
                    .build())
                .entity(UserProvidedServiceInstanceEntity.builder()
                    .name("name-2565")
                    .credential("somekey", "somenewvalue")
                    .spaceId("438b5923-fe7a-4459-bbcd-a7c27332bad3")
                    .tag("tag1")
                    .type("user_provided_service_instance")
                    .syslogDrainUrl("https://foo.com/url-91")
                    .spaceUrl("/v2/spaces/438b5923-fe7a-4459-bbcd-a7c27332bad3")
                    .serviceBindingsUrl("/v2/user_provided_service_instances/e2c198b1-fa15-414e-a9a4-31537996b39d/service_bindings")
                    .routesUrl("/v2/user_provided_service_instances/e2c198b1-fa15-414e-a9a4-31537996b39d/routes")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateWithEmptyCredentials() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/user_provided_service_instances/e2c198b1-fa15-414e-a9a4-31537996b39d")
                .payload("fixtures/client/v2/user_provided_service_instances/PUT_{id}_empty_creds_request.json")
                .build())
            .response(TestResponse.builder()
                .status(ACCEPTED)
                .payload("fixtures/client/v2/user_provided_service_instances/PUT_{id}_empty_creds_response.json")
                .build())
            .build());

        this.userProvidedServiceInstances
            .update(UpdateUserProvidedServiceInstanceRequest.builder()
                .credentials(Collections.emptyMap())
                .userProvidedServiceInstanceId("e2c198b1-fa15-414e-a9a4-31537996b39d")
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateUserProvidedServiceInstanceResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2016-02-19T02:04:06Z")
                    .id("e2c198b1-fa15-414e-a9a4-31537996b39d")
                    .updatedAt("2016-02-19T02:04:06Z")
                    .url("/v2/user_provided_service_instances/e2c198b1-fa15-414e-a9a4-31537996b39d")
                    .build())
                .entity(UserProvidedServiceInstanceEntity.builder()
                    .credentials(Collections.emptyMap())
                    .name("name-2565")
                    .spaceId("438b5923-fe7a-4459-bbcd-a7c27332bad3")
                    .type("user_provided_service_instance")
                    .syslogDrainUrl("https://foo.com/url-91")
                    .spaceUrl("/v2/spaces/438b5923-fe7a-4459-bbcd-a7c27332bad3")
                    .serviceBindingsUrl("/v2/user_provided_service_instances/e2c198b1-fa15-414e-a9a4-31537996b39d/service_bindings")
                    .routesUrl("/v2/user_provided_service_instances/e2c198b1-fa15-414e-a9a4-31537996b39d/routes")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}

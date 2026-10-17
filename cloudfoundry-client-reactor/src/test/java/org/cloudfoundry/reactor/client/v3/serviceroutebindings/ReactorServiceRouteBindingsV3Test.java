/*
 * Copyright 2026 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.serviceroutebindings;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.cloudfoundry.client.v3.LastOperation;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.serviceroutebindings.CreateServiceRouteBindingRequest;
import org.cloudfoundry.client.v3.serviceroutebindings.CreateServiceRouteBindingResponse;
import org.cloudfoundry.client.v3.serviceroutebindings.DeleteServiceRouteBindingRequest;
import org.cloudfoundry.client.v3.serviceroutebindings.DeleteServiceRouteBindingResponse;
import org.cloudfoundry.client.v3.serviceroutebindings.GetServiceRouteBindingParametersRequest;
import org.cloudfoundry.client.v3.serviceroutebindings.GetServiceRouteBindingParametersResponse;
import org.cloudfoundry.client.v3.serviceroutebindings.GetServiceRouteBindingRequest;
import org.cloudfoundry.client.v3.serviceroutebindings.GetServiceRouteBindingResponse;
import org.cloudfoundry.client.v3.serviceroutebindings.ListServiceRouteBindingsRequest;
import org.cloudfoundry.client.v3.serviceroutebindings.ListServiceRouteBindingsResponse;
import org.cloudfoundry.client.v3.serviceroutebindings.ServiceRouteBindingRelationships;
import org.cloudfoundry.client.v3.serviceroutebindings.ServiceRouteBindingResource;
import org.cloudfoundry.client.v3.serviceroutebindings.UpdateServiceRouteBindingRequest;
import org.cloudfoundry.client.v3.serviceroutebindings.UpdateServiceRouteBindingResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

final class ReactorServiceRouteBindingsV3Test extends AbstractClientApiTest {

    private static final String ROUTE_ID = "7304bc3c-7010-11ea-8840-48bf6bec2d78";
    private static final String SERVICE_INSTANCE_ID_1 = "e0e4417c-74ee-11ea-a604-48bf6bec2d78";
    private static final String SERVICE_INSTANCE_ID_2 = "f0a63e60-74ee-11ea-a604-48bf6bec2d78";
    private static final String BINDING_ID_1 = "dde5ad2a-d8f4-44dc-a56f-0452d744f1c3";
    private static final String BINDING_ID_2 = "7aa37bad-6ccb-4ef9-ba48-9ce3a91b2b62";
    private static final String CREATED_AT = "2020-03-10T15:49:29Z";
    private static final String UPDATED_AT = "2020-03-10T15:49:32Z";

    private final ReactorServiceRouteBindingsV3 serviceRouteBindings =
            new ReactorServiceRouteBindingsV3(
                    CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    void create() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(POST)
                                        .path("/service_route_bindings")
                                        .payload(
                                                "fixtures/client/v3/serviceroutebindings/POST_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(ACCEPTED)
                                        .header(
                                                "Location",
                                                "https://api.example.org/v3/jobs/af5c57f6-8769-41fa-a499-2c84ed896788")
                                        .build())
                        .build());

        this.serviceRouteBindings
                .create(
                        CreateServiceRouteBindingRequest.builder()
                                .relationships(relationships(SERVICE_INSTANCE_ID_1))
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        CreateServiceRouteBindingResponse.builder()
                                .jobId("af5c57f6-8769-41fa-a499-2c84ed896788")
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void delete() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(DELETE)
                                        .path(
                                                "/service_route_bindings/test-service-route-binding-id")
                                        .build())
                        .response(TestResponse.builder().status(NO_CONTENT).build())
                        .build());

        this.serviceRouteBindings
                .delete(
                        DeleteServiceRouteBindingRequest.builder()
                                .serviceRouteBindingId("test-service-route-binding-id")
                                .build())
                .as(StepVerifier::create)
                .expectNext(DeleteServiceRouteBindingResponse.builder().build())
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
                                                "/service_route_bindings/test-service-route-binding-id")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/serviceroutebindings/GET_{id}_response.json")
                                        .build())
                        .build());

        this.serviceRouteBindings
                .get(
                        GetServiceRouteBindingRequest.builder()
                                .serviceRouteBindingId("test-service-route-binding-id")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        GetServiceRouteBindingResponse.builder()
                                .id(BINDING_ID_1)
                                .createdAt(CREATED_AT)
                                .updatedAt(UPDATED_AT)
                                .lastOperation(lastOperation("create"))
                                .relationships(relationships(SERVICE_INSTANCE_ID_1))
                                .link("self", link(bindingHref(BINDING_ID_1)))
                                .link(
                                        "service_instance",
                                        link(serviceInstanceHref(SERVICE_INSTANCE_ID_1)))
                                .link("route", link(routeHref()))
                                .link("parameters", link(parametersHref(BINDING_ID_1)))
                                .metadata(emptyMetadata())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void getParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("key1", "value1");
        parameters.put("key2", "value2");

        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(GET)
                                        .path(
                                                "/service_route_bindings/test-service-route-binding-id/parameters")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/serviceroutebindings/GET_{id}_parameters_response.json")
                                        .build())
                        .build());

        this.serviceRouteBindings
                .getParameters(
                        GetServiceRouteBindingParametersRequest.builder()
                                .serviceRouteBindingId("test-service-route-binding-id")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        GetServiceRouteBindingParametersResponse.builder()
                                .parameters(parameters)
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
                                                "/service_route_bindings?route_guids=7304bc3c-7010-11ea-8840-48bf6bec2d78&order_by=%2Bcreated_at&page=1")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/serviceroutebindings/GET_response.json")
                                        .build())
                        .build());

        this.serviceRouteBindings
                .list(
                        ListServiceRouteBindingsRequest.builder()
                                .page(1)
                                .orderBy("+created_at")
                                .routeId(ROUTE_ID)
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        ListServiceRouteBindingsResponse.builder()
                                .pagination(
                                        Pagination.builder()
                                                .totalResults(2)
                                                .first(
                                                        link(
                                                                "https://api.example.org/v3/service_route_bindings?page=1&per_page=50"))
                                                .last(
                                                        link(
                                                                "https://api.example.org/v3/service_route_bindings?page=1&per_page=50"))
                                                .build())
                                .resource(
                                        bindingResource(
                                                BINDING_ID_1, SERVICE_INSTANCE_ID_1, "create"))
                                .resource(
                                        bindingResource(
                                                BINDING_ID_2, SERVICE_INSTANCE_ID_2, "create"))
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void update() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(PATCH)
                                        .path(
                                                "/service_route_bindings/test-service-route-binding-id")
                                        .payload(
                                                "fixtures/client/v3/serviceroutebindings/PATCH_{id}_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/serviceroutebindings/PATCH_{id}_response.json")
                                        .build())
                        .build());

        this.serviceRouteBindings
                .update(
                        UpdateServiceRouteBindingRequest.builder()
                                .serviceRouteBindingId("test-service-route-binding-id")
                                .metadata(metadataWithLabel("env", "production"))
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        UpdateServiceRouteBindingResponse.builder()
                                .id(BINDING_ID_1)
                                .createdAt(CREATED_AT)
                                .updatedAt(UPDATED_AT)
                                .lastOperation(lastOperation("update"))
                                .relationships(relationships(SERVICE_INSTANCE_ID_1))
                                .link("self", link(bindingHref(BINDING_ID_1)))
                                .link(
                                        "service_instance",
                                        link(serviceInstanceHref(SERVICE_INSTANCE_ID_1)))
                                .link("route", link(routeHref()))
                                .link("parameters", link(parametersHref(BINDING_ID_1)))
                                .metadata(metadataWithLabel("env", "production"))
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    private static LastOperation lastOperation(String type) {
        return LastOperation.builder()
                .type(type)
                .state("succeeded")
                .description("Operation succeeded")
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

    private static ToOneRelationship toOneRelationship(String id) {
        return ToOneRelationship.builder().data(Relationship.builder().id(id).build()).build();
    }

    private static ServiceRouteBindingRelationships relationships(String serviceInstanceId) {
        return ServiceRouteBindingRelationships.builder()
                .route(toOneRelationship(ROUTE_ID))
                .serviceInstance(toOneRelationship(serviceInstanceId))
                .build();
    }

    private static Link link(String href) {
        return Link.builder().href(href).build();
    }

    private static String bindingHref(String bindingId) {
        return "https://api.example.org/v3/service_route_bindings/" + bindingId;
    }

    private static String serviceInstanceHref(String serviceInstanceId) {
        return "https://api.example.org/v3/service_instances/" + serviceInstanceId;
    }

    private static String routeHref() {
        return "https://api.example.org/v3/routes/" + ROUTE_ID;
    }

    private static String parametersHref(String bindingId) {
        return "https://api.example.org/v3/service_route_bindings/" + bindingId + "/parameters";
    }

    private static Metadata emptyMetadata() {
        return Metadata.builder()
                .labels(Collections.emptyMap())
                .annotations(Collections.emptyMap())
                .build();
    }

    private static Metadata metadataWithLabel(String key, String value) {
        return Metadata.builder().label(key, value).annotations(Collections.emptyMap()).build();
    }

    private static ServiceRouteBindingResource bindingResource(
            String bindingId, String serviceInstanceId, String lastOperationType) {
        return ServiceRouteBindingResource.builder()
                .id(bindingId)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .lastOperation(lastOperation(lastOperationType))
                .relationships(relationships(serviceInstanceId))
                .link("self", link(bindingHref(bindingId)))
                .link("service_instance", link(serviceInstanceHref(serviceInstanceId)))
                .link("route", link(routeHref()))
                .link("parameters", link(parametersHref(bindingId)))
                .metadata(emptyMetadata())
                .build();
    }
}

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

package org.cloudfoundry.reactor.client.v3.servicebindings;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.servicebindings.CreateServiceBindingData;
import org.cloudfoundry.client.v3.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v3.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v3.servicebindings.DeleteServiceBindingRequest;
import org.cloudfoundry.client.v3.servicebindings.GetServiceBindingRequest;
import org.cloudfoundry.client.v3.servicebindings.GetServiceBindingResponse;
import org.cloudfoundry.client.v3.servicebindings.ListServiceBindingsRequest;
import org.cloudfoundry.client.v3.servicebindings.ListServiceBindingsResponse;
import org.cloudfoundry.client.v3.servicebindings.ServiceBindingData;
import org.cloudfoundry.client.v3.servicebindings.ServiceBindingRelationships;
import org.cloudfoundry.client.v3.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v3.servicebindings.ServiceBindingType;
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
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorServiceBindingsV3Test extends AbstractClientApiTest {

    private final ReactorServiceBindingsV3 serviceBindings = new ReactorServiceBindingsV3(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/service_bindings")
                .payload("fixtures/client/v3/servicebindings/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v3/servicebindings/POST_response.json")
                .build())
            .build());

        this.serviceBindings
            .create(CreateServiceBindingRequest.builder()
                .data(CreateServiceBindingData.builder()
                    .parameter("some_object_id", "for_the_service_broker")
                    .build())
                .relationships(ServiceBindingRelationships.builder()
                    .application(Relationship.builder()
                        .id("74f7c078-0934-470f-9883-4fddss5b8f13")
                        .build())
                    .serviceInstance(Relationship.builder()
                        .id("8bfe4c1b-9e18-45b1-83be-124163f31f9e")
                        .build())
                    .build())
                .type(ServiceBindingType.APPLICATION)
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateServiceBindingResponse.builder()
                .id("dde5ad2a-d8f4-44dc-a56f-0452d744f1c3")
                .type("app")
                .data(ServiceBindingData.builder()
                    .credential("super-secret", "password")
                    .syslogDrainUrl("syslog://drain.url.com")
                    .build())
                .createdAt("2015-11-13T17:02:56Z")
                .link("self", Link.builder()
                    .href("/v3/service_bindings/dde5ad2a-d8f4-44dc-a56f-0452d744f1c3")
                    .build())
                .link("service_instance", Link.builder()
                    .href("/v3/service_instances/8bfe4c1b-9e18-45b1-83be-124163f31f9e")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/74f7c078-0934-470f-9883-4fddss5b8f13")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/service_bindings/test-service-binding-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.serviceBindings
            .delete(DeleteServiceBindingRequest.builder()
                .serviceBindingId("test-service-binding-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/service_bindings/test-service-binding-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/servicebindings/GET_{id}_response.json")
                .build())
            .build());

        this.serviceBindings
            .get(GetServiceBindingRequest.builder()
                .serviceBindingId("test-service-binding-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetServiceBindingResponse.builder()
                .id("dde5ad2a-d8f4-44dc-a56f-0452d744f1c3")
                .type("app")
                .data(ServiceBindingData.builder()
                    .credential("super-secret", "password")
                    .syslogDrainUrl("syslog://drain.url.com")
                    .build())
                .createdAt("2015-11-13T17:02:56Z")
                .link("self", Link.builder()
                    .href("/v3/service_bindings/dde5ad2a-d8f4-44dc-a56f-0452d744f1c3")
                    .build())
                .link("service_instance", Link.builder()
                    .href("/v3/service_instances/8bfe4c1b-9e18-45b1-83be-124163f31f9e")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/74f7c078-0934-470f-9883-4fddss5b8f13")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/service_bindings?app_guids=test-application-id&order_by=%2Bcreated_at&page=1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/servicebindings/GET_response.json")
                .build())
            .build());

        this.serviceBindings
            .list(ListServiceBindingsRequest.builder()
                .page(1)
                .orderBy("+created_at")
                .applicationId("test-application-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListServiceBindingsResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(3)
                    .first(Link.builder()
                        .href("/v3/service_bindings?page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("/v3/service_bindings?page=2&per_page=2")
                        .build())
                    .next(Link.builder()
                        .href("/v3/service_bindings?page=2&per_page=2")
                        .build())
                    .build())
                .resource(ServiceBindingResource.builder()
                    .id("dde5ad2a-d8f4-44dc-a56f-0452d744f1c3")
                    .type("app")
                    .data(ServiceBindingData.builder()
                        .credential("super-secret", "password")
                        .syslogDrainUrl("syslog://drain.url.com")
                        .build())
                    .createdAt("2015-11-13T17:02:56Z")
                    .link("self", Link.builder()
                        .href("/v3/service_bindings/dde5ad2a-d8f4-44dc-a56f-0452d744f1c3")
                        .build())
                    .link("service_instance", Link.builder()
                        .href("/v3/service_instances/8bfe4c1b-9e18-45b1-83be-124163f31f9e")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/74f7c078-0934-470f-9883-4fddss5b8f13")
                        .build())
                    .build())
                .resource(ServiceBindingResource.builder()
                    .id("7aa37bad-6ccb-4ef9-ba48-9ce3a91b2b62")
                    .type("app")
                    .data(ServiceBindingData.builder()
                        .credential("super-secret", "password")
                        .syslogDrainUrl("syslog://drain.url.com")
                        .build())
                    .createdAt("2015-11-13T17:02:56Z")
                    .link("self", Link.builder()
                        .href("/v3/service_bindings/7aa37bad-6ccb-4ef9-ba48-9ce3a91b2b62")
                        .build())
                    .link("service_instance", Link.builder()
                        .href("/v3/service_instances/8bf356j3-9e18-45b1-3333-124163f31f9e")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/74f7c078-0934-470f-9883-4fddss5b8f13")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}

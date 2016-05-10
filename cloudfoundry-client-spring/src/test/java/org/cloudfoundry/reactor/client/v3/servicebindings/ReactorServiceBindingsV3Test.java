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

package org.cloudfoundry.reactor.client.v3.servicebindings;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v3.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v3.servicebindings.DeleteServiceBindingRequest;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static org.cloudfoundry.client.v3.servicebindings.CreateServiceBindingRequest.ServiceBindingType.APP;

public final class ReactorServiceBindingsV3Test {

    public static final class Create extends AbstractClientApiTest<CreateServiceBindingRequest, CreateServiceBindingResponse> {

        private final ReactorServiceBindingsV3 serviceBindings = new ReactorServiceBindingsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/v3/service_bindings")
                    .payload("fixtures/client/v3/servicebindings/POST_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/client/v3/servicebindings/POST_response.json")
                    .build())
                .build();
        }

        @Override
        protected CreateServiceBindingRequest getInvalidRequest() {
            return CreateServiceBindingRequest.builder()
                .build();
        }

        @Override
        protected CreateServiceBindingResponse getResponse() {
            return CreateServiceBindingResponse.builder()
                .id("dde5ad2a-d8f4-44dc-a56f-0452d744f1c3")
                .type("app")
                .data("credentials", Collections.singletonMap("super-secret", "password"))
                .data("syslog_drain_url", "syslog://drain.url.com")
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
                .build();
        }

        @Override
        protected CreateServiceBindingRequest getValidRequest() throws Exception {
            CreateServiceBindingRequest.Data data = CreateServiceBindingRequest.Data.builder()
                .parameters(Collections.singletonMap("some_object_id", "for_the_service_broker"))
                .build();
            Relationship applicationRelationship = Relationship.builder().id("74f7c078-0934-470f-9883-4fddss5b8f13").build();
            Relationship serviceInstanceRelationship = Relationship.builder().id("8bfe4c1b-9e18-45b1-83be-124163f31f9e").build();
            CreateServiceBindingRequest.Relationships relationships = CreateServiceBindingRequest.Relationships.builder()
                .application(applicationRelationship)
                .serviceInstance(serviceInstanceRelationship)
                .build();

            return CreateServiceBindingRequest.builder()
                .data(data)
                .relationships(relationships)
                .type(APP)
                .build();
        }

        @Override
        protected Mono<CreateServiceBindingResponse> invoke(CreateServiceBindingRequest request) {
            return this.serviceBindings.create(request);
        }

    }

    public static final class Delete extends AbstractClientApiTest<DeleteServiceBindingRequest, Void> {

        private final ReactorServiceBindingsV3 serviceBindings = new ReactorServiceBindingsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v3/service_bindings/test-service-binding-id")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected DeleteServiceBindingRequest getInvalidRequest() {
            return DeleteServiceBindingRequest.builder()
                .build();
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected DeleteServiceBindingRequest getValidRequest() throws Exception {
            return DeleteServiceBindingRequest.builder()
                .serviceBindingId("test-service-binding-id")
                .build();
        }

        @Override
        protected Mono<Void> invoke(DeleteServiceBindingRequest request) {
            return this.serviceBindings.delete(request);
        }

    }

}

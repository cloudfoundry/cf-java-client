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

package org.cloudfoundry.reactor.client.v2.routemappings;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.routemappings.CreateRouteMappingRequest;
import org.cloudfoundry.client.v2.routemappings.CreateRouteMappingResponse;
import org.cloudfoundry.client.v2.routemappings.DeleteRouteMappingRequest;
import org.cloudfoundry.client.v2.routemappings.DeleteRouteMappingResponse;
import org.cloudfoundry.client.v2.routemappings.GetRouteMappingRequest;
import org.cloudfoundry.client.v2.routemappings.GetRouteMappingResponse;
import org.cloudfoundry.client.v2.routemappings.ListRouteMappingsRequest;
import org.cloudfoundry.client.v2.routemappings.ListRouteMappingsResponse;
import org.cloudfoundry.client.v2.routemappings.RouteMappingEntity;
import org.cloudfoundry.client.v2.routemappings.RouteMappingResource;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.test.subscriber.ScriptedSubscriber;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorRouteMappingsTest {

    public static final class Create extends AbstractClientApiTest<CreateRouteMappingRequest, CreateRouteMappingResponse> {

        private final ReactorRouteMappings routeMappings = new ReactorRouteMappings(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<CreateRouteMappingResponse> expectations() {
            return ScriptedSubscriber.<CreateRouteMappingResponse>create()
                .expectValue(CreateRouteMappingResponse.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2016-01-19T19:40:59Z")
                        .id("ca9cdd28-53c4-4b8e-a7e0-1838f69b8f91")
                        .url("/v2/route_mappings/ca9cdd28-53c4-4b8e-a7e0-1838f69b8f91")
                        .build())
                    .entity(RouteMappingEntity.builder()
                        .applicationId("d232b485-b035-4d65-9f77-6b867d859de5")
                        .applicationPort(8888)
                        .routeId("c041e8a3-64d0-4beb-bac8-1900e3aedd07")
                        .applicationUrl("/v2/apps/d232b485-b035-4d65-9f77-6b867d859de5")
                        .routeUrl("/v2/routes/c041e8a3-64d0-4beb-bac8-1900e3aedd07")
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/v2/route_mappings")
                    .payload("fixtures/client/v2/route_mappings/POST_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/client/v2/route_mappings/POST_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<CreateRouteMappingResponse> invoke(CreateRouteMappingRequest request) {
            return this.routeMappings.create(request);
        }

        @Override
        protected CreateRouteMappingRequest validRequest() {
            return CreateRouteMappingRequest.builder()
                .applicationId("d232b485-b035-4d65-9f77-6b867d859de5")
                .routeId("c041e8a3-64d0-4beb-bac8-1900e3aedd07")
                .applicationPort(8888)
                .build();
        }
    }

    public static final class Delete extends AbstractClientApiTest<DeleteRouteMappingRequest, DeleteRouteMappingResponse> {

        private final ReactorRouteMappings routeMappings = new ReactorRouteMappings(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<DeleteRouteMappingResponse> expectations() {
            return ScriptedSubscriber.<DeleteRouteMappingResponse>create()
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/route_mappings/random-route-mapping-id")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected Publisher<DeleteRouteMappingResponse> invoke(DeleteRouteMappingRequest request) {
            return this.routeMappings.delete(request);
        }

        @Override
        protected DeleteRouteMappingRequest validRequest() {
            return DeleteRouteMappingRequest.builder()
                .routeMappingId("random-route-mapping-id")
                .build();
        }
    }

    public static final class DeleteAsync extends AbstractClientApiTest<DeleteRouteMappingRequest, DeleteRouteMappingResponse> {

        private final ReactorRouteMappings routeMappings = new ReactorRouteMappings(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<DeleteRouteMappingResponse> expectations() {
            return ScriptedSubscriber.<DeleteRouteMappingResponse>create()
                .expectValue(DeleteRouteMappingResponse.builder()
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
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v2/route_mappings/random-route-mapping-id?async=true")
                    .build())
                .response(TestResponse.builder()
                    .status(ACCEPTED)
                    .payload("fixtures/client/v2/route_mappings/DELETE_{id}_async_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<DeleteRouteMappingResponse> invoke(DeleteRouteMappingRequest request) {
            return this.routeMappings.delete(request);
        }

        @Override
        protected DeleteRouteMappingRequest validRequest() {
            return DeleteRouteMappingRequest.builder()
                .async(true)
                .routeMappingId("random-route-mapping-id")
                .build();
        }

    }

    public static final class Get extends AbstractClientApiTest<GetRouteMappingRequest, GetRouteMappingResponse> {

        private final ReactorRouteMappings routeMappings = new ReactorRouteMappings(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<GetRouteMappingResponse> expectations() {
            return ScriptedSubscriber.<GetRouteMappingResponse>create()
                .expectValue(GetRouteMappingResponse.builder()
                    .metadata(Metadata.builder()
                        .id("304bead7-ad5a-4f6e-a093-f2a85d30c54a")
                        .createdAt("2016-04-06T00:17:40Z")
                        .url("/v2/route_mappings/304bead7-ad5a-4f6e-a093-f2a85d30c54a")
                        .build())
                    .entity(RouteMappingEntity.builder()
                        .applicationId("65489f49-f437-431a-8f58-c118ce08d83a")
                        .applicationPort(8888)
                        .applicationUrl("/v2/apps/65489f49-f437-431a-8f58-c118ce08d83a")
                        .routeId("c7ce0cac-f1d6-405c-83fd-c2d75513eb23")
                        .routeUrl("/v2/routes/c7ce0cac-f1d6-405c-83fd-c2d75513eb23")
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/route_mappings/route-mapping-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/route_mappings/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected Publisher<GetRouteMappingResponse> invoke(GetRouteMappingRequest request) {
            return this.routeMappings.get(request);
        }

        @Override
        protected GetRouteMappingRequest validRequest() {
            return GetRouteMappingRequest.builder()
                .routeMappingId("route-mapping-id")
                .build();
        }
    }

    public static final class List extends AbstractClientApiTest<ListRouteMappingsRequest, ListRouteMappingsResponse> {

        private final ReactorRouteMappings routeMappings = new ReactorRouteMappings(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

        @Override
        protected ScriptedSubscriber<ListRouteMappingsResponse> expectations() {
            return ScriptedSubscriber.<ListRouteMappingsResponse>create()
                .expectValue(ListRouteMappingsResponse.builder()
                    .totalPages(1)
                    .totalResults(1)
                    .resource(RouteMappingResource.builder()
                        .metadata(Metadata.builder()
                            .createdAt("2016-04-06T00:17:40Z")
                            .id("50dedf28-08db-4cdd-9903-0d74f3b8708d")
                            .url("/v2/route_mappings/50dedf28-08db-4cdd-9903-0d74f3b8708d")
                            .build())
                        .entity(RouteMappingEntity.builder()
                            .applicationId("fbfe5df8-5391-4e75-966b-69fe34b7ee5d")
                            .applicationPort(8888)
                            .routeId("b683ae9e-0a54-4445-a2ea-5d78d9f89266")
                            .applicationUrl("/v2/apps/fbfe5df8-5391-4e75-966b-69fe34b7ee5d")
                            .routeUrl("/v2/routes/b683ae9e-0a54-4445-a2ea-5d78d9f89266")
                            .build())
                        .build())
                    .build())
                .expectComplete();
        }

        @Override
        protected InteractionContext interactionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/route_mappings?page=-1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/route_mappings/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected Mono<ListRouteMappingsResponse> invoke(ListRouteMappingsRequest request) {
            return this.routeMappings.list(request);
        }

        @Override
        protected ListRouteMappingsRequest validRequest() {
            return ListRouteMappingsRequest.builder()
                .page(-1)
                .build();
        }

    }

}

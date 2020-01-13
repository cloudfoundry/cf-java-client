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
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorRouteMappingsTest extends AbstractClientApiTest {

    private final ReactorRouteMappings routeMappings = new ReactorRouteMappings(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/route_mappings")
                .payload("fixtures/client/v2/route_mappings/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/route_mappings/POST_response.json")
                .build())
            .build());

        this.routeMappings
            .create(CreateRouteMappingRequest.builder()
                .applicationId("d232b485-b035-4d65-9f77-6b867d859de5")
                .routeId("c041e8a3-64d0-4beb-bac8-1900e3aedd07")
                .applicationPort(8888)
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateRouteMappingResponse.builder()
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
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/route_mappings/random-route-mapping-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.routeMappings
            .delete(DeleteRouteMappingRequest.builder()
                .routeMappingId("random-route-mapping-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteAsync() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/route_mappings/random-route-mapping-id?async=true")
                .build())
            .response(TestResponse.builder()
                .status(ACCEPTED)
                .payload("fixtures/client/v2/route_mappings/DELETE_{id}_async_response.json")
                .build())
            .build());

        this.routeMappings
            .delete(DeleteRouteMappingRequest.builder()
                .async(true)
                .routeMappingId("random-route-mapping-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(DeleteRouteMappingResponse.builder()
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
                .method(GET).path("/route_mappings/route-mapping-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/route_mappings/GET_{id}_response.json")
                .build())
            .build());

        this.routeMappings
            .get(GetRouteMappingRequest.builder()
                .routeMappingId("route-mapping-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetRouteMappingResponse.builder()
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
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/route_mappings?page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/route_mappings/GET_response.json")
                .build())
            .build());

        this.routeMappings
            .list(ListRouteMappingsRequest.builder()
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListRouteMappingsResponse.builder()
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
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}

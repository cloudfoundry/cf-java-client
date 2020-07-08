/*
 * Copyright 2013-2019 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.routes;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.routes.Application;
import org.cloudfoundry.client.v3.routes.CreateRouteRequest;
import org.cloudfoundry.client.v3.routes.CreateRouteResponse;
import org.cloudfoundry.client.v3.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v3.routes.Destination;
import org.cloudfoundry.client.v3.routes.GetRouteRequest;
import org.cloudfoundry.client.v3.routes.GetRouteResponse;
import org.cloudfoundry.client.v3.routes.InsertRouteDestinationsRequest;
import org.cloudfoundry.client.v3.routes.InsertRouteDestinationsResponse;
import org.cloudfoundry.client.v3.routes.ListRouteDestinationsRequest;
import org.cloudfoundry.client.v3.routes.ListRouteDestinationsResponse;
import org.cloudfoundry.client.v3.routes.ListRoutesRequest;
import org.cloudfoundry.client.v3.routes.ListRoutesResponse;
import org.cloudfoundry.client.v3.routes.Process;
import org.cloudfoundry.client.v3.routes.RemoveRouteDestinationsRequest;
import org.cloudfoundry.client.v3.routes.ReplaceRouteDestinationsRequest;
import org.cloudfoundry.client.v3.routes.ReplaceRouteDestinationsResponse;
import org.cloudfoundry.client.v3.routes.RouteRelationships;
import org.cloudfoundry.client.v3.routes.RouteResource;
import org.cloudfoundry.client.v3.routes.UpdateRouteRequest;
import org.cloudfoundry.client.v3.routes.UpdateRouteResponse;
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
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class ReactorRoutesV3Test extends AbstractClientApiTest {

    private final ReactorRoutesV3 routes = new ReactorRoutesV3(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/routes")
                .payload("fixtures/client/v3/routes/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v3/routes/POST_response.json")
                .build())
            .build());

        this.routes.create(CreateRouteRequest.builder()
            .relationships(RouteRelationships.builder()
                .space(ToOneRelationship.builder()
                    .data(Relationship.builder()
                        .id("space-guid")
                        .build())
                    .build())
                .domain(ToOneRelationship.builder()
                    .data(Relationship.builder()
                        .id("domain-guid")
                        .build())
                    .build())
                .build())
            .path("/some_path")
            .host("test-hostname")
            .metadata(Metadata.builder()
                .label("test-label", "test-label-value")
                .annotation("note", "detailed information")
                .build())
            .build())
            .as(StepVerifier::create)
            .expectNext(CreateRouteResponse.builder()
                .host("test-hostname")
                .id("cbad697f-cac1-48f4-9017-ac08f39dfb31")
                .path("/some_path")
                .url("test-hostname.a-domain.com/some_path")
                .createdAt("2019-11-01T17:17:48Z")
                .updatedAt("2019-11-01T17:17:48Z")
                .metadata(Metadata.builder()
                    .label("test-label", "test-label-value")
                    .annotation("note", "detailed information")
                    .build())
                .relationships(RouteRelationships.builder()
                    .space(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("space-guid")
                            .build())
                        .build())
                    .domain(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("domain-guid")
                            .build())
                        .build())
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/routes/cbad697f-cac1-48f4-9017-ac08f39dfb31")
                    .build())
                .link("space", Link.builder()
                    .href("https://api.example.org/v3/spaces/space-guid")
                    .build())
                .link("domain", Link.builder()
                    .href("https://api.example.org/v3/domains/domain-guid")
                    .build())
                .link("destinations", Link.builder()
                    .href("https://api.example.org/v3/routes/cbad697f-cac1-48f4-9017-ac08f39dfb31/destinations")
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
                .status(ACCEPTED)
                .header("Location", "https://api.example.org/v3/jobs/test-route-id")
                .build())
            .build());

        this.routes
            .delete(DeleteRouteRequest.builder()
                .routeId("test-route-id")
                .build())
            .as(StepVerifier::create)
            .expectNext("test-route-id")
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/routes/cbad697f-cac1-48f4-9017-ac08f39dfb31")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/routes/GET_{id}_response.json")
                .build())
            .build());

        this.routes
            .get(GetRouteRequest.builder()
                .routeId("cbad697f-cac1-48f4-9017-ac08f39dfb31")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetRouteResponse.builder()
                .id("cbad697f-cac1-48f4-9017-ac08f39dfb31")
                .host("test-host")
                .path("/some_path")
                .url("test-host.a-domain.com/some_path")
                .metadata(Metadata.builder()
                    .putAllAnnotations(Collections.emptyMap())
                    .putAllLabels(Collections.emptyMap())
                    .build())
                .createdAt("2019-11-01T17:17:48Z")
                .updatedAt("2019-11-01T17:17:48Z")
                .relationships(RouteRelationships.builder()
                    .domain(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("test-domain-id")
                            .build())
                        .build())
                    .space(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("test-space-id")
                            .build())
                        .build())
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/routes/cbad697f-cac1-48f4-9017-ac08f39dfb31")
                    .build())
                .link("space", Link.builder()
                    .href("https://api.example.org/v3/spaces/test-space-id")
                    .build())
                .link("domain", Link.builder()
                    .href("https://api.example.org/v3/domains/test-domain-id")
                    .build())
                .link("destinations", Link.builder()
                    .href("https://api.example.org/v3/routes/cbad697f-cac1-48f4-9017-ac08f39dfb31/destinations")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));

    }

    @Test
    public void insertDestinations() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/routes/test-route-id/destinations")
                .payload("fixtures/client/v3/routes/POST_{id}_destinations_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/routes/POST_{id}_destinations_response.json")
                .build())
            .build());

        this.routes.insertDestinations(InsertRouteDestinationsRequest.builder()
            .destinations(Destination.builder()
                    .application(Application.builder()
                        .applicationId("1cb006ee-fb05-47e1-b541-c34179ddc446")
                        .build())
                    .build(),
                Destination.builder()
                    .application(Application.builder()
                        .applicationId("01856e12-8ee8-11e9-98a5-bb397dbc818f")
                        .process(Process.builder()
                            .type("api")
                            .build())
                        .build())
                    .port(9000)
                    .build())
            .routeId("test-route-id")
            .build())
            .as(StepVerifier::create)
            .expectNext(InsertRouteDestinationsResponse.builder()
                .destinations(Destination.builder()
                        .destinationId("89323d4e-2e84-43e7-83e9-adbf50a20c0e")
                        .application(Application.builder()
                            .applicationId("1cb006ee-fb05-47e1-b541-c34179ddc446")
                            .process(Process.builder()
                                .type("web")
                                .build())
                            .build())
                        .port(8080)
                        .build(),
                    Destination.builder()
                        .destinationId("fbef10a2-8ee7-11e9-aa2d-abeeaf7b83c5")
                        .application(Application.builder()
                            .applicationId("01856e12-8ee8-11e9-98a5-bb397dbc818f")
                            .process(Process.builder()
                                .type("api")
                                .build())
                            .build())
                        .port(9000)
                        .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/routes/cbad697f-cac1-48f4-9017-ac08f39dfb31/destinations")
                    .build())
                .link("route", Link.builder()
                    .href("https://api.example.org/v3/routes/cbad697f-cac1-48f4-9017-ac08f39dfb31")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/routes")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/routes/GET_response.json")
                .build())
            .build());

        this.routes.list(ListRoutesRequest.builder().build())
            .as(StepVerifier::create)
            .expectNext(ListRoutesResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(3)
                    .totalPages(2)
                    .first(Link.builder()
                        .href("https://api.example.org/v3/routes?page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("https://api.example.org/v3/routes?page=2&per_page=2")
                        .build())
                    .next(Link.builder()
                        .href("https://api.example.org/v3/routes?page=2&per_page=2")
                        .build())
                    .build())
                .resource(RouteResource.builder().host("test-hostname")
                    .id("cbad697f-cac1-48f4-9017-ac08f39dfb31")
                    .path("/some_path")
                    .url("test-hostname.a-domain.com/some_path")
                    .createdAt("2019-11-01T17:17:48Z")
                    .updatedAt("2019-11-01T17:17:48Z")
                    .metadata(Metadata.builder()
                        .label("test-label", "test-label-value")
                        .annotation("note", "detailed information")
                        .build())
                    .relationships(RouteRelationships.builder()
                        .space(ToOneRelationship.builder()
                            .data(Relationship.builder()
                                .id("space-guid")
                                .build())
                            .build())
                        .domain(ToOneRelationship.builder()
                            .data(Relationship.builder()
                                .id("domain-guid")
                                .build())
                            .build())
                        .build())
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/routes/cbad697f-cac1-48f4-9017-ac08f39dfb31")
                        .build())
                    .link("space", Link.builder()
                        .href("https://api.example.org/v3/spaces/space-guid")
                        .build())
                    .link("domain", Link.builder()
                        .href("https://api.example.org/v3/domains/domain-guid")
                        .build())
                    .link("destinations", Link.builder()
                        .href("https://api.example.org/v3/routes/cbad697f-cac1-48f4-9017-ac08f39dfb31/destinations")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listDestinations() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/routes/test-route-id/destinations")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/routes/GET_{id}_destinations_response.json")
                .build())
            .build());

        this.routes.listDestinations(ListRouteDestinationsRequest.builder()
            .routeId("test-route-id")
            .build())
            .as(StepVerifier::create)
            .expectNext(ListRouteDestinationsResponse.builder()
                .destinations(Destination.builder()
                        .destinationId("89323d4e-2e84-43e7-83e9-adbf50a20c0e")
                        .application(Application.builder()
                            .applicationId("1cb006ee-fb05-47e1-b541-c34179ddc446")
                            .process(Process.builder()
                                .type("web")
                                .build())
                            .build())
                        .port(8080)
                        .build(),
                    Destination.builder()
                        .destinationId("fbef10a2-8ee7-11e9-aa2d-abeeaf7b83c5")
                        .application(Application.builder()
                            .applicationId("01856e12-8ee8-11e9-98a5-bb397dbc818f")
                            .process(Process.builder()
                                .type("api")
                                .build())
                            .build())
                        .port(9000)
                        .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/routes/cbad697f-cac1-48f4-9017-ac08f39dfb31/destinations")
                    .build())
                .link("route", Link.builder()
                    .href("https://api.example.org/v3/routes/cbad697f-cac1-48f4-9017-ac08f39dfb31")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void removeDestinations() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/routes/test-route-id/destinations/test-destination-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.routes.removeDestinations(RemoveRouteDestinationsRequest.builder()
            .destinationId("test-destination-id")
            .routeId("test-route-id")
            .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void replaceDestinations() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PATCH).path("/routes/test-route-id/destinations")
                .payload("fixtures/client/v3/routes/PATCH_{id}_destinations_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/routes/PATCH_{id}_destinations_response.json")
                .build())
            .build());

        this.routes.replaceDestinations(ReplaceRouteDestinationsRequest.builder()
            .destinations(Destination.builder()
                    .application(Application.builder()
                        .applicationId("1cb006ee-fb05-47e1-b541-c34179ddc446")
                        .build())
                    .weight(61)
                    .build(),
                Destination.builder()
                    .application(Application.builder()
                        .applicationId("01856e12-8ee8-11e9-98a5-bb397dbc818f")
                        .process(Process.builder()
                            .type("api")
                            .build())
                        .build())
                    .port(9000)
                    .weight(39)
                    .build())
            .routeId("test-route-id")
            .build())
            .as(StepVerifier::create)
            .expectNext(ReplaceRouteDestinationsResponse.builder()
                .destinations(Destination.builder()
                        .destinationId("89323d4e-2e84-43e7-83e9-adbf50a20c0e")
                        .application(Application.builder()
                            .applicationId("1cb006ee-fb05-47e1-b541-c34179ddc446")
                            .process(Process.builder()
                                .type("web")
                                .build())
                            .build())
                        .port(8080)
                        .weight(61)
                        .build(),
                    Destination.builder()
                        .destinationId("fbef10a2-8ee7-11e9-aa2d-abeeaf7b83c5")
                        .application(Application.builder()
                            .applicationId("01856e12-8ee8-11e9-98a5-bb397dbc818f")
                            .process(Process.builder()
                                .type("api")
                                .build())
                            .build())
                        .port(9000)
                        .weight(39)
                        .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/routes/cbad697f-cac1-48f4-9017-ac08f39dfb31/destinations")
                    .build())
                .link("route", Link.builder()
                    .href("https://api.example.org/v3/routes/cbad697f-cac1-48f4-9017-ac08f39dfb31")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PATCH).path("/routes/cbad697f-cac1-48f4-9017-ac08f39dfb31")
                .payload("fixtures/client/v3/routes/PATCH_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/routes/PATCH_{id}_response.json")
                .build())
            .build());

        this.routes.update(UpdateRouteRequest.builder()
            .routeId("cbad697f-cac1-48f4-9017-ac08f39dfb31")
            .metadata(Metadata.builder()
                .label("key", "value")
                .annotation("note", "detailed information")
                .build())
            .build())
            .as(StepVerifier::create)
            .expectNext(UpdateRouteResponse.builder().host("test-hostname")
                .id("cbad697f-cac1-48f4-9017-ac08f39dfb31")
                .path("/some_path")
                .url("test-hostname.a-domain.com/some_path")
                .createdAt("2019-11-01T17:17:48Z")
                .updatedAt("2019-11-01T17:17:48Z")
                .metadata(Metadata.builder()
                    .label("key", "value")
                    .annotation("note", "detailed information")
                    .build())
                .relationships(RouteRelationships.builder()
                    .space(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("space-guid")
                            .build())
                        .build())
                    .domain(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("domain-guid")
                            .build())
                        .build())
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/routes/cbad697f-cac1-48f4-9017-ac08f39dfb31")
                    .build())
                .link("space", Link.builder()
                    .href("https://api.example.org/v3/spaces/space-guid")
                    .build())
                .link("domain", Link.builder()
                    .href("https://api.example.org/v3/domains/domain-guid")
                    .build())
                .link("destinations", Link.builder()
                    .href("https://api.example.org/v3/routes/cbad697f-cac1-48f4-9017-ac08f39dfb31/destinations")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }
}

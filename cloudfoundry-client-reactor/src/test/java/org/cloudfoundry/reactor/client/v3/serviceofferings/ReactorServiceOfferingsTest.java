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

package org.cloudfoundry.reactor.client.v3.serviceofferings;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.time.Duration;
import java.util.Collections;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.serviceofferings.BrokerCatalog;
import org.cloudfoundry.client.v3.serviceofferings.DeleteServiceOfferingRequest;
import org.cloudfoundry.client.v3.serviceofferings.Features;
import org.cloudfoundry.client.v3.serviceofferings.GetServiceOfferingRequest;
import org.cloudfoundry.client.v3.serviceofferings.GetServiceOfferingResponse;
import org.cloudfoundry.client.v3.serviceofferings.ListServiceOfferingsRequest;
import org.cloudfoundry.client.v3.serviceofferings.ListServiceOfferingsResponse;
import org.cloudfoundry.client.v3.serviceofferings.ServiceOfferingRelationships;
import org.cloudfoundry.client.v3.serviceofferings.ServiceOfferingResource;
import org.cloudfoundry.client.v3.serviceofferings.UpdateServiceOfferingRequest;
import org.cloudfoundry.client.v3.serviceofferings.UpdateServiceOfferingResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

final class ReactorServiceOfferingsTest extends AbstractClientApiTest {

    private final ReactorServiceOfferingsV3 serviceOfferings =
            new ReactorServiceOfferingsV3(
                    CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    void delete() {
        mockRequest(
                InteractionContext.builder()
                        .request(
                                TestRequest.builder()
                                        .method(DELETE)
                                        .path("/service_offerings/test-service-offering-id")
                                        .build())
                        .response(TestResponse.builder().status(NO_CONTENT).build())
                        .build());

        this.serviceOfferings
                .delete(
                        DeleteServiceOfferingRequest.builder()
                                .serviceOfferingId("test-service-offering-id")
                                .build())
                .as(StepVerifier::create)
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
                                        .path("/service_offerings/test-service-offering-id")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/serviceofferings/GET_{id}_response.json")
                                        .build())
                        .build());

        this.serviceOfferings
                .get(
                        GetServiceOfferingRequest.builder()
                                .serviceOfferingId("test-service-offering-id")
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        GetServiceOfferingResponse.builder()
                                .id("bf7eb420-11e5-11ea-b7db-4b5d5e7976a9")
                                .name("my_service_offering")
                                .description("Provides my service")
                                .available(true)
                                .tags("relational", "caching")
                                .requires(Collections.emptyList())
                                .createdAt("2019-11-28T13:44:02Z")
                                .updatedAt("2019-11-28T13:44:02Z")
                                .shareable(true)
                                .documentationUrl("https://some-documentation-link.io")
                                .brokerCatalog(
                                        BrokerCatalog.builder()
                                                .brokerCatalogId(
                                                        "db730a8c-11e5-11ea-838a-0f4fff3b1cfb")
                                                .metadata(
                                                        Collections.singletonMap("shareable", true))
                                                .features(
                                                        Features.builder()
                                                                .planUpdateable(true)
                                                                .bindable(true)
                                                                .instancesRetrievable(true)
                                                                .bindingsRetrievable(true)
                                                                .allowContextUpdates(false)
                                                                .build())
                                                .build())
                                .relationships(
                                        ServiceOfferingRelationships.builder()
                                                .serviceBroker(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(
                                                                                        "13c60e38-11e7-11ea-9106-33ee3c5bd4d7")
                                                                                .build())
                                                                .build())
                                                .build())
                                .metadata(
                                        Metadata.builder()
                                                .annotations(Collections.emptyMap())
                                                .labels(Collections.emptyMap())
                                                .build())
                                .link(
                                        "self",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/service_offerings/bf7eb420-11e5-11ea-b7db-4b5d5e7976a")
                                                .build())
                                .link(
                                        "service_plans",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/service_plans?service_offering_guids=bf7eb420-11e5-11ea-b7db-4b5d5e7976a")
                                                .build())
                                .link(
                                        "service_broker",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/service_brokers/13c60e38-11e7-11ea-9106-33ee3c5bd4d7")
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
                                        .path("/service_offerings")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(OK)
                                        .payload(
                                                "fixtures/client/v3/serviceofferings/GET_response.json")
                                        .build())
                        .build());

        this.serviceOfferings
                .list(ListServiceOfferingsRequest.builder().build())
                .as(StepVerifier::create)
                .expectNext(
                        ListServiceOfferingsResponse.builder()
                                .pagination(
                                        Pagination.builder()
                                                .totalResults(3)
                                                .totalPages(2)
                                                .first(
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_offerings?page=1&per_page=2")
                                                                .build())
                                                .last(
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_offerings?page=2&per_page=2")
                                                                .build())
                                                .next(
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_offerings?page=2&per_page=2")
                                                                .build())
                                                .build())
                                .resource(
                                        ServiceOfferingResource.builder()
                                                .id("bf7eb420-11e5-11ea-b7db-4b5d5e7976a9")
                                                .name("my_service_offering")
                                                .description("Provides my service")
                                                .available(true)
                                                .tags("relational", "caching")
                                                .requires(Collections.emptyList())
                                                .createdAt("2019-11-28T13:44:02Z")
                                                .updatedAt("2019-11-28T13:44:02Z")
                                                .shareable(true)
                                                .documentationUrl(
                                                        "https://some-documentation-link.io")
                                                .brokerCatalog(
                                                        BrokerCatalog.builder()
                                                                .brokerCatalogId(
                                                                        "db730a8c-11e5-11ea-838a-0f4fff3b1cfb")
                                                                .metadata(
                                                                        Collections.singletonMap(
                                                                                "shareable", true))
                                                                .features(
                                                                        Features.builder()
                                                                                .planUpdateable(
                                                                                        true)
                                                                                .bindable(true)
                                                                                .instancesRetrievable(
                                                                                        true)
                                                                                .bindingsRetrievable(
                                                                                        true)
                                                                                .allowContextUpdates(
                                                                                        false)
                                                                                .build())
                                                                .build())
                                                .relationships(
                                                        ServiceOfferingRelationships.builder()
                                                                .serviceBroker(
                                                                        ToOneRelationship.builder()
                                                                                .data(
                                                                                        Relationship
                                                                                                .builder()
                                                                                                .id(
                                                                                                        "13c60e38-11e7-11ea-9106-33ee3c5bd4d7")
                                                                                                .build())
                                                                                .build())
                                                                .build())
                                                .metadata(
                                                        Metadata.builder()
                                                                .annotations(Collections.emptyMap())
                                                                .labels(Collections.emptyMap())
                                                                .build())
                                                .link(
                                                        "self",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_offerings/bf7eb420-11e5-11ea-b7db-4b5d5e7976a")
                                                                .build())
                                                .link(
                                                        "service_plans",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_plans?service_offering_guids=bf7eb420-11e5-11ea-b7db-4b5d5e7976a")
                                                                .build())
                                                .link(
                                                        "service_broker",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_brokers/13c60e38-11e7-11ea-9106-33ee3c5bd4d7")
                                                                .build())
                                                .build())
                                .resource(
                                        ServiceOfferingResource.builder()
                                                .id("20e6cd62-12bb-11ea-90d1-7bfec2c75bcd")
                                                .name("other_service_offering")
                                                .description("Provides another service")
                                                .available(true)
                                                .tag("caching")
                                                .requires(Collections.emptyList())
                                                .createdAt("2019-11-29T16:44:02Z")
                                                .updatedAt("2019-11-29T16:44:02Z")
                                                .shareable(true)
                                                .documentationUrl(
                                                        "https://some-other-documentation-link.io")
                                                .brokerCatalog(
                                                        BrokerCatalog.builder()
                                                                .brokerCatalogId(
                                                                        "3cb11822-12bb-11ea-beb1-a350dc7453b9")
                                                                .metadata(
                                                                        Collections.singletonMap(
                                                                                "shareable", true))
                                                                .features(
                                                                        Features.builder()
                                                                                .planUpdateable(
                                                                                        true)
                                                                                .bindable(true)
                                                                                .instancesRetrievable(
                                                                                        true)
                                                                                .bindingsRetrievable(
                                                                                        true)
                                                                                .allowContextUpdates(
                                                                                        false)
                                                                                .build())
                                                                .build())
                                                .relationships(
                                                        ServiceOfferingRelationships.builder()
                                                                .serviceBroker(
                                                                        ToOneRelationship.builder()
                                                                                .data(
                                                                                        Relationship
                                                                                                .builder()
                                                                                                .id(
                                                                                                        "13c60e38-11e7-11ea-9106-33ee3c5bd4d7")
                                                                                                .build())
                                                                                .build())
                                                                .build())
                                                .metadata(
                                                        Metadata.builder()
                                                                .annotations(Collections.emptyMap())
                                                                .labels(Collections.emptyMap())
                                                                .build())
                                                .link(
                                                        "self",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_offerings/20e6cd62-12bb-11ea-90d1-7bfec2c75bcd")
                                                                .build())
                                                .link(
                                                        "service_plans",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_plans?service_offering_guids=20e6cd62-12bb-11ea-90d1-7bfec2c75bcd")
                                                                .build())
                                                .link(
                                                        "service_broker",
                                                        Link.builder()
                                                                .href(
                                                                        "https://api.example.org/v3/service_brokers/13c60e38-11e7-11ea-9106-33ee3c5bd4d7")
                                                                .build())
                                                .build())
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
                                        .path("/service_offerings/test-service-offering-id")
                                        .payload(
                                                "fixtures/client/v3/serviceofferings/PATCH_{id}_request.json")
                                        .build())
                        .response(
                                TestResponse.builder()
                                        .status(CREATED)
                                        .payload(
                                                "fixtures/client/v3/serviceofferings/PATCH_{id}_response.json")
                                        .build())
                        .build());

        this.serviceOfferings
                .update(
                        UpdateServiceOfferingRequest.builder()
                                .serviceOfferingId("test-service-offering-id")
                                .metadata(
                                        Metadata.builder()
                                                .annotation("note", "detailed information")
                                                .label("key", "value")
                                                .build())
                                .build())
                .as(StepVerifier::create)
                .expectNext(
                        UpdateServiceOfferingResponse.builder()
                                .id("bf7eb420-11e5-11ea-b7db-4b5d5e7976a9")
                                .name("my_service_offering")
                                .description("Provides my service")
                                .available(true)
                                .tags("relational", "caching")
                                .requires(Collections.emptyList())
                                .createdAt("2019-11-28T13:44:02Z")
                                .updatedAt("2019-11-28T13:44:02Z")
                                .shareable(true)
                                .documentationUrl("https://some-documentation-link.io")
                                .brokerCatalog(
                                        BrokerCatalog.builder()
                                                .brokerCatalogId(
                                                        "db730a8c-11e5-11ea-838a-0f4fff3b1cfb")
                                                .metadata(
                                                        Collections.singletonMap("shareable", true))
                                                .features(
                                                        Features.builder()
                                                                .planUpdateable(true)
                                                                .bindable(true)
                                                                .instancesRetrievable(true)
                                                                .bindingsRetrievable(true)
                                                                .allowContextUpdates(false)
                                                                .build())
                                                .build())
                                .relationships(
                                        ServiceOfferingRelationships.builder()
                                                .serviceBroker(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(
                                                                                        "13c60e38-11e7-11ea-9106-33ee3c5bd4d7")
                                                                                .build())
                                                                .build())
                                                .build())
                                .metadata(
                                        Metadata.builder()
                                                .annotations(Collections.emptyMap())
                                                .labels(Collections.emptyMap())
                                                .build())
                                .link(
                                        "self",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/service_offerings/bf7eb420-11e5-11ea-b7db-4b5d5e7976a")
                                                .build())
                                .link(
                                        "service_plans",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/service_plans?service_offering_guids=bf7eb420-11e5-11ea-b7db-4b5d5e7976a")
                                                .build())
                                .link(
                                        "service_broker",
                                        Link.builder()
                                                .href(
                                                        "https://api.example.org/v3/service_brokers/13c60e38-11e7-11ea-9106-33ee3c5bd4d7")
                                                .build())
                                .build())
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }
}

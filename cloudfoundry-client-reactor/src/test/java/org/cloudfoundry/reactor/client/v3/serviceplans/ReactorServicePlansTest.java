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

package org.cloudfoundry.reactor.client.v3.serviceplans;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Metadata;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.serviceplans.BrokerCatalog;
import org.cloudfoundry.client.v3.serviceplans.Cost;
import org.cloudfoundry.client.v3.serviceplans.DeleteServicePlanRequest;
import org.cloudfoundry.client.v3.serviceplans.Features;
import org.cloudfoundry.client.v3.serviceplans.GetServicePlanRequest;
import org.cloudfoundry.client.v3.serviceplans.GetServicePlanResponse;
import org.cloudfoundry.client.v3.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v3.serviceplans.ListServicePlansResponse;
import org.cloudfoundry.client.v3.serviceplans.MaintenanceInfo;
import org.cloudfoundry.client.v3.serviceplans.Parameters;
import org.cloudfoundry.client.v3.serviceplans.Schema;
import org.cloudfoundry.client.v3.serviceplans.Schemas;
import org.cloudfoundry.client.v3.serviceplans.ServiceBindingSchema;
import org.cloudfoundry.client.v3.serviceplans.ServiceInstanceSchema;
import org.cloudfoundry.client.v3.serviceplans.ServicePlanRelationships;
import org.cloudfoundry.client.v3.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v3.serviceplans.UpdateServicePlanRequest;
import org.cloudfoundry.client.v3.serviceplans.UpdateServicePlanResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.cloudfoundry.client.v3.serviceplans.Visibility.ADMIN;
import static org.cloudfoundry.client.v3.serviceplans.Visibility.ORGANIZATION;
import static org.cloudfoundry.client.v3.serviceplans.Visibility.PUBLIC;

public final class ReactorServicePlansTest extends AbstractClientApiTest {

    private final ReactorServicePlansV3 servicePlans = new ReactorServicePlansV3(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/service_plans/test-service-plan-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.servicePlans
            .delete(DeleteServicePlanRequest.builder()
                .servicePlanId("test-service-plan-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        Map<String, String> details = new HashMap<>();
        details.put("description", "Billing account number used to charge use of shared fake server.");
        details.put("type", "string");

        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/service_plans/test-service-plan-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/service_plans/GET_{id}_response.json")
                .build())
            .build());

        this.servicePlans
            .get(GetServicePlanRequest.builder()
                .servicePlanId("test-service-plan-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetServicePlanResponse.builder()
                .id("bf7eb420-11e5-11ea-b7db-4b5d5e7976a9")
                .name("my_big_service_plan")
                .description("Big")
                .visibilityType(PUBLIC)
                .available(true)
                .free(false)
                .cost(Cost.builder()
                    .currency("USD")
                    .amount(199.99F)
                    .unit("Monthly")
                    .build())
                .createdAt("2019-11-28T13:44:02Z")
                .updatedAt("2019-11-28T13:44:02Z")
                .maintenanceInfo(MaintenanceInfo.builder()
                    .version("1.0.0+dev4")
                    .description("Database version 7.8.0")
                    .build())
                .brokerCatalog(BrokerCatalog.builder()
                    .brokerCatalogId("db730a8c-11e5-11ea-838a-0f4fff3b1cfb")
                    .metadata(Collections.singletonMap("custom-key", "custom-information"))
                    .features(Features.builder()
                        .planUpdateable(true)
                        .bindable(true)
                        .build())
                    .build())
                .schemas(Schemas.builder()
                    .serviceInstance(ServiceInstanceSchema.builder()
                        .create(Schema.builder()
                            .parameters(Parameters.builder()
                                .jsonSchema("http://json-schema.org/draft-04/schema#")
                                .type("object")
                                .properties(Collections.singletonMap("billing-account", details))
                                .property("billing-account", details)
                                .build())
                            .build())
                        .update(Schema.builder()
                            .build())
                        .build())
                    .serviceBinding(ServiceBindingSchema.builder()
                        .create(Schema.builder()
                            .build())
                        .build())
                    .build())
                .relationships(ServicePlanRelationships.builder()
                    .serviceOffering(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("13c60e38-11e7-11ea-9106-33ee3c5bd4d7")
                            .build())
                        .build())
                    .build())
                .metadata(Metadata.builder()
                    .annotations(Collections.emptyMap())
                    .labels(Collections.emptyMap())
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/service_plans/bf7eb420-11e5-11ea-b7db-4b5d5e7976a9")
                    .build())
                .link("service_offering", Link.builder()
                    .href("https://api.example.org/v3/service_offerings/13c60e38-11e7-11ea-9106-33ee3c5bd4d7")
                    .build())
                .link("visibility", Link.builder()
                    .href("https://api.example.org/v3/service_plans/bf7eb420-11e5-11ea-b7db-4b5d5e7976a9/visibility")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        Map<String, String> details = new HashMap<>();
        details.put("description", "Billing account number used to charge use of shared fake server.");
        details.put("type", "string");

        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/service_plans")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/service_plans/GET_response.json")
                .build())
            .build());

        this.servicePlans
            .list(ListServicePlansRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListServicePlansResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(3)
                    .totalPages(2)
                    .first(Link.builder()
                        .href("https://api.example.org/v3/service_plans?page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("https://api.example.org/v3/service_plans?page=2&per_page=2")
                        .build())
                    .next(Link.builder()
                        .href("https://api.example.org/v3/service_plans?page=2&per_page=2")
                        .build())
                    .build())
                .resource(ServicePlanResource.builder()
                    .id("bf7eb420-11e5-11ea-b7db-4b5d5e7976a9")
                    .name("my_big_service_plan")
                    .description("Big plan")
                    .visibilityType(ORGANIZATION)
                    .available(true)
                    .free(false)
                    .cost(Cost.builder()
                        .currency("USD")
                        .amount(199.99F)
                        .unit("Monthly")
                        .build())
                    .createdAt("2019-11-28T13:44:02Z")
                    .updatedAt("2019-11-28T13:44:02Z")
                    .maintenanceInfo(MaintenanceInfo.builder()
                        .version("1.0.0+dev4")
                        .description("Database version 7.8.0")
                        .build())
                    .brokerCatalog(BrokerCatalog.builder()
                        .brokerCatalogId("db730a8c-11e5-11ea-838a-0f4fff3b1cfb")
                        .metadata(Collections.singletonMap("custom-key", "custom-value"))
                        .features(Features.builder()
                            .planUpdateable(true)
                            .bindable(true)
                            .build())
                        .build())
                    .schemas(Schemas.builder()
                        .serviceInstance(ServiceInstanceSchema.builder()
                            .create(Schema.builder()
                                .parameters(Parameters.builder()
                                    .jsonSchema("http://json-schema.org/draft-04/schema#")
                                    .type("object")
                                    .properties(Collections.singletonMap("billing-account", details))
                                    .property("billing-account", details)
                                    .build())
                                .build())
                            .update(Schema.builder()
                                .build())
                            .build())
                        .serviceBinding(ServiceBindingSchema.builder()
                            .create(Schema.builder()
                                .build())
                            .build())
                        .build())
                    .relationships(ServicePlanRelationships.builder()
                        .serviceOffering(ToOneRelationship.builder()
                            .data(Relationship.builder()
                                .id("13c60e38-11e7-11ea-9106-33ee3c5bd4d7")
                                .build())
                            .build())
                        .build())
                    .metadata(Metadata.builder()
                        .annotations(Collections.emptyMap())
                        .labels(Collections.emptyMap())
                        .build())
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/service_plans/bf7eb420-11e5-11ea-b7db-4b5d5e7976a9")
                        .build())
                    .link("service_offering", Link.builder()
                        .href("https://api.example.org/v3/service_offerings/13c60e38-11e7-11ea-9106-33ee3c5bd4d7")
                        .build())
                    .link("visibility", Link.builder()
                        .href("https://api.example.org/v3/service_plans/bf7eb420-11e5-11ea-b7db-4b5d5e7976a9/visibility")
                        .build())
                    .build())
                .resource(ServicePlanResource.builder()
                    .id("20e6cd62-12bb-11ea-90d1-7bfec2c75bcd")
                    .name("other_service_plan")
                    .description("Provides another service plan")
                    .visibilityType(ADMIN)
                    .available(true)
                    .free(true)
                    .createdAt("2019-11-29T16:44:02Z")
                    .updatedAt("2019-11-29T16:44:02Z")
                    .maintenanceInfo(MaintenanceInfo.builder()
                        .build())
                    .brokerCatalog(BrokerCatalog.builder()
                        .brokerCatalogId("3cb11822-12bb-11ea-beb1-a350dc7453b9")
                        .metadata(Collections.singletonMap("other-data", true))
                        .features(Features.builder()
                            .planUpdateable(true)
                            .bindable(true)
                            .build())
                        .build())
                    .schemas(Schemas.builder()
                        .serviceInstance(ServiceInstanceSchema.builder()
                            .create(Schema.builder()
                                .parameters(null)
                                .build())
                            .update(Schema.builder()
                                .parameters(null)
                                .build())
                            .build())
                        .serviceBinding(ServiceBindingSchema.builder()
                            .create(Schema.builder()
                                .parameters(null)
                                .build())
                            .build())
                        .build())
                    .relationships(ServicePlanRelationships.builder()
                        .serviceOffering(ToOneRelationship.builder()
                            .data(Relationship.builder()
                                .id("13c60e38-11e7-11ea-9106-33ee3c5bd4d7")
                                .build())
                            .build())
                        .build())
                    .metadata(Metadata.builder()
                        .annotations(Collections.emptyMap())
                        .labels(Collections.emptyMap())
                        .build())
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/service_plans/20e6cd62-12bb-11ea-90d1-7bfec2c75bcd")
                        .build())
                    .link("service_offering", Link.builder()
                        .href("https://api.example.org/v3/service_offerings/13c60e38-11e7-11ea-9106-33ee3c5bd4d7")
                        .build())
                    .link("visibility", Link.builder()
                        .href("https://api.example.org/v3/service_plans/20e6cd62-12bb-11ea-90d1-7bfec2c75bcd/visibility")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        Map<String, String> details = new HashMap<>();
        details.put("description", "Billing account number used to charge use of shared fake server.");
        details.put("type", "string");

        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PATCH).path("/service_plans/test-service-plan-id")
                .payload("fixtures/client/v3/service_plans/PATCH_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v3/service_plans/PATCH_{id}_response.json")
                .build())
            .build());

        this.servicePlans
            .update(UpdateServicePlanRequest.builder()
                .servicePlanId("test-service-plan-id")
                .metadata(Metadata.builder()
                    .annotation("note", "detailed information")
                    .label("key", "value")
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateServicePlanResponse.builder()
                .id("bf7eb420-11e5-11ea-b7db-4b5d5e7976a9")
                .name("my_big_service_plan")
                .description("Big")
                .visibilityType(PUBLIC)
                .available(true)
                .free(false)
                .cost(Cost.builder()
                    .currency("USD")
                    .amount(199.99F)
                    .unit("Monthly")
                    .build())
                .createdAt("2019-11-28T13:44:02Z")
                .updatedAt("2019-11-28T13:44:02Z")
                .maintenanceInfo(MaintenanceInfo.builder()
                    .version("1.0.0+dev4")
                    .description("Database version 7.8.0")
                    .build())
                .brokerCatalog(BrokerCatalog.builder()
                    .brokerCatalogId("db730a8c-11e5-11ea-838a-0f4fff3b1cfb")
                    .metadata("custom-key", "custom-information")
                    .features(Features.builder()
                        .planUpdateable(true)
                        .bindable(true)
                        .build())
                    .build())
                .schemas(Schemas.builder()
                    .serviceInstance(ServiceInstanceSchema.builder()
                        .create(Schema.builder()
                            .parameters(Parameters.builder()
                                .jsonSchema("http://json-schema.org/draft-04/schema#")
                                .type("object")
                                .properties(Collections.singletonMap("billing-account", details))
                                .property("billing-account", details)
                                .build())
                            .build())
                        .update(Schema.builder()
                            .build())
                        .build())
                    .serviceBinding(ServiceBindingSchema.builder()
                        .create(Schema.builder()
                            .build())
                        .build())
                    .build())
                .relationships(ServicePlanRelationships.builder()
                    .serviceOffering(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("13c60e38-11e7-11ea-9106-33ee3c5bd4d7")
                            .build())
                        .build())
                    .build())
                .metadata(Metadata.builder()
                    .annotations(Collections.emptyMap())
                    .labels(Collections.emptyMap())
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/service_plans/bf7eb420-11e5-11ea-b7db-4b5d5e7976a9")
                    .build())
                .link("service_offering", Link.builder()
                    .href("https://api.example.org/v3/service_offerings/13c60e38-11e7-11ea-9106-33ee3c5bd4d7")
                    .build())
                .link("visibility", Link.builder()
                    .href("https://api.example.org/v3/service_plans/bf7eb420-11e5-11ea-b7db-4b5d5e7976a9/visibility")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}

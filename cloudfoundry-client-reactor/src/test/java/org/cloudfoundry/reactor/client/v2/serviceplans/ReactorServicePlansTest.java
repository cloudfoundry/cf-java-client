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

package org.cloudfoundry.reactor.client.v2.serviceplans;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceplans.DeleteServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.DeleteServicePlanResponse;
import org.cloudfoundry.client.v2.serviceplans.GetServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.GetServicePlanResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlanServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlanServiceInstancesResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansResponse;
import org.cloudfoundry.client.v2.serviceplans.Parameters;
import org.cloudfoundry.client.v2.serviceplans.Schema;
import org.cloudfoundry.client.v2.serviceplans.Schemas;
import org.cloudfoundry.client.v2.serviceplans.ServiceBindingSchema;
import org.cloudfoundry.client.v2.serviceplans.ServiceInstanceSchema;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanEntity;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.serviceplans.UpdateServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.UpdateServicePlanResponse;
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
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorServicePlansTest extends AbstractClientApiTest {

    private final ReactorServicePlans servicePlans = new ReactorServicePlans(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

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
    public void deleteAsync() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/service_plans/test-service-plan-id?async=true")
                .build())
            .response(TestResponse.builder()
                .status(ACCEPTED)
                .payload("fixtures/client/v2/service_plans/DELETE_{id}_async_response.json")
                .build())
            .build());

        this.servicePlans
            .delete(DeleteServicePlanRequest.builder()
                .async(true)
                .servicePlanId("test-service-plan-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(DeleteServicePlanResponse.builder()
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
        Map<String, String> details = new HashMap<>();
        details.put("description", "Billing account number used to charge use of shared fake server.");
        details.put("type", "string");

        Schema testSchema = Schema.builder()
            .parameters(Parameters.builder()
                .jsonSchema("http://json-schema.org/draft-04/schema#")
                .properties(Collections.singletonMap("billing-account", details))
                .type("object")
                .build())
            .build();

        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/service_plans/test-service-plan-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_plans/GET_{id}_response.json")
                .build())
            .build());

        this.servicePlans
            .get(GetServicePlanRequest.builder()
                .servicePlanId("test-service-plan-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetServicePlanResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2015-07-27T22:43:16Z")
                    .id("f6ceb8a2-e6fc-43d5-a11b-7ced9e1b47c7")
                    .url("/v2/service_plans/f6ceb8a2-e6fc-43d5-a11b-7ced9e1b47c7")
                    .build())
                .entity(ServicePlanEntity.builder()
                    .name("name-462")
                    .free(false)
                    .description("desc-52")
                    .serviceId("8ac39757-0f9d-4295-9b6f-e626f7ee3cd4")
                    .uniqueId("2aa0162c-9c88-4084-ad1d-566a09e8d316")
                    .publiclyVisible(true)
                    .active(true)
                    .schemas(Schemas.builder()
                        .serviceBinding(ServiceBindingSchema.builder()
                            .create(testSchema)
                            .build())
                        .serviceInstance(ServiceInstanceSchema.builder()
                            .create(testSchema)
                            .update(testSchema)
                            .build())
                        .build())
                    .serviceUrl("/v2/services/8ac39757-0f9d-4295-9b6f-e626f7ee3cd4")
                    .serviceInstancesUrl("/v2/service_plans/f6ceb8a2-e6fc-43d5-a11b-7ced9e1b47c7/service_instances")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/service_plans?q=service_guid%3Atest-service-id&page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_plans/GET_response.json")
                .build())
            .build());

        this.servicePlans
            .list(ListServicePlansRequest.builder()
                .serviceId("test-service-id")
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListServicePlansResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ServicePlanResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:16Z")
                        .id("956cb355-3acc-4ced-8161-a57b9b5c7943")
                        .url("/v2/service_plans/956cb355-3acc-4ced-8161-a57b9b5c7943")
                        .build())
                    .entity(ServicePlanEntity.builder()
                        .name("name-464")
                        .free(false)
                        .description("desc-54")
                        .serviceId("83dc64ef-eb0a-454c-b3d9-c554921f3bd2")
                        .uniqueId("49aee95b-2108-4bbb-9769-c6197f308acf")
                        .publiclyVisible(true)
                        .active(true)
                        .serviceUrl("/v2/services/83dc64ef-eb0a-454c-b3d9-c554921f3bd2")
                        .serviceInstancesUrl("/v2/service_plans/956cb355-3acc-4ced-8161-a57b9b5c7943/service_instances")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listServiceInstances() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/service_plans/test-service-plan-id/service_instances?q=space_guid%3Atest-space-id&page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_plans/GET_{id}_service_instances_response.json")
                .build())
            .build());

        this.servicePlans
            .listServiceInstances(ListServicePlanServiceInstancesRequest.builder()
                .servicePlanId("test-service-plan-id")
                .spaceId("test-space-id")
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListServicePlanServiceInstancesResponse.builder()
                .totalResults(1)
                .totalPages(1)
                .resource(ServiceInstanceResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:16Z")
                        .id("b95c56b9-f81b-4d34-9a00-a1a1ddba5f2f")
                        .url("/v2/service_instances/b95c56b9-f81b-4d34-9a00-a1a1ddba5f2f")
                        .build())
                    .entity(ServiceInstanceEntity.builder()
                        .name("name-457")
                        .credential("creds-key-268", "creds-val-268")
                        .servicePlanId("bb29926c-7482-4ae5-803c-ec99e95aa278")
                        .spaceId("cf5812f5-bf43-40cc-88d4-d50b76d7797d")
                        .type("managed_service_instance")
                        .tags(Collections.emptyList())
                        .spaceUrl("/v2/spaces/cf5812f5-bf43-40cc-88d4-d50b76d7797d")
                        .servicePlanUrl("/v2/service_plans/bb29926c-7482-4ae5-803c-ec99e95aa278")
                        .serviceBindingsUrl("/v2/service_instances/b95c56b9-f81b-4d34-9a00-a1a1ddba5f2f/service_bindings")
                        .serviceKeysUrl("/v2/service_instances/b95c56b9-f81b-4d34-9a00-a1a1ddba5f2f/service_keys")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/service_plans/test-service-plan-id")
                .payload("fixtures/client/v2/service_plans/PUT_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/service_plans/PUT_{id}_response.json")
                .build())
            .build());

        this.servicePlans
            .update(UpdateServicePlanRequest.builder()
                .servicePlanId("test-service-plan-id")
                .publiclyVisible(false)
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateServicePlanResponse.builder()
                .metadata(Metadata.builder()
                    .id("195f6bd5-0aa4-4a97-9c8d-5410e5e6d4b6")
                    .url("/v2/service_plans/195f6bd5-0aa4-4a97-9c8d-5410e5e6d4b6")
                    .createdAt("2016-02-19T02:04:09Z")
                    .updatedAt("2016-02-19T02:04:09Z")
                    .build())
                .entity(ServicePlanEntity.builder()
                    .name("name-2674")
                    .free(false)
                    .description("desc-225")
                    .serviceId("42bea093-8fe5-491a-8a34-b1943dc3709a")
                    .uniqueId("7c4f2f8a-aa82-49e9-9f0c-76248aa1036d")
                    .publiclyVisible(false)
                    .active(true)
                    .serviceUrl("/v2/services/42bea093-8fe5-491a-8a34-b1943dc3709a")
                    .serviceInstancesUrl("/v2/service_plans/195f6bd5-0aa4-4a97-9c8d-5410e5e6d4b6/service_instances")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}

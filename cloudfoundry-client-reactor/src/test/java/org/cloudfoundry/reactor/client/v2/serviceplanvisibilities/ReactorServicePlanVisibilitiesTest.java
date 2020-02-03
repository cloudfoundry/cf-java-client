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

package org.cloudfoundry.reactor.client.v2.serviceplanvisibilities;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v2.serviceplanvisibilities.CreateServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.CreateServicePlanVisibilityResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.DeleteServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.DeleteServicePlanVisibilityResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.GetServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.GetServicePlanVisibilityResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ListServicePlanVisibilitiesRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ListServicePlanVisibilitiesResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilities;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilityEntity;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilityResource;
import org.cloudfoundry.client.v2.serviceplanvisibilities.UpdateServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.UpdateServicePlanVisibilityResponse;
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

public final class ReactorServicePlanVisibilitiesTest extends AbstractClientApiTest {

    private final ServicePlanVisibilities servicePlanVisibilities = new ReactorServicePlanVisibilities(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/service_plan_visibilities")
                .payload("fixtures/client/v2/service_plan_visibilities/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/service_plan_visibilities/POST_response.json")
                .build())
            .build());

        this.servicePlanVisibilities
            .create(CreateServicePlanVisibilityRequest.builder()
                .organizationId("09be17a1-0cc6-4edb-955c-cf2a2ae85470")
                .servicePlanId("43f5496b-9117-404a-a637-eb38141b05af")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateServicePlanVisibilityResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2015-07-27T22:43:28Z")
                    .id("28a22749-25f4-44bd-a371-c37e2ee53175")
                    .url("/v2/service_plan_visibilities/28a22749-25f4-44bd-a371-c37e2ee53175")
                    .build())
                .entity(ServicePlanVisibilityEntity.builder()
                    .organizationId("09be17a1-0cc6-4edb-955c-cf2a2ae85470")
                    .organizationUrl("/v2/organizations/09be17a1-0cc6-4edb-955c-cf2a2ae85470")
                    .servicePlanId("43f5496b-9117-404a-a637-eb38141b05af")
                    .servicePlanUrl("/v2/service_plans/43f5496b-9117-404a-a637-eb38141b05af")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/service_plan_visibilities/test-service-plan-visibility-id")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.servicePlanVisibilities
            .delete(DeleteServicePlanVisibilityRequest.builder()
                .servicePlanVisibilityId("test-service-plan-visibility-id")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void deleteAsync() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/service_plan_visibilities/test-service-plan-visibility-id?async=true")
                .build())
            .response(TestResponse.builder()
                .status(ACCEPTED)
                .payload("fixtures/client/v2/service_plan_visibilities/DELETE_{id}_async_response.json")
                .build())
            .build());

        this.servicePlanVisibilities
            .delete(DeleteServicePlanVisibilityRequest.builder()
                .async(true)
                .servicePlanVisibilityId("test-service-plan-visibility-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(DeleteServicePlanVisibilityResponse.builder()
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
                .method(GET).path("/service_plan_visibilities/test-service-plan-visibility-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_plan_visibilities/GET_{id}_response.json")
                .build())
            .build());

        this.servicePlanVisibilities
            .get(GetServicePlanVisibilityRequest.builder()
                .servicePlanVisibilityId("test-service-plan-visibility-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetServicePlanVisibilityResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2015-07-27T22:43:28Z")
                    .id("18365c25-898b-4365-911d-6f6a09154297")
                    .url("/v2/service_plan_visibilities/18365c25-898b-4365-911d-6f6a09154297")
                    .build())
                .entity(ServicePlanVisibilityEntity.builder()
                    .organizationId("a1cc950b-ed5b-41eb-8eee-d9a8f85aa1ea")
                    .organizationUrl("/v2/organizations/a1cc950b-ed5b-41eb-8eee-d9a8f85aa1ea")
                    .servicePlanId("ea1ba716-e720-4aef-8a90-439924bb53d0")
                    .servicePlanUrl("/v2/service_plans/ea1ba716-e720-4aef-8a90-439924bb53d0")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/service_plan_visibilities?q=organization_guid%3Atest-organization-id&page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_plan_visibilities/GET_response.json")
                .build())
            .build());

        this.servicePlanVisibilities
            .list(ListServicePlanVisibilitiesRequest.builder()
                .organizationId("test-organization-id")
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListServicePlanVisibilitiesResponse.builder()
                .totalPages(1)
                .totalResults(1)
                .resource(ServicePlanVisibilityResource.builder()
                    .metadata(Metadata.builder()
                        .id("3d5c0584-fbf0-4d75-b68e-226e77496f69")
                        .url("/v2/service_plan_visibilities/3d5c0584-fbf0-4d75-b68e-226e77496f69")
                        .createdAt("2015-07-27T22:43:28Z")
                        .build())
                    .entity(ServicePlanVisibilityEntity.builder()
                        .organizationId("1dbe25db-6a8c-43e7-a941-cc483bb45570")
                        .organizationUrl("/v2/organizations/1dbe25db-6a8c-43e7-a941-cc483bb45570")
                        .servicePlanId("69cab29d-826c-48bf-b435-b43013f9c11b")
                        .servicePlanUrl("/v2/service_plans/69cab29d-826c-48bf-b435-b43013f9c11b")
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
                .method(PUT).path("/service_plan_visibilities/test-service-plan-visibility-id")
                .payload("fixtures/client/v2/service_plan_visibilities/PUT_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v2/service_plan_visibilities/PUT_{id}_response.json")
                .build())
            .build());

        this.servicePlanVisibilities
            .update(UpdateServicePlanVisibilityRequest.builder()
                .organizationId("e4d0b68b-9e73-4253-b03f-2bfda6cd814b")
                .servicePlanId("7288464d-3866-436a-915c-2bada4725e7e")
                .servicePlanVisibilityId("test-service-plan-visibility-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateServicePlanVisibilityResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2015-07-27T22:43:28Z")
                    .id("5f1514f9-66ee-4799-9de2-69f2ec3cb5f1")
                    .updatedAt("2015-07-27T22:43:28Z")
                    .url("/v2/service_plan_visibilities/5f1514f9-66ee-4799-9de2-69f2ec3cb5f1")
                    .build())
                .entity(ServicePlanVisibilityEntity.builder()
                    .organizationId("e4d0b68b-9e73-4253-b03f-2bfda6cd814b")
                    .organizationUrl("/v2/organizations/e4d0b68b-9e73-4253-b03f-2bfda6cd814b")
                    .servicePlanId("7288464d-3866-436a-915c-2bada4725e7e")
                    .servicePlanUrl("/v2/service_plans/7288464d-3866-436a-915c-2bada4725e7e")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }


}

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

package org.cloudfoundry.client.spring.v2.serviceplanvisibilities;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.serviceplanvisibilities.CreateServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.CreateServicePlanVisibilityResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.DeleteServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ListServicePlanVisibilitiesRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ListServicePlanVisibilitiesResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilities;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilityEntity;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilityResource;
import org.reactivestreams.Publisher;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;


public final class SpringServicePlanVisibilitiesTest {

    public static final class Create extends AbstractApiTest<CreateServicePlanVisibilityRequest, CreateServicePlanVisibilityResponse> {

        private final ServicePlanVisibilities servicePlanVisibilities = new SpringServicePlanVisibilities(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected CreateServicePlanVisibilityRequest getInvalidRequest() {
            return CreateServicePlanVisibilityRequest.builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(POST).path("v2/service_plan_visibilities")
                .requestPayload("v2/service_plan_visibilities/POST_request.json")
                .status(CREATED)
                .responsePayload("v2/service_plan_visibilities/POST_response.json");
        }

        @Override
        protected CreateServicePlanVisibilityResponse getResponse() {
            return CreateServicePlanVisibilityResponse.builder()
                .metadata(Resource.Metadata.builder()
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
                .build();
        }

        @Override
        protected CreateServicePlanVisibilityRequest getValidRequest() throws Exception {
            return CreateServicePlanVisibilityRequest.builder()
                .organizationId("09be17a1-0cc6-4edb-955c-cf2a2ae85470")
                .servicePlanId("43f5496b-9117-404a-a637-eb38141b05af")
                .build();
        }

        @Override
        protected Publisher<CreateServicePlanVisibilityResponse> invoke(CreateServicePlanVisibilityRequest request) {
            return this.servicePlanVisibilities.create(request);
        }
    }

    public static final class Delete extends AbstractApiTest<DeleteServicePlanVisibilityRequest, Void> {

        private final ServicePlanVisibilities servicePlanVisibilities = new SpringServicePlanVisibilities(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected DeleteServicePlanVisibilityRequest getInvalidRequest() {
            return DeleteServicePlanVisibilityRequest.builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(DELETE).path("/v2/service_plan_visibilities/test-service-plan-visibility-id?async=true")
                .status(NO_CONTENT);
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected DeleteServicePlanVisibilityRequest getValidRequest() throws Exception {
            return DeleteServicePlanVisibilityRequest.builder()
                .async(true)
                .servicePlanVisibilityId("test-service-plan-visibility-id")
                .build();
        }

        @Override
        protected Publisher<Void> invoke(DeleteServicePlanVisibilityRequest request) {
            return this.servicePlanVisibilities.delete(request);
        }
    }

    public static final class List extends AbstractApiTest<ListServicePlanVisibilitiesRequest, ListServicePlanVisibilitiesResponse> {

        private final ServicePlanVisibilities servicePlanVisibilities = new SpringServicePlanVisibilities(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListServicePlanVisibilitiesRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v2/service_plan_visibilities?q=organization_guid%20IN%20test-organization-id&page=-1")
                    .status(OK)
                    .responsePayload("v2/service_plan_visibilities/GET_response.json");
        }

        @Override
        protected ListServicePlanVisibilitiesResponse getResponse() {
            return ListServicePlanVisibilitiesResponse.builder()
                    .totalPages(1)
                    .totalResults(1)
                    .resource(ServicePlanVisibilityResource.builder()
                            .metadata(Resource.Metadata.builder()
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
                    .build();
        }

        @Override
        protected ListServicePlanVisibilitiesRequest getValidRequest() throws Exception {
            return ListServicePlanVisibilitiesRequest.builder()
                    .organizationId("test-organization-id")
                    .page(-1)
                    .build();
        }

        @Override
        protected Publisher<ListServicePlanVisibilitiesResponse> invoke(ListServicePlanVisibilitiesRequest request) {
            return this.servicePlanVisibilities.list(request);
        }
    }

}

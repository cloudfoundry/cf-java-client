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

package org.cloudfoundry.client.spring.v2.serviceplans;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlanServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlanServiceInstancesResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;


public final class SpringServicePlansTest {

    public static final class ListServiceInstances
            extends AbstractApiTest<ListServicePlanServiceInstancesRequest, ListServicePlanServiceInstancesResponse> {

        private final SpringServicePlans servicePlans = new SpringServicePlans(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListServicePlanServiceInstancesRequest getInvalidRequest() {
            return ListServicePlanServiceInstancesRequest.builder()
                    .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET)
                    .path("v2/service_plans/test-id/service_instances?q=space_guid%20IN%20test-space-id&page=-1")
                    .status(OK)
                    .responsePayload("v2/service_plans/GET_{id}_service_instances_response.json");
        }

        @Override
        protected ListServicePlanServiceInstancesResponse getResponse() {
            return ListServicePlanServiceInstancesResponse.builder()
                    .totalResults(1)
                    .totalPages(1)
                    .resource(ServiceInstanceResource.builder()
                            .metadata(Resource.Metadata.builder()
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
                                    .spaceUrl("/v2/spaces/cf5812f5-bf43-40cc-88d4-d50b76d7797d")
                                    .servicePlanUrl("/v2/service_plans/bb29926c-7482-4ae5-803c-ec99e95aa278")
                                    .serviceBindingsUrl("/v2/service_instances/b95c56b9-f81b-4d34-9a00-a1a1ddba5f2f/service_bindings")
                                    .serviceKeysUrl("/v2/service_instances/b95c56b9-f81b-4d34-9a00-a1a1ddba5f2f/service_keys")
                                    .build())
                            .build())
                    .build();
        }

        @Override
        protected ListServicePlanServiceInstancesRequest getValidRequest() throws Exception {
            return ListServicePlanServiceInstancesRequest.builder()
                    .id("test-id")
                    .spaceId("test-space-id")
                    .page(-1)
                    .build();
        }

        @Override
        protected Mono<ListServicePlanServiceInstancesResponse> invoke(ListServicePlanServiceInstancesRequest request) {
            return this.servicePlans.listServiceInstances(request);
        }

    }


}
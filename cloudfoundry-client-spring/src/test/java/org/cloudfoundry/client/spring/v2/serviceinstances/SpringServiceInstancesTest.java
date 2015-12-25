/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.spring.v2.serviceinstances;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesResponse;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.reactivestreams.Publisher;

import static org.cloudfoundry.client.v2.Resource.Metadata;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringServiceInstancesTest {

    public static final class List extends AbstractApiTest<ListServiceInstancesRequest, ListServiceInstancesResponse> {

        private final SpringServiceInstances serviceInstances = new SpringServiceInstances(this.restTemplate, this.root);

        @Override
        protected ListServiceInstancesRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                    .method(GET).path("/v2/service_instances?q=name%20IN%20test-name&page=-1")
                    .status(OK)
                    .responsePayload("v2/service_instances/GET_response.json");
        }

        @Override
        protected ListServiceInstancesResponse getResponse() {
            return ListServiceInstancesResponse.builder()
                    .totalResults(1)
                    .totalPages(1)
                    .resource(ServiceInstanceResource.builder()
                            .metadata(Metadata.builder()
                                    .id("24ec15f9-f6c7-434a-8893-51baab8408d8")
                                    .url("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8")
                                    .createdAt("2015-07-27T22:43:08Z")
                                    .build())
                            .entity(ServiceInstanceEntity.builder()
                                    .name("name-133")
                                    .credential("creds-key-72", "creds-val-72")
                                    .servicePlanId("2b53255a-8b40-4671-803d-21d3f5d4183a")
                                    .spaceId("83b3e705-49fd-4c40-8adf-f5e34f622a19")
                                    .type("managed_service_instance")
                                    .lastOperation(ServiceInstanceEntity.LastOperation.builder()
                                            .type("create")
                                            .state("succeeded")
                                            .description("service broker-provided description")
                                            .updatedAt("2015-07-27T22:43:08Z")
                                            .createdAt("2015-07-27T22:43:08Z")
                                            .build())
                                    .tag("accounting")
                                    .tag("mongodb")
                                    .spaceUrl("/v2/spaces/83b3e705-49fd-4c40-8adf-f5e34f622a19")
                                    .servicePlanUrl("/v2/service_plans/2b53255a-8b40-4671-803d-21d3f5d4183a")
                                    .serviceBindingsUrl
                                            ("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8/service_bindings")
                                    .serviceKeysUrl
                                            ("/v2/service_instances/24ec15f9-f6c7-434a-8893-51baab8408d8/service_keys")
                                    .build())
                            .build())
                    .build();
        }

        @Override
        protected ListServiceInstancesRequest getValidRequest() {
            return ListServiceInstancesRequest.builder()
                    .name("test-name")
                    .page(-1)
                    .build();
        }

        @Override
        protected Publisher<ListServiceInstancesResponse> invoke(ListServiceInstancesRequest request) {
            return this.serviceInstances.list(request);
        }

    }

}

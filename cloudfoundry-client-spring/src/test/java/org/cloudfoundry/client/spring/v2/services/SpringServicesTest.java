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

package org.cloudfoundry.client.spring.v2.services;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.services.GetServiceRequest;
import org.cloudfoundry.client.v2.services.GetServiceResponse;
import org.cloudfoundry.client.v2.services.ServiceEntity;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;


public final class SpringServicesTest {

    public static final class Get extends AbstractApiTest<GetServiceRequest, GetServiceResponse> {

        private final SpringServices services = new SpringServices(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetServiceRequest getInvalidRequest() {
            return GetServiceRequest.builder()
                .build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/services/test-service-id")
                .status(OK)
                .responsePayload("v2/services/GET_{id}_response.json");
        }

        @Override
        protected GetServiceResponse getResponse() {
            return GetServiceResponse.builder()
                .metadata(Resource.Metadata.builder()
                    .id("58eb36ad-0636-428b-b4ed-afc14e48d926")
                    .url("/v2/services/58eb36ad-0636-428b-b4ed-afc14e48d926")
                    .createdAt("2015-07-27T22:43:35Z")
                    .build())
                .entity(ServiceEntity.builder()
                    .label("label-86")
                    .description("desc-219")
                    .active(true)
                    .bindable(true)
                    .uniqueId("8fbdd3bc-3eee-4b03-97a3-57929484649b")
                    .serviceBrokerId("fe6e3f23-7b92-4855-aaa7-56f515d678c5")
                    .planUpdateable(false)
                    .servicePlansUrl("/v2/services/58eb36ad-0636-428b-b4ed-afc14e48d926/service_plans")
                    .build())
                .build();
        }

        @Override
        protected GetServiceRequest getValidRequest() throws Exception {
            return GetServiceRequest.builder()
                .serviceId("test-service-id")
                .build();
        }

        @Override
        protected Mono<GetServiceResponse> invoke(GetServiceRequest request) {
            return this.services.get(request);
        }

    }

}
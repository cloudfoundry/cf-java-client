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

package org.cloudfoundry.client.spring.v2.serviceusageevents;

import org.cloudfoundry.client.spring.AbstractApiTest;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.serviceusageevents.GetServiceUsageEventsRequest;
import org.cloudfoundry.client.v2.serviceusageevents.GetServiceUsageEventsResponse;
import org.cloudfoundry.client.v2.serviceusageevents.ServiceUsageEventsEntity;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringServiceUsageEventsTest {

    public static final class Get extends AbstractApiTest<GetServiceUsageEventsRequest, GetServiceUsageEventsResponse> {

        private final SpringServiceUsageEvents serviceUsageEvents = new SpringServiceUsageEvents(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetServiceUsageEventsRequest getInvalidRequest() {
            return GetServiceUsageEventsRequest.builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/service_usage_events/9470627d-0488-4d9a-8564-f97571487893")
                .status(OK)
                .responsePayload("v2/service_usage_events/GET_{id}_response.json");
        }

        @Override
        protected GetServiceUsageEventsResponse getResponse() {
            return GetServiceUsageEventsResponse.builder()
                .metadata(Resource.Metadata.builder()
                    .createdAt("2015-07-27T22:43:30Z")
                    .id("9470627d-0488-4d9a-8564-f97571487893")
                    .url("/v2/service_usage_events/9470627d-0488-4d9a-8564-f97571487893")
                    .build())
                .entity(ServiceUsageEventsEntity.builder()
                    .state("CREATED")
                    .organizationId("guid-3f19bc03-d183-4189-bdeb-9f33468181da")
                    .spaceId("guid-d565b0c4-3c38-41dd-a102-1c113c759fbf")
                    .spaceName("name-2160")
                    .serviceInstanceId("guid-4cef8892-46fc-4d70-a5d5-36385989f5df")
                    .serviceInstanceName("name-2161")
                    .serviceInstanceType("type-4")
                    .servicePlanId("guid-f2a17886-488c-4066-9155-a1dbb64adadd")
                    .servicePlanName("name-2162")
                    .serviceId("guid-fdff7ee0-cc1b-4bdb-87d6-b0c3b47cb2b2")
                    .serviceLabel("label-79")
                    .build())
                .build();
        }

        @Override
        protected GetServiceUsageEventsRequest getValidRequest() throws Exception {
            return GetServiceUsageEventsRequest.builder()
                .serviceUsageEventId("9470627d-0488-4d9a-8564-f97571487893")
                .build();
        }

        @Override
        protected Mono<GetServiceUsageEventsResponse> invoke(GetServiceUsageEventsRequest request) {
            return this.serviceUsageEvents.get(request);
        }
    }

}

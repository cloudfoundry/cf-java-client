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

package org.cloudfoundry.spring.client.v2.applicationusageevents;

import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.applicationusageevents.ApplicationUsageEventEntity;
import org.cloudfoundry.client.v2.applicationusageevents.ApplicationUsageEventResource;
import org.cloudfoundry.client.v2.applicationusageevents.GetApplicationUsageEventRequest;
import org.cloudfoundry.client.v2.applicationusageevents.GetApplicationUsageEventResponse;
import org.cloudfoundry.client.v2.applicationusageevents.ListApplicationUsageEventsRequest;
import org.cloudfoundry.client.v2.applicationusageevents.ListApplicationUsageEventsResponse;
import org.cloudfoundry.spring.AbstractApiTest;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

public final class SpringApplicationUsageEventsTest {

    public static final class Get extends AbstractApiTest<GetApplicationUsageEventRequest, GetApplicationUsageEventResponse> {

        private SpringApplicationUsageEvents applicationUsageEvents = new SpringApplicationUsageEvents(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected GetApplicationUsageEventRequest getInvalidRequest() {
            return GetApplicationUsageEventRequest.builder().build();
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/app_usage_events/caac0ed4-febf-48a4-951f-c0a7fadf6a68")
                .status(OK)
                .responsePayload("fixtures/client/v2/app_usage_events/GET_{id}_response.json");
        }

        @Override
        protected GetApplicationUsageEventResponse getResponse() {
            return GetApplicationUsageEventResponse.builder()
                .metadata(Resource.Metadata.builder()
                    .createdAt("2016-03-17T21:41:21Z")
                    .id("caac0ed4-febf-48a4-951f-c0a7fadf6a68")
                    .url("/v2/app_usage_events/caac0ed4-febf-48a4-951f-c0a7fadf6a68")
                    .build())
                .entity(ApplicationUsageEventEntity.builder()
                    .applicationId("guid-8cdd38d1-2c13-46a5-8f5e-e91a6cc4b060")
                    .applicationName("name-1103")
                    .buildpackId("guid-1ffac859-4635-41fd-91bb-3ba07768a5ec")
                    .buildpackName("name-1105")
                    .instanceCount(1)
                    .memoryInMbPerInstances(564)
                    .organizationId("guid-1ed968f6-a9f7-469b-a04f-ed1ebc2df1e7")
                    .packageState("STAGED")
                    .processType("web")
                    .spaceId("guid-9c4485f6-7579-45da-8c07-f62e1bc8c499")
                    .spaceName("name-1104")
                    .state("STARTED")
                    .build())
                .build();
        }

        @Override
        protected GetApplicationUsageEventRequest getValidRequest() throws Exception {
            return GetApplicationUsageEventRequest.builder()
                .applicationUsageEventId("caac0ed4-febf-48a4-951f-c0a7fadf6a68")
                .build();
        }

        @Override
        protected Mono<GetApplicationUsageEventResponse> invoke(GetApplicationUsageEventRequest request) {
            return this.applicationUsageEvents.get(request);
        }
    }

    public static final class List extends AbstractApiTest<ListApplicationUsageEventsRequest, ListApplicationUsageEventsResponse> {

        private SpringApplicationUsageEvents applicationUsageEvents = new SpringApplicationUsageEvents(this.restTemplate, this.root, PROCESSOR_GROUP);

        @Override
        protected ListApplicationUsageEventsRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected RequestContext getRequestContext() {
            return new RequestContext()
                .method(GET).path("/v2/app_usage_events?after_guid=f1d8ddec-d36a-4670-acb8-6082a1f1a95f&results-per-page=1")
                .status(OK)
                .responsePayload("fixtures/client/v2/app_usage_events/GET_response.json");
        }

        @Override
        protected ListApplicationUsageEventsResponse getResponse() {
            return ListApplicationUsageEventsResponse.builder()
                .nextUrl("/v2/app_usage_events?after_guid=f1d8ddec-d36a-4670-acb8-6082a1f1a95f&order-direction=asc&page=2&results-per-page=1")
                .totalPages(2)
                .totalResults(2)
                .resource(ApplicationUsageEventResource.builder()
                    .metadata(Resource.Metadata.builder()
                        .createdAt("2016-03-14T22:30:38Z")
                        .id("12dc4396-b7d1-444e-a3b4-9497c4ca0d14")
                        .url("/v2/app_usage_events/12dc4396-b7d1-444e-a3b4-9497c4ca0d14")
                        .build())
                    .entity(ApplicationUsageEventEntity.builder()
                        .applicationId("guid-1460025b-eb6a-4459-8f43-0d7db43dc71f")
                        .applicationName("name-1783")
                        .buildpackId("guid-c17e9ffa-a1f8-4140-9718-f627be3a3459")
                        .buildpackName("name-1785")
                        .instanceCount(1)
                        .memoryInMbPerInstances(564)
                        .organizationId("guid-7f111ae5-9017-49f6-afe7-3a175b9f7a79")
                        .packageState("STAGED")
                        .processType("web")
                        .spaceId("guid-766a0db1-6391-4d9e-9ce9-f2f7cdf93190")
                        .spaceName("name-1784")
                        .state("STARTED")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListApplicationUsageEventsRequest getValidRequest() throws Exception {
            return ListApplicationUsageEventsRequest.builder()
                .afterApplicationUsageEventId("f1d8ddec-d36a-4670-acb8-6082a1f1a95f")
                .resultsPerPage(1)
                .build();
        }

        @Override
        protected Mono<ListApplicationUsageEventsResponse> invoke(ListApplicationUsageEventsRequest request) {
            return this.applicationUsageEvents.list(request);
        }
    }

}

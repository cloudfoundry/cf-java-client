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

package org.cloudfoundry.reactor.client.v2.applicationusageevents;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.applicationusageevents.ApplicationUsageEventEntity;
import org.cloudfoundry.client.v2.applicationusageevents.ApplicationUsageEventResource;
import org.cloudfoundry.client.v2.applicationusageevents.GetApplicationUsageEventRequest;
import org.cloudfoundry.client.v2.applicationusageevents.GetApplicationUsageEventResponse;
import org.cloudfoundry.client.v2.applicationusageevents.ListApplicationUsageEventsRequest;
import org.cloudfoundry.client.v2.applicationusageevents.ListApplicationUsageEventsResponse;
import org.cloudfoundry.client.v2.applicationusageevents.PurgeAndReseedApplicationUsageEventsRequest;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import reactor.core.publisher.Mono;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorApplicationUsageEventsTest {

    public static final class Get extends AbstractClientApiTest<GetApplicationUsageEventRequest, GetApplicationUsageEventResponse> {

        private final ReactorApplicationUsageEvents applicationUsageEvents = new ReactorApplicationUsageEvents(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/app_usage_events/caac0ed4-febf-48a4-951f-c0a7fadf6a68")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/app_usage_events/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected GetApplicationUsageEventResponse getResponse() {
            return GetApplicationUsageEventResponse.builder()
                .metadata(Metadata.builder()
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

    public static final class List extends AbstractClientApiTest<ListApplicationUsageEventsRequest, ListApplicationUsageEventsResponse> {

        private final ReactorApplicationUsageEvents applicationUsageEvents = new ReactorApplicationUsageEvents(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v2/app_usage_events?after_guid=f1d8ddec-d36a-4670-acb8-6082a1f1a95f&results-per-page=1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v2/app_usage_events/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListApplicationUsageEventsRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected ListApplicationUsageEventsResponse getResponse() {
            return ListApplicationUsageEventsResponse.builder()
                .nextUrl("/v2/app_usage_events?after_guid=f1d8ddec-d36a-4670-acb8-6082a1f1a95f&order-direction=asc&page=2&results-per-page=1")
                .totalPages(2)
                .totalResults(2)
                .resource(ApplicationUsageEventResource.builder()
                    .metadata(Metadata.builder()
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

    public static final class PurgeAndReseed extends AbstractClientApiTest<PurgeAndReseedApplicationUsageEventsRequest, Void> {

        private final ReactorApplicationUsageEvents applicationUsageEvents = new ReactorApplicationUsageEvents(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/v2/app_usage_events/destructively_purge_all_and_reseed_started_apps")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected PurgeAndReseedApplicationUsageEventsRequest getInvalidRequest() {
            return null;
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected PurgeAndReseedApplicationUsageEventsRequest getValidRequest() throws Exception {
            return PurgeAndReseedApplicationUsageEventsRequest.builder().build();
        }

        @Override
        protected Mono<Void> invoke(PurgeAndReseedApplicationUsageEventsRequest request) {
            return this.applicationUsageEvents.purgeAndReseed(request);
        }
    }

}

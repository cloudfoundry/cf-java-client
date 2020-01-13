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

package org.cloudfoundry.reactor.client.v2.serviceusageevents;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.serviceusageevents.GetServiceUsageEventRequest;
import org.cloudfoundry.client.v2.serviceusageevents.GetServiceUsageEventResponse;
import org.cloudfoundry.client.v2.serviceusageevents.ListServiceUsageEventsRequest;
import org.cloudfoundry.client.v2.serviceusageevents.ListServiceUsageEventsResponse;
import org.cloudfoundry.client.v2.serviceusageevents.PurgeAndReseedServiceUsageEventsRequest;
import org.cloudfoundry.client.v2.serviceusageevents.ServiceUsageEventEntity;
import org.cloudfoundry.client.v2.serviceusageevents.ServiceUsageEventResource;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorServiceUsageEventsTest extends AbstractClientApiTest {

    private final ReactorServiceUsageEvents serviceUsageEvents = new ReactorServiceUsageEvents(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/service_usage_events/9470627d-0488-4d9a-8564-f97571487893")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_usage_events/GET_{id}_response.json")
                .build())
            .build());

        this.serviceUsageEvents
            .get(GetServiceUsageEventRequest.builder()
                .serviceUsageEventId("9470627d-0488-4d9a-8564-f97571487893")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetServiceUsageEventResponse.builder()
                .metadata(Metadata.builder()
                    .createdAt("2015-07-27T22:43:30Z")
                    .id("9470627d-0488-4d9a-8564-f97571487893")
                    .url("/v2/service_usage_events/9470627d-0488-4d9a-8564-f97571487893")
                    .build())
                .entity(ServiceUsageEventEntity.builder()
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
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/service_usage_events?after_guid=e5defac2-4ae1-44ac-a3d0-1684ae657453&page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/service_usage_events/GET_response.json")
                .build())
            .build());

        this.serviceUsageEvents
            .list(ListServiceUsageEventsRequest.builder()
                .afterServiceUsageEventId("e5defac2-4ae1-44ac-a3d0-1684ae657453")
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListServiceUsageEventsResponse.builder()
                .totalPages(1)
                .totalResults(1)
                .resource(ServiceUsageEventResource.builder()
                    .metadata(Metadata.builder()
                        .createdAt("2015-07-27T22:43:30Z")
                        .id("0c9c59b8-3462-4acf-be39-aa987f087146")
                        .url("/v2/service_usage_events/0c9c59b8-3462-4acf-be39-aa987f087146")
                        .build())
                    .entity(ServiceUsageEventEntity.builder()
                        .state("CREATED")
                        .organizationId("guid-4dd5a051-3460-4246-a842-1dc2d5983c51")
                        .spaceId("guid-76bd662b-fd5b-4b5c-a393-d65e67f99d53")
                        .spaceName("name-2154")
                        .serviceInstanceId("guid-15a7c119-838d-4516-acd9-062dec25d934")
                        .serviceInstanceName("name-2155")
                        .serviceInstanceType("type-2")
                        .servicePlanId("guid-eddab64c-7be0-407e-91b0-82a8093cdfc5")
                        .servicePlanName("name-2156")
                        .serviceId("guid-d471c693-824c-44a6-b069-a679e323326d")
                        .serviceLabel("label-77")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void purgeAndReseed() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/service_usage_events/destructively_purge_all_and_reseed_existing_instances")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.serviceUsageEvents
            .purgeAndReseed(PurgeAndReseedServiceUsageEventsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}

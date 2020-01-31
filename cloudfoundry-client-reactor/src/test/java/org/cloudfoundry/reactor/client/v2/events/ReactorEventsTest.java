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

package org.cloudfoundry.reactor.client.v2.events;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.events.EventEntity;
import org.cloudfoundry.client.v2.events.EventResource;
import org.cloudfoundry.client.v2.events.GetEventRequest;
import org.cloudfoundry.client.v2.events.GetEventResponse;
import org.cloudfoundry.client.v2.events.ListEventsRequest;
import org.cloudfoundry.client.v2.events.ListEventsResponse;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorEventsTest extends AbstractClientApiTest {

    private final ReactorEvents events = new ReactorEvents(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/events/test-event-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/events/GET_{id}_response.json")
                .build())
            .build());

        this.events
            .get(GetEventRequest.builder()
                .eventId("test-event-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetEventResponse.builder()
                .metadata(Metadata.builder()
                    .id("8f1366e5-1fe2-418c-ae33-38bf29ad857a")
                    .url("/v2/events/8f1366e5-1fe2-418c-ae33-38bf29ad857a")
                    .createdAt("2015-07-27T22:43:23Z")
                    .build())
                .entity(EventEntity.builder()
                    .type("name-1010")
                    .actor("guid-a01d98f8-ba9a-40b0-86ba-3deacc2978c2")
                    .actorType("name-1011")
                    .actorName("name-1012")
                    .actee("guid-ff2c9780-b8db-4276-ba5f-b06adb724873")
                    .acteeType("name-1013")
                    .acteeName("name-1014")
                    .timestamp("2015-07-27T22:43:23Z")
                    .metadatas(Collections.emptyMap())
                    .spaceId("dcb0dcdd-5236-4af3-abc2-1ab0ff424794")
                    .organizationId("3317f885-4670-4249-9861-7c75d851d492")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/events?q=actee%3Atest-actee&page=-1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/events/GET_response.json")
                .build())
            .build());

        this.events
            .list(ListEventsRequest.builder()
                .actee("test-actee")
                .page(-1)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListEventsResponse.builder()
                .totalResults(3)
                .totalPages(1)
                .resource(EventResource.builder()
                    .metadata(Metadata.builder()
                        .id("2cc565c7-18e7-4fff-8fb0-52525f09ee6b")
                        .url("/v2/events/2cc565c7-18e7-4fff-8fb0-52525f09ee6b")
                        .createdAt("2015-07-27T22:43:23Z")
                        .build())
                    .entity(EventEntity.builder()
                        .type("name-1034")
                        .actor("guid-ddc7f725-c67f-4e68-8118-1ae1687f9fff")
                        .actorType("name-1035")
                        .actorName("name-1036")
                        .actee("guid-16ac41e9-c30c-45e1-b51c-226fb37e4197")
                        .acteeType("name-1037")
                        .acteeName("name-1038")
                        .timestamp("2015-07-27T22:43:23Z")
                        .metadatas(Collections.emptyMap())
                        .spaceId("1a769af6-8ddb-4508-a35a-cc61c51fdcdf")
                        .organizationId("49723c2a-a11e-43f8-971a-b34e9134ce00")
                        .build())
                    .build())
                .resource(EventResource.builder()
                    .metadata(Metadata.builder()
                        .id("a82493b7-bd16-421b-aef0-d0b5c40869e8")
                        .url("/v2/events/a82493b7-bd16-421b-aef0-d0b5c40869e8")
                        .createdAt("2015-07-27T22:43:23Z")
                        .build())
                    .entity(EventEntity.builder()
                        .type("name-1042")
                        .actor("guid-e68c8d10-dc83-4466-8735-9c4201166af9")
                        .actorType("name-1043")
                        .actorName("name-1044")
                        .actee("guid-d3ecb6be-c8a0-4e3b-9838-b78c58a88b65")
                        .acteeType("name-1045")
                        .acteeName("name-1046")
                        .timestamp("2015-07-27T22:43:23Z")
                        .metadatas(Collections.emptyMap())
                        .spaceId("dbe6bbdc-0d9c-495c-abbb-0b5eb93c8494")
                        .organizationId("52c7fb45-e31b-4271-9f16-8c94df30d8c7")
                        .build())
                    .build())
                .resource(EventResource.builder()
                    .metadata(Metadata.builder()
                        .id("4a0e6a34-2807-44cd-a5cc-b61890662ade")
                        .url("/v2/events/4a0e6a34-2807-44cd-a5cc-b61890662ade")
                        .createdAt("2015-07-27T22:43:23Z")
                        .build())
                    .entity(EventEntity.builder()
                        .type("name-1050")
                        .actor("guid-69e5e7e7-7723-4af8-a7cb-255d9a90c8db")
                        .actorType("name-1051")
                        .actorName("name-1052")
                        .actee("guid-cc1f17ce-85ab-4cc2-988b-9fca0f3a1d03")
                        .acteeType("name-1053")
                        .acteeName("name-1054")
                        .timestamp("2015-07-27T22:43:23Z")
                        .metadatas(Collections.emptyMap())
                        .spaceId("38a2f075-fe19-4edc-8787-5571f2af7051")
                        .organizationId("9160433e-860d-4251-bd6d-140187a2c5db")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}

/*
 * Copyright 2013-2019 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.auditevents;

import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.auditevents.AuditEventReference;
import org.cloudfoundry.client.v3.auditevents.AuditEventRelationship;
import org.cloudfoundry.client.v3.auditevents.AuditEventResource;
import org.cloudfoundry.client.v3.auditevents.GetAuditEventRequest;
import org.cloudfoundry.client.v3.auditevents.GetAuditEventResponse;
import org.cloudfoundry.client.v3.auditevents.ListAuditEventsRequest;
import org.cloudfoundry.client.v3.auditevents.ListAuditEventsResponse;
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

public class ReactorAuditEventsV3Test extends AbstractClientApiTest {

    private ReactorAuditEventsV3 events = new ReactorAuditEventsV3(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/audit_events/a595fe2f-01ff-4965-a50c-290258ab8582")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/auditevents/GET_{id}_response.json")
                .build())
            .build());

        this.events
            .get(GetAuditEventRequest.builder()
                .eventId("a595fe2f-01ff-4965-a50c-290258ab8582")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetAuditEventResponse.builder()
                .id("a595fe2f-01ff-4965-a50c-290258ab8582")
                .createdAt("2016-11-04T16:41:23Z")
                .updatedAt("2016-11-04T16:41:23Z")
                .type("audit.app.update")
                .auditEventActor(AuditEventReference.builder()
                    .id("test-user-id")
                    .name("admin")
                    .type("user")
                    .build())
                .auditEventTarget(AuditEventReference.builder()
                    .id("test-app-id")
                    .name("test-app")
                    .type("app")
                    .build())
                .spaceRelationship(AuditEventRelationship.builder()
                    .id("test-space-id")
                    .build())
                .organizationRelationship(AuditEventRelationship.builder()
                    .id("test-organization-id")
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/audit_events/a595fe2f-01ff-4965-a50c-290258ab8582")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/audit_events")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/auditevents/GET_response.json")
                .build())
            .build());

        this.events
            .list(ListAuditEventsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListAuditEventsResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(3)
                    .totalPages(2)
                    .first(Link.builder()
                        .href("https://api.example.org/v3/audit_events?page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("https://api.example.org/v3/audit_events?page=2&per_page=2")
                        .build())
                    .next(Link.builder()
                        .href("https://api.example.org/v3/audit_events?page=2&per_page=2")
                        .build())
                    .build())
                .resource(AuditEventResource.builder()
                    .id("a595fe2f-01ff-4965-a50c-290258ab8582")
                    .createdAt("2016-11-04T16:41:23Z")
                    .updatedAt("2016-11-04T16:41:23Z")
                    .type("audit.app.update")
                    .auditEventActor(AuditEventReference.builder()
                        .id("test-user-id")
                        .name("admin")
                        .type("user")
                        .build())
                    .auditEventTarget(AuditEventReference.builder()
                        .id("test-app-id")
                        .name("test-app")
                        .type("app")
                        .build())
                    .spaceRelationship(AuditEventRelationship.builder()
                        .id("test-space-id")
                        .build())
                    .organizationRelationship(AuditEventRelationship.builder()
                        .id("test-organization-id")
                        .build())
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/audit_events/a595fe2f-01ff-4965-a50c-290258ab8582")
                        .build())
                    .build())
                .resource(AuditEventResource.builder()
                    .id("8a8a14a3-beab-4946-ab6a-3e69741c6405")
                    .createdAt("2016-11-04T16:41:23Z")
                    .updatedAt("2016-11-04T16:41:23Z")
                    .type("audit.app.update")
                    .auditEventActor(AuditEventReference.builder()
                        .id("test-user-id")
                        .name("admin-admin")
                        .type("user")
                        .build())
                    .auditEventTarget(AuditEventReference.builder()
                        .id("test-app-id")
                        .name("test-app-2")
                        .type("app")
                        .build())
                    .spaceRelationship(AuditEventRelationship.builder()
                        .id("test-space-id-1")
                        .build())
                    .organizationRelationship(AuditEventRelationship.builder()
                        .id("test-organization-id-1")
                        .build())
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/audit_events/8a8a14a3-beab-4946-ab6a-3e69741c6405")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }
}

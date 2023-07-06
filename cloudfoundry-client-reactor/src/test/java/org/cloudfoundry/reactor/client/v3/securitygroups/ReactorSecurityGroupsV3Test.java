/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.reactor.client.v3.securitygroups;

import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.client.v3.securitygroups.Relationships;
import org.cloudfoundry.client.v3.securitygroups.CreateSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.CreateSecurityGroupResponse;
import org.cloudfoundry.client.v3.securitygroups.GloballyEnabled;
import org.cloudfoundry.client.v3.securitygroups.Protocol;
import org.cloudfoundry.client.v3.securitygroups.Rule;
import org.cloudfoundry.client.v3.securitygroups.CreateSecurityGroupRequest;
import org.cloudfoundry.client.v3.ToManyRelationship;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.junit.Test;
import reactor.test.StepVerifier;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Relationship;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;

public final class ReactorSecurityGroupsV3Test extends AbstractClientApiTest {

        private final ReactorSecurityGroupsV3 securityGroups = new ReactorSecurityGroupsV3(CONNECTION_CONTEXT,
                        this.root,
                        TOKEN_PROVIDER, Collections.emptyMap());

        @Test
        public void create() {
                mockRequest(InteractionContext.builder()
                                .request(TestRequest.builder()
                                                .method(POST).path("/security_groups")
                                                .payload("fixtures/client/v3/security_groups/POST_request.json")
                                                .build())
                                .response(TestResponse.builder()
                                                .status(CREATED)
                                                .payload("fixtures/client/v3/security_groups/POST_response.json")
                                                .build())
                                .build());
                this.securityGroups
                                .create(CreateSecurityGroupRequest.builder()

                                                .rules(Rule.builder()
                                                                .protocol(Protocol.TCP)
                                                                .destination("10.10.10.0/24")
                                                                .ports("443,80,8080")
                                                                .build())
                                                .name("my-group0")
                                                .rules(Rule.builder()
                                                                .protocol(Protocol.ICMP)
                                                                .destination("10.10.10.0/24")
                                                                .description("Allow ping requests to private services")
                                                                .type(8)
                                                                .code(0)
                                                                .build())
                                                .build())
                                .as(StepVerifier::create)
                                .expectNext(CreateSecurityGroupResponse.builder()
                                                .name("my-group0")
                                                .id("b85a788e-671f-4549-814d-e34cdb2f539a")
                                                .createdAt("2020-02-20T17:42:08Z")
                                                .updatedAt("2020-02-20T17:42:08Z")
                                                .globallyEnabled(GloballyEnabled.builder()
                                                                .staging(false)
                                                                .running(true)
                                                                .build())
                                                .rules(Rule.builder()
                                                                .protocol(Protocol.TCP)
                                                                .destination("10.10.10.0/24")
                                                                .ports("443,80,8080")
                                                                .build())
                                                .rules(Rule.builder()
                                                                .protocol(Protocol.ICMP)
                                                                .destination("10.10.10.0/24")
                                                                .description("Allow ping requests to private services")
                                                                .type(8)
                                                                .code(0)
                                                                .build())
                                                .relationships(Relationships.builder()
                                                                .stagingSpaces(ToManyRelationship.builder()
                                                                                .data(Relationship.builder()
                                                                                                .id("space-guid-1")
                                                                                                .build())
                                                                                .data(Relationship.builder()
                                                                                                .id("space-guid-2")
                                                                                                .build())
                                                                                .build())
                                                                .runningSpaces(ToManyRelationship.builder().build())
                                                                .build())
                                                .link("self", Link.builder()
                                                                .href("https://api.example.org/v3/security_groups/b85a788e-671f-4549-814d-e34cdb2f539a")
                                                                .build())
                                                .build())
                                .expectComplete()
                                .verify(Duration.ofSeconds(5));

        }

}
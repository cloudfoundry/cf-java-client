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

package org.cloudfoundry.reactor.uaa.users;

import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.uaa.AbstractUaaApiTest;
import org.cloudfoundry.uaa.users.Approval;
import org.cloudfoundry.uaa.users.Email;
import org.cloudfoundry.uaa.users.Group;
import org.cloudfoundry.uaa.users.ListUsersRequest;
import org.cloudfoundry.uaa.users.ListUsersResponse;
import org.cloudfoundry.uaa.users.Meta;
import org.cloudfoundry.uaa.users.Name;
import org.cloudfoundry.uaa.users.User;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.cloudfoundry.uaa.users.ApprovalStatus.APPROVED;
import static org.cloudfoundry.uaa.users.MembershipType.DIRECT;
import static org.cloudfoundry.uaa.users.SortOrder.ASCENDING;

public final class ReactorUsersTest {

    public static final class List extends AbstractUaaApiTest<ListUsersRequest, ListUsersResponse> {

        private final ReactorUsers users = new ReactorUsers(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path(
                        "/Users?count=50&filter=id%2Beq%2B%22a94534d5-de08-41eb-8712-a51314e6a484%22%2Bor%2Bemail%2Beq%2B%22Da63pG@test.org%22&sortBy=email&sortOrder=ascending&startIndex=1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/uaa/users/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListUsersResponse getResponse() {
            return ListUsersResponse.builder()
                .resource(User.builder()
                    .id("18f0aa45-941c-4fd0-a06a-97a72a1dcf99")
                    .externalId("test-user")
                    .meta(Meta.builder()
                        .version(0)
                        .created("2016-04-29T23:20:50.611Z")
                        .lastModified("2016-04-29T23:20:50.611Z")
                        .build())
                    .userName("TLeoOn@test.org")
                    .name(Name.builder()
                        .familyName("family name")
                        .givenName("given name")
                        .build())
                    .email(Email.builder()
                        .address("TLeoOn@test.org")
                        .primary(false)
                        .build())
                    .group(Group.builder()
                        .id("cbd2053f-d266-4eb0-893a-48743e45611f")
                        .display("openid")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .id("1c413464-607a-4505-bf8b-0f9feaf7133f")
                        .display("password.write")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .id("84d79cdc-e638-4c96-bd1c-71162d9b227f")
                        .display("oauth.approvals")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .id("277a4d72-dad0-448a-abde-21bdb0a29d70")
                        .display("user_attributes")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .id("4a69732b-8696-44a8-8c1c-a9dd9261fecd")
                        .display("scim.userids")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .id("e485d91d-250e-49b0-b9fe-1f937f67bb24")
                        .display("uaa.user")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .id("4d325561-28d5-43df-b1ce-0424bd163180")
                        .display("profile")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .id("52cba040-893a-40be-b043-b5bc2d08cc58")
                        .display("cloud_controller.read")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .id("a8a60f05-f78b-47b7-990b-4d53952eec12")
                        .display("scim.me")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .id("a365936f-585a-4ced-b33e-d70bdc25697d")
                        .display("cloud_controller_service_permissions.read")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .id("84ab90cb-d181-41b8-8585-25f193b21bae")
                        .display("roles")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .id("c3f8c8cb-9cbd-435e-bedb-02e10be66146")
                        .display("approvals.me")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .id("86477ebe-4792-414c-a289-d1072a0cfe12")
                        .display("cloud_controller.write")
                        .type(DIRECT)
                        .build())
                    .approval(Approval.builder()
                        .userId("18f0aa45-941c-4fd0-a06a-97a72a1dcf99")
                        .clientId("client id")
                        .scope("scim.read")
                        .status(APPROVED)
                        .lastUpdatedAt("2016-04-29T23:20:50.624Z")
                        .expiresAt("2016-04-29T23:21:00.624Z")
                        .build())
                    .active(true)
                    .verified(true)
                    .origin("uaa")
                    .zoneId("uaa")
                    .passwordLastModified("2016-04-29T23:20:50.000Z")
                    .schemas(Collections.singletonList("urn:scim:schemas:core:1.0"))
                    .build())
                .startIndex(1)
                .itemsPerPage(50)
                .totalResults(1)
                .build();
        }

        @Override
        protected ListUsersRequest getValidRequest() throws Exception {
            return ListUsersRequest.builder()
                .filter("id+eq+\"a94534d5-de08-41eb-8712-a51314e6a484\"+or+email+eq+\"Da63pG@test.org\"")
                .count(50)
                .startIndex(1)
                .sortBy("email")
                .sortOrder(ASCENDING)
                .build();
        }

        @Override
        protected Mono<ListUsersResponse> invoke(ListUsersRequest request) {
            return this.users.list(request);
        }

    }

}

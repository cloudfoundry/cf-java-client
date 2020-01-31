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

package org.cloudfoundry.reactor.uaa.users;

import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.uaa.AbstractUaaApiTest;
import org.cloudfoundry.uaa.users.Approval;
import org.cloudfoundry.uaa.users.ChangeUserPasswordRequest;
import org.cloudfoundry.uaa.users.ChangeUserPasswordResponse;
import org.cloudfoundry.uaa.users.CreateUserRequest;
import org.cloudfoundry.uaa.users.CreateUserResponse;
import org.cloudfoundry.uaa.users.DeleteUserRequest;
import org.cloudfoundry.uaa.users.DeleteUserResponse;
import org.cloudfoundry.uaa.users.Email;
import org.cloudfoundry.uaa.users.ExpirePasswordRequest;
import org.cloudfoundry.uaa.users.ExpirePasswordResponse;
import org.cloudfoundry.uaa.users.GetUserVerificationLinkRequest;
import org.cloudfoundry.uaa.users.GetUserVerificationLinkResponse;
import org.cloudfoundry.uaa.users.Group;
import org.cloudfoundry.uaa.users.Invite;
import org.cloudfoundry.uaa.users.InviteUsersRequest;
import org.cloudfoundry.uaa.users.InviteUsersResponse;
import org.cloudfoundry.uaa.users.ListUsersRequest;
import org.cloudfoundry.uaa.users.ListUsersResponse;
import org.cloudfoundry.uaa.users.LookupUserIdsRequest;
import org.cloudfoundry.uaa.users.LookupUserIdsResponse;
import org.cloudfoundry.uaa.users.Meta;
import org.cloudfoundry.uaa.users.Name;
import org.cloudfoundry.uaa.users.PhoneNumber;
import org.cloudfoundry.uaa.users.UpdateUserRequest;
import org.cloudfoundry.uaa.users.UpdateUserResponse;
import org.cloudfoundry.uaa.users.User;
import org.cloudfoundry.uaa.users.UserId;
import org.cloudfoundry.uaa.users.UserInfoRequest;
import org.cloudfoundry.uaa.users.UserInfoResponse;
import org.cloudfoundry.uaa.users.VerifyUserRequest;
import org.cloudfoundry.uaa.users.VerifyUserResponse;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.cloudfoundry.uaa.SortOrder.ASCENDING;
import static org.cloudfoundry.uaa.SortOrder.DESCENDING;
import static org.cloudfoundry.uaa.users.ApprovalStatus.APPROVED;
import static org.cloudfoundry.uaa.users.ApprovalStatus.DENIED;
import static org.cloudfoundry.uaa.users.MembershipType.DIRECT;

public final class ReactorUsersTest extends AbstractUaaApiTest {

    private final ReactorUsers users = new ReactorUsers(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER, Collections.emptyMap());

    @Test
    public void changePassword() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/Users/9140c37c-c5d9-4c4d-a265-b6fe2f9dd02d/password")
                .payload("fixtures/uaa/users/PUT_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/users/PUT_response.json")
                .build())
            .build());

        this.users
            .changePassword(ChangeUserPasswordRequest.builder()
                .oldPassword("secret")
                .password("newsecret")
                .userId("9140c37c-c5d9-4c4d-a265-b6fe2f9dd02d")
                .build())
            .as(StepVerifier::create)
            .expectNext(ChangeUserPasswordResponse.builder()
                .status("ok")
                .message("password updated")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/Users")
                .payload("fixtures/uaa/users/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/users/POST_response.json")
                .build())
            .build());

        this.users
            .create(CreateUserRequest.builder()
                .externalId("test-user")
                .userName("ZO6FEI@test.org")
                .name(Name.builder()
                    .familyName("family name")
                    .givenName("given name")
                    .build())
                .email(Email.builder()
                    .value("ZO6FEI@test.org")
                    .primary(true)
                    .build())
                .phoneNumber(PhoneNumber.builder()
                    .value("5555555555")
                    .build())
                .active(true)
                .verified(true)
                .origin("")
                .password("secret")
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateUserResponse.builder()
                .id("9d175c69-8f25-4460-82d7-be9657f87a68")
                .externalId("test-user")
                .meta(Meta.builder()
                    .version(0)
                    .created("2016-05-18T18:25:24.559Z")
                    .lastModified("2016-05-18T18:25:24.559Z")
                    .build())
                .userName("ZO6FEI@test.org")
                .name(Name.builder()
                    .familyName("family name")
                    .givenName("given name")
                    .build())
                .email(Email.builder()
                    .value("ZO6FEI@test.org")
                    .primary(false)
                    .build())
                .phoneNumber(PhoneNumber.builder()
                    .value("5555555555")
                    .build())
                .group(Group.builder()
                    .value("4622c5e1-ddfd-4e17-9e81-2ae3c03972be")
                    .display("password.write")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("62f67643-05d8-43c6-b193-4cd6ab9960cb")
                    .display("cloud_controller.write")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("c47bf470-f9c4-4eea-97e4-490ce7b8f6f7")
                    .display("uaa.user")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("8a6add1f-d3ee-400c-a263-c4197351b78e")
                    .display("approvals.me")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("e10424ed-ed80-45ac-848b-7f7e79b00c42")
                    .display("cloud_controller.read")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("ede11441-6ffe-4510-81f8-bb40626155f0")
                    .display("openid")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("7e3d4b06-0d6b-43a1-ac3a-5f1b2642262c")
                    .display("scim.me")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("3b481f3c-d9a7-4920-a687-72cb0381b671")
                    .display("cloud_controller_service_permissions.read")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("4480c647-4047-4c6a-877f-70f5f96e8c11")
                    .display("oauth.approvals")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("542bb178-1c04-4bb5-813a-5a038319ac1d")
                    .display("user_attributes")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("c4ac4653-2fdd-4901-a028-9c9866cb4e9c")
                    .display("scim.userids")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("74fde138-daf3-4e4d-bb52-93a6cb727030")
                    .display("profile")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("1b18551f-eead-4076-90dd-b464998f6ddd")
                    .display("roles")
                    .type(DIRECT)
                    .build())
                .active(true)
                .verified(true)
                .origin("uaa")
                .zoneId("uaa")
                .passwordLastModified("2016-05-18T18:25:24.000Z")
                .schemas(Collections.singletonList("urn:scim:schemas:core:1.0"))
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/Users/421225f4-318e-4a4d-9219-4b6a0ed3678a")
                .header("If-Match", "*")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/users/DELETE_response.json")
                .build())
            .build());

        this.users
            .delete(DeleteUserRequest.builder()
                .userId("421225f4-318e-4a4d-9219-4b6a0ed3678a")
                .version("*")
                .build())
            .as(StepVerifier::create)
            .expectNext(DeleteUserResponse.builder()
                .id("421225f4-318e-4a4d-9219-4b6a0ed3678a")
                .externalId("test-user")
                .meta(Meta.builder()
                    .version(0)
                    .created("2016-05-18T18:25:23.102Z")
                    .lastModified("2016-05-18T18:25:23.102Z")
                    .build())
                .userName("7Q4Rqr@test.org")
                .name(Name.builder()
                    .familyName("family name")
                    .givenName("given name")
                    .build())
                .email(Email.builder()
                    .value("7Q4Rqr@test.org")
                    .primary(false)
                    .build())
                .group(Group.builder()
                    .value("4622c5e1-ddfd-4e17-9e81-2ae3c03972be")
                    .display("password.write")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("62f67643-05d8-43c6-b193-4cd6ab9960cb")
                    .display("cloud_controller.write")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("c47bf470-f9c4-4eea-97e4-490ce7b8f6f7")
                    .display("uaa.user")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("8a6add1f-d3ee-400c-a263-c4197351b78e")
                    .display("approvals.me")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("e10424ed-ed80-45ac-848b-7f7e79b00c42")
                    .display("cloud_controller.read")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("ede11441-6ffe-4510-81f8-bb40626155f0")
                    .display("openid")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("7e3d4b06-0d6b-43a1-ac3a-5f1b2642262c")
                    .display("scim.me")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("3b481f3c-d9a7-4920-a687-72cb0381b671")
                    .display("cloud_controller_service_permissions.read")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("4480c647-4047-4c6a-877f-70f5f96e8c11")
                    .display("oauth.approvals")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("542bb178-1c04-4bb5-813a-5a038319ac1d")
                    .display("user_attributes")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("c4ac4653-2fdd-4901-a028-9c9866cb4e9c")
                    .display("scim.userids")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("74fde138-daf3-4e4d-bb52-93a6cb727030")
                    .display("profile")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .value("1b18551f-eead-4076-90dd-b464998f6ddd")
                    .display("roles")
                    .type(DIRECT)
                    .build())
                .approval(Approval.builder()
                    .userId("421225f4-318e-4a4d-9219-4b6a0ed3678a")
                    .clientId("identity")
                    .scope("uaa.user")
                    .status(APPROVED)
                    .lastUpdatedAt("2016-05-18T18:25:53.114Z")
                    .expiresAt("2016-05-18T18:25:53.114Z")
                    .build())
                .approval(Approval.builder()
                    .userId("421225f4-318e-4a4d-9219-4b6a0ed3678a")
                    .clientId("client id")
                    .scope("scim.read")
                    .status(APPROVED)
                    .lastUpdatedAt("2016-05-18T18:25:23.112Z")
                    .expiresAt("2016-05-18T18:25:33.112Z")
                    .build())
                .active(true)
                .verified(true)
                .origin("uaa")
                .zoneId("uaa")
                .passwordLastModified("2016-05-18T18:25:23.000Z")
                .schemas(Collections.singletonList("urn:scim:schemas:core:1.0"))
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void expirePassword() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PATCH).path("/Users/9022f2cf-2663-479e-82e6-d2ccc348a1e4/status")
                .payload("fixtures/uaa/users/PATCH_{id}_status_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/users/PATCH_{id}_status_response.json")
                .build())
            .build());

        this.users
            .expirePassword(ExpirePasswordRequest.builder()
                .passwordChangeRequired(true)
                .userId("9022f2cf-2663-479e-82e6-d2ccc348a1e4")
                .build())
            .as(StepVerifier::create)
            .expectNext(ExpirePasswordResponse.builder()
                .passwordChangeRequired(true)
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getVerificationLink() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/Users/1faa46a0-0c6f-4e13-8334-d1f6e5f2e1dd/verify-link?redirect_uri=http%3A%2F%2Fredirect.to%2Fapp")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/users/GET_{id}_verify_link_response.json")
                .build())
            .build());

        this.users
            .getVerificationLink(GetUserVerificationLinkRequest.builder()
                .redirectUri("http://redirect.to/app")
                .userId("1faa46a0-0c6f-4e13-8334-d1f6e5f2e1dd")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetUserVerificationLinkResponse.builder()
                .verifyLink("http://localhost/verify_user?code=nOGQWBqCx5")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void inviteUsers() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/invite_users?client_id=u7ptqw&redirect_uri=example.com")
                .payload("fixtures/uaa/users/POST_invite_users_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/users//POST_invite_users_response.json")
                .build())
            .build());

        this.users
            .invite(InviteUsersRequest.builder()
                .clientId("u7ptqw")
                .emails("user1@pjy596.com", "user2@pjy596.com")
                .redirectUri("example.com")
                .build())
            .as(StepVerifier::create)
            .expectNext(InviteUsersResponse.builder()
                .newInvite(Invite.builder()
                    .email("user1@pjy596.com")
                    .userId("68af461b-484e-464a-96ac-a336abed48ad")
                    .origin("uaa")
                    .success(true)
                    .inviteLink("http://localhost/invitations/accept?code=WEqtpOh73k")
                    .build())
                .newInvite(Invite.builder()
                    .email("user2@pjy596.com")
                    .userId("d256cf96-5c14-4649-9a0d-5564c66411b5")
                    .origin("uaa")
                    .success(true)
                    .inviteLink("http://localhost/invitations/accept?code=n5X0hCsD3N")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path(
                    "/Users?count=50&filter=id%2Beq%2B%22a94534d5-de08-41eb-8712-a51314e6a484%22%2Bor%2Bemail%2Beq%2B%22Da63pG%40test.org%22&sortBy=email&sortOrder=ascending&startIndex=1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/users/GET_response.json")
                .build())
            .build());

        this.users
            .list(ListUsersRequest.builder()
                .filter("id+eq+\"a94534d5-de08-41eb-8712-a51314e6a484\"+or+email+eq+\"Da63pG@test.org\"")
                .count(50)
                .startIndex(1)
                .sortBy("email")
                .sortOrder(ASCENDING)
                .build())
            .as(StepVerifier::create)
            .expectNext(ListUsersResponse.builder()
                .resource(User.builder()
                    .id("a94534d5-de08-41eb-8712-a51314e6a484")
                    .externalId("test-user")
                    .meta(Meta.builder()
                        .version(0)
                        .created("2016-05-18T18:25:24.036Z")
                        .lastModified("2016-05-18T18:25:24.036Z")
                        .build())
                    .userName("Da63pG@test.org")
                    .name(Name.builder()
                        .familyName("family name")
                        .givenName("given name")
                        .build())
                    .email(Email.builder()
                        .value("Da63pG@test.org")
                        .primary(false)
                        .build())
                    .group(Group.builder()
                        .value("4622c5e1-ddfd-4e17-9e81-2ae3c03972be")
                        .display("password.write")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .value("62f67643-05d8-43c6-b193-4cd6ab9960cb")
                        .display("cloud_controller.write")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .value("c47bf470-f9c4-4eea-97e4-490ce7b8f6f7")
                        .display("uaa.user")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .value("8a6add1f-d3ee-400c-a263-c4197351b78e")
                        .display("approvals.me")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .value("e10424ed-ed80-45ac-848b-7f7e79b00c42")
                        .display("cloud_controller.read")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .value("ede11441-6ffe-4510-81f8-bb40626155f0")
                        .display("openid")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .value("7e3d4b06-0d6b-43a1-ac3a-5f1b2642262c")
                        .display("scim.me")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .value("3b481f3c-d9a7-4920-a687-72cb0381b671")
                        .display("cloud_controller_service_permissions.read")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .value("4480c647-4047-4c6a-877f-70f5f96e8c11")
                        .display("oauth.approvals")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .value("542bb178-1c04-4bb5-813a-5a038319ac1d")
                        .display("user_attributes")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .value("c4ac4653-2fdd-4901-a028-9c9866cb4e9c")
                        .display("scim.userids")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .value("74fde138-daf3-4e4d-bb52-93a6cb727030")
                        .display("profile")
                        .type(DIRECT)
                        .build())
                    .group(Group.builder()
                        .value("1b18551f-eead-4076-90dd-b464998f6ddd")
                        .display("roles")
                        .type(DIRECT)
                        .build())
                    .approval(Approval.builder()
                        .userId("a94534d5-de08-41eb-8712-a51314e6a484")
                        .clientId("client id")
                        .scope("scim.read")
                        .status(APPROVED)
                        .lastUpdatedAt("2016-05-18T18:25:24.047Z")
                        .expiresAt("2016-05-18T18:25:34.047Z")
                        .build())
                    .active(true)
                    .verified(true)
                    .origin("uaa")
                    .zoneId("uaa")
                    .passwordLastModified("2016-05-18T18:25:24.000Z")
                    .schema("urn:scim:schemas:core:1.0")
                    .build())
                .startIndex(1)
                .itemsPerPage(50)
                .totalResults(1)
                .schema("urn:scim:schemas:core:1.0")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void lookup() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path(
                    "/ids/Users?count=10&filter=userName%2Beq%2B%22bobOu38vE%40test.org%22%2Bor%2Bid%2Beq%2B%22c1476587-5ec9-4b7e-9ed2-381e3133f07a%22" +
                        "&includeInactive=true&sortOrder=descending&startIndex=1")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/users/GET_ids_response.json")
                .build())
            .build());

        this.users
            .lookup(LookupUserIdsRequest.builder()
                .filter("userName+eq+\"bobOu38vE@test.org\"+or+id+eq+\"c1476587-5ec9-4b7e-9ed2-381e3133f07a\"")
                .count(10)
                .startIndex(1)
                .sortOrder(DESCENDING)
                .includeInactive(true)
                .build())
            .as(StepVerifier::create)
            .expectNext(LookupUserIdsResponse.builder()
                .resource(UserId.builder()
                    .id("c1476587-5ec9-4b7e-9ed2-381e3133f07a")
                    .userName("dwayneSnbjBm@test.org")
                    .origin("uaa")
                    .build())
                .resource(UserId.builder()
                    .id("2fc67623-ee31-4edc-9b1f-0b50416195fb")
                    .userName("bobOu38vE@test.org")
                    .origin("uaa")
                    .build())
                .startIndex(1)
                .itemsPerPage(10)
                .totalResults(2)
                .schema("urn:scim:schemas:core:1.0")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/Users/test-user-id")
                .header("If-Match", "*")
                .payload("fixtures/uaa/users/PUT_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/users/PUT_{id}_response.json")
                .build())
            .build());

        this.users
            .update(UpdateUserRequest.builder()
                .active(true)
                .email(Email.builder()
                    .primary(false)
                    .value("oH4jON@test.org")
                    .build())
                .phoneNumber(PhoneNumber.builder()
                    .value("5555555555")
                    .build())
                .externalId("test-user")
                .id(("test-user-id"))
                .version("*")
                .name(Name.builder()
                    .familyName("family name")
                    .givenName("given name")
                    .build())
                .origin("uaa")
                .userName("oH4jON@test.org")
                .verified(true)
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateUserResponse.builder()
                .active(true)
                .approval(Approval.builder()
                    .clientId("identity")
                    .expiresAt("2016-05-18T18:25:54.239Z")
                    .lastUpdatedAt("2016-05-18T18:25:54.239Z")
                    .scope("uaa.user")
                    .status(DENIED)
                    .userId("test-user-id")
                    .build())
                .approval(Approval.builder()
                    .clientId("client id")
                    .expiresAt("2016-05-18T18:25:34.236Z")
                    .lastUpdatedAt("2016-05-18T18:25:34.236Z")
                    .scope("scim.read")
                    .status(APPROVED)
                    .userId("test-user-id")
                    .build())
                .email(Email.builder()
                    .primary(false)
                    .value("oH4jON@test.org")
                    .build())
                .phoneNumber(PhoneNumber.builder()
                    .value("5555555555")
                    .build())
                .externalId("test-user")
                .group(Group.builder()
                    .display("password.write")
                    .value("4622c5e1-ddfd-4e17-9e81-2ae3c03972be")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .display("cloud_controller.write")
                    .value("62f67643-05d8-43c6-b193-4cd6ab9960cb")
                    .type(DIRECT)
                    .build())
                .group(Group.builder()
                    .display("uaa.user")
                    .value("c47bf470-f9c4-4eea-97e4-490ce7b8f6f7")
                    .type(DIRECT)
                    .build())
                .id(("test-user-id"))
                .meta(Meta.builder()
                    .created("2016-05-18T18:25:24.222Z")
                    .lastModified("2016-05-18T18:25:24.265Z")
                    .version(1)
                    .build())
                .name(Name.builder()
                    .familyName("family name")
                    .givenName("given name")
                    .build())
                .origin("uaa")
                .passwordLastModified("2016-05-18T18:25:24.000Z")
                .schema("urn:scim:schemas:core:1.0")
                .userName("oH4jON@test.org")
                .verified(true)
                .zoneId("uaa")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void userInfo() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/userinfo")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/users/GET_userinfo_response.json")
                .build())
            .build());

        this.users
            .userInfo(UserInfoRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(UserInfoResponse.builder()
                .email("anO0Lv@test.org")
                .emailVerified(true)
                .familyName("PasswordResetUserLast")
                .givenName("PasswordResetUserFirst")
                .name("PasswordResetUserFirst PasswordResetUserLast")
                .phoneNumber("+15558880000")
                .previousLogonTime(null)
                .sub("ab485a4f-168a-4de8-b3ac-ab501767bfc9")
                .userId("ab485a4f-168a-4de8-b3ac-ab501767bfc9")
                .userName("anO0Lv@test.org")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void verifyUser() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/Users/c0d42e48-9b69-461d-a77b-f75d3a5948b6/verify")
                .header("If-Match", "12")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/uaa/users/GET_{id}_verify_user_response.json")
                .build())
            .build());

        this.users
            .verify(VerifyUserRequest.builder()
                .userId("c0d42e48-9b69-461d-a77b-f75d3a5948b6")
                .version("12")
                .build())
            .as(StepVerifier::create)
            .expectNext(VerifyUserResponse.builder()
                .id("c0d42e48-9b69-461d-a77b-f75d3a5948b6")
                .meta(Meta.builder()
                    .version(12)
                    .created("2016-06-03T17:59:31.027Z")
                    .lastModified("2016-06-03T17:59:31.027Z")
                    .build())
                .userName("billy_o@example.com")
                .name(Name.builder()
                    .familyName("d'Orange")
                    .givenName("William")
                    .build())
                .email(Email.builder()
                    .value("billy_o@example.com")
                    .primary(false)
                    .build())
                .active(true)
                .verified(true)
                .origin("uaa")
                .zoneId("uaa")
                .passwordLastModified("2016-06-03T17:59:31.000Z")
                .schema("urn:scim:schemas:core:1.0")
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}

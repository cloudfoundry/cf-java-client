/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.uaa;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.uaa.groups.AddMemberRequest;
import org.cloudfoundry.uaa.groups.CheckMembershipRequest;
import org.cloudfoundry.uaa.groups.CreateGroupRequest;
import org.cloudfoundry.uaa.groups.CreateGroupResponse;
import org.cloudfoundry.uaa.groups.DeleteGroupRequest;
import org.cloudfoundry.uaa.groups.DeleteGroupResponse;
import org.cloudfoundry.uaa.groups.ExternalGroupResource;
import org.cloudfoundry.uaa.groups.GetGroupRequest;
import org.cloudfoundry.uaa.groups.GetGroupResponse;
import org.cloudfoundry.uaa.groups.Group;
import org.cloudfoundry.uaa.groups.ListExternalGroupMappingsRequest;
import org.cloudfoundry.uaa.groups.ListGroupsRequest;
import org.cloudfoundry.uaa.groups.ListMembersRequest;
import org.cloudfoundry.uaa.groups.ListMembersResponse;
import org.cloudfoundry.uaa.groups.MapExternalGroupRequest;
import org.cloudfoundry.uaa.groups.MapExternalGroupResponse;
import org.cloudfoundry.uaa.groups.Member;
import org.cloudfoundry.uaa.groups.MemberSummary;
import org.cloudfoundry.uaa.groups.MemberType;
import org.cloudfoundry.uaa.groups.RemoveMemberRequest;
import org.cloudfoundry.uaa.groups.RemoveMemberResponse;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupDisplayNameRequest;
import org.cloudfoundry.uaa.groups.UnmapExternalGroupByGroupIdRequest;
import org.cloudfoundry.uaa.groups.UpdateGroupRequest;
import org.cloudfoundry.uaa.groups.UserEntity;
import org.cloudfoundry.uaa.users.CreateUserRequest;
import org.cloudfoundry.uaa.users.CreateUserResponse;
import org.cloudfoundry.uaa.users.Email;
import org.cloudfoundry.uaa.users.Name;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudfoundry.util.tuple.TupleUtils.consumer;
import static org.cloudfoundry.util.tuple.TupleUtils.function;


public final class GroupsTest extends AbstractIntegrationTest {

    @Autowired
    private UaaClient uaaClient;

    @Test
    public void addMemberGroup() throws TimeoutException, InterruptedException {
        String baseDisplayName = this.nameFactory.getGroupName();
        String memberDisplayName = this.nameFactory.getGroupName();

        Mono
            .when(
                createGroupId(this.uaaClient, baseDisplayName),
                createGroupId(this.uaaClient, memberDisplayName)
            )
            .flatMap(function((baseGroupId, memberGroupId) -> Mono.when(
                this.uaaClient.groups()
                    .addMember(AddMemberRequest.builder()
                        .groupId(baseGroupId)
                        .origin(memberDisplayName + "-origin")
                        .type(MemberType.GROUP)
                        .memberId(memberGroupId)
                        .build()),
                Mono.just(memberGroupId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(consumer((response, memberGroupId) -> {
                assertThat(response.getMemberId()).isEqualTo(memberGroupId);
                assertThat(response.getOrigin()).isEqualTo(Optional.of(String.format("%s-origin", memberDisplayName)));
                assertThat(response.getType()).isEqualTo(Optional.of(MemberType.GROUP));
            }))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void addMemberUser() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();
        String userName = this.nameFactory.getUserName();

        Mono.
            when(
                createGroupId(this.uaaClient, displayName),
                createUserId(this.uaaClient, userName)
            )
            .flatMap(function((groupId, userId) -> Mono.when(
                this.uaaClient.groups()
                    .addMember(AddMemberRequest.builder()
                        .groupId(groupId)
                        .origin(userName + "-origin")
                        .type(MemberType.USER)
                        .memberId(userId)
                        .build()),
                Mono.just(userId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(consumer((response, userId) -> {
                assertThat(response.getMemberId()).isEqualTo(userId);
                assertThat(response.getOrigin()).isEqualTo(Optional.of(String.format("%s-origin", userName)));
                assertThat(response.getType()).isEqualTo(Optional.of(MemberType.USER));
            }))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void checkMembership() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();
        String userName = this.nameFactory.getUserName();

        createUserId(this.uaaClient, userName)
            .flatMap(userId -> Mono.when(
                createGroupIdWithMember(this.uaaClient, displayName, userId),
                Mono.just(userId)
            ))
            .flatMap(function((groupId, userId) -> Mono.when(
                this.uaaClient.groups()
                    .checkMembership(CheckMembershipRequest.builder()
                        .groupId(groupId)
                        .memberId(userId)
                        .build()),
                Mono.just(userId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(consumer((response, userId) -> {
                assertThat(response.getMemberId()).isEqualTo(userId);
                assertThat(response.getOrigin()).isEqualTo(Optional.of("test-origin"));
                assertThat(response.getType()).isEqualTo(Optional.of(MemberType.USER));
            }))
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();
        String userName = this.nameFactory.getUserName();

        createUserId(this.uaaClient, userName)
            .flatMap(userId -> this.uaaClient.groups()
                .create(CreateGroupRequest.builder()
                    .displayName(displayName)
                    .member(MemberSummary.builder()
                        .memberId(userId)
                        .build())
                    .build()))
            .then(requestList(this.uaaClient)
                .filter(resource -> displayName.equals(resource.getDisplayName()))
                .single())
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();

        createGroupId(this.uaaClient, displayName)
            .flatMap(groupId -> this.uaaClient.groups()
                .delete(DeleteGroupRequest.builder()
                    .groupId(groupId)
                    .build()))
            .map(DeleteGroupResponse::getDisplayName)
            .as(StepVerifier::create)
            .expectNext(displayName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();

        createGroupId(this.uaaClient, displayName)
            .flatMap(groupId -> this.uaaClient.groups()
                .get(GetGroupRequest.builder()
                    .groupId(groupId)
                    .build())
                .map(GetGroupResponse::getDisplayName))
            .as(StepVerifier::create)
            .expectNext(displayName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();

        createGroupId(this.uaaClient, displayName)
            .flatMap(groupId -> PaginationUtils
                .requestUaaResources(startIndex -> this.uaaClient.groups()
                    .list(ListGroupsRequest.builder()
                        .filter(String.format("id eq \"%s\"", groupId))
                        .startIndex(startIndex)
                        .build()))
                .single())
            .map(Group::getDisplayName)
            .as(StepVerifier::create)
            .expectNext(displayName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listExternalGroupMappings() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();

        createGroupId(this.uaaClient, displayName)
            .flatMap(groupId -> requestMapExternalGroupResponse(this.uaaClient, displayName, groupId))
            .thenMany(PaginationUtils
                .requestUaaResources(startIndex -> this.uaaClient.groups()
                    .listExternalGroupMappings(ListExternalGroupMappingsRequest.builder()
                        .startIndex(startIndex)
                        .build()))
                .filter(group -> displayName.equals(group.getDisplayName())))
            .map(ExternalGroupResource::getExternalGroup)
            .as(StepVerifier::create)
            .expectNext(displayName + "-external-group")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listMembers() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();
        String userName = this.nameFactory.getUserName();

        createUserId(this.uaaClient, userName)
            .flatMap(userId -> Mono.when(
                createGroupIdWithMember(this.uaaClient, displayName, userId),
                Mono.just(userId)
            ))
            .flatMap(function((groupId, userId) -> Mono.when(
                this.uaaClient.groups()
                    .listMembers(ListMembersRequest.builder()
                        .groupId(groupId)
                        .returnEntities(false)
                        .build())
                    .flatMapIterable(ListMembersResponse::getMembers)
                    .map(Member::getMemberId)
                    .single(),
                Mono.just(userId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listMembersWithEntity() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();
        String userName = this.nameFactory.getUserName();

        createUserId(this.uaaClient, userName)
            .flatMap(userId -> createGroupIdWithMember(this.uaaClient, displayName, userId))
            .flatMapMany(groupId -> this.uaaClient.groups()
                .listMembers(ListMembersRequest.builder()
                    .groupId(groupId)
                    .returnEntities(true)
                    .build()))
            .flatMapIterable(ListMembersResponse::getMembers)
            .map(Member::getEntity)
            .map(Optional::get)
            .cast(UserEntity.class)
            .single()
            .map(UserEntity::getUserName)
            .as(StepVerifier::create)
            .expectNext(userName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void mapExternalGroupMappings() {
        String displayName = this.nameFactory.getGroupName();

        createGroupId(this.uaaClient, displayName)
            .flatMap(groupId -> this.uaaClient.groups()
                .mapExternalGroup(MapExternalGroupRequest.builder()
                    .externalGroup(displayName + "-external-group")
                    .groupId(groupId)
                    .origin(displayName + "-origin")
                    .build()))
            .thenMany(requestListExternalGroupResource(this.uaaClient)
                .filter(group -> displayName.equals(group.getDisplayName())))
            .as(StepVerifier::create)
            .consumeNextWith(resource -> {
                assertThat(resource.getExternalGroup()).isEqualTo(String.format("%s-external-group", displayName));
                assertThat(resource.getOrigin()).isEqualTo(String.format("%s-origin", displayName));
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void removeMember() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();
        String userName = this.nameFactory.getUserName();

        createUserId(this.uaaClient, userName)
            .flatMap(userId -> Mono.when(
                createGroupIdWithMember(this.uaaClient, displayName, userId),
                Mono.just(userId)
            ))
            .flatMap(function((groupId, userId) -> Mono.when(
                this.uaaClient.groups()
                    .removeMember(RemoveMemberRequest.builder()
                        .groupId(groupId)
                        .memberId(userId)
                        .build())
                    .map(RemoveMemberResponse::getMemberId),
                Mono.just(userId)
            )))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void unmapExternalGroupMappingsByGroupDisplayName() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();

        createGroupId(this.uaaClient, displayName)
            .flatMap(groupId -> requestMapExternalGroupResponse(this.uaaClient, displayName, groupId))
            .then(this.uaaClient.groups()
                .unmapExternalGroupByGroupDisplayName(UnmapExternalGroupByGroupDisplayNameRequest.builder()
                    .groupDisplayName(displayName)
                    .externalGroup(displayName + "-external-group")
                    .origin(displayName + "-origin")
                    .build()))
            .thenMany(requestListExternalGroupResources(this.uaaClient))
            .filter(resource -> displayName.equals(resource.getDisplayName()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void unmapExternalGroupMappingsByGroupId() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();

        createGroupId(this.uaaClient, displayName)
            .delayUntil(groupId -> requestMapExternalGroupResponse(this.uaaClient, displayName, groupId))
            .flatMap(groupId -> this.uaaClient.groups()
                .unmapExternalGroupByGroupId(UnmapExternalGroupByGroupIdRequest.builder()
                    .groupId(groupId)
                    .externalGroup(displayName + "-external-group")
                    .origin(displayName + "-origin")
                    .build()))
            .thenMany(requestListExternalGroupResources(this.uaaClient)
                .filter(resource -> displayName.equals(resource.getDisplayName())))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() throws TimeoutException, InterruptedException {
        String baseDisplayName = this.nameFactory.getGroupName();
        String newDisplayName = this.nameFactory.getGroupName();

        createGroupId(this.uaaClient, baseDisplayName)
            .flatMap(groupId -> this.uaaClient.groups()
                .update(UpdateGroupRequest.builder()
                    .displayName(newDisplayName)
                    .groupId(groupId)
                    .version("*")
                    .build()))
            .then(requestList(this.uaaClient)
                .filter(resource -> newDisplayName.equals(resource.getDisplayName()))
                .single())
            .as(StepVerifier::create)
            .expectNextCount(1)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createGroupId(UaaClient uaaClient, String displayName) {
        return requestCreateGroup(uaaClient, displayName)
            .map(CreateGroupResponse::getId);
    }

    private static Mono<String> createGroupIdWithMember(UaaClient uaaClient, String displayName, String userId) {
        return requestCreateGroupWithMember(uaaClient, displayName, userId)
            .map(CreateGroupResponse::getId);
    }

    private static Mono<String> createUserId(UaaClient uaaClient, String userName) {
        return requestCreateUser(uaaClient, userName)
            .map(CreateUserResponse::getId);
    }

    private static Mono<CreateGroupResponse> requestCreateGroup(UaaClient uaaClient, String displayName) {
        return uaaClient.groups()
            .create(CreateGroupRequest.builder()
                .displayName(displayName)
                .build());
    }

    private static Mono<CreateGroupResponse> requestCreateGroupWithMember(UaaClient uaaClient, String displayName, String userId) {
        return uaaClient.groups()
            .create(CreateGroupRequest.builder()
                .displayName(displayName)
                .member(MemberSummary.builder()
                    .memberId(userId)
                    .origin("test-origin")
                    .type(MemberType.USER)
                    .build())
                .build());
    }

    private static Mono<CreateUserResponse> requestCreateUser(UaaClient uaaClient, String userName) {
        return uaaClient.users()
            .create(CreateUserRequest.builder()
                .email(Email.builder()
                    .value("test-email")
                    .primary(true)
                    .build())
                .name(Name.builder()
                    .familyName("test-family-name")
                    .givenName("test-given-name")
                    .build())
                .password("test-password")
                .userName(userName)
                .build());
    }

    private static Flux<Group> requestList(UaaClient uaaClient) {
        return PaginationUtils
            .requestUaaResources(startIndex -> uaaClient.groups()
                .list(ListGroupsRequest.builder()
                    .startIndex(startIndex)
                    .build()));
    }

    private static Flux<ExternalGroupResource> requestListExternalGroupResource(UaaClient uaaClient) {
        return PaginationUtils
            .requestUaaResources(startIndex -> uaaClient.groups()
                .listExternalGroupMappings(ListExternalGroupMappingsRequest.builder()
                    .startIndex(startIndex)
                    .build()));
    }

    private static Flux<ExternalGroupResource> requestListExternalGroupResources(UaaClient uaaClient) {
        return PaginationUtils
            .requestUaaResources(startIndex -> uaaClient.groups()
                .listExternalGroupMappings(ListExternalGroupMappingsRequest.builder()
                    .startIndex(startIndex)
                    .build()));
    }

    private static Mono<MapExternalGroupResponse> requestMapExternalGroupResponse(UaaClient uaaClient, String displayName, String groupId) {
        return uaaClient.groups()
            .mapExternalGroup(MapExternalGroupRequest.builder()
                .externalGroup(displayName + "-external-group")
                .groupId(groupId)
                .origin(displayName + "-origin")
                .build());
    }

}

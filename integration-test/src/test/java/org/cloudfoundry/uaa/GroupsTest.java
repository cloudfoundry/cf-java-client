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

package org.cloudfoundry.uaa;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.uaa.groups.AddMemberRequest;
import org.cloudfoundry.uaa.groups.AddMemberResponse;
import org.cloudfoundry.uaa.groups.CheckMembershipRequest;
import org.cloudfoundry.uaa.groups.CheckMembershipResponse;
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
import reactor.test.subscriber.ScriptedSubscriber;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static org.cloudfoundry.util.OperationUtils.thenKeep;
import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static reactor.core.publisher.Mono.when;

public final class GroupsTest extends AbstractIntegrationTest {

    @Autowired
    private UaaClient uaaClient;

    @Test
    public void addMemberGroup() throws TimeoutException, InterruptedException {
        String baseDisplayName = this.nameFactory.getGroupName();
        String memberDisplayName = this.nameFactory.getGroupName();

        Function<Tuple2<AddMemberResponse, String>, Optional<String>> assertion = function((response, memberGroupId) -> {
            if (!memberGroupId.equals(response.getMemberId())) {
                return Optional.of(String.format("expected group id: %s; actual group id: %s", memberGroupId, response.getMemberId()));
            }

            if (!Optional.of(memberDisplayName + "-origin").equals(response.getOrigin())) {
                return Optional.of(String.format("expected origin: %s; actual origin: %s", Optional.of(memberDisplayName + "-origin"), response.getOrigin()));
            }

            if (!Optional.of(MemberType.GROUP).equals(response.getType())) {
                return Optional.of(String.format("expected type: %s; actual type: %s", Optional.of(MemberType.GROUP), response.getType()));
            }

            return Optional.empty();
        });

        ScriptedSubscriber<Tuple2<AddMemberResponse, String>> subscriber = ScriptedSubscriber.<Tuple2<AddMemberResponse, String>>create()
            .expectValueWith(actual -> !assertion.apply(actual).isPresent(),
                actual -> assertion.apply(actual).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching response")))
            .expectComplete();

        Mono
            .when(
                createGroupId(this.uaaClient, baseDisplayName),
                createGroupId(this.uaaClient, memberDisplayName)
            )
            .then(function((baseGroupId, memberGroupId) -> Mono
                .when(
                    this.uaaClient.groups()
                        .addMember(AddMemberRequest.builder()
                            .groupId(baseGroupId)
                            .origin(memberDisplayName + "-origin")
                            .type(MemberType.GROUP)
                            .memberId(memberGroupId)
                            .build()),
                    Mono.just(memberGroupId)
                )))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void addMemberUser() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();
        String userName = this.nameFactory.getUserName();

        Function<Tuple2<AddMemberResponse, String>, Optional<String>> assertion = function((response, userId) -> {
            if (!userId.equals(response.getMemberId())) {
                return Optional.of(String.format("expected member id: %s; actual member id: %s", userId, response.getMemberId()));
            }

            if (!Optional.of(userName + "-origin").equals(response.getOrigin())) {
                return Optional.of(String.format("expected origin: %s; actual origin: %s", Optional.of(userName + "-origin"), response.getOrigin()));
            }

            if (!Optional.of(MemberType.USER).equals(response.getType())) {
                return Optional.of(String.format("expected type: %s; actual type: %s", Optional.of(MemberType.USER), response.getType()));
            }

            return Optional.empty();
        });

        ScriptedSubscriber<Tuple2<AddMemberResponse, String>> subscriber = ScriptedSubscriber.<Tuple2<AddMemberResponse, String>>create()
            .expectValueWith(actual -> !assertion.apply(actual).isPresent(),
                actual -> assertion.apply(actual).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching response")))
            .expectComplete();

        when(
            createGroupId(this.uaaClient, displayName),
            createUserId(this.uaaClient, userName)
        )
            .then(function((groupId, userId) ->
                when(
                    this.uaaClient.groups()
                        .addMember(AddMemberRequest.builder()
                            .groupId(groupId)
                            .origin(userName + "-origin")
                            .type(MemberType.USER)
                            .memberId(userId)
                            .build()),
                    Mono.just(userId)
                )))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void checkMembership() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();
        String userName = this.nameFactory.getUserName();

        Function<Tuple2<CheckMembershipResponse, String>, Optional<String>> assertion = function((response, userId) -> {
            if (!userId.equals(response.getMemberId())) {
                return Optional.of(String.format("expected member id: %s; actual member id: %s", userId, response.getMemberId()));
            }

            if (!Optional.of("test-origin").equals(response.getOrigin())) {
                return Optional.of(String.format("expected origin: %s; actual origin: %s", Optional.of("test-origin"), response.getOrigin()));
            }

            if (!Optional.of(MemberType.USER).equals(response.getType())) {
                return Optional.of(String.format("expected type: %s; actual type: %s", Optional.of(MemberType.USER), response.getType()));
            }

            return Optional.empty();
        });

        ScriptedSubscriber<Tuple2<CheckMembershipResponse, String>> subscriber = ScriptedSubscriber.<Tuple2<CheckMembershipResponse, String>>create()
            .expectValueWith(actual -> !assertion.apply(actual).isPresent(),
                actual -> assertion.apply(actual).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching response")))
            .expectComplete();

        createUserId(this.uaaClient, userName)
            .then(userId ->
                when(
                    createGroupIdWithMember(this.uaaClient, displayName, userId),
                    Mono.just(userId)
                ))
            .then(function((groupId, userId) ->
                when(
                    this.uaaClient.groups()
                        .checkMembership(CheckMembershipRequest.builder()
                            .groupId(groupId)
                            .memberId(userId)
                            .build()),
                    Mono.just(userId)
                )))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();
        String userName = this.nameFactory.getUserName();

        ScriptedSubscriber<Group> subscriber = ScriptedSubscriber.<Group>expectValueCount(1)
            .expectComplete();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.groups()
                .create(CreateGroupRequest.builder()
                    .displayName(displayName)
                    .member(MemberSummary.builder()
                        .memberId(userId)
                        .build())
                    .build()))
            .then(requestList(this.uaaClient)
                .filter(resource -> displayName.equals(resource.getDisplayName()))
                .single())
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();

        ScriptedSubscriber<String> subscriber = ScriptedSubscriber.<String>create()
            .expectValue(displayName)
            .expectComplete();

        createGroupId(this.uaaClient, displayName)
            .then(groupId -> this.uaaClient.groups()
                .delete(DeleteGroupRequest.builder()
                    .groupId(groupId)
                    .build()))
            .map(DeleteGroupResponse::getDisplayName)
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();

        ScriptedSubscriber<String> subscriber = ScriptedSubscriber.<String>create()
            .expectValue(displayName)
            .expectComplete();

        createGroupId(this.uaaClient, displayName)
            .then(groupId -> this.uaaClient.groups()
                .get(GetGroupRequest.builder()
                    .groupId(groupId)
                    .build())
                .map(GetGroupResponse::getDisplayName))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();

        ScriptedSubscriber<String> subscriber = ScriptedSubscriber.<String>create()
            .expectValue(displayName)
            .expectComplete();

        createGroupId(this.uaaClient, displayName)
            .then(groupId -> PaginationUtils
                .requestUaaResources(startIndex -> this.uaaClient.groups()
                    .list(ListGroupsRequest.builder()
                        .filter(String.format("id eq \"%s\"", groupId))
                        .startIndex(startIndex)
                        .build()))
                .single())
            .map(Group::getDisplayName)
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listExternalGroupMappings() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();

        ScriptedSubscriber<String> subscriber = ScriptedSubscriber.<String>create()
            .expectValue(displayName + "-external-group")
            .expectComplete();

        createGroupId(this.uaaClient, displayName)
            .then(groupId -> requestMapExternalGroupResponse(this.uaaClient, displayName, groupId))
            .flatMap(ignore -> PaginationUtils
                .requestUaaResources(startIndex -> this.uaaClient.groups()
                    .listExternalGroupMappings(ListExternalGroupMappingsRequest.builder()
                        .startIndex(startIndex)
                        .build()))
                .filter(group -> displayName.equals(group.getDisplayName())))
            .map(ExternalGroupResource::getExternalGroup)
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listMembers() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();
        String userName = this.nameFactory.getUserName();

        ScriptedSubscriber<Tuple2<String, String>> subscriber = tupleEquality();

        createUserId(this.uaaClient, userName)
            .then(userId ->
                when(
                    createGroupIdWithMember(this.uaaClient, displayName, userId),
                    Mono.just(userId)
                ))
            .then(function((groupId, userId) ->
                when(
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
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void listMembersWithEntity() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();
        String userName = this.nameFactory.getUserName();

        ScriptedSubscriber<String> subscriber = ScriptedSubscriber.<String>create()
            .expectValue(userName)
            .expectComplete();

        createUserId(this.uaaClient, userName)
            .then(userId -> createGroupIdWithMember(this.uaaClient, displayName, userId))
            .flatMap(groupId -> this.uaaClient.groups()
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
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void mapExternalGroupMappings() {
        String displayName = this.nameFactory.getGroupName();

        Function<ExternalGroupResource, Optional<String>> assertion = resource -> {
            if (!(displayName + "-external-group").equals(resource.getExternalGroup())) {
                return Optional.of(String.format("expected external group: %s; actual external group: %s", displayName + "-external-group", resource.getExternalGroup()));
            }

            if (!(displayName + "-origin").equals(resource.getOrigin())) {
                return Optional.of(String.format("expected origin: %s; actual origin: %s", displayName + "-origin", resource.getOrigin()));
            }

            return Optional.empty();
        };

        ScriptedSubscriber<ExternalGroupResource> subscriber = ScriptedSubscriber.<ExternalGroupResource>create()
            .expectValueWith(actual -> !assertion.apply(actual).isPresent(),
                actual -> assertion.apply(actual).orElseThrow(() -> new IllegalArgumentException("Cannot generate assertion message for matching response")))
            .expectComplete();

        createGroupId(this.uaaClient, displayName)
            .then(groupId -> this.uaaClient.groups()
                .mapExternalGroup(MapExternalGroupRequest.builder()
                    .externalGroup(displayName + "-external-group")
                    .groupId(groupId)
                    .origin(displayName + "-origin")
                    .build()))
            .flatMap(ignore -> requestListExternalGroupResource(this.uaaClient)
                .filter(group -> displayName.equals(group.getDisplayName())))
            .subscribe(subscriber);
    }

    @Test
    public void removeMember() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();
        String userName = this.nameFactory.getUserName();

        ScriptedSubscriber<Tuple2<String, String>> subscriber = tupleEquality();

        createUserId(this.uaaClient, userName)
            .then(userId ->
                when(
                    createGroupIdWithMember(this.uaaClient, displayName, userId),
                    Mono.just(userId)
                ))
            .then(function((groupId, userId) ->
                when(
                    this.uaaClient.groups()
                        .removeMember(RemoveMemberRequest.builder()
                            .groupId(groupId)
                            .memberId(userId)
                            .build())
                        .map(RemoveMemberResponse::getMemberId),
                    Mono.just(userId)
                )))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void unmapExternalGroupMappingsByGroupDisplayName() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();

        ScriptedSubscriber<ExternalGroupResource> subscriber = ScriptedSubscriber.<ExternalGroupResource>create()
            .expectComplete();

        createGroupId(this.uaaClient, displayName)
            .then(groupId -> requestMapExternalGroupResponse(this.uaaClient, displayName, groupId))
            .then(this.uaaClient.groups()
                .unmapExternalGroupByGroupDisplayName(UnmapExternalGroupByGroupDisplayNameRequest.builder()
                    .groupDisplayName(displayName)
                    .externalGroup(displayName + "-external-group")
                    .origin(displayName + "-origin")
                    .build()))
            .flatMap(ignore -> requestListExternalGroupResources(this.uaaClient))
            .filter(resource -> displayName.equals(resource.getDisplayName()))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void unmapExternalGroupMappingsByGroupId() throws TimeoutException, InterruptedException {
        String displayName = this.nameFactory.getGroupName();

        ScriptedSubscriber<ExternalGroupResource> subscriber = ScriptedSubscriber.<ExternalGroupResource>create()
            .expectComplete();

        createGroupId(this.uaaClient, displayName)
            .as(thenKeep(groupId -> requestMapExternalGroupResponse(this.uaaClient, displayName, groupId)))
            .then(groupId -> this.uaaClient.groups()
                .unmapExternalGroupByGroupId(UnmapExternalGroupByGroupIdRequest.builder()
                    .groupId(groupId)
                    .externalGroup(displayName + "-external-group")
                    .origin(displayName + "-origin")
                    .build()))
            .flatMap(ignore -> requestListExternalGroupResources(this.uaaClient)
                .filter(resource -> displayName.equals(resource.getDisplayName())))
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() throws TimeoutException, InterruptedException {
        String baseDisplayName = this.nameFactory.getGroupName();
        String newDisplayName = this.nameFactory.getGroupName();

        ScriptedSubscriber<Group> subscriber = ScriptedSubscriber.<Group>expectValueCount(1)
            .expectComplete();

        createGroupId(this.uaaClient, baseDisplayName)
            .then(groupId -> this.uaaClient.groups()
                .update(UpdateGroupRequest.builder()
                    .displayName(newDisplayName)
                    .groupId(groupId)
                    .version("*")
                    .build()))
            .then(requestList(this.uaaClient)
                .filter(resource -> newDisplayName.equals(resource.getDisplayName()))
                .single())
            .subscribe(subscriber);

        subscriber.verify(Duration.ofMinutes(5));
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

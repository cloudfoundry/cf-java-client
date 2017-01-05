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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.securitygroups.AssociateSecurityGroupSpaceRequest;
import org.cloudfoundry.client.v2.securitygroups.AssociateSecurityGroupSpaceResponse;
import org.cloudfoundry.client.v2.securitygroups.CreateSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.CreateSecurityGroupResponse;
import org.cloudfoundry.client.v2.securitygroups.DeleteSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupSpacesRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupSpacesResponse;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupsRequest;
import org.cloudfoundry.client.v2.securitygroups.RemoveSecurityGroupSpaceRequest;
import org.cloudfoundry.client.v2.securitygroups.RuleEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupResource;
import org.cloudfoundry.client.v2.securitygroups.UpdateSecurityGroupRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuples;

import java.time.Duration;

import static org.assertj.core.api.Assertions.fail;
import static org.cloudfoundry.client.v2.securitygroups.Protocol.TCP;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class SecurityGroupsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private Mono<String> organizationId;

    @Test
    public void associateSpace() {
        String securityGroupName = this.nameFactory.getSecurityGroupName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .then(organizationId ->
                Mono.when(
                    createSecurityGroupId(this.cloudFoundryClient, securityGroupName),
                    createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
                ))
            .then(function((securityGroupId, spaceId) -> this.cloudFoundryClient.securityGroups()
                .associateSpace(AssociateSecurityGroupSpaceRequest.builder()
                    .securityGroupId(securityGroupId)
                    .spaceId(spaceId)
                    .build())))
            .map(ResourceUtils::getEntity)
            .map(SecurityGroupEntity::getName)
            .as(StepVerifier::create)
            .expectNext(securityGroupName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() {
        String securityGroupName = this.nameFactory.getSecurityGroupName();

        this.cloudFoundryClient.securityGroups()
            .create(CreateSecurityGroupRequest.builder()
                .name(securityGroupName)
                .rule(RuleEntity.builder()
                    .destination("0.0.0.0/0")
                    .log(false)
                    .ports("2048-3000")
                    .protocol(TCP)
                    .build())
                .build())
            .map(ResourceUtils::getEntity)
            .map(SecurityGroupEntity::getName)
            .as(StepVerifier::create)
            .expectNext(securityGroupName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String securityGroupName = this.nameFactory.getSecurityGroupName();

        createSecurityGroupId(this.cloudFoundryClient, securityGroupName)
            .then(securityGroupId -> this.cloudFoundryClient.securityGroups()
                .delete(DeleteSecurityGroupRequest.builder()
                    .securityGroupId(securityGroupId)
                    .build())
                .then(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, job)))
            .then(requestListSecurityGroups(this.cloudFoundryClient, securityGroupName)
                .single())
            .as(StepVerifier::create)
            .expectNextCount(0)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteSpace() {
        String securityGroupName = this.nameFactory.getSecurityGroupName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .then(organizationId -> Mono.when(
                createSecurityGroupId(this.cloudFoundryClient, securityGroupName),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .then(function((securityGroupId, spaceId) -> associateSpace(this.cloudFoundryClient, spaceId, securityGroupId)
                .then(Mono.just(Tuples.of(securityGroupId, spaceId)))))
            .flatMap(function((securityGroupId, spaceId) -> this.cloudFoundryClient.securityGroups()
                .removeSpace(RemoveSecurityGroupSpaceRequest.builder()
                    .securityGroupId(securityGroupId)
                    .spaceId(spaceId)
                    .build())
                .then(Mono.just(Tuples.of(securityGroupId, spaceId)))))
            .flatMap(function((securityGroupId, spaceId) -> requestListSecurityGroupSpaces(this.cloudFoundryClient, spaceId, securityGroupId)))
            .as(StepVerifier::create)
            .expectNextCount(0)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522666 et al")
    @Test
    public void get() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101522666");
    }

    @Test
    public void list() {
        String securityGroupName = this.nameFactory.getSecurityGroupName();

        requestCreateSecurityGroup(this.cloudFoundryClient, securityGroupName)
            .flatMap(ignore -> PaginationUtils.
                requestClientV2Resources(page -> this.cloudFoundryClient.securityGroups()
                    .list(ListSecurityGroupsRequest.builder()
                        .name(securityGroupName)
                        .page(page)
                        .build()))
                .map(ResourceUtils::getEntity)
                .map(SecurityGroupEntity::getName))
            .as(StepVerifier::create)
            .expectNext(securityGroupName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522656 et al")
    @Test
    public void listRunningDefaults() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101522656 et al");
    }

    @Test
    public void listSpaces() {
        String securityGroupName = this.nameFactory.getSecurityGroupName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .then(organizationId ->
                Mono.when(
                    createSecurityGroupId(this.cloudFoundryClient, securityGroupName),
                    createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
                ))
            .then(function((securityGroupId, spaceId) -> associateSpace(this.cloudFoundryClient, spaceId, securityGroupId)
                .then(Mono.just(Tuples.of(securityGroupId, spaceId)))))
            .flatMap(function((securityGroupId, spaceId) -> PaginationUtils.
                requestClientV2Resources(page -> this.cloudFoundryClient.securityGroups()
                    .listSpaces(ListSecurityGroupSpacesRequest.builder()
                        .page(page)
                        .securityGroupId(securityGroupId)
                        .spaceId(spaceId)
                        .build()))))
            .map(ResourceUtils::getEntity)
            .map(SpaceEntity::getName)
            .as(StepVerifier::create)
            .expectNext(spaceName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));

    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522656 et al")
    @Test
    public void listStagingDefaults() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101522650 et al");
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522652")
    @Test
    public void setRunningDefault() {
        fail("TODO: awaiting https://www.pivotaltracker.com/story/show/101522652");
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522652")
    @Test
    public void setStagingDefault() {
        fail("TODO: awaiting https://www.pivotaltracker.com/story/show/101522652");
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522656 et al")
    @Test
    public void unsetRunningDefault() {
        fail("TODO: awaiting https://www.pivotaltracker.com/story/show/101522656 et al");
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522644")
    @Test
    public void unsetStagingDefault() {
        fail("TODO: awaiting https://www.pivotaltracker.com/story/show/101522644");
    }

    @Test
    public void update() {
        String oldSecurityGroupName = this.nameFactory.getSecurityGroupName();
        String newSecurityGroupName = this.nameFactory.getSecurityGroupName();

        createSecurityGroupId(this.cloudFoundryClient, oldSecurityGroupName)
            .log("stream.before")
            .then(securityGroupId -> this.cloudFoundryClient.securityGroups()
                .update(UpdateSecurityGroupRequest.builder()
                    .securityGroupId(securityGroupId)
                    .name(newSecurityGroupName)
                    .build()))
            .then(requestListSecurityGroups(this.cloudFoundryClient, newSecurityGroupName)
                .single()
                .map(ResourceUtils::getEntity)
                .map(SecurityGroupEntity::getName))
            .as(StepVerifier::create)
            .expectNext(newSecurityGroupName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<AssociateSecurityGroupSpaceResponse> associateSpace(CloudFoundryClient cloudFoundryClient, String spaceId, String securityGroupId) {
        return cloudFoundryClient.securityGroups()
            .associateSpace(AssociateSecurityGroupSpaceRequest.builder()
                .securityGroupId(securityGroupId)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<String> createSecurityGroupId(CloudFoundryClient cloudFoundryClient, String securityGroupName) {
        return requestCreateSecurityGroup(cloudFoundryClient, securityGroupName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceName) {
        return cloudFoundryClient.spaces()
            .create(CreateSpaceRequest.builder()
                .organizationId(organizationId)
                .name(spaceName)
                .build())
            .map(ResourceUtils::getId);
    }

    private static Mono<CreateSecurityGroupResponse> requestCreateSecurityGroup(CloudFoundryClient cloudFoundryClient, String securityGroupName) {
        return cloudFoundryClient.securityGroups()
            .create(CreateSecurityGroupRequest.builder()
                .name(securityGroupName)
                .build());
    }

    private static Mono<ListSecurityGroupSpacesResponse> requestListSecurityGroupSpaces(CloudFoundryClient cloudFoundryClient, String spaceId, String securityGroupId) {
        return cloudFoundryClient.securityGroups()
            .listSpaces(ListSecurityGroupSpacesRequest.builder()
                .securityGroupId(securityGroupId)
                .spaceId(spaceId)
                .build());
    }

    private static Flux<SecurityGroupResource> requestListSecurityGroups(CloudFoundryClient cloudFoundryClient, String securityGroupName) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.securityGroups()
            .list(ListSecurityGroupsRequest.builder()
                .name(securityGroupName)
                .page(page)
                .build()));
    }

}

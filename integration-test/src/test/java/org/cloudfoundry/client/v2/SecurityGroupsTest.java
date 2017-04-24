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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.securitygroups.AssociateSecurityGroupSpaceRequest;
import org.cloudfoundry.client.v2.securitygroups.AssociateSecurityGroupSpaceResponse;
import org.cloudfoundry.client.v2.securitygroups.CreateSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.CreateSecurityGroupResponse;
import org.cloudfoundry.client.v2.securitygroups.DeleteSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.GetSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupRunningDefaultsRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupSpacesRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupStagingDefaultsRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupsRequest;
import org.cloudfoundry.client.v2.securitygroups.RemoveSecurityGroupRunningDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.RemoveSecurityGroupSpaceRequest;
import org.cloudfoundry.client.v2.securitygroups.RemoveSecurityGroupStagingDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.RuleEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupResource;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupRunningDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupRunningDefaultResponse;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupStagingDefaultRequest;
import org.cloudfoundry.client.v2.securitygroups.SetSecurityGroupStagingDefaultResponse;
import org.cloudfoundry.client.v2.securitygroups.UpdateSecurityGroupRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.SpaceEntity;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuples;

import java.time.Duration;

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
            .flatMap(organizationId ->
                Mono.when(
                    createSecurityGroupId(this.cloudFoundryClient, securityGroupName),
                    createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
                ))
            .flatMap(function((securityGroupId, spaceId) -> this.cloudFoundryClient.securityGroups()
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
            .flatMap(securityGroupId -> this.cloudFoundryClient.securityGroups()
                .delete(DeleteSecurityGroupRequest.builder()
                    .securityGroupId(securityGroupId)
                    .build())
                .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job)))
            .thenMany(requestListSecurityGroups(this.cloudFoundryClient, securityGroupName))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteSpace() {
        String securityGroupName = this.nameFactory.getSecurityGroupName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId -> Mono.when(
                createSecurityGroupId(this.cloudFoundryClient, securityGroupName),
                createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
            ))
            .flatMap(function((securityGroupId, spaceId) -> associateSpace(this.cloudFoundryClient, spaceId, securityGroupId)
                .then(Mono.just(Tuples.of(securityGroupId, spaceId)))))
            .flatMapMany(function((securityGroupId, spaceId) -> this.cloudFoundryClient.securityGroups()
                .removeSpace(RemoveSecurityGroupSpaceRequest.builder()
                    .securityGroupId(securityGroupId)
                    .spaceId(spaceId)
                    .build())
                .then(Mono.just(Tuples.of(securityGroupId, spaceId)))))
            .flatMap(function((securityGroupId, spaceId) -> requestListSecurityGroupSpaces(this.cloudFoundryClient, spaceId, securityGroupId)))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        String securityGroupName = this.nameFactory.getSecurityGroupName();

        createSecurityGroupId(this.cloudFoundryClient, securityGroupName)
            .flatMap(securityGroupId -> this.cloudFoundryClient.securityGroups()
                .get(GetSecurityGroupRequest.builder()
                    .securityGroupId(securityGroupId)
                    .build())
                .map(ResourceUtils::getEntity)
                .map(SecurityGroupEntity::getName))
            .as(StepVerifier::create)
            .expectNext(securityGroupName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        String securityGroupName = this.nameFactory.getSecurityGroupName();

        requestCreateSecurityGroup(this.cloudFoundryClient, securityGroupName)
            .thenMany(PaginationUtils.
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

    @Test
    public void listRunningDefaults() {
        String securityGroupName1 = this.nameFactory.getSecurityGroupName();
        String securityGroupName2 = this.nameFactory.getSecurityGroupName();

        createSecurityGroupId(this.cloudFoundryClient, securityGroupName1)
            .flatMap(securityGroupId -> requestSetRunningDefault(this.cloudFoundryClient, securityGroupId))
            .then(createSecurityGroupId(this.cloudFoundryClient, securityGroupName2)
                .flatMap(securityGroupId -> requestSetRunningDefault(this.cloudFoundryClient, securityGroupId)))
            .thenMany(requestListRunningDefaults(this.cloudFoundryClient))
            .filter(response -> securityGroupName1.equals(response.getEntity().getName()) || securityGroupName2.equals(response.getEntity().getName()))
            .as(StepVerifier::create)
            .expectNextCount(2)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listSpaces() {
        String securityGroupName = this.nameFactory.getSecurityGroupName();
        String spaceName = this.nameFactory.getSpaceName();

        this.organizationId
            .flatMap(organizationId ->
                Mono.when(
                    createSecurityGroupId(this.cloudFoundryClient, securityGroupName),
                    createSpaceId(this.cloudFoundryClient, organizationId, spaceName)
                ))
            .flatMap(function((securityGroupId, spaceId) -> associateSpace(this.cloudFoundryClient, spaceId, securityGroupId)
                .then(Mono.just(Tuples.of(securityGroupId, spaceId)))))
            .flatMapMany(function((securityGroupId, spaceId) -> PaginationUtils.
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

    @Test
    public void listStagingDefaults() {
        String securityGroupName1 = this.nameFactory.getSecurityGroupName();
        String securityGroupName2 = this.nameFactory.getSecurityGroupName();

        createSecurityGroupId(this.cloudFoundryClient, securityGroupName1)
            .flatMap(securityGroupId -> requestSetStagingDefault(this.cloudFoundryClient, securityGroupId))
            .then(createSecurityGroupId(this.cloudFoundryClient, securityGroupName2)
                .flatMap(securityGroupId -> requestSetStagingDefault(this.cloudFoundryClient, securityGroupId)))
            .thenMany(requestListStagingDefaults(this.cloudFoundryClient))
            .filter(response -> securityGroupName1.equals(response.getEntity().getName()) || securityGroupName2.equals(response.getEntity().getName()))
            .as(StepVerifier::create)
            .expectNextCount(2)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void setRunningDefault() {
        String securityGroupName = this.nameFactory.getSecurityGroupName();

        createSecurityGroupId(this.cloudFoundryClient, securityGroupName)
            .flatMap(securityGroupId -> this.cloudFoundryClient.securityGroups()
                .setRunningDefault(SetSecurityGroupRunningDefaultRequest.builder()
                    .securityGroupId(securityGroupId)
                    .build()))
            .map(ResourceUtils::getEntity)
            .map(SecurityGroupEntity::getName)
            .as(StepVerifier::create)
            .expectNext(securityGroupName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void setStagingDefault() {
        String securityGroupName = this.nameFactory.getSecurityGroupName();

        createSecurityGroupId(this.cloudFoundryClient, securityGroupName)
            .flatMap(securityGroupId -> this.cloudFoundryClient.securityGroups()
                .setStagingDefault(SetSecurityGroupStagingDefaultRequest.builder()
                    .securityGroupId(securityGroupId)
                    .build()))
            .map(ResourceUtils::getEntity)
            .map(SecurityGroupEntity::getName)
            .as(StepVerifier::create)
            .expectNext(securityGroupName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void unsetRunningDefault() {
        String securityGroupName = this.nameFactory.getSecurityGroupName();

        createSecurityGroupId(this.cloudFoundryClient, securityGroupName)
            .flatMap(securityGroupId -> requestSetRunningDefault(this.cloudFoundryClient, securityGroupId)
                .then(this.cloudFoundryClient.securityGroups()
                    .removeRunningDefault(RemoveSecurityGroupRunningDefaultRequest.builder()
                        .securityGroupId(securityGroupId)
                        .build())))
            .thenMany(requestListRunningDefaults(this.cloudFoundryClient))
            .filter(response -> securityGroupName.equals(response.getEntity().getName()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void unsetStagingDefault() {
        String securityGroupName = this.nameFactory.getSecurityGroupName();

        createSecurityGroupId(this.cloudFoundryClient, securityGroupName)
            .flatMap(securityGroupId -> requestSetStagingDefault(this.cloudFoundryClient, securityGroupId)
                .then(this.cloudFoundryClient.securityGroups()
                    .removeStagingDefault(RemoveSecurityGroupStagingDefaultRequest.builder()
                        .securityGroupId(securityGroupId)
                        .build())))
            .thenMany(requestListRunningDefaults(this.cloudFoundryClient))
            .filter(response -> securityGroupName.equals(response.getEntity().getName()))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() {
        String oldSecurityGroupName = this.nameFactory.getSecurityGroupName();
        String newSecurityGroupName = this.nameFactory.getSecurityGroupName();

        createSecurityGroupId(this.cloudFoundryClient, oldSecurityGroupName)
            .flatMap(securityGroupId -> this.cloudFoundryClient.securityGroups()
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

    private static Flux<SecurityGroupResource> requestListRunningDefaults(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.securityGroups()
                .listRunningDefaults(ListSecurityGroupRunningDefaultsRequest.builder()
                    .build()));
    }

    private static Flux<SpaceResource> requestListSecurityGroupSpaces(CloudFoundryClient cloudFoundryClient, String spaceId, String securityGroupId) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.securityGroups()
            .listSpaces(ListSecurityGroupSpacesRequest.builder()
                .page(page)
                .securityGroupId(securityGroupId)
                .spaceId(spaceId)
                .build()));
    }

    private static Flux<SecurityGroupResource> requestListSecurityGroups(CloudFoundryClient cloudFoundryClient, String securityGroupName) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.securityGroups()
            .list(ListSecurityGroupsRequest.builder()
                .name(securityGroupName)
                .page(page)
                .build()));
    }

    private static Flux<SecurityGroupResource> requestListStagingDefaults(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.
            requestClientV2Resources(page -> cloudFoundryClient.securityGroups()
                .listStagingDefaults(ListSecurityGroupStagingDefaultsRequest.builder()
                    .build()));
    }

    private static Mono<SetSecurityGroupRunningDefaultResponse> requestSetRunningDefault(CloudFoundryClient cloudFoundryClient, String securityGroupId) {
        return cloudFoundryClient.securityGroups()
            .setRunningDefault(SetSecurityGroupRunningDefaultRequest.builder()
                .securityGroupId(securityGroupId)
                .build());
    }

    private static Mono<SetSecurityGroupStagingDefaultResponse> requestSetStagingDefault(CloudFoundryClient cloudFoundryClient, String securityGroupId) {
        return cloudFoundryClient.securityGroups()
            .setStagingDefault(SetSecurityGroupStagingDefaultRequest.builder()
                .securityGroupId(securityGroupId)
                .build());
    }

}

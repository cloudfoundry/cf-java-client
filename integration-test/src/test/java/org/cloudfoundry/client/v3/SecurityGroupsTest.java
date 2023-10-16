/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.cloudfoundry.client.v3;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.securitygroups.UnbindStagingSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.UnbindRunningSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.BindStagingSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.BindRunningSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.CreateSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.CreateSecurityGroupResponse;
import org.cloudfoundry.client.v3.securitygroups.Rule;

import org.cloudfoundry.client.v3.securitygroups.GloballyEnabled;

import org.cloudfoundry.client.v3.securitygroups.UpdateSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.DeleteSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.GetSecurityGroupRequest;

import org.cloudfoundry.client.v3.securitygroups.ListSecurityGroupsRequest;
import org.cloudfoundry.client.v3.securitygroups.ListRunningSecurityGroupsRequest;
import org.cloudfoundry.client.v3.securitygroups.ListStagingSecurityGroupsRequest;

import org.junit.Test;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;

import static org.cloudfoundry.client.v3.securitygroups.Protocol.TCP;

public final class SecurityGroupsTest extends AbstractIntegrationTest {

        @Autowired
        private CloudFoundryClient cloudFoundryClient;

        @Autowired
        private Mono<String> spaceId;
        private Mono<CreateSecurityGroupResponse> securityGroup;
        private String securityGroupName;

        @Before
        public void setup() {
                this.securityGroupName = this.nameFactory.getSecurityGroupName();

                this.securityGroup = this.cloudFoundryClient.securityGroupsV3()
                                .create(CreateSecurityGroupRequest.builder()
                                                .name(this.securityGroupName)
                                                .globallyEnabled(GloballyEnabled.builder()
                                                                .staging(true).running(true)
                                                                .build())
                                                .rule(Rule.builder().destination("0.0.0.0/0")
                                                                .log(false).ports("2048-3000")
                                                                .protocol(TCP).build())
                                                .build());
        }

        @Test
        public void create() {
                this.securityGroup.map(securityGroup -> securityGroup.getName())
                                .as(StepVerifier::create).expectNext(this.securityGroupName)
                                .expectComplete().verify(Duration.ofMinutes(5));
        }

        @Test
        public void get() {
                this.securityGroup
                                .flatMap(securityGroup -> this.cloudFoundryClient.securityGroupsV3()
                                                .get(GetSecurityGroupRequest.builder()
                                                                .securityGroupId(securityGroup
                                                                                .getId())
                                                                .build())
                                                .map(sg -> sg.getName()))
                                .as(StepVerifier::create).expectNext(this.securityGroupName)
                                .expectComplete().verify(Duration.ofMinutes(5));
        }

        @Test
        public void update() {
                String newSecurityGroupName = this.nameFactory.getSecurityGroupName();
                this.securityGroup
                                .flatMap(securityGroup -> this.cloudFoundryClient.securityGroupsV3()
                                                .update(UpdateSecurityGroupRequest.builder()
                                                                .securityGroupId(securityGroup
                                                                                .getId())
                                                                .name(newSecurityGroupName)
                                                                .build()))
                                .map(securityGroup -> securityGroup.getName())
                                .as(StepVerifier::create).expectNext(newSecurityGroupName)
                                .expectComplete().verify(Duration.ofMinutes(5));
        }

        @Test
        public void delete() {
                this.securityGroup
                                .flatMap(securityGroup -> this.cloudFoundryClient.securityGroupsV3()
                                                .delete(DeleteSecurityGroupRequest.builder()
                                                                .securityGroupId(securityGroup
                                                                                .getId())
                                                                .build())
                                                .map(id -> Arrays.asList(id)))
                                .as(StepVerifier::create).expectNextCount(1).expectComplete()
                                .verify(Duration.ofMinutes(5));
        }

        @Test
        public void list() {
                this.securityGroup
                                .map(securityGroup -> this.cloudFoundryClient.securityGroupsV3()
                                                .list(ListSecurityGroupsRequest.builder()
                                                                .names(Arrays.asList(securityGroup
                                                                                .getName()))
                                                                .build()))
                                .as(StepVerifier::create).expectNextCount(1).expectComplete()
                                .verify(Duration.ofMinutes(5));
        }

        @Test
        public void listRunning() {
                Mono.zip(this.securityGroup, this.spaceId).flatMap(v -> this.cloudFoundryClient
                                .securityGroupsV3()
                                .listRunning(ListRunningSecurityGroupsRequest.builder()
                                                .spaceId(v.getT2())
                                                .names(Arrays.asList(v.getT1().getName())).build()))
                                .as(StepVerifier::create).expectNextCount(1).expectComplete()
                                .verify(Duration.ofMinutes(5));
        }

        @Test
        public void listStaging() {
                Mono.zip(this.securityGroup, this.spaceId).flatMap(v -> this.cloudFoundryClient
                                .securityGroupsV3()
                                .listStaging(ListStagingSecurityGroupsRequest.builder()
                                                .spaceId(v.getT2())
                                                .names(Arrays.asList(v.getT1().getName())).build()))
                                .as(StepVerifier::create).expectNextCount(1).expectComplete()
                                .verify(Duration.ofMinutes(5));
        }

        @Test
        public void bindStagingSecurityGroup() {
                Mono.zip(this.securityGroup, this.spaceId).flatMap(v -> this.cloudFoundryClient
                                .securityGroupsV3()
                                .bindStagingSecurityGroup(BindStagingSecurityGroupRequest.builder()
                                                .securityGroupId(v.getT1().getId())
                                                .boundSpaces(Relationship.builder().id(v.getT2())
                                                                .build())
                                                .build()))
                                .as(StepVerifier::create).expectNextCount(1).expectComplete()
                                .verify(Duration.ofMinutes(5));
        }

        @Test
        public void unbindStagingSecurityGroup() {
                Mono.zip(this.securityGroup, this.spaceId).flatMap(v -> this.cloudFoundryClient
                                .securityGroupsV3()
                                .bindStagingSecurityGroup(BindStagingSecurityGroupRequest.builder()
                                                .securityGroupId(v.getT1().getId())
                                                .boundSpaces(Relationship.builder().id(v.getT2())
                                                                .build())
                                                .build())
                                .then(this.cloudFoundryClient.securityGroupsV3()
                                                .unbindStagingSecurityGroup(
                                                                UnbindStagingSecurityGroupRequest
                                                                                .builder()
                                                                                .securityGroupId(v
                                                                                                .getT1()
                                                                                                .getId())
                                                                                .spaceId(v.getT2())
                                                                                .build())))

                                .as(StepVerifier::create).expectNextCount(0).expectComplete()
                                .verify(Duration.ofMinutes(5));
        }

        @Test
        public void bindRunningSecurityGroup() {
                Mono.zip(this.securityGroup, this.spaceId).flatMap(v -> this.cloudFoundryClient
                                .securityGroupsV3()
                                .bindRunningSecurityGroup(BindRunningSecurityGroupRequest.builder()
                                                .securityGroupId(v.getT1().getId())
                                                .boundSpaces(Relationship.builder().id(v.getT2())
                                                                .build())
                                                .build()))
                                .as(StepVerifier::create).expectNextCount(1).expectComplete()
                                .verify(Duration.ofMinutes(5));
        }

        @Test
        public void unbindRunnungSecurityGroup() {
                Mono.zip(this.securityGroup, this.spaceId).flatMap(v -> this.cloudFoundryClient
                                .securityGroupsV3()
                                .bindRunningSecurityGroup(BindRunningSecurityGroupRequest.builder()
                                                .securityGroupId(v.getT1().getId())
                                                .boundSpaces(Relationship.builder().id(v.getT2())
                                                                .build())
                                                .build())
                                .then(this.cloudFoundryClient.securityGroupsV3()
                                                .unbindRunningSecurityGroup(
                                                                UnbindRunningSecurityGroupRequest
                                                                                .builder()
                                                                                .securityGroupId(v
                                                                                                .getT1()
                                                                                                .getId())
                                                                                .spaceId(v.getT2())
                                                                                .build())))

                                .as(StepVerifier::create).expectNextCount(0).expectComplete()
                                .verify(Duration.ofMinutes(5));
        }
}

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

package org.cloudfoundry.client.v3;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.securitygroups.UnbindStagingSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.UnbindRunningSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.BindStagingSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.BindRunningSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.CreateSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.Rule;
import org.cloudfoundry.client.v3.securitygroups.UpdateSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.DeleteSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.GetSecurityGroupRequest;
import org.cloudfoundry.client.v3.securitygroups.ListSecurityGroupsRequest;
import org.cloudfoundry.client.v3.securitygroups.ListRunningSecurityGroupsRequest;
import org.cloudfoundry.client.v3.securitygroups.ListStagingSecurityGroupsRequest;
import org.cloudfoundry.util.JobUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.cloudfoundry.client.v3.securitygroups.Protocol.TCP;

public final class SecurityGroupsTest extends AbstractIntegrationTest {

        @Autowired
        private CloudFoundryClient cloudFoundryClient;

        @Autowired
        private Mono<String> organizationId;

        private String securityGroupName;
        private Mono<String> securityGroupId;

        @Autowired
        private Mono<String> spaceId;

        @BeforeClass
        public void settup() {
                this.securityGroupName = this.nameFactory.getSecurityGroupName();

                this.cloudFoundryClient.securityGroupsV3()
                                .create(CreateSecurityGroupRequest.builder()
                                                .name(securityGroupName)
                                                .rule(Rule.builder()
                                                                .destination("0.0.0.0/0")
                                                                .log(false)
                                                                .ports("2048-3000")
                                                                .protocol(TCP)
                                                                .build())
                                                .build())
                                .doOnSuccess(response -> this.securityGroupId = Mono.just(response.getId()))
                                .map(response -> response.getName())
                                .as(StepVerifier::create)
                                .expectNext(securityGroupName)
                                .expectComplete()
                                .verify(Duration.ofMinutes(5));
        }

        @AfterClass
        public void tearDown() {
                this.cloudFoundryClient.securityGroupsV3().delete(
                                DeleteSecurityGroupRequest.builder()
                                                .securityGroupId(this.securityGroupId.block())
                                                .build())
                                .flatMap(
                                                job -> JobUtils.waitForCompletion(this.cloudFoundryClient,
                                                                Duration.ofMinutes(5), job))
                                .as(StepVerifier::create)
                                .expectComplete()
                                .verify(Duration.ofMinutes(5));
        }

        @Test
        public void update() {
                this.cloudFoundryClient.securityGroupsV3().update(
                                UpdateSecurityGroupRequest.builder()
                                                .securityGroupId(securityGroupId.block())
                                                .rule(Rule.builder()
                                                                .destination("0.0.0.0/0")
                                                                .ports("8080")
                                                                .protocol(TCP)
                                                                .build())
                                                .build())
                                .as(StepVerifier::create)
                                .expectComplete()
                                .verify(Duration.ofMinutes(5));

        }

        @Test
        public void get() {
                this.cloudFoundryClient.securityGroupsV3().get(
                                GetSecurityGroupRequest.builder()
                                                .securityGroupId(securityGroupId.block())
                                                .build())
                                .map(securityGroup -> securityGroup.getName())
                                .as(StepVerifier::create)
                                .expectNext(this.securityGroupName)
                                .expectComplete()
                                .verify(Duration.ofMinutes(5));

        }

        @Test
        public void list() {
                this.cloudFoundryClient.securityGroupsV3().list(
                                ListSecurityGroupsRequest.builder()
                                                .build())
                                .as(StepVerifier::create)
                                .expectComplete()
                                .verify(Duration.ofMinutes(5));
        }

        @Test
        public void listRunning() {
                this.cloudFoundryClient.securityGroupsV3().listRunning(
                                ListRunningSecurityGroupsRequest.builder()
                                                .spaceId(this.spaceId.block())
                                                .build())
                                .as(StepVerifier::create)
                                .expectComplete()
                                .verify(Duration.ofMinutes(5));
        }

        @Test
        public void listStaging() {
                this.cloudFoundryClient.securityGroupsV3().listStaging(
                                ListStagingSecurityGroupsRequest.builder()
                                                .spaceId(this.spaceId.block())
                                                .build())
                                .as(StepVerifier::create)
                                .expectComplete()
                                .verify(Duration.ofMinutes(5));
        }

        @Test
        public void bindStagingSecurityGroup() {
                this.cloudFoundryClient.securityGroupsV3().bindStagingSecurityGroup(
                                BindStagingSecurityGroupRequest.builder()
                                                .securityGroupId(this.securityGroupId.block())
                                                .boundSpaces(Relationship.builder()
                                                                .id(this.spaceId.block())
                                                                .build())
                                                .build())
                                .as(StepVerifier::create)
                                .expectComplete()
                                .verify(Duration.ofMinutes(5));
        }

        @Test
        public void bindRunningSecurityGroup() {
                this.cloudFoundryClient.securityGroupsV3().bindRunningSecurityGroup(
                                BindRunningSecurityGroupRequest.builder()
                                                .securityGroupId(this.securityGroupId.block())
                                                .boundSpaces(Relationship.builder()
                                                                .id(this.spaceId.block())
                                                                .build())
                                                .build())
                                .as(StepVerifier::create)
                                .expectComplete()
                                .verify(Duration.ofMinutes(5));
        }

        @Test
        public void unbindRunningSecurityGroup() {
                this.cloudFoundryClient.securityGroupsV3().unbindRunningSecurityGroup(
                                UnbindRunningSecurityGroupRequest.builder()
                                                .securityGroupId(this.securityGroupId.block())
                                                .spaceId(this.spaceId.block())
                                                .build())
                                .as(StepVerifier::create)
                                .expectComplete()
                                .verify(Duration.ofMinutes(5));
        }

        @Test
        public void unbindStagingSecurityGroup() {
                this.cloudFoundryClient.securityGroupsV3().unbindStagingSecurityGroup(
                                UnbindStagingSecurityGroupRequest.builder()
                                                .securityGroupId(this.securityGroupId.block())
                                                .spaceId(this.spaceId.block())
                                                .build())
                                .as(StepVerifier::create)
                                .expectComplete()
                                .verify(Duration.ofMinutes(5));
        }
}

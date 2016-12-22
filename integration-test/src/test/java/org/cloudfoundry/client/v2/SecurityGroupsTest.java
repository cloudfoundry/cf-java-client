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
import org.cloudfoundry.client.v2.securitygroups.CreateSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.CreateSecurityGroupResponse;
import org.cloudfoundry.client.v2.securitygroups.DeleteSecurityGroupRequest;
import org.cloudfoundry.client.v2.securitygroups.ListSecurityGroupsRequest;
import org.cloudfoundry.client.v2.securitygroups.RuleEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupEntity;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroupResource;
import org.cloudfoundry.client.v2.securitygroups.UpdateSecurityGroupRequest;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.fail;
import static org.cloudfoundry.client.v2.securitygroups.Protocol.TCP;

public final class SecurityGroupsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522654 et al")
    @Test
    public void associateSpace() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101522654");
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

        getSecurityGroupId(this.cloudFoundryClient, securityGroupName)
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

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522664 et al")
    @Test
    public void deleteSpace() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101522664");
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

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522662 et al")
    @Test
    public void listSpaces() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101522662");
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

        getSecurityGroupId(this.cloudFoundryClient, oldSecurityGroupName)
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

    private static Mono<String> getSecurityGroupId(CloudFoundryClient cloudFoundryClient, String securityGroupName) {
        return requestCreateSecurityGroup(cloudFoundryClient, securityGroupName)
            .map(ResourceUtils::getId);
    }

    private static Mono<CreateSecurityGroupResponse> requestCreateSecurityGroup(CloudFoundryClient cloudFoundryClient, String securityGroupName) {
        return cloudFoundryClient.securityGroups()
            .create(CreateSecurityGroupRequest.builder()
                .name(securityGroupName)
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

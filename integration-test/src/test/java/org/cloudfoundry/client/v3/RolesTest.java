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
import org.cloudfoundry.CloudFoundryVersion;
import org.cloudfoundry.IfCloudFoundryVersion;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v3.roles.CreateRoleRequest;
import org.cloudfoundry.client.v3.roles.CreateRoleResponse;
import org.cloudfoundry.client.v3.roles.DeleteRoleRequest;
import org.cloudfoundry.client.v3.roles.GetRoleRequest;
import org.cloudfoundry.client.v3.roles.ListRolesRequest;
import org.cloudfoundry.client.v3.roles.RoleRelationships;
import org.cloudfoundry.client.v3.roles.RoleResource;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.cloudfoundry.client.v3.roles.RoleType.ORGANIZATION_USER;

@IfCloudFoundryVersion(greaterThanOrEqualTo = CloudFoundryVersion.PCF_2_9)
final class RolesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Test
    void create() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> this.cloudFoundryClient.rolesV3()
                .create(CreateRoleRequest.builder()
                    .relationships(RoleRelationships.builder()
                        .organization(ToOneRelationship.builder()
                            .data(Relationship.builder()
                                .id(organizationId)
                                .build())
                            .build())
                        .user(ToOneRelationship.builder()
                            .data(Relationship.builder()
                                .id(userId)
                                .build())
                            .build())
                        .build())
                    .type(ORGANIZATION_USER)
                    .build())
                .thenReturn(organizationId))
            .flatMapMany(organizationId -> requestListRoles(this.cloudFoundryClient)
                .map(RoleResource::getRelationships)
                .filter(relationship -> {
                    if (relationship.getOrganization().getData() != null) {
                        return organizationId.equals(relationship.getOrganization().getData().getId());
                    } else {
                        return false;
                    }
                })
                .map(relationship -> relationship.getUser().getData().getId()))
            .as(StepVerifier::create)
            .expectNext(userId)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    void delete() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateOrganizationRelationship(this.cloudFoundryClient, organizationId, userId)
                .map(CreateRoleResponse::getId))
            .delayUntil(roleId -> this.cloudFoundryClient.rolesV3()
                .delete(DeleteRoleRequest.builder()
                    .roleId(roleId)
                    .build())
                .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job)))
            .flatMapMany(roleId -> requestListRoles(this.cloudFoundryClient)
                .filter(resource -> roleId.equals(resource.getId())))
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    void get() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateOrganizationRelationship(this.cloudFoundryClient, organizationId, userId)
                .map(CreateRoleResponse::getId))
            .flatMap(roleId -> this.cloudFoundryClient.rolesV3()
                .get(GetRoleRequest.builder()
                    .roleId(roleId)
                    .build())
                .map(response -> response.getRelationships().getUser().getData().getId()))
            .as(StepVerifier::create)
            .expectNext(userId)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    void listFilterByOrganizationId() {
        String organizationName = this.nameFactory.getOrganizationName();
        String userId = this.nameFactory.getUserId();

        createOrganizationId(this.cloudFoundryClient, organizationName)
            .flatMap(organizationId -> requestCreateOrganizationRelationship(this.cloudFoundryClient, organizationId, userId)
                .map(CreateRoleResponse::getId))
            .flatMapMany(roleId -> PaginationUtils.requestClientV3Resources(page ->
                this.cloudFoundryClient.rolesV3()
                    .list(ListRolesRequest.builder()
                        .roleId(roleId)
                        .page(page)
                        .build()))
                .map(resource -> resource.getRelationships().getUser().getData().getId()))
            .as(StepVerifier::create)
            .expectNext(userId)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createOrganizationId(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return requestCreateOrganization(cloudFoundryClient, organizationName)
            .map(CreateOrganizationResponse::getId);
    }

    private static Mono<CreateOrganizationResponse> requestCreateOrganization(CloudFoundryClient cloudFoundryClient, String organizationName) {
        return cloudFoundryClient.organizationsV3()
            .create(CreateOrganizationRequest.builder()
                .name(organizationName)
                .build());
    }

    private static Mono<CreateRoleResponse> requestCreateOrganizationRelationship(CloudFoundryClient cloudFoundryClient, String organizationId, String userId) {
        return cloudFoundryClient.rolesV3()
            .create(CreateRoleRequest.builder()
                .relationships(RoleRelationships.builder()
                    .organization(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id(organizationId)
                            .build())
                        .build())
                    .user(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id(userId)
                            .build())
                        .build())
                    .build())
                .type(ORGANIZATION_USER)
                .build());
    }

    private static Flux<RoleResource> requestListRoles(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.requestClientV3Resources(page ->
            cloudFoundryClient.rolesV3()
                .list(ListRolesRequest.builder()
                    .page(page)
                    .build()));
    }

}

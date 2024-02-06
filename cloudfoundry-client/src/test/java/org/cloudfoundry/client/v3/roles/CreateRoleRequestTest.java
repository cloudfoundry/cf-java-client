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

package org.cloudfoundry.client.v3.roles;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.junit.jupiter.api.Test;

class CreateRoleRequestTest {

    @Test
    void invalidWithMissingOrgRelationship() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateRoleRequest.builder()
                            .type(RoleType.ORGANIZATION_AUDITOR)
                            .relationships(
                                    RoleRelationships.builder()
                                            .user(
                                                    ToOneRelationship.builder()
                                                            .data(
                                                                    Relationship.builder()
                                                                            .id("test-user-id")
                                                                            .build())
                                                            .build())
                                            .build())
                            .build();
                });
    }

    @Test
    void invalidWithMissingSpaceRelationship() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateRoleRequest.builder()
                            .type(RoleType.SPACE_AUDITOR)
                            .relationships(
                                    RoleRelationships.builder()
                                            .user(
                                                    ToOneRelationship.builder()
                                                            .data(
                                                                    Relationship.builder()
                                                                            .id("test-user-id")
                                                                            .build())
                                                            .build())
                                            .build())
                            .build();
                });
    }

    @Test
    void invalidWithNoRelationships() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateRoleRequest.builder().type(RoleType.ORGANIZATION_AUDITOR).build();
                });
    }

    @Test
    void invalidWithOrgRelationship() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateRoleRequest.builder()
                            .type(RoleType.SPACE_AUDITOR)
                            .relationships(
                                    RoleRelationships.builder()
                                            .user(
                                                    ToOneRelationship.builder()
                                                            .data(
                                                                    Relationship.builder()
                                                                            .id("test-user-id")
                                                                            .build())
                                                            .build())
                                            .organization(
                                                    ToOneRelationship.builder()
                                                            .data(
                                                                    Relationship.builder()
                                                                            .id("test-org-id")
                                                                            .build())
                                                            .build())
                                            .build())
                            .build();
                });
    }

    @Test
    void invalidWithSpaceRelationship() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateRoleRequest.builder()
                            .type(RoleType.ORGANIZATION_AUDITOR)
                            .relationships(
                                    RoleRelationships.builder()
                                            .user(
                                                    ToOneRelationship.builder()
                                                            .data(
                                                                    Relationship.builder()
                                                                            .id("test-user-id")
                                                                            .build())
                                                            .build())
                                            .space(
                                                    ToOneRelationship.builder()
                                                            .data(
                                                                    Relationship.builder()
                                                                            .id("test-space-id")
                                                                            .build())
                                                            .build())
                                            .build())
                            .build();
                });
    }

    @Test
    void invalidWithoutType() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateRoleRequest.builder()
                            .relationships(
                                    RoleRelationships.builder()
                                            .user(
                                                    ToOneRelationship.builder()
                                                            .data(
                                                                    Relationship.builder()
                                                                            .id("test-user-id")
                                                                            .build())
                                                            .build())
                                            .build())
                            .build();
                });
    }

    @Test
    void validOrgRole() {
        CreateRoleRequest.builder()
                .type(RoleType.ORGANIZATION_AUDITOR)
                .relationships(
                        RoleRelationships.builder()
                                .user(
                                        ToOneRelationship.builder()
                                                .data(
                                                        Relationship.builder()
                                                                .id("test-user-id")
                                                                .build())
                                                .build())
                                .organization(
                                        ToOneRelationship.builder()
                                                .data(
                                                        Relationship.builder()
                                                                .id("test-org-id")
                                                                .build())
                                                .build())
                                .build())
                .build();
    }

    @Test
    void validSpaceRole() {
        CreateRoleRequest.builder()
                .type(RoleType.SPACE_AUDITOR)
                .relationships(
                        RoleRelationships.builder()
                                .user(
                                        ToOneRelationship.builder()
                                                .data(
                                                        Relationship.builder()
                                                                .id("test-user-id")
                                                                .build())
                                                .build())
                                .space(
                                        ToOneRelationship.builder()
                                                .data(
                                                        Relationship.builder()
                                                                .id("test-space-id")
                                                                .build())
                                                .build())
                                .build())
                .build();
    }
}

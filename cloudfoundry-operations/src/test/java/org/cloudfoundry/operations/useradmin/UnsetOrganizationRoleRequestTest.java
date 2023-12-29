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

package org.cloudfoundry.operations.useradmin;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class UnsetOrganizationRoleRequestTest {

    @Test
    void noOrganizationName() {
        assertThrows(IllegalStateException.class, () -> {
            UnsetOrganizationRoleRequest.builder()
                .organizationRole(OrganizationRole.AUDITOR)
                .username("test-username")
                .build();
        });
    }

    @Test
    void noOrganizationRole() {
        assertThrows(IllegalStateException.class, () -> {
            UnsetOrganizationRoleRequest.builder()
                .organizationName("test-organization")
                .username("test-username")
                .build();
        });
    }

    @Test
    void noUsername() {
        assertThrows(IllegalStateException.class, () -> {
            UnsetOrganizationRoleRequest.builder()
                .organizationName("test-organization")
                .organizationRole(OrganizationRole.BILLING_MANAGER)
                .build();
        });
    }

    @Test
    void valid() {
        UnsetOrganizationRoleRequest.builder()
                .organizationName("test-organization")
                .organizationRole(OrganizationRole.MANAGER)
                .username("test-username")
                .build();
    }
}

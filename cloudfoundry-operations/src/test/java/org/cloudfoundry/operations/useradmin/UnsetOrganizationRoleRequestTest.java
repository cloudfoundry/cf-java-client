/*
 * Copyright 2013-2020 the original author or authors.
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

import org.junit.Test;

public final class UnsetOrganizationRoleRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noOrganizationName() {
        UnsetOrganizationRoleRequest.builder()
            .organizationRole(OrganizationRole.AUDITOR)
            .username("test-username")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noOrganizationRole() {
        UnsetOrganizationRoleRequest.builder()
            .organizationName("test-organization")
            .username("test-username")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noUsername() {
        UnsetOrganizationRoleRequest.builder()
            .organizationName("test-organization")
            .organizationRole(OrganizationRole.BILLING_MANAGER)
            .build();
    }

    @Test
    public void valid() {
        UnsetOrganizationRoleRequest.builder()
            .organizationName("test-organization")
            .organizationRole(OrganizationRole.MANAGER)
            .username("test-username")
            .build();
    }

}

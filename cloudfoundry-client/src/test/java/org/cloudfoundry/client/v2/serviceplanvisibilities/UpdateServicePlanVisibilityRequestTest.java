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

package org.cloudfoundry.client.v2.serviceplanvisibilities;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class UpdateServicePlanVisibilityRequestTest {

    @Test
    void noOrganizationId() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    UpdateServicePlanVisibilityRequest.builder()
                            .servicePlanId("service-plan-id")
                            .servicePlanVisibilityId("test-service-plan-visibility-id")
                            .build();
                });
    }

    @Test
    void noServicePlanId() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    UpdateServicePlanVisibilityRequest.builder()
                            .organizationId("organization-id")
                            .servicePlanVisibilityId("test-service-plan-visibility-id")
                            .build();
                });
    }

    @Test
    void noServicePlanVisibilityId() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    UpdateServicePlanVisibilityRequest.builder()
                            .organizationId("organization-id")
                            .servicePlanId("service-plan-id")
                            .build();
                });
    }

    @Test
    void valid() {
        UpdateServicePlanVisibilityRequest.builder()
                .organizationId("organization-id")
                .servicePlanId("service-plan-id")
                .servicePlanVisibilityId("test-service-plan-visibility-id")
                .build();
    }
}

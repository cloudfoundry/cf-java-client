/*
 * Copyright 2013-2022 the original author or authors.
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

package org.cloudfoundry.client.v3.serviceplans;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class UpdateServicePlanVisibilityTest {

    @Test
    void valid() {
        UpdateServicePlanVisibilityRequest.builder()
                .servicePlanId("test-service-plan-id")
                .type(Visibility.ORGANIZATION)
                .organization(
                        Organization.builder()
                                .name("test-organization")
                                .guid("test-organization-id")
                                .build())
                .build();
    }

    @Test
    void invalid() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    UpdateServicePlanVisibilityRequest.builder()
                            .servicePlanId("test-service-plan-id")
                            .organization(
                                    Organization.builder()
                                            .name("test-organization")
                                            .guid("test-organization-id")
                                            .build())
                            .build();
                });
    }
}

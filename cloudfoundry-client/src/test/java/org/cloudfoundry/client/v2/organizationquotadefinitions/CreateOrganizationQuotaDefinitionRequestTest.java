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

package org.cloudfoundry.client.v2.organizationquotadefinitions;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class CreateOrganizationQuotaDefinitionRequestTest {

    @Test
    void noInstanceMemoryLimit() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateOrganizationQuotaDefinitionRequest.builder()
                            .memoryLimit(1024)
                            .name("test-quota-definition-name")
                            .nonBasicServicesAllowed(false)
                            .totalRoutes(-1)
                            .totalServices(-1)
                            .build();
                });
    }

    @Test
    void noMemoryLimit() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateOrganizationQuotaDefinitionRequest.builder()
                            .instanceMemoryLimit(1024)
                            .name("test-quota-definition-name")
                            .nonBasicServicesAllowed(false)
                            .totalRoutes(-1)
                            .totalServices(-1)
                            .build();
                });
    }

    @Test
    void noName() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateOrganizationQuotaDefinitionRequest.builder()
                            .instanceMemoryLimit(1024)
                            .memoryLimit(1024)
                            .nonBasicServicesAllowed(false)
                            .totalRoutes(-1)
                            .totalServices(-1)
                            .build();
                });
    }

    @Test
    void noNonBasicServicesAllowed() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateOrganizationQuotaDefinitionRequest.builder()
                            .instanceMemoryLimit(1024)
                            .memoryLimit(1024)
                            .name("test-quota-definition-name")
                            .totalRoutes(-1)
                            .totalServices(-1)
                            .build();
                });
    }

    @Test
    void noTotalRoutes() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateOrganizationQuotaDefinitionRequest.builder()
                            .instanceMemoryLimit(1024)
                            .memoryLimit(1024)
                            .name("test-quota-definition-name")
                            .totalServices(-1)
                            .build();
                });
    }

    @Test
    void noTotalServices() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    CreateOrganizationQuotaDefinitionRequest.builder()
                            .instanceMemoryLimit(1024)
                            .memoryLimit(1024)
                            .name("test-quota-definition-name")
                            .nonBasicServicesAllowed(false)
                            .totalRoutes(-1)
                            .build();
                });
    }

    @Test
    void valid() {
        CreateOrganizationQuotaDefinitionRequest.builder()
                .instanceMemoryLimit(1024)
                .memoryLimit(1024)
                .name("test-quota-definition-name")
                .nonBasicServicesAllowed(false)
                .totalRoutes(-1)
                .totalServices(-1)
                .build();
    }
}

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

package org.cloudfoundry.client.v2.organizationquotadefinitions;

import org.junit.Test;

public final class CreateOrganizationQuotaDefinitionRequestTest {

    @Test(expected = IllegalStateException.class)
    public void noInstanceMemoryLimit() {
        CreateOrganizationQuotaDefinitionRequest.builder()
            .memoryLimit(1024)
            .name("test-quota-definition-name")
            .nonBasicServicesAllowed(false)
            .totalRoutes(-1)
            .totalServices(-1)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noMemoryLimit() {
        CreateOrganizationQuotaDefinitionRequest.builder()
            .instanceMemoryLimit(1024)
            .name("test-quota-definition-name")
            .nonBasicServicesAllowed(false)
            .totalRoutes(-1)
            .totalServices(-1)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noName() {
        CreateOrganizationQuotaDefinitionRequest.builder()
            .instanceMemoryLimit(1024)
            .memoryLimit(1024)
            .nonBasicServicesAllowed(false)
            .totalRoutes(-1)
            .totalServices(-1)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noNonBasicServicesAllowed() {
        CreateOrganizationQuotaDefinitionRequest.builder()
            .instanceMemoryLimit(1024)
            .memoryLimit(1024)
            .name("test-quota-definition-name")
            .totalRoutes(-1)
            .totalServices(-1)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noTotalRoutes() {
        CreateOrganizationQuotaDefinitionRequest.builder()
            .instanceMemoryLimit(1024)
            .memoryLimit(1024)
            .name("test-quota-definition-name")
            .totalServices(-1)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noTotalServices() {
        CreateOrganizationQuotaDefinitionRequest.builder()
            .instanceMemoryLimit(1024)
            .memoryLimit(1024)
            .name("test-quota-definition-name")
            .nonBasicServicesAllowed(false)
            .totalRoutes(-1)
            .build();
    }

    @Test
    public void valid() {
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

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

package org.cloudfoundry.client.v2.organizationquotadefinitions;

import org.cloudfoundry.ValidationResult;
import org.junit.Test;

import java.util.Arrays;

import static org.cloudfoundry.ValidationResult.Status.INVALID;
import static org.cloudfoundry.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public final class CreateOrganizationQuotaDefinitionRequestTest {

    @Test
    public void isValidMax() {
        ValidationResult result = CreateOrganizationQuotaDefinitionRequest.builder()
            .applicationInstanceLimit(-1)
            .applicationTaskLimit(-1)
            .instanceMemoryLimit(1024)
            .memoryLimit(1024)
            .name("test-quota-definition-name")
            .nonBasicServicesAllowed(false)
            .totalPrivateDomains(-1)
            .totalRoutes(-1)
            .totalServiceKeys(-1)
            .totalServices(-1)
            .trialDatabaseAllowed(false)
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidMin() {
        ValidationResult result = CreateOrganizationQuotaDefinitionRequest.builder()
            .instanceMemoryLimit(1024)
            .memoryLimit(1024)
            .name("test-quota-definition-name")
            .nonBasicServicesAllowed(false)
            .totalRoutes(-1)
            .totalServices(-1)
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidNoInstanceMemoryLimit() {
        ValidationResult result = CreateOrganizationQuotaDefinitionRequest.builder()
            .applicationInstanceLimit(-1)
            .applicationTaskLimit(-1)
            .memoryLimit(1024)
            .name("test-quota-definition-name")
            .nonBasicServicesAllowed(false)
            .totalPrivateDomains(-1)
            .totalRoutes(-1)
            .totalServiceKeys(-1)
            .totalServices(-1)
            .trialDatabaseAllowed(false)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("instance memory limit must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoMemoryLimit() {
        ValidationResult result = CreateOrganizationQuotaDefinitionRequest.builder()
            .applicationInstanceLimit(-1)
            .applicationTaskLimit(-1)
            .instanceMemoryLimit(1024)
            .name("test-quota-definition-name")
            .nonBasicServicesAllowed(false)
            .totalPrivateDomains(-1)
            .totalRoutes(-1)
            .totalServiceKeys(-1)
            .totalServices(-1)
            .trialDatabaseAllowed(false)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("memory limit must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoName() {
        ValidationResult result = CreateOrganizationQuotaDefinitionRequest.builder()
            .applicationInstanceLimit(-1)
            .applicationTaskLimit(-1)
            .instanceMemoryLimit(1024)
            .memoryLimit(1024)
            .nonBasicServicesAllowed(false)
            .totalPrivateDomains(-1)
            .totalRoutes(-1)
            .totalServiceKeys(-1)
            .totalServices(-1)
            .trialDatabaseAllowed(false)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("name must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoNonBasicServicesAllowed() {
        ValidationResult result = CreateOrganizationQuotaDefinitionRequest.builder()
            .applicationInstanceLimit(-1)
            .applicationTaskLimit(-1)
            .instanceMemoryLimit(1024)
            .memoryLimit(1024)
            .name("test-quota-definition-name")
            .totalPrivateDomains(-1)
            .totalRoutes(-1)
            .totalServiceKeys(-1)
            .totalServices(-1)
            .trialDatabaseAllowed(false)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("non basic services allowed must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoTotalRoutes() {
        ValidationResult result = CreateOrganizationQuotaDefinitionRequest.builder()
            .applicationInstanceLimit(-1)
            .applicationTaskLimit(-1)
            .instanceMemoryLimit(1024)
            .memoryLimit(1024)
            .name("test-quota-definition-name")
            .nonBasicServicesAllowed(false)
            .totalPrivateDomains(-1)
            .totalServiceKeys(-1)
            .totalServices(-1)
            .trialDatabaseAllowed(false)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("total routes must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoTotalServices() {
        ValidationResult result = CreateOrganizationQuotaDefinitionRequest.builder()
            .applicationInstanceLimit(-1)
            .applicationTaskLimit(-1)
            .instanceMemoryLimit(1024)
            .memoryLimit(1024)
            .name("test-quota-definition-name")
            .nonBasicServicesAllowed(false)
            .totalPrivateDomains(-1)
            .totalRoutes(-1)
            .totalServiceKeys(-1)
            .trialDatabaseAllowed(false)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("total services must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNothingSpecified() {
        ValidationResult result = CreateOrganizationQuotaDefinitionRequest.builder()
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals(Arrays.asList(
            "instance memory limit must be specified",
            "memory limit must be specified",
            "name must be specified",
            "non basic services allowed must be specified",
            "total routes must be specified",
            "total services must be specified"),
            result.getMessages());
    }

}

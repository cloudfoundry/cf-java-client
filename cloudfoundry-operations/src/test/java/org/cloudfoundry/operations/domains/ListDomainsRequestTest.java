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

package org.cloudfoundry.operations.domains;

import org.cloudfoundry.ValidationResult;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.cloudfoundry.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public final class ListDomainsRequestTest {

    @Test
    public void isValid() {
        ValidationResult result = ListDomainsRequest.builder()
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidNameAndOwnerOrganization() {
        ValidationResult result = ListDomainsRequest.builder()
            .name("test-domain")
            .owningOrganizationId("test-owner-organization-id")
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidNamesAndOwnerOrganizations() {
        List<String> names = new ArrayList<>();
        names.add("test-domain1");
        names.add("test-domain2");

        List<String> owningOrganizationIds = new ArrayList<>();
        owningOrganizationIds.add("test-owner-organization-id1");
        owningOrganizationIds.add("test-owner-organization-id2");

        ValidationResult result = ListDomainsRequest.builder()
            .names(names)
            .owningOrganizationIds(owningOrganizationIds)
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }
}

/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.v3.application;

import org.cloudfoundry.client.ValidationResult;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public final class ListApplicationsRequestTest {

    @Test
    public void test() {
        ListApplicationsRequest request = new ListApplicationsRequest()
                .withId("test-id-1")
                .withIds(Collections.singletonList("test-id-2"))
                .withName("test-name-1")
                .withNames(Collections.singletonList("test-name-2"))
                .withOrganizationId("test-organization-id-1")
                .withOrganizationIds(Collections.singletonList("test-organization-id-2"))
                .withSpaceId("test-space-id-1")
                .withSpaceIds(Collections.singletonList("test-space-id-2"));

        assertEquals(Arrays.asList("test-id-1", "test-id-2"), request.getIds());
        assertEquals(Arrays.asList("test-name-1", "test-name-2"), request.getNames());
        assertEquals(Arrays.asList("test-organization-id-1", "test-organization-id-2"), request.getOrganizationIds());
        assertEquals(Arrays.asList("test-space-id-1", "test-space-id-2"), request.getSpaceIds());
    }

    @Test
    public void isValid() {
        ValidationResult result = new ListApplicationsRequest()
                .isValid();

        assertEquals(ValidationResult.Status.VALID, result.getStatus());
    }

    @Test
    public void isValidInvalidPaginatedRequest() {
        ValidationResult result = new ListApplicationsRequest()
                .withPage(0)
                .isValid();

        assertEquals(ValidationResult.Status.INVALID, result.getStatus());
        assertEquals("page must be greater than or equal to 1", result.getMessages().get(0));
    }
}

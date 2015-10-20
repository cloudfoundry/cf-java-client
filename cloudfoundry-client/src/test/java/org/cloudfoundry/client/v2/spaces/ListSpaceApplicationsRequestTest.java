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

package org.cloudfoundry.client.v2.spaces;

import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v2.PaginatedRequest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public final class ListSpaceApplicationsRequestTest {

    @Test
    public void test() {
        ListSpaceApplicationsRequest request = new ListSpaceApplicationsRequest()
                .withDiego(true)
                .withDiegos(Collections.singletonList(false))
                .withName("test-name-1")
                .withNames(Collections.singletonList("test-name-2"))
                .withOrganizationId("test-organization-id-1")
                .withOrganizationIds(Collections.singletonList("test-organization-id-2"))
                .withSpaceId("test-space-id-1")
                .withSpaceIds(Collections.singletonList("test-space-id-2"))
                .withStackId("test-stack-id-1")
                .withStackIds(Collections.singletonList("test-stack-id-2"))
                .withOrderDirection(PaginatedRequest.OrderDirection.ASC)
                .withPage(-1)
                .withResultsPerPage(-2);

        assertEquals(Arrays.asList(true, false), request.getDiegos());
        assertEquals(Arrays.asList("test-name-1", "test-name-2"), request.getNames());
        assertEquals(Arrays.asList("test-organization-id-1", "test-organization-id-2"), request.getOrganizationIds());
        assertEquals(Arrays.asList("test-space-id-1", "test-space-id-2"), request.getSpaceIds());
        assertEquals(Arrays.asList("test-stack-id-1", "test-stack-id-2"), request.getStackIds());
        assertEquals(PaginatedRequest.OrderDirection.ASC, request.getOrderDirection());
        assertEquals(Integer.valueOf(-1), request.getPage());
        assertEquals(Integer.valueOf(-2), request.getResultsPerPage());
    }

    @Test
    public void isValid() {
        assertEquals(ValidationResult.Status.VALID, new ListSpaceApplicationsRequest().withId("test-id")
                .isValid().getStatus());
    }

    @Test
    public void isValidNoId() {
        assertEquals(ValidationResult.Status.INVALID, new ListSpaceApplicationsRequest().isValid().getStatus());
    }

}

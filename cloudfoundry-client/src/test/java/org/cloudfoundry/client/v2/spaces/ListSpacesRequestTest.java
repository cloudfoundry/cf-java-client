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
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public final class ListSpacesRequestTest {

    @Test
    public void test() {
        ListSpacesRequest request = new ListSpacesRequest()
                .withApplicationId("test-application-id-1")
                .withApplicationIds(Collections.singletonList("test-application-id-2"))
                .withDeveloperId("test-developer-id-1")
                .withDeveloperIds(Collections.singletonList("test-developer-id-2"))
                .withName("test-name-1")
                .withNames(Collections.singletonList("test-name-2"))
                .withOrganizationId("test-organization-id-1")
                .withOrganizationIds(Collections.singletonList("test-organization-id-2"))
                .withOrderDirection(PaginatedRequest.OrderDirection.ASC)
                .withPage(-1)
                .withResultsPerPage(-2);

        assertEquals(Arrays.asList("test-application-id-1", "test-application-id-2"), request.getApplicationIds());
        assertEquals(Arrays.asList("test-developer-id-1", "test-developer-id-2"), request.getDeveloperIds());
        assertEquals(Arrays.asList("test-name-1", "test-name-2"), request.getNames());
        assertEquals(Arrays.asList("test-organization-id-1", "test-organization-id-2"), request.getOrganizationIds());
        Assert.assertEquals(PaginatedRequest.OrderDirection.ASC, request.getOrderDirection());
        assertEquals(Integer.valueOf(-1), request.getPage());
        assertEquals(Integer.valueOf(-2), request.getResultsPerPage());
    }

    @Test
    public void isValid() {
        assertEquals(ValidationResult.Status.VALID, new ListSpacesRequest().isValid().getStatus());
    }

}

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

package org.cloudfoundry.client.v2.space;

import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v2.PaginatedRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public final class ListSpacesRequestTest {

    @Test
    public void name() {
        ListSpacesRequest request = new ListSpacesRequest()
                .filterByApplicationId("test-application-id")
                .filterByDeveloperId("test-developer-id")
                .filterByName("test-name")
                .filterByOrganizationId("test-organization-id")
                .withOrderDirection(PaginatedRequest.OrderDirection.ASC)
                .withPage(-1)
                .withResultsPerPage(-2);

        assertEquals(Collections.singletonList("test-application-id"), request.getApplicationIds());
        assertEquals(Collections.singletonList("test-developer-id"), request.getDeveloperIds());
        assertEquals(Collections.singletonList("test-name"), request.getNames());
        assertEquals(Collections.singletonList("test-organization-id"), request.getOrganizationIds());
        Assert.assertEquals(PaginatedRequest.OrderDirection.ASC, request.getOrderDirection());
        assertEquals(Integer.valueOf(-1), request.getPage());
        assertEquals(Integer.valueOf(-2), request.getResultsPerPage());
    }

    @Test
    public void isValid() {
        assertEquals(ValidationResult.Status.VALID, new ListSpacesRequest().isValid().getStatus());
    }

}

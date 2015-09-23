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

package org.cloudfoundry.client.v2.organizations;

import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v2.PaginatedRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public final class ListOrganizationsRequestTest {

    @Test
    public void name() {
        ListOrganizationsRequest request = new ListOrganizationsRequest()
                .withAuditorId("test-auditor-id-1")
                .withAuditorIds(Collections.singletonList("test-auditor-id-2"))
                .withBillingManagerId("test-billing-manager-id-1")
                .withBillingManagerIds(Collections.singletonList("test-billing-manager-id-2"))
                .withManagerId("test-manager-id-1")
                .withManagerIds(Collections.singletonList("test-manager-id-2"))
                .withName("test-name-1")
                .withNames(Collections.singletonList("test-name-2"))
                .withSpaceId("test-space-id-1")
                .withSpaceIds(Collections.singletonList("test-space-id-2"))
                .withStatus("test-status-1")
                .withStatuses(Collections.singletonList("test-status-2"))
                .withUserId("test-user-id-1")
                .withUserIds(Collections.singletonList("test-user-id-2"))
                .withOrderDirection(PaginatedRequest.OrderDirection.ASC)
                .withPage(-1)
                .withResultsPerPage(-2);

        assertEquals(Arrays.asList("test-auditor-id-1", "test-auditor-id-2"), request.getAuditorIds());
        assertEquals(Arrays.asList("test-billing-manager-id-1", "test-billing-manager-id-2"), request
                .getBillingManagerIds());
        assertEquals(Arrays.asList("test-manager-id-1", "test-manager-id-2"), request.getManagerIds());
        assertEquals(Arrays.asList("test-name-1", "test-name-2"), request.getNames());
        assertEquals(Arrays.asList("test-space-id-1", "test-space-id-2"), request.getSpaceIds());
        assertEquals(Arrays.asList("test-status-1", "test-status-2"), request.getStatuses());
        assertEquals(Arrays.asList("test-user-id-1", "test-user-id-2"), request.getUserIds());
        Assert.assertEquals(PaginatedRequest.OrderDirection.ASC, request.getOrderDirection());
        assertEquals(Integer.valueOf(-1), request.getPage());
        assertEquals(Integer.valueOf(-2), request.getResultsPerPage());
    }

    @Test
    public void isValid() {
        assertEquals(ValidationResult.Status.VALID, new ListOrganizationsRequest().isValid().getStatus());
    }

}

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

package org.cloudfoundry.client.v2.serviceinstances;

import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v2.PaginatedRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public final class ListServiceInstancesRequestTest {

    @Test
    public void test() {
        ListServiceInstancesRequest request = new ListServiceInstancesRequest()
                .withGatewayName("test-gateway-name-id-1")
                .withGatewayNames(Collections.singletonList("test-gateway-name-id-2"))
                .withName("test-name-1")
                .withNames(Collections.singletonList("test-name-2"))
                .withOrganizationId("test-organization-id-1")
                .withOrganizationIds(Collections.singletonList("test-organization-id-2"))
                .withServiceKeyId("test-service-key-id-1")
                .withServiceKeyIds(Collections.singletonList("test-service-key-id-2"))
                .withServiceBindingId("test-service-binding-id-1")
                .withServiceBindingIds(Collections.singletonList("test-service-binding-id-2"))
                .withServicePlanId("test-service-plan-id-1")
                .withServicePlanIds(Collections.singletonList("test-service-plan-id-2"))
                .withSpaceId("test-space-id-1")
                .withSpaceIds(Collections.singletonList("test-space-id-2"))
                .withOrderDirection(PaginatedRequest.OrderDirection.ASC)
                .withPage(-1)
                .withResultsPerPage(-2);

        assertEquals(Arrays.asList("test-gateway-name-id-1", "test-gateway-name-id-2"), request.getGatewayNames());
        assertEquals(Arrays.asList("test-name-1", "test-name-2"), request.getNames());
        assertEquals(Arrays.asList("test-organization-id-1", "test-organization-id-2"), request.getOrganizationIds());
        assertEquals(Arrays.asList("test-service-key-id-1", "test-service-key-id-2"), request.getServiceKeyIds());
        assertEquals(Arrays.asList("test-service-binding-id-1", "test-service-binding-id-2"),
                request.getServiceBindingIds());
        assertEquals(Arrays.asList("test-service-plan-id-1", "test-service-plan-id-2"), request.getServicePlanIds());
        assertEquals(Arrays.asList("test-space-id-1", "test-space-id-2"), request.getSpaceIds());
        Assert.assertEquals(PaginatedRequest.OrderDirection.ASC, request.getOrderDirection());
        assertEquals(Integer.valueOf(-1), request.getPage());
        assertEquals(Integer.valueOf(-2), request.getResultsPerPage());
    }

    @Test
    public void isValid() {
        assertEquals(ValidationResult.Status.VALID, new ListServiceInstancesRequest().isValid().getStatus());
    }
}

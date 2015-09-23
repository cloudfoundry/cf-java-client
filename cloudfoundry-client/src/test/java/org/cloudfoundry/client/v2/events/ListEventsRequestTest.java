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

package org.cloudfoundry.client.v2.events;

import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v2.PaginatedRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public final class ListEventsRequestTest {

    @Test
    public void name() {
        ListEventsRequest request = new ListEventsRequest()
                .withActee("test-actee-1")
                .withActees(Collections.singletonList("test-actee-2"))
                .withTimestamp("test-timestamp-1")
                .withTimestamps(Collections.singletonList("test-timestamp-2"))
                .withType("test-type-1")
                .withTypes(Collections.singletonList("test-type-2"))
                .withOrderDirection(PaginatedRequest.OrderDirection.ASC)
                .withPage(-1)
                .withResultsPerPage(-2);

        assertEquals(Arrays.asList("test-actee-1", "test-actee-2"), request.getActees());
        assertEquals(Arrays.asList("test-timestamp-1", "test-timestamp-2"), request.getTimestamps());
        assertEquals(Arrays.asList("test-type-1", "test-type-2"), request.getTypes());
        Assert.assertEquals(PaginatedRequest.OrderDirection.ASC, request.getOrderDirection());
        assertEquals(Integer.valueOf(-1), request.getPage());
        assertEquals(Integer.valueOf(-2), request.getResultsPerPage());
    }

    @Test
    public void isValid() {
        assertEquals(ValidationResult.Status.VALID, new ListEventsRequest().isValid().getStatus());
    }

}

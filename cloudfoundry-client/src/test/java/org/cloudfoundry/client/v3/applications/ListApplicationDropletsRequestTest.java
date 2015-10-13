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

package org.cloudfoundry.client.v3.applications;

import org.cloudfoundry.client.ValidationResult;
import org.junit.Test;

import static org.cloudfoundry.client.ValidationResult.Status.INVALID;
import static org.cloudfoundry.client.ValidationResult.Status.VALID;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public final class ListApplicationDropletsRequestTest {

    @Test
    public void test() {
        ListApplicationDropletsRequest request = new ListApplicationDropletsRequest()
                .withId("test-id")
                .withState("test-state-1")
                .withStates(new String[]{"test-state-2", "test-state-3"});

        assertEquals("test-id", request.getId());
        assertArrayEquals(new String[]{"test-state-1", "test-state-2", "test-state-3"}, request.getState());
    }

    @Test
    public void isValid() {
        ValidationResult result = new ListApplicationDropletsRequest()
                .withId("test-id")
                .withState("test-state-1")
                .withStates(new String[]{"test-state-2", "test-state-3"})
                .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidNoId() {
        ValidationResult result = new ListApplicationDropletsRequest()
                .withState("test-state")
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("id must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidInvalidPaginatedRequest() {
        ValidationResult result = new ListApplicationDropletsRequest()
                .withPage(0)
                .isValid();

        assertEquals(ValidationResult.Status.INVALID, result.getStatus());
        assertEquals("page must be greater than or equal to 1", result.getMessages().get(0));
    }

}

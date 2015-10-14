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

package org.cloudfoundry.client.v3.processes;

import org.cloudfoundry.client.ValidationResult;
import org.junit.Test;

import static org.cloudfoundry.client.ValidationResult.Status.INVALID;
import static org.cloudfoundry.client.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public class DeleteInstanceRequestTest {

    @Test
    public void test() throws Exception {
        DeleteInstanceRequest request = new DeleteInstanceRequest()
                .withId("test-id")
                .withIndex("test-index");

        assertEquals("test-id", request.getId());
        assertEquals("test-index", request.getIndex());
    }

    @Test
    public void isValid() throws Exception {
        ValidationResult result = new DeleteInstanceRequest()
                .withId("test-id")
                .withIndex("test-index")
                .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidNoId() {
        ValidationResult result = new DeleteInstanceRequest()
                .withIndex("test-index")
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("id must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoIndex() {
        ValidationResult result = new DeleteInstanceRequest()
                .withId("test-id")
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("index must be specified", result.getMessages().get(0));
    }

}

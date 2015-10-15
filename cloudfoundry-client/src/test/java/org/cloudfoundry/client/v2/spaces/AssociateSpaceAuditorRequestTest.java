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
import org.junit.Test;

import static org.cloudfoundry.client.ValidationResult.Status.INVALID;
import static org.cloudfoundry.client.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public final class AssociateSpaceAuditorRequestTest {

    @Test
    public void test() {
        AssociateSpaceAuditorRequest request = new AssociateSpaceAuditorRequest()
                .withId("test-id")
                .withAuditorId("test-auditor-id");

        assertEquals("test-id", request.getId());
        assertEquals("test-auditor-id", request.getAuditorId());
    }

    @Test
    public void isValid() {
        ValidationResult result = new AssociateSpaceAuditorRequest()
                .withId("test-id")
                .withAuditorId("test-auditor-id")
                .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidNoId() {
        ValidationResult result = new AssociateSpaceAuditorRequest()
                .withAuditorId("test-auditor-id")
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("id must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoAuditorId() {
        ValidationResult result = new AssociateSpaceAuditorRequest()
                .withId("test-id")
                .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("auditorId must be specified", result.getMessages().get(0));
    }

}

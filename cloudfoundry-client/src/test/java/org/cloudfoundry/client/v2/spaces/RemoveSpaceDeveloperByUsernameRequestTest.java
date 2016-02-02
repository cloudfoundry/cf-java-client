/*
 * Copyright 2013-2016 the original author or authors.
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

public final class RemoveSpaceDeveloperByUsernameRequestTest {

    @Test
    public void isValid() {
        ValidationResult result = RemoveSpaceDeveloperByUsernameRequest.builder()
            .spaceId("test-space-id")
            .username("test-username")
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidNoId() {
        ValidationResult result = RemoveSpaceDeveloperByUsernameRequest.builder()
            .username("test-username")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("space id must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoUsername() {
        ValidationResult result = RemoveSpaceDeveloperByUsernameRequest.builder()
            .spaceId("test-space-id")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("username must be specified", result.getMessages().get(0));
    }

}

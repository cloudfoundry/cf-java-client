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

package org.cloudfoundry.doppler;

import org.cloudfoundry.ValidationResult;
import org.junit.Test;

import static org.cloudfoundry.ValidationResult.Status.INVALID;
import static org.cloudfoundry.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public final class ErrorTest {

    @Test
    public void isValid() {
        ValidationResult result = Error.builder()
            .code(0)
            .message("test-message")
            .source("test-source")
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidNoCode() {
        ValidationResult result = Error.builder()
            .message("test-message")
            .source("test-source")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("code must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoMessage() {
        ValidationResult result = Error.builder()
            .code(0)
            .source("test-source")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("message must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoSource() {
        ValidationResult result = Error.builder()
            .code(0)
            .message("test-message")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("source must be specified", result.getMessages().get(0));
    }

}

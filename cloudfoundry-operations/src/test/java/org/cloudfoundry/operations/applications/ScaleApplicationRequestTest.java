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

package org.cloudfoundry.operations.applications;

import org.cloudfoundry.ValidationResult;
import org.junit.Test;

import static org.cloudfoundry.ValidationResult.Status.INVALID;
import static org.cloudfoundry.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public final class ScaleApplicationRequestTest {

    @Test
    public void isValid() {
        ValidationResult result = ScaleApplicationRequest.builder()
            .name("test-name")
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidBadDiskLimit1() {
        ValidationResult result = ScaleApplicationRequest.builder()
            .name("test-name")
            .diskLimit("94x")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("disk limit (94x) specified incorrectly", result.getMessages().get(0));
    }

    @Test
    public void isValidBadDiskLimit2() {
        ValidationResult result = ScaleApplicationRequest.builder()
            .name("test-name")
            .diskLimit("00g")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("disk limit (00g) specified incorrectly", result.getMessages().get(0));
    }

    @Test
    public void isValidBadDiskLimit3() {
        ValidationResult result = ScaleApplicationRequest.builder()
            .name("test-name")
            .diskLimit("-3g")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("disk limit (-3g) specified incorrectly", result.getMessages().get(0));
    }

    @Test
    public void isValidBadDiskLimit4() {
        ValidationResult result = ScaleApplicationRequest.builder()
            .name("test-name")
            .diskLimit("yahoog")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("disk limit (yahoog) specified incorrectly", result.getMessages().get(0));
    }

    @Test
    public void isValidGoodDiskLimit() {
        ScaleApplicationRequest request = ScaleApplicationRequest.builder()
            .name("test-name")
            .diskLimit("100g")
            .build();

        ValidationResult result = request.isValid();

        assertEquals(VALID, result.getStatus());
        assertEquals(Integer.valueOf(100 * 1024), request.getDiskLimit());
    }

    @Test
    public void isValidNoName() {
        ValidationResult result = ScaleApplicationRequest.builder()
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("name must be specified", result.getMessages().get(0));
    }

}

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

public final class HttpStartTest {

    @Test
    public void isValid() {
        ValidationResult result = ContainerMetric.builder()
            .applicationId("test-application-id")
            .cpuPercentage(0.0)
            .diskBytes(0L)
            .instanceIndex(0)
            .memoryBytes(0L)
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidNoApplicationId() {
        ValidationResult result = ContainerMetric.builder()
            .cpuPercentage(0.0)
            .diskBytes(0L)
            .instanceIndex(0)
            .memoryBytes(0L)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("application id must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoCpuPercentage() {
        ValidationResult result = ContainerMetric.builder()
            .applicationId("test-application-id")
            .diskBytes(0L)
            .instanceIndex(0)
            .memoryBytes(0L)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("cpu percentage must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoDiskBytes() {
        ValidationResult result = ContainerMetric.builder()
            .applicationId("test-application-id")
            .cpuPercentage(0.0)
            .instanceIndex(0)
            .memoryBytes(0L)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("disk bytes must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoInstanceIndex() {
        ValidationResult result = ContainerMetric.builder()
            .applicationId("test-application-id")
            .cpuPercentage(0.0)
            .diskBytes(0L)
            .memoryBytes(0L)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("instance index must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoMemoryBytes() {
        ValidationResult result = ContainerMetric.builder()
            .applicationId("test-application-id")
            .cpuPercentage(0.0)
            .diskBytes(0L)
            .instanceIndex(0)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("memory bytes must be specified", result.getMessages().get(0));
    }

}

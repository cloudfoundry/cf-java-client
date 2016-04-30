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

public final class ValueMetricTest {

    @Test
    public void isValid() {
        ValidationResult result = ValueMetric.builder()
            .name("test-name")
            .unit("test-unit")
            .value(0.0)
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidNoName() {
        ValidationResult result = ValueMetric.builder()
            .unit("test-unit")
            .value(0.0)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("name must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoUnit() {
        ValidationResult result = ValueMetric.builder()
            .name("test-name")
            .value(0.0)
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("unit must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoValue() {
        ValidationResult result = ValueMetric.builder()
            .name("test-name")
            .unit("test-unit")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("value must be specified", result.getMessages().get(0));
    }

}

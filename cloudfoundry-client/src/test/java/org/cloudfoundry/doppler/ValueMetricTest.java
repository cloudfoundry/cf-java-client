/*
 * Copyright 2013-2021 the original author or authors.
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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class ValueMetricTest {

    @Test
    void dropsonde() {
        ValueMetric.from(new org.cloudfoundry.dropsonde.events.ValueMetric.Builder()
            .name("test-name")
            .unit("test-unit")
            .value(0.0)
            .build());
    }

    @Test
    void noName() {
        assertThrows(IllegalStateException.class, () -> {
            ValueMetric.builder()
                .unit("test-unit")
                .value(0.0)
                .build();
        });
    }

    @Test
    void noUnit() {
        assertThrows(IllegalStateException.class, () -> {
            ValueMetric.builder()
                .name("test-name")
                .value(0.0)
                .build();
        });
    }

    @Test
    void noValue() {
        assertThrows(IllegalStateException.class, () -> {
            ValueMetric.builder()
                .name("test-name")
                .unit("test-unit")
                .build();
        });
    }

    @Test
    void valid() {
        ValueMetric.builder()
            .name("test-name")
            .unit("test-unit")
            .value(0.0)
            .build();
    }

}

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

final class ContainerMetricTest {

    @Test
    void dropsonde() {
        ContainerMetric.from(new org.cloudfoundry.dropsonde.events.ContainerMetric.Builder()
            .applicationId("test-application-id")
            .cpuPercentage(0.0)
            .diskBytes(0L)
            .instanceIndex(0)
            .memoryBytes(0L)
            .build());
    }

    @Test
    void noApplicationId() {
        assertThrows(IllegalStateException.class, () -> {
            ContainerMetric.builder()
                .cpuPercentage(0.0)
                .diskBytes(0L)
                .instanceIndex(0)
                .memoryBytes(0L)
                .build();
        });
    }

    @Test
    void noCpuPercentage() {
        assertThrows(IllegalStateException.class, () -> {
            ContainerMetric.builder()
                .applicationId("test-application-id")
                .diskBytes(0L)
                .instanceIndex(0)
                .memoryBytes(0L)
                .build();
        });
    }

    @Test
    void noDiskBytes() {
        assertThrows(IllegalStateException.class, () -> {
            ContainerMetric.builder()
                .applicationId("test-application-id")
                .cpuPercentage(0.0)
                .instanceIndex(0)
                .memoryBytes(0L)
                .build();
        });
    }

    @Test
    void noInstanceIndex() {
        assertThrows(IllegalStateException.class, () -> {
            ContainerMetric.builder()
                .applicationId("test-application-id")
                .cpuPercentage(0.0)
                .diskBytes(0L)
                .memoryBytes(0L)
                .build();
        });
    }

    @Test
    void noMemoryBytes() {
        assertThrows(IllegalStateException.class, () -> {
            ContainerMetric.builder()
                .applicationId("test-application-id")
                .cpuPercentage(0.0)
                .diskBytes(0L)
                .instanceIndex(0)
                .build();
        });
    }

    @Test
    void valid() {
        ContainerMetric.builder()
                .applicationId("test-application-id")
                .cpuPercentage(0.0)
                .diskBytes(0L)
                .instanceIndex(0)
                .memoryBytes(0L)
                .build();
    }
}

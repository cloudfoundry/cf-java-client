/*
 * Copyright 2013-2020 the original author or authors.
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

import org.junit.Test;

public final class ContainerMetricTest {

    @Test
    public void dropsonde() {
        ContainerMetric.from(new org.cloudfoundry.dropsonde.events.ContainerMetric.Builder()
            .applicationId("test-application-id")
            .cpuPercentage(0.0)
            .diskBytes(0L)
            .instanceIndex(0)
            .memoryBytes(0L)
            .build());
    }

    @Test(expected = IllegalStateException.class)
    public void noApplicationId() {
        ContainerMetric.builder()
            .cpuPercentage(0.0)
            .diskBytes(0L)
            .instanceIndex(0)
            .memoryBytes(0L)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noCpuPercentage() {
        ContainerMetric.builder()
            .applicationId("test-application-id")
            .diskBytes(0L)
            .instanceIndex(0)
            .memoryBytes(0L)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noDiskBytes() {
        ContainerMetric.builder()
            .applicationId("test-application-id")
            .cpuPercentage(0.0)
            .instanceIndex(0)
            .memoryBytes(0L)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noInstanceIndex() {
        ContainerMetric.builder()
            .applicationId("test-application-id")
            .cpuPercentage(0.0)
            .diskBytes(0L)
            .memoryBytes(0L)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noMemoryBytes() {
        ContainerMetric.builder()
            .applicationId("test-application-id")
            .cpuPercentage(0.0)
            .diskBytes(0L)
            .instanceIndex(0)
            .build();
    }

    @Test
    public void valid() {
        ContainerMetric.builder()
            .applicationId("test-application-id")
            .cpuPercentage(0.0)
            .diskBytes(0L)
            .instanceIndex(0)
            .memoryBytes(0L)
            .build();
    }

}

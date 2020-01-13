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

import org.immutables.value.Value;

import java.util.Objects;

/**
 * Indicates the value of a metric at an instant in time
 */
@Value.Immutable
abstract class _ValueMetric {

    public static ValueMetric from(org.cloudfoundry.dropsonde.events.ValueMetric dropsonde) {
        Objects.requireNonNull(dropsonde, "dropsonde");

        return ValueMetric.builder()
            .name(dropsonde.name)
            .unit(dropsonde.unit)
            .value(dropsonde.value)
            .build();
    }

    /**
     * The name of the metric. Must be consistent for downstream consumers to associate events semantically.
     */
    abstract String getName();

    /**
     * The unit of the metric. Please see <a href="http://metrics20.org/spec/#units">http://metrics20.org/spec/#units</a> for ideas; SI units/prefixes are recommended where applicable. Should be
     * consistent for the life of the metric (consumers are expected to report, but not interpret, prefixes).
     */
    abstract String getUnit();

    /**
     * The value at the time of event emission
     */
    abstract Double value();

}

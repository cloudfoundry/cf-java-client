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

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.Validatable;
import org.cloudfoundry.ValidationResult;

import java.util.Optional;

/**
 * Indicates the value of a metric at an instant in time
 */
@Data
public final class ValueMetric implements Event, Validatable {

    /**
     * The name of the metric. Must be consistent for downstream consumers to associate events semantically.
     *
     * @param name the name of the metric
     * @return the name of the metric
     */
    private final String name;

    /**
     * The unit of the metric. Please see <a href="http://metrics20.org/spec/#units">http://metrics20.org/spec/#units</a> for ideas; SI units/prefixes are recommended where applicable. Should be
     * consistent for the life of the metric (consumers are expected to report, but not interpret, prefixes).
     *
     * @param unit the unit of the metric
     * @return the unit of the metric
     */
    private final String unit;

    /**
     * The value at the time of event emission
     *
     * @param value the value at the time of event emission
     * @return the value at the time of event emission
     */
    private final Double value;

    @Builder
    ValueMetric(org.cloudfoundry.dropsonde.events.ValueMetric dropsonde, String name, String unit, Double value) {
        Optional<org.cloudfoundry.dropsonde.events.ValueMetric> o = Optional.ofNullable(dropsonde);

        this.name = o.map(d -> d.name).orElse(name);
        this.unit = o.map(d -> d.unit).orElse(unit);
        this.value = o.map(d -> d.value).orElse(value);
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.name == null) {
            builder.message("name must be specified");
        }

        if (this.unit == null) {
            builder.message("unit must be specified");
        }

        if (this.value == null) {
            builder.message("value must be specified");
        }

        return builder.build();
    }

}

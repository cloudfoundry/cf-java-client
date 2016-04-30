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
 * Represents the increment of a counter. It contains only the change in the value; it is the responsibility of downstream consumers to maintain the value of the counter.
 */
@Data
public final class CounterEvent implements Event, Validatable {

    /**
     * The amount by which to increment the counter
     *
     * @param delta the amount by which to increment the counter
     * @return the amount by which to increment the counter
     */
    private final Long delta;

    /**
     * The name of the counter. Must be consistent for downstream consumers to associate events semantically.
     *
     * @param name the name of the counter
     * @return the name of the counter
     */
    private final String name;

    /**
     * The total value of the counter. This will be overridden by Metron, which internally tracks the total of each named Counter it receives.
     *
     * @param total the total value of the counter
     * @return the total value of the counter
     */
    private final Long total;

    @Builder
    CounterEvent(org.cloudfoundry.dropsonde.events.CounterEvent dropsonde, Long delta, String name, Long total) {
        Optional<org.cloudfoundry.dropsonde.events.CounterEvent> o = Optional.ofNullable(dropsonde);

        this.delta = o.map(d -> d.delta).orElse(delta);
        this.name = o.map(d -> d.name).orElse(name);
        this.total = o.map(d -> d.total).orElse(total);
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.delta == null) {
            builder.message("delta must be specified");
        }

        if (this.name == null) {
            builder.message("name must be specified");
        }

        return builder.build();
    }

}

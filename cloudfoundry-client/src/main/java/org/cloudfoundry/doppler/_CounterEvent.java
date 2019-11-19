/*
 * Copyright 2013-2019 the original author or authors.
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

import org.cloudfoundry.Nullable;
import org.cloudfoundry.loggregator.v2.LoggregatorEnvelope;
import org.immutables.value.Value;

import java.util.Objects;

/**
 * Represents the increment of a counter. It contains only the change in the value; it is the responsibility of downstream consumers to maintain the value of the counter.
 */
@Value.Immutable
abstract class _CounterEvent {

    public static CounterEvent from(org.cloudfoundry.dropsonde.events.CounterEvent dropsonde) {
        Objects.requireNonNull(dropsonde, "dropsonde");

        return CounterEvent.builder()
            .delta(dropsonde.delta)
            .name(dropsonde.name)
            .total(dropsonde.total)
            .build();
    }
    public static CounterEvent from(LoggregatorEnvelope.Counter counter) {
        Objects.requireNonNull(counter, "counter");

        return CounterEvent.builder()
            .delta(counter.getDelta())
            .name(counter.getName())
            .total(counter.getTotal())
            .build();
    }

    /**
     * The amount by which to increment the counter
     */
    abstract Long getDelta();

    /**
     * The name of the counter. Must be consistent for downstream consumers to associate events semantically.
     */
    abstract String getName();

    /**
     * The total value of the counter. This will be overridden by Metron, which internally tracks the total of each named Counter it receives.
     */
    @Nullable
    abstract Long getTotal();

}

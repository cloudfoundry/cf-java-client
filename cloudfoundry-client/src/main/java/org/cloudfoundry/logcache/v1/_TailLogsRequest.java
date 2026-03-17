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

package org.cloudfoundry.logcache.v1;

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.time.Duration;
import java.util.List;

/**
 * The request options for the Log Cache tail (streaming follow) operation.
 * This continuously polls the Log Cache /api/v1/read endpoint, emitting new envelopes
 * as they appear – equivalent to {@code cf tail --follow} or the Go {@code logcache.Walk()} API.
 */
@Value.Immutable
abstract class _TailLogsRequest {

    /**
     * The source id (application guid or service guid) to stream logs for.
     */
    abstract String getSourceId();

    /**
     * Optional start time (UNIX nanoseconds).  Defaults to "now – 5 seconds" when not set.
     */
    @Nullable
    abstract Long getStartTime();

    /**
     * Optional envelope type filter.
     */
    @Nullable
    abstract List<EnvelopeType> getEnvelopeTypes();

    /**
     * Optional regex name filter (requires Log Cache ≥ 2.1.0).
     */
    @Nullable
    abstract String getNameFilter();

    /**
     * How long to wait between successive polls when no new envelopes are available.
     * Defaults to 250 ms (matching the Go client's {@code AlwaysRetryBackoff}).
     */
    @Value.Default
    Duration getPollInterval() {
        return Duration.ofMillis(250);
    }
}


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

package org.cloudfoundry.client.v3.processes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * Process usage information
 */
@JsonDeserialize
@Value.Immutable
abstract class _ProcessUsage {

    /**
     * The CPU
     */
    @JsonProperty("cpu")
    @Nullable
    abstract Double getCpu();

    /**
     * The disk
     */
    @JsonProperty("disk")
    @Nullable
    abstract Long getDisk();

    /**
     * The memory
     */
    @JsonProperty("mem")
    @Nullable
    abstract Long getMemory();

    /**
     * The time
     */
    @JsonProperty("time")
    @Nullable
    abstract String getTime();

}

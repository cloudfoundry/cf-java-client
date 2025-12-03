/*
 * Copyright 2013-2025 the original author or authors.
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

package org.cloudfoundry.client.v3.spacequotas;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * Quotas that affect applications and application sub-resources
 */
@JsonDeserialize
@Value.Immutable
abstract class _Apps {

    /**
     * Maximum memory for a single process or task
     * @return the maximum memory for a single process or task
     */
    @JsonProperty("per_process_memory_in_mb")
    @Nullable
    abstract Integer getPerProcessMemoryInMb();

    /**
     * Total memory allowed for all the started processes and running tasks in a space
     * @return the total memory allowed for all the started processes and running tasks in a space
     */
    @JsonProperty("total_memory_in_mb")
    @Nullable
    abstract Integer getTotalMemoryInMb();

    /**
     * Total instances of all the started processes allowed in a space
     * @return the total instances of all the started processes allowed in a space
     */
    @JsonProperty("total_instances")
    @Nullable
    abstract Integer getTotalInstances();

    /**
     * Total log rate limit allowed for all the started processes and running tasks in a space
     */
    @JsonProperty("log_rate_limit_in_bytes_per_second")
    @Nullable
    abstract Integer getLogRateLimitInBytesPerSecond();

    /**
     * Maximum number of running tasks in a space
     * @return the maximum number of running tasks in a space
     */
    @JsonProperty("per_app_tasks")
    @Nullable
    abstract Integer getPerAppTasks();

}

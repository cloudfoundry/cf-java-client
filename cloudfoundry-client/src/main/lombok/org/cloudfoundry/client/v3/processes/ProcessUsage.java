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

package org.cloudfoundry.client.v3.processes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Process usage information
 */
@Data
public final class ProcessUsage {

    private final Double cpu;

    private final Long disk;

    private final Long memory;

    private final String time;

    @Builder
    ProcessUsage(@JsonProperty("cpu") Double cpu,
                 @JsonProperty("disk") Long disk,
                 @JsonProperty("mem") Long memory,
                 @JsonProperty("time") String time) {
        this.cpu = cpu;
        this.disk = disk;
        this.memory = memory;
        this.time = time;
    }

}

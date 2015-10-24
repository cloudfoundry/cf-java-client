/*
 * Copyright 2013-2015 the original author or authors.
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
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.v3.Link;

import java.util.Map;

/**
 * The response payload for the Scale Process operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ScaleProcessResponse extends Process {

    @Builder
    ScaleProcessResponse(@JsonProperty("created_at") String createdAt,
                         @JsonProperty("command") String command,
                         @JsonProperty("disk_in_mb") Integer diskInMb,
                         @JsonProperty("guid") String id,
                         @JsonProperty("_links") @Singular Map<String, Link> links,
                         @JsonProperty("instances") Integer instances,
                         @JsonProperty("memory_in_mb") Integer memoryInMb,
                         @JsonProperty("type") String type,
                         @JsonProperty("updated_at") String updatedAt) {
        super(createdAt, command, diskInMb, id, links, instances, memoryInMb, type, updatedAt);
    }

}

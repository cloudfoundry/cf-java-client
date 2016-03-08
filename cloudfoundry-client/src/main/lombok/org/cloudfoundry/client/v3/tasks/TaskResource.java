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

package org.cloudfoundry.client.v3.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.v3.Link;

import java.util.Map;

/**
 * The Resource response payload for the List Tasks operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class TaskResource extends Task {

    @Builder
    TaskResource(@JsonProperty("command") String command,
                 @JsonProperty("created_at") String createdAt,
                 @JsonProperty("environment_variables") @Singular Map<String, String> environmentVariables,
                 @JsonProperty("guid") String id,
                 @JsonProperty("links") @Singular Map<String, Link> links,
                 @JsonProperty("memory_in_mb") Integer memoryInMb,
                 @JsonProperty("name") String name,
                 @JsonProperty("result") @Singular Map<String, Object> results,
                 @JsonProperty("state") String state,
                 @JsonProperty("updated_at") String updatedAt) {

        super(command, createdAt, environmentVariables, id, links, memoryInMb, name, results, state, updatedAt);
    }

}

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

package org.cloudfoundry.client.v3.applications;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Singular;
import org.cloudfoundry.client.v3.Link;

import java.util.Map;

@Data
public abstract class Application {

    private final String buildpack;

    private final String createdAt;

    private final String desiredState;

    private final Map<String, String> environmentVariables;

    private final String id;

    private final Map<String, Link> links;

    private final String name;

    private final Integer totalDesiredInstances;

    private final String updatedAt;

    protected Application(@JsonProperty("buildpack") String buildpack,
                          @JsonProperty("created_at") String createdAt,
                          @JsonProperty("desired_state") String desiredState,
                          @JsonProperty("environment_variables") @Singular Map<String, String> environmentVariables,
                          @JsonProperty("guid") String id,
                          @JsonProperty("_links") @Singular Map<String, Link> links,
                          @JsonProperty("name") String name,
                          @JsonProperty("total_desired_instances") Integer totalDesiredInstances,
                          @JsonProperty("updated_at") String updatedAt) {
        this.buildpack = buildpack;
        this.createdAt = createdAt;
        this.desiredState = desiredState;
        this.environmentVariables = environmentVariables;
        this.id = id;
        this.links = links;
        this.name = name;
        this.totalDesiredInstances = totalDesiredInstances;
        this.updatedAt = updatedAt;
    }

}

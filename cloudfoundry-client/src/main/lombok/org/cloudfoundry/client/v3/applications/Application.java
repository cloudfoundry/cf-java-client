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
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.Link;

import java.util.Map;

@Data
public abstract class Application {

    /**
     * When the application was created
     *
     * @param createdAt when the application was created
     * @return when the application was created
     */
    private final String createdAt;

    /**
     * The desired state
     *
     * @param desiredState the desired state
     * @return the desired state
     */
    private final String desiredState;

    /**
     * The environment variables
     *
     * @param environmentVariables the environment variables
     * @return the environment variables
     */
    private final Map<String, String> environmentVariables;

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    private final String id;

    /**
     * The lifecycle
     *
     * @param lifecycle the lifecycle
     * @return the lifecycle
     */
    private final Lifecycle lifecycle;

    /**
     * The links
     *
     * @param links the links
     * @return the links
     */
    private final Map<String, Link> links;

    /**
     * The name
     *
     * @param name the name
     * @return the name
     */
    private final String name;

    /**
     * The total desired instances
     *
     * @param totalDesiredInstances the total desired instances
     * @return the total desired instances
     */
    private final Integer totalDesiredInstances;

    /**
     * When the application was updated
     *
     * @param updatedAt when the application was updated
     * @return when the application was updated
     */
    private final String updatedAt;

    protected Application(@JsonProperty("created_at") String createdAt,
                          @JsonProperty("desired_state") String desiredState,
                          @JsonProperty("environment_variables") @Singular Map<String, String> environmentVariables,
                          @JsonProperty("guid") String id,
                          @JsonProperty("lifecycle") Lifecycle lifecycle,
                          @JsonProperty("links") @Singular Map<String, Link> links,
                          @JsonProperty("name") String name,
                          @JsonProperty("total_desired_instances") Integer totalDesiredInstances,
                          @JsonProperty("updated_at") String updatedAt) {
        this.createdAt = createdAt;
        this.desiredState = desiredState;
        this.environmentVariables = environmentVariables;
        this.id = id;
        this.links = links;
        this.lifecycle = lifecycle;
        this.name = name;
        this.totalDesiredInstances = totalDesiredInstances;
        this.updatedAt = updatedAt;
    }

}

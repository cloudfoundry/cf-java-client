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

package org.cloudfoundry.client.v2.serviceinstances;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * The last operation payload for the List Service Instances operation
 */
@Data
public final class LastOperation {

    /**
     * When the entity was created
     *
     * @param createdAt when the entity was created
     * @return when the entity was created
     */
    private final String createdAt;

    /**
     * The description
     *
     * @param description the description
     * @return the description
     */
    private final String description;

    /**
     * The state
     *
     * @param state the state
     * @return the state
     */
    private final String state;

    /**
     * The type
     *
     * @param type the type
     * @return the type
     */
    private final String type;

    /**
     * When the entity was last updated
     *
     * @param updatedAt when the entity was last updated
     * @return when the entity was last updated
     */
    private final String updatedAt;

    @Builder
    LastOperation(@JsonProperty("created_at") String createdAt,
                  @JsonProperty("description") String description,
                  @JsonProperty("state") String state,
                  @JsonProperty("type") String type,
                  @JsonProperty("updated_at") String updatedAt) {
        this.createdAt = createdAt;
        this.description = description;
        this.state = state;
        this.type = type;
        this.updatedAt = updatedAt;
    }

}
